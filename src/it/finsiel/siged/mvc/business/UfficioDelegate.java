package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.UfficioDAO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

/**
 * @author G.Calli Intersiel Spa
 */
public class UfficioDelegate {

    private static Logger logger = Logger.getLogger(UfficioDelegate.class
            .getName());

    private UfficioDAO ufficioDAO = null;

    private ServletConfig config = null;

    private static UfficioDelegate delegate = null;

    private UfficioDelegate() {
        // Connect to DAO
        try {
            if (ufficioDAO == null) {
                ufficioDAO = (UfficioDAO) DAOFactory
                        .getDAO(Constants.UFFICIO_DAO_CLASS);
                logger.debug("ufficioDAO instantiated:"
                        + Constants.UFFICIO_DAO_CLASS);

            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }

    }

    public static UfficioDelegate getInstance() {
        if (delegate == null)
            delegate = new UfficioDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.UFFICIO_DELEGATE;
    }

    public UfficioVO salvaUfficio(Connection connection, UfficioVO ufficioVO)
            throws Exception {
        UfficioVO uffVO = new UfficioVO();

        if (ufficioVO.getId() != null && ufficioVO.getId().intValue() > 0) {
            uffVO = ufficioDAO.modificaUfficio(connection, ufficioVO);
            // aggiorno nome cartella sul documentale
            // docDelegate.modificaNomeCartellaSuAlbero(connection, uffVO);
        } else {
            ufficioVO.setId(IdentificativiDelegate.getInstance().getNextId(
                    connection, NomiTabelle.UFFICI));
            uffVO = ufficioDAO.nuovoUfficio(connection, ufficioVO);
            // inserisce la cartella in doc_cartelle (documentale)
            /*
             * CartellaVO cartella = docDelegate.aggiungiCartellaSuAlbero(
             * connection, uffVO);
             * docDelegate.aggiornaAlberoCartelleUtenti(connection, cartella);
             */
        }
        return uffVO;

    }

    public UfficioVO salvaUfficio(UfficioVO ufficioVO, String[] utenti) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        UfficioVO uffVO = new UfficioVO();
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            uffVO = salvaUfficio(connection, ufficioVO);
            if (uffVO.getReturnValue() == ReturnValues.SAVED) {
                ufficioDAO.cancellaUtentiReferenti(connection, ufficioVO
                        .getId().intValue());
                if (utenti != null) {
                    ufficioDAO.aggiornaUtentiReferenti(connection, ufficioVO
                            .getId().intValue(), utenti);
                }
            }
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "Salvataggio Ufficio fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "Salvataggio Ufficio fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return uffVO;
    }

    public int getNumeroReferentiByUfficio(int ufficioId) {

        int risultato = 0;
        try {

            risultato = ufficioDAO.getNumeroReferentiByUfficio(ufficioId);
            logger.info("numero utenti per ufficio: " + risultato);
        } catch (DataException de) {
            logger.error("Errore getNumeroReferentiByUfficio ");
        }

        return risultato;

    }

    public boolean cancellaUfficio(int ufficioId) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean cancellato = false;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            if (ufficioId > 0 && ufficioDAO.isUfficioCancellabile(ufficioId)
                    && (ufficioDAO.getUfficiByParent(ufficioId)).size() == 0) {
                /*
                 * DocumentaleDelegate docDelegate = DocumentaleDelegate
                 * .getInstance(); CartellaVO c =
                 * docDelegate.getCartellaVOByUfficioId(connection, ufficioId);
                 * if (c != null) { docDelegate.cancellaCartella(connection,
                 * c.getId() .intValue()); }
                 */
                ufficioDAO.cancellaUfficio(connection, ufficioId);
                connection.commit();
                cancellato = true;
            }

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("UfficioDelegate: failed cancellaUfficio: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellato;
    }

    public Collection getUfficiByParent(int ufficioId) {

        try {
            return ufficioDAO.getUfficiByParent(ufficioId);
        } catch (DataException de) {
            logger.error("UfficioDelegate: failed getting getUfficiByParent: ");
            return null;
        }
    }

    public UfficioVO getUfficioVO(int ufficioId) {
        UfficioVO u = new UfficioVO();
        try {
            u = ufficioDAO.getUfficioVO(ufficioId);
        } catch (DataException de) {
            logger.error("UfficioDelegate: failed getting getUfficioVO: "
                    + ufficioId);
        }
        return u;
    }

    public Collection getUtentiByUfficio(int ufficioId) {
        try {
            if (ufficioId > 0) {
                return ufficioDAO.getUtentiByUfficio(ufficioId);
            } else {
                return null;
            }
        } catch (DataException de) {
            logger
                    .error("UfficioDelegate: failed getting getUtentiByUfficio: ");
            return null;
        }
    }

    /*
     * Ritorna una Collection di UfficioVO per l'utente (id) a cui ha
     * autorizzazione
     */
    public Collection getUfficiUtente(int utenteId) {
        try {
            if (utenteId > 0) {
                return ufficioDAO.getUfficiByUtente(utenteId);
            } else {
                return new ArrayList();
            }
        } catch (DataException de) {
            logger.error("UfficioDelegate: failed getting getUfficiUtente: "
                    + utenteId);
            return new ArrayList();
        }
    }

    public Collection getUffici() {
        try {
            {
                return ufficioDAO.getUffici();
            }
        } catch (DataException de) {
            logger.error("UfficioDelegate: failed getting getUffici");
            return new ArrayList();
        }
    }

    public Collection getUffici(int aooId) {
        try {
            {
                return ufficioDAO.getUffici(aooId);
            }
        } catch (DataException de) {
            logger.error("UfficioDelegate: failed getting getUffici");
            return new ArrayList();
        }
    }

    public String[] getReferentiByUfficio(int ufficioId) {
        String[] referenti = null;
        try {
            referenti = ufficioDAO.getReferentiByUfficio(ufficioId);
        } catch (DataException de) {
            logger.error("", de);
        }
        return referenti;
    }

    public boolean isUtenteReferenteUfficio(int ufficioId, int utenteId)
            throws DataException {
        boolean isReferente = false;
        try {
            isReferente = ufficioDAO.isUtenteReferenteUfficio(ufficioId,
                    utenteId);
        } catch (DataException de) {
            logger.error("", de);
        }
        return isReferente;

    }
}