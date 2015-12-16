/*
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model;

import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;

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
public class InvioFascicolo {
    private int fascicoloId;

    private Collection documenti = new ArrayList(); // di tipo FileVO

    private Map destinatari = new HashMap(); // di tipo DestinatarioVO

    private Collection protocolli = new ArrayList();

    public Collection getDestinatariCollection() {
        return destinatari.values();
    }

    public Map getDestinatari() {
        return destinatari;
    }

    public void addDestinatari(DestinatarioVO destinatario) {
        if (destinatario != null) {
            destinatari.put(destinatario.getDestinatario()+destinatario.getMezzoDesc(), destinatario);
        }
    }

    public void removeDestinatario(String destinatario) {
        destinatari.remove(destinatario);
    }

    public void removeDestinatari() {
        if (destinatari != null) {
            destinatari.clear();
        }
    }

    public void setDestinatari(Map destinatari) {
        this.destinatari = destinatari;
    }

    public Collection getDocumenti() {
        return documenti;
    }

    public void setDocumenti(Collection documenti) {
        this.documenti = documenti;
    }

    public int getFascicoloId() {
        return fascicoloId;
    }

    public void setFascicoloId(int fascicoloId) {
        this.fascicoloId = fascicoloId;
    }

    public Collection getProtocolli() {
        return protocolli;
    }

    public void setProtocolli(Collection protocolli) {
        this.protocolli = protocolli;
    }
}