package org.unicamp.poo.dao.impl.mariadb;

import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImplMariaDB implements TransactionDAO {
    private Connection connection = null;

    public TransactionDAOImplMariaDB(Connection connection)
    {
        this.connection = connection;
    }

    @Override
    public Transaction create(Transaction transaction) {
        String sql = "INSERT INTO transactions (quantity, description, date, wallet_id) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDouble(1, transaction.getQuantity()); 
            stmt.setString(2, transaction.getDescription());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(transaction.getDateTime()));
            stmt.setInt(4, transaction.getWalletId()); // Supondo que exista getWalletId()
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                }
            }
            
            System.out.println("Transação criada com sucesso!");
            return transaction;
            
        } catch (SQLException e) {
            System.err.println("Erro ao criar transação: " + e.getMessage());
            return null;
        }
    }

    
        @Override
        public Transaction findById(int id) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setQuantity(rs.getInt("quantity")); 
                    t.setDescription(rs.getString("description"));
                    t.setDateTime(rs.getTimestamp("date").toLocalDateTime());
                    t.setWalletId(rs.getInt("wallet_id"));
                    return t;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar transação por ID: " + e.getMessage());
        }
        return null;
    }

    
    @Override
    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setId(rs.getInt("id"));
                t.setQuantity(rs.getInt("quantity")); 
                t.setDescription(rs.getString("description"));
                t.setDateTime(rs.getTimestamp("date").toLocalDateTime());
                t.setWalletId(rs.getInt("wallet_id"));
                list.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as transações: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Transaction> findByWalletId(int walletId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE wallet_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, walletId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction t = new Transaction();
                    t.setId(rs.getInt("id"));
                    t.setQuantity(rs.getInt("quantity")); 
                    t.setDescription(rs.getString("description"));
                    t.setDateTime(rs.getTimestamp("date").toLocalDateTime());
                    t.setWalletId(rs.getInt("wallet_id"));
                    list.add(t);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar transações por Wallet ID: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void update(Transaction transaction) {
        String sql = "UPDATE transactions SET quantity = ?, description = ?, date = ?, wallet_id = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, transaction.getQuantity());
            stmt.setString(2, transaction.getDescription());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(transaction.getDateTime()));
            stmt.setInt(4, transaction.getWalletId());
            stmt.setInt(5, transaction.getId());
            
            stmt.executeUpdate();
            System.out.println("Transação atualizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Transação excluída com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao deletar transação: " + e.getMessage());
        }
    }
}
