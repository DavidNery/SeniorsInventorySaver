package me.dery.seniorsinventorysaver.storage.impl;

import me.dery.seniorsinventorysaver.storage.IStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQLStorage implements IStorage {

    private Connection currentConnection;

    private final List<PreparedStatement> preparedStatements;

    private final String host, database, user, password;

    public MySQLStorage(String host, String database, String user, String password) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;

        this.preparedStatements = new ArrayList<>();
    }

    @Override
    public synchronized void openConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        if (currentConnection == null || currentConnection.isClosed())
            currentConnection = DriverManager.getConnection(String.format(
                    "jdbc:mysql://%s/%s", host, database
            ), user, password);
    }

    @Override
    public synchronized void closeConnection(boolean forceClose) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");

        if (currentConnection != null && !currentConnection.isClosed()
                && (forceClose || preparedStatements.size() == 0)) {
            for (PreparedStatement preparedStatement : preparedStatements)
                preparedStatement.close();

            currentConnection.close();
            currentConnection = null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException {
        openConnection();
        PreparedStatement stmt = currentConnection.prepareStatement(sql);
        preparedStatements.add(stmt);

        return stmt;
    }

    @Override
    public void closeStatement(PreparedStatement stmt) throws SQLException, ClassNotFoundException {
        stmt.close();
        preparedStatements.remove(stmt);
        closeConnection(false);
    }
}
