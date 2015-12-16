<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione uffici">
<div id="protocollo-errori">
  <html:errors bundle="bundleErroriAmministrazione" />
</div>

<html:form action="/page/amministrazione/organizzazione/ufficio.do">
<html:hidden property="id"/>
<table summary="">
	<tr>
    	<td class="label">
      		<span><bean:message key="amministrazione.organizzazione.uffici.ufficiopadre"/>: <html:hidden property="parentId" /></span>
    	</td>
    	<td>
      		<logic:notEqual name="ufficioForm" property="parentId" value="0">
	      		<bean:define id="ufficioPadre" name="ufficioForm" property="ufficioPadre" />
	      		<bean:define id="path" name="ufficioForm" property="ufficioPadre.path" />
	      		<span title="<bean:write name='ufficioForm' property='ufficioPadre.path' />">
	      		<strong>
					<bean:write name="ufficioPadre" property="valueObject.description"/>
	      		</strong></span>
	      	</logic:notEqual>	
	    </td>
	</tr>
	<tr>  
    	<td class="label">
    		<label for="description"><bean:message bundle="bundleMessaggiAmministrazione" key="descrizione"/>
      		<span class="obbligatorio"> * </span></label>:
    	</td>  
    	<td>
      		<html:text property="description" styleClass="obbligatorio" styleId="description" size="60" maxlength="254"></html:text>
    	</td>  
	</tr>
	<tr>  
    	<td class="label">
      		<label for="ufficioCentrale"><bean:message bundle="bundleMessaggiAmministrazione" key="tipoUfficio"/></label>: 
    	</td>      	
    	<td>
    		<logic:equal name="ufficioForm" property="parentId" value="0" >
				<html:hidden property="tipo" /><span><bean:message key="amministrazione.organizzazione.uffici.centrale"/></span>
			</logic:equal>	
    		<logic:greaterThan name="ufficioForm" property="parentId" value="0" >
				<html:select styleClass="obbligatorio" property="tipo">
					<html:optionsCollection name="tipiUfficio" value="codice" label="descrizione" />
				</html:select>
			</logic:greaterThan>
			<logic:equal name="ufficioForm" property="parentId" value="0" >
				<logic:equal name="ufficioForm" property="id" value="0" >
					<html:select styleClass="obbligatorio" property="tipo">
						<html:optionsCollection name="tipiUfficio" value="codice" label="descrizione" />
					</html:select>
				</logic:equal>
			</logic:equal>	
    		
    	</td>  
	</tr>
	<tr>  
    	<td class="label">
      		<label for="accettazioneAutomatica"><bean:message bundle="bundleMessaggiAmministrazione" key="accettazioneAutomatica"/></label>: 
    	</td>  
    	<td>
		  <html:checkbox property="accettazioneAutomatica" styleId="accettazioneAutomatica"></html:checkbox>
    	</td>  
	</tr>
	    	
	<tr>  
    	<td class="label">
      		<label for="attivo"><bean:message bundle="bundleMessaggiAmministrazione" key="attivo"/></label>: 
    	</td>  
    	<td>
    		<logic:equal name="ufficioForm" property="parentId" value="0">
			  <html:checkbox disabled="true" property="attivo" ></html:checkbox>
			</logic:equal>
			<logic:notEqual name="ufficioForm" property="parentId" value="0">
			  <html:checkbox disabled="false" property="attivo" styleId="attivo"></html:checkbox>
			</logic:notEqual>
			
    	</td>  
	</tr>
	
</table>
<logic:greaterThan name="ufficioForm" property="id" value="0" >
<hr></hr>

<logic:notEmpty name="ufficioForm" property="dipendentiUfficio" >
<p><span><strong>Utenti associati</strong></span></p>
<table summary="" border="1" cellpadding="2" cellspacing="2" width="100%">
	<tr>
    	<th><span><bean:message key="amministrazione.organizzazione.uffici.nominativo"/></span></th>
    	<th><span>referente</span></th>
	</tr>

	<logic:notEmpty name="ufficioForm" property="dipendentiUfficio" >
	<logic:iterate id="utente" name="ufficioForm" property="dipendentiUfficio" >
	<tr>
    	<td>
			<span>
				<bean:write name="utente" property="cognome" /> 
				<bean:write name="utente" property="nome" /> 
			</span>
    	</td>
  
    	<td>		
			<span>
		     	<html:multibox name="ufficioForm" property="referentiId" >
    				<bean:write name="utente" property="id"/>
		    	</html:multibox> 
			</span> 
    	</td>
	</tr>

	</logic:iterate>
	</logic:notEmpty>

</table>
</logic:notEmpty>

<logic:empty name="ufficioForm" property="dipendentiUfficio" >
	<p><span><strong><bean:message key="amministrazione.organizzazione.uffici.messaggio"/>.</strong></span></p>
</logic:empty>	



</logic:greaterThan>
<table>
<tr>
		<td></td>
		<td>		
			<html:submit styleClass="submit" property="btnSalva" value="Salva" title="Inserisce il nuovo utente"></html:submit>
			<html:submit styleClass="button" property="btnAnnulla" value="Annulla" title="Annulla l'operazione"></html:submit>
		</td>
</tr>	
</table>

</html:form>


</eprot:page>
