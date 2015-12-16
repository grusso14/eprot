package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.Protocollo;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.model.protocollo.ProtocolloUscita;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.integration.ProtocolloDAO;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO.Indirizzo;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.protocollo.SegnaturaVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.EmailUtil;
import it.finsiel.siged.util.PdfUtil;
import it.finsiel.siged.util.StringUtil;
import it.flosslab.mvc.business.MittentiDelegate;
import it.flosslab.mvc.business.OggettarioDelegate;
import it.flosslab.mvc.presentation.integration.ContaProtocolloDAO;
import it.flosslab.mvc.vo.OggettoVO;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;
import org.omg.CORBA.portable.IndirectionException;

public class ProtocolloDelegate {

    private static Logger logger = Logger.getLogger(ProtocolloDelegate.class
            .getName());

    private ProtocolloDAO protocolloDAO = null;
    private ContaProtocolloDAO contaProtocolliDAO = null;
    private ServletConfig config = null;

    private static ProtocolloDelegate delegate = null;

    private ProtocolloDelegate() {

        try {
            if (protocolloDAO == null) {
                protocolloDAO = (ProtocolloDAO) DAOFactory
                        .getDAO(Constants.PROTOCOLLO_DAO_CLASS);

                logger.debug("protocolloDAO instantiated:"
                        + Constants.PROTOCOLLO_DAO_CLASS);
            }
            if (contaProtocolliDAO == null) {
            	contaProtocolliDAO = (ContaProtocolloDAO) DAOFactory
                        .getDAO(Constants.CONTA_PROTOCOLLO_DAO_CLASS);

                logger.debug("contaProtocolliDAO instantiated:"
                        + Constants.CONTA_PROTOCOLLO_DAO_CLASS);
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }
    }

    public static ProtocolloDelegate getInstance() {
        if (delegate == null)
            delegate = new ProtocolloDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.PROTOCOLLO_DELEGATE;
    }

    public int getUltimoProtocollo(int anno, int registro) {
        try {
            return protocolloDAO.getUltimoProtocollo(anno, registro);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getUltimoProtocollo: ");
            return 0;
        }
    }

    private DocumentoVO salvaDocumentoPrincipale(Connection connection,
            ProtocolloVO protocollo, DocumentoVO documento, Utente utente)
            throws Exception {
        String newFile = null;
        if (documento != null && documento.getPath() != null
                && documento.getSize() > 0) {
            // Timbro il PDF
            try {
                newFile = PdfUtil.scriviTimbro(ProtocolloBO.getTimbro(
                        Organizzazione.getInstance(), protocollo), documento
                        .getPath());
                documento.setPath(newFile);

            } catch (Exception e) {
                // TODO: cosa fare? errore all'utente o scavalcare?
                logger.debug("", e);
            }

            DocumentoDelegate documentoDelegate = DocumentoDelegate
                    .getInstance();
            documento = documentoDelegate.salvaDocumento(connection, documento);
            if (newFile != null) // il file era un pdf ed � stato timbrato
                // con
                // successo
                documento.setPath(newFile);// facciamo puntare il documento al
            // nuovo PDF
            // aggiorno l'id del documento su protocollo
            int protocolloId = protocollo.getId().intValue();
            protocolloDAO.aggiornaDocumentoPrincipaleId(connection,
                    protocolloId, documento.getId().intValue());
        }
        return documento;
    }

    private Map salvaAllegati(Connection connection, ProtocolloVO protocollo,
            Map allegati) throws Exception {
        DocumentoDelegate documentoDelegate = DocumentoDelegate.getInstance();
        int protocolloId = protocollo.getId().intValue();

        Iterator iterator = allegati.values().iterator();
        HashMap docs = new HashMap(2);
        while (iterator.hasNext()) {
            DocumentoVO doc = (DocumentoVO) iterator.next();
            int idx = doc.getIdx();// salvo l'indice corrente
            if (doc != null) {
                // il documento se nuovo non ha Id o ha il flag su true
                if (doc.getId() == null || doc.isMustCreateNew()) {
                    // salvo il documento
                    doc = documentoDelegate.salvaDocumento(connection, doc);
                }
                doc.setIdx(idx);// ripristino l'indice chiave
                ProtocolloBO.putAllegato(doc, docs);

                // salvo l'allegato nella tabella delle referenze
                protocolloDAO.salvaAllegato(connection, IdentificativiDelegate
                        .getInstance().getNextId(connection,
                                NomiTabelle.PROTOCOLLO_ALLEGATI), protocolloId,
                        doc.getId().intValue(), protocollo.getVersione());
            }
        }
        return docs;
    }

    private void eliminaAllacci(Connection connection, int protocolloId,
            Utente utente) throws Exception {
        Collection allacci = protocolloDAO.getAllacciProtocollo(connection,
                protocolloId);
        if (allacci != null) {
            for (Iterator i = allacci.iterator(); i.hasNext();) {
                AllaccioVO allaccio = (AllaccioVO) i.next();
                if (allaccio.isPrincipale()
                        && allaccio.getAllaccioDescrizione().indexOf("I") > 0) {
                    ProtocolloVO protIngresso = protocolloDAO
                            .getProtocolloById(connection, allaccio
                                    .getProtocolloAllacciatoId());
                    if (!protIngresso.getStatoProtocollo().equals("C")) {
                        /*
                         * protIngresso.setStatoProtocollo("N");
                         * protIngresso.setDataScarico(null);
                         * protocolloDAO.aggiornaProtocollo(connection,
                         * protIngresso);
                         */
                        updateScarico(protIngresso, "N", utente, connection);
                        // scaricaProtocollo(protIngresso, "A", "N", utente,
                        // connection);
                    }
                }
                protocolloDAO.eliminaAllaccioProtocollo(connection, allaccio
                        .getProtocolloId(), allaccio
                        .getProtocolloAllacciatoId());
            }
        }
    }

    private void salvaAllacci(Connection connection, int protocolloId,
            Collection allacci, Utente utente, String tipoProtocollo)
            throws Exception {
        if (allacci != null) {
            boolean flagPrincipale;
            for (Iterator i = allacci.iterator(); i.hasNext();) {
                flagPrincipale = false;
                AllaccioVO allaccio = (AllaccioVO) i.next();
                allaccio.setProtocolloId(protocolloId);
                allaccio.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.PROTOCOLLO_ALLACCI));

                AssegnatarioVO ass = getAssegnatarioPerCompetenza(connection,
                        allaccio.getProtocolloAllacciatoId());
                if ("U".equals(tipoProtocollo)
                        && allaccio.getProtocolloAllacciatoId() < allaccio
                                .getProtocolloId()
                        && allaccio.getAllaccioDescrizione().indexOf("I") > 0
                        && utente.isUtenteAbilitatoSuUfficio(ass
                                .getUfficioAssegnatarioId())
                        && (ass.getUtenteAssegnatarioId() == 0 || ass
                                .getUtenteAssegnatarioId() == utente
                                .getValueObject().getId().intValue())) {
                    ProtocolloVO protIngresso = protocolloDAO
                            .getProtocolloById(connection, allaccio
                                    .getProtocolloAllacciatoId());
                    if (!protIngresso.getStatoProtocollo().equals("C")) {
                        /*
                         * protIngresso.setStatoProtocollo("R");
                         * protIngresso.setDataScarico(new Date(System
                         * .currentTimeMillis()));
                         * protocolloDAO.aggiornaProtocollo(connection,
                         * protIngresso);
                         */
                        updateScarico(protIngresso, "R", utente, connection);
                        // scaricaProtocollo(protIngresso, "A", "R", utente,
                        // connection);
                        flagPrincipale = true;
                    }
                }
                if (!protocolloDAO.esisteAllaccio(connection, allaccio
                        .getProtocolloId(), allaccio
                        .getProtocolloAllacciatoId())) {

                    allaccio.setPrincipale(flagPrincipale);
                    protocolloDAO.salvaAllaccio(connection, allaccio);
                }
                AllaccioVO allaccioBilaterale = new AllaccioVO();
                allaccioBilaterale.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection, NomiTabelle.PROTOCOLLO_ALLACCI));
                allaccioBilaterale.setProtocolloId(allaccio
                        .getProtocolloAllacciatoId());
                allaccioBilaterale.setProtocolloAllacciatoId(protocolloId);

                if (!protocolloDAO.esisteAllaccio(connection,
                        allaccioBilaterale.getProtocolloId(),
                        allaccioBilaterale.getProtocolloAllacciatoId())) {

                    allaccioBilaterale.setPrincipale(flagPrincipale);
                    protocolloDAO.salvaAllaccio(connection, allaccioBilaterale);
                }

            }
        }
    }

    private void salvaProcedimenti(Connection connection,
            ProtocolloVO protocollo, Collection procedimenti, Utente utente)
            throws Exception {
        int protocolloId = protocollo.getId().intValue();
        protocolloDAO.cancellaProcedimenti(connection, protocolloId);

        if (!procedimenti.isEmpty()) {
            ArrayList ids = new ArrayList();
            Iterator it = procedimenti.iterator();
            while (it.hasNext()) {
                ProtocolloProcedimentoVO protocolloProcedimentoVO = (ProtocolloProcedimentoVO) it
                        .next();
                protocolloProcedimentoVO.setProtocolloId(protocolloId);
                protocolloProcedimentoVO.setVersione(protocollo.getVersione());
                protocolloProcedimentoVO.setRowCreatedUser(utente
                        .getValueObject().getUsername());
                protocolloProcedimentoVO.setRowUpdatedUser(utente
                        .getValueObject().getUsername());
                protocolloProcedimentoVO.setRowCreatedTime(new Date(System
                        .currentTimeMillis()));
                protocolloProcedimentoVO.setRowUpdatedTime(new Date(System
                        .currentTimeMillis()));
                ids.add(protocolloProcedimentoVO);
            }
            protocolloDAO.inserisciProcedimenti(connection, ids);
        }
    }

    private void salvaFascicoli(Connection connection, ProtocolloVO protocollo,
            Utente utente) throws Exception {
        int protocolloId = protocollo.getId().intValue();
        FascicoloDelegate fd = FascicoloDelegate.getInstance();
        fd.rimuoviFascicoliProtocollo(connection, protocolloId);
        if (protocollo.getFascicoli() != null) {
            for (Iterator i = protocollo.getFascicoli().iterator(); i.hasNext();) {
                FascicoloVO fVO = (FascicoloVO) i.next();
                fd.salvaFascicoloProtocollo(connection, fVO, protocolloId,
                        utente.getValueObject().getUsername());

            }
        }
    }

    private ProtocolloVO registraProtocollo(Connection connection,
            Protocollo protocollo, Utente utente) throws Exception {

        RegistroVO registro = utente.getRegistroVOInUso();
        int numeroProtocollo = protocolloDAO.getMaxNumProtocollo(connection,registro.getAnnoCorrente(), registro.getId().intValue());
        ProtocolloVO protocolloVO = protocollo.getProtocollo();
        protocolloVO.setNumProtocollo(numeroProtocollo);

        // salvo ProtocolloVO
        protocolloVO.setId(IdentificativiDelegate.getInstance().getNextId(connection, NomiTabelle.PROTOCOLLI));
        ProtocolloVO protocolloSalvato = protocolloDAO.newProtocollo(connection, protocolloVO);
        Integer protocolloId = protocolloSalvato.getId();
        // salvo il documento principale
        protocollo.setDocumentoPrincipale(salvaDocumentoPrincipale(connection,protocolloSalvato, protocollo.getDocumentoPrincipale(),utente));

        // salvo i documenti allegati
        protocollo.setAllegati(salvaAllegati(connection, protocolloSalvato,
                protocollo.getAllegati()));

        // salvo gli Allacci
        salvaAllacci(connection, protocolloId.intValue(), protocollo
                .getAllacci(), utente, protocolloVO.getFlagTipo());

        // salvo i dati dei fascicoli
        salvaFascicoli(connection, protocolloVO, utente);

        // salvo i dati dei procedimenti
        salvaProcedimenti(connection, protocolloVO, protocollo
                .getProcedimenti(), utente);
        
        salvaMittenti(connection, protocolloVO);

        return protocolloSalvato;
    }
    
    

    private void salvaMittenti(Connection connection, ProtocolloVO protocolloVO) throws DataException, SQLException {
    	
    	MittentiDelegate mittentiDelegate = MittentiDelegate.getInstance();
    	mittentiDelegate.salvaMittenti(connection, protocolloVO);
	}

    /*
     * Questo metodo deve essere sincrono. Sincronizzare questo metodo per
     * eveitare "buchi" nei numeri di protocollo Errore: Due transazioni che
     * iniziano allo stesso tempo, se una fallisce vi sar? un buco nei numeri
     * progressivi del protocollo, dovrebbe fallire la transazione che per prima
     * ha ottenuto l'id.
     */


	public ProtocolloVO registraProtocolloIngresso(
            ProtocolloIngresso protocollo, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        ProtocolloVO pVO = null;
        OggettarioDelegate oggettario = OggettarioDelegate.getInstance();
        SoggettoDelegate soggettoDelegate = SoggettoDelegate.getInstance();
        try {
            jdbcMan = new JDBCManager();

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            pVO = registraProtocolloIngresso(connection, protocollo, utente);
            if(protocollo.isAddFisica() && "F".equals(protocollo.getProtocollo().getFlagTipoMittente())){
            	SoggettoVO soggetto = createSoggetto(protocollo, "F");
            	soggettoDelegate.salvaPersonaFisica(soggetto, utente);
                
            }if(protocollo.isAddGiuridica() && "G".equals(protocollo.getProtocollo().getFlagTipoMittente())){
            	SoggettoVO soggetto = createSoggetto(protocollo, "G");
            	soggettoDelegate.salvaPersonaGiuridica(soggetto, utente);
                
            }if(protocollo.isAddOggetto()){
            	OggettoVO oggettoVO = new OggettoVO(null, pVO.getOggetto());
                oggettario.salvaOggetto(oggettoVO);
                
            }
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn("Salvataggio Protocollo fallito, rolling back transction..",de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn("Salvataggio Protocollo fallito, rolling back transction..",se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return pVO;

    }

    private SoggettoVO createSoggetto(ProtocolloIngresso protocollo, String tipo) {
		SoggettoVO persona = new SoggettoVO(tipo);
		ProtocolloVO protocolloVO = protocollo.getProtocollo();
		if("F".equals(tipo)){
			persona.setCognome(protocolloVO.getCognomeMittente());
			persona.setNome(protocolloVO.getNomeMittente());
		}else if("G".equals(tipo)){
			persona.setDescrizioneDitta(protocolloVO.getDenominazioneMittente());
		}
		Indirizzo indirizzo = persona.getIndirizzo();
		indirizzo.setCap(protocolloVO.getMittenteCap());
		indirizzo.setComune(protocolloVO.getMittenteComune());
		indirizzo.setProvinciaId(protocolloVO.getMittenteProvinciaId());
		String indirizzoString = protocolloVO.getMittenteIndirizzo();
		if(indirizzoString != null && !"".equals(indirizzoString)){
			String[] indirizzoSplitted = indirizzoString.replace(",", " ").split(" ");
			String[] numeroAndToponimo = getNumeroAndToponimo(indirizzoSplitted);
			indirizzo.setToponimo(numeroAndToponimo[0]);
			indirizzo.setCivico(numeroAndToponimo[1]);
		}
		return persona;
	}

	private String[] getNumeroAndToponimo(String[] indirizzoSplitted) {
		String civico = "";
		String toponimo = "";
		for(String elemento : indirizzoSplitted){
			try{
				Integer.parseInt(elemento);
				civico = elemento;
			}catch (Exception e) {
				toponimo += elemento+" ";
			}
		}
		return new String[]{toponimo, civico};
		
		
	}

	public ProtocolloVO registraProtocolloIngresso(Connection connection,
            ProtocolloIngresso protocollo, Utente utente) throws Exception {

        ProtocolloVO protocolloSalvato = new ProtocolloVO();
        protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
        logger.info("ProtocolloDelegate:registraProtocolloIngresso");

        protocolloSalvato = registraProtocollo(connection, protocollo, utente);

        protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
        int protocolloId = protocolloSalvato.getId().intValue();
        // salva assegnatari
        Collection assegnatari = protocollo.getAssegnatari();
        if (assegnatari != null) {
            for (Iterator i = assegnatari.iterator(); i.hasNext();) {
                AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
                assegnatario.setProtocolloId(protocolloId);
                assegnatario.setId(IdentificativiDelegate.getInstance().getNextId(connection,NomiTabelle.PROTOCOLLO_ASSEGNATARI));
                protocolloDAO.salvaAssegnatario(connection, assegnatario, protocolloSalvato.getVersione());
            }
        }
        
        // salva mittenti
        List<SoggettoVO> mittenti = protocollo.getProtocollo().getMittenti();
        
        
        // Registro la segnatura
        SegnaturaVO segnaturaVO = new SegnaturaVO();
        segnaturaVO.setFkProtocolloId(protocolloId);
        segnaturaVO.setRowCreatedUser(utente.getValueObject().getUsername());
        segnaturaVO.setTipoProtocollo(protocollo.getProtocollo().getFlagTipo());
        segnaturaVO.setTextSegnatura(ProtocolloBO.getSignature(protocollo));
        segnaturaVO.setId(IdentificativiDelegate.getInstance().getNextId(connection, NomiTabelle.SEGNATURE));
        protocolloDAO.salvaSegnatura(connection, segnaturaVO);

        /*
         * TODO controllare il valore del statusFlag ed eseguire le operazioni
         * corrispondenti. es. se errore (statusFlag) rollbak
         */
        protocolloSalvato.setReturnValue(ReturnValues.SAVED);

        // se il protocollo � stato salvato eliminiamo i file temporanei:
        // principale e allegati
        if (protocolloSalvato.getReturnValue() == ReturnValues.SAVED) {
            Collection docs = new ArrayList(protocollo.getAllegati().values());
            docs.add(protocollo.getDocumentoPrincipale());
            for (Iterator i = docs.iterator(); i.hasNext();) {
                DocumentoVO doc = (DocumentoVO) i.next();
                if (doc != null && doc.getPath() != null) {
                    File f = new File(doc.getPath());
                    f.delete();
                }
            }
        }
        // se l'assegnatarioId � maggiore di Zero � stato selezionato un
        // utente come assegnatario
        // di competenza.
        //Modifica Daniele Sanna per gestire l'invio della mail
        //a più assegnatari per competenza (12/09/2008)
        List<Integer> assegnatarioId = ProtocolloBO.notificaAssegnatarioCompetenza(protocollo.getAssegnatari());
        if (assegnatarioId.size() > 0) {
        	for(Integer id : assegnatarioId){
        		UtenteVO utenteVO = UtenteDelegate.getInstance().getUtente(
        				id);
                AreaOrganizzativaVO aoo = Organizzazione.getInstance()
                        .getAreaOrganizzativa(utenteVO.getAooId()).getValueObject();
                // notifica via email
                // TODO : permettere la notifica multiassegnatari
                if (aoo.getPn_smtp() != null && !"".equals(aoo.getPn_smtp().trim())
                        && aoo.getPn_username() != null
                        && !"".equals(aoo.getPn_username().trim())
                        && aoo.getPn_pwd() != null
                        && !"".equals(aoo.getPn_pwd().trim())
                        && aoo.getPn_indirizzo() != null
                        && !"".equals(aoo.getPn_indirizzo().trim())
                        && utenteVO.getEmailAddress() != null
                        && !"".equals(utenteVO.getEmailAddress().trim())) {
                    String subjectMsg = aoo.getDescription()
                            + ": Notifica assegnazione protocollo";
                    String bodyMsg = prepareMessage(protocolloSalvato);
                    try {
                        EmailUtil.sendNoAttachement(aoo.getPn_smtp(), aoo
                                .getPn_username(), aoo.getPn_pwd(), aoo
                                .getPn_indirizzo(), utenteVO.getEmailAddress(),
                                subjectMsg, bodyMsg);
                    } catch (Exception e1) {
                        logger
                                .warn(
                                        "Non � stato possibile inviare la mail di notifica assegnazione.",
                                        e1);
                    }
                }
        	}
            
        }
        // se proveniente da email segniamo l'email come protocollata
        if (protocollo.getMessaggioEmailId() != null) {
            EmailDelegate.getInstance().aggiornaStatoEmailIngresso(connection,
                    protocollo.getMessaggioEmailId().intValue(),
                    EmailConstants.MESSAGGIO_INGRESSO_PROTOCOLLATO);

        }
        return protocolloSalvato;
    }

    private ProtocolloVO aggiornaProtocollo(Connection connection,
            Protocollo protocollo, Utente utente) throws Exception {
        ProtocolloVO protocolloVO = protocollo.getProtocollo();
        protocolloVO.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        protocolloVO.setRowUpdatedUser(utente.getValueObject().getUsername());
        // salvo ProtocolloVO

        ProtocolloVO protocolloSalvato = protocolloDAO.aggiornaProtocollo(
                connection, protocolloVO);

        if (protocolloSalvato.getReturnValue() == ReturnValues.SAVED) {
            Integer protocolloId = protocolloSalvato.getId();

            // salvo il documento principale
            protocollo.setDocumentoPrincipale(salvaDocumentoPrincipale(
                    connection, protocollo.getProtocollo(), protocollo
                            .getDocumentoPrincipale(), utente));

            // salvo i documenti allegati
            protocollo.setAllegati(salvaAllegati(connection, protocolloSalvato,
                    protocollo.getAllegati()));

            // salvo gli Allacci
            /*
             * protocolloDAO.eliminaAllacciProtocollo(connection, protocolloId
             * .intValue());
             */
            eliminaAllacci(connection, protocolloId.intValue(), utente);
            salvaAllacci(connection, protocolloId.intValue(), protocollo
                    .getAllacci(), utente, protocolloVO.getFlagTipo());

            // fascicoli
            salvaFascicoli(connection, protocolloVO, utente);

            // procedimenti
            salvaProcedimenti(connection, protocolloSalvato, protocollo
                    .getProcedimenti(), utente);
        }
        return protocolloSalvato;
    }

    public ProtocolloVO aggiornaProtocolloIngresso(
            ProtocolloIngresso protocollo, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        ProtocolloVO protocolloSalvato = new ProtocolloVO();
        protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
        logger.info("ProtocolloDelegate:aggiornaProtocolloIngresso");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            protocolloSalvato = aggiornaProtocollo(connection, protocollo,
                    utente);
            if (protocolloSalvato.getReturnValue() == ReturnValues.SAVED) {

                protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
                int protocolloId = protocolloSalvato.getId().intValue();
                // salva assegnatari
                protocolloDAO.eliminaAssegnatariProtocollo(connection,
                        protocolloId);
                Collection assegnatari = protocollo.getAssegnatari();
                if (assegnatari != null) {
                    for (Iterator i = assegnatari.iterator(); i.hasNext();) {
                        AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
                        assegnatario.setProtocolloId(protocolloId);
                        assegnatario.setId(IdentificativiDelegate.getInstance()
                                .getNextId(connection,
                                        NomiTabelle.PROTOCOLLO_ASSEGNATARI));
                        protocolloDAO.salvaAssegnatario(connection,
                                assegnatario, protocolloSalvato.getVersione());
                    }
                }
                // Registro la segnatura
                /*
                 * SegnaturaVO segnaturaVO = new SegnaturaVO(); ProtocolloBO
                 * protocolloBO = new ProtocolloBO();
                 * segnaturaVO.setFkProtocolloId(protocolloId);
                 * segnaturaVO.setRowCreatedUser(utente.getValueObject().getUsername());
                 * segnaturaVO.setTipoProtocollo(protocollo.getProtocollo().getFlagTipo());
                 * segnaturaVO.setTextSegnatura(protocolloBO.getSignature(protocollo,
                 * utente)); protocolloDAO.salvaSegnatura(connection,
                 * segnaturaVO);
                 */
                /*
                 * TODO controllare il valore del statusFlag ed eseguire le
                 * operazioni corrispondenti. es. se errore (statusFlag) rollbak
                 */
                connection.commit();
                protocolloSalvato.setReturnValue(ReturnValues.SAVED);
            } else {
                jdbcMan.rollback(connection);
            }
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocollo fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocollo fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return protocolloSalvato;
    }

    public ProtocolloVO aggiornaProtocolloUscita(ProtocolloUscita protocollo,
            Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        ProtocolloVO protocolloSalvato = new ProtocolloVO();
        protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
        logger.info("ProtocolloDelegate:aggiornaProtocolloUscita");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            // salva dati comuni
            int protocolloId = protocollo.getProtocollo().getId().intValue();
            protocolloSalvato = aggiornaProtocollo(connection, protocollo,
                    utente);
            if (protocolloSalvato.getReturnValue() == ReturnValues.SAVED) {

                protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
                // salva destinatari
                // protocolloDAO.eliminaMessiSpedDestinatariProtocollo(protocolloId)
                protocolloDAO.eliminaDestinatariProtocollo(connection,
                        protocolloId);
                Collection assegnatari = protocollo.getDestinatari();
                if (assegnatari != null) {
                    for (Iterator i = assegnatari.iterator(); i.hasNext();) {
                        DestinatarioVO destinatario = (DestinatarioVO) i.next();
                        destinatario.setProtocolloId(protocolloId);
                        destinatario.setId(IdentificativiDelegate.getInstance()
                                .getNextId(connection,
                                        NomiTabelle.PROTOCOLLO_DESTINATARI));
                        protocolloDAO.salvaDestinatario(connection,
                                destinatario, protocolloSalvato.getVersione());

                    }
                }

                // Registro la segnatura
                /*
                 * SegnaturaVO segnaturaVO = new SegnaturaVO(); ProtocolloBO
                 * protocolloBO = new ProtocolloBO();
                 * segnaturaVO.setFkProtocolloId(protocolloId);
                 * segnaturaVO.setRowCreatedUser(utente.getValueObject().getUsername());
                 * segnaturaVO.setTipoProtocollo(protocollo.getProtocollo().getFlagTipo());
                 * segnaturaVO.setTextSegnatura(protocolloBO.getSignature(protocollo,
                 * utente)); protocolloDAO.salvaSegnatura(connection,
                 * segnaturaVO);
                 */
                /*
                 * TODO controllare il valore del statusFlag ed eseguire le
                 * operazioni corrispondenti. es. se errore (statusFlag) rollbak
                 */
                connection.commit();
                protocolloSalvato.setReturnValue(ReturnValues.SAVED);
            } else {
                jdbcMan.rollback(connection);
            }

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocollo fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocollo fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return protocolloSalvato;
    }

    public ProtocolloVO registraProtocolloUscita(ProtocolloUscita protocollo,
            Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        ProtocolloVO pVO = null;
        OggettarioDelegate oggettario = OggettarioDelegate.getInstance();
        try {
            jdbcMan = new JDBCManager();

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            pVO = registraProtocolloUscita(connection, protocollo, utente);
            if(protocollo.isAddOggetto()){
            	OggettoVO oggettoVO = new OggettoVO(null, pVO.getOggetto());
                oggettario.salvaOggetto(oggettoVO);
                
            }
            registraDestinatariRubrica(protocollo.getDestinatariToSaveId(), protocollo, utente);
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocollo fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocollo fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return pVO;

    }

    private void registraDestinatariRubrica(String[] destinatariToSaveId, ProtocolloUscita protocollo, Utente utente) {
    	SoggettoDelegate soggettoDelegate = SoggettoDelegate.getInstance();
    	if(destinatariToSaveId != null){
	    	for(String index : destinatariToSaveId){
	    		DestinatarioVO destinatarioVO = protocollo.getDestinatario(Integer.parseInt(index));
	    		SoggettoVO soggetto = createSoggettoFromDestinatario(destinatarioVO);	
		    	if("F".equals(soggetto.getTipo())){
		         	soggettoDelegate.salvaPersonaFisica(soggetto, utente);   
		        }else if("G".equals(soggetto.getTipo())){
		         	soggettoDelegate.salvaPersonaGiuridica(soggetto, utente);
		        }
	    	}
    	}
	}
    
    private SoggettoVO createSoggettoFromDestinatario(DestinatarioVO destinatarioVO) {
		String tipo = destinatarioVO.getFlagTipoDestinatario();
    	SoggettoVO persona = new SoggettoVO(tipo);
		if("F".equals(tipo)){
			persona.setCognome(destinatarioVO.getCognome());
			persona.setNome(destinatarioVO.getNome());
		}else if("G".equals(tipo)){
			persona.setDescrizioneDitta(destinatarioVO.getDestinatario());
		}
		Indirizzo indirizzo = persona.getIndirizzo();
		indirizzo.setCap(destinatarioVO.getCodicePostale());
		indirizzo.setComune(destinatarioVO.getCitta());
		
		String indirizzoString = destinatarioVO.getIndirizzo();
		if(indirizzoString != null && !"".equals(indirizzoString)){
			String[] indirizzoSplitted = indirizzoString.replace(",", " ").split(" ");
			String[] numeroAndToponimo = getNumeroAndToponimo(indirizzoSplitted);
			indirizzo.setToponimo(numeroAndToponimo[0]);
			indirizzo.setCivico(numeroAndToponimo[1]);
		}
		return persona;
	}

	public ProtocolloVO registraProtocolloUscita(Connection connection,
            ProtocolloUscita protocollo, Utente utente) throws Exception {
        ProtocolloVO protocolloSalvato = new ProtocolloVO();
        protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
        logger.info("ProtocolloDelegate:registraProtocolloUscita");

        // salva dati comuni
        protocolloSalvato = registraProtocollo(connection, protocollo, utente);

        protocolloSalvato.setReturnValue(ReturnValues.UNKNOWN);
        int protocolloId = protocolloSalvato.getId().intValue();
        // salva destinatari
        Collection destinatari = protocollo.getDestinatari();
        if (destinatari != null) {
            for (Iterator i = destinatari.iterator(); i.hasNext();) {
                DestinatarioVO destinatario = (DestinatarioVO) i.next();
                destinatario.setProtocolloId(protocolloId);
                destinatario.setId(IdentificativiDelegate.getInstance()
                        .getNextId(connection,
                                NomiTabelle.PROTOCOLLO_DESTINATARI));
                protocolloDAO.salvaDestinatario(connection, destinatario,
                        protocolloSalvato.getVersione());

            }
        }
        // Registro la segnatura
        SegnaturaVO segnaturaVO = new SegnaturaVO();
        segnaturaVO.setFkProtocolloId(protocolloId);
        segnaturaVO.setRowCreatedUser(utente.getValueObject().getUsername());
        segnaturaVO.setTipoProtocollo(protocollo.getProtocollo().getFlagTipo());
        segnaturaVO.setTextSegnatura(ProtocolloBO.getSignature(protocollo));
        segnaturaVO.setId(IdentificativiDelegate.getInstance().getNextId(
                connection, NomiTabelle.SEGNATURE));
        protocolloDAO.salvaSegnatura(connection, segnaturaVO);

        Collection destinatariViaEmail = ProtocolloBO
                .getDestinatariViaEmail(protocollo.getDestinatari());
        if (!destinatariViaEmail.isEmpty()) {
            // salvare il mimemessage ottenuto nella tabella
            // coda_invio_email
            EmailDelegate.getInstance().salvaMessaggioPerInvio(
                    connection,
                    IdentificativiDelegate.getInstance().getNextId(connection,
                            NomiTabelle.EMAIL_CODA_INVIO),
                    protocolloSalvato.getAooId(), protocolloId,
                    destinatariViaEmail);
        }
        /*
         * Si controlla se il protocollo � stato generato con le funzioni:
         * Documenti da archivio, in questo caso aggiorno lo stato del documento
         * nella tabella invio_classificati
         * 
         * Fascicoli da Archivio
         */
        if (protocollo.getFascicoloInvioId() > 0) {
            FascicoloDelegate.getInstance().eliminaCodaInvioFascicolo(
                    connection, protocollo.getFascicoloInvioId());
            int versioneFascicolo = FascicoloDelegate.getInstance()
                    .getFascicoloVOById(protocollo.getFascicoloInvioId())
                    .getVersione();
            FascicoloDelegate.getInstance().aggiornaStatoFascicolo(connection,
                    protocollo.getFascicoloInvioId(),
                    Parametri.STATO_FASCICOLO_APERTO,
                    utente.getValueObject().getUsername(), versioneFascicolo);
        } else if (protocollo.getDocumentoInvioId() > 0) {
            DocumentaleDelegate.getInstance().eliminaCodaInvioDocumento(
                    connection, protocollo.getDocumentoInvioId(), "1");
            int dfrId = DocumentaleDelegate.getInstance().getFileId(connection,
                    protocollo.getDocumentoInvioId());
            DocumentaleDelegate.getInstance().eliminaDocumento(connection,
                    protocollo.getDocumentoInvioId(), dfrId);
        }

        /*
         * TODO controllare il valore del statusFlag ed eseguire le operazioni
         * corrispondenti. es. se errore (statusFlag) rollbak
         */

        protocolloSalvato.setReturnValue(ReturnValues.SAVED);
        return protocolloSalvato;
    }

    public Collection getAllacciProtocollo(int protocolloId) {
        try {
            return protocolloDAO.getAllacciProtocollo(protocolloId);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getting getAllacci: ");
            return null;
        }
    }

    public Collection getAssegnatariProtocollo(int protocolloId) {
        try {
            return protocolloDAO.getAssegnatariProtocollo(protocolloId);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getting getAssegnatari: ");
            return null;
        }
    }

    public Collection getProtocolliAllacciabili(Utente utente,
            int annoProtocolo, int numeroProtocolloDa, int numeroProtocolloA,
            int protocolloId) {
        try {
            return protocolloDAO.getProtocolliAllacciabili(utente,
                    annoProtocolo, numeroProtocolloDa, numeroProtocolloA,
                    protocolloId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliAllacciabili: ");
            return null;
        }
    }

    public int contaProtocolliAllacciabili(Utente utente, int annoProtocolo,
            int numeroProtocolloDa, int numeroProtocolloA, int protocolloId) {
        try {
            return contaProtocolliDAO.contaProtocolliAllacciabili(utente,
                    annoProtocolo, numeroProtocolloDa, numeroProtocolloA,
                    protocolloId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting contaProtocolliAllacciabili: ");
            return 0;
        }
    }

    public ProtocolloVO getProtocollo_By_Numero(int anno, int registro,
            int numProtocollo) {
        try {
            return protocolloDAO.getProtocolloByNumero(anno, registro,
                    numProtocollo);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliAllacciabili: ");
            return null;
        }

    }

    public Map getProtocolliAssegnati(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA,
            String statoProtocollo, String statoScarico,
            String tipoUtenteUfficio) {
        try {
            return protocolloDAO.getProtocolliAssegnati(utente,
                    annoProtocolloDa, annoProtocolloA, numeroProtocolloDa,
                    numeroProtocolloA, dataDa, dataA, statoProtocollo,
                    statoScarico, tipoUtenteUfficio);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliAssegnati: ");
            return null;
        }
    }

    public int contaProtocolliAssegnati(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA,
            String statoProtocollo, String statoScarico,
            String tipoUtenteUfficio) {
        try {

            return contaProtocolliDAO.contaProtocolliAssegnati(utente,
                    annoProtocolloDa, annoProtocolloA, numeroProtocolloDa,
                    numeroProtocolloA, dataDa, dataA, statoProtocollo,
                    statoScarico, tipoUtenteUfficio);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting contaProtocolli: ");
            return 0;
        }

    }

    // TODO: link Doc
    public String getDocId(int documentoId) {
        try {
            return protocolloDAO.getDocId(documentoId);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getting getDocId: ");
            return null;
        }
    }

    public Map getProtocolliRespinti(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA) {
        try {
            return protocolloDAO.getProtocolliRespinti(utente,
                    annoProtocolloDa, annoProtocolloA, numeroProtocolloDa,
                    numeroProtocolloA, dataDa, dataA);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliRespinti: ");
            return null;
        }

    }

    public int contaProtocolliRespinti(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA) {
        try {

            return contaProtocolliDAO.contaProtocolliRespinti(utente,
                    annoProtocolloDa, annoProtocolloA, numeroProtocolloDa,
                    numeroProtocolloA, dataDa, dataA);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting contaProtocolliRespinti: ");
            return 0;
        }

    }

    public int rifiutaProtocollo(ProtocolloIngresso protocollo,
            String tipoAzione, String statoProtocollo, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:rifiutaProtocollo");
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            statusFlag = rifiutaProtocollo(connection, protocollo, tipoAzione,
                    statoProtocollo, utente);
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn("rifiutaProtocollo fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn("rifiutaProtocollo fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;

    }

    private int rifiutaProtocollo(Connection connection,
            ProtocolloIngresso protocollo, String tipoAzione,
            String StatoProtocollo, Utente utente) throws Exception {
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:rifiutaProtocollo");

        ProtocolloVO protocolloVO = protocollo.getProtocollo();
        statusFlag = protocolloDAO.updateScarico(connection, protocolloVO,
                StatoProtocollo, utente);
        // if (statusFlag == ReturnValues.SAVED) {
        // statusFlag = protocolloDAO.presaIncarico(connection, protocolloVO,
        // tipoAzione, utente);
        // }
        protocolloDAO.updateMsgAssegnatarioCompetenteByIdProtocollo(connection,
                protocollo.getMsgAssegnatarioCompetente(), protocolloVO.getId()
                        .intValue());

        statusFlag = ReturnValues.SAVED;
        return statusFlag;

    }

    public int presaInCarico(Collection protocolli, String tipoAzione,
            Utente utente) {
        // tipoAzione A=Accetta, R=Respingi
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:presaInCarico");
        String StatoProtocollo = "";
        try {
            jdbcMan = new JDBCManager();

            if ("R".equals(tipoAzione)) {
                StatoProtocollo = "F";
            } else if ("A".equals(tipoAzione)) {
                StatoProtocollo = "N";
            }

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            Iterator it = protocolli.iterator();
            while (it.hasNext()) {
                ProtocolloVO protocolloVO = (ProtocolloVO) it.next();
                statusFlag = protocolloDAO.updateScarico(connection,
                        protocolloVO, StatoProtocollo, utente);
                if (statusFlag == ReturnValues.SAVED) {
                    statusFlag = protocolloDAO.presaIncarico(connection,
                            protocolloVO, tipoAzione, utente);
                }
            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("ProtocolloDelegate: failed presaInCarico: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);

        }
        return statusFlag;

    }

    public int rifiutaProtocolli(Collection protocolli, Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:rifiutaProtocolli");
        try {
            jdbcMan = new JDBCManager();

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            Iterator it = protocolli.iterator();
            while (it.hasNext()) {
                ProtocolloIngresso protocollo = (ProtocolloIngresso) it.next();
                statusFlag = rifiutaProtocollo(connection, protocollo, "R",
                        "F", utente);
            }
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn("rifiutaProtocolli fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn("rifiutaProtocolli fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;

    }

    public int riassegnaProtocollo(ProtocolloIngresso protocollo, Utente utente)
            throws DataException {
        try {
            return protocolloDAO.riassegnaProtocollo(protocollo, utente);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting riassegnaProtocollo: ");
            return ReturnValues.UNKNOWN;
        }
    }

    public int updateScarico(ProtocolloVO protocolloVO, String flagScarico,
            Utente utente) {
        // flagScarico N=in Lavorazione, A=agli Atti, R=in Risposta
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:updateScarico");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            statusFlag = updateScarico(protocolloVO, flagScarico, utente,
                    connection);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("ProtocolloDelegate: failed updateScarico: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int updateScarico(ProtocolloVO protocolloVO, String flagScarico,
            Utente utente, Connection connection) throws DataException {
        logger.info("ProtocolloDelegate:updateScarico");
        return protocolloDAO.updateScarico(connection, protocolloVO,
                flagScarico, utente);
    }

    public int annullaProtocollo(ProtocolloVO protocolloVO, Utente utente)
            throws DataException {
        // flagScarico N=in Lavorazione, A=agli Atti, R=in Risposta
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:annullaProtocollo");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            statusFlag = protocolloDAO.annullaProtocollo(connection,
                    protocolloVO, utente);
            connection.commit();
            statusFlag = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("ProtocolloDelegate: failed annullaProtocollo: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int contaProtocolli(Utente utente, Ufficio ufficio, HashMap sqlDB) {
        try {

            return contaProtocolliDAO.contaProtocolli(utente, ufficio, sqlDB);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting contaProtocolli: ");
            return 0;
        }

    }

    public SortedMap cercaProtocolli(Utente utente, Ufficio ufficio,
            HashMap sqlDB) {
        try {

            return protocolloDAO.cercaProtocolli(utente, ufficio, sqlDB);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting cercaProtocolli: ");
            return null;
        }

    }

    public Collection getProtocolliByProtMittente(Utente utente,
            String protMittente) {
        try {
            return protocolloDAO.getProtocolliByProtMittente(utente,
                    protMittente);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliByProtMittente: ");
            return null;
        }

    }

    public Map getDestinatariProtocollo(int protocolloId) {
        try {
            return protocolloDAO.getDestinatariProtocollo(protocolloId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getDestinatariProtocollo: ");
            return null;
        }
    }

    public Collection getDestinatari(String destinatario) {
        try {

            return protocolloDAO.getDestinatari(destinatario);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getting getDestinatari: ");
            return null;
        }
    }

    public Collection getMittenti(String mittente) {
        try {

            return protocolloDAO.getMittenti(mittente);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getting getMittenti: ");
            return null;
        }
    }

    public boolean isUtenteAbilitatoView(Utente utente, Ufficio uff,
            int protocolloId) {
        try {
            return protocolloDAO.isUtenteAbilitatoView(utente, uff,
                    protocolloId);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed isUtenteAbilitatoView: ");
            return false;
        }

    }

    public ProtocolloVO getProtocolloById(int id) {
        try {
            return protocolloDAO.getProtocolloById(id);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getProtocolloById: ");
            return null;
        }
    }

    public ReportProtocolloView getProtocolloView(int id) {
        try {
            return protocolloDAO.getProtocolloView(id);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getProtocolloView: ");
            return null;
        }
    }

    public ReportProtocolloView getProtocolloView(Connection connection, int id) {
        try {
            return protocolloDAO.getProtocolloView(connection, id);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getProtocolloView: ");
            return null;
        }
    }

    public ProtocolloVO getProtocolloById(Connection connection, int id) {
        logger.info("ProtocolloDelegate:getProtocolloById");
        try {
            return protocolloDAO.getProtocolloById(connection, id);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getProtocolloById: ");
            return null;
        }
    }

    public AssegnatarioVO getAssegnatarioPerCompetenza(int protocolloId) {
        logger.info("ProtocolloDelegate:getAssegnatarioPerCompetenza");
        try {
            return protocolloDAO.getAssegnatarioPerCompetenza(protocolloId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getAssegnatarioPerCompetenza: ");
            return null;
        }
    }

    public AssegnatarioVO getAssegnatarioPerCompetenza(Connection connection,
            int protocolloId) {
        logger.info("ProtocolloDelegate:getAssegnatarioPerCompetenza");
        try {
            return protocolloDAO.getAssegnatarioPerCompetenza(connection,
                    protocolloId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getAssegnatarioPerCompetenza: ");
            return null;
        }
    }

    public int getDocumentoDefault(int aooId) {

        logger.info("ProtocolloDelegate:getDocumentoDefault");
        try {
            return protocolloDAO.getDocumentoDefault(aooId);
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getDocumentoDefault: ");
            return 0;
        }

    }

    public ProtocolloIngresso getProtocolloIngressoById(int id) {
        ProtocolloIngresso pi = new ProtocolloIngresso();
        try {
            pi.setProtocollo(protocolloDAO.getProtocolloById(id));
            pi.setAllegati(protocolloDAO.getAllegatiProtocollo(id));
            if (pi.getProtocollo().getDocumentoPrincipaleId() != null) {
                pi.setDocumentoPrincipale(DocumentoDelegate.getInstance()
                        .getDocumento(
                                pi.getProtocollo().getDocumentoPrincipaleId()
                                        .intValue()));
            }
            pi.aggiungiAssegnatari(getAssegnatariProtocollo(id));
            pi.setAllacci(getAllacciProtocollo(id));
            pi.setProcedimenti(protocolloDAO.getProcedimentiProtocollo(id));
            setFascicoliProtocollo(pi, id);
            return pi;
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getProtocolloById: ");
            return null;
        }
    }

    private void setFascicoliProtocollo(Protocollo p, int protocolloId) {
        p.getProtocollo().setFascicoli(
                FascicoloDelegate.getInstance().getFascicoliByProtocolloId(
                        protocolloId));
    }

    public ProtocolloUscita getProtocolloUscitaById(int id) {
        ProtocolloUscita pu = new ProtocolloUscita();
        try {
            pu.setProtocollo(protocolloDAO.getProtocolloById(id));
            pu.setAllegati(protocolloDAO.getAllegatiProtocollo(id));
            if (pu.getProtocollo().getDocumentoPrincipaleId() != null) {
                pu.setDocumentoPrincipale(DocumentoDelegate.getInstance()
                        .getDocumento(
                                pu.getProtocollo().getDocumentoPrincipaleId()
                                        .intValue()));
            }
            pu.setDestinatari(getDestinatariProtocollo(id));
            // imposto il mittente con i dati relativi all'assegnatario per
            // competenza
            AssegnatarioVO assegnatario = new AssegnatarioVO();
            assegnatario.setUfficioAssegnatarioId(pu.getProtocollo()
                    .getUfficioMittenteId());
            assegnatario.setUtenteAssegnatarioId(pu.getProtocollo()
                    .getUtenteMittenteId());
            pu.setMittente(assegnatario);
            pu.setAllacci(getAllacciProtocollo(id));
            pu.setProcedimenti(protocolloDAO.getProcedimentiProtocollo(id));
            setFascicoliProtocollo(pu, id);
            return pu;
        } catch (DataException de) {
            logger.error("ProtocolloDelegate: failed getProtocolloById: ");
            return null;
        }
    }

    private String prepareMessage(ProtocolloVO protocollo) {
        StringBuffer stB = new StringBuffer();
        stB.append("Protocollo N. " + protocollo.getNumProtocollo() + "\r\n");
        stB.append("Tipo: " + protocollo.getFlagTipo() + "\r\n");
        stB.append("Data Registrazione "
                + DateUtil.formattaData(protocollo.getDataRegistrazione()
                        .getTime()) + "\r\n");
        stB.append("Oggetto: " + protocollo.getOggetto() + "\r\n");
        if (protocollo.getFlagTipoMittente().equals("F")) {
            stB.append("Mittente: " + protocollo.getCognomeMittente() + " "
                    + StringUtil.getStringa(protocollo.getNomeMittente())
                    + "\r\n");
        } else {
            stB.append("Mittente: " + protocollo.getDenominazioneMittente()
                    + "\r\n");
        }
        return stB.toString();
    }

    // gestione registro d'emergenza

    public Collection getProtocolliToExport(int registroId) throws Exception {
        JDBCManager jdbcLocal = null;
        Connection connLocal = null;
        Collection protocolli = new ArrayList();
        try {
            // connessione al db locale d'emergenza
            jdbcLocal = (JDBCManager) config.getServletContext().getAttribute(
                    Constants.JDBCMAN_1);
            connLocal = jdbcLocal.getConnection();
            protocolli = protocolloDAO.getProtocolliToExport(connLocal,
                    registroId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliToExport: ");
        } finally {
            jdbcLocal.close(connLocal);
        }
        return protocolli;
    }

    private Collection getProtocolliToExport(Connection connLocal,
            int registroId) {
        try {
            return protocolloDAO.getProtocolliToExport(connLocal, registroId);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting getProtocolliToExport: ");
            return null;
        }
    }

    private void updateRegistroEmergenza(Connection connLocal) {
        try {
            protocolloDAO.updateRegistroEmergenza(connLocal);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting updateRegistroEmergenza: ");
        }
    }

    public synchronized ProtocolloVO registraProtocollo(Object protocollo,
            Utente utente, boolean uscita) {
        if (uscita) {
            return registraProtocolloUscita((ProtocolloUscita) protocollo,
                    utente);
        } else {
            return registraProtocolloIngresso((ProtocolloIngresso) protocollo,
                    utente);
        }
    }

    public int acquisizioneMassiva(Utente utente, HashMap documenti)
            throws Exception {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = ReturnValues.UNKNOWN;
        logger.info("ProtocolloDelegate:acquisizioneMassiva");

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            int protocolloId = 0;
            for (Iterator it = documenti.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                protocolloId = ((Integer) key).intValue();

                DocumentoVO doc = (DocumentoVO) value;
                ProtocolloVO pVO = ProtocolloDelegate.getInstance()
                        .getProtocolloById(connection, protocolloId);
                ProtocolloDelegate.getInstance().salvaDocumentoPrincipale(
                        connection, pVO, doc, utente);

            }
            connection.commit();
            statusFlag = ReturnValues.SAVED;

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("ProtocolloDelegate: failed acquisizioneMassiva: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int registraProtocolliEmergenza(int numProtIngresso,
            int numProtUscita, Utente utente) {

        int tipoDocumentoId = ProtocolloDelegate.getInstance()
                .getDocumentoDefault(utente.getRegistroVOInUso().getAooId());

        JDBCManager jdbcMan = null;
        Connection connection = null;
        int returnValue = ReturnValues.UNKNOWN;

        try {
            Timestamp dataRegistrazione = new Timestamp(System
                    .currentTimeMillis());
            jdbcMan = new JDBCManager();

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            ProtocolloVO protocollo = null;

            int ufficioId = utente.getUfficioVOInUso().getId().intValue();
            int utenteId = utente.getValueObject().getId().intValue();
            int aooId = utente.getUfficioVOInUso().getAooId();
            AssegnatarioVO assVO;
            assVO = new AssegnatarioVO();
            assVO.setCompetente(true);
            assVO.setUfficioAssegnanteId(ufficioId);
            assVO.setUtenteAssegnanteId(utenteId);
            assVO.setUfficioAssegnatarioId(ufficioId);
            assVO.setDataAssegnazione(dataRegistrazione);

            for (int i = 0; i < numProtIngresso; i++) {
                ProtocolloIngresso protocolloIngresso = null;
                protocolloIngresso = ProtocolloBO
                        .getDefaultProtocolloIngresso(utente);
                ProtocolloVO pVO = protocolloIngresso.getProtocollo();
                pVO.setOggetto("protocollo da registro emergenza");
                pVO.setFlagTipoMittente("F");
                pVO.setCognomeMittente("Registro Emergenza");
                pVO.setDataRegistrazione(dataRegistrazione);
                pVO.setAooId(aooId);
                pVO.setUfficioProtocollatoreId(ufficioId);
                pVO.setUtenteProtocollatoreId(utenteId);
                pVO.setNumProtocolloEmergenza(-1);
                pVO.setTipoDocumentoId(tipoDocumentoId);
                pVO.setStatoProtocollo("S");
                pVO
                        .setDescrizioneAnnotazione("protocollo da registro emergenza n� "
                                + String.valueOf(i + 1));
                protocolloIngresso.setProtocollo(pVO);

                protocolloIngresso.aggiungiAssegnatario(assVO);

                protocollo = registraProtocolloIngresso(connection,
                        protocolloIngresso, utente);

            }
            DestinatarioVO destVO = new DestinatarioVO();
            destVO.setDestinatario("protocollo Emergenza");
            destVO.setFlagTipoDestinatario("F");
            destVO.setFlagConoscenza(false);
            for (int i = 0; i < numProtUscita; i++) {
                ProtocolloUscita protocolloUscita = null;
                protocolloUscita = ProtocolloBO
                        .getDefaultProtocolloUscita(utente);

                ProtocolloVO pVO = protocolloUscita.getProtocollo();
                pVO.setOggetto("protocollo da registro emergenza");
                pVO.setNumProtocolloEmergenza(-1);
                pVO.setDataRegistrazione(dataRegistrazione);
                pVO.setAooId(aooId);
                pVO.setFlagTipoMittente("G");
                pVO.setDenominazioneMittente("Registro Emergenza");
                pVO.setUfficioMittenteId(ufficioId);
                pVO.setUfficioProtocollatoreId(ufficioId);
                pVO.setUtenteProtocollatoreId(utenteId);
                pVO.setTipoDocumentoId(tipoDocumentoId);
                pVO.setMozione(true);
                pVO.setStatoProtocollo("N");
                pVO
                        .setDescrizioneAnnotazione("protocollo da registro emergenza n� "
                                + String.valueOf(i + 1));
                protocolloUscita.setProtocollo(pVO);

                protocolloUscita.setMittente(assVO);
                protocolloUscita.addDestinatari(destVO);

                protocollo = registraProtocolloUscita(connection,
                        protocolloUscita, utente);

            }

            connection.commit();
            returnValue = ReturnValues.SAVED;
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocolli Registro emergenza fallito, rolling back transction..",
                            de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger
                    .warn(
                            "Salvataggio Protocolli Registro emergenza fallito, rolling back transction..",
                            se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return returnValue;

    }

}