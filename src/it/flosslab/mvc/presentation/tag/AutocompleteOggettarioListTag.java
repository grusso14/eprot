package it.flosslab.mvc.presentation.tag;

import java.util.ArrayList;
import java.util.Collection;

import it.flosslab.mvc.business.OggettarioDelegate;
import it.flosslab.mvc.vo.OggettoVO;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.io.CopyUtils;
import org.apache.commons.io.IOUtils;

public class AutocompleteOggettarioListTag extends TagSupport{
	
	
	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			
			
			OggettarioDelegate delegate = OggettarioDelegate.getInstance();
			ArrayList<OggettoVO> listaOggetti = (ArrayList<OggettoVO>)delegate.getOggetti();
			
			StringBuffer str = new StringBuffer();
			str.append("var data = [");
			if(null != listaOggetti){
				for(int i=0; i<listaOggetti.size();i++){;
					OggettoVO oggetto = listaOggetti.get(i);
					str.append('"'+oggetto.getDescrizione()+'"');
					if(i != listaOggetti.size()-1){
						str.append(",");
					}
					
				}
			}
			str.append("];");
			out.print(str);
			
		} catch (Throwable t) {
			throw new JspException("Errore inizializzazione tag", t);
		}
		return 0;
	}
	
    public int doEndTag() throws JspException {
        return 0;
    }	

}
