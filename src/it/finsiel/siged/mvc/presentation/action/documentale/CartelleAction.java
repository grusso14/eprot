package it.finsiel.siged.mvc.presentation.action.documentale;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.CannotDeleteException;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.DocumentaleDelegate;
import it.finsiel.siged.mvc.presentation.actionform.documentale.CartelleForm;
import it.finsiel.siged.mvc.vo.documentale.CartellaVO;
import it.finsiel.siged.util.NumberUtil;

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

public class CartelleAction extends Action {

    static Logger logger = Logger.getLogger(CartelleAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession(true);
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        int utenteId = utente.getValueObject().getId().intValue();
        CartelleForm cForm = (CartelleForm) form;
        DocumentaleDelegate delegate = DocumentaleDelegate.getInstance();

        String cartellaSelezionata = request.getParameter("sfogliaCartellaId");
        String browse = request.getParameter("browse");

        int cartellaSelezionataId = NumberUtil.getInt(cartellaSelezionata);

        if (request.getParameter("nuovaCartella") != null) {
            cForm.setNomeCartella("");
            return mapping.findForward("nuova");
        } else if (request.getParameter("nuovoDocumento") != null) {
            request.setAttribute("cartellaSelezionataId", String.valueOf(cForm
                    .getCartellaCorrenteId()));

            return mapping.findForward("nuovoDocumento");
        } else if (request.getParameter("documentoSelezionatoId") != null) {
            try {
                int id = NumberUtil.getInt(request
                        .getParameter("documentoSelezionatoId"));
                if (delegate.isOwnerDocumento(id, utenteId)) {
                    request.setAttribute("documentoId", new Integer(id));
                    return mapping.findForward("visualizzaDocumento");
                } else {
                    errors.add("permissi", new ActionMessage(
                            "error.documento.no_permission"));
                }
            } catch (DataException de) {
                errors.add("generale",
                        new ActionMessage("database.cannot.load"));
            }
        } else if (request.getParameter("eliminaCartella") != null) {
            int eliminaCartellaId = cForm.getCartellaCorrenteId();
            if (delegate.hasAccessToFolder(eliminaCartellaId, utenteId)) {
                try {
                    if (DocumentaleDelegate.getInstance().getCartellaVO(
                            eliminaCartellaId).isRoot()) {
                        errors.add("cartella_non_cancellabile",
                                new ActionMessage("record_non_cancellabile",
                                        "la cartella selezionata", ""));
                    } else {
                        int parentId = DocumentaleDelegate.getInstance()
                                .cancellaCartella(eliminaCartellaId);
                        cartellaSelezionataId = parentId;
                        try {
                            if (delegate.hasAccessToFolder(
                                    cartellaSelezionataId, utenteId)) {
                                if (cartellaSelezionataId >= 0) {
                                    // remove all object after the selected
                                    // one...
                                    ArrayList newPath = new ArrayList(1);
                                    Iterator it = cForm.getPathCartella()
                                            .iterator();
                                    while (it.hasNext()) {
                                        CartellaVO c = (CartellaVO) it.next();
                                        newPath.add(c);
                                        if (c.getId().intValue() == cartellaSelezionataId)
                                            break;
                                    }
                                    cForm.setPathCartella(newPath);
                                }
                            } else {
                                errors.add("permissi", new ActionMessage(
                                        "error.cartella.no_permission"));
                            }
                        } catch (DataException de) {
                            errors.add("generale", new ActionMessage(
                                    "database.cannot.load"));
                        }

                    }
                } catch (DataException e) {
                    errors.add("generale", new ActionMessage(
                            "errore_nel_salvataggio"));
                } catch (CannotDeleteException e) {
                    
                   /* errors.add("cartella_non_cancellabile",
                            new ActionMessage("errore.cartella.cannot_delete",
                                    "la cartella selezionata"));*/
                    errors.add("generale", new ActionMessage(
                            "errore.cartella.cannot_delete",e.getMessage()));
                }
            } else {
                errors.add("permissi", new ActionMessage(
                        "error.cartella.no_permission"));
            }
        } else if (request.getParameter("rinominaCartella") != null) {
            cForm.setCartellaId(cForm.getCartellaCorrenteId());
            cForm.setNomeCartella(delegate.getCartellaVO(
                    cForm.getCartellaCorrenteId()).getNome());
            return mapping.findForward("rinominaCartella");
        } else if (request.getParameter("aggiornaCartella") != null) {
            CartellaVO ca = new CartellaVO();
            ca.setNome(cForm.getNomeCartella());
            ca.setId(cForm.getCartellaId());
            try {
                CartellaVO newCa = delegate.updateNomeCartellaVO(ca);
                if (newCa.getReturnValue() != ReturnValues.SAVED) {
                    if (newCa.getReturnValue() == ReturnValues.FOUND)
                        errors.add("cartella_presente", new ActionMessage(
                                "error.nome.cartella"));
                    else
                        errors.add("generale", new ActionMessage(
                                "errore_nel_salvataggio"));
                } else {
                    // TODO: creare un metoto aggiorna path, e aggiungere il
                    // blocco sotto
                    ArrayList newPath = new ArrayList(1);
                    Iterator it = cForm.getPathCartella().iterator();
                    while (it.hasNext()) {
                        CartellaVO c = (CartellaVO) it.next();
                        if (c.getId().intValue() == newCa.getId().intValue()) {
                            newPath.add(newCa);
                            break;
                        } else {
                            newPath.add(c);
                        }
                    }
                    cForm.setPathCartella(newPath);
                    cartellaSelezionataId = ca.getId().intValue();
                }
            } catch (DataException e) {
                errors.add("generale", new ActionMessage(
                        "errore_nel_salvataggio"));
            }
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.findForward("rinominaCartella");
            } else {
                ActionMessages msg = new ActionMessages();
                msg.add("operazioneEffettuata", new ActionMessage(
                        "cartella_aggiornata"));
                saveMessages(request, msg);
                cartellaSelezionataId = cForm.getCartellaCorrenteId();
            }
        } else if (request.getParameter("salvaCartella") != null) {
            CartellaVO nuovaCartella = preparaCartellaVO(cForm, utente);
            try {
                errors = cForm.validate(mapping, request);
                if (errors.isEmpty()) {
                    int returnValue = delegate
                            .creaCartellaUtente(nuovaCartella).getReturnValue();
                    if (returnValue != ReturnValues.SAVED) {
                        if (returnValue == ReturnValues.FOUND)
                            errors.add("cartella_presente", new ActionMessage(
                                    "error.nome.cartella"));
                        else
                            errors.add("generale", new ActionMessage(
                                    "errore_nel_salvataggio"));
                    }
                } else {
                    saveErrors(request, errors);
                }
            } catch (DataException e) {
                errors.add("generale", new ActionMessage(
                        "errore_nel_salvataggio"));
            }
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.findForward("nuova");
            } else {
                ActionMessages msg = new ActionMessages();
                msg.add("operazioneEffettuata", new ActionMessage(
                        "cartella_creata"));
                saveMessages(request, msg);
                cartellaSelezionataId = cForm.getCartellaCorrenteId();
            }
        } else if (request.getParameter("spostaDocumento") != null) {
            int spostaCartellaId = cForm.getCartellaCorrenteId();
            int dfaId = cForm.getDocumentoId();
            if (delegate.hasAccessToFolder(spostaCartellaId, utenteId)) {
                try {
                    DocumentaleDelegate.getInstance().spostaDocumento(
                            spostaCartellaId, dfaId);
                    request.setAttribute("documentoId", new Integer(cForm
                            .getDocumentoId()));
                    return mapping.findForward("visualizzaDocumento");
                } catch (DataException e) {
                    errors.add("generale", new ActionMessage(
                            "errore_nel_salvataggio"));
                }
            } else {
                errors.add("permissi", new ActionMessage(
                        "error.cartella.no_permission"));
            }
        } else if (request.getParameter("gotoCartellaId") != null) {
            try {
                int idx = NumberUtil.getInt(request
                        .getParameter("gotoCartellaId"));
                if (delegate.hasAccessToFolder(idx, utenteId)) {
                    if (idx >= 0) {
                        // remove all object after the selected one...
                        ArrayList newPath = new ArrayList(1);
                        Iterator it = cForm.getPathCartella().iterator();
                        while (it.hasNext()) {
                            CartellaVO c = (CartellaVO) it.next();
                            newPath.add(c);
                            if (c.getId().intValue() == idx)
                                break;
                        }
                        cForm.setPathCartella(newPath);
                        cartellaSelezionataId = idx;
                    }
                } else {
                    errors.add("permissi", new ActionMessage(
                            "error.cartella.no_permission"));
                }
            } catch (DataException de) {
                errors.add("generale",
                        new ActionMessage("database.cannot.load"));
            }
        } else if (browse == null && cartellaSelezionataId <= 0) {
            CartellaVO root = delegate.getCartellaVOByUfficioUtenteId(utente
                    .getUfficioInUso(), utenteId);
            cartellaSelezionataId = root.getId().intValue();
            ArrayList a = new ArrayList(1);
            a.add(root);
            cForm.setPathCartella(a);
        } else if (cartellaSelezionataId > 0) {
            try {
                if (delegate.hasAccessToFolder(cartellaSelezionataId, utenteId)) {
                    CartellaVO corrente = delegate
                            .getCartellaVO(cartellaSelezionataId);
                    cForm.getPathCartella().add(corrente);
                } else {
                    errors.add("permissi", new ActionMessage(
                            "error.cartella.no_permission"));
                }
            } catch (DataException de) {
                errors.add("generale",
                        new ActionMessage("database.cannot.load"));
            }
        }
        if (errors.isEmpty()) {
            try {
                Collection sottoCartelle = delegate
                        .getSottoCartelle(cartellaSelezionataId);
                cForm.setSottoCartelle(sottoCartelle);
                cForm.setCartellaCorrenteId(cartellaSelezionataId);

                cForm.setFiles(delegate.getFilesLista(cartellaSelezionataId)
                        .values());
            } catch (DataException de) {
                errors.add("generale",
                        new ActionMessage("database.cannot.load"));
            }
        }
        saveErrors(request, errors);
        return mapping.findForward("input");
    }

    public CartellaVO preparaCartellaVO(CartelleForm cForm, Utente utente) {
        CartellaVO vo = new CartellaVO();
        vo.setAooId(utente.getValueObject().getAooId());
        vo.setNome(cForm.getNomeCartella());
        vo.setParentId(cForm.getCartellaCorrenteId());
        vo.setRoot(false);
        vo.setUfficioId(utente.getUfficioInUso());
        vo.setUtenteId(utente.getValueObject().getId().intValue());
        return vo;
    }

}