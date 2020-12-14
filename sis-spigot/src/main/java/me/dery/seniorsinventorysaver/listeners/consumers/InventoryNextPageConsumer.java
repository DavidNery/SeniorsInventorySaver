package me.dery.seniorsinventorysaver.listeners.consumers;

import me.dery.seniorsinventorysaver.inventories.MainInventorySaverHolder;
import me.dery.seniorsinventorysaver.managers.InventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class InventoryNextPageConsumer implements Consumer<InventoryClickEvent> {

    private final InventoryManager inventoryManager;

    public InventoryNextPageConsumer(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void accept(InventoryClickEvent e) {
        MainInventorySaverHolder holder = (MainInventorySaverHolder) e.getInventory().getHolder();

        if (holder.getCurrentPage() < holder.getTotalPages())
            inventoryManager.openMainInventory((Player) e.getWhoClicked(), holder.getCurrentPage() + 1);
    }
}
