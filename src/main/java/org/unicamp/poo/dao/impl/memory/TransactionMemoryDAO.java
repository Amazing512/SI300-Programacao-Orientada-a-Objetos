package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação em memória de {@link TransactionDAO}.
 * Mantém os dados de transações em uma lista em memória para simulação e execução sem banco de dados relacional.
 */
public final class TransactionMemoryDAO implements TransactionDAO {

    private static final List<Transaction> transactions = new ArrayList<>();
    private static int nextId = 1;

    /**
     * Construtor padrão para o DAO de transações em memória.
     */
    public TransactionMemoryDAO() {
    }

    /**
     * Cria e armazena uma transação na lista em memória com ID sequencial.
     *
     * @param transaction a transação a ser registrada
     * @return a transação com seu respectivo ID atribuído
     */
    @Override
    public Transaction create(Transaction transaction) {
        transaction.setId(nextId++);
        transactions.add(transaction);
        return transaction;
    }

    /**
     * Retorna todas as transações cadastradas em memória para uma carteira específica.
     *
     * @param walletId o identificador da carteira
     * @return a lista de transações da carteira
     */
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