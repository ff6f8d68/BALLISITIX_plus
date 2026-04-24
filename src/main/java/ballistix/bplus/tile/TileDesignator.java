package ballistix.bplus.tile;

import ballistix.common.network.SiloRegistry;
import ballistix.common.tile.TileMissileSilo;
import ballistix.registers.BallistixBlockTypes;
import ballistix.bplus.inventory.container.ContainerDesignator;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.properties.PropertyType;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.type.ComponentPacketHandler;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.tile.components.type.ComponentContainerProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileDesignator extends GenericTile {

    public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0));
    public Property<BlockPos> target = property(new Property<>(PropertyType.BlockPos, "target", BlockPos.ZERO));
    public Property<Integer> lastLaunchedSiloIndex = property(new Property<>(PropertyType.Integer, "lastLaunchedSiloIndex", -1));

    private boolean wasPowered = false;

    public TileDesignator(BlockPos pos, BlockState state) {
        super(BallistixBlockTypes.TILE_DESIGNATOR.get(), pos, state);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentContainerProvider("container.designator", this).createMenu((id, player) -> new ContainerDesignator(id, player, new SimpleContainer(0), getCoordsArray())));
    }

    protected void tickServer(ComponentTickable tickable) {
        boolean isPowered = level.hasNeighborSignal(getBlockPos());
        if (isPowered && !wasPowered) {
            launchMissiles();
        }
        wasPowered = isPowered;
    }

    private void launchMissiles() {
        if (level.isClientSide) return;
        int freq = frequency.get();
        BlockPos targetPos = target.get();
        
        List<GenericTile> silos = new ArrayList<>(SiloRegistry.getSilos(freq, level));
        if (silos.isEmpty()) return;

        int currentIndex = (lastLaunchedSiloIndex.get() + 1) % silos.size();
        lastLaunchedSiloIndex.set(currentIndex);

        GenericTile siloToLaunch = silos.get(currentIndex);
        int range = 0;
        Property<BlockPos> targetProp = null;
        
        if (siloToLaunch instanceof TileHorizontalMissileSilo h) {
            range = h.range.get();
            targetProp = h.target;
            h.shouldLaunch = true;
        } else if (siloToLaunch instanceof TileDartPod d) {
            range = d.range.get();
            targetProp = d.target;
            d.shouldLaunch = true;
        } else if (siloToLaunch instanceof TileMissileSilo s) {
            range = s.range.get();
            targetProp = s.target;
            s.shouldLaunch = true;
        }

        if (targetProp != null) {
            double dist = TileMissileSilo.calculateDistance(siloToLaunch.getBlockPos(), targetPos);
            if (range == 0 || range >= dist) {
                targetProp.set(targetPos);
            }
        }
    }

    @Override 
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("wasPowered", wasPowered);
    }
    
    @Override 
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        wasPowered = nbt.getBoolean("wasPowered");
    }
    
    @Override public Component getName() { return Component.translatable("container.designator"); }
}
