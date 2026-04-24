package ballistix.common.entity;

import ballistix.api.entity.IDefusable;
import ballistix.api.explosive.BallistixExplosives;
import ballistix.api.explosive.BallistixExplosives.ExplosiveData;
import ballistix.common.blast.Blast;
import ballistix.common.blast.BlastDarkmatter;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.NetworkHooks;
import net.minecraft.core.registries.BuiltInRegistries;

public class EntityExplosive extends Entity implements IDefusable {

	private static final EntityDataAccessor<Integer> FUSE = SynchedEntityData.defineId(EntityExplosive.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityExplosive.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<String> CUSTOM_BLOCK = SynchedEntityData.defineId(EntityExplosive.class, EntityDataSerializers.STRING);

	public int blastOrdinal = -1;
	public int fuse = 80;
	public Block customBlock = null;

	public EntityExplosive(EntityType<? extends EntityExplosive> type, Level worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityExplosive(Level worldIn, double x, double y, double z) {
		this(BallistixEntities.ENTITY_EXPLOSIVE.get(), worldIn);
		setPos(x, y, z);
		double d0 = worldIn.random.nextDouble() * ((float) Math.PI * 2F);
		this.setDeltaMovement(-Math.sin(d0) * 0.02D, 0.2F, -Math.cos(d0) * 0.02D);
		xo = x;
		yo = y;
		zo = z;
	}

	@Override
	public boolean isPickable() {
		return !isRemoved();
	}

	public void setBlastType(SubtypeBlast explosive) {
		blastOrdinal = explosive.ordinal();
		fuse = explosive.fuse;
	}

	public void setCustomBlock(Block block, int fuse) {
		this.customBlock = block;
		this.fuse = fuse;
	}

	public SubtypeBlast getBlastType() {
		return blastOrdinal == -1 ? null : SubtypeBlast.values()[blastOrdinal];
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(FUSE, 80);
		entityData.define(TYPE, -1);
		entityData.define(CUSTOM_BLOCK, "");
	}

	@Override
	public void defuse() {
		remove(RemovalReason.DISCARDED);
		if (blastOrdinal != -1) {
			SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
			ItemEntity item = new ItemEntity(level(), getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5, new ItemStack(BallistixBlocks.SUBTYPEBLOCKREGISTER_MAPPINGS.get(explosive).get()));
			level().addFreshEntity(item);
		} else if (customBlock != null) {
			ItemEntity item = new ItemEntity(level(), getBlockX() + 0.5, getBlockY() + 0.5, getBlockZ() + 0.5, new ItemStack(customBlock));
			level().addFreshEntity(item);
		}
	}

	@Override
	public void tick() {
		if (!level().isClientSide) {
			entityData.set(TYPE, blastOrdinal);
			entityData.set(FUSE, fuse);
			entityData.set(CUSTOM_BLOCK, customBlock == null ? "" : BuiltInRegistries.BLOCKS.getKey(customBlock).toString());
		} else {
			blastOrdinal = entityData.get(TYPE);
			fuse = entityData.get(FUSE);
			String key = entityData.get(CUSTOM_BLOCK);
			customBlock = key.isEmpty() ? null : BuiltInRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation(key));
		}
		if (!isNoGravity()) {
			this.setDeltaMovement(getDeltaMovement().add(0.0D, -0.04D, 0.0D));
		}

		move(MoverType.SELF, getDeltaMovement());
		this.setDeltaMovement(getDeltaMovement().scale(0.98D));
		if (onGround()) {
			this.setDeltaMovement(getDeltaMovement().multiply(0.7D, -0.5D, 0.7D));
		}

		if (!level().isClientSide && blastOrdinal > -1 && SubtypeBlast.values()[blastOrdinal] == SubtypeBlast.largeantimatter) {

			for (EntityBlast entity : level().getEntitiesOfClass(EntityBlast.class, getBoundingBox().inflate(getDeltaMovement().length()))) {
				if (entity.blastOrdinal == SubtypeBlast.darkmatter.ordinal() && entity.getBlast() != null) {
					BlastDarkmatter blast = (BlastDarkmatter) entity.getBlast();
					blast.canceled = true;
					entity.remove(RemovalReason.DISCARDED);
					SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
					Blast b = explosive.createBlast(level(), blockPosition());
					if (b != null) {
						b.performExplosion();
					}
					removeAfterChangingDimensions();
					return;
				}
			}

		}

		--fuse;
		if (fuse <= 0) {
			if (!level().isClientSide()) {
				remove(RemovalReason.DISCARDED);
				if (blastOrdinal != -1) {
					SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];
					Blast b = explosive.createBlast(level(), blockPosition());
					if (b != null) {
						b.performExplosion();
					}
				} else if (customBlock != null) {
					ExplosiveData data = BallistixExplosives.EXPLOSIVE_MAPPING.get(customBlock);
					if (data != null) {
						data.onTriggered.accept(level(), blockPosition());
					}
				}
			}
		} else {
			updateInWaterStateAndDoFluidPushing();
			if (level().isClientSide) {
				level().addParticle(ParticleTypes.LAVA, getX(), getY() + 0.5D, getZ(), 0.0D, 0.0D, 0.0D);
			}
		}

	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Fuse", fuse);
		compound.putInt("type", blastOrdinal);
		if (customBlock != null) {
			compound.putString("customBlock", BuiltInRegistries.BLOCKS.getKey(customBlock).toString());
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		fuse = compound.getInt("Fuse");
		blastOrdinal = compound.getInt("type");
		if (compound.contains("customBlock")) {
			customBlock = BuiltInRegistries.BLOCKS.getValue(new net.minecraft.resources.ResourceLocation(compound.getString("customBlock")));
		}
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
