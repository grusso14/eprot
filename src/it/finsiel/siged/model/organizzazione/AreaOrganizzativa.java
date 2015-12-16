package it.finsiel.siged.model.organizzazione;

import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;

public class AreaOrganizzativa {
    AreaOrganizzativaVO valueObject;

    private Organizzazione amministrazione;

    private Ufficio ufficioCentrale;

    public AreaOrganizzativa(AreaOrganizzativaVO vo) {
        setValueObject(vo);
    }

    public AreaOrganizzativaVO getValueObject() {
        return valueObject;
    }

    public void setValueObject(AreaOrganizzativaVO aooVO) {
        this.valueObject = aooVO;
    }

    public Organizzazione getAmministrazione() {
        return amministrazione;
    }

    void setAmministrazione(Organizzazione amministrazione) {
        this.amministrazione = amministrazione;
    }

    public Ufficio getUfficioCentrale() {
        return ufficioCentrale;
    }

    public void setUfficioCentrale(Ufficio ufficio) {
        this.ufficioCentrale = ufficio;
    }
}