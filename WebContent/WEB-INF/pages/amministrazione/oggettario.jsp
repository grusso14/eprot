<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot"%>

<html:xhtml />

<eprot:page title="Oggettario">
<div id="protocollo-errori">
<html:errors bundle="bundleErroriAmministrazione" />
</div>


<html:form action="/cambioPwd.do"></html:form>

<p></p>
<br/><br/>

<fieldset>
<legend><strong>Aggiungi oggetto</strong></legend>
<html:form action="/page/amministrazione/oggettarioAdd.do" >
<html:text property="descrizione"></html:text>
<html:submit></html:submit>
</html:form>
</fieldset>
<br><br>

<fieldset>
<legend><strong>Lista Oggetti</strong></legend>
<br>
<table class="listaOggetti">
<tr>
	<th>Nr</th>
	<th>Descrizione</th>
	<th></th>
</tr>
<logic:iterate id="oggetto" name="oggettario" indexId="index">
	<tr>
		<td><bean:write name="index"/></td>
		<td><bean:write property="descrizione" name="oggetto"/></td>
		<td><html:form action="/page/amministrazione/oggettarioDelete.do" >
			<html:submit value="Cancella"></html:submit>
			<html:hidden  property="descrizione" name="oggetto"></html:hidden>
			<html:hidden  property="id" name="oggetto"></html:hidden>
		</html:form></td>
	</tr>
</logic:iterate>
</table>
</fieldset>
</eprot:page>
