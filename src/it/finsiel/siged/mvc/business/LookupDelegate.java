package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.PermessiConst;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.integration.LookupDAO;
import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.lookup.ParametriLdapVO;
import it.finsiel.siged.mvc.vo.lookup.StatoAssegnazioneProtocolloVO;
import it.finsiel.siged.mvc.vo.lookup.TipoPersonaVO;
import it.finsiel.siged.mvc.vo.lookup.TipoProtocolloVO;
import it.finsiel.siged.mvc.vo.lookup.TipoUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.protocollo.StatoFascicoloVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

public class LookupDelegate implements ComponentStatus {

    private static Logger logger = Logger.getLogger(LookupDelegate.class
            .getName());

    private int status;

    private LookupDAO lookupDAO = null;

    private ServletConfig config = null;

    private static LookupDelegate delegate = null;

    private static Map tipiDocumento = new HashMap(8);

    private static Map statiDocumento = new HashMap(8);

    private static Map mezziSpedizione = new HashMap(8);

    private static Map tipiPermessiView = new HashMap(4);

    private static Map tipiPermessiBusiness = new HashMap(4);

    private static Map tipiProcedimento = new HashMap(8);

    private static Map statiProcedimento = null;

    private static Map statiFascicolo = null;

    private static Map condizioniFascicolo = null;

    private static Map tipiFinalitaProcedimento = null;

    private static Collection posizioneProcedimento = null;

    private static IdentityVO tipoOwner = new IdentityVO(String
            .valueOf(PermessiConst.OWNER), "Owner");

    private static Map tipiFascicolo = new HashMap(2);

    private static Map posizioniProcedimento = null;

    private static Collection destinazioniScarto = null;

    /*
     * PersonaFisica alla posizione 0 e Giuridica alla posizione 1 dell'array
     */
    public static TipoPersonaVO[] tipiPersona = new TipoPersonaVO[] {
            new TipoPersonaVO("F", "Persona Fisica"),
            new TipoPersonaVO("G", "Persona Giuridica"),
            new TipoPersonaVO("M", "Multimittente")};

    static {
        tipiPermessiView.put(String.valueOf(PermessiConst.SOLA_LETTURA),
                new IdentityVO(String.valueOf(PermessiConst.SOLA_LETTURA),
                        "Lettura"));
        tipiPermessiView.put(String.valueOf(PermessiConst.LETTURA_STORIA),
                new IdentityVO(String.valueOf(PermessiConst.LETTURA_STORIA),
                        "Lettura e Storia"));

        tipiPermessiView.put(String.valueOf(PermessiConst.MODIFICA),
                new IdentityVO(String.valueOf(PermessiConst.MODIFICA),
                        "Modifica"));
        tipiPermessiView.put(String.valueOf(PermessiConst.MODIFICA_CANCELLA),
                new IdentityVO(String.valueOf(PermessiConst.MODIFICA_CANCELLA),
                        "Modifica e cancella"));
        tipiPermessiBusiness.putAll(tipiPermessiView);
        tipiPermessiBusiness.put(tipoOwner.getCodice(), tipoOwner);
    }

    /**
     * Constructor for LookupDelegate. Here we decide which persistence type we
     * will use and contact an appropriate DAO of the integration tier. The
     * ServletConfig is send as parameter. It contains our application specific
     * parameters which are needed in the backend, too, i.e. the ServletContext.
     * </ol>
     * 
     * @param servlet
     */
    private LookupDelegate() {
        // Connect to DAO
        try {
            if (lookupDAO == null) {
                lookupDAO = (LookupDAO) DAOFactory
                        .getDAO(Constants.LOOKUP_DAO_CLASS);
                logger.debug("UserDAO instantiated:"
                        + Constants.LOOKUP_DAO_CLASS);
                status = STATUS_OK;
            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
            status = STATUS_ERROR;
        }

    }

    public static LookupDelegate getInstance() {
        if (delegate == null)
            delegate = new LookupDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.LOOKUP_DELEGATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.finsiel.siged.mvc.business.ComponentStatus#getStatus()
     */
    public int getStatus() {
        return status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.finsiel.siged.mvc.business.ComponentStatus#setStatus(int)
     */
    public void setStatus(int s) {
        this.status = s;
    }

    public void caricaTabelle(ServletContext context) {
        try {

            caricaTipiDocumento();

            logger.info("LookupDelegate: getting tipiDoc");

            context.setAttribute("tipiPersona", getTipiPersona());
            logger.info("LookupDelegate: getting tipiPersona");

            context.setAttribute("statiAssegnazioneProtocollo",
                    getStatoAssegnazioneProtocollo());
            logger
                    .info("LookupDelegate: getting getStatoAssegnazioneProtocollo");

            context.setAttribute("tipiProtocollo", getTipiProtocollo());
            logger.info("LookupDelegate: getting getTipiProtocollo");

            caricaMezziSpedizione();
            logger.info("LookupDelegate: getting mezziSpedizione");

            context.setAttribute("tipiUfficio", getTipiUfficio());
            logger.info("LookupDelegate: getting getTipiUfficio");

            context.setAttribute("tipiPermessiDocumenti",
                    getTipiPermessiDocumentiView());
            logger.info("LookupDelegate: getting getTipiPermessiDocumenti");

        } catch (Exception de) {
            logger.error("LookupDelegate: failed getting caricaTabelle: ");
        }
    }

    public void caricaTipiDocumento() {
        Organizzazione org = Organizzazione.getInstance();
        Collection aoo = org.getAreeOrganizzative();
        try {
            tipiDocumento = lookupDAO.getTipiDocumento(aoo);
        } catch (DataException de) {
            logger.error("LookupDelegate: failed getting getTipiDocumento: ");
        }
    }

    public Collection getTipiDocumento(int aooId) {
        Collection tipi = new ArrayList();
        if (tipiDocumento.get(new Integer(aooId)) != null)
            tipi = (Collection) tipiDocumento.get(new Integer(aooId));
        return tipi;
    }

    public void caricaTipiProcedimento() {
        Organizzazione org = Organizzazione.getInstance();
        Collection aoo = org.getAreeOrganizzative();
        try {
            tipiProcedimento = lookupDAO.getTipiProcedimento(aoo);
        } catch (DataException de) {
            logger
                    .error("LookupDelegate: failed getting getTipiProcedimento: ");
        }
    }

    public Collection getTipiProcedimento(int aooId) {
        Collection tipi = new ArrayList();
        if (tipiProcedimento.get(new Integer(aooId)) != null)
            tipi = (Collection) tipiProcedimento.get(new Integer(aooId));
        return tipi;
    }

    public void caricaMezziSpedizione() {
        Organizzazione org = Organizzazione.getInstance();
        Collection aoo = org.getAreeOrganizzative();
        try {
            mezziSpedizione = lookupDAO.getMezziSpedizione(aoo);
        } catch (DataException de) {
            logger
                    .error("LookupDelegate: failed getting caricaMezziSpedizione: ");
        }
    }

    /**
     * 
     * @param aooId
     * @return IdentityVO
     */
    public Collection getMezziSpedizione(int aooId) {
        Collection mezzi = new ArrayList();
        if (mezziSpedizione.get(new Integer(aooId)) != null)
            mezzi = (Collection) mezziSpedizione.get(new Integer(aooId));
        return mezzi;
    }

    public IdentityVO getDescMezzoSpedizione(int mezzoSpedizioneId) {
        IdentityVO mezzo = new IdentityVO();
        try {
            mezzo = lookupDAO.getMezzoSpedizione(mezzoSpedizioneId);
        } catch (Exception e) {

        }

        return mezzo;
    }

    // public Collection getMezziSpedizione() {
    //
    // try {
    // return lookupDAO.getMezziSpedizione();
    // } catch (DataException de) {
    // logger.error("LookupDelegate: failed getting getMezziSpedizione: ");
    // return null;
    // }
    // }

    public Collection getProvince() {

        try {
            return lookupDAO.getProvince();
        } catch (DataException de) {
            logger.error("LookupDelegate: failed getting getProvince: ");
            return null;
        }
    }

    public Collection getTitoliDestinatario() {

        try {
            return lookupDAO.getTitoliDestinatario();
        } catch (DataException de) {
            logger
                    .error("LookupDelegate: failed getting getTitoliDestinatario: ");
            return null;
        }
    }

    public Collection getTipiPersona() {
        ArrayList list = new ArrayList(2);
        list.add(tipiPersona[0]);
        list.add(tipiPersona[1]);
        return list;
    }

    private Collection getStatoAssegnazioneProtocollo() {
        ArrayList list = new ArrayList(3);
        list.add(new StatoAssegnazioneProtocolloVO("N", "in Lavorazione"));
        list.add(new StatoAssegnazioneProtocolloVO("A", "agli Atti"));
        list.add(new StatoAssegnazioneProtocolloVO("R", "in Risposta"));
        return list;
    }

    private Collection getTipiUfficio() {
        ArrayList list = new ArrayList(3);
        list.add(new TipoUfficioVO(UfficioVO.UFFICIO_NORMALE,
                UfficioVO.LABEL_UFFICIO_NORMALE));
        list.add(new TipoUfficioVO(UfficioVO.UFFICIO_CENTRALE,
                UfficioVO.LABEL_UFFICIO_CENTRALE));
        list.add(new TipoUfficioVO(UfficioVO.UFFICIO_SEMICENTRALE,
                UfficioVO.LABEL_UFFICIO_SEMICENTRALE));
        return list;
    }

    private Collection getTipiProtocollo() {
        ArrayList list = new ArrayList(3);
        list.add(new TipoProtocolloVO("I", "Ingresso"));
        list.add(new TipoProtocolloVO("U", "Uscita"));
        list.add(new TipoProtocolloVO("M", "Mozione"));
        return list;
    }

    public Collection getStatiProtocollo(String tipoProtocollo) {
        ArrayList list = new ArrayList(4);
        list.add(new TipoProtocolloVO("C", "Annullato"));
        if (tipoProtocollo != null && tipoProtocollo.length() == 1) {
            switch (tipoProtocollo.charAt(0)) {
            case 'I':
                list.add(new TipoProtocolloVO("A", "agli Atti"));
                list.add(new TipoProtocolloVO("N", "in Lavorazione"));
                list.add(new TipoProtocolloVO("R", "in Risposta"));
                list.add(new TipoProtocolloVO("S", "Sospeso"));
                list.add(new TipoProtocolloVO("F", "Rifiutato"));
                list.add(new TipoProtocolloVO(
                        Parametri.STATO_ASSOCIATO_A_PROCEDIMENTO,
                        "Associato a Procedimento"));
                break;

            case 'U':
                list.add(new TipoProtocolloVO("R", "Spedito"));
                list.add(new TipoProtocolloVO("N", "non Spedito"));
                list.add(new TipoProtocolloVO(
                        Parametri.STATO_ASSOCIATO_A_PROCEDIMENTO,
                        "Associato a Procedimento"));
                break;

            case 'M':
                list.add(new TipoProtocolloVO("A", "Spedito"));
                list.add(new TipoProtocolloVO("N", "non Spedito"));
                list.add(new TipoProtocolloVO(
                        Parametri.STATO_ASSOCIATO_A_PROCEDIMENTO,
                        "Associato a Procedimento"));
                break;

            }
        } else if (tipoProtocollo != null
                && tipoProtocollo.equals(Parametri.LABEL_MOZIONE_USCITA)) {
            list.add(new TipoProtocolloVO("A", "Spedito"));
            list.add(new TipoProtocolloVO("N", "non Spedito"));
        }
        return list;
    }

    // TODO: load from database
    public Collection getCategoriePA() {
        ArrayList cat = new ArrayList();
        cat.add(new IdentityVO("*", "scelta categoria"));
        cat.add(new IdentityVO("P", "ASL"));
        cat.add(new IdentityVO("E", "Autorita' Amministrative Indipendenti"));
        cat.add(new IdentityVO("R", "Camere di Commercio"));
        cat.add(new IdentityVO("O", "Comuni"));
        cat.add(new IdentityVO("U", "Comunit� montane"));
        cat.add(new IdentityVO("F", "Enti a Struttura Associativa"));
        cat
                .add(new IdentityVO("Y",
                        "Enti Autonomi Lirici ed Istituzioni Concertistiche Assimilate"));
        cat.add(new IdentityVO("C",
                "Enti di Regolazione dell'Attivit&agrave; Economica"));
        cat.add(new IdentityVO("Q", "Enti e Aziende Ospedaliere"));
        cat.add(new IdentityVO("I",
                "Enti ed Istituzioni di Ricerca non Strumentale"));
        cat.add(new IdentityVO("K",
                "Enti Nazionali di Previdenza e Assistenza Sociale"));
        cat.add(new IdentityVO("W", "Enti Parco"));
        cat.add(new IdentityVO("X", "Enti per il Diritto allo Studio"));
        cat.add(new IdentityVO("S", "Enti per il Turismo"));
        cat.add(new IdentityVO("T", "Enti Portuali"));
        cat.add(new IdentityVO("G", "Enti Produttori di Servizi Culturali"));
        cat.add(new IdentityVO("D", "Enti Produttori di Servizi Economici"));
        cat.add(new IdentityVO("V", "Enti Regionali di Sviluppo"));
        cat.add(new IdentityVO("J",
                "Enti Regionali per la Ricerca e per l'Ambiente"));
        cat.add(new IdentityVO("L",
                "Istituti e Stazioni Sperimentali per la Ricerca"));
        cat.add(new IdentityVO("A", "Ministeri e Presidenza del Consiglio"));
        cat.add(new IdentityVO("B",
                "Organi Costituzionali e di Rilievo Costituzionale"));
        cat.add(new IdentityVO("N", "Province"));
        cat.add(new IdentityVO("M", "Regioni"));
        cat.add(new IdentityVO("Z", "Universit� ed Istituti di Istruzione"));
        cat.add(new IdentityVO("H", "Altri Enti"));
        return cat;

    }

    // TODO: load from DB or JConfig?
    public ParametriLdapVO getIndicePAParams() {
        ParametriLdapVO pa = new ParametriLdapVO();
        pa.setHost("indicepa.gov.it");
        pa.setPorta(389);
        pa.setDn("c=it");
        return pa;
    }

    public Collection getTipiPermessiDocumentiView() {
        return tipiPermessiView.values();
    }

    public Map getTipiPermessiDocumentiBusiness() {
        return tipiPermessiBusiness;
    }

    public IdentityVO getTipoPermessoOwner() {
        return tipoOwner;
    }

    public static Map getStatiDocumento() {
        statiDocumento.put(Parametri.STATO_CLASSIFICATO, new IdentityVO(
                Parametri.STATO_CLASSIFICATO, "Classificato"));
        statiDocumento.put(Parametri.STATO_INVIATO_PROTOCOLLO, new IdentityVO(
                Parametri.STATO_INVIATO_PROTOCOLLO, "Inviato a Protocollo"));
        statiDocumento.put(Parametri.STATO_LAVORAZIONE, new IdentityVO(
                Parametri.STATO_LAVORAZIONE, "In Lavorazione"));
        statiDocumento.put(Parametri.STATO_PROTOCOLLATO, new IdentityVO(
                Parametri.STATO_PROTOCOLLATO, "Protocollato"));
        return statiDocumento;
    }

    /**
     * 
     * @return map of IdentityVO
     */
    public static Map getPosizioniProcedimento() {
        if (posizioniProcedimento == null) {
            posizioniProcedimento = new HashMap(3);
            posizioniProcedimento.put("A", new IdentityVO("A", "Agli Atti"));
            posizioniProcedimento.put("E", new IdentityVO("E", "In Evidenza"));
            posizioniProcedimento.put("T",
                    new IdentityVO("T", "In Trattazione"));

        }
        return posizioniProcedimento;
    }

    public static Map getTipiFascicolo() {
        tipiFascicolo.put(new Integer(Parametri.TIPO_FASCICOLO_ORDINARIO),
                new IdentityVO(String
                        .valueOf(Parametri.TIPO_FASCICOLO_ORDINARIO),
                        Parametri.LABEL_TIPO_FASCICOLO_ORDINARIO));
        tipiFascicolo.put(new Integer(Parametri.TIPO_FASCICOLO_VIRTUALE),
                new IdentityVO(String
                        .valueOf(Parametri.TIPO_FASCICOLO_VIRTUALE),
                        Parametri.LABEL_TIPO_FASCICOLO_VIRTUALE));
        return tipiFascicolo;
    }

    /**
     * 
     * @return map of IdentityVO
     */
    public static Map getTipiProcedimento() {
        if (tipiProcedimento == null) {
            tipiProcedimento = new HashMap(2);
            tipiProcedimento.put("1", new IdentityVO(1, "Tipo Proc 1"));
            tipiProcedimento.put("2", new IdentityVO(2, "Tipo Proc 2"));
        }
        return tipiProcedimento;
    }

    /**
     * 
     * @return map of IdentityVO
     */
    public static Map getStatiProcedimento() {
        if (statiProcedimento == null) {
            statiProcedimento = new HashMap(2);
            statiProcedimento.put("1", new IdentityVO(1, "Fase di Iniziativa"));
            statiProcedimento.put("2", new IdentityVO(2, "Fase Istruttoria"));
            statiProcedimento.put("3", new IdentityVO(3, "Fase Decisoria"));
            statiProcedimento.put("4", new IdentityVO(4,
                    "Fase Integrativa dell'efficacia"));
            statiProcedimento.put("5", new IdentityVO(5, "Concluso"));
        }
        return statiProcedimento;
    }

    /**
     * 
     * @return map of IdentityVO
     */
    public static Map getTipiFinalitaProcedimento() {
        if (tipiFinalitaProcedimento == null) {
            tipiFinalitaProcedimento = new HashMap(2);
            tipiFinalitaProcedimento.put("1", new IdentityVO(1,
                    "Tipo Finalita 1"));
            tipiFinalitaProcedimento.put("2", new IdentityVO(2,
                    "Tipo Finalita 2"));
        }
        return tipiFinalitaProcedimento;
    }

    public static Map getStatiFascicolo() {

        if (statiFascicolo == null) {
            Collection stati = FascicoloDelegate.getInstance()
                    .getStatiFascicolo(null);
            if (stati.size() > 0) {
                statiFascicolo = new HashMap(stati.size());
                Iterator it = stati.iterator();
                while (it.hasNext()) {
                    StatoFascicoloVO s = (StatoFascicoloVO) it.next();
                    statiFascicolo.put(s.getId(), s.getDescrizione());

                }
            }
        }
        return statiFascicolo;
    }

    public static Map getCondizioniFascicolo() {
        if (condizioniFascicolo == null) {
            condizioniFascicolo = new HashMap(2);
            condizioniFascicolo.put(new Integer(
                    Parametri.CONDIZIONE_FASCICOLO_ASSOCIATO), new IdentityVO(
                    String.valueOf(Parametri.CONDIZIONE_FASCICOLO_ASSOCIATO),
                    Parametri.LABEL_CONDIZIONE_FASCICOLO_ASSOCIATO));
            condizioniFascicolo
                    .put(
                            new Integer(
                                    Parametri.CONDIZIONE_FASCICOLO_NON_ASSOCIATO),
                            new IdentityVO(
                                    String
                                            .valueOf(Parametri.CONDIZIONE_FASCICOLO_NON_ASSOCIATO),
                                    Parametri.LABEL_CONDIZIONE_FASCICOLO_NON_ASSOCIATO));
        }
        return condizioniFascicolo;
    }

    public static Collection getDestinazioniScarto() {
        if (destinazioniScarto == null) {
            destinazioniScarto = new ArrayList();
            IdentityVO iVO = new IdentityVO(0,
                    Parametri.DESTINAZIONE_SCARTO_ARCHIVIO_STATO);
            destinazioniScarto.add(iVO);

            iVO = new IdentityVO(1, Parametri.DESTINAZIONE_SCARTO_MACERO);
            destinazioniScarto.add(iVO);
        }
        return destinazioniScarto;
    }

}