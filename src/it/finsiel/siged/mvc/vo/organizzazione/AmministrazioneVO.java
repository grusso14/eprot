package it.finsiel.siged.mvc.vo.organizzazione;

import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.lookup.ParametriLdapVO;

public class AmministrazioneVO extends IdentityVO {

    public AmministrazioneVO() {

    }

    private String flagLdap;

    private ParametriLdapVO parametriLdap;

    private String pathDoc;

    public String getFlagLdap() {
        return flagLdap;
    }

    public void setFlagLdap(String flagLdap) {
        this.flagLdap = flagLdap;
    }

    public ParametriLdapVO getParametriLdap() {
        return parametriLdap;
    }

    public void setParametriLdap(ParametriLdapVO parametriLdap) {
        this.parametriLdap = parametriLdap;
    }

    public String getPathDoc() {
        return pathDoc;
    }

    public void setPathDoc(String dirDocumenti) {
        this.pathDoc = dirDocumenti;
    }
}