package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public final class TransactionMemoryDAO implements TransactionDAO {

    private static final List<Transaction> transactions = new ArrayList<>();
    private static int nextId = 1;

    @Override
    public Transaction create(Transaction transaction) {
        transaction.setId(nextId++);
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public List<Transaction> findByWalletId(int walletId) {
        List<Transaction> walletTransactions = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getWalletId() == walletId) {
                walletTransactions.add(transaction);
            }
        }

        return walletTransactions;
    }
}