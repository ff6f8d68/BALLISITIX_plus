package ballistix.client.screen;

import ballistix.common.inventory.container.ContainerMissileSilo;
import ballistix.common.settings.Constants;
import ballistix.common.tile.TileMissileSilo;
import ballistix.bplus.tile.TileHorizontalMissileSilo;
import ballistix.bplus.tile.TileDartPod;
import ballistix.bplus.tile.TileDesignator;
import ballistix.prefab.screen.ScreenComponentBallistixLabel;
import ballistix.prefab.screen.ScreenComponentCustomRender;
import ballistix.prefab.screen.ScreenComponentFillArea;
import ballistix.prefab.utils.BallistixTextUtils;
import electrodynamics.api.electricity.formatting.ChatFormatter;
import electrodynamics.api.electricity.formatting.DisplayUnit;
import electrodynamics.prefab.properties.Property;
import electrodynamics.prefab.screen.GenericScreen;
import electrodynamics.prefab.screen.component.editbox.ScreenComponentEditBox;
import electrodynamics.prefab.screen.component.types.guitab.ScreenComponentElectricInfo;
import electrodynamics.prefab.screen.component.types.wrapper.InventoryIOWrapper;
import electrodynamics.prefab.screen.component.utils.AbstractScreenComponentInfo;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.tile.components.IComponentType;
import electrodynamics.prefab.tile.components.type.ComponentElectrodynamic;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class ScreenMissileSilo extends GenericScreen<ContainerMissileSilo> {

	private boolean needsUpdate = true;

	private final ScreenComponentEditBox xCoordField;
	private final ScreenComponentEditBox yCoordField;
	private final ScreenComponentEditBox zCoordField;
	private final ScreenComponentEditBox frequencyField;


	public ScreenMissileSilo(ContainerMissileSilo container, Inventory playerInventory, Component title) {
		super(container, playerInventory, title);

		imageHeight += 20;
		inventoryLabelY += 20;

		addComponent(new ScreenComponentElectricInfo(this::getElectricInformation,-AbstractScreenComponentInfo.SIZE + 1, 2).wattage(Constants.MISSILESILO_USAGE * 20));

		addEditBox(xCoordField = new ScreenComponentEditBox(10, 20, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setX).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(yCoordField = new ScreenComponentEditBox(10, 38, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setY).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(zCoordField = new ScreenComponentEditBox(10, 56, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setZ).setFilter(ScreenComponentEditBox.INTEGER));
		addEditBox(frequencyField = new ScreenComponentEditBox(10, 74, 48, 15, getFontRenderer()).setTextColor(-1).setTextColorUneditable(-1).setMaxLength(10).setResponder(this::setFrequency).setFilter(ScreenComponentEditBox.INTEGER));



		addComponent(new ScreenComponentBallistixLabel(110, 24, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.missile")));
		addComponent(new ScreenComponentBallistixLabel(110, 45, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.explosive")));
		addComponent(new ScreenComponentBallistixLabel(60, 22, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.x")));
		addComponent(new ScreenComponentBallistixLabel(60, 40, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.y")));
		addComponent(new ScreenComponentBallistixLabel(60, 58, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.z")));
		addComponent(new ScreenComponentBallistixLabel(60, 76, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.freq")));
		addComponent(new ScreenComponentBallistixLabel(110, 74, 10, ScreenComponentCustomRender.TEXT_GRAY, BallistixTextUtils.gui("missilesilo.sync")));

		new InventoryIOWrapper(this, -AbstractScreenComponentInfo.SIZE + 1, AbstractScreenComponentInfo.SIZE + 2, 75, 102, 8, 92).hideAdditional(show -> {

		});
	}

	@Override
	protected void initializeComponents() {
		addComponent(new ScreenComponentFillArea(88, 18, 80, 71, new Color(120,120, 120, 255)));
		super.initializeComponents();
	}

	private void setSiloTargetX(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		GenericTile silo = menu.getHostFromIntArray();

		if (silo == null) {
			return;
		}

		Property<BlockPos> targetProp = getTargetProperty(silo);
		if (targetProp == null) return;

		int x = targetProp.get().getX();

		try {
			x = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(x, targetProp.get().getY(), targetProp.get().getZ(), targetProp);

	}

	private void setSiloTargetY(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		GenericTile silo = menu.getHostFromIntArray();

		if (silo == null) {
			return;
		}

		Property<BlockPos> targetProp = getTargetProperty(silo);
		if (targetProp == null) return;

		int y = targetProp.get().getY();

		try {
			y = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(targetProp.get().getX(), y, targetProp.get().getZ(), targetProp);

	}

	private void setSiloTargetZ(String coord) {

		if (coord.isEmpty()) {
			return;
		}

		GenericTile silo = menu.getHostFromIntArray();

		if (silo == null) {
			return;
		}

		Property<BlockPos> targetProp = getTargetProperty(silo);
		if (targetProp == null) return;

		int z = targetProp.get().getZ();

		try {
			z = Integer.parseInt(coord);
		} catch (Exception e) {
			// Filler
		}

		updateSiloCoords(targetProp.get().getX(), targetProp.get().getY(), z, targetProp);

	}

	private void updateSiloCoords(int x, int y, int z, Property<BlockPos> targetProp) {

		targetProp.set(new BlockPos(x, y, z));

		targetProp.updateServer();

	}

	private void setSiloFrequency(String val) {

		if (val.isEmpty()) {
			return;
		}

		GenericTile silo = menu.getHostFromIntArray();

		if (silo == null) {
			return;
		}

		Property<Integer> freqProp = getFrequencyProperty(silo);
		if (freqProp == null) return;

		int frequency = 0;

		try {
			frequency = Integer.parseInt(val);
		} catch (Exception e) {
			// Filler
		}

		freqProp.set(frequency);

		freqProp.updateServer();

	}

	private Property<BlockPos> getTargetProperty(GenericTile silo) {
		if (silo instanceof TileMissileSilo s) return s.target;
		if (silo instanceof TileHorizontalMissileSilo h) return h.target;
		if (silo instanceof TileDartPod d) return d.target;
		if (silo instanceof TileDesignator td) return td.target;
		return null;
	}

	private Property<Integer> getFrequencyProperty(GenericTile silo) {
		if (silo instanceof TileMissileSilo s) return s.frequency;
		if (silo instanceof TileHorizontalMissileSilo h) return h.frequency;
		if (silo instanceof TileDartPod d) return d.frequency;
		if (silo instanceof TileDesignator td) return td.frequency;
		return null;
	}

	private void setFrequency(String val) {
		frequencyField.setFocus(true);
		xCoordField.setFocus(false);
		yCoordField.setFocus(false);
		zCoordField.setFocus(false);
		setSiloFrequency(val);
	}

	private void setX(String val) {
		xCoordField.setFocus(true);
		yCoordField.setFocus(false);
		zCoordField.setFocus(false);
		frequencyField.setFocus(false);
		setSiloTargetX(val);
	}

	private void setY(String val) {
		yCoordField.setFocus(true);
		xCoordField.setFocus(false);
		zCoordField.setFocus(false);
		frequencyField.setFocus(false);
		setSiloTargetY(val);
	}

	private void setZ(String val) {
		zCoordField.setFocus(true);
		yCoordField.setFocus(false);
		xCoordField.setFocus(false);
		frequencyField.setFocus(false);
		setSiloTargetZ(val);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		super.render(graphics, mouseX, mouseY, partialTicks);
		if (needsUpdate) {
			needsUpdate = false;
			GenericTile silo = menu.getHostFromIntArray();
			if (silo != null) {
				Property<BlockPos> targetProp = getTargetProperty(silo);
				Property<Integer> freqProp = getFrequencyProperty(silo);
				if (targetProp != null) {
					xCoordField.setValue("" + targetProp.get().getX());
					yCoordField.setValue("" + targetProp.get().getY());
					zCoordField.setValue("" + targetProp.get().getZ());
				}
				if (freqProp != null) {
					frequencyField.setValue("" + freqProp.get());
				}
			}
		}
	}

	private List<? extends FormattedCharSequence> getElectricInformation() {
		ArrayList<FormattedCharSequence> list = new ArrayList<>();

		GenericTile silo = menu.getHostFromIntArray();
		if (silo == null) {
			return list;
		}

		ComponentElectrodynamic el = silo.getComponent(IComponentType.Electrodynamic);
		if (el != null) {
			list.add(BallistixTextUtils.tooltip("missilesilo.charge", ChatFormatter.getChatDisplayShort(el.getJoulesStored(), DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY), ChatFormatter.getChatDisplayShort(Constants.MISSILESILO_USAGE, DisplayUnit.JOULES).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
			list.add(ElectroTextUtils.gui("machine.voltage", ChatFormatter.getChatDisplayShort(el.getVoltage(), DisplayUnit.VOLTAGE).withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY).getVisualOrderText());
		}

		return list;
	}

}
