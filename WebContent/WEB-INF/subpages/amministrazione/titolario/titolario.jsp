<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<table summary="">
  <tr>
    <td class="label">
      <span><bean:message key="protocollo.argomento.argomentocorrente"/>:</span>
    </td>
    <td>
      <span><strong>
		<logic:notEmpty name="titolarioForm" property="titolario">
				<html:hidden name="titolarioForm" property="titolario.id" />
				<bean:write name="titolarioForm" property="pathDescrizioneTitolario" /> 
				<html:submit styleClass="button" property="titolarioPrecedenteAction" value=".." title="Imposta il titolario precedente come corrente" />
		</logic:notEmpty>

		<logic:empty name="titolarioForm" property="titolario">
		      <bean:message key="protocollo.argomento.root"/> 
		</logic:empty>
      </strong></span>
      <html:hidden property="titolarioPrecedenteId" />
    </td>
  </tr>

<logic:notEmpty name="titolarioForm" property="titolariFigli">
  <tr>
    <td class="label">
      <label for="titolarioSelezionatoId"><bean:message key="protocollo.argomento.argomento"/> :</label>
    </td>
    <td>
      <html:select property="titolarioSelezionatoId">
		<logic:iterate id="tit" name="titolarioForm" property="titolariFigli">
			<bean:define id="id" name="tit" property="id"/>
        	<option value="<bean:write name='tit' property='id'/>">
	        	<bean:write name='tit' property='codice'/> - 
	        	<bean:write name='tit' property='descrizione'/>
        	</option>
		</logic:iterate>
      </html:select>
	&nbsp;&nbsp;
      <html:submit styleClass="button" property="impostaTitolarioAction" value="Imposta" title="Imposta il titolario selezionato come corrente" />
    </td>
  </tr>

</logic:notEmpty> 
</table>
