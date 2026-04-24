package ballistix.registers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ballistix.References;
import ballistix.bplus.items.BallistixPlusItems;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemDefuser;
import ballistix.common.item.ItemGrenade;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemLaserDesignator;
import ballistix.common.item.ItemMinecart;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.common.item.ItemRadarGun;
import ballistix.common.item.ItemRocketLauncher;
import ballistix.common.item.ItemScanner;
import ballistix.common.item.ItemTracker;
import electrodynamics.api.ISubtype;
import electrodynamics.api.creativetab.CreativeTabSupplier;
import electrodynamics.common.blockitem.types.BlockItemDescriptable;
import electrodynamics.common.item.ItemElectrodynamics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEMS, References.ID);

	public static final HashMap<ISubtype, RegistryObject<Item>> SUBTYPEITEMREGISTER_MAPPINGS = new HashMap<>();

	public static final RegistryObject<Item> ITEM_AAMISSILE = ITEMS.register("aamissile", () -> new ItemElectrodynamics(new Item.Properties().stacksTo(10), BallistixCreativeTabs.MAIN));
	public static final RegistryObject<Item> ITEM_BULLET = ITEMS.register("bullet", () -> new ItemElectrodynamics(new Item.Properties().stacksTo(64), BallistixCreativeTabs.MAIN));
	public static final RegistryObject<Item> ITEM_DUSTPOISON = ITEMS.register("dustpoison", () -> new ItemElectrodynamics(new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_ROCKETLAUNCHER = ITEMS.register("rocketlauncher", ItemRocketLauncher::new);
	public static final RegistryObject<Item> ITEM_RADARGUN = ITEMS.register("radargun", ItemRadarGun::new);
	public static final RegistryObject<Item> ITEM_TRACKER = ITEMS.register("tracker", ItemTracker::new);
	public static final RegistryObject<Item> ITEM_SCANNER = ITEMS.register("scanner", ItemScanner::new);
	public static final RegistryObject<Item> ITEM_LASERDESIGNATOR = ITEMS.register("laserdesignator", ItemLaserDesignator::new);
	public static final RegistryObject<Item> ITEM_DEFUSER = ITEMS.register("defuser", ItemDefuser::new);

	public static final RegistryObject<Item> ITEM_MISSILESILO = ITEMS.register("missilesilo", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockMissileSilo.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_RADAR = ITEMS.register("radar", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockRadar.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_FIRECONTROLRADAR = ITEMS.register("firecontrolradar", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockFireControlRadar.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_ESMTOWER = ITEMS.register("esmtower", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockEsmTower.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_SAMTURRET = ITEMS.register("samturret", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockSamTurret.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_CIWSTURRET = ITEMS.register("ciwsturret", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockCiwsTurret.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_LASERTURRET = ITEMS.register("laserturret", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockLaserTurret.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
	public static final RegistryObject<Item> ITEM_RAILGUNTURRET = ITEMS.register("railgunturret", () -> new BlockItemDescriptable(() -> BallistixBlocks.blockRailgunTurret.get(), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));

	public static void init() {
		for (SubtypeMissile subtype : SubtypeMissile.values()) {
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, BallistixMissiles.registerMissile(subtype.tag(), subtype.range, subtype.speedModifier, subtype.radarVisible, subtype.model(), 0.5f, 6f, 0.5f, 0, 0, 0, BallistixCreativeTabs.MAIN, null));
		}

		BallistixPlusItems.init();

		for (SubtypeBlast subtype : SubtypeBlast.values()) {
			ITEMS.register(subtype.tag(), () -> new BlockItemDescriptable(() -> BallistixBlocks.getBlock(subtype), new Item.Properties(), () -> BallistixCreativeTabs.MAIN.get()));
		}
		for (SubtypeGrenade subtype : SubtypeGrenade.values()) {
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, ITEMS.register(subtype.tag(), () -> new ItemGrenade(subtype)));
		}
		for (SubtypeMinecart subtype : SubtypeMinecart.values()) {
			SUBTYPEITEMREGISTER_MAPPINGS.put(subtype, ITEMS.register(subtype.tag(), () -> new ItemMinecart(subtype)));
		}
	}

	public static Item[] getAllItemForSubtype(ISubtype[] values) {
		List<Item> list = new ArrayList<>();
		for (ISubtype value : values) {
			list.add(SUBTYPEITEMREGISTER_MAPPINGS.get(value).get());
		}
		return list.toArray(new Item[] {});
	}

	public static Item getItem(ISubtype value) {
		return SUBTYPEITEMREGISTER_MAPPINGS.get(value).get();
	}

	@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = References.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	private static class ElectroCreativeRegistry {

		@SubscribeEvent
		public static void registerItems(BuildCreativeModeTabContentsEvent event) {

			ITEMS.getEntries().forEach(reg -> {

				CreativeTabSupplier supplier = (CreativeTabSupplier) reg.get();

				if (supplier.hasCreativeTab() && supplier.isAllowedInCreativeTab(event.getTab())) {
					List<ItemStack> toAdd = new ArrayList<>();
					supplier.addCreativeModeItems(event.getTab(), toAdd);
					event.acceptAll(toAdd);
				}

			});

		}

	}

}
