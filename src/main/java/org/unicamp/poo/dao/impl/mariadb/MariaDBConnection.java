package org.unicamp.poo.dao.impl.mariadb;

import org.unicamp.poo.dao.DatabaseConnection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MariaDBConnection implements DatabaseConnection {
    private String     user;
    private String     password;
    private String     url;
    private Connection connection = null;

    public MariaDBConnection(String serverName)
    {
        // for OLD drivers use this statment in the method:
        // Class.forName("org.mariadb.jdbc.Driver");
        // and catch
        // ClassNotFoundException

        final Properties props = new Properties();
        final Properties credentials = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(serverName + ".properties"))
        {
            Class.forName("org.mariadb.jdbc.Driver");

            props.load(input);
            String driver = props.getProperty("driver");
            String serverIP = props.getProperty("serverIP");
            String serverPort = props.getProperty("serverPort");
            String database = props.getProperty("database");
            String tail = props.getProperty("tail");
            url        = driver + "://" + serverIP + ":" + serverPort + "/" + database + tail;

            // TODO: Add serverName based env names
            user = System.getenv("MARIADB_USER");
            password = System.getenv("MARIADB_PASSWORD");

            if ((user == null || password == null) && loadCredentialsFromFile(credentials))
            {
                if (user == null)
                {
                    user = credentials.getProperty("MARIADB_USER");
                }
                if (password == null)
                {
                    password = credentials.getProperty("MARIADB_PASSWORD");
                }
            }

            if ((user == null || user.isBlank()) || (password == null || password.isBlank()))
            {
                throw new IllegalStateException("Defina MARIADB_USER e MARIADB_PASSWORD nas variaveis de ambiente ou no arquivo local mariadb.env.");
            }
        }
        catch (final Exception exceptionValue)
        {
            System.out.println(exceptionValue.getMessage());
        }
    }

    @Override
    public void closeConnection()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (final SQLException exceptionValue)
        {
            System.out.println(exceptionValue.getMessage());
        }
    }

    @Override
    public Connection getConnection()
    {
        return (connection);
    }

    @Override
    public void openConnection()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
            }
            System.out.println("URL = " + url);
            System.out.println("USER = " + user);
            connection = DriverManager.getConnection(url, user, password);
        }
        catch (final SQLException exceptionValue)
        {
            System.out.println(exceptionValue.getMessage());
        }
    }

    private boolean loadCredentialsFromFile(Properties credentials)
    {
        try (InputStream input = new FileInputStream("mariadb.env"))
        {
            credentials.load(input);
            return true;
        }
        catch (final FileNotFoundException exceptionValue)
        {
            return false;
        }
        catch (final IOException exceptionValue)
        {
            System.out.println(exceptionValue.getMessage());
            return false;
        }
    }
}
