package me.dery.seniorsinventorysaver.storage;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IStorage {

    void openConnection() throws SQLException, ClassNotFoundException;

    void closeConnection(boolean forceClose) throws SQLException, ClassNotFoundException;

    PreparedStatement prepareStatement(String sql) throws SQLException, ClassNotFoundException;

    void closeStatement(PreparedStatement stmt) throws SQLException, ClassNotFoundException;
}
