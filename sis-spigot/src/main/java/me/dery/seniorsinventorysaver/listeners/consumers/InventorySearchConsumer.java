package me.dery.seniorsinventorysaver.listeners.consumers;

import me.dery.seniorsinventorysaver.managers.InventoryManager;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.Consumer;

public class InventorySearchConsumer implements Consumer<InventoryClickEvent> {

    private final InventoryManager inventoryManager;

    public InventorySearchConsumer(InventoryManager inventoryManager) {
        this.inventoryManager = inventoryManager;
    }

    @Override
    public void accept(InventoryClickEvent e) {
        HumanEntity player = e.getWhoClicked();

        player.getOpenInventory().close();
        inventoryManager.addSearch(player.getUniqueId());
        player.sendMessage("§e§lINVENTORYSAVER §7Say in chat the player name who you is searching.");
    }
}
