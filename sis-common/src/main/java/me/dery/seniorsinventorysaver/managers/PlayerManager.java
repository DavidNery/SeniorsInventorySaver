package me.dery.seniorsinventorysaver.managers;

import me.dery.seniorsinventorysaver.models.DatabaseModel;
import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import me.dery.seniorsinventorysaver.utils.DateToDay;
import me.dery.seniorsinventorysaver.wrappers.InventoryWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public abstract class PlayerManager {

    private final DatabaseModel<InventorySaverPlayer, UUID> playerStorage;

    private final HashMap<UUID, InventorySaverPlayer> players;

    public PlayerManager(DatabaseModel<InventorySaverPlayer, UUID> playerStorage) {
        this.playerStorage = playerStorage;
        this.players = new HashMap<>();
    }

    public ArrayList<InventorySaverPlayer> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public void savePlayer(InventorySaverPlayer player) {
        players.put(player.getIdentifier(), player);
    }

    public InventorySaverPlayer loadPlayer(UUID identifier) {
        Optional<InventorySaverPlayer> optional = playerStorage.get(identifier);

        if (!optional.isPresent())
            return createPlayer(identifier);

        InventorySaverPlayer player = optional.get();
        savePlayer(player);
        return player;
    }

    public Optional<InventorySaverPlayer> findPlayer(UUID identifier) {
        return Optional.ofNullable(players.get(identifier));
    }

    public InventorySaverPlayer createPlayer(UUID identifier) {
        InventorySaverPlayer player = new InventorySaverPlayer(identifier);
        players.put(identifier, player);

        return player;
    }

    public <V> InventorySaverPlayer unloadPlayer(UUID identifier, V inventory) {
        InventorySaverPlayer player = updatePlayerInStorage(identifier, inventory);
        players.remove(identifier);

        return player;
    }

    public <V> InventorySaverPlayer updatePlayerInStorage(UUID identifier, V inventory) {
        Optional<InventorySaverPlayer> optional = findPlayer(identifier);
        if (!optional.isPresent())
            return null;

        InventorySaverPlayer player = optional.get();
        player.setLastSave(System.currentTimeMillis());

        String today = DateToDay.today();
        Optional<? extends InventoryWrapper> optionalInventory = player.findInventoryByDay(today);

        try {
            if (optionalInventory.isPresent()) {
                InventoryWrapper inventoryWrapper = optionalInventory.get();
                player.saveInventory(today, InventoryWrapper.fromInventory(
                        inventoryWrapper.getId(), inventoryWrapper.getOwner(), inventoryWrapper.getDate(),
                        inventory
                ));
            } else {
                player.saveInventory(today, InventoryWrapper.fromInventory(
                        -1, identifier, today, inventory
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        playerStorage.save(player);

        return player;
    }
}
