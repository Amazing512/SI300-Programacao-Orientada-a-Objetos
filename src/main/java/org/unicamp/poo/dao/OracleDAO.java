package org.unicamp.poo.dao;

import org.unicamp.poo.model.Oracle;

import java.util.Date;
import java.util.List;

public interface OracleDAO {

    Oracle create(Oracle oracle);

    Oracle findByDate(Date date);

    List<Oracle> findAll();

    void update(Oracle oracle);

    void delete(Date date);
}