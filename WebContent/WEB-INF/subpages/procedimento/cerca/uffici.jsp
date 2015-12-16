<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<html:xhtml />

<table summary="">

  <tr>
    <td>
      <span>Ufficio corrente : <html:hidden name="ricercaProcedimentoForm" property="ufficioCorrenteId" /></span>
    </td>
    <td>
    <logic:notEmpty name="ricercaProcedimentoForm" property="ufficioCorrentePath">
	    <bean:define id="ufficioCorrentePath" name="ricercaProcedimentoForm" property="ufficioCorrentePath"/>
			<span title='<bean:write name="ricercaProcedimentoForm" property="ufficioCorrentePath" />'> <strong>
				<bean:write name="ricercaProcedimentoForm" property="ufficioCorrente.description" />
			</strong></span>
	</logic:notEmpty>	
      <html:submit styleClass="button" property="ufficioPrecedenteAction" value=".." title="Vai al livello superiore" />
    </td>
  </tr>

<logic:notEmpty name="ricercaProcedimentoForm" property="ufficiDipendenti">
  <tr>
    <td>
      <label for="ufficioSelezionatoId"><bean:message key="procedimento.ufficio"/> :</label>
    </td>
    <td>
 	    <html:select name="ricercaProcedimentoForm" property="ufficioSelezionatoId">
			<logic:iterate id="ufficio" name="ricercaProcedimentoForm" property="ufficiDipendenti">
		        <option value='<bean:write name="ufficio" property="id"/>'>
		        <bean:write name="ufficio" property="description"/></option>
			</logic:iterate>
 	    </html:select>
      <html:submit styleClass="button" property="impostaUfficioAction" value="Vai" title="Imposta l'ufficio come corrente" />
    </td>
  </tr>
</logic:notEmpty>

</table>
