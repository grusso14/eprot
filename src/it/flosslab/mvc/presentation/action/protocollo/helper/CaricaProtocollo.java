package it.flosslab.mvc.presentation.action.protocollo.helper;

import java.util.List;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.model.protocollo.ProtocolloUscita;
import it.finsiel.siged.mvc.bo.ProtocolloBO;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.StoriaProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.flosslab.mvc.presentation.action.protocollo.helper.form.AggiornaProtocolloIngressoForm;
import it.flosslab.mvc.presentation.action.protocollo.helper.form.AggiornaProtocolloUscitaForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CaricaProtocollo {

	public static final String PROTOCOLLO_USCITA = "Protocollo_uscita";
	public static final String PROTOCOLLO_INGRESSO = "Protocollo_ingresso";
	
	
	public static void caricaProtocolloIngresso(HttpServletRequest request, ProtocolloForm form){
		 HttpSession session = request.getSession();
	        Integer protocolloId = (Integer) request.getAttribute("protocolloId");
	        ProtocolloIngresso protocollo = (ProtocolloIngresso) request.getAttribute(Constants.PROTOCOLLO_INGRESSO_DA_EMAIL);

	        if (protocolloId != null || protocollo != null) {

	            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

	            if (protocollo != null) {
	                // protocollo ingresso da email
	                request.removeAttribute(Constants.PROTOCOLLO_INGRESSO_DA_EMAIL);
	                session.setAttribute("protocolloIngresso", protocollo);
	            } else if (protocolloId != null) {
	                // protocollo ingresso da ricerca
	                int id = protocolloId.intValue();
	                Integer versioneId = (Integer) request
	                        .getAttribute("versioneId");
	                if (versioneId == null) {
	                    protocollo = ProtocolloDelegate.getInstance().getProtocolloIngressoById(id);
	                    form.setVersioneDefault(true);
	                } else {
	                    int versione = versioneId.intValue();
	                    protocollo = StoriaProtocolloDelegate.getInstance()
	                            .getVersioneProtocolloIngresso(id, versione);
	                    form.setVersioneDefault(false);
	                }

	                if (protocollo == null) {
	                    protocollo = ProtocolloBO
	                            .getDefaultProtocolloIngresso(utente);
	                    session.removeAttribute("protocolloIngresso");
	                    AggiornaProtocolloIngressoForm.aggiorna(protocollo, form, session);
	                } else {
	                    session.setAttribute("protocolloIngresso", protocollo);
	                }
	            }
	            AggiornaProtocolloIngressoForm.aggiorna(protocollo, form, session);
	        }
	}
	
	
	public static void caricaProtocolloUscita(HttpServletRequest request, ProtocolloForm form){
	        HttpSession session = request.getSession(true);
	        Integer protocolloId = (Integer) request.getAttribute("protocolloId");
	        ProtocolloUscita protocollo = (ProtocolloUscita) request
	                .getAttribute(Constants.PROTOCOLLO_USCITA_ARCHIVIO);

	        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
	        if (protocolloId != null || protocollo != null) {

	            if (protocollo != null) {
	                // protocollo uscita da archivio
	                request.removeAttribute(Constants.PROTOCOLLO_USCITA_ARCHIVIO);

	            } else if (protocolloId != null) {

	                int id = protocolloId.intValue();
	                ProtocolloDelegate pd = ProtocolloDelegate.getInstance();
	                ProtocolloVO puVO = null;
	                Boolean risposta = (Boolean) request.getAttribute("risposta");
	                protocollo = null;
	                if (Boolean.TRUE.equals(risposta)) {
	                    protocollo = ProtocolloBO
	                            .getDefaultProtocolloUscita(utente);
	                    puVO = new ProtocolloVO();
	                    // seleziono il protocollo in ingresso
	                    ProtocolloVO piVO = pd.getProtocolloById(id);
	                    puVO.setAooId(piVO.getAooId());
	                    puVO.setOggetto(piVO.getOggetto());
	                    puVO.setFlagTipo(ProtocolloVO.FLAG_TIPO_PROTOCOLLO_USCITA);
	                    puVO.setTipoDocumentoId(piVO.getTipoDocumentoId());
	                    puVO.setMozione(false);
	                    // imposto l'id del protocollo in ingresso
	                    puVO.setRispostaId(id);
	                    puVO.setTitolarioId(piVO.getTitolarioId());
	                    // imposto il destinatario con i dati del mittente
	                    // protocollo in
	                    // ingressso
	                    DestinatarioVO destinatario = new DestinatarioVO();
	                    if ("F".equals(piVO.getFlagTipoMittente())) {
	                        destinatario.setFlagTipoDestinatario("F");
	                        if (piVO.getNomeMittente() != null) {
	                            destinatario.setDestinatario(piVO
	                                    .getCognomeMittente()
	                                    + " " + piVO.getNomeMittente());
	                        } else {
	                            destinatario.setDestinatario(piVO
	                                    .getCognomeMittente());
	                        }
	                        destinatario.setNome(piVO.getNomeMittente());
	                        destinatario.setCognome(piVO.getCognomeMittente());
	                        destinatario.setFlagConoscenza(false);
		                    destinatario.setDataSpedizione(null);

		                    protocollo.addDestinatari(destinatario);
	                    } else if ("M".equals(piVO.getFlagTipoMittente())) {
	                        List<SoggettoVO> mittenti = piVO.getMittenti();
	                        for(SoggettoVO mittente : mittenti){
	                        	destinatario = new DestinatarioVO();
	     	                    destinatario.setFlagTipoDestinatario("F");
	                        	if (mittente.getNome() != null) {
		                            destinatario.setDestinatario(mittente.getNome()
		                                    + " " + mittente.getCognome());
		                        } else {
		                            destinatario.setDestinatario(mittente.getCognome());
		                        }
		                        destinatario.setNome(mittente.getNome());
		                        destinatario.setCognome(mittente.getCognome());
		                        destinatario.setEmail(mittente.getIndirizzoEMail());
		                        destinatario.setIndirizzo(mittente.getIndirizzoCompleto());
		                        destinatario.setFlagConoscenza(false);
			                    destinatario.setDataSpedizione(null);

			                    protocollo.addDestinatari(destinatario);
	                        }
	                   
	                    } else {
	                        destinatario.setFlagTipoDestinatario("G");
	                        destinatario.setDestinatario(piVO
	                                .getDenominazioneMittente());
	                        destinatario.setFlagConoscenza(false);
		                    destinatario.setDataSpedizione(null);

		                    protocollo.addDestinatari(destinatario);
	                    }
	                    

	                    // imposto il mittente con i dati relativi all'assegnatario
	                    // per
	                    // competenza
	                    AssegnatarioVO assegnatario = pd
	                            .getAssegnatarioPerCompetenza(id);
	                    protocollo.setMittente(assegnatario);

	                    // imposto il protocollo in ingresso come allaccio
	                    AllaccioVO allaccioVo = new AllaccioVO();
	                    allaccioVo.setProtocolloAllacciatoId(id);
	                    allaccioVo.setAllaccioDescrizione(piVO.getNumProtocollo()
	                            + "/" + piVO.getAnnoRegistrazione() + " ("
	                            + piVO.getFlagTipo() + ")");
	                    protocollo.allacciaProtocollo(allaccioVo);
	                    protocollo.setProtocollo(puVO);

	                } else {
	                    protocollo = new ProtocolloUscita();
	                    // puVO = pd.getProtocolloById(id);
	                    // protocollo.setAllacci(pd.getAllacciProtocollo(id));
	                    // // protocollo.allegaDocumenti(pd.getAllegati());
	                    //
	                    // protocollo = pd.getProtocolloUscitaById(id);// perch� il
	                    // metodo
	                    // viene chiamato
	                    // due volte?
	                    Integer versioneId = (Integer) request
	                            .getAttribute("versioneId");
	                    request.removeAttribute("versioneId");
	                    if (versioneId == null) {
	                        protocollo = pd.getProtocolloUscitaById(id);// ???
	                        form.setVersioneDefault(true);
	                    } else {
	                        int versione = versioneId.intValue();
	                        protocollo = StoriaProtocolloDelegate.getInstance()
	                                .getVersioneProtocolloUscita(id, versione);
	                        form.setVersioneDefault(false);
	                    }

	                    if (protocollo == null) {
	                        protocollo = ProtocolloBO
	                                .getDefaultProtocolloUscita(utente);
	                        session.removeAttribute("protocolloUscita");
	                        // aggiornaForm(protocollo, form, session);
	                    } else {
	                        session.setAttribute("protocolloUscita", protocollo);
	                    }
	                }
	                // aggiornaForm(protocollo, form, session);
	            }
	            AggiornaProtocolloUscitaForm.aggiornaForm(protocollo, form, session);
	        }

	    }
}
