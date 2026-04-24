package ballistix.registers;

import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.prefab.utilities.ElectroTextUtils;
import net.neoforged.bus.api.IEventBus;

public class UnifiedBallistixRegister {

	public static void register(IEventBus bus) {
		BallistixBlocks.BLOCKS.register(bus);
		BallistixPlusBlocks.BLOCKS.register(bus);
		BallistixItems.init();
		BallistixItems.ITEMS.register(bus);
		BallistixBlockTypes.BLOCK_ENTITY_TYPES.register(bus);
		BallistixMenuTypes.MENU_TYPES.register(bus);
		BallistixEntities.ENTITIES.register(bus);
		BallistixSounds.SOUNDS.register(bus);
		BallistixCreativeTabs.CREATIVE_TABS.register(bus);
	}
	
	static {
		
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockMissileSilo.get(), ElectroTextUtils.voltageTooltip(120));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockRadar.get(), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockFireControlRadar.get(), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockEsmTower.get(), ElectroTextUtils.voltageTooltip(480));

		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockSamTurret.get(), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockCiwsTurret.get(), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockLaserTurret.get(), ElectroTextUtils.voltageTooltip(120));
		BlockItemDescriptable.addDescription(() -> BallistixBlocks.blockRailgunTurret.get(), ElectroTextUtils.voltageTooltip(120));
		
		BlockItemDescriptable.addDescription(() -> BallistixPlusBlocks.blockHorizontalMissileSilo.get(), ElectroTextUtils.voltageTooltip(120));
	}

}
