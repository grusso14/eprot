package it.finsiel.siged.mvc.presentation.actionform.soggetto;

import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public abstract class SoggettoForm extends ActionForm {

    private SoggettoVO soggetto = new SoggettoVO('F');

    /**
     * @return Returns the soggetto.
     */
    public SoggettoVO getSoggetto() {
        return soggetto;
    }

    /**
     * @param soggetto
     *            The soggetto to set.
     */
    public void setSoggetto(SoggettoVO soggetto) {
        this.soggetto = soggetto;
    }

    public abstract void reset(ActionMapping mapping, HttpServletRequest request);

    public abstract ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request);

    private int soggettoId;

    /**
     * @return Returns the id.
     */
    public int getSoggettoId() {
        return soggettoId;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setSoggettoId(int id) {
        this.soggettoId = id;
    }
}
