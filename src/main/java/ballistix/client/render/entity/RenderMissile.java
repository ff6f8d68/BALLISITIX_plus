package ballistix.client.render.entity;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import ballistix.common.entity.EntityMissile;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.BallistixMissiles.MissileData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.math.Axis;

@OnlyIn(Dist.CLIENT)
public class RenderMissile extends EntityRenderer<EntityMissile> {

	private static final Logger log = LoggerFactory.getLogger(RenderMissile.class);

	public RenderMissile(Context renderManagerIn) {
		super(renderManagerIn);
		shadowRadius = 0.15F;
		shadowStrength = 0.75F;
	}

	@Override
	public void render(EntityMissile entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		int missileId = entity.missileId;

		Level world = entity.level();

		matrixStackIn.pushPose();

		// Use interpolated rotations for smooth movement.
		float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
		float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

		// 1. Align with entity yaw and pitch.
		// Yaw 0 is South (+Z). mulPose rotates clockwise.
		matrixStackIn.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));
		matrixStackIn.mulPose(Axis.XP.rotationDegrees(pitch));

		// 2. Rotate the model (Assumption: Nose points UP (+Y)) to point forward (+Z).
		matrixStackIn.mulPose(Axis.XP.rotationDegrees(90.0F));

		BakedModel model = null;
		MissileData dataFound = BallistixMissiles.getData(missileId);

		if (dataFound != null) {
			model = Minecraft.getInstance().getModelManager().getModel(dataFound.model);
			matrixStackIn.scale(dataFound.scaleX, dataFound.scaleY, dataFound.scaleZ);
			matrixStackIn.translate(dataFound.translateX, dataFound.translateY, dataFound.translateZ);
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
	public boolean shouldRender(EntityMissile livingEntityIn, Frustum camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMissile entity) {
		return InventoryMenu.BLOCK_ATLAS;
	}
}
