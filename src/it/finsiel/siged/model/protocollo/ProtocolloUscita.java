/*
 * Created on 22-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model.protocollo;

import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ProtocolloUscita extends Protocollo {
    private AssegnatarioVO mittente;

    private Map destinatari = new HashMap(); // di tipo DestinatarioVO

    private int fascicoloInvioId;

    private int documentoInvioId;

    /**
     * @return Returns the destinatari.
     */
    public Collection getDestinatari() {
        return destinatari.values();
    }

    public void addDestinatari(DestinatarioVO destinatario) {
        if (destinatario != null) {
            destinatari.put(destinatario.getIdx(), destinatario);
            
        }
    }

    public void removeDestinatario(Integer destinatarioId) {
        destinatari.remove(destinatarioId);
    }

    public void removeDestinatario(int destinatarioId) {
        removeDestinatario(new Integer(destinatarioId));
    }

    public void removeDestinatario(DestinatarioVO destinatario) {
        if (destinatario != null) {
            removeDestinatario(destinatario.getId());
        }
    }

    public void removeDestinatari() {
        if (destinatari != null) {
            destinatari.clear();
        }
    }

    /**
     * @param destinatari
     *            The destinatari to set.
     */
    public void setDestinatari(Map destinatari) {
        this.destinatari = destinatari;
    }

    public DestinatarioVO getDestinatario(Integer destinatarioId) {
        return (DestinatarioVO) destinatari.get(destinatarioId);
    }

    public DestinatarioVO getDestinatario(int destinatarioId) {
        return getDestinatario(new Integer(destinatarioId));
    }

    /**
     * @return Returns the mittente.
     */
    public AssegnatarioVO getMittente() {
        return mittente;
    }

    /**
     * @param mittente
     *            The mittente to set.
     */
    public void setMittente(AssegnatarioVO mittente) {
        this.mittente = mittente;
    }

    public int getFascicoloInvioId() {
        return fascicoloInvioId;
    }

    public void setFascicoloInvioId(int fascicoloInvioId) {
        this.fascicoloInvioId = fascicoloInvioId;
    }

    public int getDocumentoInvioId() {
        return documentoInvioId;
    }

    public void setDocumentoInvioId(int documentoInvioId) {
        this.documentoInvioId = documentoInvioId;
    }
    
    private String[] destinatariToSaveId;

	/**
	 * @return the destinatariToSaveId
	 */
	public String[] getDestinatariToSaveId() {
		return destinatariToSaveId;
	}

	/**
	 * @param destinatariToSaveId the destinatariToSaveId to set
	 */
	public void setDestinatariToSaveId(String[] destinatariToSaveId) {
		this.destinatariToSaveId = destinatariToSaveId;
	}
}