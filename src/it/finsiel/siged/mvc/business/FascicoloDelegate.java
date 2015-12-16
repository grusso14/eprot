package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.Fascicolo;
import it.finsiel.siged.model.InvioFascicolo;
import it.finsiel.siged.model.documentale.Documento;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.FascicoloDAO;
import it.finsiel.siged.mvc.presentation.helper.FascicoloView;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.FaldoneVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioFascicoliDestinatariVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioFascicoliVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloFascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

/**
 * 
 * @author G.Calli Intersiel Spa
 */
public class FascicoloDelegate {

    private static Logger logger = Logger.getLogger(FascicoloDelegate.class
            .getName());

    private FascicoloDAO fascicoloDAO = null;

    private ServletConfig config = null;

    private static FascicoloDelegate delegate = null;

    private FascicoloDelegate() {
        try {
            if (fascicoloDAO == null) {
                fascicoloDAO = (FascicoloDAO) DAOFactory
                        .getDAO(Constants.FASCICOLO_DAO_CLASS);

                logger.debug("fascicoloDAO instantiated:"
                        + Constants.FASCICOLO_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }
    }

    public static FascicoloDelegate getInstance() {
        if (delegate == null)
            delegate = new FascicoloDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.FASCICOLO_DELEGATE;
    }

    public Collection getStatiFascicolo(String stati) {
        try {
            return fascicoloDAO.getStatiFascicolo(stati);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getStatiFascicolo: ");

            return null;
        }
    }

    public SortedMap getFascicoliArchivioInvio(int aooId) {
        try {

            return fascicoloDAO.getFascicoliArchivioInvio(aooId);
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoliArchivioInvio: ");
            return null;
        }

    }

    public Collection getProtocolliFascicolo(int fascicoloId, Utente utente) {
        try {
            return fascicoloDAO.getProtocolliFascicolo(fascicoloId, utente);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getProtocolliFascicolo: ");
            return null;
        }
    }

    public Collection getFascicoli(Utente utente, int progressivo, int anno,
            String oggetto, String note, String stato, int titolarioId,
            Date dataAperturaDa, Date dataAperturaA, Date dataEvidenzaDa,
            Date dataEvidenzaA, int ufficioId)

    {
        try {
            return fascicoloDAO.getFascicoli(utente, progressivo, anno,
                    oggetto, note, stato, titolarioId, dataAperturaDa,
                    dataAperturaA, dataEvidenzaDa, dataEvidenzaA, ufficioId);
        } catch (Exception de) {
            logger.error("FascicoloDelegate: failed getting getFascicoli: ");
            return null;
        }
    }

    public int contaFascicoli(Utente utente, int progressivo, int anno,
            String oggetto, String note, String stato, int titolarioId,
            Date dataAperturaDa, Date dataAperturaA, Date dataEvidenzaDa,
            Date dataEvidenzaA, int ufficioId)

    {
        try {
            return fascicoloDAO.contaFascicoli(utente, progressivo, anno,
                    oggetto, note, stato, titolarioId, dataAperturaDa,
                    dataAperturaA, dataEvidenzaDa, dataEvidenzaA, ufficioId);
        } catch (Exception de) {
            logger.error("FascicoloDelegate: failed getting contaFascicoli: ");
            return 0;
        }
    }

    public Collection getFascicoliByAooId(int aooId)

    {
        try {
            return fascicoloDAO.getFascicoloByAooId(aooId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoliByAooId: ");
            return null;
        }
    }

    public FascicoloVO getFascicoloVOById(int fascicoloId) {
        try {
            return fascicoloDAO.getFascicoloById(fascicoloId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoloById: ");
            return null;
        }
    }

    public FascicoloView getFascicoloViewById(int fascicoloId) {
        try {
            return fascicoloDAO.getFascicoloViewById(fascicoloId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoloById: ");
            return null;
        }
    }

    public FascicoloView getFascicoloViewById(Connection connection,
            int fascicoloId) {
        try {
            return fascicoloDAO.getFascicoloViewById(connection, fascicoloId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoloById: ");
            return null;
        }
    }

    public Fascicolo getFascicoloById(int fascicoloId) {

        try {
            Fascicolo f = new Fascicolo();
            f.setFascicoloVO(fascicoloDAO.getFascicoloById(fascicoloId));

            // gestione protocolli del fascicolo
            Collection protocolliFascicolo = new ArrayList();
            Iterator itProt = fascicoloDAO.getProtocolliFascicoloById(
                    fascicoloId).iterator();
            while (itProt.hasNext()) {
                Integer protocolloId = (Integer) itProt.next();
                ProtocolloFascicoloVO pfVO = new ProtocolloFascicoloVO();
                pfVO.setFascicoloId(fascicoloId);
                pfVO.setProtocolloId(protocolloId.intValue());
                protocolliFascicolo.add(pfVO);
            }

            f.setProtocolli(protocolliFascicolo);

            // gestione documenti del fascicolo
            Collection documentiFascicolo = new ArrayList();
            Iterator itDoc = fascicoloDAO
                    .getDocumentiFascicoloById(fascicoloId).iterator();
            DocumentaleDelegate dd = DocumentaleDelegate.getInstance();
            while (itDoc.hasNext()) {
                Integer documentoId = (Integer) itDoc.next();
                Documento doc = dd.getDocumentoById(documentoId.intValue());
                documentiFascicolo.add(doc.getFileVO());
            }

            f.setDocumenti(documentiFascicolo);

            // gestione faldoni associati al fascicolo
            Collection faldoniFascicolo = new ArrayList();
            Iterator itFal = fascicoloDAO.getFaldoniFascicoloById(fascicoloId)
                    .iterator();
            FaldoneDelegate fal = FaldoneDelegate.getInstance();
            while (itFal.hasNext()) {
                Integer faldoneId = (Integer) itFal.next();
                FaldoneVO faldoneVO = fal.getFaldone(faldoneId.intValue());
                faldoniFascicolo.add(faldoneVO);
            }

            f.setFaldoni(faldoniFascicolo);

            // gestione procedimenti associati al fascicolo
            Collection procedimentiFascicolo = new ArrayList();
            Iterator itProc = fascicoloDAO.getProcedimentiFascicoloById(
                    fascicoloId).iterator();
            ProcedimentoDelegate proc = ProcedimentoDelegate.getInstance();
            while (itProc.hasNext()) {
                Integer procedimentoId = (Integer) itProc.next();
                ProcedimentoVO procedimentoVO = proc
                        .getProcedimentoVO(procedimentoId.intValue());
                procedimentiFascicolo.add(procedimentoVO);
            }

            f.setProcedimenti(procedimentiFascicolo);

            return f;
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoloById: ");
            return null;
        }
    }

    public Collection getFascicoliByProtocolloId(int protocolloId) {
        try {
            return fascicoloDAO.getFascicoliByProtocolloId(protocolloId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoliByProtocolloId: ");
            return null;
        }
    }

    public Collection getFascicoliByDocumentoId(int documentoId) {
        try {
            return fascicoloDAO.getFascicoliByDocumentoId(documentoId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoliByDocumentoId: ");
            return null;
        }
    }

    public Collection getFascicoliByDocumentoId(Connection connection,
            int documentoId) throws DataException {
        return fascicoloDAO.getFascicoliByDocumentoId(connection, documentoId);
    }

    public Collection getStoriaFascicoliByDocumentoId(Connection connection,
            int documentoId, int versione) throws DataException {
        return fascicoloDAO.getStoriaFascicoliByDocumentoId(connection,
                documentoId, versione);
    }

    public Map getDestinatariFascicoliInvio(int fascicoloId) {
        try {
            return fascicoloDAO.getDestinatariFascicoliInvio(fascicoloId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getDestinatariFascicoliInvio: ");
            return null;
        }
    }

    public Map getDestinatariFascicoliInvio(Connection connection,
            int fascicoloId) {
        try {
            return fascicoloDAO.getDestinatariFascicoliInvio(connection,
                    fascicoloId);
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getDestinatariFascicoliInvio: ");
            return null;
        }
    }

    public void salvaFascicoloDocumento(FascicoloVO fascicoloVO,
            int documentoId, String userName) throws Exception {

        JDBCManager jdbcMan = null;
        Connection connection = null;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            salvaFascicoloDocumento(connection, fascicoloVO, documentoId,
                    userName);
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio FascicoloDocumento fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio FascicoloDocumento fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }

    }

    public int eliminaCodaInvioFascicolo(Connection connection, int fascicoloId)
            throws DataException {
        int recUpdate = 0;
        try {
            recUpdate = fascicoloDAO.eliminaCodaInvioFascicolo(connection,
                    fascicoloId);
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore eliminaCodaInvioFascicolo.");
        }
        return recUpdate;
    }

    public int aggiornaStatoFascicolo(Connection connection, int fascicoloId,
            int stato, String userName, int versione) throws DataException {
        int recUpdate = 0;
        try {
            recUpdate = fascicoloDAO.aggiornaStatoFascicolo(connection,
                    fascicoloId, stato, userName, versione);
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore aggiornaStatoFascicolo.");
        }
        return recUpdate;
    }

    public void salvaFascicoloDocumento(Connection connection,
            FascicoloVO fascicoloVO, int documentoId, String userName)
            throws Exception {
        Date dataCorrente = new Date(System.currentTimeMillis());
        try {
            if (fascicoloVO.getId().intValue() == 0) {
                fascicoloVO.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection, NomiTabelle.FASCICOLI));
                fascicoloVO
                        .setProgressivo(IdentificativiDelegate.getInstance()
                                .getNextId(
                                        connection,
                                        NomiTabelle.FASCICOLI
                                                + String.valueOf(fascicoloVO
                                                        .getAooId())
                                                + DateUtil
                                                        .getYear(dataCorrente)));

                fascicoloVO = fascicoloDAO
                        .newFascicolo(connection, fascicoloVO);
            } else {
                fascicoloVO = fascicoloDAO.getFascicoloById(connection,
                        fascicoloVO.getId().intValue());
            }
            fascicoloDAO.salvaDocumentoFascicolo(connection, fascicoloVO,
                    documentoId, userName);
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException(
                    "Errore nel salvataggio del Fascicolo-Documento.");
        }
    }

    public void salvaFascicoloProtocollo(Connection connection,
            FascicoloVO fascicoloVO, int protocolloId, String userName)
            throws Exception {
        FascicoloVO fascicoloSalvato = new FascicoloVO();
        int fascicoloId;
        try {
            if (fascicoloVO.getId().intValue() == 0) {
                fascicoloVO.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection, NomiTabelle.FASCICOLI));
                Date dataCorrente = new Date(System.currentTimeMillis());
                fascicoloVO.setDataUltimoMovimento(dataCorrente);
                fascicoloVO
                        .setProgressivo(IdentificativiDelegate.getInstance()
                                .getNextId(
                                        connection,
                                        NomiTabelle.FASCICOLI
                                                + String.valueOf(fascicoloVO
                                                        .getAooId())
                                                + DateUtil
                                                        .getYear(dataCorrente)));

                fascicoloSalvato = fascicoloDAO.newFascicolo(connection,
                        fascicoloVO);

                fascicoloId = fascicoloSalvato.getId().intValue();
            } else {
                fascicoloId = fascicoloVO.getId().intValue();
                // controllo la versione di fascicoloVO con quella presente in
                // base dati
                fascicoloVO = fascicoloDAO.getFascicoloById(connection,
                        fascicoloId);
            }
            // salvo i dati della relazione tra fascicolo e protocollo
            fascicoloDAO.salvaProtocolloFascicolo(connection, fascicoloId,
                    protocolloId, userName, fascicoloVO.getVersione());

            // modifico la data movimentazione del fascicolo
            Date dataCorrente = new Date(System.currentTimeMillis());
            fascicoloVO.setDataUltimoMovimento(dataCorrente);
            fascicoloDAO.aggiornaFascicolo(connection, fascicoloVO);

        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException(
                    "Errore nel salvataggio del Fascicolo-Protocollo.");
        }
    }

    public int cancellaFascicolo(int fascicoloId) throws Exception {
        try {
            if (fascicoloId > 0) {
                return fascicoloDAO.deleteFascicolo(fascicoloId);
            }
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore cancellaFascicolo.");
        }
        return ReturnValues.UNKNOWN;
    }

    public void rimuoviFascicoliProtocollo(Connection connection,
            int protocolloId) throws Exception {
        try {
            if (protocolloId > 0) {
                fascicoloDAO
                        .deleteFascicoliProtocollo(connection, protocolloId);
            }
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore rimuoviFascicoliProtocollo.");
        }
    }

    public void rimuoviDocumentoDaFascicolo(int fascicoloId, int documentoId,
            int versione) throws Exception {
        try {
            if (documentoId > 0) {
                fascicoloDAO.deleteDocumentoFascicolo(fascicoloId, documentoId,
                        versione);
            }
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore rimuoviDocumentoDaFascicolo.");
        }
    }

    public void rimuoviFascicoliDocumento(Connection connection, int documentoId)
            throws Exception {
        try {
            if (documentoId > 0) {
                fascicoloDAO.deleteFascicoliDocumento(connection, documentoId);
            }
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore rimuoviFascicoliDocumento.");
        }
    }

    public FascicoloVO salvaFascicolo(FascicoloVO fascicolo) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        FascicoloVO fascicoloSalvato = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            fascicoloSalvato = fascicoloDAO.aggiornaFascicolo(connection,
                    fascicolo);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("FascicoloDelegate: failed salvaFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return fascicoloSalvato;
    }

    public synchronized FascicoloVO nuovoFascicolo(FascicoloVO fascicolo) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        FascicoloVO fascicoloSalvato = new FascicoloVO();
        try {
            Date dataCorrente = new Date(System.currentTimeMillis());
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            fascicolo.setId(IdentificativiDelegate.getInstance().getNextId(
                    connection, NomiTabelle.FASCICOLI));
            fascicolo.setProgressivo(IdentificativiDelegate.getInstance()
                    .getNextId(
                            connection,
                            NomiTabelle.FASCICOLI
                                    + String.valueOf(fascicolo.getAooId())
                                    + DateUtil.getYear(dataCorrente)));

            fascicoloSalvato = fascicoloDAO.newFascicolo(connection, fascicolo);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("FascicoloDelegate: failed nuovoFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return fascicoloSalvato;
    }

    public int chiudiFascicolo(Fascicolo fascicolo, Utente utente)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("FascicoloDelegate:chiudiFascicolo");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            // i protocolli del fascicoli vanno agli ATTI
            // Iterator it = fascicolo.getProtocolli().iterator();
            // ProtocolloDelegate pd = ProtocolloDelegate.getInstance();
            // while (it.hasNext()) {
            // ProtocolloFascicoloVO pfVO = (ProtocolloFascicoloVO) it.next();
            // ProtocolloVO protocollo = pd.getProtocolloById(connection, pfVO
            // .getProtocolloId());
            // if ("I".equals(protocollo.getFlagTipo())
            // && !"A".equals(protocollo.getStatoProtocollo())) {
            // pd.updateScarico(protocollo, "A", utente, connection);
            // }
            // }
            fascicolo.getFascicoloVO().setStato(1);
            fascicolo.getFascicoloVO().setDataScarico(
                    new Date(System.currentTimeMillis()));
            fascicoloDAO.aggiornaFascicolo(connection, fascicolo
                    .getFascicoloVO());
            /*
             * statusFlag = fascicoloDAO.aggiornaStatoFascicolo(connection,
             * fascicolo.getFascicoloVO().getId().intValue(),
             * Parametri.STATO_FASCICOLO_CHIUSO, utente.getValueObject()
             * .getUsername(), fascicolo.getFascicoloVO() .getVersione());
             */

            // fascicoloDAO.aggiornaFascicolo(connection, fascicolo
            // .getFascicoloVO());
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            statusFlag = ReturnValues.INVALID;
            logger.error("FascicoloDelegate: failed chiudiFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int riapriFascicolo(Fascicolo fascicolo, Utente utente)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("FascicoloDelegate:riapriFascicolo");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            // i protocolli del fascicoli vanno in Lavorazione
            // Iterator it = fascicolo.getProtocolli().iterator();
            // ProtocolloDelegate pd = ProtocolloDelegate.getInstance();
            // while (it.hasNext()) {
            // ProtocolloFascicoloVO pfw = (ProtocolloFascicoloVO) it.next();
            // ProtocolloVO protocollo = pd.getProtocolloById(connection, pfw
            // .getProtocolloId());
            //
            // if ("I".equals(protocollo.getFlagTipo())
            // && !"N".equals(protocollo.getStatoProtocollo())) {
            // pd.updateScarico(protocollo, "N", utente, connection);
            // }
            // }
            fascicolo.getFascicoloVO().setStato(0);
            fascicolo.getFascicoloVO().setDataScarico(
                    new Date(System.currentTimeMillis()));

            fascicoloDAO.aggiornaFascicolo(connection, fascicolo
                    .getFascicoloVO());
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            statusFlag = ReturnValues.INVALID;
            logger.error("FascicoloDelegate: failed riapriFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int inviaFascicolo(InvioFascicolo invioFascicolo, String userName,
            int versione) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = 0;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            for (Iterator i = invioFascicolo.getDocumenti().iterator(); i
                    .hasNext();) {
                InvioFascicoliVO ifVO = (InvioFascicoliVO) i.next();
                ifVO.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.INVIO_FASCICOLI));

                fascicoloDAO.salvaDocumentiInvioFascicolo(connection, ifVO);
            }
            for (Iterator y = invioFascicolo.getDestinatariCollection()
                    .iterator(); y.hasNext();) {
                InvioFascicoliDestinatariVO ifdVO = new InvioFascicoliDestinatariVO();
                DestinatarioVO destinatario = (DestinatarioVO) y.next();
                ifdVO.setFascicoloId(invioFascicolo.getFascicoloId());
                ifdVO.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.INVIO_FASCICOLI_DESTINATARI));
                ifdVO.setDestinatario(destinatario);

                fascicoloDAO.salvaDestinatariInvioFascicolo(connection, ifdVO);
            }
            statusFlag = fascicoloDAO.aggiornaStatoFascicolo(connection,
                    invioFascicolo.getFascicoloId(),
                    Parametri.STATO_FASCICOLO_INVIATO, userName, versione);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("FascicoloDelegate: failed inviaFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int annullaInvioFascicolo(int fascicoloId, String userName,
            int versione) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = 0;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            fascicoloDAO.annullaInvioFascicolo(connection, fascicoloId,
                    versione);
            statusFlag = fascicoloDAO.aggiornaStatoFascicolo(connection,
                    fascicoloId, Parametri.STATO_FASCICOLO_APERTO, userName,
                    versione);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("FascicoloDelegate: failed annullaInvioFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int scartaFascicolo(int fascicoloId, String destinazioneScarto,
            String userName, int versione) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = 0;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            statusFlag = fascicoloDAO.scartaFascicolo(connection, fascicoloId,
                    destinazioneScarto, userName, versione);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            statusFlag = ReturnValues.INVALID;
            logger.error("FascicoloDelegate: failed scartaFascicolo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public boolean esisteFascicoloInCodaInvio(int fascicoloId) {
        try {
            return fascicoloDAO.esisteFascicoloInCodaInvio(fascicoloId);
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed esisteFascicoloInCodaInvio: ");
        }
        return false;
    }

    public InvioFascicolo getFascicoloInviatoById(int fascicoloId) {
        try {
            InvioFascicolo fInviato = new InvioFascicolo();
            fInviato.setFascicoloId(fascicoloId);

            // gestione protocolli del fascicolo
            Collection protocolliFascicolo = new ArrayList();
            Iterator itProt = fascicoloDAO.getProtocolliFascicoloById(
                    fascicoloId).iterator();
            ProtocolloDelegate pd = ProtocolloDelegate.getInstance();
            while (itProt.hasNext()) {
                Integer protocolloId = (Integer) itProt.next();
                ProtocolloVO pVO = pd
                        .getProtocolloById(protocolloId.intValue());
                protocolliFascicolo.add(pVO);
            }

            fInviato.setProtocolli(protocolliFascicolo);

            // gestione documenti del fascicolo
            Collection documentiFascicolo = new ArrayList();
            Iterator itDoc = fascicoloDAO.getDocumentiFascicoliInvio(
                    fascicoloId).iterator();
            while (itDoc.hasNext()) {
                InvioFascicoliVO ifVO = (InvioFascicoliVO) itDoc.next();
                documentiFascicolo.add(ifVO);
            }

            fInviato.setDocumenti(documentiFascicolo);

            // destinatari fascicolo
            Map destinatari = FascicoloDelegate.getInstance()
                    .getDestinatariFascicoliInvio(fascicoloId);
            fInviato.setDestinatari(destinatari);

            return fInviato;
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoloInviatoById: ");
            return null;
        }
    }

    public Collection getStoriaFascicolo(int fascicoloId) {
        try {
            return fascicoloDAO.getStoriaFascicolo(fascicoloId);
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed getting getStoriaFascicolo: ");
            return null;
        }
    }

    public int salvaProtocolliFascicolo(FascicoloVO fascicoloVO,
            String[] protocolli, String userName) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            if (protocolli != null) {
                // salvo i dati della relazione tra fascicolo e protocollo
                fascicoloVO.setDataUltimoMovimento(new Date(System
                        .currentTimeMillis()));
                fascicoloVO = fascicoloDAO.aggiornaFascicolo(connection,
                        fascicoloVO);

                for (int i = 0; i < protocolli.length; i++) {
                    fascicoloDAO.deleteFascicoloProtocollo(connection,
                            (new Integer(protocolli[i])).intValue(),
                            fascicoloVO.getId().intValue());
                    fascicoloDAO.salvaProtocolloFascicolo(connection,
                            fascicoloVO.getId().intValue(), (new Integer(
                                    protocolli[i])).intValue(), userName,
                            fascicoloVO.getVersione());
                }
            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed salvaProtocolliFascicolo: ");
            jdbcMan.rollback(connection);

        } catch (SQLException se) {
            logger
                    .error("FascicoloDelegate: failed salvaProtocolliFascicolo: ");
            jdbcMan.rollback(connection);

        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int eliminaProtocolliFascicolo(FascicoloVO fascicoloVO,
            String[] protocolli, String userName) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            if (protocolli != null) {
                // salvo i dati della relazione tra fascicolo e protocollo
                fascicoloVO.setDataUltimoMovimento(new Date(System
                        .currentTimeMillis()));
                fascicoloDAO.aggiornaFascicolo(connection, fascicoloVO);

                for (int i = 0; i < protocolli.length; i++) {
                    fascicoloDAO.deleteFascicoloProtocollo(connection,
                            (new Integer(protocolli[i])).intValue(),
                            fascicoloVO.getId().intValue());
                }
            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed eliminaProtocolliFascicolo: ");
            jdbcMan.rollback(connection);

        } catch (SQLException se) {
            logger
                    .error("FascicoloDelegate: failed eliminaProtocolliFascicolo: ");
            jdbcMan.rollback(connection);

        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int salvaProcedimentiFascicolo(FascicoloVO fascicoloVO,
            String[] procedimenti, String userName) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            if (procedimenti != null) {
                // salvo i dati della relazione tra fascicolo e procedimento
                fascicoloVO.setDataUltimoMovimento(new Date(System
                        .currentTimeMillis()));
                fascicoloDAO.aggiornaFascicolo(connection, fascicoloVO);

                for (int i = 0; i < procedimenti.length; i++) {
                    fascicoloDAO.deleteFascicoloProcedimento(connection,
                            (new Integer(procedimenti[i])).intValue(),
                            fascicoloVO.getId().intValue());
                    fascicoloDAO.salvaProcedimentoFascicolo(connection,
                            fascicoloVO.getId().intValue(), (new Integer(
                                    procedimenti[i])).intValue(), userName,
                            fascicoloVO.getVersione() + 1);
                }
            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed salvaProcedimentiFascicolo: ");
            jdbcMan.rollback(connection);

        } catch (SQLException se) {
            logger
                    .error("FascicoloDelegate: failed salvaProcedimentiFascicolo: ");
            jdbcMan.rollback(connection);

        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int eliminaProcedimentiFascicolo(FascicoloVO fascicoloVO,
            String[] procedimenti, String userName) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            if (procedimenti != null) {
                // salvo i dati della relazione tra fascicolo e protocollo
                fascicoloVO.setDataUltimoMovimento(new Date(System
                        .currentTimeMillis()));
                fascicoloDAO.aggiornaFascicolo(connection, fascicoloVO);

                for (int i = 0; i < procedimenti.length; i++) {
                    fascicoloDAO.deleteFascicoloProcedimento(connection,
                            (new Integer(procedimenti[i])).intValue(),
                            fascicoloVO.getId().intValue());
                }
            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed eliminaProcedimentiFascicolo: ");
            jdbcMan.rollback(connection);

        } catch (SQLException se) {
            logger
                    .error("FascicoloDelegate: failed eliminaProcedimentiFascicolo: ");
            jdbcMan.rollback(connection);

        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    // STORIA FASCICOLO

    public Fascicolo getFascicoloByIdVersione(int fascicoloId, int versione) {

        try {
            Fascicolo f = new Fascicolo();
            f.setFascicoloVO(fascicoloDAO.getFascicoloByIdVersione(fascicoloId,
                    versione));

            // gestione protocolli del fascicolo
            Collection protocolliFascicolo = new ArrayList();
            Iterator itProt = fascicoloDAO.getProtocolliFascicoloByIdVersione(
                    fascicoloId, versione).iterator();
            while (itProt.hasNext()) {
                Integer protocolloId = (Integer) itProt.next();
                ProtocolloFascicoloVO pfVO = new ProtocolloFascicoloVO();
                pfVO.setFascicoloId(fascicoloId);
                pfVO.setProtocolloId(protocolloId.intValue());
                protocolliFascicolo.add(pfVO);
            }

            f.setProtocolli(protocolliFascicolo);

            // gestione documenti del fascicolo
            Collection documentiFascicolo = new ArrayList();
            Iterator itDoc = fascicoloDAO.getDocumentiFascicoloByIdVersione(
                    fascicoloId, versione).iterator();
            DocumentaleDelegate dd = DocumentaleDelegate.getInstance();
            while (itDoc.hasNext()) {
                Integer documentoId = (Integer) itDoc.next();
                Documento doc = dd.getDocumentoById(documentoId.intValue());
                documentiFascicolo.add(doc.getFileVO());
            }

            f.setDocumenti(documentiFascicolo);

            // gestione faldoni associati al fascicolo
            Collection faldoniFascicolo = new ArrayList();
            Iterator itFal = fascicoloDAO.getFaldoniFascicoloByIdVersione(
                    fascicoloId, versione).iterator();
            FaldoneDelegate fal = FaldoneDelegate.getInstance();
            while (itFal.hasNext()) {
                Integer faldoneId = (Integer) itFal.next();
                FaldoneVO faldoneVO = fal.getFaldone(faldoneId.intValue());
                faldoniFascicolo.add(faldoneVO);
            }

            f.setFaldoni(faldoniFascicolo);

            // gestione procedimenti associati al fascicolo
            Collection procedimentiFascicolo = new ArrayList();
            Iterator itProc = fascicoloDAO
                    .getProcedimentiFascicoloByIdVersione(fascicoloId, versione)
                    .iterator();
            ProcedimentoDelegate proc = ProcedimentoDelegate.getInstance();
            while (itProc.hasNext()) {
                Integer procedimentoId = (Integer) itProc.next();
                ProcedimentoVO procedimentoVO = proc
                        .getProcedimentoVO(procedimentoId.intValue());
                procedimentiFascicolo.add(procedimentoVO);
            }

            f.setProcedimenti(procedimentiFascicolo);

            return f;
        } catch (Exception de) {
            logger
                    .error("FascicoloDelegate: failed getting getFascicoloByIdVersione: ");
            return null;
        }
    }

    public boolean isUtenteAbilitatoView(Utente utente, Ufficio uff,
            int fascicoloId) {
        try {
            return fascicoloDAO.isUtenteAbilitatoView(utente, uff, fascicoloId);
        } catch (DataException de) {
            logger
                    .error("FascicoloDelegate: failed getting getStoriaFascicolo: ");
            return false;
        }
    }

}