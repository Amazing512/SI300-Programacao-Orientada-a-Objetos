package org.unicamp.poo.dao.impl.mariadb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unicamp.poo.dao.WalletDAO;
import org.unicamp.poo.model.Wallet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class WalletDAOImplMariaDB implements WalletDAO {
    private static final Logger log = LoggerFactory.getLogger(WalletDAOImplMariaDB.class);

    private final Connection connection;

    private static final String TABLE_NAME = "CARTEIRA";
    private static final String ID_COLUMN = "IdCarteira";
    private static final String HOLDER_COLUMN = "Titular";
    private static final String BROKER_COLUMN = "Corretora";

    public WalletDAOImplMariaDB(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Wallet create(Wallet wallet) {
        String sql = "INSERT INTO " + TABLE_NAME +
                     " (" + HOLDER_COLUMN + ", " + BROKER_COLUMN + ") " +
                     "VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, wallet.getHolder());
            stmt.setString(2, wallet.getBroker());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    wallet.setId(keys.getInt(1));
                }
            }

            log.info("Carteira criada com sucesso no MariaDB: ID={}, titular='{}', corretora='{}'",
                    wallet.getId(), wallet.getHolder(), wallet.getBroker());
            return wallet;

        } catch (SQLException e) {
            log.error("Erro ao criar carteira no MariaDB para o titular '{}': {}", wallet.getHolder(), e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Wallet findById(int id) {
        log.debug("Buscando carteira por ID {} no MariaDB", id);
        String sql = "SELECT * FROM " + TABLE_NAME +
                     " WHERE " + ID_COLUMN + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Wallet(
                            rs.getInt(ID_COLUMN),
                            rs.getString(HOLDER_COLUMN),
                            rs.getString(BROKER_COLUMN)
                    );
                }
            }

        } catch (SQLException e) {
            log.error("Erro ao buscar carteira por ID {} no MariaDB: {}", id, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Wallet> findAll() {
        log.debug("Buscando todas as carteiras no MariaDB");
        List<Wallet> wallets = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                wallets.add(new Wallet(
                        rs.getInt(ID_COLUMN),
                        rs.getString(HOLDER_COLUMN),
                        rs.getString(BROKER_COLUMN)
                ));
            }

        } catch (SQLException e) {
            log.error("Erro ao listar carteiras no MariaDB: {}", e.getMessage(), e);
        }

        return wallets;
    }

    @Override
    public List<Wallet> findAllOrderByHolder() {
        log.debug("Buscando carteiras ordenadas por titular no MariaDB");
        List<Wallet> wallets = new ArrayList<>();

        String sql = "SELECT * FROM " + TABLE_NAME +
                     " ORDER BY " + HOLDER_COLUMN;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                wallets.add(new Wallet(
                        rs.getInt(ID_COLUMN),
                        rs.getString(HOLDER_COLUMN),
                        rs.getString(BROKER_COLUMN)
                ));
            }

        } catch (SQLException e) {
            log.error("Erro ao listar carteiras ordenadas por titular no MariaDB: {}", e.getMessage(), e);
        }

        return wallets;
    }

    @Override
    public void update(Wallet wallet) {
        String sql = "UPDATE " + TABLE_NAME +
                     " SET " + HOLDER_COLUMN + " = ?, " +
                     BROKER_COLUMN + " = ? " +
                     "WHERE " + ID_COLUMN + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, wallet.getHolder());
            stmt.setString(2, wallet.getBroker());
            stmt.setInt(3, wallet.getId());

            stmt.executeUpdate();
            log.info("Carteira atualizada no MariaDB: ID={}, titular='{}', corretora='{}'",
                    wallet.getId(), wallet.getHolder(), wallet.getBroker());

        } catch (SQLException e) {
            log.error("Erro ao atualizar carteira ID {} no MariaDB: {}", wallet.getId(), e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM " + TABLE_NAME +
                     " WHERE " + ID_COLUMN + " = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
            log.info("Carteira excluída com sucesso no MariaDB: ID={}", id);

        } catch (SQLException e) {
            log.error("Erro ao excluir carteira ID {} no MariaDB: {}", id, e.getMessage(), e);
        }
    }
}
