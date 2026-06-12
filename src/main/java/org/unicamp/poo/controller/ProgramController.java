package org.unicamp.poo.controller;

import org.unicamp.poo.dao.DatabaseConnection;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.dao.impl.mariadb.MariaDBConnection;
import org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB;
import org.unicamp.poo.dao.impl.mariadb.WalletDAOImplMariaDB;
import org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO ;
import org.unicamp.poo.model.enums.DatabaseSelector;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.TransactionView;
import org.unicamp.poo.view.WalletView;

import java.util.ArrayList;
import java.util.List;

public class ProgramController {
    private final DatabaseSelector databaseSelector;
    private final MessageProvider messages;
    private DatabaseConnection dbConn;

    private WalletDAO walletDAO;
    private TransactionDAO transactionDAO;

    public ProgramController(DatabaseSelector databaseSelector, MessageProvider messages) {
        super();
        this.databaseSelector = databaseSelector;
        this.messages = messages;
    }

    void actionWallet() {
        final WalletController walletController = new WalletController(walletDAO, new WalletView(), messages);
        walletController.start();
    }

    void actionTransaction() {
        final TransactionController transactionController = new TransactionController(transactionDAO, walletDAO, new TransactionView(), messages);
        transactionController.start();
    }

    void actionReports() {

    }

    void actionHelp() {

    }

    private void openDatabase(String serverName)
    {
        switch (databaseSelector)
        {
            case MARIADB:
            {
                dbConn = new MariaDBConnection(serverName);
                dbConn.openConnection();
                walletDAO = new WalletDAOImplMariaDB(dbConn.getConnection());
                transactionDAO     = new TransactionDAOImplMariaDB(dbConn.getConnection());
            }
            break;
            case MEMORY:
            {
                walletDAO = new WalletMemoryDAO();
                transactionDAO = new TransactionMemoryDAO();
            }
            break;
        }
    }

    private void closeDatabase() {
        if(dbConn != null) {
            dbConn.closeConnection();
        }
    }

    private List<String> getMenuOptions() {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("mainMenu.exit"));
        options.add(messages.get("mainMenu.wallet"));
        options.add(messages.get("mainMenu.transaction"));
        options.add(messages.get("mainMenu.reports"));
        options.add(messages.get("mainMenu.help"));
        return (options);

    }

    public void start(String serverName)
    {
        final Menu userMenu = new Menu(ConsoleScanner.getInstance());
        boolean    loop     = true;

        openDatabase(serverName);

        while (loop)
        {
            switch (userMenu.getChoice(messages.get("mainMenu.title"), getMenuOptions(), messages.get("mainMenu.prompt")))
            {
                case 0 -> loop = false;
                case 1 -> actionWallet();
                case 2 -> actionTransaction();
                case 3 -> actionReports();
                case 4 -> actionHelp();
                default -> loop = false;
            }
        }

        closeDatabase();
    }
}
