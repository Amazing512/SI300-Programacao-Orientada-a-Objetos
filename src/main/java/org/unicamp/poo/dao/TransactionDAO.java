package org.unicamp.poo.dao;

import org.unicamp.poo.model.Transaction;

import java.util.List;

public abstract interface TransactionDAO {

    Transaction create(Transaction transaction);

    List<Transaction> findByWalletId(int walletId);
}