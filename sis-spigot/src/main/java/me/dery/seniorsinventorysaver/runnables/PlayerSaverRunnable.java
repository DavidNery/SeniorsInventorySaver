package me.dery.seniorsinventorysaver.runnables;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerSaverRunnable extends BukkitRunnable {

    private final PlayerManager playerManager;

    public PlayerSaverRunnable(SeniorsInventorySaver instance) {
        this.playerManager = instance.getPlayerManager();
    }

    @Override
    public void run() {
        Bukkit.getServer().getConsoleSender().sendMessage("[SeniorsInventorySaver] Saving data...");
        Bukkit.getOnlinePlayers().forEach(player ->
                playerManager.updatePlayerInStorage(player.getUniqueId(), player.getInventory())
        );
        Bukkit.getServer().getConsoleSender().sendMessage("[SeniorsInventorySaver] Data saved...");
    }

}
