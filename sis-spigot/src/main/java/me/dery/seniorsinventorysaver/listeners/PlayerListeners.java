package me.dery.seniorsinventorysaver.listeners;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.managers.PlayerManager;
import me.dery.seniorsinventorysaver.scheduler.ConcurrentTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    private final PlayerManager playerManager;

    public PlayerListeners(SeniorsInventorySaver instance) {
        instance.getServer().getPluginManager().registerEvents(this, instance);

        this.playerManager = instance.getPlayerManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        ConcurrentTask.runAsync(() -> playerManager.loadPlayer(e.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        ConcurrentTask.runAsync(() ->
                playerManager.unloadPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getInventory())
        );
    }
}
