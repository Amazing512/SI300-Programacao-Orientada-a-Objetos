package org.unicamp.poo.dao.impl.mariadb;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;

public final class TransactionDAOImplMariaDB implements TransactionDAO {
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
            stmt.setString(3,String.valueOf(transaction.getOperationType().getCode()));
            stmt.setDouble(4,transaction.getQuantity());
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println(java.util.ResourceBundle.getBundle("messages").getString("database.transaction.create.success"));
            return transaction;
            
        } catch (SQLException e) {
            System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.transaction.create.error") + " " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<Transaction> findByWalletId(int walletId) {

        List<Transaction> list = new ArrayList<>();

        String sql = "SELECT * FROM MOVIMENTACAO WHERE IdCarteira = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    try {
                        Transaction transaction = new Transaction(
                                rs.getInt("IdCarteira"),
                                rs.getDate("Data").toLocalDate(),
                                OperationType.fromCode(rs.getString("TipoOperacao").charAt(0)),
                                rs.getDouble("Quantidade")
                        );

                        transaction.setId(rs.getInt("IdMovimento"));

                        list.add(transaction);
                    } catch (IllegalArgumentException exception) {
                        System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.transaction.map.error") + " " + exception.getMessage());
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.transaction.list.error") + " " + e.getMessage());
        }

        return list;
    }
}
