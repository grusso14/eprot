package it.finsiel.siged.mvc.presentation.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class CalendarTag extends TagSupport {
    private String textField;
    private boolean hasTime;
    
    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public boolean isHasTime() {
		return hasTime;
	}

	public void setHasTime(boolean hasTime) {
		this.hasTime = hasTime;
	}

	public int doStartTag() throws JspException {
        JspWriter out = pageContext.getOut();
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

        try {
            out.print(" <img id='");
            out.print(getTextField());
            out.print("Button' alt='' src='");
            out.print(req.getContextPath());
            out.print("/images/calendar/calendar.png' ");
            if(isHasTime()){
            	out.println("title='Seleziona la data e l&#39; ora' />");
            }else{
            	out.println("title='Seleziona la data' />");
            }
            out.println("<script type='text/javascript'>");
            out.println("<!--");
            
            out.println("$().ready(function(){");
            out.println("$(\"#" + getTextField() + "\").dynDateTime({");
            if(isHasTime()){
            	out.println("showsTime: true,");
            	out.println("ifFormat: \"%d/%m/%Y - %H:%M\",");            	
            }else{
            	out.println("ifFormat: \"%d/%m/%Y\",");            	
            }
            
            out.println("align: \"TL\",");
            out.println("electric: true,");
            out.println("button: \".next()\"");
            out.println("});");
            out.println("});");
            
            out.println("// -->");
            out.println("</script>");
        } catch (IOException e) {
        }
        return 0;
    }

    public int doEndTag() throws JspException {
        return 0;
    }
}