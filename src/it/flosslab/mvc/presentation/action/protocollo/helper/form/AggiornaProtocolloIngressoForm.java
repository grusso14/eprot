/*
*
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
*
* This file is part of e-prot 1.1 software.
* e-prot 1.1 is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
* Version: e-prot 1.1
*/
package it.flosslab.mvc.presentation.action.protocollo.helper.form;

import java.sql.SQLException;
import java.util.Iterator;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.Protocollo;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.flosslab.mvc.business.MittentiDelegate;

import javax.servlet.http.HttpSession;


public class AggiornaProtocolloIngressoForm extends AggiornaProtocolloForm {

	/**
     * Aggiorna il form prendendo i dati dal model
     */
    public static void aggiorna(Protocollo protocollo, ProtocolloForm form,
            HttpSession session) {
    	AggiornaProtocolloForm.aggiorna(protocollo, form, session);
        ProtocolloIngresso protocolloIngresso = (ProtocolloIngresso) protocollo;
        ProtocolloIngressoForm piForm = (ProtocolloIngressoForm) form;

        piForm.setMessaggioEmailId(protocolloIngresso.getMessaggioEmailId());
        // stato protocollo
        aggiornaStatoProtocolloForm(protocolloIngresso, piForm, session);

        // mittente
        aggiornaMittenteForm(protocolloIngresso, piForm);

        // assegnatari
        aggiornaAssegnatariForm(protocolloIngresso, piForm);

        // titolario
        aggiornaTitolarioForm(protocolloIngresso.getProtocollo(), piForm,
                (Utente) session.getAttribute(Constants.UTENTE_KEY));
    }
    
// Metodi che aggionano il Form a partire dal model

    
    private static void aggiornaMittenteForm(ProtocolloIngresso protocollo,ProtocolloIngressoForm form) {
        form.getMittente().setTipo(protocollo.getProtocollo().getFlagTipoMittente());
        SoggettoVO mittente = null;
        if ("F".equals(protocollo.getProtocollo().getFlagTipoMittente())) {
            mittente = new SoggettoVO('F');
            mittente.setCognome(protocollo.getProtocollo().getCognomeMittente());
            mittente.setNome(protocollo.getProtocollo().getNomeMittente());
        }else if ("M".equals(protocollo.getProtocollo().getFlagTipoMittente())) {
            mittente = new SoggettoVO('M');
            MittentiDelegate delegate = MittentiDelegate.getInstance();
            try {
				form.getMittenti().addAll(delegate.getMittenti(protocollo.getProtocollo().getId()));
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (DataException e) {
				e.printStackTrace();
			}
        } else {
            mittente = new SoggettoVO('G');
            mittente.setDescrizioneDitta(protocollo.getProtocollo().getDenominazioneMittente());
        }
        form.setNumProtocolloMittente(protocollo.getProtocollo().getNumProtocolloMittente());
        mittente.getIndirizzo().setToponimo(protocollo.getProtocollo().getMittenteIndirizzo());
        mittente.getIndirizzo().setComune(protocollo.getProtocollo().getMittenteComune());
        mittente.getIndirizzo().setProvinciaId(protocollo.getProtocollo().getMittenteProvinciaId());
        mittente.getIndirizzo().setCap(protocollo.getProtocollo().getMittenteCap());
        form.setMittente(mittente);
    }

    private static void aggiornaAssegnatariForm(ProtocolloIngresso protocollo,
            ProtocolloIngressoForm form) {
        Organizzazione org = Organizzazione.getInstance();
        // modifica daniele sanna  07/07/2008
        for (Iterator i = protocollo.getAssegnatari().iterator(); i.hasNext();) {
        	AssegnatarioVO assegnatario = (AssegnatarioVO) i.next();
            AssegnatarioView ass = new AssegnatarioView();
            int uffId = assegnatario.getUfficioAssegnatarioId();
            ass.setUfficioId(uffId);
            Ufficio uff = org.getUfficio(uffId);
            if (uff != null) {
                ass.setNomeUfficio(uff.getValueObject().getDescription());
            }
            int uteId = assegnatario.getUtenteAssegnatarioId();
            ass.setUtenteId(uteId);
            Utente ute = org.getUtente(uteId);
            if (ute != null) {
                ass.setNomeUtente(ute.getValueObject().getFullName());
            }
            ass.setStato(assegnatario.getStatoAssegnazione());
            form.aggiungiAssegnatario(ass);
            if (assegnatario.isCompetente()) {
                form.setAssegnatarioCompetente(ass.getKey());
                form.setMsgAssegnatarioCompetente(assegnatario.getMsgAssegnatarioCompetente());
            }
            
        }
       
    }

    private static void aggiornaStatoProtocolloForm(ProtocolloIngresso protocollo,
            ProtocolloIngressoForm form, HttpSession session) {
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        form.setModificabile(ProtocolloBO.isModificable(protocollo, utente));
        if (form.isModificabile()
                || !protocollo.getProtocollo().isRiservato()
                || (protocollo.getProtocollo().isRiservato() && utente
                        .getValueObject().getId().intValue() == protocollo
                        .getProtocollo().getUtenteProtocollatoreId())) {
            form.setDocumentoVisibile(true);
        } else {
            form.setDocumentoVisibile(false);
        }

    }
    
    private static void aggiornaTitolarioForm(ProtocolloVO protocollo,
            ProtocolloIngressoForm form, Utente utente) {
        int titolarioId = protocollo.getTitolarioId();
        impostaTitolario(form, utente, titolarioId);
    }

    public static void impostaTitolario(ProtocolloIngressoForm form, Utente utente,
            int titolarioId) {
        int ufficioId = utente.getUfficioInUso();
        if (form.isDipTitolarioUfficio()) {
            Iterator i = form.getAssegnatari().iterator();
            while (i.hasNext()) {
                AssegnatarioView assegnatario = (AssegnatarioView) i.next();
                if (assegnatario.isCompetente()) {
                    ufficioId = assegnatario.getUfficioId();
                    break;
                }
            }
        }
        TitolarioBO.impostaTitolario(form, ufficioId, titolarioId);
    }

	
}
