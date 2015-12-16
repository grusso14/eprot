package it.finsiel.siged.model;

import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioClassificatiVO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class InvioClassificati {

    private InvioClassificatiVO icVO = new InvioClassificatiVO(); // di tipo

    // InvioClassificatiVO

    private Map destinatari = new HashMap(); // di tipo

    // DestinatariVO

    public Map getDestinatari() {
        return destinatari;
    }

    public Collection getDestinatariCollection() {
        return destinatari.values();
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

    public InvioClassificatiVO getIcVO() {
        return icVO;
    }

    public void setIcVO(InvioClassificatiVO icVO) {
        this.icVO = icVO;
    }
}