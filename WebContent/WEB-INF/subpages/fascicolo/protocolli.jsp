<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html:xhtml />
<div>
<logic:notEmpty name="fascicoloForm" property="protocolliFascicolo">
	<display:table class="simple" width="95%" requestURI="/page/fascicolo.do"
		name="sessionScope.fascicoloForm.protocolliFascicolo"
		export="false" sort="list" pagesize="10" id="row">
				<display:column title="">
				    <input type="checkbox" name="protocolliSelezionati" value="<bean:write name='row' property='protocolloId'/>" />
				</display:column>
				<display:column title="Numero">
					<html:link action="/page/fascicoli.do" paramName="row" paramId="protocolloSelezionato" paramProperty="protocolloId">
						<bean:write name="row" property="annoNumeroProtocollo" />
					</html:link>
				</display:column>	
				<%-- display:column property="annoProtocollo" title="Anno" /  --%>
				<display:column property="tipoProtocollo" title="Tipo" />
				<display:column property="dataProtocollo" title="Registrato il" />
				<display:column property="mittente" title="Mittente" />
				<display:column property="oggetto" title="Oggetto" />
				<display:column title="Doc">
					  <html:link action="/page/fascicolo.do" 
			             paramId="downloadDocprotocolloSelezionato" 
			             paramName="row" 
			             paramProperty="protocolloId" 
			             title="Download File">		 	
   					<span><bean:write name="row" property="pdf" /></span>
  				  </html:link>
				</display:column>	
			
			
				<display:column property="statoProtocollo" title="Stato" />

	</display:table>
</logic:notEmpty>
<logic:empty name="fascicoloForm" property="protocolliFascicolo">
	<span><bean:message key="fascicolo.messaggio4"/></span>
</logic:empty>
</div>

<logic:equal name="fascicoloForm" property="modificabile" value="true">
<div>
<br class="hidden" />
<logic:equal name="fascicoloForm" property="versioneDefault" value="true">
	<logic:equal name="fascicoloForm" property="statoFascicolo" value="0">
		<html:submit styleClass="submit" property="btnAggiungiProtocolli" value="Aggiungi" title="Associa uno o più protocolli al fascicolo" />
		<logic:notEmpty name="fascicoloForm" property="protocolliFascicolo">
			<html:submit styleClass="submit" property="btnRimuoviProtocolli" value="Rimuovi" title="Rimuove i protocolli selezionati dal fascicolo" />
		</logic:notEmpty>	
	</logic:equal>
</logic:equal>
</div>
</logic:equal>
