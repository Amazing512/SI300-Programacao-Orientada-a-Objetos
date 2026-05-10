package org.unicamp.poo.dao.impl.mariadb;

import org.unicamp.poo.dao.OracleDAO;

import java.sql.Connection;

public class OracleDAOImplMariaDB implements OracleDAO {
    private Connection connection = null;

    public OracleDAOImplMariaDB(Connection connection)
    {
        this.connection = connection;
    }
}
