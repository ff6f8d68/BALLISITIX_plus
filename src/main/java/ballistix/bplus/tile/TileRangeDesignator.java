package ballistix.bplus.tile;

import ballistix.common.network.SiloRegistry;
import ballistix.common.tile.TileMissileSilo;
import ballistix.bplus.block.BlockRangeDesignator;
import ballistix.bplus.inventory.container.ContainerRangeDesignator;
import ballistix.registers.BallistixBlockTypes;
import voltaic.prefab.block.GenericEntityBlock;
import voltaic.prefab.properties.Property;
import voltaic.prefab.properties.PropertyType;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.type.ComponentContainerProvider;
import voltaic.prefab.tile.components.type.ComponentPacketHandler;
import voltaic.prefab.tile.components.type.ComponentTickable;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.prefab.utilities.object.Location;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TileRangeDesignator extends GenericTile {

    public Property<Integer> frequency = property(new Property<>(PropertyType.Integer, "frequency", 0).onChange((prop, prevFreq) -> {
        if (level.isClientSide) return;
        // Frequency is just stored, no registry needed for designators
    }));
    
    public Property<BlockPos> lastTarget = property(new Property<>(PropertyType.BlockPos, "lasttarget", BlockPos.ZERO));
    public Property<Boolean> hasTarget = property(new Property<>(PropertyType.Boolean, "hastarget", false));
    public Property<Integer> lastLaunchedSiloIndex = property(new Property<>(PropertyType.Integer, "lastLaunchedSiloIndex", -1));

    private boolean wasPowered = false;
    private int raycastCooldown = 0;

    public TileRangeDesignator(BlockPos pos, BlockState state) {
        super(BallistixBlockTypes.TILE_RANGEDESIGNATOR.get(), pos, state);
        addComponent(new ComponentTickable(this).tickServer(this::tickServer));
        addComponent(new ComponentPacketHandler(this));
        addComponent(new ComponentContainerProvider("container.rangedesignator", this).createMenu((id, player) -> new ContainerRangeDesignator(id, player, new SimpleContainer(0), getCoordsArray())));
    }

    protected void tickServer(ComponentTickable tickable) {
        boolean isPowered = level.hasNeighborSignal(getBlockPos());
        
        // Detect rising edge of redstone signal
        if (isPowered && !wasPowered) {
            performRaycastAndLaunch();
        }
        
        wasPowered = isPowered;
        
        if (raycastCooldown > 0) {
            raycastCooldown--;
        }
    }

    private void performRaycastAndLaunch() {
        if (level.isClientSide) return;
        if (raycastCooldown > 0) return;
        
        // Get the facing direction of the block
        Direction facing = Direction.NORTH;
        BlockState state = getBlockState();
        if (state.hasProperty(GenericEntityBlock.FACING)) {
            facing = state.getValue(GenericEntityBlock.FACING);
        }
        
        // Cast ray from the block position in the facing direction
        Vec3 startPos = new Vec3(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5);
        Vec3 endPos = startPos.add(new Vec3(facing.getStepX() * 1000, facing.getStepY() * 1000, facing.getStepZ() * 1000));
        
        // Use ClipContext for proper raycasting
        ClipContext context = new ClipContext(startPos, endPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, null);
        BlockHitResult hitResult = level.clip(context);
        
        if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
            BlockPos targetPos = hitResult.getBlockPos();
            
            // Store the target
            lastTarget.set(targetPos);
            hasTarget.set(true);
            
            // Launch all missiles on this frequency to the target
            launchMissilesToTarget(targetPos);
            
            // Cooldown to prevent spam
            raycastCooldown = 20;
        }
    }

    private void launchMissilesToTarget(BlockPos targetPos) {
        int freq = frequency.get();
        
        // Get all silos registered to this frequency
        Set<GenericTile> silosSet = SiloRegistry.getSilos(freq, level);
        if (silosSet.isEmpty()) return;
        
        // Convert to list for indexed access
        List<GenericTile> silos = new ArrayList<>(silosSet);

        // Launch ONE silo at a time in sequence (like normal Designator)
        int currentIndex = (lastLaunchedSiloIndex.get() + 1) % silos.size();
        lastLaunchedSiloIndex.set(currentIndex);

        GenericTile siloToLaunch = silos.get(currentIndex);
        int range = 0;
        Property<BlockPos> targetProp = null;
        
        if (siloToLaunch instanceof TileHorizontalMissileSilo h) {
            range = h.range.get();
            targetProp = h.target;
        } else if (siloToLaunch instanceof TileDartPod d) {
            range = d.range.get();
            targetProp = d.target;
        } else if (siloToLaunch instanceof TileMissileSilo s) {
            range = s.range.get();
            targetProp = s.target;
        } else if (siloToLaunch instanceof TileBigDartPod bd) {
            range = bd.range.get();
            targetProp = bd.target;
        }

        // Check range (0 = unlimited)
        double distance = TileMissileSilo.calculateDistance(siloToLaunch.getBlockPos(), targetPos);
        if (range == 0 || range >= distance) {
            // Set target and trigger launch
            if (targetProp != null) {
                targetProp.set(targetPos);
            }
            
            if (siloToLaunch instanceof TileHorizontalMissileSilo h) {
                h.shouldLaunch = true;
            } else if (siloToLaunch instanceof TileDartPod d) {
                d.shouldLaunch = true;
            } else if (siloToLaunch instanceof TileMissileSilo s) {
                s.shouldLaunch = true;
            } else if (siloToLaunch instanceof TileBigDartPod bd) {
                bd.shouldLaunch = true;
            }
        }
    }

    @Override 
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("wasPowered", wasPowered);
        nbt.putInt("raycastCooldown", raycastCooldown);
        nbt.putInt("lastLaunchedSiloIndex", lastLaunchedSiloIndex.get());
    }
    
    @Override 
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        wasPowered = nbt.getBoolean("wasPowered");
        raycastCooldown = nbt.getInt("raycastCooldown");
        lastLaunchedSiloIndex.set(nbt.getInt("lastLaunchedSiloIndex"));
    }
    
    @Override 
    public Component getName() { 
        return Component.translatable("container.rangedesignator"); 
    }
}
