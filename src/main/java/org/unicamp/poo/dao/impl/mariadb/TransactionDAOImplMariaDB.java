package org.unicamp.poo.dao.impl.mariadb;

import org.unicamp.poo.dao.TransactionDAO;

import java.sql.Connection;

public class TransactionDAOImplMariaDB implements TransactionDAO {
    private Connection connection = null;

    public TransactionDAOImplMariaDB(Connection connection)
    {
        this.connection = connection;
    }
}
