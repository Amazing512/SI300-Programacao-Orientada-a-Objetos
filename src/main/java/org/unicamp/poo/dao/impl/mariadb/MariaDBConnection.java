package org.unicamp.poo.dao.impl.mariadb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.unicamp.poo.dao.DatabaseConnection;

public final class MariaDBConnection implements DatabaseConnection {
    private static MariaDBConnection instance = null;

    private String     user;
    private String     password;
    private String     url;
    private Connection connection = null;

    private MariaDBConnection(String serverName)
    {
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
            
            url = driver + "://" + serverIP + ":" + serverPort + "/" + database + tail;

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
                throw new IllegalStateException(java.util.ResourceBundle.getBundle("messages").getString("database.connection.credentials.missing"));
            }
        }
        catch (final Exception exceptionValue)
        {
            System.out.println(exceptionValue.getMessage());
        }
    }

    public static synchronized MariaDBConnection getInstance(String serverName)
    {
        if (instance == null)
        {
            instance = new MariaDBConnection(serverName);
        }
        return instance;
    }

    public static synchronized MariaDBConnection getInstance()
    {
        if (instance == null)
        {
            instance = new MariaDBConnection("WindServer");
        }
        return instance;
    }

    public static synchronized void resetInstance()
    {
        if (instance != null)
        {
            instance.closeConnection();
            instance = null;
        }
    }

    @Override
    public void closeConnection()
    {
        try
        {
            if (connection != null && !connection.isClosed())
            {
                connection.close();
            }
        }
        catch (final SQLException exceptionValue)
        {
            System.out.println(exceptionValue.getMessage());
        }
        finally
        {
            connection = null;
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
            if (connection != null && !connection.isClosed())
            {
                return;
            }
            if (connection != null)
            {
                connection.close();
            }
            System.out.println("URL = " + url);
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
