package it.finsiel.siged.mvc.presentation.helper;

import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.util.StringUtil;
import it.flosslab.mvc.business.MittentiDelegate;

import java.util.ArrayList;
import java.util.List;

public class ReportProtocolloView {

    public ReportProtocolloView() {
    }

    private int protocolloId;

    private int numeroProtocollo;

    private int annoProtocollo;

    private String annoNumeroProtocollo;

    private String dataScarico;

    private String dataProtocollo;

    private String tipoProtocollo;

    private String mittente;
    
    // Modifica Daniele Sanna 15/09/2008
    private String tipoMittente;
    // Fine modifica
	private String oggetto;

    private String destinatario;

    private String ufficio;

    private String utenteAssegnante;

    private String tipoDocumento;

    private int documentoId; // Inserito il 15/02/06 per gestire il linkDoc

    private String filename; // Inserito il 16/02/06 per gestire il linkDoc

    private boolean pdf;

    private String statoProtocollo;

    private String ufficioAssegnatario;

    private String utenteAssegnatario;

    private String dataAnnullamento;

    private String provvedimentoAnnullamento;

    private String notaAnnullamento;

    private String dataSpedizione;

    private String mezzoSpedizione;

    private int mezzoSpedizioneId;

    private String allaccio;

    private int versione;

    private int massimario;

    private String messaggio;

    private boolean isModificabile;

    public final static String PDF_SI = "SI";

    private int titolarioId;
    
    
    // Modifica Daniele Sanna 15/09/2008
  
    /**
	 * @return the tipoMittente
	 */
	public String getTipoMittente() {
		return tipoMittente;
	}

	/**
	 * @param tipoMittente the tipoMittente to set
	 */
	public void setTipoMittente(String tipoMittente) {
		this.tipoMittente = tipoMittente;
	}
	
	public List<SoggettoVO> getMittenti() throws Exception{
		List<SoggettoVO> mittenti = new ArrayList<SoggettoVO>();
		MittentiDelegate delegate = MittentiDelegate.getInstance();
		mittenti = delegate.getMittenti(this.protocolloId);
		
		return mittenti;
	}
	// Fine modifica

    public int getMezzoSpedizioneId() {
        return mezzoSpedizioneId;
    }

    public void setMezzoSpedizioneId(int mezzoSpedizioneId) {
        this.mezzoSpedizioneId = mezzoSpedizioneId;
    }

    public int getMassimario() {
        return massimario;
    }

    public void setMassimario(int massimario) {
        this.massimario = massimario;
    }

    /**
     * @return Returns the pdf.
     */
    public String getPdf() {
        return this.pdf ? PDF_SI : null;
    }

    /**
     * @param pdf
     *            The pdf to set.
     */
    public void setPdf(boolean pdf) {
        this.pdf = pdf;
    }

    /**
     * @return Returns the statoProtocollo.
     */
    public String getStatoProtocollo() {
        return ProtocolloBO.getStatoProtocollo(this.tipoProtocollo,
                this.statoProtocollo);
    }

    public String getStato() {
        return this.statoProtocollo;
    }

    /**
     * @return Returns the protocolloId.
     */
    public int getProtocolloId() {
        return protocolloId;
    }

    /**
     * @param protocolloId
     *            The protocolloId to set.
     */
    public void setProtocolloId(int protocolloId) {
        this.protocolloId = protocolloId;
    }

    /**
     * @param annoNumeroProtocollo
     *            The annoNumeroProtocollo to set.
     */
    public void setAnnoNumeroProtocollo(String annoNumeroProtocollo) {

        this.annoNumeroProtocollo = annoNumeroProtocollo;
    }

    /**
     * @param statoProtocollo
     *            The statoProtocollo to set.
     */
    public void setStatoProtocollo(String statoProtocollo) {
        this.statoProtocollo = statoProtocollo;
    }

    /**
     * @return Returns the annoNumeroProtocollo.
     */
    public String getAnnoNumeroProtocollo() {
        // if (numeroProtocollo != 0 && annoProtocollo != 0)
        // return StringUtil.formattaNumeroProtocollo(String
        // .valueOf(numeroProtocollo), 7)
        // + "/" + String.valueOf(annoProtocollo);
        // else
        // return "";
        if (numeroProtocollo != 0 && annoProtocollo != 0)
            return String.valueOf(annoProtocollo)
                    + StringUtil.formattaNumeroProtocollo(String
                            .valueOf(numeroProtocollo), 7);
        else
            return "";
    }

    /**
     * @return Returns the annoProtocollo.
     */
    public int getAnnoProtocollo() {
        return annoProtocollo;
    }

    /**
     * @param annoProtocollo
     *            The annoProtocollo to set.
     */
    public void setAnnoProtocollo(int annoProtocollo) {
        this.annoProtocollo = annoProtocollo;
    }

    /**
     * @return Returns the dataProtocollo.
     */
    public String getDataProtocollo() {
        return dataProtocollo;
    }

    /**
     * @param dataProtocollo
     *            The dataProtocollo to set.
     */
    public void setDataProtocollo(String dataProtocollo) {
        this.dataProtocollo = dataProtocollo;
    }

    /**
     * @return Returns the dataScarico.
     */
    public String getDataScarico() {
        return dataScarico;
    }

    /**
     * @param dataScarico
     *            The dataScarico to set.
     */
    public void setDataScarico(String dataScarico) {
        this.dataScarico = dataScarico;
    }

    /**
     * @return Returns the mittente.
     */
    public String getMittente() {
        return mittente;
    }

    /**
     * @param mittente
     *            The mittente to set.
     */
    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    /**
     * @return Returns the numeroProtocollo.
     */
    public int getNumeroProtocollo() {
        return numeroProtocollo;
    }

    /**
     * @param numeroProtocollo
     *            The numeroProtocollo to set.
     */
    public void setNumeroProtocollo(int numeroProtocollo) {
        this.numeroProtocollo = numeroProtocollo;
    }

    /**
     * @return Returns the oggetto.
     */
    public String getOggetto() {
        return oggetto;
    }

    /**
     * @param oggetto
     *            The oggetto to set.
     */
    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    /**
     * @return Returns the tipoDocumento.
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * @param tipoDocumento
     *            The tipoDocumento to set.
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * @return Returns the tipoProtocollo.
     */
    public String getTipoProtocollo() {
        return tipoProtocollo;
    }

    /**
     * @param tipoProtocollo
     *            The tipoProtocollo to set.
     */
    public void setTipoProtocollo(String tipoProtocollo) {
        this.tipoProtocollo = tipoProtocollo;
    }

    /**
     * @return Returns the ufficio.
     */
    public String getUfficio() {
        return ufficio;
    }

    public String getUtenteAssegnante() {
        return utenteAssegnante;
    }

    /**
     * @param ufficio
     *            The ufficio to set.
     */
    public void setUfficio(String ufficio) {
        Organizzazione org = Organizzazione.getInstance();
        this.ufficio = org.getUfficio(Integer.parseInt(ufficio)).getPath();
    }

    /**
     * @return Returns the destinatario.
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * @param destinatario
     *            The destinatario to set.
     */
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    /**
     * @return Returns the ufficio.
     */
    public String getUfficioAssegnatario() {
        return ufficioAssegnatario;
    }

    /**
     * @return Returns the ufficio.
     */
    public String getUtenteAssegnatario() {
        return utenteAssegnatario;
    }

    /**
     * @param ufficio
     *            The ufficio to set.
     */
    public void setUfficioAssegnatario(String ufficioAssegnatario) {
        if (!Parametri.PROTOCOLLO_RISERVATO.equals(ufficioAssegnatario)) {
            Organizzazione org = Organizzazione.getInstance();
            this.ufficioAssegnatario = org.getUfficio(
                    Integer.parseInt(ufficioAssegnatario)).getPath();
        } else {
            this.ufficioAssegnatario = Parametri.PROTOCOLLO_RISERVATO;
        }
    }

    /**
     * @param ufficio
     *            The ufficio to set.
     */
    public void setUtenteAssegnatario(String utenteAssegnatario) {
        Organizzazione org = Organizzazione.getInstance();
        if (utenteAssegnatario != null && !"0".equals(utenteAssegnatario)) {
            if (!Parametri.PROTOCOLLO_RISERVATO.equals(utenteAssegnatario)) {
                this.utenteAssegnatario = org.getUtente(
                        Integer.parseInt(utenteAssegnatario)).getValueObject()
                        .getFullName();
            } else {
                this.utenteAssegnatario = Parametri.PROTOCOLLO_RISERVATO;
            }

        } else {
            this.utenteAssegnatario = "";
        }
    }

    public void setUtenteAssegnante(String utente) {
        Organizzazione org = Organizzazione.getInstance();
        if (utente != null && !"0".equals(utente)) {
            if (!Parametri.PROTOCOLLO_RISERVATO.equals(utente)) {
                this.utenteAssegnante = org.getUtente(Integer.parseInt(utente))
                        .getValueObject().getFullName();
            } else {
                this.utenteAssegnante = Parametri.PROTOCOLLO_RISERVATO;
            }

        } else {
            this.utenteAssegnante = "";
        }
    }

    public String getAssegnatario() {
        if (utenteAssegnatario != null && !"".equals(utenteAssegnatario)
                && !Parametri.PROTOCOLLO_RISERVATO.equals(utenteAssegnatario)) {
            return ufficioAssegnatario + utenteAssegnatario;
        } else {
            return ufficioAssegnatario;
        }

    }

    public String getAssegnante() {
        if (utenteAssegnante != null && !"".equals(utenteAssegnante)
                && !Parametri.PROTOCOLLO_RISERVATO.equals(utenteAssegnante)) {
            return ufficio + utenteAssegnante;
        } else {
            return ufficio;
        }
    }

    /**
     * @return Returns the dataAnnullamento.
     */
    public String getDataAnnullamento() {
        return dataAnnullamento;
    }

    /**
     * @param dataAnnullamento
     *            The dataAnnullamento to set.
     */
    public void setDataAnnullamento(String dataAnnullamento) {
        this.dataAnnullamento = dataAnnullamento;
    }

    /**
     * @return Returns the noteAnnullamento.
     */
    public String getNotaAnnullamento() {
        return notaAnnullamento;
    }

    /**
     * @param noteAnnullamento
     *            The noteAnnullamento to set.
     */
    public void setNotaAnnullamento(String notaAnnullamento) {
        this.notaAnnullamento = notaAnnullamento;
    }

    /**
     * @return Returns the provvedimentoAnnullamento.
     */
    public String getProvvedimentoAnnullamento() {
        return provvedimentoAnnullamento;
    }

    /**
     * @param provvedimentoAnnullamento
     *            The provvedimentoAnnullamento to set.
     */
    public void setProvvedimentoAnnullamento(String provvedimentoAnnullamento) {
        this.provvedimentoAnnullamento = provvedimentoAnnullamento;
    }

    /**
     * @return Returns the dataSpedizione.
     */
    public String getDataSpedizione() {
        return dataSpedizione;
    }

    /**
     * @param dataSpedizione
     *            The dataSpedizione to set.
     */
    public void setDataSpedizione(String dataSpedizione) {
        this.dataSpedizione = dataSpedizione;
    }

    /**
     * @return Returns the mezzoSpedizione.
     */
    public String getMezzoSpedizione() {
        return mezzoSpedizione;
    }

    /**
     * @param mezzoSpedizione
     *            The mezzoSpedizione to set.
     */
    public void setMezzoSpedizione(String mezzoSpedizione) {
        this.mezzoSpedizione = mezzoSpedizione;
    }

    public String getAllaccio() {
        return allaccio;
    }

    public void setAllaccio(String allaccio) {
        this.allaccio = allaccio;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public int getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(int documentoId) {
        this.documentoId = documentoId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean getIsModificabile() {
        return isModificabile;
    }

    public void setModificabile(boolean isModificabile) {
        this.isModificabile = isModificabile;
    }

    public int getTitolarioId() {
        return titolarioId;
    }

    public void setTitolarioId(int titolarioId) {
        this.titolarioId = titolarioId;
    }

}