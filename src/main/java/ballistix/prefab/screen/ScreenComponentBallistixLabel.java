package ballistix.prefab.screen;

import java.util.function.Supplier;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.Minecraft;

public class ScreenComponentBallistixLabel extends AbstractWidget {

	private Supplier<Component> text = Component::empty;
	private int color = 0xFFFFFF; // White color in ARGB format

	public ScreenComponentBallistixLabel(int x, int y, int height, int color, Component text) {
		this(x, y, 0, height, () -> text, color);
	}

	public ScreenComponentBallistixLabel(int x, int y, int height, int color, Supplier<Component> text) {
		this(x, y, 0, height, text, color);
	}
	
	public ScreenComponentBallistixLabel(int x, int y, int width, int height, Supplier<Component> text, int color) {
		super(x, y, width, height, text.get());
		this.text = text;
		this.color = color;
	}

	@Override
	public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		if (this.visible) {
			graphics.drawString(Minecraft.getInstance().font, text.get(), getX(), getY(), color, false);
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narration) {
		// No narration needed for label
	}
}
