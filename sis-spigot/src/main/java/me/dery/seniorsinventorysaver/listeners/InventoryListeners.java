package me.dery.seniorsinventorysaver.listeners;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.inventories.MainInventorySaverHolder;
import me.dery.seniorsinventorysaver.inventories.PlayerInvInventorySaverHolder;
import me.dery.seniorsinventorysaver.inventories.PlayerInventorySaverHolder;
import me.dery.seniorsinventorysaver.listeners.consumers.InventoryNextPageConsumer;
import me.dery.seniorsinventorysaver.listeners.consumers.InventoryPreviousPageConsumer;
import me.dery.seniorsinventorysaver.listeners.consumers.InventorySearchConsumer;
import me.dery.seniorsinventorysaver.listeners.consumers.InventoryViewConsumer;
import me.dery.seniorsinventorysaver.managers.InventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.function.Consumer;

public class InventoryListeners implements Listener {

    private final InventoryManager inventoryManager;

    private final HashMap<Integer, Consumer<InventoryClickEvent>> registeredSlotAction;

    public InventoryListeners(SeniorsInventorySaver instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);

        this.inventoryManager = instance.getInventoryManager();

        registeredSlotAction = new HashMap<>(3);
        registeredSlotAction.put(-1, new InventoryViewConsumer(inventoryManager, instance.getPlayerManager()));
        registeredSlotAction.put(0, new InventoryPreviousPageConsumer(inventoryManager));
        registeredSlotAction.put(4, new InventorySearchConsumer(inventoryManager));
        registeredSlotAction.put(8, new InventoryNextPageConsumer(inventoryManager));
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) {
            if (e.getInventory().getHolder() instanceof MainInventorySaverHolder) {
                e.setCancelled(true);

                if (e.getSlot() > 8) {
                    registeredSlotAction.get(-1).accept(e);
                } else {
                    Consumer<InventoryClickEvent> consumer = registeredSlotAction.get(e.getSlot());
                    if (consumer != null) consumer.accept(e);
                }
            } else if (e.getInventory().getHolder() instanceof PlayerInvInventorySaverHolder) {
                e.setCancelled(true);

                if (e.getSlot() == 49) {
                    e.getWhoClicked().getOpenInventory().close();
                    inventoryManager.openPlayerInventories(
                            (Player) e.getWhoClicked(),
                            ((PlayerInvInventorySaverHolder) e.getInventory().getHolder()).getPlayer()
                    );
                }
            } else if (e.getInventory().getHolder() instanceof PlayerInventorySaverHolder) {
                e.setCancelled(true);
                inventoryManager.openPlayerInventory(
                        (Player) e.getWhoClicked(),
                        ((PlayerInventorySaverHolder) e.getInventory().getHolder()).getPlayer(),
                        e.getSlot()
                );
            }
        }
    }

}
