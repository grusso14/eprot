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

package it.flosslab.mvc.presentation.action.anagrafica;

import it.finsiel.siged.mvc.business.SoggettoDelegate;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.flosslab.mvc.business.OggettarioDelegate;
import it.flosslab.mvc.presentation.actionform.amministrazione.OggettiActionForm;
import it.flosslab.mvc.vo.OggettoVO;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AnagraficaAction extends Action {

    static Logger logger = Logger.getLogger(AnagraficaAction.class.getName());
    
    private static final String PERSONA_FISICA = "F";
    private static final String PERSONA_GIURIDICA = "G";
    
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward myforward = null;
		String myaction = mapping.getParameter();
		if ("".equalsIgnoreCase(myaction)) {
			myforward = mapping.findForward("failure");
		} else if ("SEARCH_FISICA".equalsIgnoreCase(myaction)) {
			myforward = performSearch(mapping, form, request, response, PERSONA_FISICA);
		} else if ("SEARCH_GIURIDICA".equalsIgnoreCase(myaction)) {
			myforward = performSearch(mapping, form, request, response, PERSONA_GIURIDICA);
		} 
        return myforward;

}

	private ActionForward performSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response, String tipo) {
		SoggettoDelegate soggettoDelegate = SoggettoDelegate.getInstance();
		List<SoggettoVO> soggetti = new ArrayList<SoggettoVO>();
		if("F".equals(tipo)) {
			String prefixSurname = request.getParameter("q").toLowerCase();
			soggetti = soggettoDelegate.getListaPersonaFisica(1, prefixSurname, "", "");
		}
		if("G".equals(tipo)) {
			String denominazione = request.getParameter("q").toLowerCase();
			soggetti = soggettoDelegate.getListaPersonaGiuridica(1, denominazione, "");
		}
			try {
				OutputStream out = response.getOutputStream();
				response.setContentType("text/plain");
				IOUtils.write(this.createRenderedList(soggetti, tipo), out);
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	
	}

	private String createRenderedList(List<SoggettoVO> soggetti, String tipo) {
		StringBuffer result = new StringBuffer("");
		if("F".equals(tipo)) {
			for(SoggettoVO soggetto : soggetti){
				//result.append(soggetto.toString() + "|" + soggetto.getId());
				result.append(soggetto.toString() +"|" + soggetto.getNome() + "|" + soggetto.getCognome() + "|");
				result.append(soggetto.getIndirizzo().getToponimo() + ", " + soggetto.getIndirizzo().getCivico() + "|");
				result.append(soggetto.getIndirizzo().getComune() + "|" + soggetto.getIndirizzo().getCap() + "|" + soggetto.getIndirizzo().getProvinciaId());
				result.append("\n");
			}
		} else if("G".equals(tipo)) {
			for(SoggettoVO soggetto : soggetti){
				//result.append(soggetto.toString() + "|" + soggetto.getId());
				result.append(soggetto.getDescrizioneDitta() +"|" + soggetto.getDescrizioneDitta() + "|");
				result.append(soggetto.getIndirizzo().getToponimo() + ", " + soggetto.getIndirizzo().getCivico() + "|");
				result.append(soggetto.getIndirizzo().getComune() + "|" + soggetto.getIndirizzo().getCap() + "|" + soggetto.getIndirizzo().getProvinciaId());
				result.append("\n");
			}
		}
		
		return result.toString();
	}
}