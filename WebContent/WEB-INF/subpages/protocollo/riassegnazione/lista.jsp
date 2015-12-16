<%@ page contentType="text/html;charset=iso-8859-1" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>

<html:xhtml />

<div><logic:notEmpty property="protocolliRifiutatiCollection"
	name="respintiForm">
	<display:table class="simple" width="100%"
		name="sessionScope.respintiForm.protocolliRifiutatiCollection"
		requestURI="/page/protocollo/ingresso/respinti.do" export="false"
		sort="list" pagesize="10" id="row">
		<display:column title="">
			<html:radio property="protocolloRifiutato" value="protocolloId" idName="row" ></html:radio>
		</display:column>
		<display:column title="N. protocollo"> 
			<html:link action="/page/protocollo/ingresso/respinti.do" paramId="protocolloSelezionato"  
				paramName="row" paramProperty="protocolloId">
				<bean:write name="row" property="annoNumeroProtocollo" />
			</html:link> 
		</display:column>			
<%--		<display:column property="annoProtocollo" title="Anno" />--%>
		<display:column property="dataProtocollo" title="Registrato il"
			sortable="true" group="2" headerClass="sortable" />
		<display:column property="mittente" title="Mittente" />
		<display:column property="oggetto" title="Oggetto" />
		<display:column property="assegnatario" title="Rifiutato da" />
		<display:column title="DOC" >
					  <html:link action="/page/protocollo/ingresso/respinti.do" 
			             paramId="downloadDocprotocolloSelezionato" 
			             paramName="row" 
			             paramProperty="protocolloId" 
			             target="_blank"
			             title="Download File">
				<bean:write name="row" property="pdf"/>
				  </html:link>
			</display:column>		
		
		<display:column property="messaggio" title="Messaggio" />
	</display:table>
	<p><html:submit styleClass="submit" property="btnRiassegna"
		value="Riassegna" alt="Riassegna il protocollo selezionato" />
		<html:submit styleClass="button" property="btnAnnulla" value="Annulla"
		alt="Annulla" /></p>
</logic:notEmpty></div>

