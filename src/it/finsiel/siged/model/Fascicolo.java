/*
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model;

import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Fascicolo {
    private FascicoloVO fascicoloVO = new FascicoloVO();

    private Collection protocolli = new ArrayList(); // di ProtocolloVO

    private Collection documenti = new ArrayList(); // di DocumentoVO

    private Collection faldoni = new ArrayList(); // di FaldoneVO

    private Collection procedimenti = new ArrayList(); // di ProcedimentoVO

    public Collection getDocumenti() {
        return documenti;
    }

    public void setDocumenti(Collection documenti) {
        this.documenti = documenti;
    }

    public FascicoloVO getFascicoloVO() {
        return fascicoloVO;
    }

    public void setFascicoloVO(FascicoloVO fascicolo) {
        this.fascicoloVO = fascicolo;
    }

    public Collection getProtocolli() {
        return protocolli;
    }

    public void setProtocolli(Collection protocolli) {
        this.protocolli = protocolli;
    }

    public Collection getFaldoni() {
        return faldoni;
    }

    public void setFaldoni(Collection faldoni) {
        this.faldoni = faldoni;
    }

    public Collection getProcedimenti() {
        return procedimenti;
    }

    public void setProcedimenti(Collection procedimenti) {
        this.procedimenti = procedimenti;
    }
}