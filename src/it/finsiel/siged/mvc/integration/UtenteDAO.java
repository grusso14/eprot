package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoMenuVO;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoRegistroVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;

import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

/*
 * @author Almaviva sud.
 */

public interface UtenteDAO {
    public UtenteVO getUtente(String username, String pwd) throws DataException;

    public UtenteVO getUtente(Connection connection, String username,
            String password) throws DataException;

    public UtenteVO getUtente(int id) throws DataException;

    public UtenteVO getUtente(String username) throws DataException;

    public UtenteVO getUtente(Connection connection, String username)
            throws DataException;

    public void sincronizzaPassword(int utenteId, String newPwd)
            throws DataException;

    public Collection cercaUtenti(int aooId, String username, String cognome,
            String nome) throws DataException;

    public Collection getUtenti() throws DataException;

    public UtenteVO newUtenteVO(Connection connection, UtenteVO utente)
            throws DataException;

    public UtenteVO updateUtenteVO(Connection connection, UtenteVO utente)
            throws DataException;

    public boolean isUsernameUsed(Connection connection, String username,
            int utenteId) throws DataException;

    public Map getUfficiByUtente(Utente utente) throws DataException;

    public void nuovoPermessoMenu(Connection conn, PermessoMenuVO permesso)
            throws DataException;

    public void nuovoPermessoRegistro(Connection conn,
            PermessoRegistroVO permesso) throws DataException;

    public String[] getPermessiRegistri(int utenteId) throws DataException;

    public String[] getFunzioniByUfficioUtente(int utenteId, int ufficioId)
            throws DataException;

    public void cancellaPermessiUtente(Connection conn, int utenteId,
            int ufficioId, Utente utente) throws DataException;

    public void cancellaPermessiRegistriUtente(Connection conn, int utenteId,
            Utente utente) throws DataException;

    public Collection getUfficiByUtente(int utenteId) throws DataException;

    public long getCountUtenti() throws DataException;
}