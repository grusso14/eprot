<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<script type="text/javascript"><!--
/*function copySelectValue(){
	var select = document.getElementById("selectOggetto");
	var selectedValue = select.options[select.selectedIndex].text;
	//alert(selectedValue);
	if (select.selectedIndex != 0) 
		document.getElementById("textareaOggettoGenerico").value = selectedValue;
	else
		document.getElementById("textareaOggettoGenerico").value = "";
}*/

$().ready(function(){
	<eprot:ListaOggettario />
	$("#textareaOggettoGenerico").autocomplete(data,{
			autoFill: true
			});

});//end ready

</script>

<div class="sezione"><span class="title"><strong><bean:message
	key="protocollo.documento" /></strong></span>
<table summary="">
	<tr>
		<td class="label">
			<label for="tipoDocumentoId">
				<bean:message key="protocollo.documento.tipo" />
			</label>
			<span class="obbligatorio"> * </span>:</td>
		<td>
		
		<logic:greaterThan name="protocolloForm" property="protocolloId" value="0">
			<html:select property="tipoDocumentoId" styleClass="obbligatorio" disabled="true">
				<html:optionsCollection property="tipiDocumento" value="id" label="descrizione" />
			</html:select>
		</logic:greaterThan> 
		
		<logic:equal name="protocolloForm" property="protocolloId" value="0">
			<html:select styleClass="obbligatorio" disabled="false" property="tipoDocumentoId">
				<html:optionsCollection property="tipiDocumento" value="id" label="descrizione" />
			</html:select>
		</logic:equal> &nbsp;&nbsp; 
		<label for="protocolloRiservato">
			<bean:message key="protocollo.mittente.riservato" />
		</label>&nbsp;: 
		
		<logic:greaterThan name="protocolloForm" property="protocolloId" value="0">
			<html:checkbox property="riservato" styleId="protocolloRiservato" disabled="false" />
		</logic:greaterThan> 
		
		<logic:equal name="protocolloForm" property="protocolloId" value="0">
			<html:checkbox property="riservato" styleId="protocolloRiservato" disabled="false" />
		</logic:equal></td>
	</tr>
	<tr>
		<td class="label"><label title="Data del documento"
			for="dataDocumento"><bean:message key="protocollo.documento.data" /></label>&nbsp;:
		</td>
		<td>
		<html:text styleClass="text" property="dataDocumento" styleId="dataDocumento" size="10" maxlength="10" /> 
		<eprot:calendar	textField="dataDocumento" hasTime="false"/>


		&nbsp;</td>

		<logic:equal name="protocolloForm" property="flagTipo" value="I">
			<td class="label"><label
				title="Data in cui il documento &egrave; stato ricevuto"
				for="dataRicezione"><bean:message
				key="protocollo.documento.ricevuto" /> </label>:</td>
			<td><html:text styleClass="text" property="dataRicezione" styleId="dataRicezione" size="14"	maxlength="10" /> 
			<eprot:calendar	textField="dataRicezione" hasTime="false"/>
			</td>
		</logic:equal>

	</tr>
	<%-- <tr>
		<td class="label">
			<label for="oggettoProtocollo">
				<bean:message key="protocollo.oggetto" />
			</label>&nbsp;<span class="obbligatorio">*</span>&nbsp;:
			
			<!--  <span><html:link action="/page/unicode.do?campo=oggetto" target="_blank" >segni diacritici</html:link></span>-->
		</td>
		
			
			<logic:equal name="protocolloForm" property="protocolloId" value="0">
			<td colspan="3">
			    <html:select styleClass="obbligatorio" disabled="false" property="oggetto" styleId="selectOggetto" onchange="copySelectValue();">
			    	<option value="">--Select--</option>
					<html:optionsCollection name="protocolloForm" property="oggettario" value="descrizione" label="descrizione" />	
				</html:select>
			</td>
		</tr> --%>
		
			<tr><td></td>
				<td id="oggetto">&nbsp;</td>
			<tr><td></td>				
				<td>
				<html:textarea property="oggettoGenerico" cols="30" styleId="textareaOggettoGenerico"/></td>
				<td>	
					<eprot:ifAuthorized permission="139">
						<logic:equal name="protocolloForm" property="protocolloId" value="0">
							<label for="oggettoToAdd"><strong>
								Aggiungi oggetto nell'oggettario
							</strong></label>&nbsp;: 
							<html:checkbox property="oggettoToAdd" styleId="oggettoToAdd" disabled="false" />
						</logic:equal>
					</eprot:ifAuthorized>
				</td>
			</tr>

	
	<tr>

		<logic:empty name="protocolloForm"
			property="documentoPrincipale.fileName">
			<td class="label"><label
				title="Path del file da allegare (deve essere di tipo PDF)"
				for="filePrincipaleUpload"><bean:message
				key="protocollo.documento.file" /></label>&nbsp;:</td>
			<td colspan="3"><html:file styleId="filePrincipaleUpload"
				property="filePrincipaleUpload" /> <html:submit styleClass="button"
				property="allegaDocumentoPrincipaleAction" value="Allega"
				title="Allega il file" /></td>
		</logic:empty>
		
		<logic:notEmpty name="protocolloForm"
			property="documentoPrincipale.fileName">
			<td class="label"><span title="File allegato"><bean:message
				key="protocollo.documento.file" /></span>&nbsp;:</td>
			<td colspan="3">
				<logic:equal name="protocolloForm" property="flagTipo" value="I">
					<html:link action="/page/protocollo/ingresso/documento.do"
						paramId="downloadDocumentoPrincipale" paramName="protocolloForm"
						paramProperty="documentoPrincipale.fileName" title="Download File">
						<span><strong> <bean:write name="protocolloForm"
							property="documentoPrincipale.fileName" /> </strong></span>
					</html:link>
				</logic:equal> 
			
				<logic:equal name="protocolloForm" property="flagTipo" value="U">
					<html:link action="/page/protocollo/uscita/documento.do"
						paramId="downloadDocumentoPrincipale" paramName="protocolloForm"
						paramProperty="documentoPrincipale.fileName" title="Download File">
						<span><strong> <bean:write name="protocolloForm"
							property="documentoPrincipale.fileName" />aaaaaaaa </strong></span>
					</html:link>
				</logic:equal> 
				
				<logic:empty name="protocolloForm" property="documentoPrincipale.id">
					<html:submit styleClass="button"
						property="rimuoviDocumentoPrincipaleAction" value="Rimuovi"
						title="Rimuove il documento" />
				</logic:empty></td>
		</logic:notEmpty>

	</tr>
</table>
</div>
