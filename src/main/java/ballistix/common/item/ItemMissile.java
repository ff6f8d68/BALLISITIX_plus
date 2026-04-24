package ballistix.common.item;

import java.util.List;
import java.util.function.Supplier;

import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import electrodynamics.common.item.ItemElectrodynamics;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ItemMissile extends ItemElectrodynamics {

	public final int id;
	public final int range;
	public final float speedModifier;
	public final boolean radarVisible;
	private String customName = null;

	public ItemMissile(int range) {
		this(-1, range, 1.0f, true);
	}

	public ItemMissile(int id, int range, float speedModifier, boolean radarVisible) {
		this(id, range, speedModifier, radarVisible, BallistixCreativeTabs.MAIN);
	}

	public ItemMissile(int id, int range, float speedModifier, boolean radarVisible, Supplier<CreativeModeTab> tab) {
		super(new Properties().stacksTo(1), tab);
		this.id = id;
		this.range = range;
		this.speedModifier = speedModifier;
		this.radarVisible = radarVisible;
	}

	public ItemMissile setCustomName(String name) {
		this.customName = name;
		return this;
	}

	@Override
	public Component getName(ItemStack stack) {
		if (customName != null) {
			return Component.literal(customName);
		}
		return super.getName(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		super.appendHoverText(stack, world, tooltipComponents, tooltipFlag);

		Component rangeText = Component.literal(range + "");

		if (range < 0) {
			rangeText = BallistixTextUtils.tooltip("missile.unlimited");
		}

		tooltipComponents.add(BallistixTextUtils.tooltip("missile.range", rangeText).withStyle(ChatFormatting.GRAY));
		tooltipComponents.add(Component.literal("Speed: ").withStyle(ChatFormatting.GRAY).append(Component.literal(speedModifier + "x").withStyle(ChatFormatting.WHITE)));
		
		if (!radarVisible) {
			tooltipComponents.add(Component.literal("Stealth").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
		}
	}

}
