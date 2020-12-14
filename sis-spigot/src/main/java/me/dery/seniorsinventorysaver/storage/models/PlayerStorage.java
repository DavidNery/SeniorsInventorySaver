package me.dery.seniorsinventorysaver.storage.models;

import me.dery.seniorsinventorysaver.models.DatabaseModel;
import me.dery.seniorsinventorysaver.models.InventorySaverPlayer;
import me.dery.seniorsinventorysaver.storage.IStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerStorage extends DatabaseModel<InventorySaverPlayer, UUID> {

    public PlayerStorage(IStorage storage) {
        super(
                storage,
                "CREATE TABLE IF NOT EXISTS players (uuid CHAR(36) PRIMARY KEY, lastSave DATETIME)",
                "players",
                "INSERT INTO players VALUES(?, ?) ON DUPLICATE KEY UPDATE lastSave = ?",
                "uuid"
        );
    }

    @Override
    protected Consumer<PreparedStatement> saveHandler(InventorySaverPlayer data) {
        return stmt -> {
            try {
                Date date = new Date(data.getLastSave());
                stmt.setString(1, data.getIdentifier().toString());
                stmt.setDate(2, date);
                stmt.setDate(3, date);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    @Override
    protected InventorySaverPlayer getHandler(ResultSet resultSet) {
        try {
            return new InventorySaverPlayer(
                    UUID.fromString(resultSet.getString("uuid")),
                    resultSet.getLong("lastSave")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
