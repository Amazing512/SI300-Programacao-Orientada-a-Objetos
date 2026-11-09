package org.unicamp.poo.dao.impl.mariadb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

public final class OracleMariaDBDAO implements OracleDAO {
    private static final Logger log = LoggerFactory.getLogger(OracleMariaDBDAO.class);
    
    private final Connection connection;

    public OracleMariaDBDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Oracle create(Oracle oracle) {
        String sql = "INSERT INTO ORACULO (Data, Cotacao) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(oracle.getDate()));
            stmt.setDouble(2, oracle.getPrice());
            
            stmt.executeUpdate();
            log.info("Cotação gravada com sucesso no MariaDB: Data={}, Cotação={}", oracle.getDate(), oracle.getPrice());
            return oracle;
        } catch (SQLException e) {
            log.error("Erro ao gravar cotação no MariaDB para a data {}: {}", oracle.getDate(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Oracle findByDate(LocalDate date) {
        log.debug("Buscando cotação para a data {} no MariaDB", date);
        String sql = "SELECT * FROM ORACULO WHERE Data = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    try {
                        return new Oracle(
                                rs.getDate("Data").toLocalDate(),
                                rs.getDouble("Cotacao")
                        );
                    } catch (RuntimeException exception) {
                        log.error("Erro ao mapear cotação para a data {}: {}", date, exception.getMessage());
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao consultar cotação para a data {} no MariaDB: {}", date, e.getMessage(), e);
        }
        return null;
    }
}
