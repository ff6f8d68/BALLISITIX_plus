package ballistix.registers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ballistix.References;
import ballistix.common.block.BlockESMTower;
import ballistix.common.block.BlockExplosive;
import ballistix.common.block.BlockMissileSilo;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import voltaic.api.ISubtype;
import voltaic.prefab.block.GenericMachineBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCKS, References.ID);

	public static final HashMap<ISubtype, RegistryObject<Block>> SUBTYPEBLOCKREGISTER_MAPPINGS = new HashMap<>();

	public static final RegistryObject<BlockMissileSilo> blockMissileSilo = BLOCKS.register("missilesilo", BlockMissileSilo::new);
	public static final RegistryObject<GenericMachineBlock> blockRadar = BLOCKS.register("radar", () -> new GenericMachineBlock(TileSearchRadar::new));
	public static final RegistryObject<GenericMachineBlock> blockFireControlRadar = BLOCKS.register("firecontrolradar", () -> new GenericMachineBlock(TileFireControlRadar::new));
	public static final RegistryObject<BlockESMTower> blockEsmTower = BLOCKS.register("esmtower", BlockESMTower::new);
	public static final RegistryObject<GenericMachineBlock> blockSamTurret = BLOCKS.register("samturret", () -> new GenericMachineBlock(TileTurretSAM::new));
	public static final RegistryObject<GenericMachineBlock> blockCiwsTurret = BLOCKS.register("ciwsturret", () -> new GenericMachineBlock(TileTurretCIWS::new));
	public static final RegistryObject<GenericMachineBlock> blockLaserTurret = BLOCKS.register("laserturret", () -> new GenericMachineBlock(TileTurretLaser::new));
	public static final RegistryObject<GenericMachineBlock> blockRailgunTurret = BLOCKS.register("railgunturret", () -> new GenericMachineBlock(TileTurretRailgun::new));

	static {
		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			SUBTYPEBLOCKREGISTER_MAPPINGS.put(subtype, BLOCKS.register(subtype.tag(), () -> new BlockExplosive(subtype)));
		}
	}

	public static Block[] getAllBlockForSubtype(ISubtype[] values) {
		List<Block> list = new ArrayList<>();
		for (ISubtype value : values) {
			list.add(SUBTYPEBLOCKREGISTER_MAPPINGS.get(value).get());
		}
		return list.toArray(new Block[] {});
	}

	public static Block getBlock(ISubtype value) {
		return SUBTYPEBLOCKREGISTER_MAPPINGS.get(value).get();
	}

}
