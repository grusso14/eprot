<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<table summary="">
	<tr>
		<td>
			<label for="nomeFascicolo"><bean:message key="protocollo.fascicolo.oggettofascicolo"/>:</label>
			<html:hidden property="fascicolo.id" />
<%--			<html:text property="fascicolo.nome" styleId="nomeFascicolo" size="50" maxlength="100"></html:text>&nbsp;--%>
			<html:text property="cercaFascicoloNome" styleId="cercaFascicoloNome" size="50" maxlength="100"></html:text>&nbsp;
			<html:submit styleClass="submit" property="btnCercaFascicoli" value="Cerca" title="Cerca Fascicoli"/>
			<html:submit styleClass="submit" property="btnNuovoFascicolo" value="Nuovo" title="Nuovo Fascicolo"/>
		</td>
	</tr>
	<logic:notEmpty name="protocolloForm" property="fascicoliProtocollo">
	<tr>
		<td>
			<label for="nomeFascicolo"><bean:message key="protocollo.fascicolo.fascicoloassociato"/>:</label>
		</td>
	</tr>
	<tr>		
		<td>
		<logic:iterate id="currentRecord" property="fascicoliProtocollo" name="protocolloForm">
		<html:multibox property="fascicoloSelezionatoId"><bean:write name="currentRecord" property="id"/></html:multibox>
		<span>
		
		<logic:equal  name="currentRecord" property="progressivo" value="0">
			<bean:write name="currentRecord" property="annoRiferimento"/>&lt;Nuovo&gt;
		</logic:equal>
		<logic:greaterThan name="currentRecord" property="progressivo" value="0">
			<html:link action="/page/protocollo/ingresso/documento.do" paramName="currentRecord" paramId="fascicoloId" paramProperty="id">
			<bean:write name="currentRecord" property="annoRiferimento"/><bean:write name="currentRecord" property="progressivo" format="0000000"/>
			</html:link>
		</logic:greaterThan>
		<logic:notEmpty name="currentRecord" property="oggetto">
		 - <bean:write name="currentRecord" property="oggetto"/>
		</logic:notEmpty>
		</span>
		<br/>
		</logic:iterate>
		<html:submit styleClass="submit" property="rimuoviFascicoli" value="Rimuovi" title="Rimuovi i fascicoli selezionati"/>
		</td>
	</tr>
	</logic:notEmpty>
</table>

