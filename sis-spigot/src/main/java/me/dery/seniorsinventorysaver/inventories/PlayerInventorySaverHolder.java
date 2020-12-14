package me.dery.seniorsinventorysaver.inventories;

import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class PlayerInventorySaverHolder implements InventoryHolder {

    private Inventory inventory;
    private InventorySaverPlayer player;

    public PlayerInventorySaverHolder(InventorySaverPlayer player) {
        this.player = player;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public InventorySaverPlayer getPlayer() {
        return player;
    }
}
