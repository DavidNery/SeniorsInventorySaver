package me.dery.seniorsinventorysaver.storage.models;

import me.dery.seniorsinventorysaver.models.DatabaseModel;
import me.dery.seniorsinventorysaver.storage.IStorage;
import me.dery.seniorsinventorysaver.utils.DateToDay;
import me.dery.seniorsinventorysaver.wrappers.InventoryWrapper;
import me.dery.seniorsinventorysaver.wrappers.impl.PlayerInventoryWrapper;

import java.io.IOException;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class InventoryStorage extends DatabaseModel<PlayerInventoryWrapper, Integer> {

    public InventoryStorage(IStorage storage) {
        super(
                storage,
                "CREATE TABLE IF NOT EXISTS inventories (" +
                        "id INT PRIMARY KEY AUTO_INCREMENT," +
                        "owner CHAR(36) NOT NULL," +
                        "day DATE NOT NULL," +
                        "content TEXT NOT NULL," +
                        "FOREIGN KEY (owner) REFERENCES players(uuid))",
                "inventories",
                "REPLACE INTO inventories VALUES(?, ?, ?, ?)",
                "id"
        );
    }

    public void bulkSave(Collection<? extends InventoryWrapper> inventories) {
        try {
            PreparedStatement stmt = storage.prepareStatement(saveCode);
            for (InventoryWrapper inventory : inventories) {
                saveHandler((PlayerInventoryWrapper) inventory).accept(stmt);
                stmt.addBatch();
            }
            stmt.executeBatch();
            storage.closeStatement(stmt);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Consumer<PreparedStatement> saveHandler(PlayerInventoryWrapper data) {
        return stmt -> {
            data.getSerialized().ifPresent(serialized -> {
                try {
                    if (data.getId() == -1) // if -1, insert new entry
                        stmt.setNull(1, Types.INTEGER);
                    else
                        stmt.setInt(1, data.getId());

                    stmt.setString(2, data.getOwner().toString());
                    stmt.setDate(3, new Date(DateToDay.dayToDate(data.getDate()).getTime()));
                    stmt.setString(4, serialized);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        };
    }

    public HashMap<String, PlayerInventoryWrapper> getInventoriesByOwner(UUID owner) {
        HashMap<String, PlayerInventoryWrapper> inventories = new HashMap<>();

        try {
            PreparedStatement stmt = storage.prepareStatement("SELECT * FROM inventories WHERE owner=?");
            stmt.setString(1, owner.toString());

            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                String date = DateToDay.dateToDay(result.getDate("day"));
                inventories.put(
                        date,
                        (PlayerInventoryWrapper) InventoryWrapper.fromString(
                                result.getInt("id"),
                                UUID.fromString(result.getString("owner")),
                                date,
                                result.getString("content")
                        ));
            }

        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return inventories;
    }

    @Override
    protected PlayerInventoryWrapper getHandler(ResultSet resultSet) {
        return null;
    }

    public void removeOldInventories(int olderThanDays) {
        try {
            PreparedStatement stmt = storage.prepareStatement("DELETE FROM inventories WHERE DATEDIFF(NOW(), day) > " + olderThanDays);
            stmt.executeUpdate();
            storage.closeStatement(stmt);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
