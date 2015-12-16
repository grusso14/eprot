/*
 * Created on 25-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.organizzazione.AreaOrganizzativa;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.AlberoUfficiForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.AlberoUfficiUtentiForm;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AlberoUfficiBO {

    public static void impostaUfficio(Utente utente, AlberoUfficiForm form,
            boolean alberoCompleto) {
        int ufficioId = form.getUfficioCorrenteId();
        Organizzazione org = Organizzazione.getInstance();
        Ufficio ufficioRoot = org.getUfficio(utente.getUfficioInUso());
        if (alberoCompleto) { // ufficio centrale
            AreaOrganizzativa aoo = org.getAreaOrganizzativa(ufficioRoot
                    .getValueObject().getAooId());
            ufficioRoot = aoo.getUfficioCentrale();
        }
        Ufficio ufficioCorrente = org.getUfficio(ufficioId);
        if (ufficioCorrente == null) {
            // ufficioCorrente = ufficioRoot;
            // ufficioId = ufficioRoot.getValueObject().getId().intValue();
            ufficioCorrente = org.getUfficio(utente.getUfficioInUso());
            ;
            ufficioId = ufficioCorrente.getValueObject().getId().intValue();
        }
        Ufficio uff = ufficioCorrente;
        while (uff != ufficioRoot) {
            if (uff == null) {
                ufficioCorrente = ufficioRoot;
                ufficioId = ufficioCorrente.getValueObject().getId().intValue();
                break;
            }
            uff = uff.getUfficioDiAppartenenza();
        }
        form.setUfficioCorrenteId(ufficioId);
        form.setUfficioCorrentePath(ufficioCorrente.getPath());
        form.setUfficioCorrente(ufficioCorrente.getValueObject());
        List list = new ArrayList();
        for (Iterator i = ufficioCorrente.getUfficiDipendenti().iterator(); i
                .hasNext();) {
            uff = (Ufficio) i.next();
            list.add(uff.getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UfficioVO uff1 = (UfficioVO) obj1;
                UfficioVO uff2 = (UfficioVO) obj2;
                return uff1.getDescription().compareToIgnoreCase(
                        uff2.getDescription());
            }
        };
        Collections.sort(list, c);
        form.setUfficiDipendenti(list);
    }

    public static void impostaUfficioUtenti(Utente utente,
            AlberoUfficiUtentiForm form, boolean alberoCompleto) {
        Organizzazione org = Organizzazione.getInstance();
        impostaUfficio(utente, form, alberoCompleto);
        Ufficio ufficioCorrente = org.getUfficio(form.getUfficioCorrenteId());
        List list = new ArrayList();
        for (Iterator i = ufficioCorrente.getUtenti().iterator(); i.hasNext();) {
            Utente ute = (Utente) i.next();
            list.add(ute.getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UtenteVO ute1 = (UtenteVO) obj1;
                UtenteVO ute2 = (UtenteVO) obj2;
                return ute1.getFullName().compareToIgnoreCase(
                        ute2.getFullName());
            }
        };
        Collections.sort(list, c);
        form.setUtenti(list);
    }

    public static void impostaUfficioUtentiAbilitati(Utente utente,
            AlberoUfficiUtentiForm form, boolean alberoCompleto) {
        Organizzazione org = Organizzazione.getInstance();
        impostaUfficio(utente, form, alberoCompleto);
        Ufficio ufficioCorrente = org.getUfficio(form.getUfficioCorrenteId());
        List list = new ArrayList();
        for (Iterator i = ufficioCorrente.getUtenti().iterator(); i.hasNext();) {
            Utente ute = (Utente) i.next();
            if (ute.getValueObject().isAbilitato()) {
                list.add(ute.getValueObject());
            }
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UtenteVO ute1 = (UtenteVO) obj1;
                UtenteVO ute2 = (UtenteVO) obj2;
                return ute1.getFullName().compareToIgnoreCase(
                        ute2.getFullName());
            }
        };
        Collections.sort(list, c);
        form.setUtenti(list);
    }

    public static void impostaUfficioUtentiReferenti(Utente utente,
            AlberoUfficiUtentiForm form, boolean alberoCompleto) {
        Organizzazione org = Organizzazione.getInstance();
        impostaUfficio(utente, form, alberoCompleto);
        Ufficio ufficioCorrente = org.getUfficio(form.getUfficioCorrenteId());
        List list = new ArrayList();
        list = new ArrayList();
        for (Iterator i = ufficioCorrente.getUtentiReferenti().iterator(); i
                .hasNext();) {
            Utente ute = (Utente) i.next();
            list.add(ute.getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UtenteVO ute1 = (UtenteVO) obj1;
                UtenteVO ute2 = (UtenteVO) obj2;
                return ute1.getFullName().compareToIgnoreCase(
                        ute2.getFullName());
            }
        };
        Collections.sort(list, c);
        form.setUtenti(list);

    }

}