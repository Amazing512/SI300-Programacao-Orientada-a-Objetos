package org.unicamp.poo.dao;

import org.unicamp.poo.model.Wallet;

import java.util.List;

public abstract class WalletDAO {

    public abstract Wallet create(Wallet wallet);

    public abstract Wallet findById(int id);

    public abstract List<Wallet> findAll();

    public abstract List<Wallet> findAllOrderByHolder();

    public abstract void update(Wallet wallet);

    public abstract void delete(int id);
}