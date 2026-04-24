package ballistix.common.entity;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import ballistix.api.missile.MissileManager;
import ballistix.api.missile.virtual.VirtualMissile;
import ballistix.registers.BallistixEntities;
import electrodynamics.common.tile.machines.quarry.TileQuarry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.NetworkHooks;

public class EntityMissile extends Entity {

	private static final EntityDataAccessor<Integer> MISSILE_ID = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<BlockPos> TARGET = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<Float> SPEED = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> START_X = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> START_Z = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> IS_ITEM = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> CURRENTLYEXPLODING = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> SPEED_MODIFIER = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Boolean> RADAR_VISIBLE = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Float> Y_ROT = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> X_ROT = SynchedEntityData.defineId(EntityMissile.class, EntityDataSerializers.FLOAT);

	public int missileId = -1;
	public float speed = 0.0F;
	public float speedModifier = 1.0F;
	public boolean radarVisible = true;
	@Nullable
	public UUID id;
	public boolean isItem = false;
	public boolean isExploding = false;
	public BlockPos target = TileQuarry.OUT_OF_REACH;
	public float startX;
	public float startZ;

	public EntityMissile(EntityType<? extends EntityMissile> type, Level worldIn) {
		super(type, worldIn);
		blocksBuilding = true;
	}

	public EntityMissile(Level worldIn) {
		this(BallistixEntities.ENTITY_MISSILE.get(), worldIn);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(TARGET, TileQuarry.OUT_OF_REACH);
		entityData.define(MISSILE_ID, -1);
		entityData.define(START_X, 0.0F);
		entityData.define(START_Z, 0.0F);
		entityData.define(SPEED, 0.0F);
		entityData.define(IS_ITEM, true);
		entityData.define(CURRENTLYEXPLODING, false);
		entityData.define(SPEED_MODIFIER, 1.0F);
		entityData.define(RADAR_VISIBLE, true);
		entityData.define(Y_ROT, 0.0F);
		entityData.define(X_ROT, 0.0F);
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return super.getBoundingBoxForCulling().expandTowards(20, 20, 20);
	}

	@Override
	public void tick() {
		this.xRotO = this.getXRot();
		this.yRotO = this.getYRot();
		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();

		super.tick();

		Level level = level();
		boolean isClientSide = level.isClientSide;
		boolean isServerSide = !isClientSide;

		if (isServerSide) {
			if (id == null) {
				remove(RemovalReason.DISCARDED);
				return;
			}

			VirtualMissile missile = MissileManager.getMissile(level.dimension(), id);

			if (missile == null || missile.hasExploded()) {
				remove(RemovalReason.DISCARDED);
				return;
			}

			if (missile.blastEntity != null) {
				isExploding = true;
			}

			if (!position().equals(missile.position)) {
				setPos(missile.position);
				setDeltaMovement(missile.deltaMovement);
				speed = missile.speed;
				speedModifier = missile.speedModifier;
				radarVisible = missile.radarVisible;
				missileId = missile.missileId;
			}

			entityData.set(TARGET, target);
			entityData.set(MISSILE_ID, missileId);
			entityData.set(START_X, startX);
			entityData.set(START_Z, startZ);
			entityData.set(SPEED, speed);
			entityData.set(IS_ITEM, isItem);
			entityData.set(CURRENTLYEXPLODING, isExploding);
			entityData.set(SPEED_MODIFIER, speedModifier);
			entityData.set(RADAR_VISIBLE, radarVisible);
			entityData.set(Y_ROT, getYRot());
			entityData.set(X_ROT, getXRot());

		} else {
			target = entityData.get(TARGET);
			missileId = entityData.get(MISSILE_ID);
			startX = entityData.get(START_X);
			startZ = entityData.get(START_Z);
			speed = entityData.get(SPEED);
			isItem = entityData.get(IS_ITEM);
			isExploding = entityData.get(CURRENTLYEXPLODING);
			speedModifier = entityData.get(SPEED_MODIFIER);
			radarVisible = entityData.get(RADAR_VISIBLE);
			setYRot(entityData.get(Y_ROT));
			setXRot(entityData.get(X_ROT));
		}

		if (isExploding) {
			return;
		}

		if (!isItem) {
			updateMissileTrajectory();
		}

		Vec3 delta = getDeltaMovement();
		if (delta.lengthSqr() > 1.0E-7D) {
			double lookAhead = Math.max(speed * speedModifier, 0.1);
			Vec3 futurePos = position().add(delta.x * lookAhead, delta.y * lookAhead, delta.z * lookAhead);
			Vec3 movementVec = futurePos.subtract(position());

			float targetYaw = (float) (Mth.atan2(movementVec.x, movementVec.z) * (180D / Math.PI));
			float targetPitch = (float) (Mth.atan2(-movementVec.y, movementVec.horizontalDistance()) * (180D / Math.PI));

			this.setYRot(targetYaw);
			this.setXRot(targetPitch);
		}

		Vec3 finalMovement = getDeltaMovement().scale(speed * speedModifier);
		setPos(getX() + finalMovement.x, getY() + finalMovement.y, getZ() + finalMovement.z);

		if (isClientSide && speed > 0.1f) {
			spawnMissileParticles(finalMovement);
		}
	}

	protected void updateMissileTrajectory() {
		float iDeltaX = target.getX() - startX;
		float iDeltaZ = target.getZ() - startZ;

		float initialDistance = (float) Math.sqrt(iDeltaX * iDeltaX + iDeltaZ * iDeltaZ);
		float halfwayDistance = initialDistance / 2.0F;

		float deltaX = (float) (getX() - startX);
		float deltaZ = (float) (getZ() - startZ);

		float distanceTraveled = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

		double maxRadii = VirtualMissile.MAX_CRUISING_ALTITUDE - VirtualMissile.ARC_TURN_HEIGHT_MIN;

		float turnRadius = (float) Mth.clamp(halfwayDistance, 0.001F, maxRadii);

		float deltaY = (float) (getY() - VirtualMissile.ARC_TURN_HEIGHT_MIN);

		float phi = 0;
		float signY = 1;

		if (halfwayDistance <= maxRadii) {
			if (getY() >= VirtualMissile.ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {
				phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, 0, 1));
			} else if (distanceTraveled >= halfwayDistance) {
				phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
				signY = -1;
			} else if (distanceTraveled >= initialDistance) {
				signY = -1;
			}
			float x = (float) ((iDeltaX / initialDistance) * Math.sin(phi));
			float xz = (float) ((iDeltaZ / initialDistance) * Math.sin(phi));
			setDeltaMovement(new Vec3(x, Math.cos(phi) * signY, xz).normalize());
		} else {
			if (getY() >= VirtualMissile.ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {
				if (distanceTraveled <= turnRadius) {
					phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, 0, 1));
				} else {
					phi = (float) (Math.PI / 2.0);
				}
			} else if (distanceTraveled >= halfwayDistance) {
				if (distanceTraveled >= initialDistance - turnRadius) {
					phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, 0, 1));
					signY = -1;
				} else {
					phi = (float) (Math.PI / 2.0);
				}
			} else if (distanceTraveled >= initialDistance) {
				signY = -1;
			}
			float x = (float) (iDeltaX / initialDistance * Math.sin(phi));
			float xz = (float) (iDeltaZ / initialDistance * Math.sin(phi));
			setDeltaMovement(new Vec3(x, Math.cos(phi) * signY, xz).normalize());
		}

		if (!target.equals(TileQuarry.OUT_OF_REACH) && speed < 3.0F) {
			speed += 0.02F;
		}
	}

	private void spawnMissileParticles(Vec3 finalMovement) {
		float widthOver2 = getDimensions(getPose()).width / 2.0F;

		float motionX = (float) (-finalMovement.x);
		float motionY = (float) (-finalMovement.y);
		float motionZ = (float) (-finalMovement.z);

		int smokeCount = (int) (6 * speedModifier);
		for (int i = 0; i < smokeCount; i++) {
			float x = (float) (getX() - widthOver2 + random.nextFloat() * widthOver2 * 2);
			float y = (float) (getY() - random.nextFloat() * 0.5F);
			float z = (float) (getZ() - widthOver2 + random.nextFloat() * widthOver2 * 2);
			level().addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, motionX * (0.8f + random.nextFloat() * 0.4f), motionY * (0.8f + random.nextFloat() * 0.4f), motionZ * (0.8f + random.nextFloat() * 0.4f));
		}

		int ambientSmokeCount = (int) (4 * speedModifier);
		for (int i = 0; i < ambientSmokeCount; i++) {
			level().addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, false, this.getX(), this.getY(), this.getZ(), random.nextDouble() / 1.5 - 0.3333 + motionX, random.nextDouble() / 1.5 - 0.3333 + motionY, random.nextDouble() / 1.5 - 0.3333 + motionZ);
			level().addParticle(ParticleTypes.CLOUD, false, this.getX(), this.getY(), this.getZ(), random.nextDouble() / 1.5 - 0.3333 + motionX, random.nextDouble() / 1.5 - 0.3333 + motionY, random.nextDouble() / 1.5 - 0.3333 + motionZ);
		}

		int flameCount = (int) (3 * speedModifier);
		for (int i = 0; i < flameCount; i++) {
			level().addParticle(ParticleTypes.FLAME, false, this.getX(), this.getY(), this.getZ(), motionX * 0.4, motionY * 0.4, motionZ * 0.4);
		}

		if (speedModifier > 1.5f) {
			for (int i = 0; i < (int) (2 * speedModifier); i++) {
				level().addParticle(ParticleTypes.LAVA, false, this.getX(), this.getY(), this.getZ(), motionX, motionY, motionZ);
			}
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag compound) {
		if (level() instanceof ServerLevel server && (!server.isPositionEntityTicking(blockPosition()) || !server.hasChunkAt(blockPosition()))) {
			setRemoved(RemovalReason.DISCARDED);
		}
		compound.putInt("range", missileId);
		if (id != null) {
			UUIDUtil.CODEC.encode(id, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> compound.put("id", tag));
		}
		BlockPos.CODEC.encode(target, NbtOps.INSTANCE, new CompoundTag()).result().ifPresent(tag -> compound.put("target", tag));
		compound.putFloat("startx", startX);
		compound.putFloat("startz", startZ);
		compound.putBoolean("isitem", isItem);
		compound.putFloat("speedmodifier", speedModifier);
		compound.putBoolean("radarvisible", radarVisible);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag compound) {
		missileId = compound.getInt("range");
		UUIDUtil.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("id")).result().ifPresent(pair -> id = pair.getFirst());
		BlockPos.CODEC.decode(NbtOps.INSTANCE, compound.getCompound("target")).result().ifPresent(pair -> target = pair.getFirst());
		startX = compound.getFloat("startx");
		startZ = compound.getFloat("startz");
		isItem = compound.getBoolean("isitem");
		speedModifier = compound.getFloat("speedmodifier");
		radarVisible = compound.getBoolean("radarvisible");
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		if (player.isSecondaryUseActive()) {
			return InteractionResult.PASS;
		}
		if (!this.level().isClientSide) {
			return player.startRiding(this, true) ? InteractionResult.CONSUME : InteractionResult.PASS;
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean isPickable() {
		return true;
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level().isClientSide) {
			if (id != null) {
				VirtualMissile missile = MissileManager.getMissile(level().dimension(), id);
				if (missile != null)
					missile.setSpawned(false, -1);
			}
		}
		super.remove(reason);
	}

	@Override
	public boolean isAlwaysTicking() {
		return true;
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
