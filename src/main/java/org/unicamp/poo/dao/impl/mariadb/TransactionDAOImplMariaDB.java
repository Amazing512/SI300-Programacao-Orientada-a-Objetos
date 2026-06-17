package org.unicamp.poo.dao.impl.mariadb;
import org.unicamp.poo.dao.TransactionDAO;
import org.unicamp.poo.model.Transaction;
import org.unicamp.poo.model.enums.OperationType;


import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImplMariaDB implements TransactionDAO {
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
            stmt.setDate(2,new java.sql.Date(transaction.getOperationDate().getTime()));
            stmt.setString(3,String.valueOf(transaction.getOperationType().getCode()));
            stmt.setDouble(4,transaction.getQuantity());
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
        String sql = "SELECT * FROM MOVIMENTACAO WHERE IdMovimento = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Transaction t = new Transaction(
                            rs.getInt("IdCarteira"),
                            rs.getDate("Data"),
                            OperationType.fromCode(
                                    rs.getString("TipoOperacao").charAt(0)
                            ),
                            rs.getDouble("Quantidade")
                    );

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
        String sql = "SELECT * FROM MOVIMENTACAO";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Transaction t = new Transaction(
                        rs.getInt("IdCarteira"),
                        rs.getDate("Data"),
                        OperationType.fromCode(rs.getString("TipoOperacao").charAt(0)),
                        rs.getDouble("Quantidade"));
                        t.setId(rs.getInt("IdMovimento"));
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

        String sql = "SELECT * FROM MOVIMENTACAO WHERE IdCarteira = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, walletId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Transaction t = new Transaction(
                            rs.getInt("IdCarteira"),
                            rs.getDate("Data"),
                            OperationType.fromCode(rs.getString("TipoOperacao").charAt(0)),
                            rs.getDouble("Quantidade")
                    );

                    t.setId(rs.getInt("IdMovimento"));

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

        String sql =
                "UPDATE MOVIMENTACAO " +
                        "SET IdCarteira = ?, " +
                        "Data = ?, " +
                        "TipoOperacao = ?, " +
                        "Quantidade = ? " +
                        "WHERE IdMovimento = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getWalletId());

            stmt.setDate(
                    2,
                    new java.sql.Date(
                            transaction.getOperationDate().getTime()
                    )
            );

            stmt.setString(
                    3,
                    transaction.getOperationType().name()
            );

            stmt.setDouble(
                    4,
                    transaction.getQuantity()
            );

            stmt.setInt(
                    5,
                    transaction.getId()
            );

            stmt.executeUpdate();

            System.out.println("Transação atualizada com sucesso!");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar transação: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM MOVIMENTACAO WHERE IdMovimento = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Transação excluída com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao deletar transação: " + e.getMessage());
        }
    }
}
