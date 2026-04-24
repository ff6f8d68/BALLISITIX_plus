package ballistix.prefab.screen;

import java.util.function.Consumer;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ScreenComponentCustomRender extends AbstractWidget {
	
	public static final int TEXT_GRAY = 0xFF404040; // ARGB format
	public static final int JEI_TEXT_GRAY = 0xFF808080;

    private final Consumer<GuiGraphics> graphicsConsumer;

    public ScreenComponentCustomRender(int x, int y, Consumer<GuiGraphics> graphicsConsumer) {
        super(x, y, 0, 0, Component.empty());
        this.graphicsConsumer = graphicsConsumer;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if(this.visible){
            graphicsConsumer.accept(graphics);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narration) {
        // No narration for custom render
    }
}
