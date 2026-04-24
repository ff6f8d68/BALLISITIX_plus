package ballistix.registers;

import ballistix.References;
import ballistix.bplus.block.BlockDartPod;
import ballistix.bplus.block.BlockHorizontalMissileSilo;
import ballistix.bplus.block.BlockDesignator;
import ballistix.bplus.block.BlockBigDartPod;
import ballistix.bplus.block.BlockRangeDesignator;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixPlusBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCKS, References.ID);

    public static final RegistryObject<BlockHorizontalMissileSilo> blockHorizontalMissileSilo = BLOCKS.register("horizontalsilo", BlockHorizontalMissileSilo::new);
    public static final RegistryObject<BlockDartPod> blockDartPod = BLOCKS.register("dartpod", BlockDartPod::new);
    public static final RegistryObject<BlockDesignator> blockDesignator = BLOCKS.register("designator", BlockDesignator::new);
    public static final RegistryObject<BlockBigDartPod> blockBigDartPod = BLOCKS.register("bigdartpod", BlockBigDartPod::new);
    public static final RegistryObject<BlockRangeDesignator> blockRangeDesignator = BLOCKS.register("rangedesignator", BlockRangeDesignator::new);
}
