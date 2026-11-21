package org.unicamp.poo.model;

import java.time.LocalDate;

import org.unicamp.poo.model.enums.OperationType;

/**
 * Representa uma transação vinculada a uma carteira.
 */
public class Transaction {
    private Integer id;
    private final int walletId;
    private final LocalDate operationDate;
    private final OperationType operationType;
    private final double quantity;

    /**
     * Constrói uma nova transação financeira para uma carteira específica.
     *
     * @param walletId      o id da carteira
     * @param operationDate a data em que a operação foi realizada
     * @param operationType o tipo de operação financeira
     * @param quantity      a quantidade de moedas envolvidas na operação
     */
    public Transaction(int walletId, LocalDate operationDate, OperationType operationType, double quantity) {
        this.id = null; // Definido no banco
        this.walletId = walletId;
        this.operationDate = operationDate;
        this.operationType = operationType;
        this.quantity = quantity;
    }

    /**
     * Obtém o id da transação.
     *
     * @return o id da transação ou null se ainda não persistida
     */
    public Integer getId() {
        return id;
    }

    /**
     * Define o id da transação.
     *
     * @param id o novo id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Obtém o id da carteira associada à movimentação.
     *
     * @return o id da carteira
     */
    public int getWalletId() {
        return walletId;
    }

    /**
     * Obtém a data da operação.
     *
     * @return a data da transação
     */
    public LocalDate getOperationDate() {
        return operationDate;
    }

    /**
     * Obtém o tipo da operação financeira.
     *
     * @return o tipo de operação como {@link OperationType}
     */
    public OperationType getOperationType() {
        return operationType;
    }

    /**
     * Obtém a quantidade de moedas movimentadas.
     *
     * @return a quantidade de moedas
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Retorna a representação em texto dos dados da transação.
     *
     * @return representação em {@link String} contendo id, carteira, data, tipo e quantidade
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", walletId=" + walletId +
                ", operationDate=" + operationDate +
                ", operationType=" + operationType +
                ", quantity=" + quantity +
                '}';
    }
}
