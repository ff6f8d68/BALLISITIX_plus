package ballistix.bplus.inventory.container;

import ballistix.registers.BallistixMenuTypes;
import voltaic.prefab.inventory.container.GenericContainerBlockEntity;
import voltaic.prefab.tile.GenericTile;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class ContainerRangeDesignator extends GenericContainerBlockEntity<GenericTile> {

    public ContainerRangeDesignator(int id, Inventory playerinv) {
        this(id, playerinv, new SimpleContainer(0), new SimpleContainerData(10));
    }

    public ContainerRangeDesignator(int id, Inventory playerinv, Container inventory, ContainerData inventorydata) {
        super(BallistixMenuTypes.CONTAINER_RANGEDESIGNATOR.get(), id, playerinv, inventory, inventorydata);
    }

    @Override
    public void addInventorySlots(Container inv, Inventory playerinv) {
        playerInvOffset = 20;
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
