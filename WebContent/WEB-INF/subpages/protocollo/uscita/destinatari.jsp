<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<html:errors bundle="bundleErroriProtocollo"
	property="destinatario_nome_obbligatorio" />
<html:errors bundle="bundleErroriProtocollo" property="formato_data" />
<logic:equal name="protocolloForm" property="protocolloId" value="0" >
<table summary="">
	<tr>
		<td class="label"><span><bean:message key="protocollo.mittente.tipo" />:</span></td>
		<td>
		<table summary="">
			<tr>
				<td><html:radio property="tipoDestinatario" styleId="personaFisica" value="F"
					onclick="document.forms[0].submit()">
					<label for="personaFisica"><bean:message
						key="protocollo.destinatario.personafisica" /></label>
				</html:radio></td>

				<td><html:radio property="tipoDestinatario" styleId="personaGiuridica" value="G"
					onclick="document.forms[0].submit()">
					<label for="personaGiuridica"><bean:message
						key="protocollo.destinatario.personagiuridica" /></label>
				</html:radio></td>

				<td><html:radio property="tipoDestinatario" styleId="destinatarioAoo" value="A"
					onclick="document.forms[0].submit()">
					<label for="destinatarioAoo"><bean:message
						key="protocollo.destinatario.aoo" /></label>
				</html:radio></td>
                   
<%--				<td><html:radio property="tipoDestinatario" styleId="destinatarioListaDistribuzione" value="L"-->
<!--					onclick="document.forms[0].submit()">-->
<!--					<label for="destinatarioListaDistribuzione"><bean:message-->
<!--						key="protocollo.destinatario.lista_distribuzione" /></label>-->
<!--				</html:radio></td>--%>
				
				<td><script type="text/javascript"></script>
				<noscript>
				<div><%--html:submit styleClass="button" value="Imposta" title="Imposta il tipo di Mittente"/--%>
				&nbsp;</div>
				</noscript>
				
				<html:submit styleClass="button" property="cercaDestinatari"
					value="Seleziona" title="Seleziona i destinatari della rubrica" />
				</td>
				<td>&nbsp;&nbsp;
				<html:submit styleClass="button" property="listaDistribuzione"
					value="Lista Distribuzione" title="Seleziona la lista distribuzione" />
			    </td>		
			</tr>
		</table>
		</td>
	</tr>
<logic:equal name="protocolloForm" property="tipoDestinatario" value= "F">	
<tr>
		<td class="label">
	<label for="nomeDestinatario"> 
			<bean:message key="protocollo.mittente.nome" /> </label> 
			<span class="obbligatorio">*</span> : </td>
	<td><html:text property="nomeDestinatario" styleId="nomeDestinatario" styleClass="obbligatorio"
			maxlength="160"></html:text></td>
	<tr><td class="label"><label for="cognomeDestinatario"> 
			<bean:message key="protocollo.mittente.cognome" /> </label> 
			<span class="obbligatorio">*</span> : </td>
	<td><html:text property="cognomeDestinatario" styleId="cognomeDestinatario" styleClass="obbligatorio"
			maxlength="160"></html:text></td></tr>
</logic:equal>
<logic:notEqual name="protocolloForm" property="tipoDestinatario" value= "F">	
			<tr>
				<td class="label">
					<label for="nominativoDestinatario"> 
					<bean:message key="protocollo.destinatario.nominativo" /> </label> 
					<span class="obbligatorio">*</span> : </td>
					<td><html:text property="nominativoDestinatario" styleId="nominativoDestinatario" styleClass="obbligatorio"
					maxlength="160"></html:text> &nbsp; 
				</td>
			</tr>
</logic:notEqual>
<html:hidden property="idx" /> 
			
	<tr>
		<td class="label"><label for="note"><bean:message
			key="protocollo.destinatario.note" />:</label></td>
		<td><html:textarea property="noteDestinatario" styleClass="" styleId="note" rows="1" cols="50"></html:textarea>
		</td>
	</tr>
	<tr>
		<td class="label"><label for="emailDestinatario"> <bean:message
			key="protocollo.destinatario.email" />: </label></td>
		<td><html:text property="emailDestinatario" styleId="emailDestinatario" ></html:text> &nbsp; 
		<label
			for="citta"> <bean:message key="protocollo.destinatario.citta" />: </label>
		<html:text property="citta" styleId="citta" ></html:text></td>

	</tr>
	<tr>
		<td class="label"><label for="indirizzoDestinatario"> <bean:message
			key="protocollo.destinatario.indirizzo" />: </label>
		</td>
		<td><html:text property="indirizzoDestinatario" styleId="indirizzoDestinatario" >
		</html:text> &nbsp; <label title="Codice di Avviamento Postale" for="capDestinatario">
		<bean:message key="soggetto.cap" />:</label> 
		<html:text property="capDestinatario" styleId="capDestinatario" size="6" maxlength="5" /> 
			
		</td>

	</tr>
	<tr>
		<td class="label"><label for="tipoSpedizione"> <bean:message
			key="protocollo.destinatario.tipospedizione" />: </label>
		</td>
		<td>
			<html:select property="mezzoSpedizioneId">
			    <option value="0" > </option>
				<html:optionsCollection property="mezziSpedizione" value="id" label="description" />
			</html:select> 
			<html:hidden property="destinatarioMezzoId" /> 
			
<%--			<logic:iterate id="curMezzo" property="mezziSpedizione" name="protocolloForm">-->
<!--			  <span>-->
<!--			  <html:multibox property="mezzoSpedizioneDestinatarioSelezionatiId" >-->
<!--			   <bean:write name="curMezzo" property="id"/> -->
<!--			  </html:multibox>-->
<!--			  <bean:write name="curMezzo" property="description"/> &nbsp; -->
<!--			  </span>-->
<!--			</logic:iterate> --%>
           
            &nbsp;<label title="Data spedizione" for="dataSpedizione"> <bean:message
			key="protocollo.destinatario.dataspedizione" />: </label> <html:text
			property="dataSpedizione" styleId="dataSpedizione" size="10" maxlength="10" /> 
			 <eprot:calendar textField="dataSpedizione" hasTime="false"/>
		
		
			<label title="Per conoscenza" for="flagConoscenza"> 
				<bean:message key="protocollo.destinatario.pc" />:
			</label> 
			<html:checkbox property="flagConoscenza"  styleId="flagConoscenza" ></html:checkbox>
		</td>

	</tr>
	<tr>
		<td class="label"><html:submit styleClass="button"
			property="aggiungiDestinatario" value="Aggiungi"
			title="Aggiunge il destinatario alla lista" /></td>
	</tr>
</table>

<logic:notEmpty name="protocolloForm" property="destinatari">
	<hr></hr>
	<table summary="" border="1" width="98%">
		<tr>
			<th></th>
			<th><span> <bean:message key="protocollo.destinatario.tipo" /> </span>
			</th>
			<th><span> <bean:message key="protocollo.destinatario.nominativo" />
			</span></th>
			<th><span> <bean:message key="protocollo.destinatario.indirizzo" /> </span>
			</th>
			<th><span> <bean:message key="protocollo.destinatario.email" /> </span>
			</th>
			<th><span> <bean:message key="protocollo.destinatario.tipospedizione" />
			</span></th>
			<th><span> <bean:message key="protocollo.destinatario.dataspedizione" />
			</span></th>
			<th><span> <bean:message key="protocollo.destinatario.pc" /> </span>
			</th>
			<th><bean:message key="protocollo.destinatario.rubrica" /></th>
		</tr>

		<logic:iterate id="currentRecord" name="protocolloForm"
			property="destinatari">
			<tr>

				<td><html:multibox property="destinatariSelezionatiId">
					<bean:write name="currentRecord" property="idx" />
				</html:multibox></td>

				<td><span> <bean:write name="currentRecord"
					property="flagTipoDestinatario" /> </span>
				</td>
				
				<td><span> <html:link action="/page/protocollo/uscita/documento.do"
					paramId="destinatarioId" paramName="currentRecord"
					paramProperty="idx">
					<bean:write name="currentRecord" property="destinatario" />
				</html:link> </span>
				</td>
				<td>
					<logic:notEmpty name="currentRecord" property="indirizzo">
						<span> 
							<bean:write name="currentRecord" property="indirizzo" /><br />
						</span>
					</logic:notEmpty>
					<logic:notEmpty name="currentRecord" property="capDestinatario">
						<span> 
							<bean:write name="currentRecord" property="capDestinatario" />&nbsp;
						</span>
					</logic:notEmpty>					
					<span> 
						<bean:write name="currentRecord" property="citta" /> 
					</span>
				</td>
				<td><span> <bean:write name="currentRecord" property="email" /> </span>
				</td>
				<td><span> <bean:write name="currentRecord" property="mezzoDesc" /><br>
				</span></td>
				<td><span> <bean:write name="currentRecord" property="dataSpedizione" /><br>
				</span></td>
				<td><span> <bean:write name="currentRecord" property="conoscenza" />
				</span></td>
				<logic:equal name="currentRecord" property="flagTipoDestinatario" value= "F">	
				<eprot:ifAuthorized permission="45">
					<logic:equal name="protocolloForm" property="protocolloId" value="0">
						<td><html:multibox property="destinatariToSaveId" alt="Registra in rubrica">
							<bean:write name="currentRecord" property="idx" />
						</html:multibox></td>
					</logic:equal>
				</eprot:ifAuthorized>
				</logic:equal>
				<logic:equal name="currentRecord" property="flagTipoDestinatario" value= "G">	
				<eprot:ifAuthorized permission="47">
					<logic:equal name="protocolloForm" property="protocolloId" value="0">
						<td><html:multibox property="destinatariToSaveId" alt="Registra in rubrica">
							<bean:write name="currentRecord" property="idx" />
						</html:multibox></td>
					</logic:equal>
				</eprot:ifAuthorized>
				</logic:equal>
			</tr>

		</logic:iterate>
	</table>
</logic:notEmpty>

<p><logic:notEmpty name="protocolloForm" property="destinatari">
	<html:submit styleClass="button" property="rimuoviDestinatari"
		value="Rimuovi"
		title="Rimuovi i destinatari selezionati dall'elenco dei destinatari" />
</logic:notEmpty></p>
</logic:equal>
<logic:notEqual name="protocolloForm" property="protocolloId" value="0" >
	<jsp:include page="/WEB-INF/subpages/protocollo/uscita/destinatariView.jsp" />
</logic:notEqual>
