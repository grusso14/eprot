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

package it.flosslab.mvc.presentation.action.protocollo.helper.model;

import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;

import java.util.Collection;
import java.util.Date;

public class AggiornaProtocolloIngressoModel extends AggiornaProtocolloModel {

	
	 /**
     * Aggiorna il model prendendo i dati dal form. Da invocare solo dopo la
     * validazione
     */

    public static void aggiorna(ProtocolloIngressoForm form,
            ProtocolloIngresso protocollo, Utente utente) {
    	AggiornaProtocolloModel.aggiorna(form, protocollo);

        protocollo.setMessaggioEmailId(form.getMessaggioEmailId());
        // mittente
        aggiornaMittenteModel(form, protocollo.getProtocollo());

        // assegnatari
        aggiornaAssegnatariModel(form, protocollo, utente);
    }
    
    private static void aggiornaMittenteModel(ProtocolloIngressoForm form,
            ProtocolloVO protocollo) {
        protocollo.setFlagTipoMittente(form.getMittente().getTipo());
        protocollo.setDataProtocolloMittente(form.getDataProtocolloMittente());
        protocollo.setNumProtocolloMittente(form.getNumProtocolloMittente());
        if (form.getMittente().getTipo().equals(ProtocolloVO.FLAG_TIPO_MITTENTE_MULTI)) {
        	protocollo.setMittenti(form.getMittenti());
        }else{
        	if (form.getMittente().getTipo().equals(ProtocolloVO.FLAG_TIPO_MITTENTE_PERS_FISICA)) {
                protocollo.setCognomeMittente(form.getMittente().getCognome());
                protocollo.setNomeMittente(form.getMittente().getNome());
            } else {
                protocollo.setDenominazioneMittente(form.getMittente().getDescrizioneDitta());
            }
            protocollo.setMittenteIndirizzo(form.getMittente().getIndirizzo().getToponimo());
            protocollo.setMittenteComune(form.getMittente().getIndirizzo().getComune());
            protocollo.setMittenteCap(form.getMittente().getIndirizzo().getCap());
            protocollo.setMittenteProvinciaId(form.getMittente().getIndirizzo().getProvinciaId());
        }
        
    }
    
    


    private static void aggiornaAssegnatariModel(ProtocolloIngressoForm form,
            ProtocolloIngresso protocollo, Utente utente) {
        protocollo.removeAssegnatari();
        // modifica daniele 07/07/2008
        protocollo.setMsgAssegnatarioCompetente(form
                .getMsgAssegnatarioCompetente());
        UtenteVO uteVO = utente.getValueObject();
        Collection<AssegnatarioView> assegnatari = form.getAssegnatari();
        if (assegnatari != null) {
            for (AssegnatarioView ass : assegnatari) {
                AssegnatarioVO assegnatario = new AssegnatarioVO();
                Date now = new Date();
                assegnatario.setDataAssegnazione(now);
                assegnatario.setDataOperazione(now);
                assegnatario.setRowCreatedUser(uteVO.getUsername());
                assegnatario.setRowUpdatedUser(uteVO.getUsername());
                assegnatario.setUfficioAssegnanteId(utente.getUfficioInUso());
                assegnatario.setUtenteAssegnanteId(uteVO.getId().intValue());
                assegnatario.setUfficioAssegnatarioId(ass.getUfficioId());
                assegnatario.setUtenteAssegnatarioId(ass.getUtenteId());
                // assegnatario.setStatoAssegnazione(ass.getStato());
                if (form.isCompetente(ass)) {
                    assegnatario.setCompetente(true);
                    // modifica daniele 07/07/2008
                    assegnatario.setMsgAssegnatarioCompetente(form
                            .getMsgAssegnatarioCompetente());
                    Organizzazione org = Organizzazione.getInstance();
                    UfficioVO uff = org.getUfficio(ass.getUfficioId())
                            .getValueObject();
                    if (ass.getStato() != 'S'
                            && protocollo.getProtocollo().getId().intValue() > 0) {
                        assegnatario.setStatoAssegnazione(ass.getStato());
                    } else if (form.isRiservato()
                            || (uff.isAccettazioneAutomatica() && ass
                                    .getUtenteId() > 0)) {
                        assegnatario.setStatoAssegnazione('A');
                        if (!protocollo.getProtocollo().getStatoProtocollo()
                                .equals("P")) {
                            protocollo.getProtocollo().setStatoProtocollo("N");
                        }
                    } else {
                        if (!protocollo.getProtocollo().getStatoProtocollo()
                                .equals("P")) {
                            protocollo.getProtocollo().setStatoProtocollo("S");
                        }
                    }
                }
                protocollo.aggiungiAssegnatario(assegnatario);
            }
        }
    }
}
