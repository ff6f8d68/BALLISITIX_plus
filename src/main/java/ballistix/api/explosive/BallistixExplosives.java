package ballistix.api.explosive;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class BallistixExplosives {

    public static final Map<Block, ExplosiveData> EXPLOSIVE_MAPPING = new HashMap<>();

    public static void registerExplosive(Block block, int fuse, float radius) {
        registerExplosive(block, fuse, (world, pos) -> world.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius, Level.ExplosionInteraction.TNT));
    }

    public static void registerExplosive(Block block, int fuse, BiConsumer<Level, BlockPos> onTriggered) {
        EXPLOSIVE_MAPPING.put(block, new ExplosiveData(fuse, onTriggered));
    }

    public static class ExplosiveData {
        public final int fuse;
        public final BiConsumer<Level, BlockPos> onTriggered;

        public ExplosiveData(int fuse, BiConsumer<Level, BlockPos> onTriggered) {
            this.fuse = fuse;
            this.onTriggered = onTriggered;
        }
    }
}
