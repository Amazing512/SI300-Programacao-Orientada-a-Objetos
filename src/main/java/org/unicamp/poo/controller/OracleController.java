package org.unicamp.poo.controller;

import org.unicamp.poo.dao.OracleDAO;
import org.unicamp.poo.model.Oracle;

import java.util.Date;
import java.util.List;

public class OracleController {
    
    private final OracleDAO oracleDAO;

  
    public OracleController(OracleDAO oracleDAO) {
        this.oracleDAO = oracleDAO;
    }

    public Oracle fetchAndSaveCurrentQuote(double price) {
        Oracle newQuote = new Oracle();
        newQuote.setDate(new Date());
        newQuote.setPrice(price);
        

        Oracle existingQuote = oracleDAO.findByDate(newQuote.getDate());
        if (existingQuote != null) {
            existingQuote.setPrice(price);
            oracleDAO.update(existingQuote);
            return existingQuote;
        } else {
            return oracleDAO.create(newQuote);
        }
    }

    public Oracle getCurrentQuote() {
        Date hoje = new Date();
        return oracleDAO.findByDate(hoje);
    }


    public Oracle getQuoteByDate(Date date) {
        if (date == null) {
            System.err.println("A data para consulta não pode ser nula.");
            return null;
        }
        return oracleDAO.findByDate(date);
    }


    public List<Oracle> getAllQuotes() {
        return oracleDAO.findAll();
    }

    public void removeQuote(Date date) {
        if (date != null) {
            oracleDAO.delete(date);
        }
    }
}