package ballistix.registers;

import ballistix.References;
import ballistix.bplus.items.BallistixPlusItems;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.prefab.utils.BallistixTextUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixCreativeTabs {

	public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, References.ID);

	public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_TABS.register("main", () -> CreativeModeTab.builder().title(BallistixTextUtils.creativeTab("main")).icon(() -> new ItemStack(BallistixBlocks.getBlock(SubtypeBlast.antimatter))).build());

	public static final RegistryObject<CreativeModeTab> PLUS = CREATIVE_TABS.register("plus", () -> CreativeModeTab.builder().title(Component.literal("Ballistix+")).icon(() -> {
		if (BallistixPlusItems.MISSILE_TEST != null && BallistixPlusItems.MISSILE_TEST.isPresent()) {
			return new ItemStack(BallistixPlusItems.MISSILE_TEST.get());
		}
		return new ItemStack(BallistixBlocks.getBlock(SubtypeBlast.antimatter));
	}).build());

}
