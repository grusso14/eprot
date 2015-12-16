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

package it.flosslab.mvc.presentation.action.amministrazione;

import it.finsiel.siged.mvc.business.LookupDelegate;
import it.flosslab.mvc.business.OggettarioDelegate;
import it.flosslab.mvc.presentation.actionform.amministrazione.OggettiActionForm;
import it.flosslab.mvc.vo.OggettoVO;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class OggettarioAction extends Action {

    static Logger logger = Logger.getLogger(OggettarioAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();
        ActionForward myforward = null;
		String myaction = mapping.getParameter();
		if ("".equalsIgnoreCase(myaction)) {
			myforward = mapping.findForward("failure");
		} else if ("NEW".equalsIgnoreCase(myaction)) {
			myforward = performNew(mapping, form, request, response);
		} else if ("DELETE".equalsIgnoreCase(myaction)) {
			myforward = performDelete(mapping, form, request, response);
		} else if ("ADD".equalsIgnoreCase(myaction)) {
			myforward = performAdd(mapping, form, request, response);
		}
        return myforward;

}

	private ActionForward performDelete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		OggettiActionForm oggettiForm = (OggettiActionForm)form;
		OggettoVO oggetto = new OggettoVO(oggettiForm.getId(), oggettiForm.getDescrizione());
		try {
			OggettarioDelegate.getInstance().deleteOggetto(oggetto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("success");
	}

	private ActionForward performAdd(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		OggettiActionForm oggettiForm = (OggettiActionForm)form;
		OggettoVO oggetto = new OggettoVO(null, oggettiForm.getDescrizione());
		try {
			OggettarioDelegate.getInstance().salvaOggetto(oggetto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapping.findForward("success");
	}

	private ActionForward performNew(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Collection oggetti = OggettarioDelegate.getInstance().getOggetti();
		request.setAttribute("oggettario",oggetti);
		return mapping.findForward("success");
	}
}