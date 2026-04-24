package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import ballistix.bplus.tile.TileBigDartPod;
import ballistix.common.item.ItemMissile;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.BallistixMissiles.MissileData;
import electrodynamics.prefab.block.GenericEntityBlock;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class RenderBigDartPod implements BlockEntityRenderer<TileBigDartPod> {

    public RenderBigDartPod(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileBigDartPod tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntityIn.getLevel() == null) {
            return;
        }

        ComponentInventory inv = tileEntityIn.getComponent(IComponentType.Inventory);
        BlockState state = tileEntityIn.getBlockState();
        Direction facingFromState = state.hasProperty(GenericEntityBlock.FACING) ? state.getValue(GenericEntityBlock.FACING) : null;
        Direction facingFromTile = tileEntityIn.getFacing();
        
        Direction facing = facingFromState != null ? facingFromState : facingFromTile;
        if (facing == null || facing.getAxis().isVertical()) {
            facing = Direction.NORTH;
        }

        // Render up to 4 missiles in the 2x2x2 structure
        for (int i = 0; i < 4; i++) {
            ItemStack stack = inv.getItem(TileBigDartPod.MISSILE_SLOTS[i]);
            
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemMissile im)) {
                continue;
            }

            matrixStackIn.pushPose();

            // Calculate offset for each missile in the 2x2 grid
            double offX = (i % 2) * 0.8;
            double offY = (i / 2) * 0.8;
            
            // Position at the launch point (front face of the 2x2x2 structure)
            matrixStackIn.translate(
                0.5 + facing.getStepX() * 0.5 + offX, 
                0.5 + offY, 
                0.5 + facing.getStepZ() * 0.5
            );
            
            // Rotate to face the launch direction
            float yaw = facing.toYRot();
            matrixStackIn.mulPose(Axis.YP.rotationDegrees(-yaw));
            
            // Tilt slightly upward for launch trajectory
            matrixStackIn.mulPose(Axis.XP.rotationDegrees(-20.0F));

            // Center the missile model
            matrixStackIn.translate(-0.5, -0.5, -0.5);

            BakedModel model;
            MissileData data = BallistixMissiles.getData(im.id);

            if (data != null) {
                model = Minecraft.getInstance().getModelManager().getModel(data.model);
                matrixStackIn.translate(data.translateX, data.translateY, data.translateZ);
                matrixStackIn.scale(data.scaleX, data.scaleY, data.scaleZ);
            } else {
                model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETEST);
                matrixStackIn.scale(0.5f, 1.5f, 0.5f);
            }

            if (model != null) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(
                    tileEntityIn.getLevel(), 
                    model, 
                    Blocks.AIR.defaultBlockState(), 
                    tileEntityIn.getBlockPos(), 
                    matrixStackIn, 
                    bufferIn.getBuffer(RenderType.solid()), 
                    false, 
                    tileEntityIn.getLevel().random, 
                    new Random().nextLong(), 
                    combinedOverlayIn
                );
            }

            matrixStackIn.popPose();
        }
    }
}
