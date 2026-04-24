package ballistix;

import ballistix.api.capability.BallistixCapabilities;
import ballistix.client.ClientRegister;
import ballistix.common.blast.thread.ThreadSimpleBlast;
import ballistix.common.block.BallistixVoxelShapesRegistry;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.packet.NetworkHandler;
import ballistix.common.settings.Constants;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.UnifiedBallistixRegister;
import voltaic.prefab.configuration.ConfigurationHandler;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.ModContainer;

@Mod(References.ID)
@EventBusSubscriber(modid = References.ID, bus = Bus.MOD)
public class Ballistix {

	public Ballistix(IEventBus modEventBus, ModContainer modContainer) {
		ConfigurationHandler.registerConfig(Constants.class);
		UnifiedBallistixRegister.register(modEventBus);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ClientRegister.setup();
		});
	}

	@SubscribeEvent
	public static void onCommonSetup(FMLCommonSetupEvent event) {
		NetworkHandler.init();
		BallistixTags.init();
		BallistixVoxelShapesRegistry.init();
		event.enqueueWork(() -> {
			BallistixMissiles.init();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_ANTIMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.antimatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_DARKMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.darkmatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_LARGEANTIMATTER_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.largeantimatter.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_NUCLEAR_SIZE * 2, Integer.MAX_VALUE, null, SubtypeBlast.nuclear.ordinal()).start();
			new ThreadSimpleBlast(null, BlockPos.ZERO, (int) Constants.EXPLOSIVE_EMP_RADIUS, Integer.MAX_VALUE, null, SubtypeBlast.emp.ordinal());
		});
	}

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		BallistixCapabilities.register(event);
	}

}
