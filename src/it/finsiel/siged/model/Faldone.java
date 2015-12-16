/*
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.model;

import it.finsiel.siged.mvc.vo.protocollo.FaldoneVO;

import java.util.ArrayList;
import java.util.Collection;

public class Faldone {
    private FaldoneVO faldoneVO = new FaldoneVO();

    private Collection fascicoli = new ArrayList(); // di tipo Fascicolo

    private Collection procedimenti = new ArrayList(); // di ProcedimentoVO

    public FaldoneVO getFaldoneVO() {
        return faldoneVO;
    }

    public void setFaldoneVO(FaldoneVO faldoneVO) {
        this.faldoneVO = faldoneVO;
    }

    public Collection getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(Collection fascicoli) {
        this.fascicoli = fascicoli;
    }

    public Collection getProcedimenti() {
        return procedimenti;
    }

    public void setProcedimenti(Collection provvedimenti) {
        this.procedimenti = provvedimenti;
    }

}