package ballistix.datagen.client;

import java.util.Locale;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixPlusBlocks;
import voltaic.datagen.client.VoltaicBlockStateProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BallistixBlockStateProvider extends VoltaicBlockStateProvider {

	public BallistixBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, exFileHelper, References.ID);
	}

	@Override
	protected void registerStatesAndModels() {

		// Tier 0
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.obsidian), existingBlock(blockLoc("explosiveobsidian")), true);
		// Tier 1
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.chemical), existingBlock(blockLoc("explosivechemical")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.attractive), existingBlock(blockLoc("explosiveattractive")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.repulsive), existingBlock(blockLoc("explosiverepulsive")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.incendiary), existingBlock(blockLoc("explosiveincendiary")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.shrapnel), existingBlock(blockLoc("explosiveshrapnel")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.condensive), existingBlock(blockLoc("explosivecondensive")), true);
		// Tier 2
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.thermobaric), existingBlock(blockLoc("explosivethermobaric")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.breaching), existingBlock(blockLoc("explosivebreaching")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.debilitation), existingBlock(blockLoc("explosivedebilitation")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.contagious), existingBlock(blockLoc("explosivecontagious")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.fragmentation), existingBlock(blockLoc("explosivefragmentation")), true);
		// Tier 3
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.emp), existingBlock(blockLoc("explosiveemp")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.nuclear), existingBlock(blockLoc("explosivenuclear")), true);
		// Tier 4
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.antimatter), existingBlock(blockLoc("explosiveantimatter")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.darkmatter), existingBlock(blockLoc("explosivedarkmatter")), true);
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter), existingBlock(blockLoc("explosivelargeantimatter")), true);
		// Other
		simpleBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine), existingBlock(BallistixBlocks.getBlock(SubtypeBlast.landmine)), true);

		horrRotatedBlock(BallistixBlocks.blockMissileSilo.get(), existingBlock(BallistixBlocks.blockMissileSilo.get()), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.blockRadar.get(), existingBlock(BallistixBlocks.blockRadar.get()), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.blockFireControlRadar.get(), existingBlock(BallistixBlocks.blockFireControlRadar.get()), 90, 0, false);
		horrRotatedBlock(BallistixBlocks.blockSamTurret.get(), existingBlock(BallistixBlocks.blockSamTurret.get()),false);
		horrRotatedBlock(BallistixBlocks.blockEsmTower.get(), existingBlock(BallistixBlocks.blockEsmTower.get()),false);
		horrRotatedBlock(BallistixBlocks.blockCiwsTurret.get(), existingBlock(BallistixBlocks.blockCiwsTurret.get()),false);
		horrRotatedBlock(BallistixBlocks.blockLaserTurret.get(), existingBlock(BallistixBlocks.blockLaserTurret.get()),false);
		horrRotatedBlock(BallistixBlocks.blockRailgunTurret.get(), existingBlock(BallistixBlocks.blockRailgunTurret.get()),false);

		horrRotatedBlock(BallistixPlusBlocks.blockHorizontalMissileSilo.get(), existingBlock(blockLoc("horizontal_silo")), false);
	}

	private void simpleExplosive(Block block, ExplosiveParent parent, boolean registerItem) {
		BlockModelBuilder builder = models().withExistingParent(name(block), blockLoc(parent.toString())).texture("3", blockLoc(name(block) + "base")).texture("particle", "#3");
		getVariantBuilder(block).partialState().setModels(new ConfiguredModel(builder));
		if (registerItem) {
			simpleBlockItem(block, builder);
		}
	}

	public enum ExplosiveParent {

		EXPLOSIVE_MODEL_ONE;

		@Override
		public String toString() {
			return super.toString().toLowerCase(Locale.ROOT).replaceAll("_", "");
		}

	}

}
