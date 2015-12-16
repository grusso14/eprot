/*
 * Created on 22-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model.protocollo;

import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ProtocolloIngresso extends Protocollo {

    private Integer messaggioEmailId;

    private SoggettoVO mittente;

    private Collection assegnatari = new ArrayList(); // di tipo

    // modifica pino 08/02/2006
    private String msgAssegnatarioCompetente;

    // AssegnatarioVO

    public SoggettoVO getMittente() {
        return mittente;
    }

    public void setMittente(SoggettoVO mittente) {
        this.mittente = mittente;
    }

    public Collection getAssegnatari() {
        return new ArrayList(assegnatari);
    }

    public void aggiungiAssegnatario(AssegnatarioVO assegnatario) {
        if (assegnatario != null) {
            assegnatari.add(assegnatario);
        }
    }

    public void aggiungiAssegnatari(Collection assegnatari) {
        this.assegnatari.addAll(assegnatari);
    }

    public void removeAssegnatari() {
        assegnatari.clear();
    }

    public Integer getMessaggioEmailId() {
        return messaggioEmailId;
    }

    public void setMessaggioEmailId(Integer messaggioEmailId) {
        this.messaggioEmailId = messaggioEmailId;
    }

    public String getMsgAssegnatarioCompetente() {
        return msgAssegnatarioCompetente;
    }

    public void setMsgAssegnatarioCompetente(String msgAssegnatarioCompetente) {
        this.msgAssegnatarioCompetente = msgAssegnatarioCompetente;
    }

}