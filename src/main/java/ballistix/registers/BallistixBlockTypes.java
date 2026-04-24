package ballistix.registers;

import ballistix.References;
import ballistix.common.tile.TileESMTower;
import ballistix.common.tile.TileMissileSilo;
import ballistix.bplus.tile.TileHorizontalMissileSilo;
import ballistix.bplus.tile.TileDartPod;
import ballistix.bplus.tile.TileBigDartPod;
import ballistix.bplus.tile.TileDesignator;
import ballistix.bplus.tile.TileRangeDesignator;
import ballistix.common.tile.radar.TileFireControlRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.common.tile.turret.antimissile.TileTurretCIWS;
import ballistix.common.tile.turret.antimissile.TileTurretLaser;
import ballistix.common.tile.turret.antimissile.TileTurretRailgun;
import ballistix.common.tile.turret.antimissile.TileTurretSAM;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixBlockTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPES, References.ID);
	
	public static final RegistryObject<BlockEntityType<TileMissileSilo>> TILE_MISSILESILO = BLOCK_ENTITY_TYPES.register("missilesilo", () -> BlockEntityType.Builder.of(TileMissileSilo::new, BallistixBlocks.blockMissileSilo.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileHorizontalMissileSilo>> TILE_HORIZONTALMISSILESILO = BLOCK_ENTITY_TYPES.register("horizontalsilo", () -> BlockEntityType.Builder.of(TileHorizontalMissileSilo::new, BallistixPlusBlocks.blockHorizontalMissileSilo.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileDartPod>> TILE_DARTPOD = BLOCK_ENTITY_TYPES.register("dartpod", () -> BlockEntityType.Builder.of(TileDartPod::new, BallistixPlusBlocks.blockDartPod.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileBigDartPod>> TILE_BIGDARTPOD = BLOCK_ENTITY_TYPES.register("bigdartpod", () -> BlockEntityType.Builder.of(TileBigDartPod::new, new Block[] { BallistixPlusBlocks.blockBigDartPod.get() }).build(null));
	public static final RegistryObject<BlockEntityType<TileDesignator>> TILE_DESIGNATOR = BLOCK_ENTITY_TYPES.register("designator", () -> BlockEntityType.Builder.of(TileDesignator::new, BallistixPlusBlocks.blockDesignator.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileRangeDesignator>> TILE_RANGEDESIGNATOR = BLOCK_ENTITY_TYPES.register("rangedesignator", () -> BlockEntityType.Builder.of(TileRangeDesignator::new, BallistixPlusBlocks.blockRangeDesignator.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileSearchRadar>> TILE_RADAR = BLOCK_ENTITY_TYPES.register("radar", () -> BlockEntityType.Builder.of(TileSearchRadar::new, BallistixBlocks.blockRadar.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileFireControlRadar>> TILE_FIRECONTROLRADAR = BLOCK_ENTITY_TYPES.register("firecontrolradar", () -> BlockEntityType.Builder.of(TileFireControlRadar::new, BallistixBlocks.blockFireControlRadar.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileESMTower>> TILE_ESMTOWER = BLOCK_ENTITY_TYPES.register("esmtower", () -> BlockEntityType.Builder.of(TileESMTower::new, BallistixBlocks.blockEsmTower.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileTurretSAM>> TILE_SAMTURRET = BLOCK_ENTITY_TYPES.register("samturret", () -> BlockEntityType.Builder.of(TileTurretSAM::new, BallistixBlocks.blockSamTurret.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileTurretCIWS>> TILE_CIWSTURRET = BLOCK_ENTITY_TYPES.register("ciwsturret", () -> BlockEntityType.Builder.of(TileTurretCIWS::new, BallistixBlocks.blockCiwsTurret.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileTurretLaser>> TILE_LASERTURRET = BLOCK_ENTITY_TYPES.register("laserturret", () -> BlockEntityType.Builder.of(TileTurretLaser::new, BallistixBlocks.blockLaserTurret.get()).build(null));
	public static final RegistryObject<BlockEntityType<TileTurretRailgun>> TILE_RAILGUNTURRET = BLOCK_ENTITY_TYPES.register("railgunturret", () -> BlockEntityType.Builder.of(TileTurretRailgun::new, BallistixBlocks.blockRailgunTurret.get()).build(null));

}
