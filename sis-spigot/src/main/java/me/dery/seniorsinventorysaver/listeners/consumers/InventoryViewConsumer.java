package me.dery.seniorsinventorysaver.listeners.consumers;

import me.dery.seniorsinventorysaver.managers.InventoryManager;
import me.dery.seniorsinventorysaver.managers.PlayerManager;
import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Optional;
import java.util.function.Consumer;

public class InventoryViewConsumer implements Consumer<InventoryClickEvent> {

    private final InventoryManager inventoryManager;
    private final PlayerManager playerManager;

    public InventoryViewConsumer(InventoryManager inventoryManager, PlayerManager playerManager) {
        this.inventoryManager = inventoryManager;
        this.playerManager = playerManager;
    }

    @Override
    public void accept(InventoryClickEvent e) {
        HumanEntity player = e.getWhoClicked();

        player.getOpenInventory().close();

        SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
        Player targetPlayer = Bukkit.getPlayer(meta.getOwner());

        if (targetPlayer == null) {
            player.sendMessage("§c§lINVENTORYSAVER §7Player isn't online.");
        } else {
            Optional<InventorySaverPlayer> optional = playerManager.findPlayer(targetPlayer.getUniqueId());
            if (optional.isPresent())
                inventoryManager.openPlayerInventories((Player) player, optional.get());
            else
                player.sendMessage("§c§lINVENTORYSAVER §7Player not found.");
        }
    }
}
