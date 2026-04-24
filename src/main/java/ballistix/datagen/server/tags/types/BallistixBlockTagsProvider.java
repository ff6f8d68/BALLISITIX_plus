package ballistix.datagen.server.tags.types;

import java.util.concurrent.CompletableFuture;

import ballistix.References;
import ballistix.registers.BallistixBlocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BallistixBlockTagsProvider extends BlockTagsProvider {

	public BallistixBlockTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, References.ID, existingFileHelper);
	}

	@Override
	protected void addTags(Provider provider) {

		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(BallistixBlocks.blockMissileSilo.get(), BallistixBlocks.blockRadar.get(), BallistixBlocks.blockFireControlRadar.get(), BallistixBlocks.blockEsmTower.get(), BallistixBlocks.blockSamTurret.get(), BallistixBlocks.blockCiwsTurret.get(), BallistixBlocks.blockLaserTurret.get(), BallistixBlocks.blockRailgunTurret.get());

		tag(BlockTags.NEEDS_STONE_TOOL).add(BallistixBlocks.blockMissileSilo.get(), BallistixBlocks.blockRadar.get(), BallistixBlocks.blockFireControlRadar.get(), BallistixBlocks.blockEsmTower.get(), BallistixBlocks.blockSamTurret.get(), BallistixBlocks.blockCiwsTurret.get(), BallistixBlocks.blockLaserTurret.get(), BallistixBlocks.blockRailgunTurret.get());

	}

}
