package ballistix.registers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import ballistix.common.item.ItemMissile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegistryObject;

public class BallistixMissiles {

	public static final Map<Item, MissileData> MISSILE_MAPPING = new HashMap<>();
	public static final Map<String, MissileData> PENDING_REGISTRATIONS = new LinkedHashMap<>();
	public static final List<MissileData> ALL_MISSILES = new ArrayList<>();

	public static RegistryObject<Item> registerMissile(String name, int range, ResourceLocation model) {
		return registerMissile(name, range, 1.0f, model, 0.5f, 6f, 0.5f, 0, 0, 0);
	}

	public static RegistryObject<Item> registerMissile(String name, int range, float speedModifier, ResourceLocation model, float scaleX, float scaleY, float scaleZ, float translateX, float translateY, float translateZ) {
		return registerMissile(name, range, speedModifier, model, scaleX, scaleY, scaleZ, translateX, translateY, translateZ, BallistixCreativeTabs.MAIN);
	}

	public static RegistryObject<Item> registerMissile(String name, int range, float speedModifier, ResourceLocation model, float scaleX, float scaleY, float scaleZ, float translateX, float translateY, float translateZ, Supplier<CreativeModeTab> tab) {
		return registerMissile(name, range, speedModifier, model, scaleX, scaleY, scaleZ, translateX, translateY, translateZ, tab, null);
	}

	public static RegistryObject<Item> registerMissile(String name, int range, float speedModifier, ResourceLocation model, float scaleX, float scaleY, float scaleZ, float translateX, float translateY, float translateZ, Supplier<CreativeModeTab> tab, String customName) {
		return registerMissile(name, range, speedModifier, true, model, scaleX, scaleY, scaleZ, translateX, translateY, translateZ, tab, customName);
	}

	public static RegistryObject<Item> registerMissile(String name, int range, float speedModifier, boolean radarVisible, ResourceLocation model, float scaleX, float scaleY, float scaleZ, float translateX, float translateY, float translateZ, Supplier<CreativeModeTab> tab, String customName) {
		int id = ALL_MISSILES.size();
		RegistryObject<Item> item = BallistixItems.ITEMS.register(name, () -> {
			ItemMissile im = new ItemMissile(id, range, speedModifier, radarVisible, tab);
			if (customName != null) {
				im.setCustomName(customName);
			}
			return im;
		});
		MissileData data = new MissileData(id, range, speedModifier, radarVisible, model, item, scaleX, scaleY, scaleZ, translateX, translateY, translateZ);
		PENDING_REGISTRATIONS.put(name, data);
		ALL_MISSILES.add(data);
		System.out.println("Registering missile: " + name + " with ID: " + id);
		return item;
	}

	public static void registerMissile(Supplier<Item> item, int range, ResourceLocation model) {
		int id = ALL_MISSILES.size();
		MissileData data = new MissileData(id, range, 1.0f, true, model, null, 0.5f, 6f, 0.5f, 0, 0, 0);
		ALL_MISSILES.add(data);
		
		// If the item is already registered (static block), map it immediately
		try {
			Item it = item.get();
			if (it != null) {
				MISSILE_MAPPING.put(it, data);
			}
		} catch (Exception e) {}
	}

	public static class MissileData {
		public final int id;
		public final int range;
		public final float speedModifier;
		public final boolean radarVisible;
		public final ResourceLocation model;
		public final RegistryObject<Item> reg;
		public final float scaleX, scaleY, scaleZ;
		public final float translateX, translateY, translateZ;

		public MissileData(int id, int range, float speedModifier, boolean radarVisible, ResourceLocation model, RegistryObject<Item> reg, float scaleX, float scaleY, float scaleZ, float translateX, float translateY, float translateZ) {
			this.id = id;
			this.range = range;
			this.speedModifier = speedModifier;
			this.radarVisible = radarVisible;
			this.model = model;
			this.reg = reg;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.scaleZ = scaleZ;
			this.translateX = translateX;
			this.translateY = translateY;
			this.translateZ = translateZ;
		}

		@Override
		public String toString() {
			return "MissileData[id=" + id + ", model=" + model + "]";
		}
	}

	public static void init() {
		for (MissileData data : PENDING_REGISTRATIONS.values()) {
			Item item = data.reg.get();
			MISSILE_MAPPING.put(item, data);
		}
	}

	public static MissileData getData(Item item) {
		MissileData data = MISSILE_MAPPING.get(item);
		if (data == null && item instanceof ItemMissile im) {
			return getData(im.id);
		}
		return data;
	}

	public static MissileData getData(int id) {
		for (MissileData data : ALL_MISSILES) {
			if (data.id == id) {
				return data;
			}
		}
		return null;
	}
}
