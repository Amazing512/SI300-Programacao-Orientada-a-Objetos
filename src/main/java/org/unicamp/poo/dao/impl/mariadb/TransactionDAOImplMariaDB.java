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

public class TransactionDAOImplMariaDB extends TransactionDAO {
    private final Connection connection;

    public TransactionDAOImplMariaDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean create(Transaction transaction) {
        String sql = "INSERT INTO MOVIMENTACAO (IdCarteira, Data, TipoOperacao, Quantidade) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, transaction.getWalletId());
            stmt.setDate(2, new java.sql.Date(transaction.getOperationDate().getTime()));
            stmt.setString(3, String.valueOf(transaction.getOperationType().getCode()));
            stmt.setDouble(4, transaction.getQuantity());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println(java.util.ResourceBundle.getBundle("messages").getString("database.transaction.create.success"));
                return true; // Corrigido: Retorna boolean conforme a interface
            }

            return false;

        } catch (SQLException e) {
            System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.transaction.create.error") + " " + e.getMessage());
            return false; // Corrigido: Retorna boolean no catch
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
                        String tipoOpStr = rs.getString("TipoOperacao");
                        char tipoOpChar = (tipoOpStr != null && !tipoOpStr.isEmpty()) ? tipoOpStr.charAt(0) : ' ';

                        Transaction t = new Transaction(
                                rs.getInt("IdCarteira"),
                                rs.getDate("Data"),
                                OperationType.fromCode(tipoOpChar),
                                rs.getDouble("Quantidade")
                        );

                        t.setId(rs.getInt("IdMovimento"));
                        list.add(t);

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
