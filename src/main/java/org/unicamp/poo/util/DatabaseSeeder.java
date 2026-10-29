package org.unicamp.poo.util;

import static org.unicamp.poo.util.ConsoleColors.GREEN;
import static org.unicamp.poo.util.ConsoleColors.RESET;

import java.time.LocalDate;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.OraclePriceFactory;
import org.unicamp.poo.model.TransactionFactory;
import org.unicamp.poo.model.Wallet;
import org.unicamp.poo.model.WalletFactory;

public final class DatabaseSeeder {

    private DatabaseSeeder() {
    }

    public static void seed(WalletDAO walletDAO, TransactionDAO transactionDAO, OracleDAO oracleDAO, MessageProvider messages) {
        if (!walletDAO.findAll().isEmpty()) {
            return;
        }

        System.out.println(GREEN + messages.get("seeder.start") + RESET);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);
        LocalDate threeDaysAgo = today.minusDays(3);
        LocalDate fourDaysAgo = today.minusDays(4);

        oracleDAO.create(OraclePriceFactory.create(today, 5.50));
        oracleDAO.create(OraclePriceFactory.create(yesterday, 5.25));
        oracleDAO.create(OraclePriceFactory.create(twoDaysAgo, 5.75));
        oracleDAO.create(OraclePriceFactory.create(threeDaysAgo, 6.10));
        oracleDAO.create(OraclePriceFactory.create(fourDaysAgo, 4.90));

        Wallet w1 = walletDAO.create(WalletFactory.create("Alice Silva", "Binance"));
        Wallet w2 = walletDAO.create(WalletFactory.create("Bob Santos", "Coinbase"));
        Wallet w3 = walletDAO.create(WalletFactory.create("Carol Costa", "Mercado Bitcoin"));

        if (w1 != null) {
            transactionDAO.create(TransactionFactory.createBuy(w1.getId(), threeDaysAgo, 10.0));
            transactionDAO.create(TransactionFactory.createBuy(w1.getId(), twoDaysAgo, 5.5));
            transactionDAO.create(TransactionFactory.createSell(w1.getId(), yesterday, 2.0));
        }

        if (w2 != null) {
            transactionDAO.create(TransactionFactory.createBuy(w2.getId(), fourDaysAgo, 20.0));
            transactionDAO.create(TransactionFactory.createSell(w2.getId(), twoDaysAgo, 5.0));
            transactionDAO.create(TransactionFactory.createSell(w2.getId(), yesterday, 3.0));
        }

        if (w3 != null) {
            transactionDAO.create(TransactionFactory.createBuy(w3.getId(), yesterday, 15.0));
            transactionDAO.create(TransactionFactory.createBuy(w3.getId(), today, 5.0));
        }

        System.out.println(GREEN + messages.get("seeder.success") + RESET);

        ConsoleScanner.pressEnterToContinue(messages.get("generic.pressEnter"));
        ConsoleScanner.clearScreen();
    }
}
