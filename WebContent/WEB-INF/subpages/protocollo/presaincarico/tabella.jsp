<%@ page contentType="text/html;charset=iso-8859-1" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>

<html:xhtml />

<logic:notEmpty name="presaInCaricoForm"
	property="protocolliInCaricoCollection">
	<div>
	<display:table class="simple" width="100%"
		name="sessionScope.presaInCaricoForm.protocolliInCaricoCollection"
		requestURI="/page/protocollo/presaInCarico.do" export="false"
		sort="list" pagesize="10" id="row">
		<display:column title="">
			<input type="checkbox" name="protocolliSelezionati"
				value="<bean:write name='row' property='protocolloId'/>" />
		</display:column>
		<display:column title="N. protocollo">
			<html:link action="/page/protocollo/presaInCarico.do" paramName="row"
				paramId="protocolloSelezionato" paramProperty="protocolloId">
				<bean:write name="row" property="annoNumeroProtocollo" />
			</html:link>
		</display:column>
		<%--		<display:column property="annoRegistrazione" title="Anno" />--%>
		<display:column property="dataProtocollo" title="Registrato il" />
		<!-- MITTENTI modifica Daniele Sanna 15/09/2008 -->
				<display:column title="Mittente">
					<logic:notEqual name="row" property="tipoMittente" value="M">
						<bean:write name="row" property="mittente" />
					</logic:notEqual>
					<logic:equal name="row" property="tipoMittente" value="M">
						<ul>
							<logic:iterate id="mittente" name="row" property="mittenti">
								<li><bean:write name="mittente" property="nome"/>  &nbsp;<bean:write name="mittente" property="cognome"/></li>
							</logic:iterate>
						</ul>
					</logic:equal>
				</display:column>
				<!-- FINE MODIFICA -->
		<%--			<display:column title="PDF">
			</display:column>--%>
		<display:column title="Oggetto">
			<bean:write name="row" property="oggetto" />
		</display:column>
		<display:column title="Assegnatario" >
			<bean:write name="row" property="assegnatario"/>
		</display:column>			
		<display:column title="Assegnato da" property="assegnante" />

		<display:column title="DOC" >
		<html:link action="/page/protocollo/presaInCarico.do" 
			             paramId="downloadDocprotocolloSelezionato" 
			             paramName="row" 
			             paramProperty="protocolloId" 
			             target="_blank"
			             title="Download File">
		
				<bean:write name="row" property="pdf"/>
				 </html:link>
		</display:column>				
		<display:column title="Messaggio" property="messaggio" />
			
	</display:table>
	<p>
	<br /><br />
	<label for="messaggio" class="obbligatorio">Messaggio in caso di rifiuto :*</label>
		<html:text styleId="messaggio" property="msgAssegnatarioCompetente" size="60" maxlength="250"></html:text>
	<br /><br />
	
	<html:submit styleClass="submit" property="btnAccettaSelezionati"
		value="Accetta i selezionati" alt="Accetta i selezionati" />&nbsp;&nbsp;
	<html:submit styleClass="submit" property="btnRespingiSelezionati"
		value="Respingi i selezionati" alt="Respingi i selezionati" /></p>
	</div>
</logic:notEmpty>