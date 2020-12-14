package me.dery.seniorsinventorysaver.listeners.consumers;

import me.dery.seniorsinventorysaver.inventories.MainInventorySaverHolder;
import me.dery.seniorsinventorysaver.managers.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class InventoryPreviousPageConsumer implements Consumer<InventoryClickEvent> {

    private final InventoryManager inventoryManager;

    public InventoryPreviousPageConsumer(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void accept(InventoryClickEvent e) {
        MainInventorySaverHolder holder = (MainInventorySaverHolder) e.getInventory().getHolder();

        if (holder.getCurrentPage() > 1)
            inventoryManager.openMainInventory((Player) e.getWhoClicked(), holder.getCurrentPage() - 1);
    }
}
