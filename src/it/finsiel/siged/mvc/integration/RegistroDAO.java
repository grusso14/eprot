package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

/*
 * @author Almaviva sud.
 */

public interface RegistroDAO {
    public Collection getRegistriByAooId(int aooId) throws DataException;

    public Collection getRegistri() throws DataException;

    public Collection getRegistriUtente(int utenteId) throws DataException;

    public RegistroVO aggiornaStatoRegistro(RegistroVO reg)
            throws DataException;

    public RegistroVO updateRegistro(RegistroVO registro) throws DataException;

    public RegistroVO updateRegistro(Connection connection, RegistroVO registro)
            throws DataException;

    public RegistroVO newRegistro(RegistroVO registro) throws DataException;

    public RegistroVO newRegistro(Connection connection, RegistroVO registro)
            throws DataException;

    public void setDataAperturaRegistro(int registroId, long data)
            throws DataException;

    public RegistroVO getRegistro(int id) throws DataException;

    public Map getUtentiRegistro(int registroId) throws DataException;

    public Collection getUtentiNonUtilizzatiInRegistro(int registroId,
            String cognome) throws DataException;

    public void cancellaPermessiRegistroUtente(Connection conn, int registroId,
            String utenti) throws DataException;

    public int getCountProtocolliUtenteRegistro(String utenti, int registro)
            throws DataException;

    public boolean cancellaRegistro(int registroId) throws DataException;

    public boolean esisteRegistroUfficialeByAooId(int registroInModifica,
            int aooId) throws DataException;

    public boolean isAbilitatoRegistro(int registroId, int ufficioId,
            int utenteId) throws DataException;

}