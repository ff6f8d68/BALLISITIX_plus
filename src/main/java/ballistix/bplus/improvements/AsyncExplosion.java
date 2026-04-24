/*
package ballistix.bplus.improvements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import ballistix.References;
import ballistix.api.event.BlastEvent;
import ballistix.common.blast.Blast;
import ballistix.common.block.BlockExplosive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;


@EventBusSubscriber(modid = References.ID)
public class AsyncExplosion {

	private static final Field radiusField = findField(Explosion.class, "radius", "f_46041_");
	private static final Field fireField = findField(Explosion.class, "fire", "f_46039_");

	private static Field findField(Class<?> clazz, String... names) {
		for (String name : names) {
			try {
				Field f = clazz.getDeclaredField(name);
				f.setAccessible(true);
				return f;
			} catch (NoSuchFieldException ignored) {
			}
		}
		return null;
	}

	@SubscribeEvent
	public static void onExplosionStart(ExplosionEvent.Start event) {
		if (event.getLevel().isClientSide || !(event.getLevel() instanceof ServerLevel level)) {
			return;
		}

		Explosion explosion = event.getExplosion();
		float radius = 4.0f;
		boolean causesFire = false;

		try {
			if (radiusField != null) {
				radius = radiusField.getFloat(explosion);
			}
			if (fireField != null) {
				causesFire = fireField.getBoolean(explosion);
			}
		} catch (Exception ignored) {
		}

		DamageSource ds = explosion.getDamageSource();

		event.setCanceled(true);

		Vec3 pos = explosion.getPosition();
		Entity source = explosion.getExploder();

		// Check if it's a nuclear-scale explosion and amplify it
		float amplifiedRadius = radius;
		if (radius > 10) {
			amplifiedRadius *= 3.5f; // Major boost for large explosions
		} else {
			amplifiedRadius *= 1.8f; // Moderate boost for small ones
		}

		explode(level, pos, amplifiedRadius, source, ds, causesFire);
	}

	@SubscribeEvent
	public static void onBallistixBlast(BlastEvent.PreBlastEvent event) {
		if (event.world.isClientSide || !(event.world instanceof ServerLevel level)) {
			return;
		}

		// This intercepts Ballistix's own blast system
		event.setCanceled(true);

		Blast blast = event.iExplosion;
		Vec3 pos = new Vec3(blast.position.getX() + 0.5, blast.position.getY() + 0.5, blast.position.getZ() + 0.5);

		// Attempt to get radius, default to 15 for Ballistix if unknown
		float radius = 20.0f;

		explode(level, pos, radius, null, level.damageSources().explosion(null, null), false);
	}

	public static void explode(ServerLevel world, Vec3 position, float radius, Entity source, DamageSource damageSource, boolean causesFire) {
		CompletableFuture.runAsync(() -> {
			List<BlockPos> blocksToDestroy = new ArrayList<>();
			Random random = new Random();
			int intRadius = (int) Math.ceil(radius);

			for (int x = -intRadius; x <= intRadius; x++) {
				for (int y = -intRadius; y <= intRadius; y++) {
					for (int z = -intRadius; z <= intRadius; z++) {
						double distanceSq = x * x + y * y + z * z;
						if (distanceSq <= radius * radius) {
							double distance = Math.sqrt(distanceSq);
							double probability = Math.pow(1.0 - (distance / radius), 0.25); // Flatter curve for bigger holes

							if (random.nextDouble() < probability) {
								BlockPos targetPos = BlockPos.containing(position.x + x, position.y + y, position.z + z);
								if (distance < 3.0 || isPathClear(world, position, targetPos)) {
									blocksToDestroy.add(targetPos);
								}
							}
						}
					}
				}
			}

			world.getServer().execute(() -> {
				// 1. Damage Entities
				float entityRadius = radius * 1.5f;
				AABB aabb = new AABB(position.x - entityRadius, position.y - entityRadius, position.z - entityRadius, position.x + entityRadius, position.y + entityRadius, position.z + entityRadius);
				List<Entity> list = world.getEntities(source, aabb);

				for (Entity entity : list) {
					if (!entity.ignoreExplosion()) {
						double distSq = entity.distanceToSqr(position);
						if (distSq <= entityRadius * entityRadius) {
							double impact = 1.0 - (Math.sqrt(distSq) / entityRadius);
							entity.hurt(damageSource, (float) (impact * radius * 10.0));

							Vec3 motion = entity.position().subtract(position).normalize().scale(impact * radius * 0.5);
							entity.setDeltaMovement(entity.getDeltaMovement().add(motion));
						}
					}
				}

				// 2. Block Logic (TNT & Destruction)
				for (BlockPos pos : blocksToDestroy) {
					BlockState state = world.getBlockState(pos);
					if (!state.isAir()) {
						if (state.getExplosionResistance(world, pos, null) < 1000) {
							if (state.getBlock() instanceof TntBlock || state.getBlock() instanceof BlockExplosive) {
								// Trigger ignition properly
								state.onCaughtFire(world, pos, null, null);
							}

							world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
							if (causesFire && random.nextInt(5) == 0 && world.getBlockState(pos).isAir() && world.getBlockState(pos.below()).isSolidRender(world, pos.below())) {
								world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
							}
						}
					}
				}

				spawnBetterParticles(world, position, radius, random);
			});
		});
	}

	private static void spawnBetterParticles(ServerLevel level, Vec3 pos, float radius, Random random) {
		level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
		int count = (int) Math.min(500, radius * 8);
		for (int i = 0; i < count; i++) {
			double rx = (random.nextDouble() - 0.5) * radius * 1.6;
			double ry = (random.nextDouble() - 0.5) * radius * 1.6;
			double rz = (random.nextDouble() - 0.5) * radius * 1.6;
			level.sendParticles(ParticleTypes.LARGE_SMOKE, pos.x + rx, pos.y + ry, pos.z + rz, 1, 0, 0, 0, 0.05);
			if (radius > 15 && random.nextFloat() < 0.1f) {
				level.sendParticles(ParticleTypes.EXPLOSION, pos.x + rx * 0.8, pos.y + ry * 0.8, pos.z + rz * 0.8, 1, 0, 0, 0, 0);
			}
		}
	}

	private static boolean isPathClear(Level world, Vec3 start, BlockPos end) {
		Vec3 endVec = Vec3.atCenterOf(end);
		Vec3 dir = endVec.subtract(start);
		double dist = dir.length();
		if (dist < 1.0) return true;
		dir = dir.normalize();
		int steps = 5;
		for (double d = 1.0; d < dist; d += dist / steps) {
			BlockPos pos = BlockPos.containing(start.x + dir.x * d, start.y + dir.y * d, start.z + dir.z * d);
			if (world.getBlockState(pos).getExplosionResistance(world, pos, null) >= 1000) return false;
		}
		return true;
	}
}
*/