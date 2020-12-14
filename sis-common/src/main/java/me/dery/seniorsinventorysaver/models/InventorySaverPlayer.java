package me.dery.seniorsinventorysaver.models;

import me.dery.seniorsinventorysaver.wrappers.InventoryWrapper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Class that represents the player and his saved inventories
 */
public class InventorySaverPlayer {

    private final UUID identifier;
    private long lastSave;
    private final HashMap<String, InventoryWrapper> inventories;

    public InventorySaverPlayer(UUID identifier, long lastSave) {
        this.identifier = identifier;
        this.lastSave = lastSave;
        this.inventories = new HashMap<>();
    }

    public InventorySaverPlayer(UUID identifier) {
        this(identifier, -1);
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setLastSave(long lastSave) {
        this.lastSave = lastSave;
    }

    public long getLastSave() {
        return lastSave;
    }

    public <W extends InventoryWrapper> void saveInventory(String day, W wrapper) {
        inventories.put(day, wrapper);
    }

    public <W extends InventoryWrapper> void saveInventories(HashMap<String, W> inventories) {
        this.inventories.putAll(inventories);
    }

    public Optional<? extends InventoryWrapper> findInventoryByDay(String day) {
        return Optional.ofNullable(inventories.get(day));
    }

    public Collection<InventoryWrapper> getInventories() {
        return inventories.values();
    }
}
