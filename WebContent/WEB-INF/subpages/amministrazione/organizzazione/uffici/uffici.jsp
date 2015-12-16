<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>


<html:xhtml />
 
<table summary="">
  <tr>
    <td class="label">
      <span><bean:message key="amministrazione.organizzazione.uffici.ufficiocorrente"/> : <html:hidden property="ufficioCorrenteId" /></span>
    </td>
    <td>
	<logic:notEmpty name="ufficioForm" property="ufficioCorrentePath">
      <bean:define id="ufficioCorrentePath" name="ufficioForm" property="ufficioCorrentePath"/>
      <span title="<bean:write name='ufficioForm' property='ufficioCorrentePath'/>">
	      <strong>
	      	<bean:write name="ufficioForm" property="ufficioCorrentePath"/>
	      </strong>
      </span>
      <html:submit styleClass="button" property="ufficioPrecedenteAction" value=".." title="Vai al livello superiore" />
	</logic:notEmpty>
    </td>
  </tr>

<logic:notEmpty name="ufficioForm" property="ufficiDipendenti">
  <tr>
    <td class="label">
      <label for="ufficioSelezionatoId"><bean:message key="amministrazione.organizzazione.uffici.ufficio"/> :</label>
    </td>
    <td>
    <logic:iterate id="ufficio" name="ufficioForm" property="ufficiDipendenti">
			
			<html:radio property="ufficioSelezionatoId" idName="ufficio" value="id">
				<span><bean:write name="ufficio" property="description"/></span>
			</html:radio><br />
	</logic:iterate>

    </td>
  </tr>
</logic:notEmpty>

</table>

