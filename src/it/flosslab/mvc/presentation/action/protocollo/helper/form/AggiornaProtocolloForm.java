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

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.Protocollo;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.util.DateUtil;

import javax.servlet.http.HttpSession;


public class AggiornaProtocolloForm {

	 /**
     * Aggiorna il form prendendo i dati dal model
     */
    public static void aggiorna(Protocollo protocollo, ProtocolloForm form,
            HttpSession session) {
        ProtocolloVO protocolloVO = protocollo.getProtocollo();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

        form.inizializzaForm();

        // dati generali
        aggiornaDatiGeneraliForm(protocollo.getProtocollo(), form);

        // protocolli allacciati
        aggiornaAllacciForm(protocollo, form);

        // documenti allegati
        aggiornaAllegatiForm(protocollo, form);

        // allegato principale
        form.setDocumentoPrincipale(protocollo.getDocumentoPrincipale());

        // annotazione
        aggiornaAnnotazioniForm(protocolloVO, form);

        // Fascicolo
        aggiornaFascicoloForm(protocolloVO, form, utente);

        // Procedimento
        aggiornaProcedimentoForm(protocollo, form, utente);
    }
    

    private static void aggiornaDatiGeneraliForm(ProtocolloVO protocollo,
            ProtocolloForm form) {
        Integer id = protocollo.getId();
        if (id != null) {
            form.setProtocolloId(id.intValue());
            form.setDataRegistrazione(DateUtil.formattaDataOra(protocollo
                    .getDataRegistrazione().getTime()));
            form.setUfficioProtocollatoreId(protocollo
                    .getUfficioProtocollatoreId());
            form.setUtenteProtocollatoreId(protocollo
                    .getUtenteProtocollatoreId());
            form.setStato(protocollo.getStatoProtocollo());
        } else {
            form.setProtocolloId(0);
            // form.setDataRegistrazione(DateUtil.formattaData(RegistroBO.getDataAperturaRegistro(registro).getTime()));
        }
        form.setAooId(protocollo.getAooId());
        form.setFlagTipo(protocollo.getFlagTipo());
        form.setNumero(protocollo.getNumProtocollo());
        form.setNumProtocolloEmergenza(protocollo.getNumProtocolloEmergenza());
        form.setStato(protocollo.getStatoProtocollo());
        form.setNumeroProtocollo(protocollo.getNumProtocollo() + "");

        /*
         * form.setNumeroProtocollo(protocollo.getNumProtocollo() + "/" +
         * protocollo.getAnnoRegistrazione());
         */
        form.setTipoDocumentoId(protocollo.getTipoDocumentoId());
        Date dataDoc = protocollo.getDataDocumento();
        form.setDataDocumento(dataDoc == null ? null : DateUtil
                .formattaData(dataDoc.getTime()));
        Date dataRic = protocollo.getDataRicezione();
        form.setDataRicezione(dataRic == null ? null : DateUtil
                .formattaDataOra(dataRic.getTime()));
        form.setRiservato(protocollo.isRiservato());
        form.setOggettoGenerico(protocollo.getOggetto());
        // annotazioni
        form.setChiaveAnnotazione(protocollo.getChiaveAnnotazione());
        form.setPosizioneAnnotazione(protocollo.getPosizioneAnnotazione());
        form.setDescrizioneAnnotazione(protocollo.getDescrizioneAnnotazione());
        // dati protocollo annullato
        Date dataAnnullamento = protocollo.getDataAnnullamento();
        form.setDataAnnullamento(dataAnnullamento == null ? null : DateUtil
                .formattaData(dataAnnullamento.getTime()));
        form.setProvvedimentoAnnullamento(protocollo
                .getProvvedimentoAnnullamento());
        form.setNotaAnnullamento(protocollo.getNotaAnnullamento());
        form.setVersione(protocollo.getVersione());
    }

    private static void aggiornaAllacciForm(Protocollo protocollo, ProtocolloForm form) {
        for (Iterator i = protocollo.getAllacci().iterator(); i.hasNext();) {
            AllaccioVO allaccio = (AllaccioVO) i.next();
            form.allacciaProtocollo(allaccio);
        }
    }

    private static void aggiornaAllegatiForm(Protocollo protocollo, ProtocolloForm form) {
        for (Iterator i = protocollo.getAllegati().values().iterator(); i
                .hasNext();) {
            DocumentoVO documentoVO = (DocumentoVO) i.next();
            form.allegaDocumento(documentoVO);
        }
    }

    private static void aggiornaAnnotazioniForm(ProtocolloVO protocollo,
            ProtocolloForm form) {
        form.setDescrizioneAnnotazione(protocollo.getDescrizioneAnnotazione());
        form.setPosizioneAnnotazione(protocollo.getPosizioneAnnotazione());
        form.setChiaveAnnotazione(protocollo.getChiaveAnnotazione());
    }

    private static void aggiornaFascicoloForm(ProtocolloVO protocollo,
            ProtocolloForm form, Utente utente) {
        if (protocollo != null && protocollo.getFascicoli() != null) {
            for (Iterator i = protocollo.getFascicoli().iterator(); i.hasNext();) {
                FascicoloVO fascicolo = (FascicoloVO) i.next();
                form.aggiungiFascicolo(fascicolo);
            }
        }
    }

    private static void aggiornaProcedimentoForm(Protocollo protocollo,
            ProtocolloForm form, Utente utente) {
        if (protocollo != null) {
            Collection procedimenti = protocollo.getProcedimenti();
            if (procedimenti != null) {
                for (Iterator i = procedimenti.iterator(); i.hasNext();) {
                    ProtocolloProcedimentoVO procedimento = (ProtocolloProcedimentoVO) i
                            .next();
                    form.aggiungiProcedimento(procedimento);
                }
            }
        }
    }
}
