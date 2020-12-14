package me.dery.seniorsinventorysaver.managers.impl;

import me.dery.seniorsinventorysaver.SeniorsInventorySaver;
import me.dery.seniorsinventorysaver.managers.PlayerManager;
import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import me.dery.seniorsinventorysaver.storage.models.InventoryStorage;
import me.dery.seniorsinventorysaver.wrappers.impl.PlayerInventoryWrapper;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManagerImpl extends PlayerManager {

    private final InventoryStorage inventoryStorage;

    public PlayerManagerImpl(SeniorsInventorySaver instance) {
        super(instance.getPlayerStorage());

        this.inventoryStorage = instance.getInventoryStorage();
    }

    @Override
    public InventorySaverPlayer loadPlayer(UUID identifier) {
        InventorySaverPlayer player = super.loadPlayer(identifier);
        HashMap<String, PlayerInventoryWrapper> inventories = inventoryStorage.getInventoriesByOwner(player.getIdentifier());
        player.saveInventories(inventories);

        return player;
    }

    @Override
    public <V> InventorySaverPlayer updatePlayerInStorage(UUID identifier, V inventory) {
        InventorySaverPlayer player = super.updatePlayerInStorage(identifier, inventory);
        inventoryStorage.bulkSave(player.getInventories());

        return player;
    }
}
