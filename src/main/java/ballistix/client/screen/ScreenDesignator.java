package ballistix.client.screen;

import ballistix.bplus.inventory.container.ContainerDesignator;
import ballistix.bplus.tile.TileDesignator;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentFillArea;
import ballistix.prefab.utils.BallistixTextUtils;
import voltaic.prefab.properties.Property;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ScreenDesignator extends GenericScreen<ContainerDesignator> {

    private boolean needsUpdate = true;

    private final ScreenComponentEditBox xCoordField;
    private final ScreenComponentEditBox yCoordField;
    private final ScreenComponentEditBox zCoordField;
    private final ScreenComponentEditBox frequencyField;

    public ScreenDesignator(ContainerDesignator container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        imageHeight += 20;
        inventoryLabelY += 20;

        addEditBox(xCoordField = new ScreenComponentEditBox(10, 20, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
        addEditBox(yCoordField = new ScreenComponentEditBox(10, 38, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));
        addEditBox(zCoordField = new ScreenComponentEditBox(10, 56, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setZ).setFilter(ScreenComponentEditBox.INTEGER));
        addEditBox(frequencyField = new ScreenComponentEditBox(10, 74, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setFrequency).setFilter(ScreenComponentEditBox.INTEGER));

        addComponent(new ScreenComponentBallistixLabel(60, 22, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
        addComponent(new ScreenComponentBallistixLabel(60, 40, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.y")));
        addComponent(new ScreenComponentBallistixLabel(60, 58, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.z")));
        addComponent(new ScreenComponentBallistixLabel(60, 76, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.freq")));
    }

    @Override
    protected void initializeComponents() {
        addComponent(new ScreenComponentFillArea(8, 18, 160, 75, new Color(120, 120, 120, 255)));
        super.initializeComponents();
    }

    private void setDesignatorTargetX(String coord) {
        if (coord.isEmpty()) return;
        GenericTile host = menu.getHostFromIntArray();
        if (host instanceof TileDesignator designator) {
            int x = designator.target.get().getX();
            try { x = Integer.parseInt(coord); } catch (Exception e) { return; }
            designator.target.set(new BlockPos(x, designator.target.get().getY(), designator.target.get().getZ()));
            designator.target.updateServer();
        }
    }

    private void setDesignatorTargetY(String coord) {
        if (coord.isEmpty()) return;
        GenericTile host = menu.getHostFromIntArray();
        if (host instanceof TileDesignator designator) {
            int y = designator.target.get().getY();
            try { y = Integer.parseInt(coord); } catch (Exception e) { return; }
            designator.target.set(new BlockPos(designator.target.get().getX(), y, designator.target.get().getZ()));
            designator.target.updateServer();
        }
    }

    private void setDesignatorTargetZ(String coord) {
        if (coord.isEmpty()) return;
        GenericTile host = menu.getHostFromIntArray();
        if (host instanceof TileDesignator designator) {
            int z = designator.target.get().getZ();
            try { z = Integer.parseInt(coord); } catch (Exception e) { return; }
            designator.target.set(new BlockPos(designator.target.get().getX(), designator.target.get().getY(), z));
            designator.target.updateServer();
        }
    }

    private void setDesignatorFrequency(String val) {
        if (val.isEmpty()) return;
        GenericTile host = menu.getHostFromIntArray();
        if (host instanceof TileDesignator designator) {
            int frequency = 0;
            try { frequency = Integer.parseInt(val); } catch (Exception e) { return; }
            designator.frequency.set(frequency);
            designator.frequency.updateServer();
        }
    }

    private void setFrequency(String val) {
        frequencyField.setFocus(true);
        xCoordField.setFocus(false);
        yCoordField.setFocus(false);
        zCoordField.setFocus(false);
        setDesignatorFrequency(val);
    }

    private void setX(String val) {
        xCoordField.setFocus(true);
        yCoordField.setFocus(false);
        zCoordField.setFocus(false);
        frequencyField.setFocus(false);
        setDesignatorTargetX(val);
    }

    private void setY(String val) {
        yCoordField.setFocus(true);
        xCoordField.setFocus(false);
        zCoordField.setFocus(false);
        frequencyField.setFocus(false);
        setDesignatorTargetY(val);
    }

    private void setZ(String val) {
        zCoordField.setFocus(true);
        yCoordField.setFocus(false);
        xCoordField.setFocus(false);
        frequencyField.setFocus(false);
        setDesignatorTargetZ(val);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (needsUpdate) {
            needsUpdate = false;
            GenericTile host = menu.getHostFromIntArray();
            if (host instanceof TileDesignator designator) {
                xCoordField.setValue("" + designator.target.get().getX());
                yCoordField.setValue("" + designator.target.get().getY());
                zCoordField.setValue("" + designator.target.get().getZ());
                frequencyField.setValue("" + designator.frequency.get());
            }
        }
    }
}
