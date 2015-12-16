package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.StoriaProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.StoriaProtocolloForm;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

public class StoriaProtocolloAction extends Action {

    // ----------------------------------------------------- Instance Variables

    /**
     * The <code>Log</code> instance for this application.
     */
    static Logger logger = Logger.getLogger(StoriaProtocolloAction.class
            .getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();// Report any errors we

        HttpSession session = request.getSession();

        StoriaProtocolloForm storiaProtocolloForm = (StoriaProtocolloForm) form;

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

        if (form == null) {
            logger.info(" Creating new StoriaProtocolloAction");
            form = new StoriaProtocolloForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        if (request.getAttribute("protocolloId") != null) {
            ProtocolloForm pF = new ProtocolloForm();
            pF = (ProtocolloForm) session.getAttribute("protocolloForm");
            if ("I".equals(pF.getFlagTipo())) {
                AssegnatarioVO assVO = ProtocolloDelegate.getInstance()
                        .getAssegnatarioPerCompetenza(pF.getProtocolloId());
                String assegnatario;
                String uteAssegnatario = "";
                if (!pF.getRiservato()) {
                    Organizzazione org = Organizzazione.getInstance();
                    assegnatario = org.getUfficio(
                            assVO.getUfficioAssegnatarioId()).getPath();
                    if (assVO.getUtenteAssegnatarioId() != 0) {
                        uteAssegnatario = org.getUtente(
                                assVO.getUtenteAssegnatarioId())
                                .getValueObject().getFullName();
                        if (uteAssegnatario != null) {
                            assegnatario += uteAssegnatario;
                        }
                    }
                } else {
                    assegnatario = Parametri.PROTOCOLLO_RISERVATO;
                }

                storiaProtocolloForm.setAssegnatario(assegnatario);
            }
            ProtocolloVO protVO = ProtocolloDelegate.getInstance()
                    .getProtocolloById(pF.getProtocolloId());
            storiaProtocolloForm.setVersione(protVO.getVersione());
            storiaProtocolloForm.setMotivazione(protVO.getMotivazione());
            storiaProtocolloForm.setProtocolloId(pF.getProtocolloId());
            storiaProtocolloForm.setFlagTipo(pF.getFlagTipo());
            if (protVO.getFlagTipoMittente().equals("F")) {
                storiaProtocolloForm.setCognomeMittente(protVO
                        .getCognomeMittente()
                        + " "
                        + it.finsiel.siged.util.StringUtil.getStringa(protVO
                                .getNomeMittente()));
            } else if (protVO.getFlagTipoMittente().equals("G")) {
                storiaProtocolloForm.setCognomeMittente(protVO
                        .getDenominazioneMittente());
            }

            // int docId= pVO.getDocumentoPrincipaleId().intValue();
            // storiaProtocolloForm.setDocumentoId(docId);
            storiaProtocolloForm.setDocumentoId(pF.getDocumentoId());

            storiaProtocolloForm.setNumeroProtocollo(pF.getNumeroProtocollo());
            storiaProtocolloForm.setDataRegistrazione(DateUtil
                    .formattaDataOra(protVO.getRowCreatedTime().getTime()));
            storiaProtocolloForm.setOggetto(pF.getOggetto());

            storiaProtocolloForm.setStato(pF.getStato());
            storiaProtocolloForm.setUfficioProtocollatoreId(pF
                    .getUfficioProtocollatoreId());
            storiaProtocolloForm.setUtenteProtocollatoreId(pF
                    .getUtenteProtocollatoreId());
            storiaProtocolloForm.setUserUpdate(protVO.getRowUpdatedUser());

            int protocolloId = Integer.parseInt(request.getAttribute(
                    "protocolloId").toString());
            storiaProtocolloForm.setScartato(StoriaProtocolloDelegate
                    .getInstance().isScartato(protocolloId));
            storiaProtocolloForm.setVersioniProtocollo(StoriaProtocolloDelegate
                    .getInstance().getStoriaProtocollo(utente, protocolloId,
                            pF.getFlagTipo()));
            return (mapping.findForward("input"));
        } else if (request.getParameter("versioneSelezionata") != null) {
            ProtocolloForm pF = new ProtocolloForm();
            pF = (ProtocolloForm) session.getAttribute("protocolloForm");
            pF.setModificabile(false);
            request.setAttribute("protocolloId", new Integer(pF
                    .getProtocolloId()));
            request.setAttribute("versioneId", new Integer(request
                    .getParameter("versioneSelezionata")));
            if ("I".equals(pF.getFlagTipo())) {
                return (mapping.findForward("visualizzaProtocolloIngresso"));
            } else {
                return (mapping.findForward("visualizzaProtocolloUscita"));
            }

        } else if (request.getParameter("versioneCorrente") != null) {
            ProtocolloForm pF = new ProtocolloForm();
            pF = (ProtocolloForm) session.getAttribute("protocolloForm");
            pF.setModificabile(false);
            request.setAttribute("protocolloId", new Integer(pF
                    .getProtocolloId()));
            if ("I".equals(pF.getFlagTipo())) {
                return (mapping.findForward("visualizzaProtocolloIngresso"));
            } else {
                return (mapping.findForward("visualizzaProtocolloUscita"));
            }

        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        logger.info("Execute StoriaProtocolloAction");

        return mapping.getInputForward();
    }

}
