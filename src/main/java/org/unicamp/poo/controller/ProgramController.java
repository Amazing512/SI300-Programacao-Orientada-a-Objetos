package org.unicamp.poo.controller;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.dao.DatabaseConnection;
import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.dao.impl.mariadb.MariaDBConnection;
import org.unicamp.poo.dao.impl.mariadb.OracleMariaDBDAO;
import org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB;
import org.unicamp.poo.dao.impl.mariadb.WalletDAOImplMariaDB;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO;
import org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.model.enums.DatabaseSelector;
import org.unicamp.poo.oracle.OracleApiClient;
import org.unicamp.poo.oracle.OracleApiConfig;
import org.unicamp.poo.oracle.OracleApiException;
import org.unicamp.poo.oracle.SimulatedOracleApiClient;
import org.unicamp.poo.oracle.SocketOracleApiClient;
import static org.unicamp.poo.util.ConsoleColors.RESET;
import static org.unicamp.poo.util.ConsoleColors.YELLOW;
import org.unicamp.poo.util.ConsoleScanner;
import org.unicamp.poo.util.DatabaseSeeder;
import org.unicamp.poo.util.Info;
import org.unicamp.poo.util.MessageProvider;
import org.unicamp.poo.view.Menu;
import org.unicamp.poo.view.ReportView;
import org.unicamp.poo.view.TransactionView;
import org.unicamp.poo.view.WalletView;

public class ProgramController {
    private static final Logger log = LoggerFactory.getLogger(ProgramController.class);

    private DatabaseSelector databaseSelector;
    private final MessageProvider messages;
    private DatabaseConnection dbConn;

    private WalletDAO walletDAO;
    private TransactionDAO transactionDAO;
    private OracleController oracleController;

    public ProgramController(DatabaseSelector databaseSelector, MessageProvider messages) {
        super();
        this.databaseSelector = databaseSelector;
        this.messages = messages;
    }

    void actionWallet() {
        final WalletController walletController = new WalletController(walletDAO, new WalletView(messages), messages);
        walletController.start();
    }

    void actionTransaction() {
        final TransactionController transactionController = new TransactionController(transactionDAO, walletDAO, oracleController, new TransactionView(messages), messages);
        transactionController.start();
    }

    void actionReports() {
        final ReportController reportController = new ReportController(walletDAO, transactionDAO, oracleController, new ReportView(messages), messages);
        reportController.start();
    }

    void actionLanguage(){
        boolean loop = true;

        while (loop) {

        final List<String> options = new ArrayList<>();
        options.add(messages.get("languageMenu.return"));
        options.add(messages.get("languageMenu.pt"));
        options.add(messages.get("languageMenu.en"));
        options.add(messages.get("languageMenu.es"));

            final Menu languageMenu = new Menu(ConsoleScanner.getInstance(), this.messages);
            String yellowTitle = YELLOW + messages.get("language.title") + RESET;

            switch (languageMenu.getChoice(yellowTitle, options, messages.get("languageMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> {
                    messages.changeLanguage("messages", "pt", "BR");
                    log.info("Idioma alterado para Português (pt-BR)");
                }
                case 2 -> {
                    messages.changeLanguage("messages", "en", "US");
                    log.info("Language changed to English (en-US)");
                }
                case 3 -> {
                    messages.changeLanguage("messages", "es", "ES");
                    log.info("Idioma cambiado a Español (es-ES)");
                }
            }
        }
    }

    void actionDatabase(){
        boolean loop = true;

        while (loop) {

            final List<String> options = new ArrayList<>();
            options.add(messages.get("databaseMenu.return"));
            options.add(messages.get("databaseMenu.memory"));
            options.add(messages.get("databaseMenu.mariadb"));

            final Menu databaseMenu = new Menu(ConsoleScanner.getInstance(), this.messages);
            String yellowTitle = YELLOW + messages.get("database.title") + RESET;

            switch (databaseMenu.getChoice(yellowTitle, options, messages.get("databaseMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> {
                    openDatabase(DatabaseSelector.MEMORY, "WindServer");
                }
                case 2 -> {
                    openDatabase(DatabaseSelector.MARIADB, "WindServer");
                }
            }
        }
    }

    void actionHelp() {
        final Menu helpMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        final List<String> options = new ArrayList<>();
        options.add(messages.get("helpMenu.return"));
        options.add(messages.get("helpMenu.viewInstructions"));
        options.add(messages.get("helpMenu.viewCredits"));

        while (loop) {
            String yellowTitle = YELLOW + messages.get("helpMenu.title") + RESET;

            switch (helpMenu.getChoice(yellowTitle, options, messages.get("helpMenu.prompt"))) {
                case 0 -> loop = false;
                case 1 -> {
                    ConsoleScanner.clearScreen();
                    System.out.println(YELLOW + messages.get("help.instructions.header") + RESET);
                    System.out.println(messages.get("help.instructions.title"));
                    System.out.println();
                    System.out.println(messages.get("help.instructions.wallet"));
                    System.out.println();
                    System.out.println(messages.get("help.instructions.transaction"));
                    System.out.println();
                    System.out.println(messages.get("help.instructions.reports"));
                    System.out.println();
                    System.out.println(messages.get("help.instructions.tip"));
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                case 2 -> {
                    ConsoleScanner.clearScreen();
                    showCredits();
                    ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
                    ConsoleScanner.clearScreen();
                }
                default -> loop = false;
            }
        }
    }

    private void showCredits() {
        System.out.println(YELLOW + messages.get("help.credits.header") + RESET);
        System.out.println();
        System.out.println(Info.getStamp());
        System.out.println(Info.PROJECT_LICENSE);
        System.out.println(Info.PROJECT_COPYRIGHT);
        System.out.println();
        System.out.println(Info.PROJECT_INSTITUTION);
        System.out.println(Info.PROJECT_DEPARTMENT);
        System.out.println();
        System.out.println(messages.get("help.credits.team") + ":");
        for (Info.AuthorRole authorRole : Info.projectRoles) {
            System.out.println();
            System.out.println(" " + messages.get(authorRole.role()));
            System.out.println("  - " + authorRole.getFormattedAuthors());
        }
        System.out.println();
        System.out.println(messages.get("help.credits.accessDate") + " " + LocalDate.now());
    }

    private void openDatabase(DatabaseSelector selector, String serverName)
    {
        closeDatabase();
        this.databaseSelector = selector;
        log.info("Inicializando banco de dados no modo: {} (servidor: {})", selector, serverName);

        OracleDAO oracleDAO;

        switch (this.databaseSelector) {
            case MARIADB -> {
                dbConn = MariaDBConnection.getInstance(serverName);
                dbConn.openConnection();
                System.out.println(messages.get("success.message.database"));

                if (dbConn.getConnection() == null) {
                    log.error("Falha ao abrir a conexão MariaDB no servidor {}.", serverName);
                    throw new IllegalStateException("Falha ao abrir a conexão MariaDB.");
                }

                walletDAO = new WalletDAOImplMariaDB(dbConn.getConnection());
                transactionDAO = new TransactionDAOImplMariaDB(dbConn.getConnection());
                oracleDAO = new OracleMariaDBDAO(dbConn.getConnection());
                oracleController = new OracleController(oracleDAO, createOracleApiClient());

                log.info("Executando carga inicial de dados no MariaDB...");
                DatabaseSeeder.seed(walletDAO, transactionDAO, oracleDAO, messages);
            }
            case MEMORY -> {
                walletDAO = new WalletMemoryDAO();
                transactionDAO = new TransactionMemoryDAO();
                oracleDAO = new OracleMemoryDAO();
                oracleController = new OracleController(oracleDAO, createOracleApiClient());
                System.out.println(messages.get("success.message.database"));

                log.info("Executando carga inicial de dados no banco em memória...");
                DatabaseSeeder.seed(walletDAO, transactionDAO, oracleDAO, messages);
            }
        }
    }

    /** Cliente do servidor do professor; sem configuração/credenciais, cai no oráculo simulado com aviso. */
    private OracleApiClient createOracleApiClient() {
        try {
            return new SocketOracleApiClient(OracleApiConfig.load(OracleApiConfig.DEFAULT_RESOURCE));
        } catch (OracleApiException e) {
            System.err.println("[Oráculo] " + e.getMessage() + " - usando cotação simulada.");
            return new SimulatedOracleApiClient();
        }
    }

    private void closeDatabase() {
        if(dbConn != null) {
            log.info("Fechando conexão ativa com banco de dados...");
            dbConn.closeConnection();
        }
    }

    private List<String> getMenuOptions() {
        final List<String> options = new ArrayList<>();
        options.add(messages.get("mainMenu.exit"));
        options.add(messages.get("mainMenu.wallet"));
        options.add(messages.get("mainMenu.transaction"));
        options.add(messages.get("mainMenu.reports"));
        options.add(messages.get("mainMenu.language"));
        options.add(messages.get("mainMenu.database"));
        options.add(messages.get("mainMenu.help"));
        return options;
    }

    public void start(String serverName)
    {
        final Menu userMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean    loop     = true;

        log.info("Iniciando aplicação com servidor {}", serverName);
        openDatabase(this.databaseSelector, serverName);

        while (loop)
        {
            String yellowTitle = YELLOW + messages.get("mainMenu.title") + RESET;
            switch (userMenu.getChoice(yellowTitle, getMenuOptions(), messages.get("mainMenu.prompt")))
            {
                case 0 -> {
                    log.info("Encerrando execução pelo menu principal.");
                    loop = false;
                }
                case 1 -> actionWallet();
                case 2 -> actionTransaction();
                case 3 -> actionReports();
                case 4 -> actionLanguage();
                case 5 -> actionDatabase();
                case 6 -> actionHelp();
                default -> loop = false;
            }
        }
    }
}
