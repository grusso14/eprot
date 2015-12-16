package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import it.finsiel.siged.mvc.presentation.action.protocollo.StoriaProtocolloAction;

import java.util.Collection;

import org.apache.log4j.Logger;

public class StoriaProtocolloForm extends ProtocolloForm {

    private Collection versioniProtocollo;
    
    private String tipoProtocollo;
    
    private String destinatario;
    
    private String assegnatario;

    static Logger logger = Logger.getLogger(StoriaProtocolloAction.class
            .getName());

    public Collection getVersioniProtocollo() {
        return versioniProtocollo;
    }

    public void setVersioniProtocollo(Collection versioniProtocollo) {
        this.versioniProtocollo = versioniProtocollo;
    }

    private boolean scartato = false;

    private String userUpdate;

    public boolean isScartato() {
        return scartato;
    }

    public void setScartato(boolean scartato) {
        this.scartato = scartato;
    }

    /**
     * @return Returns the userUpdate.
     */
    public String getUserUpdate() {
        return userUpdate;
    }

    /**
     * @param userUpdate
     *            The userUpdate to set.
     */
    public void setUserUpdate(String userUpdate) {
        this.userUpdate = userUpdate;
    }
    public String getLabelDestinatarioAssegnatario() {

        if (getFlagTipo().equals("I")) {
            return "Assegnatari";
        } else if (getFlagTipo().equals("U")) {
            return "Destinatari";
        } else if (getFlagTipo().equals("M")) {
            return "Destinatari";
        } else if (getFlagTipo().equals("Mozione + uscita")) {
            return "Destinatari";
        } else {
            return "Assegnatari/Destinatari";
        }

    }

    public String getTipoProtocollo() {
        return tipoProtocollo;
    }

    public void setTipoProtocollo(String tipoProtocollo) {
        this.tipoProtocollo = tipoProtocollo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAssegnatario() {
        return assegnatario;
    }

    public void setAssegnatario(String assegnatario) {
        this.assegnatario = assegnatario;
    }
    
    private String motivazione;

	public String getMotivazione() {
		return motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}
    

}