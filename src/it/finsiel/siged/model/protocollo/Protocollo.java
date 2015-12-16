/*
 * Created on 22-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model.protocollo;

import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class Protocollo {

	private boolean addOggetto;
	private boolean addFisica;
	private boolean addGiuridica;
    /**
	 * @return the addGiuridica
	 */
	public boolean isAddGiuridica() {
		return addGiuridica;
	}

	/**
	 * @param addGiuridica the addGiuridica to set
	 */
	public void setAddGiuridica(boolean addGiuridica) {
		this.addGiuridica = addGiuridica;
	}

	/**
	 * @return the addOggetto
	 */
	public boolean isAddOggetto() {
		return addOggetto;
	}

	/**
	 * @param addOggetto the addOggetto to set
	 */
	public void setAddOggetto(boolean addOggetto) {
		this.addOggetto = addOggetto;
	}
	
	

	/**
	 * @return the addFisica
	 */
	public boolean isAddFisica() {
		return addFisica;
	}

	/**
	 * @param addFisica the addFisica to set
	 */
	public void setAddFisica(boolean addFisica) {
		this.addFisica = addFisica;
	}

	private ProtocolloVO protocollo = new ProtocolloVO();

    private Collection allacci = new ArrayList(); // di tipo AllaccioVO

    private Collection procedimenti = new ArrayList(); // di tipo ProtocolloProcedimentoVO
    
    private Map allegati = new HashMap(2); // K=string with null,

    // V=DocumentoVO

    private Map documentiRimossi = new HashMap(2); // K=Integer,

    // V=DocumentoVO

    private DocumentoVO documentoPrincipale = new DocumentoVO();

    public ProtocolloVO getProtocollo() {
        return protocollo;
    }

    public void setProtocollo(ProtocolloVO protocollo) {
        this.protocollo = protocollo;
    }

    public Collection getAllacci() {
        return new ArrayList(allacci);
    }

    public void allacciaProtocollo(AllaccioVO allaccio) {
        if (allaccio != null) {
            allacci.add(allaccio);
        }
    }

    public void setAllacci(Collection allacci) {
        this.allacci.clear();
        this.allacci.addAll(allacci);
    }

    public Map getAllegati() {
        return allegati;
    }

    public void allegaDocumento(DocumentoVO documento) {
        if (documento != null) {
            ProtocolloBO.putAllegato(documento, allegati);
        }
    }

    public void removeDocumento(Object key) {
        if (allegati.containsKey(key))
            allegati.remove(key);
    }

    public void setAllegati(Map a) {
        this.allegati = a;
    }

    public Collection getAllegatiCollection() {
        return allegati.values();
    }

    /**
     * @return Returns the documentoPrincipale.
     */
    public DocumentoVO getDocumentoPrincipale() {
        return documentoPrincipale;
    }

    /**
     * @param documentoPrincipale
     *            The documentoPrincipale to set.
     */
    public void setDocumentoPrincipale(DocumentoVO documentoPrincipale) {
        this.documentoPrincipale = documentoPrincipale;
    }

    public Map getDocumentiRimossi() {
        return documentiRimossi;
    }

    public void setDocumentiRimossi(Map documentiRimossi) {
        this.documentiRimossi = documentiRimossi;
    }

    public Collection getProcedimenti() {
        return procedimenti;
    }

    public void setProcedimenti(Collection procedimenti) {
        this.procedimenti = procedimenti;
    }
}