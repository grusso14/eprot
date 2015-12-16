<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
  <tr>
    <td class="label">
     <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="1">
      <span><bean:message key="fascicolo.livelli.titolario" /></span><span class="obbligatorio">*</span>:
    </logic:equal>
      <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="0">
      <span><bean:message key="fascicolo.livelli.titolario" /></span>:
     </logic:equal>   
    </td>
    
    <td colspan="2">
      <span><strong>
		<logic:notEmpty name="fascicoloForm" property="titolario">
		        <bean:write name="fascicoloForm" property="titolario.descrizione"/>
		</logic:notEmpty>        
      </strong></span>
       <html:hidden property="titolarioPrecedenteId" />
      	<logic:notEmpty name="fascicoloForm" property="titolario">
		      <html:submit styleClass="button" property="titolarioPrecedenteAction" value=".." title="Imposta il titolario precedente come corrente" />
		</logic:notEmpty> 
    </td>
  </tr>
  <logic:notEmpty name="fascicoloForm" property="titolariFigli">
  <tr>
    <td class="label">
&nbsp;
    </td>
    <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="1">
    <td>
      <html:select property="titolarioSelezionatoId" styleClass="obbligatorio">
		<logic:iterate id="tit" name="fascicoloForm" property="titolariFigli">
			<bean:define id="id" name="tit" property="id"/>
        	<option value="<bean:write name="tit" property="id"/>">
        		<bean:write name="tit" property="codice" /> - <bean:write name="tit" property="descrizione"/>
        	</option>
		</logic:iterate>	
      </html:select>
&nbsp;
      <html:submit styleClass="button" property="impostaTitolarioAction" value="Imposta" title="Imposta il titolario selezionato come corrente" />
      
    </td>
    </logic:equal>  
      <logic:equal name="fascicoloForm" property="dipendenzaTitolarioUfficio" value="0">
    <td>
     <html:select property="titolarioSelezionatoId" >
    <logic:iterate id="tit" name="fascicoloForm" property="titolariFigli">
			<bean:define id="id1" name="tit" property="id"/>
        	<option value="<bean:write name="tit" property="id"/>">
        		<bean:write name="tit" property="codice" /> - <bean:write name="tit" property="descrizione"/>
        	</option>
		</logic:iterate>	
      </html:select>
&nbsp;
      <html:submit styleClass="button" property="impostaTitolarioAction" value="Imposta" title="Imposta il titolario selezionato come corrente" />
    </td>
     </logic:equal>  
  </tr>
</logic:notEmpty>  
