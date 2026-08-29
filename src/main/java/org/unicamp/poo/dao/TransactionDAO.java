package org.unicamp.poo.dao;

import org.unicamp.poo.model.Transaction;

import java.util.List;

public sealed interface TransactionDAO permits org.unicamp.poo.dao.impl.mariadb.TransactionDAOImplMariaDB,
        org.unicamp.poo.dao.impl.memory.TransactionMemoryDAO {
    Transaction create(Transaction transaction);

    List<Transaction> findByWalletId(int walletId);
}