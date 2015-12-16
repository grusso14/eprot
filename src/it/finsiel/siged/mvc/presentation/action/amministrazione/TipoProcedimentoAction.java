package it.finsiel.siged.mvc.presentation.action.amministrazione;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.AmministrazioneDelegate;
import it.finsiel.siged.mvc.business.LookupDelegate;
import it.finsiel.siged.mvc.business.UfficioDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.TipoProcedimentoForm;
import it.finsiel.siged.mvc.vo.lookup.TipoProcedimentoVO;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

/**
 * Implementation of <strong>Action </strong>.
 * 
 * 
 */

public class TipoProcedimentoAction extends Action {

    static Logger logger = Logger
            .getLogger(TipoDocumentoAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionMessages errors = new ActionMessages();// Report any errors we
        HttpSession session = request.getSession(true); // we create one if does
        AmministrazioneDelegate delegate = AmministrazioneDelegate.getInstance();
        TipoProcedimentoForm tipoProcedimentoForm = (TipoProcedimentoForm) form;
        TipoProcedimentoVO tipoProcedimentoVO = new TipoProcedimentoVO();
        //tipoProcedimentoForm.inizializzaForm();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        int utenteId = utente.getValueObject().getId().intValue();
//        Collection uffici = UfficioDelegate.getInstance().getUfficiUtente(utenteId);
        Collection uffici = UfficioDelegate.getInstance().getUffici();
        tipoProcedimentoForm.setUffici(uffici);
        if (form == null) {
            logger.info("Creating new TipoProcedimentoAction");
            form = new TipoProcedimentoForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        tipoProcedimentoForm.setTipiProcedimento(delegate.getTipiProcedimento(utente.getRegistroVOInUso().getAooId()));

        if (request.getParameter("btnNuovoTipoProcedimento") != null) {
            tipoProcedimentoForm.inizializzaForm();
            tipoProcedimentoForm.setUffici(UfficioDelegate.getInstance().getUffici(utente.getRegistroVOInUso().getAooId()));
            request.setAttribute("utenteId", ""+utenteId);
            return (mapping.findForward("edit"));

        } else if (request.getParameter("btnAnnulla") != null) {
            tipoProcedimentoForm.inizializzaForm();
            return (mapping.findForward("input"));

        } else if (request.getParameter("btnConferma") != null) {
            readTipoProcedimentoForm(tipoProcedimentoVO, tipoProcedimentoForm);
            errors = tipoProcedimentoForm.validateDatiInserimento(mapping,
                    request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                uffici = UfficioDelegate.getInstance().getUffici();
                tipoProcedimentoForm.setUffici(uffici);
                return (mapping.findForward("edit"));
            }
             // for (int i = 0; i < tipoProcedimentoForm.getIdUffici().length; i++) {
             tipoProcedimentoVO.setIdUffici(tipoProcedimentoForm.getIdUffici());
             if (tipoProcedimentoVO.getIdUffici()==null)
             {
                 errors.add("registrazione_tipo", new ActionMessage(
                         "record_non_inseribile", "il tipo Procedimento",
                         ": selezionare almeno un ufficio"));
                 saveErrors(request, errors);
                 uffici = UfficioDelegate.getInstance().getUffici();
                 tipoProcedimentoForm.setUffici(uffici);
                 return (mapping.findForward("edit"));
             } else {
                 tipoProcedimentoVO.setIdUffici(tipoProcedimentoForm.getIdUffici());
                caricaDatiNelVO(tipoProcedimentoVO, tipoProcedimentoForm, utente);
 //               String[] ufficiSel = tipoProcedimentoForm.getIdUffici();
                tipoProcedimentoVO.setIdUffici(tipoProcedimentoForm.getIdUffici());
                AmministrazioneDelegate.getInstance().salvaTipoProcedimento(tipoProcedimentoVO);
                tipoProcedimentoForm.inizializzaForm();
 
              if (tipoProcedimentoVO.getReturnValue() == ReturnValues.SAVED) 
                {
                    aggiornaLookupTipiProcedimento(tipoProcedimentoForm, utente
                            .getRegistroVOInUso().getAooId());
                   
                } 
                else {
                    errors.add("registrazione_tipo", new ActionMessage(
                            "record_non_inseribile", "il tipo Procedimento",
                            ": ne esiste già uno con lo stesso nome per l'ufficio"));
                    saveErrors(request, errors);
                    uffici = UfficioDelegate.getInstance().getUffici();
                    tipoProcedimentoForm.setUffici(uffici);
                    return (mapping.findForward("edit"));
                } 
            
             
        } 
        } 
        else if (request.getParameter("btnModifica") != null) {
            modificaDatiNelVO(tipoProcedimentoVO, tipoProcedimentoForm, utente);
            AmministrazioneDelegate.getInstance().salvaTipoProcedimento(tipoProcedimentoVO);
            if (tipoProcedimentoVO.getReturnValue() == ReturnValues.SAVED) {
                aggiornaLookupTipiProcedimento(tipoProcedimentoForm, utente
                        .getRegistroVOInUso().getAooId());
            } else {
                errors.add("registrazione_tipo", new ActionMessage(
                        "record_non_inseribile", "il tipo Procedimento",
                        ": ne esiste già uno con lo stesso nome per la AOO"));
                saveErrors(request, errors);
                uffici = UfficioDelegate.getInstance().getUffici();
                tipoProcedimentoForm.setUffici(uffici);
                return (mapping.findForward("edit"));
            }
        }

               
        else if (request.getParameter("btnCancella") != null) {
            int tipoProcedimentoId = tipoProcedimentoForm.getIdTipo();
            tipoProcedimentoVO = delegate.getTipoProcedimento(tipoProcedimentoId);
            caricaDatiNelForm(tipoProcedimentoForm, tipoProcedimentoVO);
            tipoProcedimentoForm.setUfficio(tipoProcedimentoVO.getUfficio());
            
            int idUfficio=tipoProcedimentoForm.getIdUfficio();
            if (AmministrazioneDelegate.getInstance().cancellaTipoProcedimento(tipoProcedimentoId,idUfficio)) {
                aggiornaLookupTipiProcedimento(tipoProcedimentoForm, utente
                        .getRegistroVOInUso().getAooId());
                errors.add("cancellazione_tipo", new ActionMessage(
                        "cancellazione_ok"));
            } else {
                errors.add("cancellazione_tipo", new ActionMessage(
                        "record_non_cancellabile", "il tipo Procedimento", ""));
                saveErrors(request, errors);
                
                tipoProcedimentoForm.setUfficio(tipoProcedimentoVO.getUfficio());
                return (mapping.findForward("edit"));
            }

        } else if (request.getParameter("parId") != null) {
  
            tipoProcedimentoVO = delegate.getTipoProcedimento(Integer
                    .parseInt(request.getParameter("parId")));
            caricaDatiNelForm(tipoProcedimentoForm, tipoProcedimentoVO);
           uffici = UfficioDelegate.getInstance().getUffici();
            tipoProcedimentoForm.setUfficio(tipoProcedimentoVO.getUfficio());
            return (mapping.findForward("edit"));
        }
        else if (request.getParameter("annullaAction") != null) {
            if ("true".equals(request.getParameter("annullaAction"))
                    || tipoProcedimentoForm.getIdTipo()== 0) {
    
                tipoProcedimentoForm.inizializzaForm();
            }
            
            saveToken(request);
            return mapping.findForward("input");
        }
        if (request.getParameter("cercaAction") != null) {
            String desc = tipoProcedimentoForm.getDescrizione();
            ArrayList elenco = null;
            if (desc == null || "".equals(desc)) {
                // mostra tutto
                elenco = delegate.getTipiProcedimento(utente.getRegistroVOInUso().getAooId());
                
            } else {
                // mostra con filtro su descrizione
                elenco = delegate.getTipiProcedimento(utente.getRegistroVOInUso().getAooId(),desc);
            }
            if (elenco != null) {
               
                tipoProcedimentoForm.setTipiProcedimento(elenco);

            } else {
//                tipoProcedimentoForm
//                        .setElencoListaDistribuzione(new ArrayList());
//
//            }
              //session.setAttribute(mapping.getAttribute(),tipoProcedimentoForm);
            return (mapping.findForward("input"));
        }
        }

        if (!errors.isEmpty()) {
              
            saveErrors(request, errors);
        }

        logger.info("Execute TipoDocumentoAction");
        uffici = UfficioDelegate.getInstance().getUffici();
        tipoProcedimentoForm.setUffici(uffici);
        tipoProcedimentoForm.setDescrizione("");
        return (mapping.findForward("input"));
        
        
        
    }

    public void caricaDatiNelVO(TipoProcedimentoVO vo, TipoProcedimentoForm form,
            Utente utente) {
        vo.setAooId(utente.getValueObject().getAooId());
        vo.setIdTipo(form.getIdTipo());
        vo.setIdUffici(form.getIdUffici());
        String ufficio="";
        if (form.getIdUffici()!=null) {
            ufficio = UfficioDelegate.getInstance().getUfficioVO(form.getIdUfficio()).getDescription();
        }
        vo.setUfficio(ufficio);
        vo.setDescrizione(form.getDescrizione()); 
        if (vo.getIdTipo() == 0) {
            vo.setRowCreatedUser(utente.getValueObject().getUsername());
        }
        vo.setRowUpdatedUser(utente.getValueObject().getUsername());
    }
    public void modificaDatiNelVO(TipoProcedimentoVO vo, TipoProcedimentoForm form,
            Utente utente) {
        vo.setAooId(utente.getValueObject().getAooId());
        vo.setIdTipo(form.getIdTipo());
        vo.setIdUfficio(form.getIdUfficio());
        String ufficio="";
        if (form.getIdUfficio()>0) {
            ufficio = UfficioDelegate.getInstance().getUfficioVO(form.getIdUfficio()).getDescription();
        }
        vo.setUfficio(ufficio);
        vo.setDescrizione(form.getDescrizione());
        if (vo.getIdTipo() == 0) {
            vo.setRowCreatedUser(utente.getValueObject().getUsername());
        }
        vo.setRowUpdatedUser(utente.getValueObject().getUsername());
    }

    public void caricaDatiNelForm(TipoProcedimentoForm form, TipoProcedimentoVO vo) {
        form.setIdTipo(vo.getIdTipo());
        form.setDescrizione(vo.getDescrizione());
        String ufficioId = vo.getIdUfficio()+"";
        form.setIdUfficio(Integer.parseInt(ufficioId));
       //form.setUffici(TipoProcedimentoDelegate.getInstance().
       //        getUfficioVO(ufficioId)));
    }

    private void aggiornaLookupTipiProcedimento(TipoProcedimentoForm form, int aooId) {
        form.setTipiProcedimento(AmministrazioneDelegate.getInstance()
                .getTipiProcedimento(aooId));
        LookupDelegate.getInstance().caricaTipiProcedimento();
        logger.info("AmministrazioneDelegate: caricaTipiProcedimento");
    }
    public static void readTipoProcedimentoForm(TipoProcedimentoVO vo,
            TipoProcedimentoForm form) {
        vo.setDescrizione(form.getDescrizione());
    }

}
