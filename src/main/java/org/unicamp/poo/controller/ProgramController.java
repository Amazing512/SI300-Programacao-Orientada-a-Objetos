package org.unicamp.poo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.unicamp.poo.dao.DatabaseConnection;
import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.dao.impl.mariadb.MariaDBConnection;
import org.unicamp.poo.dao.impl.mariadb.OracleMariaDBDAO;
import org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB;
import org.unicamp.poo.dao.impl.mariadb.WalletDAOImplMariaDB;
import org.unicamp.poo.dao.impl.memory.OracleMemoryDAO ;
import org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO;
import org.unicamp.poo.dao.impl.memory.WalletMemoryDAO;
import org.unicamp.poo.model.enums.DatabaseSelector;
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

/* Controller principal que centraliza o fluxo principal da aplicação,
   coordenando a navegação de menus, o ciclo de vida dos sub controladores e as sessões do banco de dados. */
public class ProgramController {

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

    // Inicializa o submódulo Carteira e transfere o fluxo de controle.
    void actionWallet() {
        final WalletController walletController = new WalletController(walletDAO, new WalletView(messages), messages);
        walletController.start();
    }

    // Inicializa o submódulo Transação e transfere o fluxo de controle.
    void actionTransaction() {
        final TransactionController transactionController = new TransactionController(transactionDAO, walletDAO, oracleController, new TransactionView(messages), messages);
        transactionController.start();
    }

    // Inicializa o submódulo de Relatórios Financeiros e transfere o fluxo de controle.
    void actionReports() {
        final ReportController reportController = new ReportController(walletDAO, transactionDAO, oracleController, new ReportView(messages), messages);
        reportController.start();
    }

    // Inicializa o submódulo de escolha de idioma.
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
                }
                case 2 -> {
                    messages.changeLanguage("messages", "en", "US");
                }
                case 3 -> {
                    messages.changeLanguage("messages", "es", "ES");
                }
            }
        }
    }

    // Inicializa o submódulo de escolha do banco de dados.
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

    // Inicializa o submódulo Ajuda com instruções longas e lista de créditos completa.
    void actionHelp() {
        final Menu helpMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean loop = true;

        // Prepara as opções do submenu
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
        System.out.println(messages.get("help.credits.accessDate") + " " + new Date());
    }

    private void openDatabase(DatabaseSelector selector, String serverName)
    {
        OracleDAO oracleDAO;

        switch (databaseSelector) {
            case MARIADB -> {
                dbConn = new MariaDBConnection(serverName);
                dbConn.openConnection();
                System.out.println(messages.get("success.message.database"));

                if (dbConn.getConnection() == null) {
                    throw new IllegalStateException("Falha ao abrir a conexão MariaDB.");
                }

                walletDAO = new WalletDAOImplMariaDB(dbConn.getConnection());
                transactionDAO = new TransactionDAOImplMariaDB(dbConn.getConnection());
                oracleDAO = new OracleMariaDBDAO(dbConn.getConnection());
                oracleController = new OracleController(oracleDAO);

                // Popula o banco de dados com dados fictícios de teste, caso esteja vazio.
                DatabaseSeeder.seed(walletDAO, transactionDAO, oracleDAO, messages);
            }
            case MEMORY -> {
                walletDAO = new WalletMemoryDAO();
                transactionDAO = new TransactionMemoryDAO();
                oracleDAO = new OracleMemoryDAO();
                oracleController = new OracleController(oracleDAO);
                System.out.println(messages.get("success.message.database"));

                // Popula o banco de dados com dados fictícios de teste, caso esteja vazio.
                DatabaseSeeder.seed(walletDAO, transactionDAO, oracleDAO, messages);
            }
        }
    }

    // Encerra as conexões de rede para evitar vazamentos de recursos.
    private void closeDatabase() {
        if(dbConn != null) {
            dbConn.closeConnection();
        }
    }

    // Prepara rótulos de entrada localizados mapeados para os recursos da operação raiz.
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

    // loop principal
    public void start(String serverName)
    {
        final Menu userMenu = new Menu(ConsoleScanner.getInstance(), messages);
        boolean    loop     = true;

        openDatabase(this.databaseSelector, serverName);

        while (loop)
        {
            String yellowTitle = YELLOW + messages.get("mainMenu.title") + RESET;
            switch (userMenu.getChoice(yellowTitle, getMenuOptions(), messages.get("mainMenu.prompt")))
            {
                case 0 -> loop = false;
                case 1 -> actionWallet();
                case 2 -> actionTransaction();
                case 3 -> actionReports();
                case 4 -> actionLanguage();
                case 5 -> actionDatabase();
                case 6 -> actionHelp();

                default -> loop = false;
            }
        }

        closeDatabase();
    }
}
