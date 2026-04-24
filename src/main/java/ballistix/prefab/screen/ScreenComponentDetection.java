package ballistix.prefab.screen;

import ballistix.api.radar.IDetected;
import ballistix.client.screen.ScreenSearchRadar;
import ballistix.common.inventory.container.ContainerSearchRadar;
import ballistix.common.tile.radar.TileSearchRadar;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ScreenComponentDetection extends AbstractWidget {

    private IDetected.Detected detection;
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("ballistix", "textures/screen/component/frequency.png");

    public ScreenComponentDetection(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int xAxis, int yAxis, float partialTick) {
        if (!this.visible) {
            return;
        }

        ScreenSearchRadar screen = (ScreenSearchRadar) Minecraft.getInstance().screen;
        if (screen == null) {
            return;
        }

        ContainerSearchRadar menu = (ContainerSearchRadar) screen.getMenu();
        TileSearchRadar tile = menu.getHostFromIntArray();

        if (tile == null) {
            return;
        }

        // Draw background box
        graphics.blit(TEXTURE_LOCATION, getX(), getY(), 0, 0, width, height, width, height);

        if(detection == null) {
            return;
        }

        graphics.renderItem(new ItemStack(detection.getItem()), getX() + 2, getY() + 4);

        Font font = Minecraft.getInstance().font;

        Component text = Component.literal(new BlockPos((int)detection.getPosition().x,(int)detection.getPosition().y,(int)detection.getPosition().z).toString());

        int x = getX() + 20;
        int y = getY() + 4;

        int maxWidth = width - x - 2;

        int textWidth = font.width(text);

        float scale = 1.0F;

        if(textWidth > maxWidth) {
            scale = (float) maxWidth / (float) textWidth;
            y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
        }

        graphics.pose().pushPose();

        graphics.pose().translate(x, y, 0);

        graphics.pose().scale(scale, scale, 1.0F);

        graphics.drawString(font, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY, false);

        graphics.pose().popPose();

        y = getY() + 15;

        if(detection.showBearing()) {

            double deltaX = tile.getBlockPos().getX() - detection.getPosition().x;
            double deltaZ = tile.getBlockPos().getZ() - detection.getPosition().z;

            double mag = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

            deltaX = deltaX / mag;

            deltaZ = deltaZ / mag;

            double angleRads = Math.atan2(deltaZ, deltaX);

            double theta = (angleRads / Math.PI * 180.0) + (angleRads > 0 ? 0.0 : 360.0);

            int thetaMin = (int) (Math.floor(theta) - 1);
            int thetaMax = (int) (Math.floor(theta) + 1);

            text = BallistixTextUtils.gui("radar.bearing", Component.literal("" + thetaMin % 360).withStyle(ChatFormatting.WHITE), Component.literal("" + thetaMax % 360).withStyle(ChatFormatting.WHITE)).withStyle(ChatFormatting.BLACK);

            scale = 1.0F;

            textWidth = font.width(text);

            if(textWidth > maxWidth) {
                scale = (float) maxWidth / (float) textWidth;
                y += (int) ((font.lineHeight - font.lineHeight * scale) / 2.0F);
            }

            graphics.pose().pushPose();

            graphics.pose().translate(x, y, 0);

            graphics.pose().scale(scale, scale, 1.0F);

            graphics.drawString(font, text, 0, 0, ScreenComponentCustomRender.TEXT_GRAY, false);

            graphics.pose().popPose();


        } else {
            graphics.drawString(font, BallistixTextUtils.gui("radar.bearingunknown"), x, y, 0xFF000000, false);
        }

    }

    public void setDetection(IDetected.Detected detection) {
        this.detection = detection;
    }

    @Override
    protected void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput narration) {
        // No narration needed
    }
}
