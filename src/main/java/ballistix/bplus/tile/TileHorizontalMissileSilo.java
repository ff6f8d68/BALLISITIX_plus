package ballistix.bplus.tile;

import ballistix.api.explosive.BallistixExplosives;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.block.BlockExplosive;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.network.SiloRegistry;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.tile.TileMultiSubnode;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import electrodynamics.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import electrodynamics.prefab.utilities.NBTUtils;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import ballistix.bplus.block.BlockHorizontalMissileSilo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

public class TileHorizontalMissileSilo extends GenericTile implements IMultiblockParentTile {

    public static final int MISSILE_SLOT = 0;
    public static final int EXPLOSIVE_SLOT = 1;
    public static final int COOLDOWN = 100;

    public Property<Integer> range = property(new Property<>(PropertyType.Integer, "range", 0));
    public Property<Boolean> hasExplosive = property(new Property<>(PropertyType.Boolean, "hasexplosive", false));
    public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0).onChange((prop, prevFreq) -> {
        if (level.isClientSide) return;
        SiloRegistry.unregisterSilo(prevFreq, this);
        SiloRegistry.registerSilo(prop.get(), this);
    }));
    public Property<BlockPos> target = property(new Property<>(PropertyType.BlockPos, "target", BlockPos.ZERO));

    private int cooldown = 100;
    public boolean shouldLaunch = false;

    public TileHorizontalMissileSilo(BlockPos pos, BlockState state) {
        super(BallistixBlockTypes.TILE_HORIZONTALMISSILESILO.get(), pos, state);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        
        // Auto-filling: Missile from BACK (relative to facing), Explosive from TOP
        // Note: Broadened input directions for missiles to simplify hopper placement.
        addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(3))
            .valid(this::isItemValidForSlot)
            .setDirectionsBySlot(MISSILE_SLOT, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN)
            .setDirectionsBySlot(EXPLOSIVE_SLOT, Direction.UP));

        addComponent(new ComponentElectrodynamic(this, false, true).voltage(120).maxJoules(Constants.MISSILESILO_USAGE * 20).setInputDirections(Direction.values()));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentContainerProvider("container.missilesilo", this).createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
    }

    protected void tickServer(ComponentTickable tickable) {
        if (target.get() == null) target.set(getBlockPos());
        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
        if (cooldown > 0 || electro.getJoulesStored() < Constants.MISSILESILO_USAGE) {
            cooldown--;
            return;
        }

        boolean hasRedstone = level.hasNeighborSignal(getBlockPos());
        for (Subnode subnode : getSubNodes()) {
            hasRedstone |= level.hasNeighborSignal(getBlockPos().offset(subnode.pos()));
            if (hasRedstone) break;
        }

        if (range.get() == 0 || !hasExplosive.get() || (!hasRedstone && !shouldLaunch)) return;

        shouldLaunch = false;
        double dist = calculateDistance(worldPosition, target.get());
        if (range.get() > 0 && range.get() < dist) return;

        ComponentInventory inv = getComponent(IComponentType.Inventory);
        ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
        ItemStack mis = inv.getItem(MISSILE_SLOT);
        ItemMissile itemMissile = (ItemMissile) mis.getItem();
        
        Direction facing = getFacing();
        Direction launchDirEnum = facing.getOpposite();
        // Updated Y to 2.5 to be at the top level of the silo
        Vec3 launchPos = new Vec3(getBlockPos().getX() + 0.5 + launchDirEnum.getStepX() * 2.5, getBlockPos().getY() + 2.5, getBlockPos().getZ() + 0.5 + launchDirEnum.getStepZ() * 2.5);
        Vec3 launchDir = new Vec3(launchDirEnum.getStepX(), 0.05, launchDirEnum.getStepZ()).normalize();

        int blastOrdinal = -1;
        Block customBlock = null;
        if (explosive.getItem() instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive be) {
            blastOrdinal = be.explosive.ordinal();
        } else if (explosive.getItem() instanceof BlockItem bi) {
            customBlock = bi.getBlock();
        }

        VirtualMissile missile = new VirtualMissile(launchPos, launchDir, 1.2F, false, (float)launchPos.x, (float)launchPos.z, target.get(), itemMissile.id, blastOrdinal, false, frequency.get(), itemMissile.speedModifier, itemMissile.radarVisible, 1);
        if (customBlock != null) missile.customExplosiveBlock = customBlock;

        MissileManager.addMissile(level.dimension(), missile);
        electro.joules(electro.getJoulesStored() - Constants.MISSILESILO_USAGE);
        inv.removeItem(MISSILE_SLOT, 1);
        inv.removeItem(EXPLOSIVE_SLOT, 1);
        level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundSource.BLOCKS, 1.0F, 0.5F + 0.5F * itemMissile.speedModifier);
        cooldown = COOLDOWN;
    }

    protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
        Item item = stack.getItem();
        if (index == 0) return item instanceof ItemMissile;
        if (index == 1) return (item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive) || (item instanceof BlockItem bi && BallistixExplosives.EXPLOSIVE_MAPPING.containsKey(bi.getBlock()));
        if (index == 2) return stack.is(BallistixItems.ITEM_RADARGUN.get()) || stack.is(BallistixItems.ITEM_LASERDESIGNATOR.get());
        return false;
    }

    @Override public void onBlockDestroyed() { if (!level.isClientSide) SiloRegistry.unregisterSilo(frequency.get(), this); }
    @Override public AABB getRenderBoundingBox() { return INFINITE_EXTENT_AABB; }
    @Override public Subnode[] getSubNodes() {
        return switch (getFacing()) {
            case EAST -> BlockHorizontalMissileSilo.SUBNODES_EAST;
            case WEST -> BlockHorizontalMissileSilo.SUBNODES_WEST;
            case NORTH -> BlockHorizontalMissileSilo.SUBNODES_NORTH;
            case SOUTH -> BlockHorizontalMissileSilo.SUBNODES_SOUTH;
            default -> BlockHorizontalMissileSilo.SUBNODES_SOUTH;
        };
    }

    @Override public void onInventoryChange(ComponentInventory inv, int index) {
        if (index == 0 || index == -1) {
            ItemStack missile = inv.getItem(0);
            range.set(!missile.isEmpty() && missile.getItem() instanceof ItemMissile item ? item.range : 0);
        }
        if (index == 1 || index == -1) {
            ItemStack explosive = inv.getItem(1);
            hasExplosive.set(!explosive.isEmpty() && (explosive.getItem() instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive || explosive.getItem() instanceof BlockItem bi && BallistixExplosives.EXPLOSIVE_MAPPING.containsKey(bi.getBlock())));
        }
        if (index == 2 || index == -1) {
            ItemStack sync = inv.getItem(2);
            if (!sync.isEmpty()) {
                if (sync.is(BallistixItems.ITEM_LASERDESIGNATOR.get())) sync.getOrCreateTag().putInt(ItemLaserDesignator.FREQUENCY_KEY, frequency.get());
                else if (sync.is(BallistixItems.ITEM_RADARGUN.get()) && sync.getOrCreateTag().contains(NBTUtils.LOCATION)) target.set(ItemRadarGun.getCoordiantes(sync));
            }
        }
    }

    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide) SiloRegistry.registerSilo(frequency.get(), this); }
    @Override public void saveAdditional(@NotNull CompoundTag nbt) { super.saveAdditional(nbt); nbt.putInt("silocooldown", cooldown); nbt.putBoolean("shouldlaunch", shouldLaunch); }
    @Override public void load(@NotNull CompoundTag nbt) { super.load(nbt); cooldown = nbt.getInt("silocooldown"); shouldLaunch = nbt.getBoolean("shouldlaunch"); }

    @Override public InteractionResult use(Player player, InteractionHand hand, BlockHitResult result) {
        return super.use(player, hand, result);
    }

    @Override public void onSubnodeDestroyed(TileMultiSubnode node) { level.destroyBlock(worldPosition, true); }
    @Override public <T> @NotNull LazyOptional<T> getSubnodeCapability(@NotNull Capability<T> cap, Direction side) { return getCapability(cap, side); }
    @Override public InteractionResult onSubnodeUse(Player player, InteractionHand hand, BlockHitResult hit, TileMultiSubnode subnode) { return use(player, hand, hit); }

    public static double calculateDistance(BlockPos fromPos, BlockPos toPos) {
        double deltaX = fromPos.getX() - toPos.getX();
        double deltaY = fromPos.getY() - toPos.getY();
        double deltaZ = fromPos.getZ() - toPos.getZ();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    @Override
    public Component getName() {
        return null;
    }
}
