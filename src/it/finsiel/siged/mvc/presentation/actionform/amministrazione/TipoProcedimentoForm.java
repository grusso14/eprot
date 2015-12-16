package it.finsiel.siged.mvc.presentation.actionform.amministrazione;

import it.finsiel.siged.mvc.presentation.action.amministrazione.TipoProcedimentoAction;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

public final class TipoProcedimentoForm extends ActionForm {

    static Logger logger = Logger
            .getLogger(TipoProcedimentoAction.class.getName());

  
    
    
    private Collection tipiProcedimento = new ArrayList();

    private int idTipo;
    
    private int idUfficio;

    private String descrizione;

    private Collection uffici = new ArrayList();
    
    private String ufficio;
    
    private int aooId;
    
    private String[] idUffici;
    
    
    

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public String getUfficio() {
        return ufficio;
    }

    public void setUfficio(String ufficio) {
        this.ufficio = ufficio;
    }

    public Collection getUffici() {
      
        return uffici;
    }

    public void setUffici(Collection uffici) {
        this.uffici = uffici;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public int getIdUfficio() {
        return idUfficio;
    }

    public void setIdUfficio(int idUfficio) {
        this.idUfficio = idUfficio;
    }

    public Collection getTipiProcedimento() {
        return tipiProcedimento;
    }

    public void setTipiProcedimento(ArrayList tipiProcedimento) {
        this.tipiProcedimento = tipiProcedimento;
    }

    public void reset(ActionMapping mapping, HttpServletRequest request) {

    }

//    public ActionErrors validate(ActionMapping mapping,
//            HttpServletRequest request) {
//        ActionErrors errors = new ActionErrors();
//        if (request.getParameter("btnConferma") != null
//                && (descrizione == null || "".equals(descrizione.trim()))) {
//            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
//                    "Descrizione", ""));
//            
//        } else if (request.getParameter("btnConferma") != null
//                && (request.getParameter("descrizione") == null || "".equals(request.getParameter("descrizione")))) {
//            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
//                    "Descrizione", ""));
//        }
//        
//       return errors;
//
//    }
    public ActionErrors validateDatiInserimento(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if (request.getParameter("btnConferma") != null
                && (descrizione == null || "".equals(descrizione.trim()))) {
            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
                    "Descrizione", ""));
        }
        else if (getDescrizione() == null || "".equals(getDescrizione().trim())) {
            errors.add("descrizione", new ActionMessage("campo.obbligatorio",
                    "descrizione", ""));
        }

        return errors;
    }
    

    public void inizializzaForm() {
        setIdTipo(0);
        setDescrizione(null);
        setIdUfficio(0);
        setUffici(uffici);
        setTipiProcedimento(null);
        
    }

    public String[] getIdUffici() {
        return idUffici;
    }

    public void setIdUffici(String[] idUffici) {
        this.idUffici = idUffici;
    }

}