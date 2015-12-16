<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<html:xhtml />
  <tr>
    <td class="label">
    <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="1">
      <span ><bean:message key="fascicolo.ufficio" /> :<span class="obbligatorio"> * </span> <html:hidden property="ufficioCorrenteId" /></span>
     </logic:equal>
     <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="0">
     <span ><bean:message key="fascicolo.ufficio" /> :  <html:hidden property="ufficioCorrenteId" /></span>  
     </logic:equal>
    </td>
    <td>
      <bean:define id="ufficioCorrentePath" name="fascicoloForm" property="ufficioCorrentePath" />
      <bean:define id="ufficioCorrenteDes" name="fascicoloForm" property="ufficioCorrente.description" /> 
      
      <span title="<bean:write name='fascicoloForm' property='ufficioCorrentePath'/>" ><strong> <bean:write name='fascicoloForm' property='ufficioCorrente.description'/></strong></span>
     
            
     
      
      <html:submit styleClass="button" property="ufficioPrecedenteAction" value=".." title="Vai al livello superiore" />
    </td>
  </tr>


<logic:notEmpty name="fascicoloForm" property="ufficiDipendenti">
  <tr>
    <td class="label">
&nbsp;
    </td>
    <td>
        <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="1">
 	    <html:select property="ufficioSelezionatoId" styleClass="obbligatorio">		
		<logic:iterate id="ufficioD" name="fascicoloForm" property="ufficiDipendenti">
				<bean:define id="id" name="ufficioD" property="id" />
		        <option value="<bean:write name="ufficioD" property="id" />"><bean:write name="ufficioD" property="description"/></option>
		</logic:iterate>
 	    </html:select>
 	    </logic:equal>
 	    <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="0">
 	    <html:select property="ufficioSelezionatoId" >		
		<logic:iterate id="ufficioD" name="fascicoloForm" property="ufficiDipendenti">
				<bean:define id="idfa" name="ufficioD" property="id" />
		        <option value="<bean:write name="ufficioD" property="id" />"><bean:write name="ufficioD" property="description"/></option>
		</logic:iterate>
 	    </html:select>
 	    </logic:equal>
		<html:submit styleClass="button" property="impostaUfficioAction" value="Vai" title="Imposta l'ufficio come corrente" />
    </td>
    
  </tr>
</logic:notEmpty>  




