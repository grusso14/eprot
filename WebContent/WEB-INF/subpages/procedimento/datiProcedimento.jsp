<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<div><jsp:include
	page="/WEB-INF/subpages/procedimento/uffici.jsp"/></div>
<div><jsp:include
	page="/WEB-INF/subpages/procedimento/titolario.jsp"/></div>	
<table summary="">
  	<tr>
		<td class="label"><label title="Data Avvio" for="dataAvvio"><bean:message
			key="procedimento.data.avvio" /></label>&nbsp;<span
			class="obbligatorio">*</span>&nbsp;:</td>
		<td colspan="2"><html:text styleClass="obbligatorio" styleId="dataAvvio" property="dataAvvio"
			name="procedimentoForm" size="10" maxlength="10" /> 
		 <eprot:calendar textField="dataAvvio" hasTime="false"/>	
		&nbsp;</td>
	</tr>	
    <tr>
		<td class="label"><label for="tipoProcedimento"> <bean:message
			key="procedimento.tipo_procedimento" />&nbsp;<span
			class="obbligatorio">*</span>&nbsp;: </label></td>
		<td colspan="2">
		<html:select styleClass="obbligatorio" styleId="tipoProcedimento" name="procedimentoForm" property="tipoProcedimentoId">
			<html:optionsCollection name="procedimentoForm"
				property="tipiProcedimento" value="idTipo" label="descrizione" />
		</html:select></td>
	</tr>
	<tr>
		<td class="label"><label for="responsabile"> <bean:message
			key="procedimento.responsabile" />&nbsp;: </label></td>
		<td colspan="2"><html:text name="procedimentoForm" styleId="responsabile"
			property="responsabile" size="30" maxlength="200">
		</html:text></td>
	</tr>	
	<tr>
		<td class="label"><label for="referenteId"><bean:message key="procedimento.referente" /></label>&nbsp;&nbsp;:
		</td>
		<td colspan="2">
		<html:select name="procedimentoForm" property="referenteId">
			<html:option value=""></html:option>
			<html:optionsCollection name="procedimentoForm" property="referentiCollection" value="id" label="fullName" />
		</html:select></td>
	</tr>
	<tr>
		<td class="label"><label for="statoId"> <bean:message
			key="procedimento.stato" />&nbsp;: </label></td>
		<td colspan="2"><html:select name="procedimentoForm"
			property="statoId">
			<html:optionsCollection name="procedimentoForm"
				property="statiProcedimentoCollection" value="id"
				label="description" />
		</html:select></td>
	</tr>
	
	
	
	<tr>
		<td class="label"><label for="posizione"> <bean:message
			key="procedimento.posizione" />&nbsp;: </label></td>
			
		
		<td colspan="2"><html:select name="procedimentoForm"
			property="posizione">
			<html:optionsCollection name="procedimentoForm"
				property="posizioniProcedimentoCollection" value="codice"
				label="description" />
		</html:select>
 	    <html:submit styleClass="button" property="impostaPosizioneAction" value="Vai" title="Imposta la posizione" />	
 	    </td>
	<tr>
 	    
 	    
		<!--  <td colspan="2"><html:select name="procedimentoForm"
			property="posizione">
			<html:optionsCollection name="procedimentoForm"
				property="posizioniProcedimentoCollection" value="codice"
				label="description" />
		</html:select></td>
	</tr>-->
	
	<logic:equal name="procedimentoForm" property="posizione" value="E">  
	<tr>
		<td class="label"><label title="Data Evidenza" for="dataEvidenza"><bean:message
			key="procedimento.data.evidenza" /></label>&nbsp;<span class="obbligatorio">*</span>&nbsp;:</td>
		<td colspan="2"><html:text styleClass="obbligatorio" styleId="dataEvidenza" property="dataEvidenza"
		    name="procedimentoForm" size="10" maxlength="10"  />
		 <eprot:calendar textField="dataEvidenza" hasTime="false"/>		
		&nbsp;</td>
	</tr>
	</logic:equal>
	<tr>
		<td class="label">
			<label for="oggettoProcedimento"><bean:message key="procedimento.oggetto" /></label>&nbsp;<span class="obbligatorio">*</span>&nbsp;:
			<br/>
			<span><html:link action="/page/unicode.do?campo=oggettoProcedimento" target="_blank" >segni diacritici</html:link></span>
		</td>
		<td colspan="2">
			<html:text styleClass="obbligatorio" styleId="oggettoProcedimento" name="procedimentoForm" property="oggettoProcedimento" size="50" maxlength="250" />
		</td>
	</tr>
	<tr>
		<td class="label">
			<label for="note"> <bean:message key="procedimento.note" />&nbsp;: </label>
			<br/>
			<span><html:link action="/page/unicode.do?campo=note" target="_blank" >segni diacritici</html:link></span>			
		</td>
		<td colspan="2">
			<html:textarea name="procedimentoForm" styleId="note" property="note" rows="3" cols="50" />
		</td>
	</tr>
	<tr>
		<td class="label"><label for="numeroProtocollo"> <bean:message
			key="procedimento.numeroProtocollo" />&nbsp;: </label></td>
		<td colspan="2"><span><strong><bean:write name="procedimentoForm"
			property="numeroProtocollo" /></strong></span></td>
	</tr>
	<tr>
		<td class="label"><label for="numeroProcedimento"> <bean:message
			key="procedimento.numeroProcedimento" />&nbsp;: </label></td>
		<td colspan="2"><logic:greaterThan name="procedimentoForm"
			property="procedimentoId" value="0">
			<span><strong><bean:write name="procedimentoForm" property="numeroProcedimento" /></strong></span>
		</logic:greaterThan></td>
	</tr>
</table>


