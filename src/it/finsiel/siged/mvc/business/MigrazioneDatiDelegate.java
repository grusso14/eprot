package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.FaldoneDAO;
import it.finsiel.siged.mvc.integration.FascicoloDAO;
import it.finsiel.siged.mvc.integration.IdentificativiDAO;
import it.finsiel.siged.mvc.integration.ProcedimentoDAO;
import it.finsiel.siged.mvc.integration.ProtocolloDAO;
import it.finsiel.siged.mvc.integration.SoggettoDAO;
import it.finsiel.siged.mvc.integration.TitolarioDAO;
import it.finsiel.siged.mvc.integration.UfficioDAO;
import it.finsiel.siged.mvc.integration.UtenteDAO;
import it.finsiel.siged.mvc.vo.ListaDistribuzioneVO;
import it.finsiel.siged.mvc.vo.MigrazioneVO;
import it.finsiel.siged.mvc.vo.RubricaListaVO;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.lookup.TipoProcedimentoVO;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO.Indirizzo;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoMenuVO;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoRegistroVO;
import it.finsiel.siged.mvc.vo.organizzazione.ReferenteUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.TitolarioUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.FaldoneFascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.FaldoneVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoFaldoneVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoFascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloFascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

/**
 * @author M.Spadafora
 */
public class MigrazioneDatiDelegate {

    private static Logger logger = Logger
            .getLogger(MigrazioneDatiDelegate.class.getName());

    private UfficioDAO ufficioDAO;

    private UtenteDAO utenteDAO;

    private TitolarioDAO titolarioDAO;

    private ProtocolloDAO protocolloDAO;

    private FaldoneDAO faldoneDAO;

    private FascicoloDAO fascicoloDAO;

    private ProcedimentoDAO procedimentoDAO;

    private IdentificativiDAO identificativiDAO;

    private SoggettoDAO soggettoDAO;

    private ServletConfig config = null;

    private static MigrazioneDatiDelegate delegate = null;

    private HashMap protocolliDuplicati = new HashMap();

    private MigrazioneDatiDelegate() {
        try {
            ufficioDAO = (UfficioDAO) DAOFactory
                    .getDAO(Constants.UFFICIO_DAO_CLASS);
            logger.info("ufficioDAO instantiated: "
                    + Constants.UFFICIO_DAO_CLASS);
            utenteDAO = (UtenteDAO) DAOFactory
                    .getDAO(Constants.UTENTE_DAO_CLASS);
            logger
                    .info("utenteDAO instantiated: "
                            + Constants.UTENTE_DAO_CLASS);
            titolarioDAO = (TitolarioDAO) DAOFactory
                    .getDAO(Constants.TITOLARIO_DAO_CLASS);
            logger.info("titolarioDAO instantiated: "
                    + Constants.TITOLARIO_DAO_CLASS);
            protocolloDAO = (ProtocolloDAO) DAOFactory
                    .getDAO(Constants.PROTOCOLLO_DAO_CLASS);
            logger.info("protocolloDAO instantiated: "
                    + Constants.PROTOCOLLO_DAO_CLASS);

            faldoneDAO = (FaldoneDAO) DAOFactory
                    .getDAO(Constants.FALDONE_DAO_CLASS);
            logger.info("faldoneDAO instantiated: "
                    + Constants.FALDONE_DAO_CLASS);

            fascicoloDAO = (FascicoloDAO) DAOFactory
                    .getDAO(Constants.FASCICOLO_DAO_CLASS);
            logger.info("fascicoloDAO instantiated: "
                    + Constants.FASCICOLO_DAO_CLASS);

            procedimentoDAO = (ProcedimentoDAO) DAOFactory
                    .getDAO(Constants.PROCEDIMENTO_DAO_CLASS);

            logger.info("procedimentoDAO instantiated: "
                    + Constants.PROCEDIMENTO_DAO_CLASS);

            identificativiDAO = (IdentificativiDAO) DAOFactory
                    .getDAO(Constants.IDENTIFICATIVI_DAO_CLASS);

            logger.info("identificativiDAO instantiated: "
                    + Constants.IDENTIFICATIVI_DAO_CLASS);

            soggettoDAO = (SoggettoDAO) DAOFactory
                    .getDAO(Constants.SOGGETTO_DAO_CLASS);
            logger.info("soggettoDAO instantiated: "
                    + Constants.SOGGETTO_DAO_CLASS);

        } catch (Exception e) {
            logger.error("Exception while connecting to DAOFactory", e);
        }

    }

    public static MigrazioneDatiDelegate getInstance() {
        if (delegate == null)
            delegate = new MigrazioneDatiDelegate();
        return delegate;
    }

    private int stringToInt(String s) {
        int i;
        if (s == null || s.equals("")) {
            i = 0;
        } else {
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                i = -1;
            }
        }
        return i;
    }

    private void salvaUffici(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica UFFICI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        UfficioVO ufficio = null;
        while ((riga = lnReader.readLine()) != null) {
            ufficio = new UfficioVO();
            String[] campi = riga.split("\t");
            ufficio.setId(NumberUtil.getInt(campi[0]));
            ufficio.setDescription(campi[1]);
            ufficio.setParentId(stringToInt(campi[2]));
            ufficio.setAooId(stringToInt(campi[3]));
            ufficio.setRowCreatedTime(DateUtil.toDate(campi[4]));
            ufficio.setRowCreatedUser(campi[5]);
            ufficio.setRowUpdatedUser(campi[6]);
            ufficio.setRowUpdatedTime(DateUtil.toDate(campi[7]));
            ufficio.setVersione(stringToInt(campi[8]));
            ufficio.setTipo(campi[9]);
            ufficio.setFlagAttivo(stringToInt(campi[10]));
            ufficio.setFlagAccetazioneAutomatica(stringToInt(campi[11]));
            ufficioDAO.nuovoUfficio(connection, ufficio);

        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        if (ufficio != null) {
            identificativiDAO.updateId(connection, NomiTabelle.UFFICI, ufficio
                    .getId().intValue(), 1);
        }
        lnReader.close();
    }

    private void salvaFascicoli(Connection connection, boolean storia,
            Reader reader) throws Throwable {
        logger.info("--- Carica FASCICOLI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);

        String riga;
        FascicoloVO fascicolo = null;
        while ((riga = lnReader.readLine()) != null) {
            fascicolo = new FascicoloVO();
            String[] campi = StringUtil.split(riga, '\t');
            fascicolo.setId(NumberUtil.getInt(campi[0]));
            fascicolo.setAooId(stringToInt(campi[1]));
            fascicolo.setCodice(campi[2]);
            fascicolo.setDescrizione(campi[3]);
            fascicolo.setNome(campi[4]);
            fascicolo.setProgressivo(stringToInt(campi[5]));
            fascicolo.setNote(campi[6]);
            fascicolo.setProcessoId(NumberUtil.getInt(campi[7]));
            fascicolo.setOggetto(campi[8]);
            fascicolo.setRegistroId(stringToInt(campi[9]));
            fascicolo.setDataApertura(DateUtil.toDate(campi[10]));
            fascicolo.setDataChiusura(DateUtil.toDate(campi[11]));
            fascicolo.setStato(stringToInt(campi[12]));
            fascicolo.setRowCreatedTime(DateUtil.toDate(campi[13]));
            fascicolo.setRowCreatedUser(campi[14]);
            fascicolo.setRowUpdatedUser(campi[15]);
            fascicolo.setRowUpdatedTime(DateUtil.toDate(campi[16]));
            fascicolo.setTitolarioId(stringToInt(campi[17]));
            fascicolo.setVersione(stringToInt(campi[18]));
            fascicolo.setUfficioIntestatarioId(stringToInt(campi[19]));
            fascicolo.setUtenteIntestatarioId(stringToInt(campi[20]));
            fascicolo.setUfficioResponsabileId(stringToInt(campi[21]));
            fascicolo.setUtenteResponsabileId(stringToInt(campi[22]));
            fascicolo.setDataEvidenza(DateUtil.toDate(campi[23]));
            fascicolo.setAnnoRiferimento(NumberUtil.getInt(campi[24]));
            fascicolo.setTipoFascicolo(NumberUtil.getInt(campi[25]));
            fascicolo.setDataUltimoMovimento(DateUtil.toDate(campi[26]));
            fascicolo.setDataScarto(DateUtil.toDate(campi[27]));
            fascicolo.setDataCarico(DateUtil.toDate(campi[28]));
            fascicolo.setDataScarico(DateUtil.toDate(campi[29]));
            fascicolo.setCollocazioneLabel1(campi[30]);
            fascicolo.setCollocazioneLabel2(campi[31]);
            fascicolo.setCollocazioneLabel3(campi[32]);
            fascicolo.setCollocazioneLabel4(campi[33]);
            fascicolo.setCollocazioneValore1(campi[34]);
            fascicolo.setCollocazioneValore2(campi[35]);
            fascicolo.setCollocazioneValore3(campi[36]);
            fascicolo.setCollocazioneValore4(campi[37]);
            if (storia) {
                fascicoloDAO.newFascicoloStoria(connection, fascicolo);
                // logger.debug("storia_id=" + fascicolo.getId());
            } else {
                fascicoloDAO.newFascicolo(connection, fascicolo);
                // logger.debug("id=" + fascicolo.getId());

            }
        }
        if (storia) {
            logger.info("records elaborati:=" + lnReader.getLineNumber());
        } else {
            logger.info("records elaborati:=" + lnReader.getLineNumber());
        }

        if (!storia && fascicolo != null) {
            identificativiDAO.insertNewId(connection, NomiTabelle.FASCICOLI,
                    fascicolo.getId().intValue());
            Collection fascicoli = new ArrayList();
            fascicoli = fascicoloDAO.getFascicoliAnnoNumero(connection);
            for (Iterator iter = fascicoli.iterator(); iter.hasNext();) {
                MigrazioneVO element = (MigrazioneVO) iter.next();
                identificativiDAO.insertNewId(connection, NomiTabelle.FASCICOLI
                        + element.getAnno(), element.getNumero());

            }

        }
        lnReader.close();
    }

    private void salvaFaldoni(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica FALDONI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);

        String riga;
        FaldoneVO faldone = null;
        while ((riga = lnReader.readLine()) != null) {
            faldone = new FaldoneVO();
            String[] campi = StringUtil.split(riga, '\t');
            faldone.setId(NumberUtil.getInt(campi[0]));
            faldone.setAooId(stringToInt(campi[1]));
            faldone.setOggetto(campi[2]);
            faldone.setUfficioId(NumberUtil.getInt(campi[3]));
            faldone.setTitolarioId(NumberUtil.getInt(campi[4]));
            faldone.setRowCreatedTime(DateUtil.toDate(campi[5]));
            faldone.setRowCreatedUser(campi[6]);
            faldone.setRowUpdatedUser(campi[7]);
            faldone.setRowUpdatedTime(DateUtil.toDate(campi[8]));
            faldone.setCodiceLocale(campi[9]);
            faldone.setSottoCategoria(campi[10]);
            faldone.setNota(campi[11]);
            faldone.setNumeroFaldone(campi[12]);
            faldone.setAnno(NumberUtil.getInt(campi[13]));
            faldone.setNumero(NumberUtil.getInt(campi[14]));
            faldone.setDataCreazione(DateUtil.toDate(campi[15]));
            faldone.setDataCarico(DateUtil.toDate(campi[16]));
            faldone.setDataScarico(DateUtil.toDate(campi[17]));
            faldone.setDataEvidenza(DateUtil.toDate(campi[18]));
            faldone.setDataMovimento(DateUtil.toDate(campi[19]));
            faldone.setStato(campi[20]);
            faldone.setVersione(stringToInt(campi[21]));
            faldone.setCollocazioneLabel1(campi[22]);
            faldone.setCollocazioneLabel2(campi[23]);
            faldone.setCollocazioneLabel3(campi[24]);
            faldone.setCollocazioneLabel4(campi[25]);
            faldone.setCollocazioneValore1(campi[26]);
            faldone.setCollocazioneValore2(campi[27]);
            faldone.setCollocazioneValore3(campi[28]);
            faldone.setCollocazioneValore4(campi[29]);
            faldoneDAO.newFaldone(connection, faldone);
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        if (faldone != null) {
            identificativiDAO.insertNewId(connection, NomiTabelle.FALDONI,
                    faldone.getId().intValue());
            Collection faldoni = new ArrayList();
            faldoni = faldoneDAO.getFaldoniAnnoNumero(connection);
            for (Iterator iter = faldoni.iterator(); iter.hasNext();) {
                MigrazioneVO element = (MigrazioneVO) iter.next();
                identificativiDAO.insertNewId(connection, NomiTabelle.FALDONI
                        + element.getAnno(), element.getNumero());

            }

        }
        lnReader.close();
    }

    private ProtocolloVO getProtocollobyNumero(String numero,
            Connection connection) throws Throwable {

        int anno = Integer.parseInt(numero.substring(0, 4));
        int numeroProtocollo = Integer.parseInt(numero.substring(4));
        // if (protocolliDuplicati.containsKey(new Integer(numeroProtocollo))) {
        // numeroProtocollo = ((Integer) protocolliDuplicati.get(new Integer(
        // numeroProtocollo))).intValue();
        // }

        ProtocolloVO protocolloVO = protocolloDAO.getProtocolloByNumero(
                connection, anno, 1, numeroProtocollo);
        return (protocolloVO);
    }

    private FaldoneVO getFaldonebyNumero(String numero, Connection connection)
            throws Throwable {

        int anno = Integer.parseInt(numero.substring(0, 4));
        int numeroFaldone = Integer.parseInt(numero.substring(4));
        FaldoneVO faldoneVO = faldoneDAO.getFaldone(connection, anno,
                numeroFaldone);
        return (faldoneVO);
    }

    public FascicoloVO getFascicolobyNumero(String numero, Connection connection)
            throws Throwable {

        int anno = Integer.parseInt(numero.substring(0, 4));
        int numeroFascicolo = Integer.parseInt(numero.substring(4));

        FascicoloVO fascicoloVO = fascicoloDAO.getFascicoloByProgressivo(
                connection, anno, numeroFascicolo);
        return (fascicoloVO);
    }

    private ProcedimentoVO getProcedimentobyNumero(String numero,
            Connection connection) throws Throwable {

        int anno = Integer.parseInt(numero.substring(0, 4));
        int numeroProcedimento = Integer.parseInt(numero.substring(4));
        ProcedimentoVO procedimentoVO = procedimentoDAO
                .getProcedimentoByAnnoNumero(connection, anno,
                        numeroProcedimento);
        return (procedimentoVO);
    }

    private void salvaFaldoniFascicoli(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica FALDONI FASCICOLI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        FaldoneVO faldoneVO;
        FascicoloVO fascicoloVO;
        FaldoneFascicoloVO faldoneFascicolo = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            faldoneFascicolo = new FaldoneFascicoloVO();
            faldoneVO = new FaldoneVO();
            faldoneVO = getFaldonebyNumero(campi[0], connection);
            fascicoloVO = new FascicoloVO();
            fascicoloVO = getFascicolobyNumero(campi[1], connection);
            // faldoneFascicolo.setRowCreatedTime(DateUtil.toDate(campi[2]));
            // faldoneFascicolo.setRowCreatedUser(campi[3]);
            // faldoneFascicolo.setRowUpdatedUser(campi[4]);
            // faldoneFascicolo.setRowUpdatedTime(DateUtil.toDate(campi[5]));
            // faldoneFascicolo.setVersione(stringToInt(campi[6]));
            if ((fascicoloVO.getReturnValue() == ReturnValues.FOUND)
                    && (faldoneVO.getReturnValue() == ReturnValues.FOUND)) {
                faldoneFascicolo.setFaldoneId(faldoneVO.getId().intValue());
                faldoneFascicolo.setFascicoloId(fascicoloVO.getId().intValue());
                faldoneDAO.insertFaldoneFascicolo(connection, faldoneFascicolo,
                        fascicoloVO.getVersione());
//                logger.debug("utente= " + faldoneFascicolo.getRowCreatedUser()
//                        + faldoneFascicolo.getFaldoneId() + " faldone_id="
//                        + faldoneFascicolo.getFascicoloId() + " fascicolo_id=");
            } else {
                logger.error("Scartato faldone fascicolo:  faldone_id="
                        + campi[0] + " fascicolo_id=" + campi[1]);
            }
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaFascicoliProtocolli(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica FASCICOLI PROTOCOLLI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        ProtocolloFascicoloVO protocollofascicolo = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            protocollofascicolo = new ProtocolloFascicoloVO();
            ProtocolloVO protocolloVO = new ProtocolloVO();
            FascicoloVO fascicoloVO = new FascicoloVO();
            protocolloVO = getProtocollobyNumero(campi[0], connection);
            fascicoloVO = getFascicolobyNumero(campi[1], connection);
            if ((protocolloVO.getReturnValue() == ReturnValues.FOUND)
                    && (fascicoloVO.getReturnValue() == ReturnValues.FOUND)) {
                protocollofascicolo.setProtocolloId(protocolloVO.getId()
                        .intValue());
                protocollofascicolo.setFascicoloId(fascicoloVO.getId()
                        .intValue());
                fascicoloDAO.salvaProtocolloFascicolo(connection,
                        protocollofascicolo);

            } else {
                logger.error("Scartato fascicolo protocollo:  protocollo_id="
                        + campi[0] + " fascicolo_id=" + campi[1]);

            }
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaProcedimentiFascicoli(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica PROCEDIMENTI FASCICOLI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        ProcedimentoFascicoloVO procedimentofascicolo = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            procedimentofascicolo = new ProcedimentoFascicoloVO();
            ProcedimentoVO procedimentoVO = new ProcedimentoVO();
            FascicoloVO fascicoloVO = new FascicoloVO();
            procedimentoVO = getProcedimentobyNumero(campi[0], connection);
            fascicoloVO = getFascicolobyNumero(campi[1], connection);
            if (procedimentoVO.getReturnValue() == ReturnValues.FOUND
                    && (fascicoloVO.getReturnValue() == ReturnValues.FOUND)) {
                procedimentofascicolo.setProcedimentoId(procedimentoVO.getId()
                        .intValue());
                procedimentofascicolo.setFascicoloId(fascicoloVO.getId()
                        .intValue());
                procedimentoDAO.inserisciFascicoloProcedimento(connection,
                        procedimentofascicolo);
            } else {
                logger.error("Scartato  "

                + campi[0] + " Procedimento_id=" + campi[1] + " fascicolo_id=");

            }
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaProcedimentiFaldoni(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica PROCEDIMENTI FALDONI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        ProcedimentoFaldoneVO procedimentofaldone = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            procedimentofaldone = new ProcedimentoFaldoneVO();
            ProcedimentoVO procedimentoVO = new ProcedimentoVO();
            FaldoneVO faldoneVO = new FaldoneVO();
            procedimentoVO = getProcedimentobyNumero(campi[0], connection);
            faldoneVO = getFaldonebyNumero(campi[1], connection);
            if ((procedimentoVO.getReturnValue() == ReturnValues.FOUND)
                    && (faldoneVO.getReturnValue() == ReturnValues.FOUND)) {
                procedimentofaldone.setProcedimentoId(procedimentoVO.getId()
                        .intValue());
                procedimentofaldone.setFaldoneId(faldoneVO.getId().intValue());
                procedimentoDAO.inserisciProcedimentoFaldone(connection,
                        procedimentofaldone);
            } else {
                logger.error("Scartato  "

                + campi[0] + " Procedimento_id=" + campi[1] + " faldone_id=");
            }
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaTipoProcedimento(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica TIPI PROCEDIMENTO ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        TipoProcedimentoVO tipoProcedimento = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            tipoProcedimento = new TipoProcedimentoVO();
            tipoProcedimento.setIdTipo(stringToInt(campi[0]));
            tipoProcedimento.setIdUfficio(stringToInt(campi[1]));
            tipoProcedimento.setDescrizione(campi[2]);
            procedimentoDAO.salvaTipoProcedimento(connection, tipoProcedimento);
        }

        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaTitolariUffici(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica TITOLARIO UFFICI---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        TitolarioUfficioVO titolarioufficio = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            titolarioufficio = new TitolarioUfficioVO();
            titolarioufficio.setUfficioId(stringToInt(campi[0]));
            titolarioufficio.setTitolarioId(stringToInt(campi[1]));
            titolarioufficio.setRowCreatedTime(DateUtil.toDate(campi[2]));
            titolarioufficio.setRowCreatedUser(campi[3]);
            titolarioufficio.setRowUpdatedUser(campi[4]);
            titolarioufficio.setRowUpdatedTime(DateUtil.toDate(campi[5]));
            titolarioufficio.setVersione(stringToInt(campi[6]));
            ufficioDAO.salvaUfficiTitolari(connection, titolarioufficio);
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaUtenti(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica UTENTI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        UtenteVO utente = null;
        while ((riga = lnReader.readLine()) != null) {
            utente = new UtenteVO();
            String[] campi = riga.split("\t");
            utente.setId(stringToInt(campi[0]));
            utente.setUsername(campi[1]);
            utente.setEmailAddress(campi[2]);
            utente.setCognome(campi[3]);
            utente.setNome(campi[4]);
            utente.setCodiceFiscale(campi[5]);
            utente.setMatricola(campi[6]);
            utente.setPassword(campi[7]);
            utente.setDataFineAttivita(DateUtil.toDate(campi[8]));
            utente.setAooId(stringToInt(campi[9]));
            utente.setRowCreatedTime(DateUtil.toDate(campi[10]));
            utente.setRowCreatedUser(campi[11]);
            utente.setRowUpdatedUser(campi[12]);
            utente.setRowUpdatedTime(DateUtil.toDate(campi[13]));
            utente.setVersione(stringToInt(campi[14]));
            utente.setAbilitato(stringToInt(campi[15]) == 1);
            utenteDAO.newUtenteVO(connection, utente);
        }
        if (utente != null) {
            identificativiDAO.updateId(connection, NomiTabelle.UTENTI, utente
                    .getId().intValue(), 1);
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaPermessiUtente(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica PERMESSI UTENTI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        PermessoMenuVO permesso = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            permesso = new PermessoMenuVO();
            permesso.setUtenteId(stringToInt(campi[0]));
            permesso.setUfficioId(stringToInt(campi[1]));
            permesso.setMenuId(stringToInt(campi[2]));
            permesso.setRowCreatedTime(DateUtil.toDate(campi[3]));
            permesso.setRowCreatedUser(campi[4]);
            permesso.setRowUpdatedUser(campi[5]);
            permesso.setRowUpdatedTime(DateUtil.toDate(campi[6]));
            permesso.setVersione(stringToInt(campi[7]));
            utenteDAO.nuovoPermessoMenu(connection, permesso);
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();

    }

    private void salvaUtentiRegistri(Connection connection, Reader reader)
            throws Throwable {

        logger.info("--- Carica UTENTI REGISTRI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        PermessoRegistroVO permesso = null;
        String riga;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            permesso = new PermessoRegistroVO();
            permesso.setUtenteId(stringToInt(campi[0]));
            permesso.setRegistroId(stringToInt(campi[1]));
            permesso.setRowCreatedTime(DateUtil.toDate(campi[2]));
            permesso.setRowCreatedUser(campi[3]);
            String updatedUser = campi[4];
            if (updatedUser == null) {
                updatedUser = permesso.getRowCreatedUser();
            }
            permesso.setRowUpdatedUser(updatedUser);
            Date updatedDate = DateUtil.toDate(campi[5]);
            if (updatedDate == null) {
                updatedDate = new Date();
            }
            permesso.setRowUpdatedTime(updatedDate);
            permesso.setVersione(stringToInt(campi[6]));
            utenteDAO.nuovoPermessoRegistro(connection, permesso);
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaReferentiUfficio(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica REFERENTI UFFICIO ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        ReferenteUfficioVO referenteufficio = null;
        String riga;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            referenteufficio = new ReferenteUfficioVO();
            referenteufficio.setUtenteId(stringToInt(campi[0]));
            referenteufficio.setUfficioId(stringToInt(campi[1]));

            ufficioDAO.inserisciUtentiReferenti(connection, referenteufficio);
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaProtocolloProcedimenti(Connection connection,
            Reader reader) throws Throwable {
        logger.info("--- Carica PROTOCOLLO PROCEDIMENTI---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        ProtocolloProcedimentoVO protocolloProcedimento = null;
        String riga;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            if (campi.length == 3 && stringToInt(campi[2]) == 1) {
                protocolloProcedimento = new ProtocolloProcedimentoVO();

                ProcedimentoVO procedimentoVO = new ProcedimentoVO();
                ProtocolloVO protocolloVO = new ProtocolloVO();
                procedimentoVO = getProcedimentobyNumero(campi[0], connection);
                protocolloVO = getProtocollobyNumero(campi[1], connection);
                if ((procedimentoVO.getReturnValue() == ReturnValues.FOUND)
                        && (protocolloVO.getReturnValue() == ReturnValues.FOUND)) {
                    protocolloProcedimento.setProcedimentoId(procedimentoVO
                            .getId().intValue());
                    protocolloProcedimento.setProtocolloId(protocolloVO.getId()
                            .intValue());
                    protocolloDAO.inserisciProcedimenti(connection,
                            protocolloProcedimento);
                } else {

                    logger.error("Scartato  "

                    + campi[0] + " =Procedimento_id" + campi[1]
                            + "= Protocollo_id=");

                }
            }

        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaTitolario(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica TITOLARIO ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        TitolarioVO titolario = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            titolario = new TitolarioVO();
            titolario.setId(stringToInt(campi[0]));
            titolario.setCodice(campi[1]);
            titolario.setDescrizione(campi[2]);
            titolario.setParentId(stringToInt(campi[3]));
            titolario.setAooId(stringToInt(campi[4]));
            titolario.setRowCreatedTime(DateUtil.toDate(campi[5]));
            titolario.setRowCreatedUser(campi[6]);
            titolario.setRowUpdatedUser(campi[7]);
            titolario.setRowUpdatedTime(DateUtil.toDate(campi[8]));
            titolario.setVersione(stringToInt(campi[9]));
            titolario.setPath(campi[10]);
            if (campi.length > 11) {
                titolario.setMassimario(stringToInt(campi[11]));
            }
            titolarioDAO.newArgomento(connection, titolario);
        }
        if (titolario != null) {
            identificativiDAO.insertNewId(connection, NomiTabelle.TITOLARIO,
                    titolario.getId().intValue());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaRubrica(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica RUBRICA ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        SoggettoVO soggetto = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            soggetto = new SoggettoVO(campi[29]);
            Indirizzo indirizzo = soggetto.getIndirizzo();
            soggetto.setId(stringToInt(campi[0]));
            if (campi[1] != null) {
                if (campi[1].length() > 100) {
                    soggetto.setCognome(campi[1].substring(0, 99));
                } else {
                    soggetto.setCognome(campi[1]);
                }
            }
            if (campi[2] != null) {
                if (campi[2].length() > 100) {
                    soggetto.setDescrizioneDitta(campi[2].substring(0, 99));
                } else {
                    soggetto.setDescrizioneDitta(campi[2]);
                }
            }
            if (campi[3] != null) {
                if (campi[3].length() > 40) {
                    soggetto.setNome(campi[3].substring(0, 39));
                } else {
                    soggetto.setNome(campi[3]);
                }
            }
            soggetto.setNote(campi[4]);
            soggetto.setTelefono(campi[5]);
            soggetto.setTeleFax(campi[6]);
            soggetto.setIndirizzoWeb(campi[7]);
            soggetto.setIndirizzoEMail(campi[8]);
            // titolario.setRowUpdatedTime(DateUtil.toDate(campi[8]));
            indirizzo.setCap(campi[9]);
            indirizzo.setComune(campi[10]);
            indirizzo.setToponimo(campi[17]);
            indirizzo.setCivico(campi[18]);
            indirizzo.setProvinciaId(stringToInt(campi[28]));
            soggetto.setIndirizzo(indirizzo);
            soggetto.setAoo(stringToInt(campi[13]));
            soggetto.setFlagSettoreAppartenenza(stringToInt(campi[14]));
            soggetto.setQualifica(campi[15]);
            soggetto.setDug(campi[16]);
            soggetto.setDataNascita(DateUtil.toDate(campi[19]));
            soggetto.setSesso(campi[20]);
            soggetto.setPartitaIva(campi[21]);
            soggetto.setComuneNascita(campi[22]);
            soggetto.setReferente(campi[23]);
            soggetto.setTelefonoReferente(campi[24]);
            soggetto.setCodiceFiscale(campi[25]);
            soggetto.setStatoCivile(campi[26]);
            soggetto.setProvinciaNascitaId(stringToInt(campi[27]));
            soggetto.setTipo(campi[29]);
            soggetto.setRowCreatedTime(DateUtil.toDate(campi[30]));
            soggetto.setRowCreatedUser(campi[31]);
            soggetto.setRowUpdatedUser(campi[32]);
            soggetto.setRowUpdatedTime(DateUtil.toDate(campi[33]));
            soggetto.setVersione(stringToInt(campi[34]));
            if (campi[29].equals("G")) {
                soggettoDAO.newPersonaGiuridica(connection, soggetto);
            } else {
                soggettoDAO.newPersonaFisica(connection, soggetto);
            }
        }
        if (soggetto != null) {
            identificativiDAO.insertNewId(connection, NomiTabelle.RUBRICA,
                    soggetto.getId().intValue());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaListaDistribuzione(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica LISTA DISTRIBUZIONE ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        ListaDistribuzioneVO listaDistribuzione = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            listaDistribuzione = new ListaDistribuzioneVO();
            listaDistribuzione.setId(campi[0]);
            listaDistribuzione.setDescription(campi[1]);
            listaDistribuzione.setRowCreatedTime(DateUtil.toDate(campi[2]));
            listaDistribuzione.setRowCreatedUser(campi[3]);
            listaDistribuzione.setRowUpdatedUser(campi[4]);
            listaDistribuzione.setRowUpdatedTime(DateUtil.toDate(campi[5]));
            listaDistribuzione.setAooId(Integer.parseInt(campi[6]));
            soggettoDAO.newListaDistribuzione(connection, listaDistribuzione);
            // logger.debug("id=" + listaDistribuzione.getId());
        }
        if (listaDistribuzione != null) {
            identificativiDAO.insertNewId(connection,
                    NomiTabelle.LISTA_DISTRIBUZIONE, listaDistribuzione.getId()
                            .intValue());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaRubricaListaDistribuzione(Connection connection,
            Reader reader) throws Throwable {
        logger.info("--- Carica RUBRICA LISTA DISTRIBUZIONE ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        RubricaListaVO rubricaListaDistribuzione = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            rubricaListaDistribuzione = new RubricaListaVO();
            rubricaListaDistribuzione.setIdLista(stringToInt(campi[0]));
            rubricaListaDistribuzione.setIdRubrica(stringToInt(campi[1]));
            rubricaListaDistribuzione.setRowCreatedTime(DateUtil
                    .toDate(campi[2]));
            rubricaListaDistribuzione.setRowCreatedUser(campi[3]);
            rubricaListaDistribuzione.setRowUpdatedUser(campi[4]);
            rubricaListaDistribuzione.setRowUpdatedTime(DateUtil
                    .toDate(campi[5]));
            rubricaListaDistribuzione.setTipoSoggetto(campi[6]);
            soggettoDAO.inserisciSoggettoLista(connection,
                    rubricaListaDistribuzione);
            // logger.debug("listaid=" + rubricaListaDistribuzione.getIdLista()
            // + " rubricaid" + rubricaListaDistribuzione.getIdRubrica());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaProtocolli(Connection connection, boolean storia,
            Reader reader) throws Throwable {
        if (storia) {
            logger.info("--- Carica STORIA PROTOCOLLI ---");
        } else {
            logger.info("--- Carica PROTOCOLLI ---");
        }

        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        ProtocolloVO protocollo = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            protocollo = new ProtocolloVO();
            protocollo.setId(stringToInt(campi[0]));
            protocollo.setAnnoRegistrazione(stringToInt(campi[1]));
            protocollo.setNumProtocollo(stringToInt(campi[2]));
            protocollo.setDataRegistrazione(DateUtil.toDate(campi[3]));
            protocollo.setFlagTipoMittente(campi[4]);
            if (campi[5] == null || "".equals(campi[5].trim())) {
                protocollo.setOggetto("Oggetto non definito");
                logger.error("PROTOCOLLO: " + campi[0]
                        + " campo oggetto null or vuoto ---");
            } else {
                protocollo.setOggetto(campi[5]);
            }

            protocollo.setFlagTipo(campi[6]);
            protocollo.setDataDocumento(DateUtil.toDate(campi[7]));
            protocollo.setTipoDocumentoId(stringToInt(campi[8]));
            protocollo.setRegistroId(stringToInt(campi[9]));
            protocollo.setTitolarioId(stringToInt(campi[10]));
            protocollo.setUtenteProtocollatoreId(stringToInt(campi[11]));
            protocollo.setUfficioProtocollatoreId(stringToInt(campi[12]));
            protocollo.setDataScadenza(DateUtil.toDate(campi[13]));
            protocollo
                    .setDataEffettivaRegistrazione(DateUtil.toDate(campi[14]));
            protocollo.setDataRicezione(DateUtil.toDateTime(campi[15]));
            protocollo.setDataProtocolloMittente(campi[16]);
            protocollo.setNumProtocolloMittente(campi[17]);
            protocollo.setDenominazioneMittente(campi[18]);
            protocollo.setCognomeMittente(campi[19]);
            protocollo.setNomeMittente(campi[20]);
            protocollo.setMittenteIndirizzo(campi[21]);
            protocollo.setMittenteCap(campi[22]);
            protocollo.setMittenteComune(campi[23]);
            protocollo.setMittenteProvinciaId(stringToInt(campi[24]));
            protocollo.setMittenteNazione(campi[25]);
            protocollo.setDataAnnullamento(DateUtil.toDate(campi[26]));
            protocollo.setDataScarico(DateUtil.toDate(campi[27]));
            protocollo.setStatoProtocollo("A"); // campi[28] forzo agli atti
            protocollo.setNotaAnnullamento(campi[29]);
            protocollo.setProvvedimentoAnnullamento(campi[30]);
            protocollo.setCodDocumentoDoc(campi[31]);
            protocollo.setRowCreatedTime(DateUtil.toDate(campi[32]));
            protocollo.setRowCreatedUser(campi[33]);
            protocollo.setUtenteMittenteId(stringToInt(campi[34]));
            protocollo.setVersione(stringToInt(campi[35]));
            protocollo.setChiaveAnnotazione(campi[36]);
            protocollo.setPosizioneAnnotazione(campi[37]);
            protocollo.setDescrizioneAnnotazione(campi[38]);
            protocollo.setAooId(stringToInt(campi[39]));
            protocollo.setDocumentoPrincipaleId(stringToInt(campi[40]));
            protocollo.setUfficioMittenteId(stringToInt(campi[41]));
            // protocollo.set(campi[42]); // scarto
            protocollo.setMozione("1".equals(campi[43]));
            protocollo.setRiservato("1".equals(campi[44]));

            if (storia) {
                protocolloDAO.newStoriaProtocollo(connection, protocollo);
                // logger.debug("storia_id=" + protocollo.getId());
            } else {
                if ((protocolloDAO.getProtocolloByNumero(connection, protocollo
                        .getAnnoRegistrazione(), protocollo.getRegistroId(),
                        protocollo.getNumProtocollo())).getReturnValue() == ReturnValues.FOUND) {
                    protocolliDuplicati.put(new Integer(protocollo
                            .getNumProtocollo()), new Integer(protocollo
                            .getNumProtocollo() + 2000000));
                    protocollo
                            .setNumProtocollo(protocollo.getNumProtocollo() + 2000000);

                    logger.error("Protocollo id=" + protocollo.getId()
                            + " DUPLICAZIONE NUMERO: "
                            + protocollo.getNumProtocollo());

                }

                protocolloDAO.newProtocollo(connection, protocollo);
                // logger.debug("id=" + protocollo.getId());
            }
        }
        if (!storia && protocollo != null) {
            identificativiDAO.insertNewId(connection, NomiTabelle.PROTOCOLLI,
                    protocollo.getId().intValue());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaProcedimenti(Connection connection, boolean storia,
            Reader reader) throws Throwable {
        if (storia) {
            logger.info("--- Carica STORIA PROCEDIMENTI ---");
        } else {
            logger.info("--- Carica PROCEDIMENTI ---");
        }
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        ProcedimentoVO procedimento = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            procedimento = new ProcedimentoVO();
            procedimento.setId(stringToInt(campi[0]));
            procedimento.setDataAvvio(DateUtil.toDate(campi[1]));
            procedimento.setUfficioId(stringToInt(campi[2]));
            procedimento.setTitolarioId(stringToInt(campi[3]));
            procedimento.setStatoId(stringToInt(campi[4]));
            procedimento.setTipoFinalitaId(stringToInt(campi[5]));
            procedimento.setOggetto(campi[6]);
            procedimento.setTipoProcedimentoId(stringToInt(campi[7]));
            procedimento.setReferenteId(stringToInt(campi[8]));
            procedimento.setPosizione(campi[9]);
            procedimento.setDataEvidenza(DateUtil.toDate(campi[10]));
            procedimento.setNote(campi[11]);
            int anno = Integer.parseInt(campi[12].substring(0, 4));
            int numero = Integer.parseInt(campi[12].substring(4));
            ProtocolloVO protocolloVO = protocolloDAO.getProtocolloByNumero(
                    connection, anno, 1, numero);
            procedimento.setProtocolloId(protocolloVO.getId().intValue());
            procedimento.setNumeroProcedimento(campi[13]);
            procedimento.setAnno(stringToInt(campi[14]));
            procedimento.setNumero(stringToInt(campi[15]));
            procedimento.setRowCreatedTime(DateUtil.toDate(campi[16]));
            procedimento.setRowCreatedUser(campi[17]);
            procedimento.setRowUpdatedUser(campi[18]);
            procedimento.setRowUpdatedTime(DateUtil.toDate(campi[19]));
            procedimento.setVersione(stringToInt(campi[20]));
            procedimento.setResponsabile(campi[21]);
            procedimento.setAooId(stringToInt(campi[22]));
            if (storia) {
                procedimentoDAO.newStoriaProcedimento(connection, procedimento);
                // logger.debug("storia_id=" + procedimento.getId());
            } else {
                procedimentoDAO.newProcedimento(connection, procedimento);
                // logger.debug("id=" + procedimento.getId());
            }
        }

        if (!storia && procedimento != null) {
            identificativiDAO.insertNewId(connection, NomiTabelle.PROCEDIMENTI,
                    procedimento.getId().intValue());
            Collection procedimenti = new ArrayList();
            procedimenti = procedimentoDAO
                    .getProcedimentiAnnoNumero(connection);
            for (Iterator iter = procedimenti.iterator(); iter.hasNext();) {
                MigrazioneVO element = (MigrazioneVO) iter.next();
                identificativiDAO.insertNewId(connection,
                        NomiTabelle.PROCEDIMENTI + element.getAnno(), element
                                .getNumero());

            }
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaProtocolloAssegnatari(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica ASSEGNATARI PROTOCOLLO---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        AssegnatarioVO assegnatario = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            assegnatario = new AssegnatarioVO();
            assegnatario.setId(stringToInt(campi[0]));
            assegnatario.setProtocolloId(stringToInt(campi[1]));
            assegnatario.setDataAssegnazione(DateUtil.toDate(campi[2]));
            assegnatario.setDataOperazione(DateUtil.toDate(campi[3]));
            // if (campi[4] != null && campi[4].length() > 0) {
            // assegnatario.setStatoAssegnazione(campi[4].charAt(0));
            // }
            assegnatario.setStatoAssegnazione('A');// agli atti
            assegnatario.setUfficioAssegnanteId(stringToInt(campi[5]));
            assegnatario.setUfficioAssegnatarioId(stringToInt(campi[6]));
            assegnatario.setUtenteAssegnatarioId(stringToInt(campi[7]));
            assegnatario.setUtenteAssegnanteId(stringToInt(campi[8]));
            assegnatario.setVersione(stringToInt(campi[9]));
            assegnatario.setCompetente("1".equals(campi[10]));
            protocolloDAO.salvaAssegnatario(connection, assegnatario,
                    assegnatario.getVersione());
            // logger.debug("p_id=" + assegnatario.getProtocolloId() + "
            // ass_id="
            // + assegnatario.getUfficioAssegnatarioId());
        }
        if (assegnatario != null) {
            identificativiDAO.insertNewId(connection,
                    NomiTabelle.PROTOCOLLO_ASSEGNATARI, assegnatario.getId()
                            .intValue());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    private void salvaDestinatari(Connection connection, Reader reader)
            throws Throwable {
        logger.info("--- Carica PROTOCOLLO DESTINATARI ---");
        LineNumberReader lnReader = new LineNumberReader(reader);
        String riga;
        DestinatarioVO destinatario = null;
        while ((riga = lnReader.readLine()) != null) {
            String[] campi = riga.split("\t");
            destinatario = new DestinatarioVO();
            destinatario.setId(stringToInt(campi[0]));
            destinatario.setFlagTipoDestinatario(campi[1]);
            destinatario.setIndirizzo(campi[2]);
            destinatario.setEmail(campi[3]);
            destinatario.setDestinatario(campi[4]);
            destinatario.setMezzoDesc(campi[5]);
            destinatario.setCitta(campi[6]);
            destinatario.setDataSpedizione(DateUtil.toDate(campi[7]));
            destinatario.setFlagConoscenza("1".equals(campi[8]));
            destinatario.setProtocolloId(stringToInt(campi[9]));
            destinatario.setDataEffettivaSpedizione(DateUtil.toDate(campi[10]));
            destinatario.setVersione(stringToInt(campi[11]));
            // destinatario.setTitoloI d(stringToInt(campi[12]));
            // destinatario.setNote(campi[13]);
            // destinatario.setMezzoSpedizioneId(stringToInt(campi[14]));
            // TODO VERIFICARE SE UTILIZZARLO O MENO
            if (campi.length >= 13) {
                destinatario.setCodicePostale(campi[12]);
            }
            protocolloDAO.salvaDestinatario(connection, destinatario,
                    destinatario.getVersione());
            // logger.debug("id=" + destinatario.getId());
        }
        if (destinatario != null) {
            identificativiDAO.insertNewId(connection,
                    NomiTabelle.PROTOCOLLO_DESTINATARI, destinatario.getId()
                            .intValue());
        }
        logger.info("records elaborati:=" + lnReader.getLineNumber());
        lnReader.close();
    }

    public void caricaDati(InputStream isUffici, InputStream isUtenti,
            InputStream isPermessiUtenti, InputStream isRegistriUtente,
            InputStream isTitolario, InputStream isProtocolli,
            InputStream isProtocolliAss, InputStream isProtocolliDest,
            InputStream isStoriaProtocolli, InputStream isFaldoni,
            InputStream isReferentiUffici, InputStream isProcedimenti,
            InputStream isTipiProcedimenti, InputStream isTitolariUffici,
            InputStream isFascicoli, InputStream isStoriaFascicoli,
            InputStream isFaldoniFascicoli, InputStream isFascicoliProtocolli,
            InputStream isProcedimentiFaldoni,
            InputStream isProcedimentiFascicoli,
            InputStream isProtocolliProcedimenti, InputStream isRubrica,
            InputStream isListaDistribuzione,
            InputStream isRubricaListaDistribuzione,
            InputStream isStoriaProcedimento) throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        Reader reader;
        logger.info("Inizio elaborazione.");
        try {
            // if (!controlloFiles(isUffici, isUtenti, isPermessiUtenti,
            // isRegistriUtente, isTitolario, isProtocolli,
            // isProtocolliAss, isProtocolliDest, isStoriaProtocolli,
            // isFaldoni, isReferentiUffici, isProcedimenti,
            // isTipiProcedimenti, isTitolariUffici, isFascicoli,
            // isStoriaFascicoli, isFaldoniFascicoli,
            // isFascicoliProtocolli, isProcedimentiFaldoni,
            // isProcedimentiFascicoli, isProtocolliProcedimenti,
            // isRubrica, isListaDistribuzione,
            // isRubricaListaDistribuzione, isStoriaProcedimento)) {
            // logger
            // .error("Errore nella migrazione: controllo congruenza campi dei
            // files");
            // throw new DataException(
            // "Errore nella migrazione: controllo congruenza campi dei files");
            //
            // }
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            // UFFICI
            reader = new InputStreamReader(isUffici);
            delegate.salvaUffici(connection, reader);
            reader.close();
            isUffici.close();

            // UTENTI
            reader = new InputStreamReader(isUtenti);
            delegate.salvaUtenti(connection, reader);
            reader.close();
            isUtenti.close();

            // PERMESSI UTENTI
            reader = new InputStreamReader(isPermessiUtenti);
            delegate.salvaPermessiUtente(connection, reader);
            reader.close();
            isPermessiUtenti.close();

            // UTENTE REGISTRI
            reader = new InputStreamReader(isRegistriUtente);
            delegate.salvaUtentiRegistri(connection, reader);
            reader.close();
            isRegistriUtente.close();

            // TITOLARIO
            reader = new InputStreamReader(isTitolario);
            delegate.salvaTitolario(connection, reader);
            reader.close();
            isTitolario.close();

            // PROTOCOLLI
            reader = new InputStreamReader(isProtocolli);
            delegate.salvaProtocolli(connection, false, reader);
            reader.close();
            isProtocolli.close();

            // PROTOCOLLI ASSEGNATARI
            reader = new InputStreamReader(isProtocolliAss);
            delegate.salvaProtocolloAssegnatari(connection, reader);
            reader.close();
            isProtocolliAss.close();

            // PROTOCOLLI DESTINATARI
            reader = new InputStreamReader(isProtocolliDest);
            delegate.salvaDestinatari(connection, reader);
            reader.close();
            isProtocolliDest.close();

            // STORIA PROTOCOLLI
            reader = new InputStreamReader(isStoriaProtocolli);
            delegate.salvaProtocolli(connection, true, reader);
            reader.close();
            isStoriaProtocolli.close();

            // FALDONI
            reader = new InputStreamReader(isFaldoni);
            delegate.salvaFaldoni(connection, reader);
            reader.close();
            isFaldoni.close();

            // FASCICOLI
            reader = new InputStreamReader(isFascicoli);
            delegate.salvaFascicoli(connection, false, reader);
            reader.close();
            isFascicoli.close();

            // STORIA FASCICOLI
            reader = new InputStreamReader(isStoriaFascicoli);
            delegate.salvaFascicoli(connection, true, reader);
            reader.close();
            isStoriaFascicoli.close();

            // FALDONI FASCICOLI
            reader = new InputStreamReader(isFaldoniFascicoli);
            delegate.salvaFaldoniFascicoli(connection, reader);
            reader.close();
            isFaldoniFascicoli.close();

            // FASCICOLI PROTOCOLLI
            reader = new InputStreamReader(isFascicoliProtocolli);
            delegate.salvaFascicoliProtocolli(connection, reader);
            reader.close();
            isFascicoliProtocolli.close();

            // REFERENTI UFFICI
            reader = new InputStreamReader(isReferentiUffici);
            delegate.salvaReferentiUfficio(connection, reader);
            reader.close();
            isReferentiUffici.close();

            // TIPI PROCEDIMENTI
            reader = new InputStreamReader(isTipiProcedimenti);
            delegate.salvaTipoProcedimento(connection, reader);
            reader.close();
            isTipiProcedimenti.close();

            // PROCEDIMENTI
            reader = new InputStreamReader(isProcedimenti);
            delegate.salvaProcedimenti(connection, false, reader);
            reader.close();
            isProcedimenti.close();

            // STORIA PROCEDIMENTI
            reader = new InputStreamReader(isStoriaProcedimento);
            delegate.salvaProcedimenti(connection, true, reader);
            reader.close();

            // PROCEDIMENTI FALDONI
            reader = new InputStreamReader(isProcedimentiFaldoni);
            delegate.salvaProcedimentiFaldoni(connection, reader);
            reader.close();
            isProcedimentiFaldoni.close();

            // PROCEDIMENTI FASCICOLI
            reader = new InputStreamReader(isProcedimentiFascicoli);
            delegate.salvaProcedimentiFascicoli(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // PROTOCOLLO PROCEDIMENTI
            reader = new InputStreamReader(isProtocolliProcedimenti);
            delegate.salvaProtocolloProcedimenti(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // RUBRICA
            reader = new InputStreamReader(isRubrica);
            delegate.salvaRubrica(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // LISTA DISTRIBUZIONE
            reader = new InputStreamReader(isListaDistribuzione);
            delegate.salvaListaDistribuzione(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // RUBRICA LISTA DISTRIBUZIONE
            reader = new InputStreamReader(isRubricaListaDistribuzione);
            delegate.salvaRubricaListaDistribuzione(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // TITOLARI UFFICI
            reader = new InputStreamReader(isTitolariUffici);
            delegate.salvaTitolariUffici(connection, reader);
            reader.close();
            isTitolariUffici.close();

            connection.commit();
            logger.info("Fine elaborazione.");
        } catch (Throwable e) {
            jdbcMan.rollback(connection);
            logger.error("Fine elaborazione con errore.", e);
            throw new DataException("Errore nella migrazione: "
                    + e.getMessage());

        } finally {
            jdbcMan.close(connection);
        }

    }

    public boolean isCaricamentoEffettuato() throws DataException {
        return ufficioDAO.getCountUffici() > 1
                || utenteDAO.getCountUtenti() > 1;
    }

    private boolean controlloFiles(InputStream isUffici, InputStream isUtenti,
            InputStream isPermessiUtenti, InputStream isRegistriUtente,
            InputStream isTitolario, InputStream isProtocolli,
            InputStream isProtocolliAss, InputStream isProtocolliDest,
            InputStream isStoriaProtocolli, InputStream isFaldoni,
            InputStream isReferentiUffici, InputStream isProcedimenti,
            InputStream isTipiProcedimenti, InputStream isTitolariUffici,
            InputStream isFascicoli, InputStream isStoriaFascicoli,
            InputStream isFaldoniFascicoli, InputStream isFascicoliProtocolli,
            InputStream isProcedimentiFaldoni,
            InputStream isProcedimentiFascicoli,
            InputStream isProtocolliProcedimenti, InputStream isRubrica,
            InputStream isListaDistribuzione,
            InputStream isRubricaListaDistribuzione,
            InputStream isStoriaProcedimento) throws DataException {

        final int UFFICI_CAMPI_SIZE = 12;
        final int FASCICOLI_CAMPI_SIZE = 38;
        final int FALDONI_CAMPI_SIZE = 30;
        final int FALDONI_FASCICOLI_CAMPI_SIZE = 2;
        final int FASCICOLI_PROTOCOLLI_CAMPI_SIZE = 2;
        final int PROCEDIMENTI_FASCICOLI_CAMPI_SIZE = 2;
        final int PROCEDIMENTI_FALDONI_CAMPI_SIZE = 2;
        final int TIPO_PROCEDIMENTO_CAMPI_SIZE = 3;
        final int TITOLARIO_UFFICI_CAMPI_SIZE = 7;
        final int UTENTI_CAMPI_SIZE = 16;
        final int PERMESSI_UTENTI_CAMPI_SIZE = 8;
        final int UTENTI_REGISTRI_CAMPI_SIZE = 7;
        final int REFERENTI_UFFICI_CAMPI_SIZE = 2;
        final int PROTOCOLLO_PROCEDIMENTI_CAMPI_SIZE = 3;
        final int TITOLARIO_CAMPI_SIZE = 12;
        final int RUBRICA_CAMPI_SIZE = 35;
        final int LISTA_DISTRIBUZIONE_CAMPI_SIZE = 7;
        final int RUBRICA_LISTA_DISTRIBUZIONE_CAMPI_SIZE = 7;
        final int PROTOCOLLI_CAMPI_SIZE = 45;
        final int PROCEDIMENTI_CAMPI_SIZE = 23;
        final int PROTOCOLLO_ASSEGNATARI_CAMPI_SIZE = 11;
        final int PROTOCOLLO_DESTINATARI_CAMPI_SIZE = 12;

        boolean controllo = true;
        Reader reader;

        try {
            LineNumberReader lnReader;
            String riga;
            String[] campi;

            reader = new InputStreamReader(isUffici);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > UFFICI_CAMPI_SIZE) {
                    logger.error("UFFICI: campi incongruenti");
                    throw new DataException("UFFICI: campi incongruenti");
                }
            }
            reader.close();

            // UTENTI
            reader = new InputStreamReader(isUtenti);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > UTENTI_CAMPI_SIZE) {
                    logger.error("UTENTI: campi incongruenti");
                    throw new DataException("UTENTI: campi incongruenti");
                }
            }
            reader.close();

            // PERMESSI UTENTI
            reader = new InputStreamReader(isPermessiUtenti);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PERMESSI_UTENTI_CAMPI_SIZE) {
                    logger.error("PERMESSI_UTENTI: campi incongruenti");
                    throw new DataException(
                            "PERMESSI_UTENTI: campi incongruenti");
                }
            }
            reader.close();

            // UTENTE REGISTRI
            reader = new InputStreamReader(isRegistriUtente);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > UTENTI_REGISTRI_CAMPI_SIZE) {
                    logger.error("UTENTI_REGISTRI: campi incongruenti");
                    throw new DataException(
                            "UTENTI_REGISTRI: campi incongruenti");
                }
            }
            reader.close();

            // TITOLARIO
            reader = new InputStreamReader(isTitolario);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > TITOLARIO_CAMPI_SIZE) {
                    logger.error("TITOLARIO: campi incongruenti");
                    throw new DataException("TITOLARIO: campi incongruenti");
                }
            }
            reader.close();

            // PROTOCOLLI
            reader = new InputStreamReader(isProtocolli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROTOCOLLI_CAMPI_SIZE) {
                    logger.error("PROTOCOLLI: campi incongruenti");
                    throw new DataException("PROTOCOLLI: campi incongruenti");
                }
            }
            reader.close();

            // PROTOCOLLI ASSEGNATARI
            reader = new InputStreamReader(isProtocolliAss);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROTOCOLLO_ASSEGNATARI_CAMPI_SIZE) {
                    logger.error("PROTOCOLLO_ASSEGNATARI: campi incongruenti");
                    throw new DataException(
                            "PROTOCOLLO_ASSEGNATARI: campi incongruenti");
                }
            }
            reader.close();

            // PROTOCOLLI DESTINATARI
            reader = new InputStreamReader(isProtocolliDest);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROTOCOLLO_DESTINATARI_CAMPI_SIZE) {
                    logger.error("PROTOCOLLO_DESTINATARI: campi incongruenti");
                    throw new DataException(
                            "PROTOCOLLO_DESTINATARI: campi incongruenti");
                }
            }
            reader.close();

            // STORIA PROTOCOLLI
            reader = new InputStreamReader(isStoriaProtocolli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROTOCOLLI_CAMPI_SIZE) {
                    logger.error("STORIA_PROTOCOLLI: campi incongruenti");
                    throw new DataException(
                            "STORIA_PROTOCOLLI: campi incongruenti");
                }
            }
            reader.close();

            // FALDONI
            reader = new InputStreamReader(isFaldoni);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > FALDONI_CAMPI_SIZE) {
                    logger.error("FALDONI: campi incongruenti");
                    throw new DataException("FALDONI: campi incongruenti");
                }
            }
            reader.close();

            // FASCICOLI
            reader = new InputStreamReader(isFascicoli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > FASCICOLI_CAMPI_SIZE) {
                    logger.error("FASCICOLI: campi incongruenti");
                    throw new DataException("FASCICOLI: campi incongruenti");
                }
            }
            reader.close();

            // STORIA FASCICOLI
            reader = new InputStreamReader(isStoriaFascicoli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > FASCICOLI_CAMPI_SIZE) {
                    logger.error("STORIA_FASCICOLI: campi incongruenti");
                    throw new DataException(
                            "STORIA_FASCICOLI: campi incongruenti");
                }
            }
            reader.close();

            // FALDONI FASCICOLI
            reader = new InputStreamReader(isFaldoniFascicoli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > FALDONI_FASCICOLI_CAMPI_SIZE) {
                    logger.error("FALDONI_FASCICOLI: campi incongruenti");
                    throw new DataException(
                            "FALDONI_FASCICOLI: campi incongruenti");
                }
            }
            reader.close();

            // FASCICOLI PROTOCOLLI
            reader = new InputStreamReader(isFascicoliProtocolli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > FASCICOLI_PROTOCOLLI_CAMPI_SIZE) {
                    logger.error("FASCICOLI_PROTOCOLLI: campi incongruenti");
                    throw new DataException(
                            "FASCICOLI_PROTOCOLLI: campi incongruenti");
                }
            }
            reader.close();

            // REFERENTI UFFICI
            reader = new InputStreamReader(isReferentiUffici);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > REFERENTI_UFFICI_CAMPI_SIZE) {
                    logger.error("REFERENTI_UFFICI: campi incongruenti");
                    throw new DataException(
                            "REFERENTI_UFFICI: campi incongruenti");
                }
            }
            reader.close();

            // TIPI PROCEDIMENTI
            reader = new InputStreamReader(isTipiProcedimenti);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > TIPO_PROCEDIMENTO_CAMPI_SIZE) {
                    logger.error("TIPO_PROCEDIMENTO: campi incongruenti");
                    throw new DataException(
                            "TIPO_PROCEDIMENTO: campi incongruenti");
                }
            }
            reader.close();

            // PROCEDIMENTI
            reader = new InputStreamReader(isProcedimenti);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROCEDIMENTI_CAMPI_SIZE) {
                    logger.error("PROCEDIMENTI: campi incongruenti");
                    throw new DataException("PROCEDIMENTI: campi incongruenti");
                }
            }
            reader.close();

            // STORIA PROCEDIMENTI
            reader = new InputStreamReader(isStoriaProcedimento);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROCEDIMENTI_CAMPI_SIZE) {
                    logger.error("STORIA PROCEDIMENTI: campi incongruenti");
                    throw new DataException(
                            "STORIA PROCEDIMENTI: campi incongruenti");
                }
            }
            reader.close();

            // PROCEDIMENTI FALDONI
            reader = new InputStreamReader(isProcedimentiFaldoni);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROCEDIMENTI_FALDONI_CAMPI_SIZE) {
                    logger.error("PROCEDIMENTI_FALDONI: campi incongruenti");
                    throw new DataException(
                            "PROCEDIMENTI_FALDONI: campi incongruenti");
                }
            }
            reader.close();

            // PROCEDIMENTI FASCICOLI
            reader = new InputStreamReader(isProcedimentiFascicoli);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROCEDIMENTI_FASCICOLI_CAMPI_SIZE) {
                    logger.error("PROCEDIMENTI_FASCICOLI: campi incongruenti");
                    throw new DataException(
                            "PROCEDIMENTI_FASCICOLI: campi incongruenti");
                }
            }
            reader.close();

            // PROTOCOLLO PROCEDIMENTI
            reader = new InputStreamReader(isProtocolliProcedimenti);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > PROTOCOLLO_PROCEDIMENTI_CAMPI_SIZE) {
                    logger.error("PROTOCOLLO_PROCEDIMENTI: campi incongruenti");
                    throw new DataException(
                            "PROTOCOLLO_PROCEDIMENTI: campi incongruenti");
                }
            }
            reader.close();

            // RUBRICA
            reader = new InputStreamReader(isRubrica);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > RUBRICA_CAMPI_SIZE) {
                    logger.error("RUBRICA: campi incongruenti");
                    throw new DataException("RUBRICA: campi incongruenti");
                }
            }
            reader.close();

            // LISTA DISTRIBUZIONE
            reader = new InputStreamReader(isListaDistribuzione);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > LISTA_DISTRIBUZIONE_CAMPI_SIZE) {
                    logger.error("LISTA_DISTRIBUZIONE: campi incongruenti");
                    throw new DataException(
                            "LISTA_DISTRIBUZIONE: campi incongruenti");
                }
            }
            reader.close();

            // RUBRICA LISTA DISTRIBUZIONE
            reader = new InputStreamReader(isRubricaListaDistribuzione);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > RUBRICA_LISTA_DISTRIBUZIONE_CAMPI_SIZE) {
                    logger
                            .error("RUBRICA_LISTA_DISTRIBUZIONE: campi incongruenti");
                    throw new DataException(
                            "RUBRICA_LISTA_DISTRIBUZIONE: campi incongruenti");
                }
            }
            reader.close();

            // TITOLARI UFFICI
            reader = new InputStreamReader(isTitolariUffici);
            lnReader = new LineNumberReader(reader);
            riga = lnReader.readLine();
            if (riga != null) {
                campi = riga.split("\t");
                if (campi.length > TITOLARIO_UFFICI_CAMPI_SIZE) {
                    logger.error("TITOLARIO_UFFICI: campi incongruenti");
                    throw new DataException(
                            "TITOLARIO_UFFICI: campi incongruenti");
                }
            }
            reader.close();

        } catch (Throwable e) {
            controllo = false;
            throw new DataException("Errore nella migrazione: "
                    + e.getMessage());

        } finally {

        }
        return controllo;
    }

    public void caricaDatiDaCartella(String dirFilesMigrazione)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        Reader reader;
        logger.info("Inizio elaborazione.");
        // String dirFilesMigrazione="d:/temp/Firenze";
        try {
            // if (!controlloFiles(isUffici, isUtenti, isPermessiUtenti,
            // isRegistriUtente, isTitolario, isProtocolli,
            // isProtocolliAss, isProtocolliDest, isStoriaProtocolli,
            // isFaldoni, isReferentiUffici, isProcedimenti,
            // isTipiProcedimenti, isTitolariUffici, isFascicoli,
            // isStoriaFascicoli, isFaldoniFascicoli,
            // isFascicoliProtocolli, isProcedimentiFaldoni,
            // isProcedimentiFascicoli, isProtocolliProcedimenti,
            // isRubrica, isListaDistribuzione,
            // isRubricaListaDistribuzione, isStoriaProcedimento)) {
            // logger
            // .error("Errore nella migrazione: controllo congruenza campi dei
            // files");
            // throw new DataException(
            // "Errore nella migrazione: controllo congruenza campi dei files");
            //
            // }
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            // UFFICI
            File fileCorrente = new File(dirFilesMigrazione + "/uffici.txt");
            InputStream isUffici = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isUffici);
            delegate.salvaUffici(connection, reader);
            reader.close();
            isUffici.close();

            // UTENTI
            fileCorrente = new File(dirFilesMigrazione + "/utenti.txt");
            InputStream isUtenti = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isUtenti);
            delegate.salvaUtenti(connection, reader);
            reader.close();
            isUtenti.close();

            // PERMESSI UTENTI
            fileCorrente = new File(dirFilesMigrazione + "/permessi_utente.txt");
            InputStream isPermessiUtenti = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isPermessiUtenti);
            delegate.salvaPermessiUtente(connection, reader);
            reader.close();
            isPermessiUtenti.close();

            // UTENTE REGISTRI
            fileCorrente = new File(dirFilesMigrazione + "/utenti$registri.txt");
            InputStream isRegistriUtente = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isRegistriUtente);
            delegate.salvaUtentiRegistri(connection, reader);
            reader.close();
            isRegistriUtente.close();

            // TITOLARIO
            fileCorrente = new File(dirFilesMigrazione + "/titolario.txt");
            InputStream isTitolario = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isTitolario);
            delegate.salvaTitolario(connection, reader);
            reader.close();
            isTitolario.close();

            // PROTOCOLLI
            fileCorrente = new File(dirFilesMigrazione + "/protocolli.txt");
            InputStream isProtocolli = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isProtocolli);
            delegate.salvaProtocolli(connection, false, reader);
            reader.close();
            isProtocolli.close();

            // PROTOCOLLI ASSEGNATARI
            fileCorrente = new File(dirFilesMigrazione
                    + "/protocollo_assegnatari.txt");
            InputStream isProtocolliAss = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isProtocolliAss);
            delegate.salvaProtocolloAssegnatari(connection, reader);
            reader.close();
            isProtocolliAss.close();

            // PROTOCOLLI DESTINATARI
            fileCorrente = new File(dirFilesMigrazione
                    + "/protocollo_destinatari.txt");
            InputStream isProtocolliDest = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isProtocolliDest);
            delegate.salvaDestinatari(connection, reader);
            reader.close();
            isProtocolliDest.close();

            // STORIA PROTOCOLLI
            fileCorrente = new File(dirFilesMigrazione
                    + "/storia_protocolli.txt");
            InputStream isStoriaProtocolli = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isStoriaProtocolli);
            delegate.salvaProtocolli(connection, true, reader);
            reader.close();
            isStoriaProtocolli.close();

            // FALDONI
            fileCorrente = new File(dirFilesMigrazione + "/faldoni.txt");
            InputStream isFaldoni = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isFaldoni);
            delegate.salvaFaldoni(connection, reader);
            reader.close();
            isFaldoni.close();

            // FASCICOLI
            fileCorrente = new File(dirFilesMigrazione + "/fascicoli.txt");
            InputStream isFascicoli = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isFascicoli);
            delegate.salvaFascicoli(connection, false, reader);
            reader.close();
            isFascicoli.close();

            // STORIA FASCICOLI
            fileCorrente = new File(dirFilesMigrazione
                    + "/storia_fascicoli.txt");
            InputStream isStoriaFascicoli = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isStoriaFascicoli);
            delegate.salvaFascicoli(connection, true, reader);
            reader.close();
            isStoriaFascicoli.close();

            // FALDONI FASCICOLI
            fileCorrente = new File(dirFilesMigrazione
                    + "/faldone_fascicoli.txt");
            InputStream isFaldoniFascicoli = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isFaldoniFascicoli);
            delegate.salvaFaldoniFascicoli(connection, reader);
            reader.close();
            isFaldoniFascicoli.close();

            // FASCICOLI PROTOCOLLI
            fileCorrente = new File(dirFilesMigrazione
                    + "/fascicolo_protocolli.txt");
            InputStream isFascicoliProtocolli = new FileInputStream(
                    fileCorrente);
            reader = new InputStreamReader(isFascicoliProtocolli);
            delegate.salvaFascicoliProtocolli(connection, reader);
            reader.close();
            isFascicoliProtocolli.close();

            // REFERENTI UFFICI
            fileCorrente = new File(dirFilesMigrazione
                    + "/referenti_uffici.txt");
            InputStream isReferentiUffici = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isReferentiUffici);
            delegate.salvaReferentiUfficio(connection, reader);
            reader.close();
            isReferentiUffici.close();

            // TIPI PROCEDIMENTI
            fileCorrente = new File(dirFilesMigrazione
                    + "/tipi_procedimento.txt");
            InputStream isTipiProcedimenti = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isTipiProcedimenti);
            delegate.salvaTipoProcedimento(connection, reader);
            reader.close();
            isTipiProcedimenti.close();

            // PROCEDIMENTI
            fileCorrente = new File(dirFilesMigrazione + "/procedimenti.txt");
            InputStream isProcedimenti = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isProcedimenti);
            delegate.salvaProcedimenti(connection, false, reader);
            reader.close();
            isProcedimenti.close();

            // STORIA PROCEDIMENTI
            fileCorrente = new File(dirFilesMigrazione
                    + "/storia_procedimenti.txt");
            InputStream isStoriaProcedimento = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isStoriaProcedimento);
            delegate.salvaProcedimenti(connection, true, reader);
            reader.close();

            // PROCEDIMENTI FALDONI
            fileCorrente = new File(dirFilesMigrazione
                    + "/procedimenti_faldone.txt");
            InputStream isProcedimentiFaldoni = new FileInputStream(
                    fileCorrente);
            reader = new InputStreamReader(isProcedimentiFaldoni);
            delegate.salvaProcedimentiFaldoni(connection, reader);
            reader.close();
            isProcedimentiFaldoni.close();

            // PROCEDIMENTI FASCICOLI
            fileCorrente = new File(dirFilesMigrazione
                    + "/procedimenti_fascicolo.txt");
            InputStream isProcedimentiFascicoli = new FileInputStream(
                    fileCorrente);
            reader = new InputStreamReader(isProcedimentiFascicoli);
            delegate.salvaProcedimentiFascicoli(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // PROTOCOLLO PROCEDIMENTI
            fileCorrente = new File(dirFilesMigrazione
                    + "/protocollo_procedimenti.txt");
            InputStream isProtocolliProcedimenti = new FileInputStream(
                    fileCorrente);
            reader = new InputStreamReader(isProtocolliProcedimenti);
            delegate.salvaProtocolloProcedimenti(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // RUBRICA
            fileCorrente = new File(dirFilesMigrazione + "/rubrica.txt");
            InputStream isRubrica = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isRubrica);
            delegate.salvaRubrica(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // LISTA DISTRIBUZIONE
            fileCorrente = new File(dirFilesMigrazione
                    + "/lista_distribuzione.txt");
            InputStream isListaDistribuzione = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isListaDistribuzione);
            delegate.salvaListaDistribuzione(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // RUBRICA LISTA DISTRIBUZIONE
            fileCorrente = new File(dirFilesMigrazione
                    + "/rubrica_lista_distribuzione.txt");
            InputStream isRubricaListaDistribuzione = new FileInputStream(
                    fileCorrente);
            reader = new InputStreamReader(isRubricaListaDistribuzione);
            delegate.salvaRubricaListaDistribuzione(connection, reader);
            reader.close();
            isProcedimentiFascicoli.close();

            // TITOLARI UFFICI
            fileCorrente = new File(dirFilesMigrazione
                    + "/titolario$uffici.txt");
            InputStream isTitolariUffici = new FileInputStream(fileCorrente);
            reader = new InputStreamReader(isTitolariUffici);
            delegate.salvaTitolariUffici(connection, reader);
            reader.close();
            isTitolariUffici.close();

            connection.commit();
            logger.info("Fine elaborazione.");
        } catch (Throwable e) {
            jdbcMan.rollback(connection);
            logger.info("Fine elaborazione con errore.");
            throw new DataException("Errore nella migrazione: "
                    + e.getMessage());

        } finally {
            jdbcMan.close(connection);
        }

    }

}