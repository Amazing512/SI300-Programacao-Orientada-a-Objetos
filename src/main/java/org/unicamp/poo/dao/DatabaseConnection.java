package org.unicamp.poo.dao;

import java.sql.Connection;

public interface DatabaseConnection {
    void closeConnection();

    Connection getConnection();

    void openConnection();
}
