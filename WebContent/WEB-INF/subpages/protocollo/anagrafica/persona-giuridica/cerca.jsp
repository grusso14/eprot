<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<div id="cerca">
<table>
<tr >
	<th><label for="descrizioneDitta"><bean:message key="soggetto.giuridica.denominazione" />:</label></th>
	<th><label for="partitaIva"><bean:message key="soggetto.giuridica.piva" />:</label> </th>
</tr> 
<tr >  
    <td>
	    <html:text property="descrizioneDitta" styleId="descrizioneDitta" size="30" maxlength="100" /> 
	</td>
	<td>  
      	<html:text property="partitaIva" styleId="partitaIva" size="16" maxlength="16"/>&nbsp;
    </td>
    <td>
		<html:submit styleClass ="submit" property="cerca" value="Cerca" alt="Cerca" />
		<logic:equal name="personaGiuridicaForm" property="indietroVisibile" value="true" >
		<html:submit styleClass ="submit" property="indietroPG" value="Indietro" alt="Indietro" />
		</logic:equal>
	</td>
</tr>
</table>
</div>

