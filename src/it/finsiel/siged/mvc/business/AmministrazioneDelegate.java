package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.AmministrazioneDAO;
import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.log.LogAcquisizioneMassivaVO;
import it.finsiel.siged.mvc.vo.lookup.ProfiloVO;
import it.finsiel.siged.mvc.vo.lookup.TipoDocumentoVO;
import it.finsiel.siged.mvc.vo.lookup.TipoProcedimentoVO;
import it.finsiel.siged.mvc.vo.organizzazione.MenuVO;
import it.finsiel.siged.mvc.vo.protocollo.SpedizioneVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class AmministrazioneDelegate implements ComponentStatus {

    private static Logger logger = Logger
            .getLogger(AmministrazioneDelegate.class.getName());

    private int status;

    private AmministrazioneDAO amministrazioneDAO = null;

    private ServletConfig config = null;

    private static AmministrazioneDelegate delegate = null;

    private AmministrazioneDelegate() {
        // Connect to DAO
        try {
            if (amministrazioneDAO == null) {
                amministrazioneDAO = (AmministrazioneDAO) DAOFactory
                        .getDAO(Constants.AMMINISTRAZIONE_DAO_CLASS);

                logger.debug("AmministrazioneDAO instantiated: "
                        + Constants.AMMINISTRAZIONE_DAO_CLASS);
                status = STATUS_OK;
            }
        } catch (Exception e) {
            status = STATUS_ERROR;
            logger.error("", e);
        }

    }

    public static AmministrazioneDelegate getInstance() {
        if (delegate == null)
            delegate = new AmministrazioneDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.AMMINISTRAZIONE_DELEGATE;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int s) {
        this.status = s;
    }

    // fine metodi interfaccia
    public Collection getMenuByProfilo(int profiloId) {
        try {
            return amministrazioneDAO.getMenuByProfilo(profiloId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getMenuByProfilo: ");
            return null;
        }
    }

    public ArrayList getFunzioniMenu() throws DataException {
        try {
            return amministrazioneDAO.getFunzioniMenu();
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getFunzioniMenu: ");
            return null;
        }
    }
    
    public ArrayList getFunzioniDocumentaleMenu() throws DataException {
        ArrayList<MenuVO> voci = new ArrayList<MenuVO>();
        try {
    		voci =  amministrazioneDAO.getFunzioniMenuByFunction(2);
    		
    		return voci;
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getFunzioniMenu: ");
            return null;
        }
    }
    
    public ArrayList getFunzioniAmministrazioneMenu() throws DataException {
    	ArrayList<MenuVO> voci = new ArrayList<MenuVO>();
       try {
    		voci =  amministrazioneDAO.getFunzioniMenuByFunction(3);
    		
    		return voci;
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getFunzioniMenu: ");
            return null;
        }
    }
    
    public ArrayList getFunzioniProtocollazioneMenu() throws DataException {
    	ArrayList<MenuVO> voci = new ArrayList<MenuVO>();
        try {
    		voci = amministrazioneDAO.getFunzioniMenuByFunction(1);
    		
    		return voci;
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getFunzioniMenu: ");
            return null;
        }
    }
    
    public ArrayList getFunzioniHelpMenu() throws DataException {
    	ArrayList<MenuVO> voci = new ArrayList<MenuVO>();
        try {
    		voci = amministrazioneDAO.getFunzioniMenuByFunction(5);
    		
    		return voci;
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getFunzioniMenu: ");
            return null;
        }
    }
    
    public ArrayList getFunzioniReportMenu() throws DataException {
    	ArrayList<MenuVO> voci = new ArrayList<MenuVO>();
        try {
    		voci =  amministrazioneDAO.getFunzioniMenuByFunction(4);
    		
    		return voci;
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getFunzioniMenu: ");
            return null;
        }
    }

    public ProfiloVO getProfilo(int profiloId) throws DataException {
        try {
            return amministrazioneDAO.getProfilo(profiloId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getMenuProfilo: ");
            return null;
        }
    }

    public Collection getMezziSpedizione(String descrizioneSpedizione, int aooId) {
        try {
            return amministrazioneDAO.getSpedizioni(descrizioneSpedizione,
                    aooId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getMezziSpedizione: ");
            return null;
        }
    }

    public SpedizioneVO getMezzoSpedizione(int id) {
        try {
            return amministrazioneDAO.getMezzoSpedizione(id);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getMezzoSpedizione: ");
            return null;
        }
    }

    public SpedizioneVO salvaMezzoSpedizione(Connection connection,
            SpedizioneVO spedizioneVO) throws Exception {
        if (spedizioneVO.getId() != null && spedizioneVO.getId().intValue() > 0) {
            spedizioneVO = amministrazioneDAO.aggiornaMezzoSpedizione(
                    connection, spedizioneVO);
        } else {
            spedizioneVO.setId(IdentificativiDelegate.getInstance().getNextId(
                    connection, NomiTabelle.SPEDIZIONI));
            amministrazioneDAO.newMezzoSpedizione(connection, spedizioneVO);
        }
        return spedizioneVO;
    }

    public SpedizioneVO salvaMezzoSpedizione(SpedizioneVO spedizioneVO)
            throws Exception {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            spedizioneVO = salvaMezzoSpedizione(connection, spedizioneVO);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Mezzo Spedizione fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Mezzo Spedizione fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Mezzo Spedizione - si e' verificata un eccezione non gestita.",
                            e);

        } finally {
            jdbcMan.close(connection);
        }
        return spedizioneVO;
    }

    public boolean cancellaMezzoSpedizione(int spedizioneId) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean cancellato = false;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            cancellato = amministrazioneDAO.cancellaMezzoSpedizione(connection,
                    spedizioneId);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .error("AmministrazioneDelegate: failed cancellaMezzoSpedizione: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellato;
    }

    public void salvaProfilo(ProfiloVO profiloVO) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            if (profiloVO.getId() != null && profiloVO.getId().intValue() > 0) {
                amministrazioneDAO.updateProfilo(connection, profiloVO);
            } else {
                profiloVO.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.PROFILI));
                amministrazioneDAO.newProfilo(connection, profiloVO);
            }
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("AmministrazioneDelegate: failed salvaProfilo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
    }

    public void cancellaProfilo(int profiloId) {
        JDBCManager jdbcMan = null;
        Connection connection = null;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            amministrazioneDAO.cancellaProfilo(profiloId);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("AmministrazioneDelegate: failed cancellaProfilo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
    }

    public ArrayList getProfili(int aooId) {
        try {
            return amministrazioneDAO.getProfili(aooId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getProfili: ");
            return null;
        }
    }

    public TipoDocumentoVO getTipoDocumento(int tipoDocumentoId) {
        try {
            return amministrazioneDAO.getTipoDocumento(tipoDocumentoId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTipoDocumento: ");
            return null;
        }
    }

    public ArrayList getTipiDocumento(int aooId) {
        try {
            return amministrazioneDAO.getTipiDocumento(aooId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTipiDocumento: ");
            return null;
        }
    }

    public TipoDocumentoVO salvaTipoDocumento(Connection connection,
            TipoDocumentoVO tipoDocumentoVO) throws Exception {
        if (tipoDocumentoVO.getId() != null
                && tipoDocumentoVO.getId().intValue() > 0) {
            tipoDocumentoVO = amministrazioneDAO.updateTipoDocumento(
                    connection, tipoDocumentoVO);
        } else {
            tipoDocumentoVO.setId(IdentificativiDelegate.getInstance()
                    .getNextId(connection, NomiTabelle.TIPI_DOCUMENTO));
            tipoDocumentoVO = amministrazioneDAO.newTipoDocumento(connection,
                    tipoDocumentoVO);
        }
        return tipoDocumentoVO;
    }

    public TipoDocumentoVO salvaTipoDocumento(TipoDocumentoVO tipoDocumentoVO) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        TipoDocumentoVO tdVO = new TipoDocumentoVO();
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            tdVO = salvaTipoDocumento(connection, tipoDocumentoVO);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn("Salva TipoDoc fallito, rolling back transction..", de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn("Salva TipoDoc fallito, rolling back transction..", se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salva TipoDoc - si e' verificata un eccezione non gestita.",
                            e);

        } finally {
            jdbcMan.close(connection);
        }
        return tdVO;
    }

    public boolean cancellaTipoDocumento(int tipoDocumentoId) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean cancellato = false;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            cancellato = amministrazioneDAO.cancellaTipoDocumento(connection,
                    tipoDocumentoId);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .error("AmministrazioneDelegate: failed cancellaTipoDocumento: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellato;
    }

    public TipoProcedimentoVO getTipoProcedimento(int tipoProcedimentoId) {
        try {
            return amministrazioneDAO.getTipoProcedimento(tipoProcedimentoId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTipoProcedimento: ");
            return null;
        }
    }

    public ArrayList getTipiProcedimento(int aooId) {
        try {
            return amministrazioneDAO.getTipiProcedimento(aooId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTipiProcedimento: ");
            return null;
        }
    }

    public ArrayList getTipiProcedimento(int aooId, int ufficioCorrente) {
        try {
            return amministrazioneDAO.getTipiProcedimento(aooId,
                    ufficioCorrente);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTipiProcedimento: ");
            return null;
        }
    }

    public TipoProcedimentoVO salvaTipoProcedimento(Connection connection,
            TipoProcedimentoVO tipoProcedimentoVO) throws Exception {
        if (tipoProcedimentoVO.getIdTipo() > 0) {
            tipoProcedimentoVO = amministrazioneDAO.updateTipoProcedimento(
                    connection, tipoProcedimentoVO);
        } else {
            tipoProcedimentoVO.setIdTipo(IdentificativiDelegate.getInstance()
                    .getNextId(connection, NomiTabelle.TIPI_PROCEDIMENTO));
            tipoProcedimentoVO = amministrazioneDAO.newTipoProcedimento(
                    connection, tipoProcedimentoVO);
        }
        return tipoProcedimentoVO;
    }

    public TipoProcedimentoVO salvaTipoProcedimento(
            TipoProcedimentoVO tipoProcedimentoVO) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        TipoProcedimentoVO tdVO = new TipoProcedimentoVO();
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            tdVO = salvaTipoProcedimento(connection, tipoProcedimentoVO);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn("Salva TipoProc fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn("Salva TipoProc fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salva TipoProc - si e' verificata una eccezione non gestita.",
                            e);

        } finally {
            jdbcMan.close(connection);
        }
        return tdVO;
    }

    public boolean cancellaTipoProcedimento(
            TipoProcedimentoVO tipoProcedimentoVO) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean cancellato = false;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            cancellato = amministrazioneDAO.cancellaTipoProcedimento(
                    connection, tipoProcedimentoVO);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .error("AmministrazioneDelegate: failed cancellaTipoProcedimento: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellato;
    }

    public boolean cancellaTipoProcedimento(int tipoProcedimentoId,
            int idUfficio) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        boolean cancellato = false;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            cancellato = amministrazioneDAO.cancellaTipoProcedimento(
                    connection, tipoProcedimentoId, idUfficio);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .error("AmministrazioneDelegate: failed cancellaTipoProcedimento: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return cancellato;
    }

    public ArrayList getLogsAcquisizioneMassiva(int aooId) {
        try {
            return amministrazioneDAO.getLogsAcquisizioneMassiva(aooId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getLogsAcquisizioneMassiva: ");
            return null;
        }
    }

    public boolean cancellaLogsAcquisizioneMassiva(int aooId) {
        try {
            return amministrazioneDAO.cancellaLogsAcquisizioneMassiva(aooId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting cancellaLogsAcquisizioneMassiva: ");
            return false;
        }

    }

    public LogAcquisizioneMassivaVO newLogAcquisizioneMassiva(
            LogAcquisizioneMassivaVO logVO) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            logVO = amministrazioneDAO.newLogAcquisizioneMassivaVO(connection,
                    logVO);

            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .error("AmministrazioneDelegate: failed newLogAcquisizioneMassiva: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }

        return logVO;
    }

    public Collection getElencoTitoliDestinatario() {
        try {
            logger.info("Siamo nel delegate.");
            return amministrazioneDAO.getElencoTitoliDestinatario();
        } catch (DataException de) {
            logger
                    .error("TitoliDestinatarioDelegate: failed getting getElencoTitoliDestinatario: ");
            return null;
        }
    }

    public IdentityVO salvaTitoloDestinatario(Connection conn,
            IdentityVO titoloVO) throws DataException {
        if (titoloVO.getId() != null && titoloVO.getId().intValue() > 0) {
            titoloVO = amministrazioneDAO.updateTitolo(conn, titoloVO);
        } else {
            titoloVO.setId(IdentificativiDelegate.getInstance().getNextId(conn,
                    NomiTabelle.TITOLI_DESTINATARI));
            amministrazioneDAO.newTitoloDestinatario(conn, titoloVO);
        }
        return titoloVO;
    }

    public IdentityVO salvaTitoloDestinatario(IdentityVO titoloVO)
            throws Exception {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            titoloVO = salvaTitoloDestinatario(connection, titoloVO);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Titolo fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Titolo fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Titolo - si e' verificata un eccezione non gestita.",
                            e);

        } finally {
            jdbcMan.close(connection);
        }
        return titoloVO;
    }

    public IdentityVO getTitoloDestinatario(int id) {
        try {
            return amministrazioneDAO.getTitoloDestinatario(id);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTitoloDestinatario: ");
            return null;
        }
    }

    public IdentityVO getTitoloDestinatarioDaTitolo(String titolo) {
        try {
            return amministrazioneDAO.getTitoloDestinatarioDaTitolo(titolo);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting getTitoloDestinatario: ");
            return null;
        }
    }

    public boolean esisteTitolo(String descrizione, int id) {
        boolean esiste = false;
        try {
            esiste = amministrazioneDAO.esisteTitolo(descrizione);

        } catch (Exception de) {
            logger.error("AmministrazioneDelegate: failed nuovoTitolo: ");
        }
        return esiste;
    }

    public boolean deleteTitolo(int titoloId) {
        boolean eliminato = false;
        try {
            eliminato = amministrazioneDAO.deleteTitolo(titoloId);
        } catch (DataException de) {
            logger
                    .error("AmministrazioneDelegate: failed getting deleteTitolo: ");
        }
        return eliminato;
    }

    public boolean esisteTitoloInProtocolloDestinatari(int id) {
        boolean esiste = false;
        try {
            esiste = amministrazioneDAO.esisteTitoloInProtocolloDestinatari(id);

        } catch (Exception de) {
            logger
                    .error("AmministrazioneDelegate: failed esisteTitoloInProtocolloDestinatari: ");
        }
        return esiste;
    }

    public boolean esisteTitoloInStoriaProtDest(int id) {
        boolean esiste = false;
        try {
            amministrazioneDAO.esisteTitoloInStoriaProtDest(id);

        } catch (Exception de) {
            logger
                    .error("AmministrazioneDelegate: failed esisteTitoloInStoriaProtDest: ");
        }
        return esiste;

    }

    public boolean esisteInInvioClassificatiProtDest(int id) {
        boolean esiste = false;
        try {
            amministrazioneDAO.esisteTitoloInInvioClassificatiProtDest(id);

        } catch (Exception de) {
            logger
                    .error("AmministrazioneDelegate: failed esisteTitoloInInvioClassificatiProtDest: ");
        }
        return esiste;
    }

    public boolean esisteInInvioFascicoliDestinatari(int id) {
        boolean esiste = false;
        try {
            amministrazioneDAO.esisteTitoloInInvioFascicoliDestinatari(id);

        } catch (Exception de) {
            logger
                    .error("AmministrazioneDelegate: failed esisteTitoloInInvioFascicoliDestinatari: ");
        }
        return esiste;
    }

    public ArrayList getTipiProcedimento(int aooId, String descrizione) {

        try {
            return amministrazioneDAO.getTipiProcedimento(aooId, descrizione);
        } catch (DataException de) {
            logger
                    .error("SoggettoDelegate: failed getting getListaDistribuzione: ");
            return null;
        }
    }

}