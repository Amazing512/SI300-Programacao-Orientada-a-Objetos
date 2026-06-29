package org.unicamp.poo.dao;

import java.sql.Connection;

public abstract class DatabaseConnection {
    public abstract void closeConnection();

    public abstract Connection getConnection();

    public abstract void openConnection();
}
