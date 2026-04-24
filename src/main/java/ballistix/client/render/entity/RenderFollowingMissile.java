package ballistix.client.render.entity;

import ballistix.common.entity.EntityFollowingMissile;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.BallistixMissiles.MissileData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RenderFollowingMissile extends EntityRenderer<EntityFollowingMissile> {

    public RenderFollowingMissile(Context renderManagerIn) {
        super(renderManagerIn);
        shadowRadius = 0.15F;
        shadowStrength = 0.75F;
    }

    @Override
    public void render(EntityFollowingMissile entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        int missileId = entity.missileId;
        Level world = entity.level();

        matrixStackIn.pushPose();

        // Use interpolated rotations for smooth movement.
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        // 1. Align with the entity's movement direction.
        // Using direct yaw instead of (180 - yaw) should correctly align local +Z with world velocity.
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(yaw));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(pitch));

        // 2. Rotate the model: Nose (+Y) turns to point forward (+Z).
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));

        BakedModel model;
        MissileData data = BallistixMissiles.getData(missileId);

        if (data != null) {
            model = Minecraft.getInstance().getModelManager().getModel(data.model);
            matrixStackIn.scale(data.scaleX, data.scaleY, data.scaleZ);
            matrixStackIn.translate(data.translateX, data.translateY, data.translateZ);
        } else {
            model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETEST);
            matrixStackIn.scale(0.5f, 6f, 0.5f);
        }

        if (model != null && model != Minecraft.getInstance().getModelManager().getMissingModel()) {
            // Center the 1x1 base of the model.
            matrixStackIn.translate(-0.5, 0, -0.5);

            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithoutAO(world, model, Blocks.AIR.defaultBlockState(), entity.blockPosition(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, world.random, new Random().nextLong(), 0);
        }

        matrixStackIn.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityFollowingMissile entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
