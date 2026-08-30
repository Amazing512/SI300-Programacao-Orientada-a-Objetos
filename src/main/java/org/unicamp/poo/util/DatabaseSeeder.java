package org.unicamp.poo.util;

import java.time.LocalDate;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Oracle;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.enums.OperationType;
import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RESET;

/**
 * Utilitário para popular o banco de dados (em memória ou físico) com dados fictícios para teste.
 */
public final class DatabaseSeeder {

    private DatabaseSeeder() {
        // Construtor vazio e privado p/ evitar instanciação
    }

    public static void seed(WalletDAO walletDAO, TransactionDAO transactionDAO, OracleDAO oracleDAO, MessageProvider messages) {
        // Só popula se não houver carteiras cadastradas para evitar duplicidade
        if (!walletDAO.findAll().isEmpty()) {
            return;
        }

        System.out.println(GREEN + messages.get("seeder.start") + RESET);

        // 1. Definindo as datas relativas ao dia de hoje
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);
        LocalDate threeDaysAgo = today.minusDays(3);
        LocalDate fourDaysAgo = today.minusDays(4);

        // 2. Criando cotações no Oráculo
        oracleDAO.create(new Oracle(today, 5.50));
        oracleDAO.create(new Oracle(yesterday, 5.25));
        oracleDAO.create(new Oracle(twoDaysAgo, 5.75));
        oracleDAO.create(new Oracle(threeDaysAgo, 6.10));
        oracleDAO.create(new Oracle(fourDaysAgo, 4.90));

        // 3. Criando as carteiras (Wallets)
        Wallet w1 = walletDAO.create(new Wallet("Alice Silva", "Binance"));
        Wallet w2 = walletDAO.create(new Wallet("Bob Santos", "Coinbase"));
        Wallet w3 = walletDAO.create(new Wallet("Carol Costa", "Mercado Bitcoin"));

        // 4. Criando transações para as carteiras
        if (w1 != null) {
            transactionDAO.create(new Transaction(w1.getId(), threeDaysAgo, OperationType.CASH_IN, 10.0));
            transactionDAO.create(new Transaction(w1.getId(), twoDaysAgo, OperationType.CASH_IN, 5.5));
            transactionDAO.create(new Transaction(w1.getId(), yesterday, OperationType.CASH_OUT, 2.0));
        }

        if (w2 != null) {
            transactionDAO.create(new Transaction(w2.getId(), fourDaysAgo, OperationType.CASH_IN, 20.0));
            transactionDAO.create(new Transaction(w2.getId(), twoDaysAgo, OperationType.CASH_OUT, 5.0));
            transactionDAO.create(new Transaction(w2.getId(), yesterday, OperationType.CASH_OUT, 3.0));
        }

        if (w3 != null) {
            transactionDAO.create(new Transaction(w3.getId(), yesterday, OperationType.CASH_IN, 15.0));
            transactionDAO.create(new Transaction(w3.getId(), today, OperationType.CASH_IN, 5.0));
        }

        System.out.println(GREEN + messages.get("seeder.success") + RESET);

        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
        ConsoleScanner.clearScreen();
    }
}
