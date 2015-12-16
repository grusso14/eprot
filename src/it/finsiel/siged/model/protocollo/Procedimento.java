/*
 * Created on 22-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model.protocollo;

import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Paolo Spadadafora - digital highway
 * 
 */
public class Procedimento {

    private ProcedimentoVO procedimentoVO = new ProcedimentoVO();

    private Map protocolli = new HashMap(2);

    private Map fascicoli = new HashMap(2);

    private Map faldoni = new HashMap(2);

    public Map getFaldoni() {
        return faldoni;
    }

    public void setFaldoni(Map faldoni) {
        this.faldoni = faldoni;
    }

    public Map getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(Map fascicoli) {
        this.fascicoli = fascicoli;
    }

    public ProcedimentoVO getProcedimentoVO() {
        return procedimentoVO;
    }

    public void setProcedimentoVO(ProcedimentoVO procedimentoVO) {
        this.procedimentoVO = procedimentoVO;
    }

    public Map getProtocolli() {
        return protocolli;
    }

    public void setProtocolli(Map protocolli) {
        this.protocolli = protocolli;
    }

}