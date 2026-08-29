package org.unicamp.poo.dao;

import java.sql.Connection;

public sealed interface DatabaseConnection permits org.unicamp.poo.dao.impl.mariadb.MariaDBConnection {
    void closeConnection();

    Connection getConnection();

    void openConnection();
}
