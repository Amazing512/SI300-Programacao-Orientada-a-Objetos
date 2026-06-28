package org.unicamp.poo.util;

import java.util.Calendar;
import java.util.Date;

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

    private static Date normalizeToDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static void seed(WalletDAO walletDAO, TransactionDAO transactionDAO, OracleDAO oracleDAO) {
        // Só popula se não houver carteiras cadastradas para evitar duplicidade
        if (!walletDAO.findAll().isEmpty()) {
            return;
        }

        System.out.println(GREEN + "Populando o banco de dados com dados de teste ficticios..." + RESET);

        // 1. Definindo as datas relativas ao dia de hoje
        Calendar cal = Calendar.getInstance();
        Date today = normalizeToDay(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = normalizeToDay(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date twoDaysAgo = normalizeToDay(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date threeDaysAgo = normalizeToDay(cal.getTime());

        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date fourDaysAgo = normalizeToDay(cal.getTime());

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

        System.out.println(GREEN + "Populamento concluido com sucesso! (3 carteiras, 5 cotacoes, 8 movimentacoes inseridas)" + RESET);
    }
}
