package it.finsiel.siged.mvc.presentation.action.amministrazione.org.aoo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.AreaOrganizzativa;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.AreaOrganizzativaDelegate;
import it.finsiel.siged.mvc.business.OrganizzazioneDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.org.aoo.AreaOrganizzativaForm;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.NumberUtil;

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
 * Implementation of <strong>Action </strong> to create a new E-Photo Utente.
 * 
 * @author Almaviva sud.
 * 
 */

public class AreaOrganizzativaAction extends Action {

    static Logger logger = Logger.getLogger(AreaOrganizzativaAction.class
            .getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        AreaOrganizzativaForm aooForm = (AreaOrganizzativaForm) form;
        AreaOrganizzativaDelegate aooDelegate = AreaOrganizzativaDelegate
                .getInstance();
        String username = ((Utente) session.getAttribute(Constants.UTENTE_KEY))
                .getValueObject().getUsername();

        if (form == null) {
            aooForm = new AreaOrganizzativaForm();
            request.setAttribute(mapping.getAttribute(), aooForm);
        }

        if (request.getParameter("btnModifica") != null) {
            System.out.println("Id vale: " + request.getParameter("id"));
            if (NumberUtil.isInteger(request.getParameter("id"))) {
                int id = NumberUtil.getInt(request.getParameter("id"));
                AreaOrganizzativaVO aooVO = aooDelegate
                        .getAreaOrganizzativa(id);
                if (aooVO != null) {
                    caricaDatiNelForm(aooForm, aooVO);
                    return (mapping.findForward("edit"));
                } else {
                    errors.add("general", new ActionMessage("selezionare.aoo"));
                }
            } else {
                errors.add("general", new ActionMessage("selezionare.aoo"));
            }
        } else if (request.getParameter("btnSalva") != null) {
            errors = aooForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return mapping.findForward("edit");
            }
            AreaOrganizzativaVO aooVO = new AreaOrganizzativaVO();
            AreaOrganizzativaVO aooSalvata = new AreaOrganizzativaVO();
            int id = aooForm.getId();
            String descrizioneAoo = aooForm.getDescription();
            if (!aooDelegate.esisteAreaOrganizzativa(descrizioneAoo, id)) {
                caricaDatiNelVO(aooVO, aooForm, username);
                aooSalvata = aooDelegate.salvaAreaOrganizzativa(aooVO, utente);
                if (aooSalvata != null
                        && aooSalvata.getReturnValue() == ReturnValues.SAVED) {
                    if (id == 0) {
                        aooForm.setAreeOrganizzative(OrganizzazioneDelegate
                                .getInstance().getAreeOrganizzative());
                        aggiornaAreaOrganizzative(aooSalvata, id);
                        request.setAttribute(mapping.getAttribute(), aooForm);
                        StringBuffer sB = new StringBuffer("Username: admin"
                                + aooSalvata.getId() + "\r\n");
                        sB.append("Password: admin" + aooSalvata.getId()
                                + "\r\n");
                        aooForm.setMsgSuccess(sB.toString());
                        return (mapping.findForward("success"));
                    } else {
                        errors.add("aoo", new ActionMessage("operazione_ok"));
                    }
                    aggiornaAreaOrganizzative(aooSalvata, id);
                } else {
                    errors.add("aoo", new ActionMessage(
                            "errore_nel_salvataggio"));
                }
            } else {
                errors.add("aoo", new ActionMessage("errore.save.aoo"));
                saveErrors(request, errors);
                return mapping.findForward("edit");

            }
        } else if (request.getParameter("btnNuovo") != null) {
            aooForm.inizializzaForm();
            request.setAttribute(mapping.getAttribute(), aooForm);
            return mapping.findForward("edit");

        } else if (request.getParameter("btnCancella") != null) {
            AreaOrganizzativaVO aooVO = new AreaOrganizzativaVO();
            errors = aooForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            }
            if (request.getParameter("id") != null) {
                int id = Integer.parseInt(request.getParameter("id"));
                aooVO = aooDelegate.getAreaOrganizzativa(id);
                caricaDatiNelVO(aooVO, aooForm, username);
                if (aooDelegate.cancellaAreaOrganizzativa(id)) {
                    rimuoviAreaOrganizzativa(id);
                    aooForm.setAreeOrganizzative(OrganizzazioneDelegate
                            .getInstance().getAreeOrganizzative());
                    request.setAttribute(mapping.getAttribute(), aooForm);
                } else {
                    errors.add("cancella_aoo", new ActionMessage(
                            "record_non_cancellabile", "la AOO", ""));
                    if (!errors.isEmpty()) {
                        saveErrors(request, errors);
                    }
                }
            }
        }
        try {
            aooForm.setAreeOrganizzative(OrganizzazioneDelegate.getInstance()
                    .getAreeOrganizzative());
        } catch (DataException e) {
            errors.add("general", new ActionMessage("errore.load.aooList"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return (mapping.findForward("input"));
    }

    public void caricaDatiNelVO(AreaOrganizzativaVO vo,
            AreaOrganizzativaForm form, String username) {
        vo.setId(form.getId());
        if (form.getId() == 0) {
            vo.setRowCreatedUser(username);
        } else {
            vo.setRowUpdatedUser(username);
        }

        vo.setCodi_aoo(form.getCodi_aoo());
        vo.setCodi_documento_doc(form.getCodi_documento_doc());
        vo.setDescription(form.getDescription());
        if (form.getData_istituzione() != null) {
            vo.setData_istituzione(DateUtil.toDate(form.getData_istituzione()));
        } else {
            vo.setData_istituzione(DateUtil.toDate(""));
        }
        vo.setResponsabile_nome(form.getResponsabile_nome());
        vo.setResponsabile_cognome(form.getResponsabile_cognome());
        vo.setResponsabile_email(form.getResponsabile_email());
        vo.setResponsabile_telefono(form.getResponsabile_telefono());
        if (form.getData_soppressione() != null) {
            vo.setData_soppressione(DateUtil
                    .toDate(form.getData_soppressione()));
        } else {
            vo.setData_soppressione(DateUtil.toDate(""));
        }
        vo.setTelefono(form.getTelefono());
        vo.setFax(form.getFax());
        vo.setIndi_dug(form.getIndi_dug());
        vo.setIndi_toponimo(form.getIndi_toponimo());
        vo.setIndi_civico(form.getIndi_civico());
        vo.setIndi_cap(form.getIndi_cap());
        vo.setIndi_comune(form.getIndi_comune());
        vo.setEmail(form.getEmail());
        vo.setDipartimento_codice(form.getDipartimento_codice());
        vo.setDipartimento_descrizione(form.getDipartimento_descrizione());
        vo.setTipo_aoo(form.getTipo_aoo());
        vo.setProvincia_id(form.getProvincia_id());
        vo.setCodi_documento_doc(form.getCodi_documento_doc());
        vo.setAmministrazione_id(form.getAmministrazione_id());
        vo.setVersione(form.getVersione());
        // dati posta elettronica
        vo.setPec_indirizzo(form.getPec_indirizzo());
        vo.setPec_pop3(form.getPec_pop3());
        vo.setPec_pwd(form.getPec_pwd());
        vo.setPec_smtp(form.getPec_smtp());
        vo.setPec_smtp_port(form.getPec_smtp_port());
        vo.setPecAbilitata(form.getPecAbilitata());
        vo.setPec_ssl_port(form.getPec_ssl_port());
        vo.setPec_username(form.getPec_username());
        vo.setPecTimer(form.getPecTimer());
        vo.setPn_indirizzo(form.getPn_indirizzo());
        vo.setPn_pop3(form.getPn_pop3());
        vo.setPn_pwd(form.getPn_pwd());
        vo.setPn_smtp(form.getPn_smtp());
        vo.setPn_ssl(form.getPn_ssl());
        vo.setPn_ssl_port(form.getPn_ssl_port());
        vo.setPn_username(form.getPn_username());
        vo.setDipendenzaTitolarioUfficio(form.getDipendenzaTitolarioUfficio());
        vo.setTitolarioLivelloMinimo(form.getTitolarioLivelloMinimo());
    }

    public void caricaDatiNelForm(AreaOrganizzativaForm form,
            AreaOrganizzativaVO aooVO) {
        form.setId(aooVO.getId().intValue());
        form.setCodi_aoo(aooVO.getCodi_aoo());
        form.setDescription(aooVO.getDescription());
        if (aooVO.getData_istituzione() != null)
            form.setData_istituzione(DateUtil.formattaData(aooVO
                    .getData_istituzione().getTime()));
        form.setResponsabile_nome(aooVO.getResponsabile_nome());
        form.setResponsabile_cognome(aooVO.getResponsabile_cognome());
        form.setResponsabile_email(aooVO.getResponsabile_email());
        form.setResponsabile_telefono(aooVO.getResponsabile_telefono());
        if (aooVO.getData_soppressione() != null)
            form.setData_soppressione(DateUtil.formattaData(aooVO
                    .getData_soppressione().getTime()));
        form.setTelefono(aooVO.getTelefono());
        form.setFax(aooVO.getFax());
        form.setIndi_dug(aooVO.getIndi_dug());
        form.setIndi_toponimo(aooVO.getIndi_toponimo());
        form.setIndi_civico(aooVO.getIndi_civico());
        form.setIndi_cap(aooVO.getIndi_cap());
        form.setIndi_comune(aooVO.getIndi_comune());
        form.setEmail(aooVO.getEmail());
        form.setDipartimento_codice(aooVO.getDipartimento_codice());
        form.setDipartimento_descrizione(aooVO.getDipartimento_descrizione());
        form.setTipo_aoo(aooVO.getTipo_aoo());
        form.setProvincia_id(aooVO.getProvincia_id());
        form.setCodi_documento_doc(aooVO.getCodi_documento_doc());
        form.setAmministrazione_id(aooVO.getAmministrazione_id());
        form.setDesc_amministrazione(Organizzazione.getInstance()
                .getValueObject().getDescription());
        // dati posta elettronica
        form.setPec_indirizzo(aooVO.getPec_indirizzo());
        form.setPec_pop3(aooVO.getPec_pop3());
        form.setPec_pwd(aooVO.getPec_pwd());
        form.setPec_smtp(aooVO.getPec_smtp());
        form.setPecAbilitata(aooVO.getPecAbilitata());
        form.setPec_ssl_port(aooVO.getPec_ssl_port());
        form.setPec_username(aooVO.getPec_username());
        form.setPec_smtp_port(aooVO.getPec_smtp_port());
        form.setPecTimer(aooVO.getPecTimer());
        form.setPn_indirizzo(aooVO.getPn_indirizzo());
        form.setPn_pop3(aooVO.getPn_pop3());
        form.setPn_pwd(aooVO.getPn_pwd());
        form.setPn_smtp(aooVO.getPn_smtp());

        form.setPn_ssl(aooVO.getPn_ssl());
        form.setPn_ssl_port(aooVO.getPn_ssl_port());
        form.setPn_username(aooVO.getPn_username());
        form.setTitolarioLivelloMinimo(aooVO.getTitolarioLivelloMinimo());
        form.setDipendenzaTitolarioUfficio(aooVO
                .getDipendenzaTitolarioUfficio());
        form.setVersione(aooVO.getVersione());
        form
                .setModificabileDipendenzaTitolarioUfficio(AreaOrganizzativaDelegate
                        .getInstance()
                        .isModificabileDipendenzaTitolarioUfficio(aooVO.getId().intValue()));
    }

    public void inizializzaForm(AreaOrganizzativaForm aooForm) {
        Collection province = null;
        aooForm.setId(0);
        aooForm.setAmministrazione_id(0);
        aooForm.setCodi_aoo(null);
        aooForm.setCodi_documento_doc(null);
        aooForm.setData_istituzione(null);
        aooForm.setData_soppressione(null);
        aooForm.setDescription(null);
        aooForm.setDipartimento_codice(null);
        aooForm.setDipartimento_descrizione(null);
        aooForm.setEmail(null);
        aooForm.setFax(null);
        aooForm.setIndi_cap(null);
        aooForm.setIndi_civico(null);
        aooForm.setIndi_comune(null);
        aooForm.setIndi_dug(null);
        aooForm.setIndi_toponimo(null);
        aooForm.setProvincia_id(0);
        aooForm.setProvince(province);
        aooForm.setResponsabile_cognome(null);
        aooForm.setResponsabile_email(null);
        aooForm.setResponsabile_nome(null);
        aooForm.setResponsabile_telefono(null);
        aooForm.setTelefono(null);
        aooForm.setTipo_aoo(null);
        aooForm.setTitolarioLivelloMinimo(0);
        aooForm.setDipendenzaTitolarioUfficio(0);
        aooForm.setVersione(0);

    }

    private void rimuoviAreaOrganizzativa(int aooId) {
        Organizzazione org = Organizzazione.getInstance();
        org.removeAreaOrganizzativa(new Integer(aooId));
    }

    private void aggiornaAreaOrganizzative(AreaOrganizzativaVO aooVO, int aooId) {
        Organizzazione org = Organizzazione.getInstance();
        AreaOrganizzativa aoo;
        if (aooId == 0) {
            aoo = new AreaOrganizzativa(aooVO);
        } else {
            aoo = org.getAreaOrganizzativa(aooId);
            aoo.setValueObject(aooVO);
        }
        org.addAreaOrganizzativa(aoo);
    }
}