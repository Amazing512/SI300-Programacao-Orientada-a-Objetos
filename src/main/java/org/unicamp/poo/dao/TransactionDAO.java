package org.unicamp.poo.dao;

import org.unicamp.poo.model.Transaction;

import java.util.List;

public abstract class TransactionDAO {

    public abstract Transaction create(Transaction transaction);

    public abstract List<Transaction> findByWalletId(int walletId);
}