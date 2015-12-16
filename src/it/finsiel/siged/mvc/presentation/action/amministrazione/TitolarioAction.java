package it.finsiel.siged.mvc.presentation.action.amministrazione;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.TitolarioDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.TitolarioForm;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
 * Implementation of <strong>Action </strong> to create a new E-Photo Utente.
 * 
 * @author Almaviva sud.
 * 
 */

public final class TitolarioAction extends Action {

    static Logger logger = Logger.getLogger(TitolarioAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();

        HttpSession session = request.getSession(true);

        TitolarioVO titolario = new TitolarioVO();

        TitolarioForm titolarioForm = (TitolarioForm) form;

        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        if (form == null) {
            logger.info("Creating new Titolario Form");
            form = new TitolarioForm();
            session.setAttribute(mapping.getAttribute(), form);
        }
        if (request.getParameter("impostaTitolarioAction") != null) {
            if (titolarioForm.getTitolario() != null) {
                titolarioForm.setTitolarioPrecedenteId(titolarioForm
                        .getTitolario().getId().intValue());
            }
            Utente ute = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            impostaTitolario(titolarioForm, ute.getUfficioInUso(),
                    titolarioForm.getTitolarioSelezionatoId(), utente
                            .getUfficioVOInUso().getAooId());
            return mapping.findForward("input");

        } else if (request.getParameter("titolarioPrecedenteAction") != null) {
            impostaTitolario(titolarioForm, utente.getRegistroInUso(),
                    titolarioForm.getTitolarioPrecedenteId(), utente
                            .getUfficioVOInUso().getAooId());
            if (titolarioForm.getTitolario() != null) {
                titolarioForm.setTitolarioPrecedenteId(titolarioForm
                        .getTitolario().getParentId());
            }
            return mapping.findForward("input");

        } else if (request.getParameter("btnModifica") != null
                || request.getParameter("btnSposta") != null) {
            titolarioForm
                    .setId(titolarioForm.getTitolario().getId().intValue());
            titolarioForm.setCodice(titolarioForm.getTitolario().getCodice());
            titolarioForm.setDescrizione(titolarioForm.getTitolario()
                    .getDescrizione());
            titolarioForm.setParentId(titolarioForm.getTitolario()
                    .getParentId());
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            TitolarioVO tPadre = td.getTitolario(titolarioForm.getTitolario()
                    .getParentId());
            if (tPadre != null) {
                titolarioForm.setParentPath(tPadre.getPath());
                titolarioForm.setParentDescrizione(tPadre.getDescrizione());
            }
            if (request.getParameter("btnSposta") != null) {
                return mapping.findForward("sposta");
            }
            return mapping.findForward("aggiorna");

        } else if (request.getParameter("btnNuovo") != null) {

            if (titolarioForm.getTitolario() != null) {
                titolarioForm.setParentPath(titolarioForm.getTitolario()
                        .getPath());
                titolarioForm.setParentDescrizione(titolarioForm.getTitolario()
                        .getDescrizione());
            }
            titolarioForm.setId(0);
            titolarioForm.setCodice(null);
            titolarioForm.setDescrizione(null);
            titolarioForm.setMassimario(null);
            if (titolarioForm.getTitolario() != null) {
                titolarioForm.setParentId(titolarioForm.getTitolario().getId()
                        .intValue());
            } else {
                titolarioForm.setParentId(0);

            }

            return mapping.findForward("aggiorna");
        } else if (request.getParameter("btnAnnullaSposta") != null) {
            return mapping.findForward("annulla");
        } else if (request.getParameter("btnConferma") != null) {
            aggiornaTitolarioModel(titolario, titolarioForm, utente);
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            td.salvaArgomento(titolario);
        } else if (request.getParameter("btnConfermaSposta") != null) {
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            titolario = td.getTitolario(titolarioForm.getId());
            titolario.setFullPathDescription(td.getPathName(titolario));
            titolario.setParentId(titolarioForm.getTitolario().getId()
                    .intValue());

            td.salvaArgomento(titolario);
            return (mapping.findForward("annulla"));

        } else if (request.getParameter("btnStoria") != null) {
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            Collection storia = new ArrayList();
            storia = td.getStoriaTitolario(titolarioForm.getTitolario().getId()
                    .intValue());
            titolarioForm.setStoriaTitolario(storia);
            return (mapping.findForward("storia"));

        } else if (request.getParameter("btnCancella") != null) {
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            if (!td.cancellaArgomento(titolarioForm.getId())) {
                errors.add("argomentoNonCancellabile", new ActionMessage(
                        "record_non_cancellabile", " il Titolario", ""));
                saveErrors(request, errors);
                return (mapping.findForward("aggiorna"));
            }
            errors.add("argomentoCancellato", new ActionMessage(
                    "cancellazione_ok"));
            saveErrors(request, errors);

            return (mapping.findForward("input"));

        } else if (request.getParameter("btnAssocia") != null) {
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            Organizzazione org = Organizzazione.getInstance();

            Ufficio ufficioRoot = org.getAreaOrganizzativa(
                    utente.getRegistroVOInUso().getAooId())
                    .getUfficioCentrale();
            Ufficio uff = org.getUfficio(ufficioRoot.getValueObject().getId()
                    .intValue());
            Collection uffici = new ArrayList();
            uffici.add(uff);
            selezionaUffici(uff, uffici);
            titolarioForm.setUfficiDipendenti(uffici);
            titolarioForm
                    .setId(titolarioForm.getTitolario().getId().intValue());
            titolarioForm.setCodice(titolarioForm.getTitolario().getCodice());
            titolarioForm.setDescrizione(titolarioForm.getTitolario()
                    .getDescrizione());
            titolarioForm.setParentId(titolarioForm.getTitolario()
                    .getParentId());
            titolarioForm.setUfficiTitolario(td
                    .getUfficiTitolario(titolarioForm.getTitolario().getId()
                            .intValue()));
            return (mapping.findForward("associa"));
        } else if (request.getParameter("btnConfermaAssociazione") != null) {
//            if (request.getParameterValues("ufficiTitolario") != null) {
                TitolarioDelegate td = TitolarioDelegate.getInstance();
//                int returnValue = td.associaUfficiArgomento(titolarioForm
//                        .getTitolario(), request
//                        .getParameterValues("ufficiTitolario"));
                int returnValue = td.associaTitolarioUffici(titolarioForm
                        .getTitolario(), request
                        .getParameterValues("ufficiTitolario"),utente.getUfficioVOInUso().getAooId());

                if (returnValue == ReturnValues.SAVED) {
                    errors.add("asociazioneUffici", new ActionMessage(
                            "operazione_ok"));
                } else if (returnValue == ReturnValues.UNKNOWN) {
                    errors.add("asociazioneUffici", new ActionMessage(
                            "errore_nel_salvataggio"));
                } else if (returnValue == ReturnValues.FOUND) {
                    errors
                            .add(
                                    "record_non_cancellabile",
                                    new ActionMessage(
                                            "record_non_cancellabile",
                                            "l'associazione titolario uffici.",
                                            " Il titolario è stato referenziato in Protocolli, Fascicoli, Faldoni Procedimenti "));
                }
                saveErrors(request, errors);
  //          }
            return (mapping.findForward("associa"));

        } else if (request.getParameter("btnAssociaTuttiUffici") != null) {
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            int returnValue = td.associaTuttiGliUfficiTitolario(titolarioForm
                    .getTitolario(), utente.getUfficioVOInUso().getAooId());
            if (returnValue == ReturnValues.SAVED) {
                errors.add("asociazioneUffici", new ActionMessage(
                        "operazione_ok"));
            } else if (returnValue == ReturnValues.UNKNOWN) {
                errors.add("asociazioneUffici", new ActionMessage(
                        "errore_nel_salvataggio"));
            } else if (returnValue == ReturnValues.FOUND) {
                errors
                        .add(
                                "record_non_cancellabile",
                                new ActionMessage(
                                        "record_non_cancellabile",
                                        "l'associazione titolario uffici.",
                                        " Il titolario è stato referenziato in Protocolli, Fascicoli, Faldoni Procedimenti "));
            }
            saveErrors(request, errors);

            return (mapping.findForward("uffici"));

        }
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return (mapping.findForward("edit"));
        }

        logger.info("Execute Titolario");
        if (titolarioForm.getTitolario() != null) {
            impostaTitolario(titolarioForm, utente.getUfficioInUso(),
                    titolarioForm.getTitolario().getId().intValue(), utente
                            .getUfficioVOInUso().getAooId());
        } else {
            impostaTitolario(titolarioForm, utente.getUfficioInUso(), 0, utente
                    .getUfficioVOInUso().getAooId());
        }

        return (mapping.findForward("input")); // 

    }

    private void aggiornaTitolarioForm(TitolarioVO titolarioVO,
            TitolarioForm form, Utente utente) {
        int titolarioId = titolarioVO.getId().intValue();
        impostaTitolario(form, utente.getUfficioInUso(), titolarioId, utente
                .getUfficioVOInUso().getAooId());
    }

    // private void impostaTitolario(TitolarioForm form, int ufficioId,
    // int titolarioId) {
    // TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);
    // }

    private void impostaTitolario(TitolarioForm form, int ufficioId,
            int titolarioId, int aooId) {
        TitolarioDelegate td = TitolarioDelegate.getInstance();
        form.setTitolario(td.getTitolario(titolarioId));
        form.setTitolariFigli(td.getTitolariByParent(titolarioId, aooId));
    }

    private void aggiornaTitolarioModel(TitolarioVO titolarioVO,
            TitolarioForm form, Utente utente) {
        titolarioVO.setId(form.getId());
        titolarioVO.setAooId(utente.getRegistroVOInUso().getAooId());
        titolarioVO.setCodice(form.getCodice());
        titolarioVO.setDescrizione(form.getDescrizione());
        if (form.getMassimario().equals("")) {
            titolarioVO.setMassimario(0);
        } else
            titolarioVO.setMassimario(Integer.parseInt(form.getMassimario()));
        if (titolarioVO.getId().intValue() == 0 && form.getParentId() > 0) {
            titolarioVO.setPath(form.getParentPath() + "."
                    + titolarioVO.getCodice());
        } else {
            if (form.getParentId() > 0) {
                titolarioVO.setPath(form.getParentPath());
            } else {
                titolarioVO.setPath(form.getCodice());
            }
        }
        titolarioVO.setParentId(form.getParentId());
        if (form.getTitolario() == null) {
            titolarioVO.setVersione(0);
        } else {
            titolarioVO.setVersione(form.getTitolario().getVersione());
        }

    }

    private void selezionaUffici(Ufficio uff, Collection uffici) {
        try {
            for (Iterator i = uff.getUfficiDipendenti().iterator(); i.hasNext();) {
                Ufficio u = (Ufficio) i.next();
                selezionaUffici(u, uffici);
                uffici.add(u);
            }
        } catch (Exception de) {
            logger.error("TitolarioAction: failed selezionaUffici: ");
        }
    }

}