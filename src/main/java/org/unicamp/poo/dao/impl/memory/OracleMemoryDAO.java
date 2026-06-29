package org.unicamp.poo.dao.impl.memory;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OracleMemoryDAO extends OracleDAO {

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
}