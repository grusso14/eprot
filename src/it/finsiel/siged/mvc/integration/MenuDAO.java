package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;

import java.util.Collection;

public interface MenuDAO {
    public Collection getAllMenu() throws DataException;

    public boolean isUserEnabled(int utenteId, int ufficioId, int menuId)
            throws DataException;
}