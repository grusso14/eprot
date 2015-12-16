<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<table>
  <tr>
    <th><label for="cognome"><bean:message key="soggetto.fisica.cognome"/>:</label></th>
    <th><label for="nome"><bean:message key="soggetto.fisica.nome"/>:</label></th>
    <th><label for="codiceFiscale"><bean:message key="soggetto.fisica.codicefiscale"/>:</label></th>
  </tr>
  <tr>
    <td><html:text property="cognome" styleId="cognome" size="30" maxlength="100" /></td>
    <td><html:text property="nome" styleId="nome" size="25" maxlength="40" /></td>
    <td><html:text property="codiceFiscale" styleId="codiceFiscale" size="16" maxlength="16"/></td>
    <td>
		<html:submit styleClass ="submit" property="cerca" value="Cerca" alt="Cerca" />
		<logic:equal name="personaFisicaForm" property="indietroVisibile" value="true" >
		<html:submit styleClass ="submit" property="indietroPF" value="Indietro" alt="Indietro" />
		</logic:equal>
	</td>
  </tr>
</table>



