package ballistix.api.missile.virtual;

import ballistix.api.explosive.BallistixExplosives;
import ballistix.common.blast.Blast;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.entity.EntityBlast;
import ballistix.common.entity.EntityFollowingMissile;
import ballistix.common.entity.EntityMissile;
import ballistix.common.settings.Constants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import voltaic.api.multiblock.subnodebased.TileMultiSubnode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.registries.BuiltInRegistries;

import org.jetbrains.annotations.Nullable;
import java.util.UUID;

import static net.minecraft.world.phys.Vec3.*;

public class VirtualMissile {

    public static final int MAX_CRUISING_ALTITUDE = 500;
    public static final int WORLD_BUILD_HEIGHT = 320;
    public static final int ARC_TURN_HEIGHT_MIN = 400;

    public static final Codec<VirtualMissile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Vec3.CODEC.fieldOf("position").forGetter(instance0 -> instance0.position),
            Vec3.CODEC.fieldOf("movement").forGetter(instance0 -> instance0.deltaMovement),
            Codec.BOOL.fieldOf("isitem").forGetter(instance0 -> instance0.isItem),
            Vec3.CODEC.fieldOf("launch_coords").forGetter(instance0 -> new Vec3(instance0.startX, 0, instance0.startZ)),
            BlockPos.CODEC.fieldOf("target").forGetter(instance0 -> instance0.target),
            Codec.INT.fieldOf("missile_id").forGetter(instance0 -> instance0.missileId),
            Codec.INT.fieldOf("blasttype").forGetter(instance0 -> instance0.blastOrdinal),
            Codec.BOOL.fieldOf("hasexploded").forGetter(instance0 -> instance0.hasExploded),
            UUIDUtil.CODEC.fieldOf("id").forGetter(instance0 -> instance0.id),
            Codec.BOOL.fieldOf("isspawned").forGetter(instance0 -> instance0.isSpawned),
            Codec.INT.fieldOf("frequency").forGetter(instance0 -> instance0.frequency),
            Codec.INT.fieldOf("entityid").forGetter(instance0 -> instance0.entityId),
            Codec.STRING.fieldOf("customexplosive").forGetter(instance0 -> instance0.customExplosiveBlock == null ? "" : BuiltInRegistries.BLOCKS.getKey(instance0.customExplosiveBlock).toString()),
            Codec.BOOL.fieldOf("radarvisible").forGetter(instance0 -> instance0.radarVisible),
            Vec3.CODEC.fieldOf("kinematics").forGetter(instance0 -> new Vec3(instance0.speed, instance0.speedModifier, instance0.flightMode)),
            Codec.INT.fieldOf("tickcount").forGetter(instance0 -> instance0.tickCount)
    ).apply(instance, VirtualMissile::new));

    public Vec3 position = ZERO;
    public Vec3 deltaMovement = ZERO;
    public float speed = 0.0F;
    public float speedModifier = 1.0F;
    public boolean radarVisible = true;
    public int missileId = -1;

    private final boolean isItem;
    private boolean isSpawned = false;
    public final float startX;
    public final float startZ;
    private final BlockPos target;
    public float health = Constants.MISSILE_HEALTH;
    public final int blastOrdinal;
    public Block customExplosiveBlock = null;
    private boolean hasExploded = false;
    private final UUID id;
    private int tickCount = 0;
    public final int frequency;
    private int entityId = -1;
    public int flightMode = 0; // 0 = Vertical, 1 = Horizontal

    @Nullable
    public EntityBlast blastEntity;

    private VirtualMissile(Vec3 startPos, Vec3 initialMovement, boolean isItem, Vec3 launchCoords, BlockPos target, int missileId, int blastOrdinal, boolean hasExploded, UUID id, boolean isSpawned, int frequency, int entityId, String customExplosive, boolean radarVisible, Vec3 kinematics, int tickCount) {

        position = startPos;
        deltaMovement = initialMovement;
        this.isItem = isItem;

        this.startX = (float) launchCoords.x;
        this.startZ = (float) launchCoords.z;
        this.target = target;
        this.missileId = missileId;
        this.blastOrdinal = blastOrdinal;
        this.hasExploded = hasExploded;
        this.id = id;
        this.isSpawned = isSpawned;
        this.frequency = frequency;
        this.entityId = entityId;
        this.customExplosiveBlock = customExplosive.isEmpty() ? null : BuiltInRegistries.BLOCKS.getValue(new ResourceLocation(customExplosive));
        this.radarVisible = radarVisible;
        
        this.speed = (float) kinematics.x;
        this.speedModifier = (float) kinematics.y;
        this.flightMode = (int) kinematics.z;
        this.tickCount = tickCount;
    }

    public VirtualMissile(Vec3 startPos, Vec3 initialMovement, float initialSpeed, boolean isItem, float startX, float startZ, BlockPos target, int missileId, int blastOrdinal, boolean isSpawned, int frequency, float speedModifier, boolean radarVisible) {
        this(startPos, initialMovement, initialSpeed, isItem, startX, startZ, target, missileId, blastOrdinal, isSpawned, frequency, speedModifier, radarVisible, 0);
    }

    public VirtualMissile(Vec3 startPos, Vec3 initialMovement, float initialSpeed, boolean isItem, float startX, float startZ, BlockPos target, int missileId, int blastOrdinal, boolean isSpawned, int frequency, float speedModifier, boolean radarVisible, int flightMode) {

        position = startPos;
        deltaMovement = initialMovement;
        speed = initialSpeed;
        this.isItem = isItem;

        this.startX = startX;
        this.startZ = startZ;
        this.target = target;
        this.missileId = missileId;
        this.blastOrdinal = blastOrdinal;
        id = UUID.randomUUID();
        this.isSpawned = isSpawned;
        this.frequency = frequency;
        this.speedModifier = speedModifier;
        this.radarVisible = radarVisible;
        this.flightMode = flightMode;
    }

    //ticks on server only
    public void tick(ServerLevel level) {

        tickCount++;

        if (health <= 0) {
            level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0F, 1.0F);
            hasExploded = true;
            return;
        }

        if ((!isItem && target.equals(TileQuarry.OUT_OF_REACH)) || (blastOrdinal == -1 && customExplosiveBlock == null)) {
            hasExploded = true;
            return;
        }

        if (hasExploded) {
            return;
        }

        if (blastEntity != null) {
            if (blastEntity.isRemoved() || blastEntity.getBlast().hasStarted) {
                hasExploded = true;
            }
            return;
        }

        BlockPos collisionPos = projectMovementForCollision(level);

        if (collisionPos != null && (isItem || tickCount > 20) || position.y <= level.getMinBuildHeight()) {

            if (blastOrdinal != -1) {
                SubtypeBlast explosive = SubtypeBlast.values()[blastOrdinal];

                Blast b = explosive.createBlast(level, collisionPos);

                if (b != null) {

                    if (b.isInstantaneous()) {
                        b.performExplosion();

                        hasExploded = true;

                    } else {
                        blastEntity = b.performExplosion();
                        position = new Vec3(position.x - speed * speedModifier * deltaMovement.x, position.y - speed * speedModifier * deltaMovement.y, position.z - speed * speedModifier * deltaMovement.z);
                    }
                    return;

                }
            } else if (customExplosiveBlock != null) {
                BallistixExplosives.ExplosiveData data = BallistixExplosives.EXPLOSIVE_MAPPING.get(customExplosiveBlock);
                if (data != null) {
                    data.onTriggered.accept(level, collisionPos == null ? blockPosition() : collisionPos);
                }
                hasExploded = true;
                return;
            }

        }

        if (!isItem) {
            if (flightMode == 0) {
                tickVerticalFlight();
            } else {
                tickHorizontalFlight();
            }
        }

        if (blastEntity == null) {
            position = new Vec3(position.x + speed * speedModifier * deltaMovement.x, position.y + speed * speedModifier * deltaMovement.y, position.z + speed * speedModifier * deltaMovement.z);
        }

        if (!isItem && !target.equals(TileQuarry.OUT_OF_REACH) && speed < 3.0F) {
            speed += 0.02F;
        }

        if (!isSpawned && level.hasChunkAt(blockPosition()) && level.isPositionEntityTicking(blockPosition())) {

            EntityMissile missile = flightMode == 1 ? new EntityFollowingMissile(level) : new EntityMissile(level);
            missile.setPos(position);
            missile.setDeltaMovement(deltaMovement);
            missile.missileId = missileId;
            missile.speed = speed;
            missile.speedModifier = speedModifier;
            missile.radarVisible = radarVisible;
            missile.id = id;
            missile.isItem = isItem;
            missile.target = target;
            missile.startX = startX;
            missile.startZ = startZ;

            if (level.addFreshEntity(missile)) {
                setSpawned(true, missile.getId());
            }

        }

        if (isSpawned && (!level.hasChunkAt(blockPosition()) || level.getEntity(entityId) == null)) {
            setSpawned(false, -1);
        }

    }

    private void tickVerticalFlight() {
        float iDeltaX = target.getX() - startX;
        float iDeltaZ = target.getZ() - startZ;

        float initialDistance = (float) Math.sqrt(iDeltaX * iDeltaX + iDeltaZ * iDeltaZ);
        float halfwayDistance = initialDistance / 2.0F;

        float deltaX = (float) (position.x - startX);
        float deltaZ = (float) (position.z - startZ);

        float distanceTraveled = (float) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        double maxRadii = MAX_CRUISING_ALTITUDE - ARC_TURN_HEIGHT_MIN;

        float turnRadius = (float) Mth.clamp(halfwayDistance, 0.001F, maxRadii);

        float deltaY = (float) (position.y - ARC_TURN_HEIGHT_MIN);

        float phi = 0;
        float signY = 1;

        if (halfwayDistance <= maxRadii) {

            if (position.y >= ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

                phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, -1, 1));

            } else if (distanceTraveled >= halfwayDistance) {

                phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, -1, 1));
                signY = -1;

            } else if (distanceTraveled >= initialDistance) {

                signY = -1;

            }

            float x = (float) ((iDeltaX / initialDistance) * Math.sin(phi));
            float z = (float) ((iDeltaZ / initialDistance) * Math.sin(phi));

            deltaMovement = new Vec3(x, Math.cos(phi) * signY, z);

        } else {


            if (position.y >= ARC_TURN_HEIGHT_MIN && distanceTraveled < halfwayDistance) {

                if (distanceTraveled <= turnRadius) {

                    phi = (float) Math.asin(Mth.clamp(deltaY / turnRadius, -1, 1));

                } else {

                    phi = (float) (Math.PI / 2.0);

                }

            } else if (distanceTraveled >= halfwayDistance) {

                if (distanceTraveled >= initialDistance - turnRadius) {

                    phi = (float) Math.asin(Mth.clamp((initialDistance - distanceTraveled) / turnRadius, -1, 1));
                    signY = -1;

                } else {

                    phi = (float) (Math.PI / 2.0);

                }

            } else if (distanceTraveled >= initialDistance) {

                signY = -1;

            }

            float x = (float) (iDeltaX / initialDistance * Math.sin(phi));
            float z = (float) (iDeltaZ / initialDistance * Math.sin(phi));

            deltaMovement = new Vec3(x, Math.cos(phi) * signY, z);

        }
    }

    private void tickHorizontalFlight() {
        if (tickCount < 30) { // Stage 0: Ejecting backwards for a longer duration
            // deltaMovement is already set from TileHorizontalMissileSilo
            // It will continue in this direction
            return; 
        } else if (tickCount < 90) { // Stage 1: Pitching up for a longer duration
            deltaMovement = deltaMovement.add(0, 0.15, 0).normalize(); // Increased upward push
        } else { // Stage 2: Seeking target
            Vec3 tar = new Vec3(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
            Vec3 diff = tar.subtract(position);
            double dist = diff.length();
            
            if (dist > 1.0) {
                Vec3 dir = diff.normalize();
                // Smooth tracking, slightly faster adjustment
                deltaMovement = deltaMovement.scale(0.9).add(dir.scale(0.1)).normalize();
            } else {
                deltaMovement = diff.normalize();
            }
        }
    }

    public BlockPos blockPosition() {
        return new BlockPos((int) Math.floor(position.x), (int) Math.floor(position.y), (int) Math.floor(position.z));
    }

    public UUID getId() {
        return id;
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    public void setSpawned(boolean spawned, int id) {
        isSpawned = spawned;
        entityId = id;
    }

    public AABB getBoundingBox() {
        return new AABB(position.x - 0.5F, position.y, position.z - 0.5F, position.x + 0.5F, position.y + 1.0F, position.z + 0.5F);
    }

    @Nullable
    public BlockPos projectMovementForCollision(ServerLevel world) {

        Vec3 currPos = position.scale(1.0);

		int iterations = Math.max(1, (int) Math.ceil(speed * speedModifier * 2));

        BlockPos pos;
        BlockState state;

        for (int i = 0; i < iterations; i++) {

            pos = new BlockPos((int) Math.floor(currPos.x), (int) Math.floor(currPos.y), (int) Math.floor(currPos.z));
            state = world.getBlockState(pos);

            if (state.getCollisionShape(world, pos).isEmpty()) {
				currPos = currPos.add(deltaMovement.scale((speed * speedModifier) / iterations));
                continue;
            }

            return pos;

        }

        return null;

    }

}
