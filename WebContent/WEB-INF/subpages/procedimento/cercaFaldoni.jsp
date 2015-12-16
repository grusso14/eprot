<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<table summary="Faldoni">
	<tr>
		<td>		
			<label for="cercaFaldoneOggetto"><bean:message key="procedimento.faldoni.oggetto"/>:</label>
			<html:text property="cercaFaldoneOggetto" styleId="cercaFaldoneOggetto" size="40" maxlength="100"></html:text>
		</td>
		<td>
			<html:submit styleClass="submit" property="btnCercaFaldoniDaProcedimento" value="Cerca" title="Cerca Faldoni"/>     
        </td>
	</tr>
</table>