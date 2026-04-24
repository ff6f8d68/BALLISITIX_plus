package ballistix.bplus.block;

import ballistix.bplus.tile.TileRangeDesignator;
import voltaic.prefab.block.GenericMachineBlock;

public class BlockRangeDesignator extends GenericMachineBlock {

    public BlockRangeDesignator() {
        super(TileRangeDesignator::new);
    }
}
