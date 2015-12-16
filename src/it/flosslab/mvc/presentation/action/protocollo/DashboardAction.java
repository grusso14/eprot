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

package it.flosslab.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.PresaInCaricoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolliRespintiForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ScaricoForm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class DashboardAction extends Action {

    static Logger logger = Logger.getLogger(DashboardAction.class.getName());
    
    private static final String PROTOCOLLI_ASSEGNATI_UTENTE = "assegnati_utente";
    private static final String PROTOCOLLI_ASSEGNATI_UFFICIO = "assegnati_ufficio";
    private static final String PROTOCOLLO_IN_CARICO = "in_carico";
    private static final String PROTOCOLLI_AGLI_ATTI = "agli_atti";
    private static final String PROTOCOLLI_RESPINTI = "respinti";
    private static final String PROTOCOLLI_IN_RISPOSTA = "in_risposta";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        ActionForward myforward = null;
		String myaction = mapping.getParameter();
		if ("".equalsIgnoreCase(myaction)) {
			myforward = mapping.findForward("failure");
		} else if ("SHOW".equalsIgnoreCase(myaction)) {
			myforward = performShow(mapping, form, request, response);
		} else if ("RESPINTI".equalsIgnoreCase(myaction)) {
			myforward = performRespinti(mapping, form, request, response);
		} else if ("SCARICO".equalsIgnoreCase(myaction)) {
			myforward = performScaricoView(mapping, form, request, response);
		} else if ("ASSEGNATI".equalsIgnoreCase(myaction)) {
			myforward = performAssegnatiView(mapping, form, request, response);
		} 
        return myforward;

}


	private ActionForward performAssegnatiView(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
		String type = request.getParameter("type");
		Map protocolli = new HashMap();
		Utente utente = (Utente)request.getSession().getAttribute(Constants.UTENTE_KEY);
		GregorianCalendar today = new GregorianCalendar();
		if(type.equals("utente")){
			protocolli = delegate.getProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "S", "S", "T");
			
		}else if(type.equals("ufficio")){
			protocolli = delegate.getProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "S", "S", "U");
			
		}
		PresaInCaricoForm caricoForm = (PresaInCaricoForm)form;
		caricoForm.setProtocolliInCarico(protocolli);
		return mapping.findForward("success");
	}


	private ActionForward performScaricoView(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
		String type = request.getParameter("type");
		Map protocolli = new HashMap();
		Utente utente = (Utente)request.getSession().getAttribute(Constants.UTENTE_KEY);
		GregorianCalendar today = new GregorianCalendar();
		ScaricoForm scaricoForm = (ScaricoForm)form;
		
		if(type.equals("risposta")){
			protocolli = delegate.getProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "A", "R", "T");
			scaricoForm.setStatoProtocollo("R");
			scaricoForm.setProtocolloScarico("R");
		}else if(type.equals("atti")){
			scaricoForm.setStatoProtocollo("A");
			protocolli = delegate.getProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "A", "A", "T");
		}else if(type.equals("carico")){
			scaricoForm.setStatoProtocollo("N");
			protocolli = delegate.getProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "A", "N", "T");
			
		}
		scaricoForm.setProtocolliScarico(protocolli);
		return mapping.findForward("success");
	}

	private ActionForward performRespinti(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
		Utente utente = (Utente)request.getSession().getAttribute(Constants.UTENTE_KEY);
		GregorianCalendar today = new GregorianCalendar();
		Map respinti = delegate.getProtocolliRespinti(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null);
		ProtocolliRespintiForm riassegnazione = (ProtocolliRespintiForm)form;
		riassegnazione.setProtocolliRifiutati(respinti);
		return mapping.findForward("success");
	}


	private ActionForward performShow(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
		Utente utente = (Utente)request.getSession().getAttribute(Constants.UTENTE_KEY);
		GregorianCalendar today = new GregorianCalendar();
		int assegnati_utente = delegate.contaProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "S", "S", "T");
		int assegnati_ufficio = delegate.contaProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "S", "S", "U");
		int in_carico = delegate.contaProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "A", "N", "T");
		int agli_atti = delegate.contaProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "A", "A", "T");
		int respinti = delegate.contaProtocolliRespinti(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null);
		int in_risposta = delegate.contaProtocolliAssegnati(utente,today.get(Calendar.YEAR) , today.get(Calendar.YEAR), 0, 0, null	, null, "A", "R", "T");
		request.setAttribute(PROTOCOLLI_ASSEGNATI_UTENTE,Integer.toString(assegnati_utente));
		request.setAttribute(PROTOCOLLI_ASSEGNATI_UFFICIO, Integer.toString(assegnati_ufficio));
		request.setAttribute(PROTOCOLLO_IN_CARICO,Integer.toString(in_carico));
		request.setAttribute(PROTOCOLLI_AGLI_ATTI,Integer.toString(agli_atti));
		request.setAttribute(PROTOCOLLI_RESPINTI,Integer.toString(respinti));
		request.setAttribute(PROTOCOLLI_IN_RISPOSTA,Integer.toString(in_risposta));
		return mapping.findForward("success");
	}
}