/*
 * Created on 10-dic-2004
 * 
 *  
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.model.protocollo.ProtocolloUscita;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloUscitaForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.presentation.helper.DestinatarioView;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ConfigurazioneUtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;
import it.finsiel.siged.util.StringUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProtocolloBO {

    private static Map statiProtocollo;
    static {
        statiProtocollo = new HashMap(10);
        statiProtocollo.put("IA", "Atti");
        statiProtocollo.put("IC", "Annullato");
        statiProtocollo.put("IN", "Lavorazione");
        statiProtocollo.put("IR", "Risposta");
        statiProtocollo.put("IS", "Sospeso");
        statiProtocollo.put("IF", "Rifiutato");
        statiProtocollo.put("IP", "Associato a Procedimento");
        statiProtocollo.put("UA", "Spedito");
        statiProtocollo.put("UR", "Spedito");
        statiProtocollo.put("UN", "non Spedito");
        statiProtocollo.put("UC", "Annullato");
        statiProtocollo.put("UP", "Associato a Procedimento");
    }

    /**
     * @return Returns the statoProtocollo.
     */
    public static String getStatoProtocollo(String tipo, String stato) {
        return (String) statiProtocollo.get(tipo + stato);
    }

    public static void putAllegato(DocumentoVO doc, Map documenti) {
        int idx = doc.getIdx();
        if (idx == 0) {
            idx = getNextDocIdx(documenti);
        }
        doc.setIdx(idx);
        documenti.put(String.valueOf(idx), doc);
    }

    private static int getNextDocIdx(Map allegati) {
        int max = 0;
        Iterator it = allegati.keySet().iterator();
        while (it.hasNext()) {
            String id = (String) it.next();
            int cur = NumberUtil.getInt(id);
            if (cur > max)
                max = cur;
        }
        return max + 1;
    }

    private static String getSignature(ProtocolloVO protocollo) {
        Organizzazione org = Organizzazione.getInstance();
        AreaOrganizzativaVO aoo = org.getAreaOrganizzativa(
                protocollo.getAooId()).getValueObject();

        StringBuffer str = new StringBuffer();
        str.append("<Identificatore>");
        str.append("<CodiceAmministrazione>");
        str.append(org.getValueObject().getCodice());
        str.append("</CodiceAmministrazione>");
        str.append("<CodiceAOO>");
        str.append(aoo.getCodice());
        str.append("</CodiceAOO>");
        str.append("<NumeroRegistrazione>");
        str.append(StringUtil.formattaNumeroProtocollo(""
                + protocollo.getNumProtocollo(), 7));
        str.append("</NumeroRegistrazione>");
        str.append("<DataRegistrazione>");
        str.append(DateUtil.formattaData(protocollo.getDataRegistrazione()
                .getTime()));
        str.append("</DataRegistrazione>");
        str.append("</Identificatore>");
        str.append("<Origine>");
        str.append("<IndirizzoTelematico tipo=\"smtp\">");
        str.append(aoo.getPec_smtp());
        str.append("</IndirizzoTelematico>");
        str.append("<Mittente>");
        str.append("<Amministrazione>");
        str.append("<Denominazione>");
        str.append(org.getValueObject().getDescription());
        str.append("</Denominazione>");
        str.append("<CodiceAmministrazione>");
        str.append(org.getValueObject().getCodice());
        str.append("</CodiceAmministrazione>");
        str.append("<AOO>");
        str.append("<CodiceAOO>");
        str.append(aoo.getCodice());
        str.append("</CodiceAOO>");
        str.append("<Denominazione>");
        str.append(aoo.getDescription());
        str.append("</Denominazione>");
        str.append("</AOO>");
        str.append("</Amministrazione>");
        str.append("</Mittente>");
        str.append("</Origine>");
        return str.toString();
    }

    public static String getSignature(ProtocolloIngresso protocollo) {
        StringBuffer str = new StringBuffer();

        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        str
                .append("<Segnatura xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        str.append(" xsi:noNamespaceSchemaLocation=\"Segnatura.dtd\"");
        str.append(" versione=\"2000-10-18\" lang=\"it\">");
        str.append("<Intestazione>");
        str.append(getSignature(protocollo.getProtocollo()));
        str.append("<Oggetto>");
        str.append(protocollo.getProtocollo().getOggetto());
        str.append("</Oggetto>");
        str.append("</Intestazione>");
        str.append(getSignatureDocumentiProtocollo(protocollo
                .getDocumentoPrincipale(), protocollo.getAllegatiCollection()));
        str.append("</Segnatura>");
        return str.toString();
    }

    public static String getSignature(ProtocolloUscita protocollo) {
        StringBuffer str = new StringBuffer();

        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        str
                .append("<Segnatura xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        str.append(" xsi:noNamespaceSchemaLocation=\"Segnatura.dtd\"");
        str.append(" versione=\"2000-10-18\" lang=\"it\">");
        str.append("<Intestazione>");
        str.append(getSignature(protocollo.getProtocollo()));
        // fine tag <origine>
        // TODO destinatari
        Collection destinatari = protocollo.getDestinatari();
        if (destinatari != null) {
            Iterator it = destinatari.iterator();
            while (it.hasNext()) {
                str.append("<Destinazione>");
                DestinatarioVO destinatario = (DestinatarioVO) it.next();
                str.append("<IndirizzoTelematico tipo=\"smtp\">");
                str.append(destinatario.getEmail());
                str.append("</IndirizzoTelematico>");
                str.append("<Destinatario>");
                str.append("<Amministrazione>");
                str.append("<Denominazione>");
                str.append(destinatario.getDestinatario());
                str.append("</Denominazione>");
                str.append("<CodiceAmministrazione />");
                str.append("<UnitaOrganizzativa>");
                str.append("<Denominazione />");
                str.append("<Persona />");
                if (destinatario.getIndirizzo() != null) {
                    str.append("<IndirizzoPostale>");
                    str.append("<Toponimo dug=\"Via\">");
                    str.append(destinatario.getIndirizzo());
                    str.append("</Toponimo>");
                    // str.append("<Civico>");
                    str.append("<Civico />");
                    // str.append("<CAP>");
                    str.append("<CAP />");
                    str.append("<Comune>");
                    str.append(destinatario.getCitta());
                    str.append("</Comune>");
                    // str.append("<Provincia>");
                    str.append("<Provincia />");
                    str.append("</IndirizzoPostale>");
                } else {
                    str.append("<IndirizzoPostale />");
                }

                str.append("<IndirizzoTelematico />");
                str.append("<Telefono />");
                str.append("<Fax />");
                str.append("</UnitaOrganizzativa>");
                str.append("</Amministrazione>");
                str.append("</Destinatario>");
                str.append("</Destinazione>");
            }

        } else {
            str.append("<Destinazione />");
        }

        str.append("<Oggetto>");
        str.append(protocollo.getProtocollo().getOggetto());
        str.append("</Oggetto>");
        str.append("</Intestazione>");
        str.append(getSignatureDocumentiProtocollo(protocollo
                .getDocumentoPrincipale(), protocollo.getAllegatiCollection()));
        str.append("</Segnatura>");
        return str.toString();
    }

    private static String getSignatureDocumentiProtocollo(
            DocumentoVO documentoPrincipale, Collection allegati) {
        StringBuffer str = new StringBuffer();
        str.append("<Descrizione>");
        str.append("<TestoDelMessaggio />");

        if ((documentoPrincipale != null && documentoPrincipale.getFileName() != null)
                || (allegati != null && allegati.size() > 0)) {

            if (documentoPrincipale != null) {
                str.append("<Documento id=\"");
                str.append(documentoPrincipale.getId());
                str.append("\"");
                str.append(" nome=\"");
                str.append(documentoPrincipale.getFileName());
                str.append("\"");
                str.append(" tipoMIME=\"");
                str.append(documentoPrincipale.getContentType());
                str.append("\" ");
                str.append(" tipoRiferimento=\"MIME\">");
                str.append("<Impronta algoritmo=\"SHA-1\">");
                str.append(documentoPrincipale.getImpronta());
                str.append("</Impronta>");
                str.append("</Documento>");
            }
            if (allegati != null) {
                Iterator it = allegati.iterator();
                while (it.hasNext()) {
                    DocumentoVO allegato = (DocumentoVO) it.next();
                    str.append("<Allegati>");
                    str.append("<Documento id=\"");
                    str.append(allegato.getId());
                    str.append("\"");
                    str.append(" nome=\"");
                    str.append(allegato.getFileName());
                    str.append("\"");
                    str.append(" tipoMIME=\"");
                    str.append(allegato.getContentType());
                    str.append("\" ");
                    str.append(" tipoRiferimento=\"MIME\" />");
                    str.append("</Allegati>");
                }
            }

        }
        str.append("</Descrizione>");
        return str.toString();
    }

    /*
     * Pre-carica i dati nell'oggetto ProtocolloIngresso secondo le regole
     * prestabilite: valori di default, valori basati sul profilo utente, valori
     * basati sulla sessione di lavoro dell'utente, etc.
     */
    private static ProtocolloVO getDefaultProtocollo(Utente utente) {
        ProtocolloVO protocollo = new ProtocolloVO();
        RegistroVO reg = utente.getRegistroVOInUso();
        protocollo.setRegistroId(reg.getId().intValue());
        Timestamp dataReg = new Timestamp(System.currentTimeMillis());
        if (reg.getDataBloccata())
            dataReg = new Timestamp(RegistroBO.getDataAperturaRegistro(reg)
                    .getTime());
        protocollo.setDataRegistrazione(dataReg);
        protocollo.setAnnoRegistrazione(reg.getAnnoCorrente());
        protocollo.setDataEffettivaRegistrazione(new Date());
        protocollo.setUfficioProtocollatoreId(utente.getUfficioInUso());
        protocollo.setUtenteProtocollatoreId(utente.getValueObject().getId()
                .intValue());
        protocollo.setRowCreatedUser(utente.getValueObject().getUsername());
        protocollo.setRowUpdatedUser(utente.getValueObject().getUsername());
        protocollo.setRowCreatedTime(new Date(System.currentTimeMillis()));
        protocollo.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        protocollo.setAooId(utente.getValueObject().getAooId());
        protocollo.setStatoProtocollo("S");
        return protocollo;
    }

    public static ProtocolloIngresso getDefaultProtocolloIngresso(Utente utente) {
        ProtocolloVO protocollo = getDefaultProtocollo(utente);
        protocollo.setFlagTipo(ProtocolloVO.FLAG_TIPO_PROTOCOLLO_INGRESSO);
        protocollo.setFlagTipoMittente(LookupDelegate.tipiPersona[0].getTipo());

        ProtocolloIngresso pi = new ProtocolloIngresso();
        pi.setProtocollo(protocollo);
        return pi;
    }

    public static ProtocolloUscita getDefaultProtocolloUscita(Utente utente) {
        ProtocolloVO protocollo = getDefaultProtocollo(utente);
        protocollo.setFlagTipo(ProtocolloVO.FLAG_TIPO_PROTOCOLLO_USCITA);

        ProtocolloUscita pu = new ProtocolloUscita();
        pu.setProtocollo(protocollo);
        return pu;
    }

    /*
     * Ritorna una collection, vuota o con i DestinatariVO che necessitano invio
     * tramite email.
     */
    public static Collection getDestinatariViaEmail(Collection destinatari) {
        ArrayList retD = new ArrayList();
        if (destinatari == null)
            return retD;
        Iterator destinatariIt = destinatari.iterator();
        while (destinatariIt.hasNext()) {
            DestinatarioVO dest = (DestinatarioVO) destinatariIt.next();
            if("Email".equals(dest.getMezzoDesc())){
            	retD.add(dest);
            }
            // TODO: fix this
            // Iterator m = dest.getMezziSpedizione().values().iterator();
            // while(m.hasNext()){
            // SpedizioneDestinatarioVO vo = (SpedizioneDestinatarioVO)m.next();
            // //TODO: aggiungere tipoemail nella tabella spedizioni di tipo int
            // 0-1, aggiornare
            // // spedizioneDestinatariovo, e messiSpedizione in lookup
            // delegate/jdbc
            // // if(vo.getTipoEmail()==1)
            // if
            // (EmailConstants.DESTINATARIO_TIPO_EMAIL.indexOf(vo.getSpedizioneDesc())>=0)
            // {
            // retD.add(dest);
            // }
            // }
        }

        return retD;
    }

    /*
     * Restituisce una stringa formattata contenente il "timbro" da sovrapporre
     * al Pdf del Protocollo da registrare.
     */
    public static String getTimbro(Organizzazione org, ProtocolloVO protocollo) {
        String aooDesc = org.getAreaOrganizzativa(protocollo.getAooId())
                .getValueObject().getDescription();
        String ufficioDesc = org.getUfficio(
                protocollo.getUfficioProtocollatoreId()).getValueObject()
                .getDescription();
        return aooDesc
                + " - "
                + ufficioDesc
                + " - "
                + DateUtil.formattaData(protocollo.getDataRegistrazione()
                        .getTime())
                + " - "
                + StringUtil.formattaNumeroProtocollo(String.valueOf(protocollo
                        .getNumProtocollo()), 7);
    }

    /*
     * Metodo che esamina la Collection "assegnatari" in input in cerca
     * dell'assegnatario per competenza di tipo Utente. si assume che l'id
     * utente sia maggiore di Zero nel caso in cui sia stato selezionato. Questo
     * metodo � stato creato per capire se notificare o meno l'assegnatario di
     * competenza, verr� notificato nel caso in cui � un utente.
     */
    public static  List<Integer> notificaAssegnatarioCompetenza(Collection assegnatari) {
    	List<Integer> res = new ArrayList<Integer>();
        if (assegnatari == null)
            return res;
        for (Iterator i = assegnatari.iterator(); i.hasNext();) {
            AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
            if (assegnatario.isCompetente()
                    && assegnatario.getUtenteAssegnatarioId() > 0) {
                res.add(assegnatario.getUtenteAssegnatarioId());
            }
        }
        return res;
    }

    public static ProtocolloIngressoForm getProtocolloIngressoConfigurazioneUtente(
            ProtocolloIngressoForm pForm,
            ConfigurazioneUtenteVO configurazioneVO, int utenteId) {
        pForm.inizializzaFormToCopyProtocollo();
        Organizzazione organizzazione = Organizzazione.getInstance();

        long dataCorrente = System.currentTimeMillis();
        if (configurazioneVO.getReturnValue() == ReturnValues.FOUND) {
            if (configurazioneVO.getCheckAssegnatari().equals("1")) {
                if (configurazioneVO.getAssegnatarioUfficioId() > 0) {
                    pForm.removeAssegnatari();
                    UfficioVO ufficioVO = (organizzazione
                            .getUfficio(configurazioneVO
                                    .getAssegnatarioUfficioId()))
                            .getValueObject();

                    AssegnatarioView ass = new AssegnatarioView();

                    ass.setUfficioId(ufficioVO.getId().intValue());
                    ass.setNomeUfficio(ufficioVO.getDescription());
                    ass.setDescrizioneUfficio(ufficioVO.getName());
                    if (configurazioneVO.getAssegnatarioUtenteId() > 0) {
                        ass.setUtenteId(configurazioneVO
                                .getAssegnatarioUtenteId());
                        UtenteVO ute = organizzazione.getUtente(
                                configurazioneVO.getAssegnatarioUtenteId())
                                .getValueObject();
                        ass.setNomeUtente(ute.getFullName());

                    }
                    pForm.aggiungiAssegnatario(ass);
                    pForm.setAssegnatarioCompetente(ass.getKey());
                    if (pForm.isDipTitolarioUfficio()) {
                        pForm.setTitolario(null);
                    }

                }

            }
            if (configurazioneVO.getCheckDataDocumento().equals("1")) {
                if ("0".equals(configurazioneVO.getDataDocumento())) {
                    pForm.setDataDocumento(null);
                } else if ("1".equals(configurazioneVO.getDataDocumento())) {
                    pForm.setDataDocumento(DateUtil.formattaData(dataCorrente));
                }

            }
            if (configurazioneVO.getCheckRicevutoIl().equals("1")) {
                if ("0".equals(configurazioneVO.getDataRicezione())) {
                    pForm.setDataRicezione(null);
                } else if ("1".equals(configurazioneVO.getDataRicezione())) {
                    pForm.setDataRicezione(DateUtil.formattaData(dataCorrente));
                }

            }
            if (configurazioneVO.getCheckOggetto().equals("1")) {
                if (configurazioneVO.getOggetto() != null
                        && !"".equals(configurazioneVO.getOggetto())) {
                    pForm.setOggetto(configurazioneVO.getOggetto());
                } else {
                    pForm.setOggetto(null);
                }

            }
            SoggettoVO soggettoVO;
            if (configurazioneVO.getCheckTipoMittente().equals("1")) {
                soggettoVO = new SoggettoVO(configurazioneVO.getTipoMittente());
            } else if (pForm.getMittente() != null) {
                soggettoVO = new SoggettoVO(pForm.getMittente().getTipo());
            } else {
                soggettoVO = new SoggettoVO("F");
            }

            if (configurazioneVO.getCheckMittente().equals("1")) {
                if ("F".equals(configurazioneVO.getTipoMittente()))
                    soggettoVO.setCognome(configurazioneVO.getMittente());
                else if ("G".equals(configurazioneVO.getTipoMittente()))
                    soggettoVO.setDescrizioneDitta(configurazioneVO
                            .getMittente());

            }
            pForm.setMittente(soggettoVO);
            if (configurazioneVO.getCheckTipoDocumento().equals("1")) {
                pForm.setTipoDocumentoId(configurazioneVO.getTipoDocumentoId());
            }

            if (configurazioneVO.getCheckTitolario().equals("1")) {
                pForm.setTitolarioSelezionatoId(configurazioneVO
                        .getTitolarioId());
                Utente utente = organizzazione.getUtente(utenteId);
                TitolarioBO.impostaTitolario(pForm, utente.getUfficioInUso(),
                        configurazioneVO.getTitolario());
            } else {
                pForm.setTitolario(null);
            }

        }
        return pForm;
    }

    public static ProtocolloUscitaForm getProtocolloUscitaConfigurazioneUtente(
            ProtocolloUscitaForm pForm,
            ConfigurazioneUtenteVO configurazioneVO, int utenteId) {
        pForm.inizializzaFormToCopyProtocollo();
        Organizzazione organizzazione = Organizzazione.getInstance();
        long dataCorrente = System.currentTimeMillis();
        if (configurazioneVO.getReturnValue() == ReturnValues.FOUND) {
            if (configurazioneVO.getCheckDestinatari().equals("1")) {
                if (configurazioneVO.getAssegnatarioUfficioId() > 0) {
                    pForm.rimuoviDestinatari();
                    DestinatarioView destinatarioView = new DestinatarioView();
                    destinatarioView.setDestinatario(configurazioneVO
                            .getDestinatario());
                    destinatarioView.setFlagTipoDestinatario("F");
                    pForm.aggiungiDestinatario(destinatarioView);
                }
            }
            if (configurazioneVO.getCheckDataDocumento().equals("1")) {
                if ("0".equals(configurazioneVO.getDataDocumento())) {
                    pForm.setDataDocumento(null);
                } else if ("1".equals(configurazioneVO.getDataDocumento())) {
                    pForm.setDataDocumento(DateUtil.formattaData(dataCorrente));
                }

            }
            if (configurazioneVO.getCheckRicevutoIl().equals("1")) {
                if ("0".equals(configurazioneVO.getDataRicezione())) {
                    pForm.setDataRicezione(null);
                } else if ("1".equals(configurazioneVO.getDataRicezione())) {
                    pForm.setDataRicezione(DateUtil.formattaData(dataCorrente));
                }

            }
            if (configurazioneVO.getCheckOggetto().equals("1")) {
                if (configurazioneVO.getOggetto() != null
                        && !"".equals(configurazioneVO.getOggetto())) {
                    pForm.setOggetto(configurazioneVO.getOggetto());
                } else {
                    pForm.setOggetto(null);
                }

            }

            if (configurazioneVO.getCheckTipoDocumento().equals("1")) {
                pForm.setTipoDocumentoId(configurazioneVO.getTipoDocumentoId());
            }

            if (configurazioneVO.getCheckTitolario().equals("1")) {
                pForm.setTitolarioSelezionatoId(configurazioneVO
                        .getTitolarioId());
                Utente utente = organizzazione.getUtente(utenteId);
                TitolarioBO.impostaTitolario(pForm, utente.getUfficioInUso(),
                        configurazioneVO.getTitolario());
            } else {
                pForm.setTitolario(null);
            }

        }
        return pForm;
    }

    public static boolean isModificable(ProtocolloIngresso protocollo,
            Utente utente) {
        boolean modificabile = false;
        String statoProtocollo = protocollo.getProtocollo()
                .getStatoProtocollo();
        if (!"C".equals(statoProtocollo) && !"F".equals(statoProtocollo)) {
            int utenteId = utente.getValueObject().getId().intValue();
            AssegnatarioVO assegnatario = null;
            for (Iterator i = protocollo.getAssegnatari().iterator(); i
                    .hasNext();) {
                assegnatario = (AssegnatarioVO) i.next();
                if (assegnatario.isCompetente()) {
                    if ((("P".equals(statoProtocollo) || "S"
                            .equals(statoProtocollo)) && utente
                            .getValueObject().getId().intValue() == assegnatario
                            .getUtenteAssegnanteId())
                            || ("N".equals(statoProtocollo)
                                    && 'A' == assegnatario
                                            .getStatoAssegnazione() && (assegnatario
                                    .getUtenteAssegnatarioId() == utenteId))) {
                        modificabile = true;
                    }
                    break;
                }
            }
        }
        return modificabile;
    }

    public static boolean isModificable(ProtocolloUscita protocollo,
            Utente utente) {

        boolean modificabile = false;
        if (!"C".equals(protocollo.getProtocollo().getStatoProtocollo())) {
            if (utente.isUtenteAbilitatoSuUfficio(protocollo.getProtocollo()
                    .getUfficioProtocollatoreId())
                    || utente.isUtenteAbilitatoSuUfficio(protocollo
                            .getProtocollo().getUfficioMittenteId())) {
                modificabile = true;
            }

        }
        return modificabile;
    }

}