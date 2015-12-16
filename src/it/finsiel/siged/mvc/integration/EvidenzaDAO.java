package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;

import java.util.Collection;
import java.util.HashMap;

/*
 * @author G.Calli.
 */

public interface EvidenzaDAO {

    public Collection getEvidenzeProcedimenti(Utente utente, HashMap sqlDB)
            throws DataException;

    public Collection getEvidenzeFascicoli(Utente utente, HashMap sqlDB)
            throws DataException;

    public int contaEvidenzeProcedimenti(Utente utente, HashMap sqlDB)
            throws DataException;

    public int contaEvidenzeFascicoli(Utente utente, HashMap sqlDB)
            throws DataException;

}