<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<table summary="">
<tr>
   <td>
      <span><bean:message key="protocollo.ricerca.assegnatario.ufficioCorrente"/>:</span>
   
		<html:select property="ufficioRicercaId">
			<logic:equal name="ricercaFascicoliForm" property="tuttiUffici" value="true">
	    	    <option value="0"><bean:message key="protocollo.ricerca.assegnatario.tutti"/></option>
	        </logic:equal>
	        <option value='<bean:write name="ricercaFascicoliForm" property="ufficioCorrenteId" />'>
				<bean:write name="ricercaFascicoliForm" property="ufficioCorrente.description" />
			</option>
	 	</html:select>
      	<html:submit styleClass="button" property="ufficioPrecedenteAction" value=".." title="Vai al livello superiore" />
    </td>
  </tr>
 
<logic:notEmpty name="ricercaFascicoliForm" property="ufficiDipendenti">
	<tr>  
	    <td>
	       <label for="ufficioSelezionatoId"><bean:message key="protocollo.ricerca.assegnatario.ufficio"/> :</label>
	   
	 	    <html:select property="ufficioSelezionatoId">
		        <option value="0"><bean:message key="protocollo.ricerca.assegnatario.tutti"/></option>
		        <bean:define id="ufficiDipendenti" name="ricercaFascicoliForm" property="ufficiDipendenti"/>
				<html:optionsCollection name="ufficiDipendenti" value="id" label="description" />
	 	    </html:select>
	      <html:submit styleClass="button" property="impostaUfficioAction" value="Vai" title="Imposta l'ufficio come corrente" />
	    </td>
	 </tr> 
</logic:notEmpty> 
<tr>
   <td> 
  <table summary="">
			<jsp:include page= "/WEB-INF/subpages/fascicolo/cerca/titolario.jsp"/>
</table>
 </td>
  </tr>
    
	<tr>
		<td>
			<label for="nome"><bean:message key="fascicolo.anno"/>:</label>
			<html:text property="anno" styleId="anno" size="4" maxlength="4"></html:text> &nbsp;
			<label for="nome"><bean:message key="fascicolo.progressivo"/>:</label>
			<html:text property="progressivo" styleId="progressivo" size="10" maxlength="10"></html:text>&nbsp;&nbsp;
			<logic:empty name="tornaProtocollo">
				<logic:empty name="tornaDocumento">
					<label for="stato"><bean:message key="fascicolo.stato"/>:</label>		
					<html:select property="stato">
				    <html:option value="-1">Tutti</html:option>
					<html:optionsCollection property="statiFascicolo" value="id" label="descrizione" />
				</html:select>
				</logic:empty>
			</logic:empty>
			
		</td>
	</tr>	
	<tr>
		<td>		
			<label for="oggettoFascicolo"><bean:message key="fascicolo.oggetto"/>:</label>
			<html:text property="oggettoFascicolo" styleId="oggettoFascicolo" size="40" maxlength="100"></html:text>
		</td>
	</tr>
	<tr>
		<td>	
			<label for="noteFascicolo"><bean:message key="fascicolo.note"/>:</label>
			<html:text property="noteFascicolo" styleId="noteFascicolo" size="44" maxlength="100"></html:text>
		</td>
	</tr>
	<tr>
	    <td>
			<span><bean:message key="fascicolo.data.apertura"/></span>
	        <label title="Data apertura minima" for="dataAperturaDa"><bean:message key="protocollo.da"/></label>:
			<html:text property="dataAperturaDa" styleId="dataAperturaDa" size="10" maxlength="10" />
			<eprot:calendar textField="dataAperturaDa" hasTime="false"/>
			&nbsp;
			<label title="Data apertura massima" for="dataAperturaA"><bean:message key="protocollo.a"/></label>:
			<html:text property="dataAperturaA" styleId="dataAperturaA" size="10" maxlength="10" />
			<eprot:calendar textField="dataAperturaA" hasTime="false"/>			    
		</td>
	</tr>
	<tr>
	    <td>
			<span><bean:message key="fascicolo.data.evidenza"/></span>
	        <label title="Data apertura minima" for="dataAperturaDa"><bean:message key="protocollo.da"/></label>:
			<html:text property="dataEvidenzaDa" styleId="dataEvidenzaDa" size="10" maxlength="10" />
			<eprot:calendar textField="dataEvidenzaDa" hasTime="false"/>
			&nbsp;
			<label title="Data apertura massima" for="dataEvidenzaA"><bean:message key="protocollo.a"/></label>:
			<html:text property="dataEvidenzaA" styleId="dataEvidenzaA" size="10" maxlength="10" />
			<eprot:calendar textField="dataEvidenzaA" hasTime="false"/>			    
		</td>
	</tr>

	<tr>
	    <td>
			<html:submit styleClass="submit" property="btnCercaFascicoli" value="Cerca" title="Cerca Fascicoli"/>     
			<html:reset styleClass="submit" property="ResetAction" value="Annulla" alt="Azzera i campi della ricerca" />
			<logic:equal name="tornaProtocollo" value="true">
				<html:submit styleClass="button" property="btnAnnulla" value="Indietro" title="Torna alla pagina precedente"/>     			
			</logic:equal>
			<logic:equal name="tornaDocumento" value="true">
				<html:submit styleClass="button" property="btnAnnulla" value="Indietro" title="Torna alla pagina precedente"/>     			
			</logic:equal>
			<logic:equal name="tornaFaldone" value="true">
				<html:submit styleClass="button" property="btnAnnulla" value="Indietro" title="Torna alla pagina precedente"/>     			
			</logic:equal>
			<logic:equal name="tornaProcedimento" value="true">
				<html:submit styleClass="button" property="btnAnnulla" value="Indietro" title="Torna alla pagina precedente"/>     			
			</logic:equal>
		</td>
	</tr>
</table>
