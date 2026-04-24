package ballistix.datagen.server;

import java.util.List;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlockTypes;
import ballistix.registers.BallistixBlocks;
import electrodynamics.datagen.server.ElectrodynamicsLootTablesProvider;
import net.minecraft.world.level.block.Block;

public class BallistixLootTablesProvider extends ElectrodynamicsLootTablesProvider {

	public BallistixLootTablesProvider() {
		super(References.ID);
	}

	@Override
	protected void generate() {

		for (SubtypeBlast blast : SubtypeBlast.values()) {
			addSimpleBlock(BallistixBlocks.getBlock(blast));
		}

		addMachineTable(BallistixBlocks.blockMissileSilo.get(), BallistixBlockTypes.TILE_MISSILESILO, true, false, false, false, false);
		addSimpleBlock(BallistixBlocks.blockRadar.get());
		addSimpleBlock(BallistixBlocks.blockFireControlRadar.get());
		addSimpleBlock(BallistixBlocks.blockEsmTower.get());
		addSimpleBlock(BallistixBlocks.blockSamTurret.get());
		addSimpleBlock(BallistixBlocks.blockCiwsTurret.get());
		addSimpleBlock(BallistixBlocks.blockLaserTurret.get());
		addSimpleBlock(BallistixBlocks.blockRailgunTurret.get());

	}

	@Override
	public List<Block> getExcludedBlocks() {
		return List.of();
	}

}
