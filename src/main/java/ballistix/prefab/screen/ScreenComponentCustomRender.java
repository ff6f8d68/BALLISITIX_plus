package ballistix.prefab.screen;

import java.util.function.Consumer;

import electrodynamics.prefab.screen.component.AbstractScreenComponent;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;

public class ScreenComponentCustomRender extends AbstractScreenComponent {
	
	public static final Color TEXT_GRAY = new Color(64, 64, 64, 255);
	public static final Color JEI_TEXT_GRAY = new Color(128, 128, 128, 255);

    private final Consumer<GuiGraphics> graphicsConsumer;

    public ScreenComponentCustomRender(int x, int y, Consumer<GuiGraphics> graphicsConsumer) {
        super(x, y, 0, 0);
        this.graphicsConsumer = graphicsConsumer;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if(!isVisible()){
            return;
        }
        graphicsConsumer.accept(graphics);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    @Override
    public void setFocused(boolean pFocused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }
}
