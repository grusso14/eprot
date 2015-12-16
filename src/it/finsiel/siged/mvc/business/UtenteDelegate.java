package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.AuthenticationException;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.UtenteDAO;
import it.finsiel.siged.mvc.vo.documentale.CartellaVO;
import it.finsiel.siged.mvc.vo.lookup.ParametriLdapVO;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoMenuVO;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoRegistroVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.ldap.LdapUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public final class UtenteDelegate {

    private static Logger logger = Logger.getLogger(UtenteDelegate.class
            .getName());

    private UtenteDAO utenteDAO = null;

    private ServletConfig config = null;

    private static UtenteDelegate delegate = null;

    private UtenteDelegate() {
        try {
            if (utenteDAO == null) {
                utenteDAO = (UtenteDAO) DAOFactory
                        .getDAO(Constants.UTENTE_DAO_CLASS);

                logger.debug("UserDAO instantiated:"
                        + Constants.UTENTE_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }

    }

    public static UtenteDelegate getInstance() {
        if (delegate == null)
            delegate = new UtenteDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.UTENTE_DELEGATE;
    }

    public String[] getPermessiRegistri(int utenteId) {
        String[] registri = null;
        try {
            registri = utenteDAO.getPermessiRegistri(utenteId);
            logger.info("getting PermessiRegistri");
        } catch (DataException de) {
            logger.error("UserDelegate failed getPermessiRegistri");
        }
        return registri;
    }

    public Collection getUtenti() {
        Collection utenti = null;
        try {
            utenti = utenteDAO.getUtenti();
            logger.info("getting utenti");
        } catch (DataException de) {
            logger.error("UserDelegate failed getting Users");
        }
        return utenti;
    }

    /*
     * public Map getUfficiByUtente(Utente utente) { Map utenti = null; try {
     * utenti = utenteDAO.getUfficiByUtente(utente); logger.info("getting
     * getUfficiByUtente"); } catch (DataException de) {
     * logger.error("UserDelegate failed getting getUfficiByUtente"); } return
     * utenti; }
     */

    public Collection cercaUtenti(int aooId, String username, String cognome,
            String nome) throws DataException {
        Collection utenti = null;
        try {
            utenti = utenteDAO.cercaUtenti(aooId, username, cognome, nome);
            logger.info("ricerca utenti...");
        } catch (DataException de) {
            logger.error("Errore nella ricerca degli utenti.");
        }
        return utenti;
    }

    /*
     * return a Utente, if not found status is = ActionStatus.NOT_FOUND
     */
    public UtenteVO getUtente(String username, String pwd) {
        UtenteVO u = null;
        try {
            u = utenteDAO.getUtente(username, pwd);
            logger.info("getting user id: " + u.getId());
        } catch (DataException de) {
            logger.error("UserDelegate failed getting User: " + username + "/"
                    + pwd);
        }
        return u;
    }

    /*
     * return a Utente, if not found status is = ActionStatus.NOT_FOUND
     */
    public UtenteVO getUtente(String username, String pwd,
            ParametriLdapVO ldapSettings) throws AuthenticationException {
        UtenteVO u = null;
        try {
            // se esiste su LDAP
            if (LdapUtil.autenticaUtente(ldapSettings.getHost(), ldapSettings.getPorta(),LdapUtil.getDN(username, ldapSettings.getDn()), pwd)) {
                u = utenteDAO.getUtente(username);
                logger.info("loading utente id: " + u.getId());
                if (u == null || u.getUsername() == null) {
                    logger.warn("L'utente " + username + " e' autenticato sul server LDAP ma non ha un profilo nell'applicazione");
                } else {
                    // backup della password nella base dati locale per
                    // permettere "un-plug" di LDAP e preservare il
                    // funzionamento dell'applicazione
                    utenteDAO.sincronizzaPassword(u.getId().intValue(), pwd);
                }
            }
        } catch (DataException de) {
            logger.error("UserDelegate failed getting User: " + username + "/"
                    + pwd);
        }
        return u;
    }

    public UtenteVO getUtente(int id) {
        UtenteVO u = null;
        try {
            u = utenteDAO.getUtente(id);
            logger.info("trovato utente id: " + u.getId());
        } catch (DataException de) {
            logger.error("UserDelegate failed getting Utente: id=" + id);
        }
        return u;
    }

    /**
     * Save a Utente profile filled with provided values by the vo.
     * 
     * @return Utente
     */
    public UtenteVO newUtenteVO(Connection connection, UtenteVO vo,
            String[] uffici, String[] menu, String[] registri, Utente utente)
            throws Exception {

        UtenteVO newVO = new UtenteVO();
        newVO.setReturnValue(ReturnValues.UNKNOWN);
        vo.setId(IdentificativiDelegate.getInstance().getNextId(connection,
                NomiTabelle.UTENTI));
        newVO = utenteDAO.newUtenteVO(connection, vo);

        int utenteId = newVO.getId().intValue();
        for (int i = 0; i < uffici.length; i++) {
            if (uffici[i] != null && menu[i] != null) {
                int ufficioId = new Integer(uffici[i]).intValue();
                cancellaPermessiUtente(connection, utenteId, ufficioId, utente);
                permessiUtente(connection, utenteId, ufficioId, menu, utente,
                        newVO);

            }
        }
        // permessi sui registri;
        /* cancellaPermessiRegistriUtente(connection, utenteId, utente); */
        if (registri != null && registri.length > 0) {
            for (int i = 0; i < registri.length; i++) {
                if (registri[i] != null) {
                    int registroId = new Integer(registri[i]).intValue();
                    permessiRegistriUtente(connection,
                            newVO.getId().intValue(), registroId, utente);
                }
            }
        }

        return newVO;

    }

    public UtenteVO newUtenteVO(UtenteVO vo, String[] uffici, String[] menu,
            String[] registri, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        UtenteVO newVO = new UtenteVO();
        newVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            newVO = newUtenteVO(connection, vo, uffici, menu, registri, utente);
            connection.commit();
            newVO.setReturnValue(ReturnValues.SAVED);
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            logger.error("", de);
        } catch (Exception se) {
            jdbcMan.rollback(connection);
            newVO.setReturnValue(ReturnValues.UNKNOWN);
            logger.error("", se);
        } finally {
            jdbcMan.close(connection);

        }
        return newVO;
    }

    /**
     * Save a Utente profile filled with provided values by the vo.
     * 
     * @return Utente
     */
    public UtenteVO updateUtenteVO(UtenteVO vo, String[] registri, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        UtenteVO newVO = new UtenteVO();
        newVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            newVO = utenteDAO.updateUtenteVO(connection, vo);
            // permessi sui registri;
            int utenteId = newVO.getId().intValue();
            cancellaPermessiRegistriUtente(connection, utenteId, utente);
            for (int i = 0; i < registri.length; i++) {
                if (registri[i] != null) {
                    int registroId = new Integer(registri[i]).intValue();
                    permessiRegistriUtente(connection,
                            newVO.getId().intValue(), registroId, utente);
                }
            }
            connection.commit();
            vo.setReturnValue(ReturnValues.SAVED);
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("", de);
        } catch (Exception se) {
            jdbcMan.rollback(connection);
            logger.error("", se);
        } finally {
            jdbcMan.close(connection);
        }
        return newVO;
    }

    public int permessiUtente(int utenteId, int ufficioId, String[] menu,
            Utente utente, UtenteVO newUtente, boolean isConfirm) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int retVal = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            if(isConfirm){
            	cancellaPermessiUtente(connection, utenteId, ufficioId, utente);
            }
            permessiUtente(connection, utenteId, ufficioId, menu, utente,
                    newUtente);

            connection.commit();
            retVal = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("", de);
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.error("", se);
        } finally {
            jdbcMan.close(connection);

        }
        return retVal;
    }

    private void permessiUtente(Connection connection, int utenteId,
            int ufficioId, String[] menu, Utente utente, UtenteVO newUtente)
            throws DataException {
        for (int i = 0; i < menu.length; i++) {
            PermessoMenuVO permesso = new PermessoMenuVO();
            permesso.setUtenteId(utenteId);
            permesso.setUfficioId(ufficioId);
            permesso.setMenuId(Integer.parseInt(menu[i]));
            permesso.setRowCreatedTime(new Date());
            permesso.setRowCreatedUser(utente.getValueObject().getUsername());
            permesso.setRowUpdatedTime(permesso.getRowCreatedTime());
            permesso.setRowUpdatedUser(permesso.getRowCreatedUser());
            utenteDAO.nuovoPermessoMenu(connection, permesso);
        }
        // creo la cartella di default per l'utente.
        CartellaVO verificaCartella = DocumentaleDelegate
                .getInstance()
                .getCartellaVOByUfficioUtenteId(connection, ufficioId, utenteId);
        if (verificaCartella.getReturnValue() == ReturnValues.NOT_FOUND) {
            CartellaVO rootCartella = new CartellaVO();
            rootCartella.setAooId(newUtente.getAooId());
            rootCartella.setNome(newUtente.getUsername());
            rootCartella.setRoot(true);
            rootCartella.setUfficioId(ufficioId);
            rootCartella.setUtenteId(newUtente.getId().intValue());
            // verifica esistenza cartelle per ufficio,utente,isRoot
            DocumentaleDelegate.getInstance().creaCartellaUtente(connection,
                    rootCartella);
        }
    }

    public void permessiRegistriUtente(Connection conn, int utenteId,
            int registroId, Utente utente) throws DataException {
        PermessoRegistroVO permesso = new PermessoRegistroVO();
        permesso.setUtenteId(utenteId);
        permesso.setRegistroId(registroId);
        permesso.setRowCreatedTime(new Date());
        permesso.setRowCreatedUser(utente.getValueObject().getUsername());
        permesso.setRowUpdatedTime(permesso.getRowCreatedTime());
        permesso.setRowUpdatedUser(permesso.getRowCreatedUser());
        utenteDAO.nuovoPermessoRegistro(conn, permesso);
    }

    public String[] getFunzioniByUfficioUtente(int utenteId, int ufficioId) {
        String[] funzioni = null;
        try {
            funzioni = utenteDAO
                    .getFunzioniByUfficioUtente(utenteId, ufficioId);
        } catch (DataException de) {
            logger.error("", de);
        }
        return funzioni;
    }

    public Collection getUfficiByUtente(int utenteId) {
        Collection uffici = new ArrayList();
        try {
            uffici = utenteDAO.getUfficiByUtente(utenteId);
        } catch (DataException de) {
            logger.error("", de);
        }
        return uffici;

    }

    /*
     * Cancella i permessi utente nel registro specificato.
     * 
     * Ritorna un valore intero che ci dice lo stato della transazione avvenuta.
     * 
     * @see it.finsiel.siged.constsnt.ReturnValues
     */
    public int cancellaPermessiUtente(int utenteId, int ufficioId, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int retVal = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            cancellaPermessiUtente(connection, utenteId, ufficioId, utente);
            connection.commit();
            retVal = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("", de);
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.error("", se);
        } finally {
            jdbcMan.close(connection);
        }
        return retVal;
    }

    private void cancellaPermessiUtente(Connection conn, int utenteId,
            int ufficioId, Utente utente) throws DataException {
        // DocumentaleDelegate.getInstance().cancellaAlberoUtentePerUfficio(conn,
        // utenteId, ufficioId, utente.getValueObject().getAooId());
        utenteDAO.cancellaPermessiUtente(conn, utenteId, ufficioId, utente);
    }

    private void cancellaPermessiRegistriUtente(Connection conn, int utenteId,
            Utente utente) throws DataException {
        utenteDAO.cancellaPermessiRegistriUtente(conn, utenteId, utente);
    }

    public boolean isUsernameUsed(String username, int utenteId)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean trovato = false;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            trovato = utenteDAO.isUsernameUsed(connection, username, utenteId);
        } catch (DataException de) {
            logger.error("", de);
        } catch (SQLException se) {
            logger.error("", se);
        } finally {
            jdbcMan.close(connection);
        }
        return trovato;
    }

    // modifica pino cambio password
    public UtenteVO aggionaUtenteVO(UtenteVO vo) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        UtenteVO newVO = new UtenteVO();
        newVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            newVO = utenteDAO.updateUtenteVO(connection, vo);
            connection.commit();
            vo.setReturnValue(ReturnValues.SAVED);
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("", de);
        } catch (Exception se) {
            jdbcMan.rollback(connection);
            logger.error("", se);
        } finally {
            jdbcMan.close(connection);
        }
        return newVO;
    }

}