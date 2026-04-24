package ballistix.bplus.items;

import ballistix.common.item.ItemLaserDesignator;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import electrodynamics.prefab.utilities.math.MathUtils;
import electrodynamics.prefab.utilities.object.Location;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import ballistix.References;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import electrodynamics.prefab.utilities.RenderingUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.minecraft.network.chat.Component;

@Mod.EventBusSubscriber(modid = References.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ItemRangeDesignator extends ItemLaserDesignator {

    public ItemRangeDesignator() {
        super(() -> BallistixCreativeTabs.PLUS.get());
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal("Range Designator");
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!worldIn.isClientSide || !isSelected) {
            return;
        }

        Location trace = MathUtils.getRaytracedBlock(entityIn);

        if (trace == null) {
            return;
        }

        if (entityIn instanceof Player player) {
            double distance = player.position().distanceTo(new Vec3(trace.x(), trace.y(), trace.z()));
            player.displayClientMessage(BallistixTextUtils.chatMessage("radargun.text", String.format("%.2f m", distance)), true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void renderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean hasRangeSelector = mainHand.getItem() instanceof ItemRangeDesignator || offHand.getItem() instanceof ItemRangeDesignator;

        if (!hasRangeSelector) {
            return;
        }

        Location trace = MathUtils.getRaytracedBlock(player);
        if (trace == null) {
            return;
        }

        Vec3 start = player.getEyePosition(event.getPartialTick());
        Vec3 end = new Vec3(trace.x(), trace.y(), trace.z());

        PoseStack matrixStack = event.getPoseStack();
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        matrixStack.pushPose();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());

        double deltaX = end.x - start.x;
        double deltaY = end.y - start.y;
        double deltaZ = end.z - start.z;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        // Render a laser beam using a filled box or lines
        // For simplicity and visibility, we'll use a thin filled box if possible, 
        // but since we're in a static context and want a simple "ray", lines or a custom renderer works.
        // Let's use the same technique as RenderLaserTurret but adapted for world space.
        
        matrixStack.pushPose();
        matrixStack.translate(start.x, start.y, start.z);
        
        double pitch = Math.asin(deltaY / distance);
        double yaw = Math.atan2(-deltaX, -deltaZ);
        
        matrixStack.mulPose(MathUtils.rotQuaternionDeg((float)Math.toDegrees(pitch), (float)Math.toDegrees(yaw), 0));
        
        AABB box = new AABB(0, -0.01, -0.01, distance, 0.01, 0.01);
        TextureAtlasSprite sprite = electrodynamics.client.ClientRegister.CACHED_TEXTUREATLASSPRITES.get(electrodynamics.client.ClientRegister.TEXTURE_WHITE);
        
        if (sprite != null) {
             RenderingUtils.renderFilledBoxNoOverlay(matrixStack, buffer.getBuffer(RenderType.lightning()), box, 1.0F, 0, 0, 0.5F, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), 15728880);
        }
        
        matrixStack.popPose();
        matrixStack.popPose();
        buffer.endBatch();
    }
}
