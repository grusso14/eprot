package it.finsiel.siged.mvc.presentation.helper;

import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.bo.ProtocolloBO;

public class VersioneProtocolloView {

    public VersioneProtocolloView() {
    }

    private int protocolloId;

    private int versione;

    private String tipoProtocollo;

    private String oggetto;
    
    private String cognomeMittente;

    private String statoProtocollo;

    private String dateUpdated;

    private String userUpdated;
    
    private int documentoId;
    
    private String riservato;
   
    private String ufficioAssegnatario;

    private String utenteAssegnatario;
    
    private String motivazione;
    
    /**
     * @return Returns the statoProtocollo.
     */
    public String getStatoProtocollo() {
        return ProtocolloBO.getStatoProtocollo(this.tipoProtocollo,
                this.statoProtocollo);
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
     * @param statoProtocollo
     *            The statoProtocollo to set.
     */
    public void setStatoProtocollo(String statoProtocollo) {
        this.statoProtocollo = statoProtocollo;
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

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getUserUpdated() {
        return userUpdated;
    }

    public void setUserUpdated(String userUpdated) {
        this.userUpdated = userUpdated;
    }

    public String getCognomeMittente() {
        return cognomeMittente;
    }

    public void setCognomeMittente(String cognomeMittente) {
        this.cognomeMittente = cognomeMittente;
    }

    public int getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(int documentoId) {
        this.documentoId = documentoId;
    }

    public String getRiservato() {
        return riservato;
    }

    public void setRiservato(String riservato) {
        this.riservato = riservato;
    }

    public void setUfficioAssegnatario(String ufficioAssegnatario) {
        if (!Parametri.PROTOCOLLO_RISERVATO.equals(getRiservato())) {
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
            if (!Parametri.PROTOCOLLO_RISERVATO.equals(getRiservato())) {
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

    public String getAssegnatario() {
        if (utenteAssegnatario != null && !"".equals(utenteAssegnatario)
                && !Parametri.PROTOCOLLO_RISERVATO.equals(utenteAssegnatario)) {
            return ufficioAssegnatario + " " + utenteAssegnatario;
        } else {
            return ufficioAssegnatario;
        }

    }

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

}