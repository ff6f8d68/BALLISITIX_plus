package ballistix.common.network;

import java.util.HashSet;

import ballistix.api.capability.BallistixCapabilities;
import electrodynamics.prefab.tile.GenericTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class SiloRegistry {

	public static void registerSilo(int frequency, GenericTile silo) {

		ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).ifPresent(cap -> {

			cap.addSilo(frequency, silo.getBlockPos());

		});

	}

	public static void unregisterSilo(int frequency, GenericTile silo) {
		ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).ifPresent(cap -> {

			cap.removeSilo(frequency, silo.getBlockPos());

		});
	}

	public static HashSet<GenericTile> getSilos(int freq, Level world) {

		return ServerLifecycleHooks.getCurrentServer().overworld().getCapability(BallistixCapabilities.SILO_REGISTRY).map(cap -> {

			HashSet<GenericTile> silos = new HashSet<>();

			BlockEntity tile;

			for (BlockPos pos : cap.getSilosForFrequency(freq)) {

				tile = world.getBlockEntity(pos);

				if (tile != null && tile instanceof GenericTile silo) {
					silos.add(silo);
				}

			}

			return silos;

		}).orElse(new HashSet<>());

	}
}
