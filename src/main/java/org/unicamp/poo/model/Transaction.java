package org.unicamp.poo.model;

import org.unicamp.poo.model.enums.OperationType;

import java.util.Date;

public class Transaction {
    private Integer id;
    private final int walletId;
    private final Date operationDate;
    private final OperationType operationType;
    private final double quantity;

    public Transaction(int walletId, Date operationDate, OperationType operationType, double quantity) {
        this.id = null; // Defined by persistence
        this.walletId = walletId;
        this.operationDate = operationDate;
        this.operationType = operationType;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getWalletId() {
        return walletId;
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public double getQuantity() {
        return quantity;
    }

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
