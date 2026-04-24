package ballistix.datagen.server.recipe.vanilla;

import java.util.function.Consumer;

import ballistix.References;
import ballistix.common.block.subtype.SubtypeBlast;
import ballistix.common.block.subtype.SubtypeMissile;
import ballistix.common.item.ItemGrenade.SubtypeGrenade;
import ballistix.common.item.ItemMinecart.SubtypeMinecart;
import ballistix.common.tags.BallistixTags;
import ballistix.registers.BallistixBlocks;
import ballistix.registers.BallistixItems;
import voltaic.common.block.subtype.SubtypeMachine;
import voltaic.common.block.subtype.SubtypeWire;
import voltaic.common.tags.VoltaicTags;
import voltaic.datagen.utils.recipe.AbstractRecipeGenerator;
import voltaic.datagen.utils.recipe.VoltaicShapedCraftingRecipe;
import voltaic.datagen.utils.recipe.VoltaicShapelessCraftingRecipe;
import voltaic.prefab.item.ItemElectric;
import voltaic.prefab.utilities.object.TransferPack;
import voltaic.registers.VoltaicItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.StrictNBTIngredient;
import net.neoforged.fml.ModList;

public class BallistixCraftingTableRecipes extends AbstractRecipeGenerator {

	@Override
	public void addRecipes(Consumer<FinishedRecipe> consumer) {

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockMissileSilo.get().asItem(), 1)
				//
				.addPattern("P P")
				//
				.addPattern("PCP")
				//
				.addPattern("PLP")
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addKey('L', Items.LEVER)
				//
				.complete(References.ID, "missilesilo", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.getItem(SubtypeMissile.closerange), 1)
				//
				.addPattern(" P ")
				//
				.addPattern("ICI")
				//
				.addPattern("IGI")
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('I', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.complete(References.ID, "missile_closerange", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.getItem(SubtypeMissile.mediumrange), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('M', BallistixItems.getItem(SubtypeMissile.closerange))
				//
				.complete(References.ID, "missile_mediumrange", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.getItem(SubtypeMissile.longrange), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PGP")
				//
				.addPattern("PMP")
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('M', BallistixItems.getItem(SubtypeMissile.mediumrange))
				//
				.complete(References.ID, "missile_longrange", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockRadar.get().asItem(), 1)
				//
				.addPattern("WRW")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('W', VoltaicItems.getItem(SubtypeWire.gold))
				//
				.addKey('R', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('M', VoltaicItems.ITEM_MOTOR.get())
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "radar", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockFireControlRadar.get().asItem(), 1)
				//
				.addPattern(" G ")
				//
				.addPattern("CRC")
				//
				.addPattern("PMP")
				//
				.addKey('G', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('R', BallistixBlocks.blockRadar.get().asItem())
				//
				.addKey('M', VoltaicItems.ITEM_MOTOR.get())
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.complete(References.ID, "fire_control_radar", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockEsmTower.get().asItem(), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("WRW")
				//
				.addPattern("PCP")
				//
				.addKey('A', VoltaicTags.Items.PLATE_ALUMINUM)
				//
				.addKey('R', BallistixBlocks.blockRadar.get().asItem())
				//
				.addKey('W', VoltaicItems.getItem(SubtypeWire.gold))
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.complete(References.ID, "esm_tower", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockSamTurret.get().asItem(), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("PMP")
				//
				.addPattern("PCP")
				//
				.addKey('S', BallistixBlocks.blockMissileSilo.get().asItem())
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('M', VoltaicItems.ITEM_MOTOR.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "turret_sam", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockCiwsTurret.get().asItem(), 1)
				//
				.addPattern("PPC")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('C', Tags.Items.CHESTS)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('M', VoltaicItems.ITEM_MOTOR.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "turret_ciws", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockLaserTurret.get().asItem(), 1)
				//
				.addPattern("GDG")
				//
				.addPattern(" M ")
				//
				.addPattern("PCP")
				//
				.addKey('G', Tags.Items.GLASS)
				//
				.addKey('D', Tags.Items.GEMS_DIAMOND)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('M', VoltaicItems.ITEM_MOTOR.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.complete(References.ID, "turret_laser", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.blockRailgunTurret.get().asItem(), 1)
				//
				.addPattern("OOH")
				//
				.addPattern(" MT")
				//
				.addPattern("PCP")
				//
				.addKey('O', VoltaicItems.ITEM_COIL.get())
				//
				.addKey('H', Tags.Items.CHESTS)
				//
				.addKey('M', VoltaicItems.ITEM_MOTOR.get())
				//
				.addKey('T', VoltaicItems.getItem(SubtypeMachine.upgradetransformer))
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.complete(References.ID, "turret_railgun", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_AAMISSILE.get(), 1)
				//
				.addPattern(" P ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.complete(References.ID, "ballistic_rocket", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_BULLET.get(), 4)
				//
				.addPattern(" P ")
				//
				.addPattern("PGP")
				//
				.addPattern("PGP")
				//
				.addKey('P', VoltaicTags.Items.PLATE_BRONZE)
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.complete(References.ID, "bullet", consumer);

		addExplosives(consumer);
		addGear(consumer);

	}

	private void addExplosives(Consumer<FinishedRecipe> consumer) {
		if (ModList.get().isLoaded("nuclearscience")) {
			VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.antimatter).asItem(), 1)
					//
					.addPattern("CCC")
					//
					.addPattern("CNC")
					//
					.addPattern("CCC")
					//
					.addKey('C', BallistixTags.Items.CELL_ANTIMATTER_LARGE)
					//
					.addKey('N', BallistixBlocks.getBlock(SubtypeBlast.nuclear).asItem())
					//
					.complete(References.ID, "explosive_antimatter", consumer);

			VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.largeantimatter).asItem(), 1)
					//
					.addPattern(" C ")
					//
					.addPattern("CAC")
					//
					.addPattern(" C ")
					//
					.addKey('C', BallistixTags.Items.CELL_ANTIMATTER_VERY_LARGE)
					//
					.addKey('A', BallistixBlocks.getBlock(SubtypeBlast.antimatter).asItem())
					//
					.complete(References.ID, "explosive_antimatterlarge", consumer);
		}
		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.attractive).asItem(), 1)
				//
				.addPattern("CDC")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem())
				//
				.complete(References.ID, "explosive_attractive", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.breaching).asItem(), 1)
				//
				.addPattern("GCG")
				//
				.addPattern("GCG")
				//
				.addPattern("GCG")
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem())
				//
				.complete(References.ID, "explosive_breaching", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.chemical).asItem(), 1)
				//
				.addPattern("PPP")
				//
				.addPattern("PDP")
				//
				.addPattern("PPP")
				//
				.addKey('P', BallistixTags.Items.DUST_POISON)
				//
				.addKey('D', BallistixBlocks.getBlock(SubtypeBlast.debilitation).asItem())
				//
				.complete(References.ID, "explosive_chemical", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem(), 3)
				//
				.addPattern("TRT")
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(References.ID, "explosive_condensive", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.contagious).asItem(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("CRC")
				//
				.addPattern(" C ")
				//
				.addKey('R', Items.ROTTEN_FLESH)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.chemical).asItem())
				//
				.complete(References.ID, "explosive_contagious", consumer);
		if (ModList.get().isLoaded("nuclearscience")) {
			VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.darkmatter).asItem(), 1)
					//
					.addPattern("DDD")
					//
					.addPattern("DAD")
					//
					.addPattern("DDD")
					//
					.addKey('D', BallistixTags.Items.CELL_DARK_MATTER)
					//
					.addKey('A', BallistixBlocks.getBlock(SubtypeBlast.largeantimatter).asItem())
					//
					.complete(References.ID, "explosive_darkmatter", consumer);
		}
		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.debilitation).asItem(), 1)
				//
				.addPattern("DDD")
				//
				.addPattern("WRW")
				//
				.addPattern("DDD")
				//
				.addKey('D', VoltaicTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.addKey('W', Items.WATER_BUCKET)
				//
				.complete(References.ID, "explosive_debilitation", consumer);

		ItemStack fullBattery = new ItemStack(VoltaicItems.ITEM_BATTERY.get());
		ItemElectric battery = (ItemElectric) fullBattery.getItem();
		battery.receivePower(fullBattery, TransferPack.joulesVoltage(battery.getElectricProperties().capacity, battery.getElectricProperties().receive.getVoltage()), false);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.emp).asItem(), 1)
				//
				.addPattern("DBD")
				//
				.addPattern("BTB")
				//
				.addPattern("DBD")
				//
				.addKey('D', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('B', StrictNBTIngredient.of(fullBattery))
				//
				.addKey('T', Items.TNT)
				//
				.complete(References.ID, "explosive_emp", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.fragmentation).asItem(), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("SIS")
				//
				.addPattern(" S ")
				//
				.addKey('S', BallistixBlocks.getBlock(SubtypeBlast.shrapnel).asItem())
				//
				.addKey('I', BallistixBlocks.getBlock(SubtypeBlast.incendiary).asItem())
				//
				.complete(References.ID, "explosive_fragmentation", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.incendiary).asItem(), 1)
				//
				.addPattern("SSS")
				//
				.addPattern("SRS")
				//
				.addPattern("SLS")
				//
				.addKey('S', VoltaicTags.Items.DUST_SULFUR)
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.addKey('L', Items.LAVA_BUCKET)
				//
				.complete(References.ID, "explosive_incendiary", consumer);
		if (ModList.get().isLoaded("nuclearscience")) {
			VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.nuclear).asItem(), 1)
					//
					.addPattern("CTC")
					//
					.addPattern("TRT")
					//
					.addPattern("CTC")
					//
					.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
					//
					.addKey('T', BallistixBlocks.getBlock(SubtypeBlast.thermobaric).asItem())
					//
					.addKey('R', BallistixTags.Items.FUELROD_URANIUM_HIGH_EN)
					//
					.complete(References.ID, "explosive_nuclear", consumer);
		}
		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.obsidian).asItem(), 1)
				//
				.addPattern("OOO")
				//
				.addPattern("TRT")
				//
				.addPattern("OOO")
				//
				.addKey('O', Tags.Items.OBSIDIAN)
				//
				.addKey('T', Items.TNT)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.complete(References.ID, "explosive_obsidian", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem(), 1)
				//
				.addPattern("CGC")
				//
				.addKey('G', Tags.Items.GUNPOWDER)
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.condensive).asItem())
				//
				.complete(References.ID, "explosive_repulsive", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.shrapnel).asItem(), 1)
				//
				.addPattern("AAA")
				//
				.addPattern("ARA")
				//
				.addPattern("AAA")
				//
				.addKey('A', ItemTags.ARROWS)
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.complete(References.ID, "explosive_shrapnel", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.thermobaric).asItem(), 1)
				//
				.addPattern("CIC")
				//
				.addPattern("BRB")
				//
				.addPattern("CIC")
				//
				.addKey('C', BallistixBlocks.getBlock(SubtypeBlast.chemical).asItem())
				//
				.addKey('I', BallistixBlocks.getBlock(SubtypeBlast.incendiary).asItem())
				//
				.addKey('B', BallistixBlocks.getBlock(SubtypeBlast.breaching).asItem())
				//
				.addKey('R', BallistixBlocks.getBlock(SubtypeBlast.repulsive).asItem())
				//
				.complete(References.ID, "explosive_thermobaric", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixBlocks.getBlock(SubtypeBlast.landmine).asItem(), 1)
				//
				.addPattern("P")
				//
				.addPattern("R")
				//
				.addPattern("F")
				//
				.addKey('P', Items.STONE_PRESSURE_PLATE)
				//
				.addKey('R', Tags.Items.DUSTS_REDSTONE)
				//
				.addKey('F', BallistixBlocks.getBlock(SubtypeBlast.fragmentation).asItem())
				//
				.complete(References.ID, "landmine", consumer);

		for (SubtypeMinecart minecart : SubtypeMinecart.values()) {
			VoltaicShapelessCraftingRecipe.start(BallistixItems.getItem(minecart), 1)
					//
					.addIngredient(Items.MINECART)
					//
					.addIngredient(BallistixBlocks.getBlock(minecart.explosiveType).asItem())
					//
					.complete(References.ID, minecart.tag(), consumer);
		}

		for (SubtypeGrenade grenade : SubtypeGrenade.values()) {

			VoltaicShapelessCraftingRecipe.start(BallistixItems.getItem(grenade), 1)
					//
					.addIngredient(BallistixBlocks.getBlock(grenade.explosiveType).asItem())
					//
					.addIngredient(Tags.Items.GUNPOWDER)
					//
					.addIngredient(Tags.Items.STRING)
					//
					.complete(References.ID, "grenade_" + grenade.name(), consumer);

		}

	}

	private void addGear(Consumer<FinishedRecipe> consumer) {

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_DEFUSER.get(), 1)
				//
				.addPattern("W  ")
				//
				.addPattern(" SB")
				//
				.addPattern("  C")
				//
				.addKey('W', VoltaicItems.getItem(SubtypeWire.copper))
				//
				.addKey('S', Items.SHEARS)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('B', VoltaicItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "defuser", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_LASERDESIGNATOR.get(), 1)
				//
				.addPattern("G  ")
				//
				.addPattern(" C ")
				//
				.addPattern("  B")
				//
				.addKey('G', BallistixItems.ITEM_RADARGUN.get())
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ELITE)
				//
				.addKey('B', VoltaicItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "laserdesignator", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_RADARGUN.get(), 1)
				//
				.addPattern("GCS")
				//
				.addPattern(" BS")
				//
				.addPattern(" AS")
				//
				.addKey('G', Tags.Items.GLASS)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_BASIC)
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.addKey('A', VoltaicItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "radargun", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_ROCKETLAUNCHER.get(), 1)
				//
				.addPattern("  G")
				//
				.addPattern("SSC")
				//
				.addPattern("  B")
				//
				.addKey('G', Tags.Items.GLASS)
				//
				.addKey('S', VoltaicTags.Items.INGOT_STEEL)
				//
				.addKey('C', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.addKey('B', Items.STONE_BUTTON)
				//
				.complete(References.ID, "rocketlauncher", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_SCANNER.get(), 1)
				//
				.addPattern(" S ")
				//
				.addPattern("STS")
				//
				.addPattern(" SB")
				//
				.addKey('S', Tags.Items.GEMS_AMETHYST)
				//
				.addKey('T', BallistixItems.ITEM_TRACKER.get())
				//
				.addKey('B', VoltaicItems.ITEM_BATTERY.get())
				//
				.complete(References.ID, "scanner", consumer);

		VoltaicShapedCraftingRecipe.start(BallistixItems.ITEM_TRACKER.get(), 1)
				//
				.addPattern(" C ")
				//
				.addPattern("PBP")
				//
				.addPattern("PAP")
				//
				.addKey('C', Items.COMPASS)
				//
				.addKey('P', VoltaicTags.Items.PLATE_STEEL)
				//
				.addKey('B', VoltaicItems.ITEM_BATTERY.get())
				//
				.addKey('A', VoltaicTags.Items.CIRCUITS_ADVANCED)
				//
				.complete(References.ID, "tracker", consumer);

	}

}
