package me.dery.seniorsinventorysaver.listeners;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.managers.InventoryManager;
import me.dery.seniorsinventorysaver.managers.PlayerManager;
import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class ChatListeners implements Listener {

    private final InventoryManager inventoryManager;
    private final PlayerManager playerManager;

    public ChatListeners(SeniorsInventorySaver instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);

        this.inventoryManager = instance.getInventoryManager();
        this.playerManager = instance.getPlayerManager();
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (inventoryManager.isSearching(player.getUniqueId())) {
            e.setCancelled(true);
            if (e.getMessage().equalsIgnoreCase("cancel")) {
                player.sendMessage("§a§lINVENTORYSAVER §7Succesfully cancelled inventory search.");
            } else {
                Player targetPlayer = Bukkit.getPlayer(e.getMessage());
                if (targetPlayer == null) {
                    player.sendMessage("§c§lINVENTORYSAVER §7Player isn't online.");
                } else {
                    Optional<InventorySaverPlayer> optional = playerManager.findPlayer(targetPlayer.getUniqueId());
                    if (optional.isPresent())
                        inventoryManager.openPlayerInventories(player, optional.get());
                    else
                        player.sendMessage("§c§lINVENTORYSAVER §7Player not found.");
                }
            }

            inventoryManager.cancelSearch(player.getUniqueId());
        }
    }
}
