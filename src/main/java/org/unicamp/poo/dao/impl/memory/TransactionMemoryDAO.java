package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionMemoryDAO implements TransactionDAO {

    private static final List<Transaction> transactions = new ArrayList<>();
    private static int nextId = 1;

    @Override
    public Transaction create(Transaction transaction) {
        transaction.setId(nextId++);
        transactions.add(transaction);
        return transaction;
    }

    @Override
    public Transaction findById(int id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == id) {
                return transaction;
            }
        }
        return null;
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
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

    @Override
    public void update(Transaction updatedTransaction) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction currentTransaction = transactions.get(i);

            if (currentTransaction.getId().equals(updatedTransaction.getId())) {
                transactions.set(i, updatedTransaction);
                return;
            }
        }
    }

    @Override
    public void delete(int id) {
        transactions.removeIf(transaction -> transaction.getId() == id);
    }
}