package ballistix.bplus.items;

import ballistix.client.ClientRegister;
import ballistix.registers.BallistixCreativeTabs;
import ballistix.registers.BallistixMissiles;
import ballistix.registers.BallistixPlusBlocks;
import ballistix.registers.BallistixItems;
import voltaic.common.blockitem.types.BlockItemDescriptable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.RegistryObject;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class BallistixPlusItems {
    public static RegistryObject<Item> MISSILE_TEST;
    public static RegistryObject<Item> MISSILE_FAST;
    public static RegistryObject<Item> MISSILE_STEALTH;
    public static RegistryObject<Item> ITEM_RANGEDESIGNATOR;
    public static RegistryObject<Item> ITEM_RANGEDESIGNATOR_BLOCK;
    public static RegistryObject<Item> ITEM_HORIZONTALSILO;
    public static RegistryObject<Item> ITEM_DARTPOD;
    public static RegistryObject<Item> ITEM_DESIGNATOR;
    public static RegistryObject<Item> ITEM_BIGDARTPOD;

    public static void init() {
        if (MISSILE_TEST != null) {
            return;
        }

        MISSILE_TEST = BallistixMissiles.registerMissile(
                "missiletest",
                -1,
                1.0f,
                true,
                ClientRegister.MODEL_MISSILETEST,
                2f, 2f, 2f, // Scale X, Y, Z
                0f, 0f, 0f,      // Translate X, Y, Z
                BallistixCreativeTabs.PLUS,
                "Nuclear Missile"
        );
        MISSILE_FAST = BallistixMissiles.registerMissile(
                "missiledart",
                10000,
                3.0f,
                false,
                ClientRegister.MODEL_MISSILEFAST,
                1f, 1f, 1f, // Scale X, Y, Z
                0f, 0f, 0f,      // Translate X, Y, Z
                BallistixCreativeTabs.PLUS,
                "Dart Missile"
        );
        MISSILE_STEALTH = BallistixMissiles.registerMissile(
                "missilestealth",
                5000,
                1.5f,
                false,
                ClientRegister.MODEL_MISSILESTEALTH,
                1f, 1f, 1f, // Scale X, Y, Z
                0f, 0f, 0f,      // Translate X, Y, Z
                BallistixCreativeTabs.PLUS,
                "Stealth Missile"
        );
        ITEM_RANGEDESIGNATOR = BallistixItems.ITEMS.register("rangedesignator", ItemRangeDesignator::new);

        ITEM_HORIZONTALSILO = BallistixItems.ITEMS.register("horizontalsilo", () -> new BlockItemDescriptable(() -> BallistixPlusBlocks.blockHorizontalMissileSilo.get(), new Item.Properties(), () -> BallistixCreativeTabs.PLUS.get()) {
            @Override
            public Component getName(ItemStack stack) {
                return Component.literal("Horizontal Missile Silo");
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.literal("nuke them, with style!").withStyle(ChatFormatting.DARK_RED));
            }
        });

        ITEM_DARTPOD = BallistixItems.ITEMS.register("dartpod", () -> new BlockItemDescriptable(() -> BallistixPlusBlocks.blockDartPod.get(), new Item.Properties(), () -> BallistixCreativeTabs.PLUS.get()) {
            @Override
            public Component getName(ItemStack stack) {
                return Component.literal("DART Pod");
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.literal("like in the movies").withStyle(ChatFormatting.DARK_RED));
            }
        });

        ITEM_DESIGNATOR = BallistixItems.ITEMS.register("designator", () -> new BlockItemDescriptable(() -> BallistixPlusBlocks.blockDesignator.get(), new Item.Properties(), () -> BallistixCreativeTabs.PLUS.get()) {
            @Override
            public Component getName(ItemStack stack) {
                return Component.literal("Designator Block");
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.literal("commander, send all our missiles").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("Trigger with redstone.").withStyle(ChatFormatting.YELLOW));
            }
        });

        ITEM_BIGDARTPOD = BallistixItems.ITEMS.register("bigdartpod", () -> new BlockItemDescriptable(() -> BallistixPlusBlocks.blockBigDartPod.get(), new Item.Properties(), () -> BallistixCreativeTabs.PLUS.get()) {
            @Override
            public Component getName(ItemStack stack) {
                return Component.literal("Big DART Pod");
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.literal("you can use em to build a battery").withStyle(ChatFormatting.DARK_RED));
                tooltip.add(Component.literal("Fits 4 missiles and 4 explosives.").withStyle(ChatFormatting.GRAY));
            }
        });

        ITEM_RANGEDESIGNATOR_BLOCK = BallistixItems.ITEMS.register("rangedesignatorblock", () -> new BlockItemDescriptable(() -> BallistixPlusBlocks.blockRangeDesignator.get(), new Item.Properties(), () -> BallistixCreativeTabs.PLUS.get()) {
            @Override
            public Component getName(ItemStack stack) {
                return Component.literal("Range Designator Block");
            }

            @Override
            public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                tooltip.add(Component.literal("Casts ray and fires missiles").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("in sequence to target.").withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.literal("One silo per redstone pulse.").withStyle(ChatFormatting.YELLOW));
            }
        });
    }
}
