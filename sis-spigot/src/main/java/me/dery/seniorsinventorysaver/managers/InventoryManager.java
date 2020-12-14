package me.dery.seniorsinventorysaver.managers;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.builders.itemstack.impl.HeadBuilder;
import me.dery.seniorsinventorysaver.builders.itemstack.impl.SimpleItemStackBuilder;
import me.dery.seniorsinventorysaver.inventories.MainInventorySaverHolder;
import me.dery.seniorsinventorysaver.inventories.PlayerInvInventorySaverHolder;
import me.dery.seniorsinventorysaver.inventories.PlayerInventorySaverHolder;
import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import me.dery.seniorsinventorysaver.wrappers.InventoryWrapper;
import me.dery.seniorsinventorysaver.wrappers.impl.PlayerInventoryWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryManager {

    private final PlayerManager playerManager;

    private final HashSet<UUID> playersSearching;

    public InventoryManager(SeniorsInventorySaver instance) {
        this.playersSearching = new HashSet<>();

        this.playerManager = instance.getPlayerManager();
    }

    public void addSearch(UUID uuid) {
        playersSearching.add(uuid);
    }

    public boolean isSearching(UUID uuid) {
        return playersSearching.contains(uuid);
    }

    public void cancelSearch(UUID uuid) {
        playersSearching.remove(uuid);
    }

    public void openMainInventory(Player player, int page) {
        List<InventorySaverPlayer> players = playerManager.getPlayers();
        int totalPages = (int) Math.ceil(players.size() / 45.0);

        MainInventorySaverHolder holder = new MainInventorySaverHolder(page, totalPages);
        holder.setInventory(buildMainInventory(
                holder, page, totalPages,
                players.subList((page - 1) * 45, Math.min(page * 45, players.size()))
        ));

        player.openInventory(holder.getInventory());
    }

    public void openPlayerInventories(Player to, InventorySaverPlayer player) {
        PlayerInventorySaverHolder holder = new PlayerInventorySaverHolder(player);
        holder.setInventory(buildPlayerInventories(holder, player));

        to.openInventory(holder.getInventory());
    }

    public void openPlayerInventory(Player to, InventorySaverPlayer player, int inventory) {
        PlayerInvInventorySaverHolder holder = new PlayerInvInventorySaverHolder(player);
        holder.setInventory(buildPlayerInventory(
                holder,
                (PlayerInventoryWrapper) new ArrayList<>(player.getInventories()).get(inventory)
        ));

        to.openInventory(holder.getInventory());
    }

    private Inventory buildMainInventory(
            InventoryHolder holder,
            int currentPage, int totalPages,
            Collection<InventorySaverPlayer> players
    ) {
        Inventory inventory = Bukkit.createInventory(
                holder, 54,
                String.format("§7Loaded inventories - %d/%d", currentPage, totalPages)
        );

        ItemStack glassPane = new SimpleItemStackBuilder(Material.STAINED_GLASS_PANE, (byte) 7)
                .withName("").build();
        ItemStack searchSign = new SimpleItemStackBuilder(Material.SIGN)
                .withName("§aSearch a player").build();

        for (int i = 0; i < 9; i++) inventory.setItem(i, glassPane);
        inventory.setItem(4, searchSign);

        if (currentPage > 1) {
            ItemStack previousHead = new HeadBuilder("MHF_ArrowLeft")
                    .withName("§eGo to page " + (currentPage - 1)).build();
            inventory.setItem(0, previousHead);
        }

        if (currentPage < totalPages) {
            ItemStack nextHead = new HeadBuilder("MHF_ArrowRight")
                    .withName("§eGo to page " + (currentPage + 1)).build();
            inventory.setItem(0, nextHead);
        }

        int index = 9;
        for (InventorySaverPlayer player : players) {
            Player bukkitPlayer = Bukkit.getPlayer(player.getIdentifier());
            inventory.setItem(
                    index++,
                    new HeadBuilder(bukkitPlayer.getName()).withName(String.format(
                            "§a%s &f- &a%s saved inventories",
                            bukkitPlayer.getName(), player.getInventories().size()
                    )).withLore("", " &7Clique to see the inventories", "").build()
            );
        }

        return inventory;
    }

    private Inventory buildPlayerInventories(InventoryHolder holder, InventorySaverPlayer player) {
        Inventory inventory = Bukkit.createInventory(
                holder, 54,
                String.format("§7%s's inventories", Bukkit.getPlayer(player.getIdentifier()).getName())
        );

        int slot = 0;
        for (InventoryWrapper wrapper : player.getInventories())
            inventory.setItem(
                    slot++,
                    new SimpleItemStackBuilder(Material.CHEST)
                            .withName(String.format("§eInventory saved on %s", wrapper.getDate()))
                            .withLore("", " §fClick to see", "")
                            .build()
            );

        return inventory;
    }

    private Inventory buildPlayerInventory(InventoryHolder holder, PlayerInventoryWrapper wrapper) {
        Inventory inventory = Bukkit.createInventory(
                holder, 54,
                String.format("§7%s's inventory", Bukkit.getPlayer(wrapper.getOwner()).getName())
        );

        int slot = 0;
        for (ItemStack item : wrapper.getArmorContents())
            inventory.setItem(slot++, item);

        slot = 9;
        for (ItemStack item : wrapper.getInventoryContents())
            inventory.setItem(slot++, item);

        ItemStack glassPane = new SimpleItemStackBuilder(Material.STAINED_GLASS_PANE, (byte) 7)
                .withName("").build();
        ItemStack back = new SimpleItemStackBuilder(Material.STAINED_GLASS_PANE, (byte) 14)
                .withName("§cBack to player inventories").build();

        for (int i = 45; i < 54; i++) inventory.setItem(i, glassPane);
        inventory.setItem(49, back);

        return inventory;
    }

}
