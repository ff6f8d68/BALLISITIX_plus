package ballistix.bplus.tile;

import ballistix.api.explosive.BallistixExplosives;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.block.BlockExplosive;
import ballistix.bplus.inventory.container.ContainerBigDartPod;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.network.SiloRegistry;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileMissileSilo;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
import ballistix.bplus.block.BlockBigDartPod;
import voltaic.api.multiblock.Subnode;
import voltaic.api.multiblock.parent.IMultiblockParentTile;
import voltaic.common.blockitem.types.BlockItemDescriptable;
import voltaic.common.tile.TileMultiSubnode;
import voltaic.prefab.properties.Property;
import voltaic.prefab.properties.PropertyType;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.tile.components.type.ComponentInventory;
import voltaic.prefab.tile.components.type.ComponentInventory.InventoryBuilder;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.NBTUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class TileBigDartPod extends GenericTile implements IMultiblockParentTile {

    public static final int[] MISSILE_SLOTS = {0, 1, 2, 3};
    public static final int[] EXPLOSIVE_SLOTS = {4, 5, 6, 7};
    public static final int SYNC_SLOT = 8;
    public static final int COOLDOWN = 100;

    public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0).onChange((prop, prevFreq) -> {
        if (level.isClientSide) return;
        SiloRegistry.unregisterSilo(prevFreq, this);
        SiloRegistry.registerSilo(prop.get(), this);
    }));
    public Property<BlockPos> target = property(new Property<>(PropertyType.BlockPos, "target", BlockPos.ZERO));
    public Property<Integer> range = property(new Property<>(PropertyType.Integer, "range", 0));

    private int cooldown = 100;
    public boolean shouldLaunch = false;

    public TileBigDartPod(BlockPos pos, BlockState state) {
        super(BallistixBlockTypes.TILE_BIGDARTPOD.get(), pos, state);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentPacketHandler(this));
        
        addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(9))
            .valid(this::isItemValidForSlot)
            .setDirectionsBySlot(MISSILE_SLOTS[0], Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            .setDirectionsBySlot(MISSILE_SLOTS[1], Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            .setDirectionsBySlot(MISSILE_SLOTS[2], Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            .setDirectionsBySlot(MISSILE_SLOTS[3], Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST)
            .setDirectionsBySlot(EXPLOSIVE_SLOTS[0], Direction.UP)
            .setDirectionsBySlot(EXPLOSIVE_SLOTS[1], Direction.UP)
            .setDirectionsBySlot(EXPLOSIVE_SLOTS[2], Direction.UP)
            .setDirectionsBySlot(EXPLOSIVE_SLOTS[3], Direction.UP));
        
        addComponent(new ComponentElectrodynamic(this, false, true).voltage(120).maxJoules(1000000).setInputDirections(Direction.values()));
        addComponent(new ComponentContainerProvider("container.bigdartpod", this).createMenu((id, player) -> new ContainerBigDartPod(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));
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

        if (!hasRedstone && !shouldLaunch) return;

        shouldLaunch = false;
        launchAllMissiles();
        cooldown = COOLDOWN;
    }

    private void launchAllMissiles() {
        ComponentInventory inv = getComponent(IComponentType.Inventory);
        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);
        Direction facing = getFacing();
        Direction launchDirEnum = facing.getOpposite();
        
        for (int i = 0; i < 4; i++) {
            ItemStack mis = inv.getItem(MISSILE_SLOTS[i]);
            ItemStack explosive = inv.getItem(EXPLOSIVE_SLOTS[i]);
            
            if (!mis.isEmpty() && !explosive.isEmpty() && mis.getItem() instanceof ItemMissile itemMissile) {
                
                // Check if we have enough power
                if (electro.getJoulesStored() < Constants.MISSILESILO_USAGE) {
                    return;
                }
                
                // Offset launch positions for each of the 4 missiles in the 2x2x2
                double offX = (i % 2) * 0.8;
                double offY = (i / 2) * 0.8;
                Vec3 launchPos = new Vec3(getBlockPos().getX() + 0.5 + launchDirEnum.getStepX() * 0.5 + offX, getBlockPos().getY() + 0.5 + offY, getBlockPos().getZ() + 0.5 + launchDirEnum.getStepZ() * 0.5);
                Vec3 launchDir = new Vec3(launchDirEnum.getStepX() * 2.0, 0.4, launchDirEnum.getStepZ() * 2.0).normalize();

                int blastOrdinal = -1;
                Block customBlock = null;
                if (explosive.getItem() instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive be) {
                    blastOrdinal = be.explosive.ordinal();
                } else if (explosive.getItem() instanceof BlockItem bi) {
                    customBlock = bi.getBlock();
                }

                VirtualMissile missile = new VirtualMissile(launchPos, launchDir, 1.8F, false, (float)launchPos.x, (float)launchPos.z, target.get(), itemMissile.id, blastOrdinal, false, frequency.get(), itemMissile.speedModifier, itemMissile.radarVisible, 1);
                if (customBlock != null) missile.customExplosiveBlock = customBlock;

                MissileManager.addMissile(level.dimension(), missile);
                
                // Consume power and items
                electro.joules(electro.getJoulesStored() - Constants.MISSILESILO_USAGE);
                inv.removeItem(MISSILE_SLOTS[i], 1);
                inv.removeItem(EXPLOSIVE_SLOTS[i], 1);
                level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundSource.BLOCKS, 1.0F, 0.5F + 0.5F * itemMissile.speedModifier);
            }
        }
    }

    protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
        Item item = stack.getItem();
        if (index >= 0 && index <= 3) return item instanceof ItemMissile;
        if (index >= 4 && index <= 7) return (item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive) || (item instanceof BlockItem bi && BallistixExplosives.EXPLOSIVE_MAPPING.containsKey(bi.getBlock()));
        if (index == SYNC_SLOT) return stack.is(BallistixItems.ITEM_RADARGUN.get()) || stack.is(BallistixItems.ITEM_LASERDESIGNATOR.get());
        return false;
    }

    @Override
    public Subnode[] getSubNodes() {
        return ((BlockBigDartPod) getBlockState().getBlock()).getSubNodes(getBlockState());
    }

    @Override
    public void onSubnodeDestroyed(TileMultiSubnode node) {
        level.destroyBlock(worldPosition, true);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getSubnodeCapability(@NotNull Capability<T> cap, Direction side) {
        return getCapability(cap, side);
    }

    @Override
    public InteractionResult onSubnodeUse(Player player, InteractionHand hand, BlockHitResult hit, TileMultiSubnode subnode) {
        return use(player, hand, hit);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(2);
    }

    @Override
    public Component getName() {
        return null;
    }

    @Override public void onInventoryChange(ComponentInventory inv, int index) {
        if (index == SYNC_SLOT || index == -1) {
            ItemStack sync = inv.getItem(SYNC_SLOT);
            if (!sync.isEmpty()) {
                if (sync.is(BallistixItems.ITEM_LASERDESIGNATOR.get())) sync.getOrCreateTag().putInt(ItemLaserDesignator.FREQUENCY_KEY, frequency.get());
                else if (sync.is(BallistixItems.ITEM_RADARGUN.get()) && sync.getOrCreateTag().contains(NBTUtils.LOCATION)) target.set(ItemRadarGun.getCoordiantes(sync));
            }
        }
    }

    @Override public void onLoad() { super.onLoad(); if (!level.isClientSide) SiloRegistry.registerSilo(frequency.get(), this); }
}
