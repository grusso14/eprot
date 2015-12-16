package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.SoggettoDAO;
import it.finsiel.siged.mvc.vo.ListaDistribuzioneVO;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.lookup.StatoCivileVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class SoggettoDelegate {

    private static Logger logger = Logger.getLogger(SoggettoDelegate.class
            .getName());

    private SoggettoDAO soggettoDAO = null;

    private ServletConfig config = null;

    private static SoggettoDelegate delegate = null;

    private SoggettoDelegate() {
        // Connect to DAO
        try {
            if (soggettoDAO == null) {
                soggettoDAO = (SoggettoDAO) DAOFactory
                        .getDAO(Constants.SOGGETTO_DAO_CLASS);
                logger.debug("soggettoDAO instantiated:"
                        + Constants.SOGGETTO_DAO_CLASS);

            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }

    }

    public static SoggettoDelegate getInstance() {
        if (delegate == null)
            delegate = new SoggettoDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.SOGGETTO_DELEGATE;
    }

    public ArrayList getListaPersonaFisica(int aooId, String cognome,
            String nome, String codiceFiscale) {

        try {
            if (cognome == null)
                cognome = "";
            if (nome == null)
                nome = "";
            if (codiceFiscale == null)
                codiceFiscale = "";
            return soggettoDAO.getListaPersonaFisica(aooId, cognome, nome,
                    codiceFiscale);
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getListaPersonaFisica: ");
            return null;
        }
    }

    public ArrayList getListaPersonaGiuridica(int aooId, String denominazione,
            String pIva) {

        try {
            if (denominazione == null)
                denominazione = "";
            if (pIva == null)
                pIva = "";
            return soggettoDAO.getListaPersonaGiuridica(aooId, denominazione,
                    pIva);
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getListaPersonaGiuridica: ");
            return null;
        }
    }

    // lista distribuzione

    public SoggettoVO getPersonaFisica(int id) {

        try {
            return soggettoDAO.getPersonaFisica(id);
        } catch (DataException de) {
            logger.error("SoggettoDelegate: failed getting getPersonaFisica: ");
            return null;
        }
    }

    public SoggettoVO getPersonaGiuridica(int id) {

        try {
            return soggettoDAO.getPersonaGiuridica(id);
        } catch (DataException de) {
            logger.error("SoggettoDelegate: failed getting getPersonaFisica: ");
            return null;
        }
    }

    public ListaDistribuzioneVO getListaDistribuzione(int id) {

        try {
            return soggettoDAO.getListaDistribuzione(id);
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getListaDistribuzione: ");
            return null;
        }
    }

    public ArrayList getElencoListaDistribuzione(String descrizione, int aooId) {

        try {
            return soggettoDAO.getElencoListaDistribuzione(descrizione, aooId);
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getElencoListaDistribuzione: ");
            return null;
        }
    }

    public ArrayList getDestinatariListaDistribuzione(int listaId) {

        try {
            return soggettoDAO.getDestinatariListaDistribuzione(listaId);
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getDestinatariListaDistribuzione");
            return null;
        }
    }

    public ArrayList getElencoListeDistribuzione() {

        try {
            return soggettoDAO.getElencoListeDistribuzione();
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getElencoListeDistribuzione(): ");
            return null;
        }
    }

    public Collection getLstStatoCivile() {
        ArrayList list = new ArrayList(6);
        list.add(new StatoCivileVO("C", "Coniugato/a"));
        list.add(new StatoCivileVO("B", "Celibe"));
        list.add(new StatoCivileVO("N", "Nubile"));
        list.add(new StatoCivileVO("L", "Stato Libero"));
        list.add(new StatoCivileVO("V", "Vedovo/a"));
        list.add(new StatoCivileVO("S", "Sconosciuto"));

        return list;
    }

    /* TODO: Rimuovere connection */
    public int salvaPersonaFisica(SoggettoVO personaFisica, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        logger.info("SoggettoDelegate:salvaSoggetto");
        SoggettoVO sVO = new SoggettoVO("F");
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            personaFisica.setAoo(utente.getValueObject().getAooId());
            if (personaFisica.getId() == null
                    || personaFisica.getId().intValue() == 0) {
                personaFisica.setRowCreatedTime(new Date(System
                        .currentTimeMillis()));
                personaFisica.setRowCreatedUser(utente.getValueObject()
                        .getUsername());
                personaFisica.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection, NomiTabelle.RUBRICA));
                sVO = soggettoDAO.newPersonaFisica(connection, personaFisica);
            } else {
                personaFisica.setRowUpdatedTime(new Date(System
                        .currentTimeMillis()));
                personaFisica.setRowUpdatedUser(utente.getValueObject()
                        .getUsername());
                sVO = soggettoDAO.editPersonaFisica(connection, personaFisica);
            }
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("SoggettoDelegate: failed salvaPersonaFisica: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return sVO.getReturnValue();
    }

    /* TODO: Rimuovere connection */
    public int salvaPersonaGiuridica(SoggettoVO personaGiuridica, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        logger.info("SoggettoDelegate:salvaPersonaGiuridica");
        SoggettoVO sVO = new SoggettoVO("G");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            personaGiuridica.setAoo(utente.getValueObject().getAooId());
            if (personaGiuridica.getId() == null
                    || personaGiuridica.getId().intValue() == 0) {
                personaGiuridica.setRowCreatedTime(new Date(System
                        .currentTimeMillis()));
                personaGiuridica.setRowCreatedUser(utente.getValueObject()
                        .getUsername());
                personaGiuridica.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection, NomiTabelle.RUBRICA));
                sVO = soggettoDAO.newPersonaGiuridica(connection,
                        personaGiuridica);
            } else {
                personaGiuridica.setRowUpdatedTime(new Date(System
                        .currentTimeMillis()));
                personaGiuridica.setRowUpdatedUser(utente.getValueObject()
                        .getUsername());
                sVO = soggettoDAO.editPersonaGiuridica(connection,
                        personaGiuridica);
            }
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("SoggettoDelegate: failed salvaPersonaGiuridica: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return sVO.getReturnValue();
    }

    /* TODO: lista Distribuzione */
    public ListaDistribuzioneVO salvaListaDistribuzione(
            ListaDistribuzioneVO listaDistribuzione, Utente utente,
            Map elencoSoggetti) {
        JDBCManager jdbcMan = null;
        Connection connection = null;

        listaDistribuzione.setReturnValue(ReturnValues.UNKNOWN);
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            if (listaDistribuzione.getId() == null
                    || listaDistribuzione.getId().intValue() == 0) {

                listaDistribuzione.setRowCreatedTime(new Date(System
                        .currentTimeMillis()));
                listaDistribuzione.setRowCreatedUser(utente.getValueObject()
                        .getUsername());
                listaDistribuzione
                        .setId(IdentificativiDelegate.getInstance().getNextId(
                                connection, NomiTabelle.LISTA_DISTRIBUZIONE));
                soggettoDAO.newListaDistribuzione(connection,
                        listaDistribuzione);
                if (listaDistribuzione.getReturnValue() == (ReturnValues.SAVED)) {
                    Iterator it = elencoSoggetti.values().iterator();
                    while (it.hasNext()) {
                        SoggettoVO s = (SoggettoVO) it.next();
                        soggettoDAO.inserisciSoggettoLista(connection,
                                listaDistribuzione.getId().intValue(), s
                                        .getId().intValue(), s.getTipo(),
                                utente.getValueObject().getUsername());
                    }
                }
            } else {
                listaDistribuzione.setRowUpdatedTime(new Date(System
                        .currentTimeMillis()));
                listaDistribuzione.setRowUpdatedUser(utente.getValueObject()
                        .getUsername());
                soggettoDAO.editListaDistribuzione(connection,
                        listaDistribuzione);

                soggettoDAO.deleteRubricaListaDistribuzione(connection,
                        listaDistribuzione.getId().intValue());
                if (listaDistribuzione.getReturnValue() == (ReturnValues.SAVED)
                        || (listaDistribuzione.getReturnValue() == (ReturnValues.EXIST_DESCRIPTION))) {
                    Iterator it = elencoSoggetti.values().iterator();
                    while (it.hasNext()) {
                        SoggettoVO s = (SoggettoVO) it.next();
                        soggettoDAO.inserisciSoggettoLista(connection,
                                listaDistribuzione.getId().intValue(), s
                                        .getId().intValue(), s.getTipo(),
                                utente.getValueObject().getUsername());
                    }

                    // listaDistribuzione.setReturnValue(ReturnValues.SAVED);
                }
                
            }
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("SoggettoDelegate: failed salvaListaDistribuzione: ",
                    de);
        } catch (SQLException se) {
            logger.error("SoggettoDelegate: failed salvaListaDistribuzione: ",
                    se);
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        // listaDistribuzione.setReturnValue(ReturnValues.SAVED);
        return listaDistribuzione;
    }

    public int cancellaSoggetto(long id) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        logger.info("SoggettoDelegate:cancellaSoggetto");
        int returnValue = ReturnValues.SAVED;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            soggettoDAO.deleteSoggetto(connection, id);
            connection.commit();
        } catch (DataException de) {
            returnValue = ReturnValues.INVALID;
            jdbcMan.rollback(connection);
            logger.error("SoggettoDelegate: failed cancellaSoggetto: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return returnValue;
    }

    public int cancellaListaDistribuzione(long id) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        logger.info("SoggettoDelegate:cancellalistaDistribuzione");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            soggettoDAO.deleteRubricaListaDistribuzione(connection, id);
            soggettoDAO.deleteListaDistribuzione(connection, id);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .error("SoggettoDelegate: failed cancellaListaDistribuzione: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return 0;

    }

}
