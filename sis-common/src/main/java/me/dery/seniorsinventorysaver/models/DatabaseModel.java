package me.dery.seniorsinventorysaver.models;

import me.dery.seniorsinventorysaver.storage.IStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @param <O> the object that it represent
 * @param <P> the primary key in database
 */
public abstract class DatabaseModel<O, P> {

    protected final IStorage storage;

    protected final String tableName, saveCode, primaryName;

    public DatabaseModel(
            IStorage storage,
            String createQuery,
            String tableName,
            String saveCode,
            String primaryName
    ) {
        this.storage = storage;
        this.tableName = tableName == null ? this.getClass().getSimpleName().toLowerCase() + "s" : tableName;
        this.saveCode = saveCode;
        this.primaryName = primaryName == null ? "id" : primaryName;

        try {
            PreparedStatement stmt = storage.prepareStatement(createQuery);
            stmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DatabaseModel(IStorage storage, String createQuery, String saveCode) {
        this(storage, createQuery, null, saveCode, null);
    }

    public void save(O data) {
        try {
            PreparedStatement stmt = storage.prepareStatement(saveCode);
            saveHandler(data).accept(stmt);
            stmt.executeUpdate();

            storage.closeStatement(stmt);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Optional<O> get(P primary) {
        try {
            PreparedStatement stmt = storage.prepareStatement(
                    "SELECT * FROM " + tableName + " WHERE " + primaryName + " = ?"
            );
            setPrimary(primary).accept(stmt);

            ResultSet set = stmt.executeQuery();
            if (!set.next()) return Optional.empty();

            Optional<O> optional = Optional.of(getHandler(set));
            storage.closeStatement(stmt);

            return optional;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void delete(P primary) {
        try {
            PreparedStatement stmt = storage.prepareStatement(
                    "DELETE FROM " + tableName + " WHERE " + primaryName + " = ?"
            );
            setPrimary(primary).accept(stmt);
            stmt.executeUpdate();

            storage.closeStatement(stmt);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Consumer<PreparedStatement> setPrimary(P primary) {
        return stmt -> {
            try {
                Class<?> primaryClass = primary.getClass();
                if (primaryClass == Integer.TYPE)
                    stmt.setInt(1, (Integer) primary);
                else if (primaryClass == Boolean.TYPE)
                    stmt.setBoolean(1, (Boolean) primary);
                else if (primaryClass == String.class)
                    stmt.setString(1, (String) primary);
                else
                    stmt.setString(1, primary.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
    }

    protected abstract Consumer<PreparedStatement> saveHandler(O data);

    protected abstract O getHandler(ResultSet resultSet);
}
