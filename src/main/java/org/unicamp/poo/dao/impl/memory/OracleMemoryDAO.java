package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OracleMemoryDAO implements OracleDAO {

    private static final List<Oracle> oracles = new ArrayList<>();

    @Override
    public Oracle create(Oracle oracle) {
        oracles.add(oracle);
        return oracle;
    }

    @Override
    public Oracle findByDate(Date date) {
        for (Oracle oracle : oracles) {
            if (oracle.getDate().equals(date)) {
                return oracle;
            }
        }

        return null;
    }

    @Override
    public List<Oracle> findAll() {
        return new ArrayList<>(oracles);
    }

    @Override
    public void update(Oracle updatedOracle) {

        for (int i = 0; i < oracles.size(); i++) {

            Oracle currentOracle = oracles.get(i);

            if (currentOracle.getDate().equals(updatedOracle.getDate())) {
                oracles.set(i, updatedOracle);
                return;
            }
        }
    }

    @Override
    public void delete(Date date) {
        oracles.removeIf(oracle -> oracle.getDate().equals(date));
    }
}