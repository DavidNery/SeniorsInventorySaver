package me.dery.seniorsinventorysaver.inventories;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MainInventorySaverHolder implements InventoryHolder {

    private Inventory inventory;
    private int currentPage;
    private final int totalPages;

    public MainInventorySaverHolder(int currentPage, int totalPages) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
