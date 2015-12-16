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

import java.util.Collection;
import java.util.Iterator;

import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloUscita;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloUscitaForm;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.presentation.helper.DestinatarioView;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;

public class AggiornaProtocolloUscitaModel extends AggiornaProtocolloModel {

	
	public static void aggiorna(ProtocolloUscitaForm form,
            ProtocolloUscita protocollo, Utente utente) {
		AggiornaProtocolloModel.aggiorna(form, protocollo);

        // mittente
        aggiornaMittenteModel(form, protocollo.getProtocollo());

        // aggiorna dati protocollo in risposta
        if (protocollo.getProtocollo().getId().intValue() == 0) {
            protocollo.getProtocollo().setRispostaId(form.getRispostaId());
            // protocollo.getProtocollo().setStatoTipoScarico(
            // form.getFlagStatoScarico());
            if (form.getRispostaId() == 0) {
                protocollo.getProtocollo().setMozione(true);
            }
        }

        // destinatari
        aggiornaDestinatariModel(form, protocollo, utente);

        protocollo.setFascicoloInvioId(form.getFascicoloInvioId());
        protocollo.setDocumentoInvioId(form.getDocumentoInvioId());
        protocollo.setDestinatariToSaveId(form.getDestinatariToSaveId());
    }
	


    private static void aggiornaMittenteModel(ProtocolloUscitaForm form,
            ProtocolloVO protocollo) {
        AssegnatarioView mittente = form.getMittente();
        if (mittente != null) {
            protocollo.setUfficioMittenteId(mittente.getUfficioId());
            protocollo.setUtenteMittenteId(mittente.getUtenteId());
            Organizzazione org = Organizzazione.getInstance();
            if (mittente.getUtenteId() == 0) {
                Ufficio uff = org.getUfficio(mittente.getUfficioId());
                protocollo.setDenominazioneMittente(uff.getValueObject()
                        .getDescription());
                protocollo.setFlagTipoMittente("G");
            } else {
                Utente ute = org.getUtente(mittente.getUtenteId());
                protocollo.setCognomeMittente(ute.getValueObject()
                        .getFullName());
                protocollo.setFlagTipoMittente("F");
            }
        }
    }
    
    
    private static void aggiornaDestinatariModel(ProtocolloUscitaForm form,
            ProtocolloUscita protocollo, Utente utente) {
        protocollo.removeDestinatari();
        Collection destinatari = form.getDestinatari();
        boolean spedito = false;
        if (destinatari != null) {
            for (Iterator i = destinatari.iterator(); i.hasNext();) {
                DestinatarioView dest = (DestinatarioView) i.next();
                DestinatarioVO destinatario = new DestinatarioVO();
                destinatario.setIdx(dest.getIdx());
                if("F".equals(dest.getFlagTipoDestinatario())){
                	destinatario.setNome(dest.getNome());
                	destinatario.setCognome(dest.getCognome());
                }else{
                	destinatario.setDestinatario(dest.getDestinatario());
                }
                destinatario.setFlagTipoDestinatario(dest
                        .getFlagTipoDestinatario());
                destinatario.setEmail(dest.getEmail());
                destinatario.setCodicePostale(dest.getCapDestinatario());

                if (dest.getIndirizzo() != null
                        && !(dest.getIndirizzo().equals(""))) {
                    destinatario.setIndirizzo(dest.getIndirizzo());

                }

                /*
                 * if (dest.getCapDestinatario() != null &&
                 * !(dest.getCapDestinatario().equals(""))) {
                 * destinatario.setIndirizzo(destinatario.getIndirizzo() + ' ' +
                 * dest.getCapDestinatario()); }
                 * 
                 * if (dest.getCitta() != null && !(dest.getCitta().equals(""))) {
                 * 
                 * destinatario.setIndirizzo(destinatario.getIndirizzo() + ' ' +
                 * dest.getCitta()); }
                 */

                if (dest.getCitta() != null) {
                    destinatario.setCitta(dest.getCitta());

                } else {
                    destinatario.setCitta("");
                }

                destinatario.setMezzoSpedizioneId(dest.getMezzoSpedizioneId());
                if (dest.getDataSpedizione() != null
                        && !"".equals(dest.getDataSpedizione())) {
                    spedito = true;
                }
                destinatario.setDataSpedizione(DateUtil.toDate(dest
                        .getDataSpedizione()));
                destinatario.setFlagConoscenza(dest.getFlagConoscenza());
                destinatario.setTitoloId(dest.getTitoloId());
                destinatario.setNote(dest.getNote());
                destinatario.setMezzoDesc(dest.getMezzoDesc());
                protocollo.addDestinatari(destinatario);
            }
        }
        if (spedito) {
            ProtocolloVO prot = protocollo.getProtocollo();
            if (prot.isMozione()) {
                protocollo.getProtocollo().setStatoProtocollo("A");
            } else {
                protocollo.getProtocollo().setStatoProtocollo("R");
            }
        } else {
            protocollo.getProtocollo().setStatoProtocollo("N");
        }
    }
}
