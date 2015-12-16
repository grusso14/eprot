<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot"%>

<html:xhtml />

<eprot:page title="Dashboard">
<div id="protocollo-errori">
<html:errors bundle="bundleErroriAmministrazione" />
</div>


<html:form action="/cambioPwd.do"></html:form>
<eprot:ifAuthorized permission="1">

<div align="center"><br><br><br>


<table border="0" cellpadding="0" cellspacing="0" bordercolor="#006666">
<tr>
	  <td><div align="left"><img src="./images/menu/dashboard.gif" width="309" height="30" border="0" /></div></td>
    <td>&nbsp;</td>
    <td><div align="right"></div></td>
  </tr>
   	


	<eprot:ifAuthorized permission="49">
   	<tr><td><span>Protocolli assegnati (Ufficio): </span></td>

		<td><bean:write name="assegnati_ufficio"/></td>
		<td> <html:link href="page/protocollo/dashboard/assegnati.do?type=ufficio">Visualizza</html:link></td>
	</tr>
	<tr><td><span>Protocolli assegnati (Utente): </span></td>
		<td><bean:write name="assegnati_utente"/></td>
		<td> <html:link href="page/protocollo/dashboard/assegnati.do?type=utente">Visualizza</html:link></td>
	</tr>
</eprot:ifAuthorized>
<eprot:ifAuthorized permission="50">
	<tr><td><span>Protocolli presi in carico:</span></td>
		<td><bean:write name="in_carico"/></td>
		<td> <html:link href="page/protocollo/dashboard/scarico.do?type=carico">Visualizza</html:link></td>
	</tr>
	<tr><td><span>Protocolli messi agli atti: </span></td>
		<td><bean:write name="agli_atti"/></td>
		<td> <html:link href="page/protocollo/dashboard/scarico.do?type=atti">Visualizza</html:link></td>
	</tr>
	<tr><td><span>Protocolli in risposta </span></td>
		<td><bean:write name="in_risposta"/></td>
		<td> <html:link href="page/protocollo/dashboard/scarico.do?type=risposta">Visualizza</html:link></td>
	</tr>
</eprot:ifAuthorized>
<eprot:ifAuthorized permission="60">
	<tr><td><span>Protocolli respinti: </span></td>
		<td><bean:write name="respinti"/></td>
		<td> <html:link href="page/protocollo/dashboard/respinti.do">Visualizza</html:link></td>
	</tr>
</eprot:ifAuthorized>
</table>
</div>
</eprot:ifAuthorized>

</eprot:page>
