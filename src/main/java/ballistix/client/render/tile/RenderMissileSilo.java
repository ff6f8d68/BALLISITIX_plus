package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;

import ballistix.common.item.ItemMissile;
import ballistix.common.tile.TileMissileSilo;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.BallistixMissiles.MissileData;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class RenderMissileSilo implements BlockEntityRenderer<TileMissileSilo> {

	public RenderMissileSilo(BlockEntityRendererProvider.Context context) {
	}

	@Override
	public void render(TileMissileSilo tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		ItemStack stack = tileEntityIn.<ComponentInventory>getComponent(IComponentType.Inventory).getItem(0);

		if (stack.isEmpty() || !(stack.getItem() instanceof ItemMissile im)) {
			return;
		}

		matrixStackIn.pushPose();

		BakedModel model = null;
		
		MissileData data = BallistixMissiles.getData(im.id);

		if (data != null) {
			model = Minecraft.getInstance().getModelManager().getModel(data.model);
			matrixStackIn.translate(0.5f + data.translateX, 0.05f + data.translateY, 0.5f + data.translateZ);
			matrixStackIn.scale(data.scaleX, data.scaleY, data.scaleZ);
		} else {
			model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETEST);
			matrixStackIn.translate(0.5f, 0.05f, 0.5f);
			matrixStackIn.scale(0.5f, 6f, 0.5f);
		}

		if (model != null) {
			Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, tileEntityIn.getBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), combinedOverlayIn);
		}

		matrixStackIn.popPose();
	}
}
