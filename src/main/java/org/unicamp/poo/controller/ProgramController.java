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
import org.unicamp.poo.view.ReportView;
import org.unicamp.poo.view.TransactionView;
import org.unicamp.poo.view.WalletView;

import java.util.ArrayList;
import java.util.List;

/* Main orchestrator controller that centralizes the application's core flow,
   coordinating menu navigation, sub-controllers lifecycle, and database sessions. */

public class ProgramController {

    // ANSI Escape codes for coloring console output texts
    public static final String reset = "\u001B[0m";
    public static final String yellow = "\u001B[33m";

    private final DatabaseSelector databaseSelector;
    private final MessageProvider messages;
    private DatabaseConnection dbConn;

    private WalletDAO walletDAO;
    private TransactionDAO transactionDAO;

    // Initializes the central controller with the persistence strategy and messaging configuration.

    public ProgramController(DatabaseSelector databaseSelector, MessageProvider messages) {
        super();
        this.databaseSelector = databaseSelector;
        this.messages = messages;
    }

    // Boots up the Wallet Sub-module and hands over the control flow.

    void actionWallet() {
        final WalletController walletController = new WalletController(walletDAO, new WalletView(), messages);
        walletController.start();
    }

    // Boots up the Transaction Sub-module and hands over the control flow.

    void actionTransaction() {
        final TransactionController transactionController = new TransactionController(transactionDAO, walletDAO, new TransactionView(), messages);
        transactionController.start();
    }

    // Boots up the Financial Reports Sub-module and hands over the control flow.

    void actionReports() {
        final ReportController reportController = new ReportController(walletDAO, transactionDAO, new ReportView(), messages);
        reportController.start();
    }

    // Renders the system help menu containing guidelines and general instructions.

    void actionHelp() {
        System.out.println("\n------------------------------");
        System.out.println("       AJUDA DO SISTEMA       ");
        System.out.println("------------------------------");
        System.out.println(messages.get("help.instructions"));
        System.out.println("------------------------------");
    }

    // Initializes state components and data connection layers based on strategy selector.

    private void openDatabase(String serverName)
    {
        switch (databaseSelector)
        {
            case MARIADB:
            {
                dbConn = new MariaDBConnection(serverName);
                dbConn.openConnection();
                walletDAO = new WalletDAOImplMariaDB(dbConn.getConnection());
                transactionDAO = new TransactionDAOImplMariaDB(dbConn.getConnection());
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

    // Terminates network socket bindings securely to prevent open stream leaks.

    private void closeDatabase() {
        if(dbConn != null) {
            dbConn.closeConnection();
        }
    }

    // Prepares localized entry labels mapped to root operation features.

    private List<String> getMenuOptions() {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("mainMenu.exit"));
        options.add(messages.get("mainMenu.wallet"));
        options.add(messages.get("mainMenu.transaction"));
        options.add(messages.get("mainMenu.reports"));
        options.add(messages.get("mainMenu.help"));
        return (options);

    }

    // Executes the primary processing core daemon shell runner loop.

    public void start(String serverName)
    {
        final Menu userMenu = new Menu(ConsoleScanner.getInstance());
        boolean    loop     = true;

        // Establish structural state before listening to interactions
        openDatabase(serverName);

        while (loop)
        {
            switch (userMenu.getChoice(messages.get(yellow + "mainMenu.title" + reset), getMenuOptions(), messages.get("mainMenu.prompt")))
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
