package ballistix.client.render.tile;

import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import ballistix.bplus.tile.TileHorizontalMissileSilo;
import ballistix.common.item.ItemMissile;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.BallistixMissiles.MissileData;
import voltaic.prefab.block.GenericEntityBlock;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentInventory;
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

public class RenderHorizontalMissileSilo implements BlockEntityRenderer<TileHorizontalMissileSilo> {

    public RenderHorizontalMissileSilo(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileHorizontalMissileSilo tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (tileEntityIn.getLevel() == null) {
            return;
        }

        ItemStack stack = tileEntityIn.<ComponentInventory>getComponent(IComponentType.Inventory).getItem(0);

        if (stack.isEmpty() || !(stack.getItem() instanceof ItemMissile im)) {
            return;
        }

        BlockState state = tileEntityIn.getBlockState();
        Direction facingFromState = state.hasProperty(GenericEntityBlock.FACING) ? state.getValue(GenericEntityBlock.FACING) : null;
        Direction facingFromTile = tileEntityIn.getFacing();
        
        Direction facing = facingFromState != null ? facingFromState : facingFromTile;
        if (facing == null || facing.getAxis().isVertical()) {
            facing = Direction.NORTH;
        }

        matrixStackIn.pushPose();

        // 1. Position at the top of the core block
        matrixStackIn.translate(0.5, 2.5, 0.5);
        
        // 2. Yaw Rotation: Orient the missile towards the silo body
        // Removed the .getOpposite() call to unflip the missile orientation in the silo
        float yaw = facing.toYRot();
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(-yaw));
        
        // 3. Pitch Rotation: Lay the missile down horizontally
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(-90.0F));

        // 4. Center and Offset to the end of the 6-block silo
        matrixStackIn.translate(-0.5, -0.5, -5.5);

        BakedModel model;
        MissileData data = BallistixMissiles.getData(im.id);

        if (data != null) {
            model = Minecraft.getInstance().getModelManager().getModel(data.model);
            matrixStackIn.translate(data.translateX + .2, data.translateY - .5, data.translateZ + 4.5);
            matrixStackIn.scale(data.scaleX, data.scaleY, data.scaleZ);
        } else {
            model = Minecraft.getInstance().getModelManager().getModel(ballistix.client.ClientRegister.MODEL_MISSILETEST);
            matrixStackIn.scale(0.5f, 6f, 0.5f);
        }

        if (model != null) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateBlock(tileEntityIn.getLevel(), model, Blocks.AIR.defaultBlockState(), tileEntityIn.getBlockPos(), matrixStackIn, bufferIn.getBuffer(RenderType.solid()), false, tileEntityIn.getLevel().random, new Random().nextLong(), combinedOverlayIn);
        }

        matrixStackIn.popPose();
    }
}
