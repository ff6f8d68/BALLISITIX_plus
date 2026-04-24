package ballistix.client.screen;

import ballistix.bplus.inventory.container.ContainerRangeDesignator;
import ballistix.bplus.tile.TileRangeDesignator;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentFillArea;
import ballistix.prefab.utils.BallistixTextUtils;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenRangeDesignator extends GenericScreen<ContainerRangeDesignator> {

    private final ScreenComponentEditBox frequencyField;

    public ScreenRangeDesignator(ContainerRangeDesignator container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        imageHeight += 20;
        inventoryLabelY += 20;

        // Add frequency input field
        addEditBox(frequencyField = new ScreenComponentEditBox(10, 20, 48, 15, getFontRenderer())
            .setTextColor(-1)
            .setTextColorUneditable(-1)
            .setMaxLength(10)
            .setResponder(this::setFrequency)
            .setFilter(ScreenComponentEditBox.INTEGER));

        // Add labels
        addComponent(new ScreenComponentBallistixLabel(60, 22, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.freq")));
        addComponent(new ScreenComponentBallistixLabel(10, 40, 10, ScreenComponentCustomRender.TEXT_GRAY, Component.literal("Range Designator")));
        addComponent(new ScreenComponentBallistixLabel(10, 52, 10, ScreenComponentCustomRender.TEXT_GRAY, Component.literal("Fires one missile per pulse")));
    }

    @Override
    protected void initializeComponents() {
        addComponent(new ScreenComponentFillArea(8, 18, 160, 50, new Color(120, 120, 120, 255)));
        super.initializeComponents();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile instanceof TileRangeDesignator rangeDesignator) {
            if (!frequencyField.isFocused()) {
                frequencyField.setValue(String.valueOf(rangeDesignator.frequency.get()));
            }
        }
    }

    private void setFrequency(String val) {
        if (val.isEmpty()) return;
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile == null) return;
        try {
            int freq = Integer.parseInt(val);
            TileRangeDesignator rangeDesignator = (TileRangeDesignator) tile;
            rangeDesignator.frequency.set(freq);
            rangeDesignator.frequency.updateServer();
        } catch (Exception e) {
            // Ignore
        }
    }
}
