<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<table summary="">
  <tr>
    <td class="label">
      <span><bean:message key="protocollo.titolario.argomento"/>:</span>
    </td>
    <td colspan="2">
      <span><strong>

<logic:notEmpty name="protocolloForm" property="titolario">
        <bean:write name="protocolloForm" property="titolario.descrizione"/>
</logic:notEmpty>        

      </strong></span>
      <html:hidden property="titolarioPrecedenteId" />

<logic:notEmpty name="protocolloForm" property="titolario">
      <html:submit styleClass="button" property="titolarioPrecedenteAction" value=".." title="Imposta il titolario precedente come corrente" />
</logic:notEmpty>      

    </td>
  </tr>
  

<logic:notEmpty name="protocolloForm" property="titolariFigli">
  <tr>
    <td class="label">
      <label for="titolarioSelezionatoId"><bean:message key="protocollo.titolario"/>:</label>
    </td>
    <td>
      <html:select property="titolarioSelezionatoId">
		<logic:iterate id="tit" name="protocolloForm" property="titolariFigli">
        	<option value='<bean:write name="tit" property="id" />'><bean:write name="tit" property="codice"/> - <bean:write name="tit" property="descrizione"/></option>
		</logic:iterate>
      </html:select>
    </td>
    <td>
      <html:submit styleClass="button" property="impostaTitolarioAction" value="Imposta" title="Imposta il titolario selezionato come corrente" />
    </td>
  </tr>

</logic:notEmpty>
</table>
