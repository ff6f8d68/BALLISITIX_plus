package ballistix.bplus.block;

import ballistix.bplus.tile.TileDesignator;
import voltaic.prefab.block.GenericMachineBlock;

public class BlockDesignator extends GenericMachineBlock {

    public BlockDesignator() {
        super(TileDesignator::new);
    }
}
