package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;

import java.util.Collection;

public interface RegistroEmergenzaDAO {

    public Collection getProtocolliPrenotati(int registroId)
            throws DataException;

    public int getNumeroProtocolliPrenotati(int registroId)
            throws DataException;

}
