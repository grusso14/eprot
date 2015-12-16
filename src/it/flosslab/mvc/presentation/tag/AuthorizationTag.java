package it.flosslab.mvc.presentation.tag;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.MenuDelegate;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;



public class AuthorizationTag extends TagSupport{


	private static final long serialVersionUID = 976905070446336754L;

	public int doStartTag() throws JspException {
		HttpSession session = this.pageContext.getSession();
		try {
			boolean isAuthorized = false;
			Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
			
			MenuDelegate delegate = MenuDelegate.getInstance();
			int utenteId = utente.getValueObject().getId().intValue();
            int ufficioId = utente.getUfficioInUso();
            isAuthorized = delegate.getMenuDAO().isUserEnabled(utenteId, ufficioId, Integer.parseInt(_permission));
			
			if (isAuthorized) {
				return EVAL_BODY_INCLUDE;
			} else {
				return SKIP_BODY;
			}
		} catch (Throwable t) {
			throw new JspException("Errore inizializzazione tag", t);
		}
	}
	
	/**
	 * Restituisce il permesso richiesto.
	 * @return Il permesso richiesto.
	 */
	public String getPermission() {
		return _permission;
	}

	/**
	 * Setta il permesso richiesto.
	 * @param permission Il permesso richiesto.
	 */
	public void setPermission(String permission) {
		this._permission = permission;
	}
	
	private String _permission;
}
