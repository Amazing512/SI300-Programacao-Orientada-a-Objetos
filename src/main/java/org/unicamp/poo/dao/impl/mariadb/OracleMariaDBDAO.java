package org.unicamp.poo.dao.impl.mariadb;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class OracleMariaDBDAO implements OracleDAO {
    
    private Connection connection = null;

    public OracleMariaDBDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Oracle create(Oracle oracle) {
        String sql = "INSERT INTO oracle_quotes (quote_date, price) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(oracle.getDate().getTime()));
            stmt.setDouble(2, oracle.getPrice());
            
            stmt.executeUpdate();
            System.out.println("[MariaDB] Cotação inserida com sucesso!");
            return oracle;
        } catch (SQLException e) {
            System.err.println("Erro ao executar CREATE no MariaDB: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Oracle findByDate(Date date) {
        String sql = "SELECT * FROM oracle_quotes WHERE quote_date = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Oracle(
                            rs.getDate("quote_date"),
                            rs.getDouble("price")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao executar FINDBYDATE no MariaDB: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Oracle> findAll() {
        List<Oracle> list = new ArrayList<>();
        String sql = "SELECT * FROM oracle_quotes ORDER BY quote_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Oracle o = new Oracle(
                        rs.getDate("quote_date"),
                        rs.getDouble("price")
                );
                list.add(o);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao executar FINDALL no MariaDB: " + e.getMessage());
        }

        return list;
    }

    @Override
    public void update(Oracle oracle) {
        String sql = "UPDATE oracle_quotes SET price = ? WHERE quote_date = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, oracle.getPrice());
            stmt.setDate(2, new java.sql.Date(oracle.getDate().getTime()));
            
            stmt.executeUpdate();
            System.out.println("[MariaDB] Cotação atualizada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao executar UPDATE no MariaDB: " + e.getMessage());
        }
    }

    @Override
    public void delete(Date date) {
        String sql = "DELETE FROM oracle_quotes WHERE quote_date = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            stmt.executeUpdate();
            System.out.println("[MariaDB] Cotação deletada com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao executar DELETE no MariaDB: " + e.getMessage());
        }
    }
}
