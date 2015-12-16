<%@ page contentType="text/html;charset=iso-8859-1" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>

<html:xhtml />
<div>
	<logic:notEmpty name="scaricoForm" property="protocolliScaricoCollection">
		<display:table class="simple" width="100%" requestURI="/page/protocollo/scarico.do"
			name="sessionScope.scaricoForm.protocolliScaricoCollection"
			export="false" sort="list" pagesize="10" id="row">
			<display:column title="">
				<html:radio property="protocolloScarico" value="protocolloId" idName="row">
				</html:radio>
			</display:column>
			<display:column title="N. protocollo">
				<html:link  action="/page/protocollo/scarico.do" 
					paramId="protocolloSelezionato" paramProperty="protocolloId" paramName="row">
					<bean:write name="row" property="annoNumeroProtocollo" />
				</html:link>	
			</display:column>	
			<display:column property="dataProtocollo" title="Registrato il" />
			<!-- MITTENTI modifica Daniele Sanna 15/09/2008 -->
				<display:column title="Mittente">
					<logic:notEqual name="row" property="tipoMittente" value="M">
						<bean:write name="row" property="mittente" />
					</logic:notEqual>
					<logic:equal name="row" property="tipoMittente" value="M">
						<ul>
							<logic:iterate id="mittente" name="row" property="mittenti">
								<li><bean:write name="mittente" property="nome"/>  <bean:write name="mittente" property="cognome"/></li>
							</logic:iterate>
						</ul>
					</logic:equal>
				</display:column>
				<!-- FINE MODIFICA -->
			<display:column title="Oggetto" property="oggetto" />
			<display:column title="Assegnatario" >
				<bean:write name="row" property="ufficioAssegnatario"/>
				<bean:write name="row" property="utenteAssegnatario"/>
			</display:column>			

			<display:column property="dataScarico" title="Data scarico" />
			<display:column title="DOC" >
					  <html:link action="/page/protocollo/scarico.do" 
			             paramId="downloadDocprotocolloSelezionato" 
			             paramName="row" 
			             paramProperty="protocolloId" 
			             target="_blank"
			             title="Download File">
				<bean:write name="row" property="pdf"/>
				  </html:link>
			</display:column>				
		</display:table>
		<p>
		<logic:equal name="scaricoForm"	property="statoProtocollo" value="N">
		<label for="messaggio">Messaggio in caso di rifiuto :</label>
			<html:text styleId="messaggio" property="msgAssegnatarioCompetente" size="60" maxlength="250"></html:text>
		<br /><br />
		</logic:equal>
		<logic:notEqual name="scaricoForm" property="statoProtocollo" value="A">
			<html:submit styleClass="submit" property="btnAtti" value="agli Atti" alt="agli Atti" />
		</logic:notEqual> 
		<logic:notEqual name="scaricoForm" property="statoProtocollo" value="R">
			<html:submit styleClass="submit" property="btnRisposta" value="in Risposta" alt="in Risposta" />
		</logic:notEqual> 
		<logic:notEqual name="scaricoForm"	property="statoProtocollo" value="N">
			<html:submit styleClass="submit" property="btnLavorazione"	value="in Lavorazione" alt="in Lavorazione" />
		</logic:notEqual> 
	
		<logic:equal name="scaricoForm"	property="statoProtocollo" value="N">
			<html:submit styleClass="submit" property="btnRifiuta" value="Rifiuta" alt="Rifiuta" />
			<html:submit styleClass="submit" property="btnRiassegna" value="Riassegna"	alt="Riassegna il protocollo" />
		</logic:equal> 
		</p>
	</logic:notEmpty>
</div>

