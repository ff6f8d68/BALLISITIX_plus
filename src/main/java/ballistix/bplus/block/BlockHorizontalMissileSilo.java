package ballistix.bplus.block;

import ballistix.bplus.tile.TileHorizontalMissileSilo;
import electrodynamics.api.multiblock.Subnode;
import electrodynamics.api.multiblock.parent.IMultiblockParentBlock;
import electrodynamics.api.multiblock.parent.IMultiblockParentTile;
import electrodynamics.prefab.block.GenericEntityBlock;
import electrodynamics.prefab.block.GenericMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockHorizontalMissileSilo extends GenericMachineBlock implements IMultiblockParentBlock {

    public BlockHorizontalMissileSilo() {
        super(TileHorizontalMissileSilo::new);
    }

    public static final Subnode[] SUBNODES_SOUTH = new Subnode[53];
    public static final Subnode[] SUBNODES_NORTH = new Subnode[53];
    public static final Subnode[] SUBNODES_EAST = new Subnode[53];
    public static final Subnode[] SUBNODES_WEST = new Subnode[53];

    static {
        // 3x3x6 silo. Core at 0,0,0.
        // 3 blocks wide (X: -1 to 1), 3 blocks high (Y: 0 to 2), 6 blocks long (Z: 0 to 5)
        
        int idx = 0;
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 5; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Core
                    SUBNODES_NORTH[idx++] = new Subnode(new BlockPos(x, y, z), Shapes.block());
                }
            }
        }
        
        // Helper to rotate nodes
        rotateNodes(Direction.NORTH, Direction.SOUTH, SUBNODES_NORTH, SUBNODES_SOUTH);
        rotateNodes(Direction.NORTH, Direction.EAST, SUBNODES_NORTH, SUBNODES_EAST);
        rotateNodes(Direction.NORTH, Direction.WEST, SUBNODES_NORTH, SUBNODES_WEST);
    }

    private static void rotateNodes(Direction from, Direction to, Subnode[] source, Subnode[] target) {
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < source.length; i++) {
            if (source[i] == null) continue;
            BlockPos pos = source[i].pos();
            for (int j = 0; j < times; j++) {
                pos = new BlockPos(-pos.getZ(), pos.getY(), pos.getX());
            }
            target[i] = new Subnode(pos, source[i].getShape(to));
        }
    }

    @Override
    public boolean hasMultiBlock() {
        return true;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        Subnode[] nodes = switch (state.getValue(GenericEntityBlock.FACING)) {
            case EAST -> SUBNODES_EAST;
            case WEST -> SUBNODES_WEST;
            case NORTH -> SUBNODES_NORTH;
            case SOUTH -> SUBNODES_SOUTH;
            default -> SUBNODES_SOUTH;
        };
        return isValidMultiblockPlacement(state, worldIn, pos, nodes);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (worldIn.getBlockEntity(pos) instanceof IMultiblockParentTile multi) {
            multi.onNodeReplaced(worldIn, pos, false);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.getBlockEntity(pos) instanceof IMultiblockParentTile multi) {
            multi.onNodePlaced(worldIn, pos, state, placer, stack);
        }
    }
}
