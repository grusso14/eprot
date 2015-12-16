package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.mvc.business.ProcedimentoDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProcedimentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.StoriaProcedimentoForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

public class StoriaProcedimentoAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(StoriaProcedimentoAction.class
            .getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();// Report any errors we

        HttpSession session = request.getSession();

        StoriaProcedimentoForm storiaProcedimentoForm = (StoriaProcedimentoForm) form;

        if (form == null) {
            logger.info(" Creating new StoriaProcedimentoAction");
            form = new StoriaProcedimentoForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        ProcedimentoForm procedimentoForm = new ProcedimentoForm();
        procedimentoForm = (ProcedimentoForm) session.getAttribute("procedimentoForm");
        if (request.getParameter("versioneSelezionata") != null) {
        //??    procedimentoForm.setModificabile(false);
            request.setAttribute("procedimentoId", new Integer(procedimentoForm.getProcedimentoId()));
            request.setAttribute("versioneId", new Integer(request
                    .getParameter("versioneSelezionata")));
            return (mapping.findForward("procedimento"));

        } else if (request.getParameter("versioneCorrente") != null) {
      //?      procedimentoForm.setModificabile(true);
            request.setAttribute("procedimentoId", new Integer(procedimentoForm.getProcedimentoId()));
            return (mapping.findForward("procedimento"));
        } else if (request.getParameter("btnStoriaProcedimento") != null) {
            request.setAttribute("procedimentoId", new Integer(procedimentoForm.getProcedimentoId()));
            request.setAttribute("btnRitornoStoria", Boolean.TRUE);
            return (mapping.findForward("procedimento"));
        } else {
            storiaProcedimentoForm.setProgressivo(procedimentoForm.getProgressivo());
            storiaProcedimentoForm.setVersioniProcedimento(ProcedimentoDelegate
                    .getInstance().getStoriaProcedimento(procedimentoForm.getProcedimentoId()));
        }
        
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        logger.info("Execute StoriaProcedimentoAction");

        return mapping.getInputForward();

    }

}