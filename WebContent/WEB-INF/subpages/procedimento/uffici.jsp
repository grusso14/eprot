<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<html:xhtml />

<table summary="">

  <tr>
    <td class="label">
      <span><bean:message key="procedimento.ufficiocorrente" /> <span class="obbligatorio"> * </span> : <html:hidden property="ufficioCorrenteId" /></span>
    </td>
    <td>
    <logic:notEmpty name="procedimentoForm" property="ufficioCorrentePath">
	    <bean:define id="ufficioCorrentePath" name="procedimentoForm" property="ufficioCorrentePath"/>
			<span title='<bean:write name="procedimentoForm" property="ufficioCorrentePath" />'> <strong>
				<bean:write name="procedimentoForm" property="ufficioCorrente.description" />
			</strong></span>
	</logic:notEmpty>	
      <html:submit styleClass="button" property="ufficioPrecedenteAction" value=".." title="Vai al livello superiore" />
    </td>
  </tr>

<logic:notEmpty name="procedimentoForm" property="ufficiDipendenti">
  <tr>
    <td class="label">
      <label for="ufficioSelezionatoId"><bean:message key="procedimento.ufficio" /> <span class="obbligatorio"> * </span> :</label>
    </td>
    <td>
 	    <html:select property="ufficioSelezionatoId" styleClass="obbligatorio" styleId="ufficioSelezionatoId">
			<logic:iterate id="ufficio" name="procedimentoForm" property="ufficiDipendenti">
		        <option value='<bean:write name="ufficio" property="id"/>'>
		        <bean:write name="ufficio" property="description"/></option>
			</logic:iterate>
 	    </html:select>
      <html:submit styleClass="button" property="impostaUfficioAction" value="Vai" title="Imposta l'ufficio come corrente" />
    </td>
  </tr>
</logic:notEmpty>

</table>
