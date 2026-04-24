package ballistix.bplus.block;

import ballistix.bplus.tile.TileRangeDesignator;
import electrodynamics.prefab.block.GenericMachineBlock;

public class BlockRangeDesignator extends GenericMachineBlock {

    public BlockRangeDesignator() {
        super(TileRangeDesignator::new);
    }
}
