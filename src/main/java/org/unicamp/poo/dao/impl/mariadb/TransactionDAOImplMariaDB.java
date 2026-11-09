package org.unicamp.poo.dao.impl.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;

public final class TransactionDAOImplMariaDB implements TransactionDAO {
    private static final Logger log = LoggerFactory.getLogger(TransactionDAOImplMariaDB.class);

    private final Connection connection;

    public TransactionDAOImplMariaDB(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    public Transaction create(Transaction transaction) {
        String sql = "INSERT INTO MOVIMENTACAO (IdCarteira, Data, TipoOperacao, Quantidade) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, transaction.getWalletId());
            stmt.setDate(2, java.sql.Date.valueOf(transaction.getOperationDate()));
            stmt.setString(3, String.valueOf(transaction.getOperationType().getCode()));
            stmt.setDouble(4, transaction.getQuantity());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                }
            }
            
            log.info("Transação registrada com sucesso no MariaDB: ID={}, Carteira={}, Data={}, Tipo={}, Quantidade={}",
                    transaction.getId(), transaction.getWalletId(), transaction.getOperationDate(),
                    transaction.getOperationType(), transaction.getQuantity());
            return transaction;
            
        } catch (SQLException e) {
            log.error("Erro ao registrar transação no MariaDB para a carteira {}: {}", transaction.getWalletId(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<Transaction> findByWalletId(int walletId) {
        log.debug("Buscando transações da carteira ID {} no MariaDB", walletId);
        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM MOVIMENTACAO WHERE IdCarteira = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    try {
                        java.sql.Date operationDate = rs.getDate("Data");
                        String operationTypeCode = rs.getString("TipoOperacao");
                        Object quantityValue = rs.getObject("Quantidade");

                        if (operationDate == null || operationTypeCode == null
                                || operationTypeCode.length() != 1 || !(quantityValue instanceof Number quantity)) {
                            log.warn("Movimentação ignorada: campos obrigatórios nulos ou inválidos na carteira {}", walletId);
                            continue;
                        }

                        Transaction transaction = new Transaction(
                                rs.getInt("IdCarteira"),
                                operationDate.toLocalDate(),
                                OperationType.fromCode(operationTypeCode.charAt(0)),
                                quantity.doubleValue()
                        );

                        transaction.setId(rs.getInt("IdMovimento"));

                        list.add(transaction);
                    } catch (IllegalArgumentException exception) {
                        log.warn("Erro ao mapear transação na carteira {}: {}", walletId, exception.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            log.error("Erro ao listar transações da carteira {} no MariaDB: {}", walletId, e.getMessage(), e);
        }

        return list;
    }
}
