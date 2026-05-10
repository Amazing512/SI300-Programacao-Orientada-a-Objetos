package org.unicamp.poo.dao.impl.mariadb;

import org.unicamp.poo.dao.WalletDAO;

import java.sql.Connection;

public class WalletDAOImplMariaDB implements WalletDAO {
    private Connection connection = null;

    public WalletDAOImplMariaDB(Connection connection)
    {
        this.connection = connection;
    }
}
