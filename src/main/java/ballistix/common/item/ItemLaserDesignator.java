package ballistix.common.item;

import java.util.List;
import java.util.function.Supplier;

import ballistix.common.network.SiloRegistry;
import ballistix.common.tile.TileMissileSilo;
import ballistix.bplus.tile.TileHorizontalMissileSilo;
import ballistix.bplus.tile.TileDartPod;
import ballistix.bplus.tile.TileBigDartPod;
import ballistix.bplus.tile.TileDesignator; // Import TileDesignator
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import voltaic.common.tile.TileMultiSubnode;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.properties.Property;
import voltaic.prefab.tile.GenericTile;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemLaserDesignator extends ItemElectric {

	public static final double USAGE = 150.0;

	public static final String FREQUENCY_KEY = "freq";

	public ItemLaserDesignator() {
		this(() -> BallistixCreativeTabs.MAIN.get());
	}

	protected ItemLaserDesignator(Supplier<CreativeModeTab> tab) {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), tab, item -> VoltaicItems.ITEM_BATTERY.get());
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		BlockEntity ent = context.getLevel().getBlockEntity(context.getClickedPos());
		GenericTile silo = null;
		if (ent instanceof TileMissileSilo s) {
			silo = s;
		} else if (ent instanceof TileHorizontalMissileSilo h) {
			silo = h;
		} else if (ent instanceof TileDartPod d) {
			silo = d;
		} else if (ent instanceof TileBigDartPod bd) {
			silo = bd;
		} else if (ent instanceof TileDesignator td) { // Add TileDesignator
			silo = td;
		}
		
		if (ent instanceof TileMultiSubnode node) {
			BlockEntity core = node.getLevel().getBlockEntity(node.parentPos.get());
			if (core instanceof TileMissileSilo c) {
				silo = c;
			} else if (core instanceof TileHorizontalMissileSilo h) {
				silo = h;
			} else if (core instanceof TileDartPod d) {
				silo = d;
			} else if (core instanceof TileBigDartPod bd) {
				silo = bd;
			} else if (core instanceof TileDesignator td) { // Add TileDesignator for multiblock core
				silo = td;
			}
		}
		
		if (silo != null) {
			int frequency = 0;
			if (silo instanceof TileMissileSilo s) {
				frequency = s.frequency.get();
			} else if (silo instanceof TileHorizontalMissileSilo h) {
				frequency = h.frequency.get();
			} else if (silo instanceof TileDartPod d) {
				frequency = d.frequency.get();
			} else if (silo instanceof TileBigDartPod bd) {
				frequency = bd.frequency.get();
			} else if (silo instanceof TileDesignator td) { // Get frequency from TileDesignator
				frequency = td.frequency.get();
			}

			if (context.getLevel().isClientSide) {
				context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.setfrequency", frequency), false);
			} else {
				CompoundTag nbt = stack.getOrCreateTag();
				nbt.putInt(FREQUENCY_KEY, frequency);
			}

		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

		if (worldIn.isClientSide) {
			return super.use(worldIn, playerIn, handIn);
		}

		ItemStack designator = playerIn.getItemInHand(handIn);

		if (getJoulesStored(designator) < USAGE || !designator.getOrCreateTag().contains(FREQUENCY_KEY)) {
			return super.use(worldIn, playerIn, handIn);
		}

		Location trace = MathUtils.getRaytracedBlock(playerIn);

		if (trace == null) {
			return super.use(worldIn, playerIn, handIn);
		}

		BlockEntity tile = trace.getTile(worldIn);

		// fixes bug of blowing self up
		if (tile instanceof TileMissileSilo || tile instanceof TileHorizontalMissileSilo || tile instanceof TileDartPod || tile instanceof TileBigDartPod || tile instanceof TileDesignator || tile instanceof TileMultiSubnode) { // Add TileDesignator here
			return InteractionResultHolder.pass(playerIn.getItemInHand(handIn));
		}

		int frequency = getFrequency(designator);

		BlockPos target = trace.toBlockPos();

		for (GenericTile silo : SiloRegistry.getSilos(frequency, worldIn)) {
			
			int range = 0;
			Property<BlockPos> targetProp = null;
			
			if (silo instanceof TileMissileSilo s) {
				range = s.range.get();
				targetProp = s.target;
			} else if (silo instanceof TileHorizontalMissileSilo h) {
				range = h.range.get();
				targetProp = h.target;
			} else if (silo instanceof TileDartPod d) {
				range = d.range.get();
				targetProp = d.target;
			} else if (silo instanceof TileBigDartPod bd) {
				range = bd.range.get();
				targetProp = bd.target;
			}

			double distance = calculateDistance(silo.getBlockPos(), target);

			if (range == 0 || (range > 0 && range < distance)) {
				continue;
			}

			if (targetProp != null) {
				targetProp.set(target);
			}

			if (silo instanceof TileMissileSilo s) {
				s.shouldLaunch = true;
			} else if (silo instanceof TileHorizontalMissileSilo h) {
				h.shouldLaunch = true;
			} else if (silo instanceof TileDartPod d) {
				d.shouldLaunch = true;
			} else if (silo instanceof TileBigDartPod bd) {
				bd.shouldLaunch = true;
			}

			extractPower(designator, USAGE, false);

		}

		playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launch", frequency), false);
		playerIn.displayClientMessage(BallistixTextUtils.chatMessage("laserdesignator.launchsend", trace), false);

		return super.use(worldIn, playerIn, handIn);
	}

	private double calculateDistance(BlockPos fromPos, BlockPos toPos) {
		double deltaX = fromPos.getX() - toPos.getX();
		double deltaY = fromPos.getY() - toPos.getY();
		double deltaZ = fromPos.getZ() - toPos.getZ();

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);

		if (!worldIn.isClientSide || !isSelected) {
			return;
		}

		Location trace = MathUtils.getRaytracedBlock(entityIn);

		if (trace == null) {
			return;
		}

		if (entityIn instanceof Player player) {
			player.displayClientMessage(BallistixTextUtils.chatMessage("radargun.text", trace.toBlockPos().toShortString()), true);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.hasTag()) {
			CompoundTag nbt = stack.getTag();
			if (nbt.contains(FREQUENCY_KEY)) {
				int freq = getFrequency(stack);
				tooltip.add(BallistixTextUtils.tooltip("laserdesignator.frequency", freq));
			} else {
				tooltip.add(BallistixTextUtils.tooltip("laserdesignator.nofrequency"));
			}
		}
	}

	public static int getFrequency(ItemStack stack) {
		return stack.getOrCreateTag().getInt(FREQUENCY_KEY);
	}

}
