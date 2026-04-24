package ballistix.client.screen;

import ballistix.bplus.inventory.container.ContainerBigDartPod;
import ballistix.bplus.tile.TileBigDartPod;
import ballistix.common.settings.Constants;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentFillArea;
import ballistix.prefab.utils.BallistixTextUtils;
import voltaic.api.electricity.formatting.ChatFormatter;
import voltaic.api.electricity.formatting.DisplayUnit;
import voltaic.prefab.screen.GenericScreen;
import voltaic.prefab.screen.component.editbox.ScreenComponentEditBox;
import voltaic.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import voltaic.prefab.screen.component.utils.AbstractScreenComponentInfo;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.tile.components.IComponentType;
import voltaic.prefab.tile.components.type.ComponentElectrodynamic;
import voltaic.prefab.utilities.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ScreenBigDartPod extends GenericScreen<ContainerBigDartPod> {

    private final ScreenComponentEditBox xCoordField;
    private final ScreenComponentEditBox yCoordField;
    private final ScreenComponentEditBox zCoordField;
    private final ScreenComponentEditBox frequencyField;

    public ScreenBigDartPod(ContainerBigDartPod container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);

        imageHeight += 50;
        inventoryLabelY += 50;

        // Add power indicator
        addComponent(new ScreenComponentElectricInfo(this::getElectricInformation, -AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.MISSILESILO_USAGE * 20));

        // Add coordinate and frequency input fields
        addEditBox(xCoordField = new ScreenComponentEditBox(10, 20, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
        addEditBox(yCoordField = new ScreenComponentEditBox(10, 38, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));
        addEditBox(zCoordField = new ScreenComponentEditBox(10, 56, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setZ).setFilter(ScreenComponentEditBox.INTEGER));
        addEditBox(frequencyField = new ScreenComponentEditBox(10, 74, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setFrequency).setFilter(ScreenComponentEditBox.INTEGER));

        // Add labels
        addComponent(new ScreenComponentBallistixLabel(60, 22, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
        addComponent(new ScreenComponentBallistixLabel(60, 40, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.y")));
        addComponent(new ScreenComponentBallistixLabel(60, 58, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.z")));
        addComponent(new ScreenComponentBallistixLabel(60, 76, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.freq")));
        addComponent(new ScreenComponentBallistixLabel(110, 10, 10, ScreenComponentCustomRender.TEXT_GRAY, Component.literal("Missiles")));
        addComponent(new ScreenComponentBallistixLabel(110, 31, 10, ScreenComponentCustomRender.TEXT_GRAY, Component.literal("Explosives")));
        addComponent(new ScreenComponentBallistixLabel(110, 74, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.sync")));
    }

    @Override
    protected void initializeComponents() {
        addComponent(new ScreenComponentFillArea(8, 18, 160, 75, new Color(120, 120, 120, 255)));
        super.initializeComponents();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile instanceof TileBigDartPod bigDartPod) {
            BlockPos target = bigDartPod.target.get();
            if (!xCoordField.isFocused()) {
                xCoordField.setValue(String.valueOf(target.getX()));
            }
            if (!yCoordField.isFocused()) {
                yCoordField.setValue(String.valueOf(target.getY()));
            }
            if (!zCoordField.isFocused()) {
                zCoordField.setValue(String.valueOf(target.getZ()));
            }
            if (!frequencyField.isFocused()) {
                frequencyField.setValue(String.valueOf(bigDartPod.frequency.get()));
            }
        }
    }

    private List<? extends FormattedCharSequence> getElectricInformation() {
        ArrayList<FormattedCharSequence> list = new ArrayList<>();

        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile == null) {
            return list;
        }

        ComponentElectrodynamic electro = tile.getComponent(IComponentType.Electrodynamic);
        if (electro != null) {
            list.add(BallistixTextUtils.tooltip("missilesilo.charge", 
                ChatFormatter.getChatDisplayShort(electro.getJoulesStored(), DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY), 
                ChatFormatter.getChatDisplayShort(Constants.MISSILESILO_USAGE, DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY))
                .withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
            list.add(voltaic.prefab.utilities.ElectroTextUtils.gui("machine.voltage", 
                ChatFormatter.getChatDisplayShort(electro.getVoltage(), DisplayUnit.VOLTAGE).withStyle(ChatFormatting.GRAY))
                .withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
        }

        return list;
    }

    private void setX(String val) {
        if (val.isEmpty()) return;
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile == null) return;
        try {
            int x = Integer.parseInt(val);
            TileBigDartPod bigDartPod = (TileBigDartPod) tile;
            BlockPos current = bigDartPod.target.get();
            bigDartPod.target.set(new BlockPos(x, current.getY(), current.getZ()));
            bigDartPod.target.updateServer();
        } catch (Exception e) {
            // Ignore
        }
    }

    private void setY(String val) {
        if (val.isEmpty()) return;
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile == null) return;
        try {
            int y = Integer.parseInt(val);
            TileBigDartPod bigDartPod = (TileBigDartPod) tile;
            BlockPos current = bigDartPod.target.get();
            bigDartPod.target.set(new BlockPos(current.getX(), y, current.getZ()));
            bigDartPod.target.updateServer();
        } catch (Exception e) {
            // Ignore
        }
    }

    private void setZ(String val) {
        if (val.isEmpty()) return;
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile == null) return;
        try {
            int z = Integer.parseInt(val);
            TileBigDartPod bigDartPod = (TileBigDartPod) tile;
            BlockPos current = bigDartPod.target.get();
            bigDartPod.target.set(new BlockPos(current.getX(), current.getY(), z));
            bigDartPod.target.updateServer();
        } catch (Exception e) {
            // Ignore
        }
    }

    private void setFrequency(String val) {
        if (val.isEmpty()) return;
        GenericTile tile = getMenu().getHostFromIntArray();
        if (tile == null) return;
        try {
            int freq = Integer.parseInt(val);
            TileBigDartPod bigDartPod = (TileBigDartPod) tile;
            bigDartPod.frequency.set(freq);
            bigDartPod.frequency.updateServer();
        } catch (Exception e) {
            // Ignore
        }
    }
}
