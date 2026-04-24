package ballistix.common.tile;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import ballistix.References;
import ballistix.api.explosive.BallistixExplosives;
import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMissile;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.network.SiloRegistry;
import ballistix.common.settings.Constants;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixItems;
import ballistix.registers.BallistixSounds;
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
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.common.world.ForgeChunkManager;

public class TileMissileSilo extends GenericTile implements IMultiblockParentTile {

	public static final int MISSILE_SLOT = 0;
	public static final int EXPLOSIVE_SLOT = 1;
	
	public static final int COOLDOWN = 100;

	public Property<Integer> range = property(new Property<>(PropertyType.Integer, "range", 0));
	public Property<Boolean> hasExplosive = property(new Property<>(PropertyType.Boolean, "hasexplosive", false));
	public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0).onChange((prop, prevFreq) -> {

		if (level.isClientSide) {
			return;
		}

		int newFreq = prop.get();

		SiloRegistry.unregisterSilo(prevFreq, this);
		SiloRegistry.registerSilo(newFreq, this);

	}));
	public Property<BlockPos> target = property(new Property<>(PropertyType.BlockPos, "target", BlockPos.ZERO));

	private int cooldown = 100;
	public boolean shouldLaunch = false;

	public TileMissileSilo(BlockPos pos, BlockState state) {
		super(BallistixBlockTypes.TILE_MISSILESILO.get(), pos, state);

		addComponent(new ComponentTickable(this).tickServer(this::tickServer));
		
		// Auto-filling: Missile from bottom, Explosive from sides (for vertical silo)
		addComponent(new ComponentInventory(this, InventoryBuilder.newInv().inputs(3))
		    .valid(this::isItemValidForSlot)
		    .setDirectionsBySlot(MISSILE_SLOT, Direction.DOWN)
		    .setDirectionsBySlot(EXPLOSIVE_SLOT, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
		    
		addComponent(new ComponentElectrodynamic(this, false, true).voltage(120).maxJoules(Constants.MISSILESILO_USAGE * 20).setInputDirections(Direction.values()));
		addComponent(new ComponentPacketHandler(this));
		addComponent(new ComponentContainerProvider("container.missilesilo", this).createMenu((id, player) -> new ContainerMissileSilo(id, player, getComponent(IComponentType.Inventory), getCoordsArray())));

	}

	protected void tickServer(ComponentTickable tickable) {

		if (target.get() == null) {
            target.set(getBlockPos());
        }

        ComponentElectrodynamic electro = getComponent(IComponentType.Electrodynamic);

        if (cooldown > 0 || electro.getJoulesStored() < Constants.MISSILESILO_USAGE) {
            cooldown--;
            return;
        }

        boolean hasRedstone = level.hasNeighborSignal(getBlockPos());

        for(Subnode subnode : getSubNodes()) {
            hasRedstone |= level.hasNeighborSignal(getBlockPos().offset(subnode.pos()));
            if(hasRedstone) {
                break;
            }
        }

        if (range.get() == 0 || !hasExplosive.get() || (!hasRedstone && !shouldLaunch)) {
            return;
        }

        shouldLaunch = false;

        double dist = calculateDistance(worldPosition, target.get());

        if (range.get() == 0 || (range.get() > 0 && range.get() < dist)) {
            return;
        }

        ComponentInventory inv = getComponent(IComponentType.Inventory);
        ItemStack explosive = inv.getItem(EXPLOSIVE_SLOT);
        ItemStack mis = inv.getItem(MISSILE_SLOT);

        ItemMissile itemMissile = (ItemMissile) mis.getItem();
        float speedModifier = itemMissile.speedModifier;
        int missileId = itemMissile.id;
        boolean radarVisible = itemMissile.radarVisible;

        int blastOrdinal = -1;
        Block customBlock = null;
        
        if (explosive.getItem() instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive be) {
        	blastOrdinal = be.explosive.ordinal();
        } else if (explosive.getItem() instanceof BlockItem bi) {
        	customBlock = bi.getBlock();
        }

        VirtualMissile missile = new VirtualMissile(
                //
                new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5),
                //
                new Vec3(0, 1, 0),
                //
                0.0F,
                //
                false,
                //
                getBlockPos().getX() + 0.5F,
                //
                getBlockPos().getZ() + 0.5F,
                //
                target.get(),
                //
                missileId,
                //
                blastOrdinal,
                //
                false,
                //
                frequency.get(),
                //
                speedModifier,
                //
                radarVisible
                //
        );

        if (customBlock != null) {
            missile.customExplosiveBlock = customBlock;
        }

        MissileManager.addMissile(level.dimension(), missile);

        electro.joules(electro.getJoulesStored() - Constants.MISSILESILO_USAGE);

        inv.removeItem(MISSILE_SLOT, 1);
        inv.removeItem(EXPLOSIVE_SLOT, 1);

        level.playSound(null, getBlockPos(), BallistixSounds.SOUND_MISSILE_SILO.get(), SoundSource.BLOCKS, 1.0F, 0.5F + 0.5F * speedModifier);

        cooldown = COOLDOWN;

	}

	protected boolean isItemValidForSlot(int index, ItemStack stack, ComponentInventory inv) {
		Item item = stack.getItem();

        if (index == 0) {
            return item instanceof ItemMissile;
        } else if (index == 1) {
            return (item instanceof BlockItemDescriptable des && des.getBlock() instanceof BlockExplosive) || (item instanceof BlockItem bi && BallistixExplosives.EXPLOSIVE_MAPPING.containsKey(bi.getBlock()));
        } else if (index == 2) {
            return stack.is(BallistixItems.ITEM_RADARGUN.get()) || stack.is(BallistixItems.ITEM_LASERDESIGNATOR.get());
        }
        return false;
	}

	@Override
	public void onBlockDestroyed() {
		if (level.isClientSide) {
			return;
		}
		SiloRegistry.unregisterSilo(frequency.get(), this);

		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, worldPosition, chunkPos.x, chunkPos.z, false, true);

	}

	@Override
	public void onPlace(BlockState oldState, boolean isMoving) {
		super.onPlace(oldState, isMoving);
		if (level.isClientSide) {
			return;
		}
		ChunkPos chunkPos = level.getChunk(worldPosition).getPos();

		ForgeChunkManager.forceChunk((ServerLevel) level, References.ID, worldPosition, chunkPos.x, chunkPos.z, true, true);
	}

	@Override
	public AABB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public Subnode[] getSubNodes() {

		return switch (getFacing()) {
		case EAST -> BlockMissileSilo.SUBNODES_EAST;
		case WEST -> BlockMissileSilo.SUBNODES_WEST;
		case NORTH -> BlockMissileSilo.SUBNODES_NORTH;
		case SOUTH -> BlockMissileSilo.SUBNODES_SOUTH;
		default -> BlockMissileSilo.SUBNODES_SOUTH;
		};

	}

	@Override
	public void onInventoryChange(ComponentInventory inv, int index) {

		handleMissile(inv, index);

		handleExplosive(inv, index);
		
		handleSync(inv, index);

	}

	private void handleMissile(ComponentInventory inv, int index) {
		if (index == 0 || index == -1) {

			ItemStack missile = inv.getItem(0);

			if (missile.isEmpty()) {
				range.set(0);
				return;
			}

			if (missile.getItem() instanceof ItemMissile item) {
				range.set(item.range);
			} else {
				range.set(0);
			}

		}
	}

	private void handleExplosive(ComponentInventory inv, int index) {
		if (index == 1 || index == -1) {

			ItemStack explosive = inv.getItem(1);

			if (!explosive.isEmpty()) {
				Item item = explosive.getItem();
				if (item instanceof BlockItemDescriptable blockItem && blockItem.getBlock() instanceof BlockExplosive) {
					hasExplosive.set(true);
					return;
				} else if (item instanceof BlockItem bi && BallistixExplosives.EXPLOSIVE_MAPPING.containsKey(bi.getBlock())) {
					hasExplosive.set(true);
					return;
				}
			}
			hasExplosive.set(false);

		}
	}
	
	private void handleSync(ComponentInventory inv, int index) {
        if (index == 2 || index == -1) {

            ItemStack sync = inv.getItem(2);

            if (sync.isEmpty()) {
                return;
            }

            if (sync.is(BallistixItems.ITEM_LASERDESIGNATOR.get())) {

                sync.getOrCreateTag().putInt(ItemLaserDesignator.FREQUENCY_KEY, frequency.get());

            } else if (sync.is(BallistixItems.ITEM_RADARGUN.get())) {

                if (sync.getOrCreateTag().contains(NBTUtils.LOCATION)) {
                    target.set(ItemRadarGun.getCoordiantes(sync));
                }

            }

        }
    }

	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide) {
			SiloRegistry.registerSilo(frequency.get(), this);
		}
	}

	@Override
	public void saveAdditional(@NotNull CompoundTag nbt) {
		super.saveAdditional(nbt);
		nbt.putInt("silocooldown", cooldown);
		nbt.putBoolean("shouldlaunch", shouldLaunch);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		cooldown = nbt.getInt("silocooldown");
		shouldLaunch = nbt.getBoolean("shouldlaunch");
	}

	@Override
	public InteractionResult use(Player player, InteractionHand hand, BlockHitResult result) {
		return super.use(player, hand, result);
	}

	@Override
	public void onSubnodeDestroyed(TileMultiSubnode arg0) {
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
