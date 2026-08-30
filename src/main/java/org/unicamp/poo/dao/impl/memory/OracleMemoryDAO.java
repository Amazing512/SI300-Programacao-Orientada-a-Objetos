package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.List;

public final class OracleMemoryDAO implements OracleDAO {

    private static final List<Oracle> oracles = new ArrayList<>();

    @Override
    public Oracle create(Oracle oracle) {
        oracles.add(oracle);
        return oracle;
    }

    @Override
    public Oracle findByDate(LocalDate date) {
        for (Oracle oracle : oracles) {
            if (oracle.getDate().equals(date)) {
                return oracle;
            }
        }

        return null;
    }
}