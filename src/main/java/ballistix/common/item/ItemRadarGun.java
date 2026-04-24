package ballistix.common.item;

import java.util.List;

import ballistix.common.tile.TileMissileSilo;
import ballistix.bplus.tile.TileHorizontalMissileSilo;
import ballistix.bplus.tile.TileDartPod;
import ballistix.bplus.tile.TileDesignator;
import ballistix.common.tile.turret.antimissile.util.TileTurretAntimissile;
import ballistix.prefab.utils.BallistixTextUtils;
import ballistix.registers.BallistixCreativeTabs;
import voltaic.common.tile.TileMultiSubnode;
import voltaic.prefab.item.ElectricItemProperties;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.NBTUtils;
import voltaic.prefab.utilities.math.MathUtils;
import voltaic.prefab.utilities.object.Location;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ItemRadarGun extends ItemElectric {

	public static final double USAGE = 150.0;

	public ItemRadarGun() {
		super((ElectricItemProperties) new ElectricItemProperties().capacity(1666666.66667).receive(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).extract(TransferPack.joulesVoltage(1666666.66667 / (120.0 * 20.0), 120)).stacksTo(1), () -> BallistixCreativeTabs.MAIN.get(), item -> VoltaicItems.ITEM_BATTERY.get());
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		if (context.getLevel().isClientSide) {
			return super.onItemUseFirst(stack, context);
		}
		BlockEntity tile = context.getLevel().getBlockEntity(context.getClickedPos());

		if(tile instanceof TileMissileSilo silo) {
			silo.target.set(getCoordiantes(stack));
		} else if (tile instanceof TileHorizontalMissileSilo h) {
			h.target.set(getCoordiantes(stack));
		} else if (tile instanceof TileDartPod d) {
			d.target.set(getCoordiantes(stack));
		} else if (tile instanceof TileDesignator td) {
			td.target.set(getCoordiantes(stack));
		} else if (tile instanceof TileMultiSubnode subnode) {
			BlockEntity parent = subnode.getLevel().getBlockEntity(subnode.parentPos.get());
			if (parent instanceof TileMissileSilo silo) {
				silo.target.set(getCoordiantes(stack));
			} else if (parent instanceof TileHorizontalMissileSilo h) {
				h.target.set(getCoordiantes(stack));
			} else if (parent instanceof TileDartPod d) {
				d.target.set(getCoordiantes(stack));
			} else if (parent instanceof TileDesignator td) {
				td.target.set(getCoordiantes(stack));
			}
		} else if (tile instanceof TileTurretAntimissile turret) {
			if(turret.bindFireControlRadar(getCoordiantes(stack))) {
				context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turretsucess"), true);
			} else {
				context.getPlayer().displayClientMessage(BallistixTextUtils.chatMessage("radargun.turrettoofar"), true);
			}
		}
		return super.onItemUseFirst(stack, context);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {

		if (worldIn.isClientSide) {
			return super.use(worldIn, playerIn, handIn);
		}

		Location trace = MathUtils.getRaytracedBlock(playerIn);

		if (trace == null) {
			return super.use(worldIn, playerIn, handIn);
		}

		ItemStack radarGun = playerIn.getItemInHand(handIn);

		if (getJoulesStored(radarGun) < USAGE) {
			return super.use(worldIn, playerIn, handIn);
		}
		
		BlockEntity tile = trace.getTile(playerIn.level());
		if(tile instanceof TileMissileSilo || tile instanceof TileHorizontalMissileSilo || tile instanceof TileDartPod || tile instanceof TileDesignator || tile instanceof TileMultiSubnode || tile instanceof TileTurretAntimissile) {
			return super.use(worldIn, playerIn, handIn);
		}

		storeCoordiantes(radarGun, trace.toBlockPos());

		extractPower(radarGun, USAGE, false);

		return super.use(worldIn, playerIn, handIn);
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
		if (stack.hasTag() && stack.getTag().contains(NBTUtils.LOCATION)) {
			tooltip.add(BallistixTextUtils.tooltip("radargun.pos", getCoordiantes(stack).toShortString()));
		} else {
			tooltip.add(BallistixTextUtils.tooltip("radargun.notag"));
		}
	}

	public static void storeCoordiantes(ItemStack stack, BlockPos pos) {
		stack.getOrCreateTag().put(NBTUtils.LOCATION, NbtUtils.writeBlockPos(pos));
	}

	public static BlockPos getCoordiantes(ItemStack stack) {
		return NbtUtils.readBlockPos(stack.getOrCreateTag().getCompound(NBTUtils.LOCATION));
	}

}
