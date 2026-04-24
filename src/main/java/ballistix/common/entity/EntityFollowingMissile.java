package ballistix.common.entity;

import ballistix.registers.BallistixEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;

public class EntityFollowingMissile extends EntityMissile {

    public EntityFollowingMissile(EntityType<? extends EntityFollowingMissile> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityFollowingMissile(Level worldIn) {
        this(BallistixEntities.ENTITY_FOLLOWING_MISSILE.get(), worldIn);
    }

    @Override
    public void tick() {
        // We override the trajectory update to avoid the vertical arc logic in EntityMissile
        super.tick();
    }

    // By overriding this to do nothing, we allow the server-side VirtualMissile 
    // to have full control over the movement via setDeltaMovement/setPos calls,
    // which are synced in super.tick().
    @Override
    public void updateMissileTrajectory() {
        // No-op for following missiles; trajectory is handled by VirtualMissile server-side
    }
}
