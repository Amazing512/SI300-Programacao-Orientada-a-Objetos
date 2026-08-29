package org.unicamp.poo.dao.impl.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

public final class OracleMariaDBDAO implements OracleDAO {
    
    private final Connection connection;

    public OracleMariaDBDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Oracle create(Oracle oracle) {
        String sql = "INSERT INTO ORACULO (Data, Cotacao) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(oracle.getDate().getTime()));
            stmt.setDouble(2, oracle.getPrice());
            
            stmt.executeUpdate();
            System.out.println(java.util.ResourceBundle.getBundle("messages").getString("database.oracle.create.success"));
            return oracle;
        } catch (SQLException e) {
            System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.oracle.create.error") + " " + e.getMessage());
            return null;
        }
    }

    @Override
    public Oracle findByDate(Date date) {
        String sql = "SELECT * FROM ORACULO WHERE Data = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new Oracle(
                                rs.getDate("Data"),
                                rs.getDouble("Cotacao")
                        );
                    } catch (RuntimeException exception) {
                        System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.oracle.map.error") + " " + exception.getMessage());
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(java.util.ResourceBundle.getBundle("messages").getString("database.oracle.find.error") + " " + e.getMessage());
        }
        return null;
    }
}
