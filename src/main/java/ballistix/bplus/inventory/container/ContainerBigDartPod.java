package ballistix.bplus.inventory.container;

import ballistix.prefab.BallistixIconTypes;
import ballistix.registers.BallistixMenuTypes;
import electrodynamics.prefab.inventory.container.GenericContainerBlockEntity;
import electrodynamics.prefab.inventory.container.slot.item.SlotGeneric;
import electrodynamics.prefab.screen.component.types.ScreenComponentSlot;
import electrodynamics.prefab.tile.GenericTile;
import electrodynamics.prefab.utilities.math.Color;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class ContainerBigDartPod extends GenericContainerBlockEntity<GenericTile> {

    public ContainerBigDartPod(int id, Inventory playerinv) {
        this(id, playerinv, new SimpleContainer(9), new SimpleContainerData(10));
    }

    public ContainerBigDartPod(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
        super(BallistixMenuTypes.CONTAINER_BIGDARTPOD.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container inv, Inventory playerinv) {
        playerInvOffset = 50;
        
        // 4 Missile Slots
        for (int i = 0; i < 4; i++) {
            addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.MISSILE_DARK, inv, nextIndex(), 40 + (i * 20), 20).setIOColor(new Color(0, 240, 255, 255)));
        }
        
        // 4 Explosive Slots
        for (int i = 0; i < 4; i++) {
            addSlot(new SlotGeneric(ScreenComponentSlot.SlotType.NORMAL, BallistixIconTypes.EXPLOSIVE_DARK, inv, nextIndex(), 40 + (i * 20), 41).setIOColor(new Color(0, 240, 255, 255)));
        }

        // 1 Sync/Tool Slot
        addSlot(new SlotGeneric(inv, nextIndex(), 80, 70));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }
}
