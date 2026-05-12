package org.unicamp.poo.dao;

import org.unicamp.poo.model.Transaction;

import java.util.List;

public interface TransactionDAO {

    Transaction create(Transaction transaction);

    Transaction findById(int id);

    List<Transaction> findAll();

    List<Transaction> findByWalletId(int walletId);

    void update(Transaction transaction);

    void delete(int id);
}