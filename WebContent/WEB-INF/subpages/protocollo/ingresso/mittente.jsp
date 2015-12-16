<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<logic:equal name="protocolloForm" property="protocolloId" value="0" >
<table summary="">
  <tr>
    <td class="label">
      <span><bean:message key="protocollo.mittente.tipo"/>&nbsp;:</span>
    </td>
    <td>
        <html:radio property="mittente.tipo" styleId="personaFisica"
              value="F" onclick="document.forms[0].submit()">
          <label for="personaFisica"><bean:message key="protocollo.mittente.personafisica"/></label>
        </html:radio>
        &nbsp;&nbsp;
        <html:radio property="mittente.tipo" styleId="personaGiuridica"
              value="G" onclick="document.forms[0].submit()">
          <label for="personaGiuridica"><bean:message key="protocollo.mittente.personagiuridica"/></label>
        </html:radio>
        &nbsp;&nbsp;
        <html:radio property="mittente.tipo" styleId="personaGiuridica"
              value="M" onclick="document.forms[0].submit()">
          <label for="personaGiuridica"><bean:message key="protocollo.mittente.multi"/></label>
        </html:radio>
        &nbsp;&nbsp;
        <script type="text/javascript"></script>
        <noscript>
          <div>
          <html:submit styleClass="button" value="Imposta" title="Imposta il tipo di Mittente"/>
          &nbsp;&nbsp;
          </div>
        </noscript>
        <html:submit styleClass="button" property="cercaMittenteAction" value="Seleziona" title="Seleziona il mittente dalla rubrica"/>
    </td>
  </tr>
  <tr>
    <td class="label"><label
        for="numProtocolloMittente"><bean:message
        key="protocollo.mittente.protocolloid"/>&nbsp;:</label>
  </td>
  <td>    
    <html:text property="numProtocolloMittente" styleId="numProtocolloMittente" size="20" maxlength="50"/>
    &nbsp;&nbsp;
    <html:submit styleClass="button" property="btnCercaProtMitt" value="Cerca" title="Cerca il Protocollo del Mittente"/>
    </td>
  </tr>

<logic:equal name="protocolloForm" property="mittente.tipo" value= "M">	
	<jsp:include page="/WEB-INF/subpages/protocollo/common/multimittenti.jsp" />
</logic:equal>

<logic:equal name="protocolloForm" property="mittente.tipo" value= "F">	
  		<eprot:ifAuthorized permission="45">
			<logic:equal name="protocolloForm" property="protocolloId" value="0">
				<label for="fisicaToAdd">
					Aggiungi persona fisica in rubrica
				</label>&nbsp;: 
				<html:checkbox property="fisicaToAdd" styleId="fisicaToAdd" disabled="false" />
			</logic:equal><br/>
		</eprot:ifAuthorized>

<tr>
    <td class="label">
      <label for="cognomeMittente"><bean:message
          key="protocollo.mittente.cognome" /></label>&nbsp;<span
          class="obbligatorio">*</span>&nbsp;:
    </td>

    <td>
      <html:text styleClass="obbligatorio" property="mittente.cognome" styleId="cognomeMittente" size="45" maxlength="100"/>
          <script type='text/javascript'>
				$().ready(function(){
					//alert("ajax");
					$("#cognomeMittente").autocomplete("searchfisica.do",{
							minChars: 3,
							autoFill: false
							});
				
				$("#cognomeMittente").result(function(event, data, formatted) {
					//alert(data[1]);
					if (data) {
						$("#nomeMittente").val(data[1]);
						$(this).val(data[2]);
						$("#indirizzoMittente").val(data[3]);
						$("#localitaMittente").val(data[4]);
						$("#capMittente").val(data[5]);
						$("#provinciaId").val(data[6]);
						
					}
						
				});
				});
			</script>
    </td>
  </tr>
  <tr>
    <td class="label">
      <label for="nomeMittente"><bean:message key="protocollo.mittente.nome"/></label>&nbsp;:
    </td>
    <td>
      <html:text property="mittente.nome" styleId="nomeMittente" size="45" maxlength="40"/>
    </td>
  </tr>
</logic:equal>
<logic:equal name="protocolloForm" property="mittente.tipo" value="G">
<eprot:ifAuthorized permission="47">
			<logic:equal name="protocolloForm" property="protocolloId" value="0">
				<label for="giuridicaToAdd">
					Aggiungi persona giuridica in rubrica
				</label>&nbsp;: 
				<html:checkbox property="giuridicaToAdd" styleId="giuridicaToAdd" disabled="false" />
			</logic:equal>
		</eprot:ifAuthorized>
  <tr>
    <td class="label">
      <label for="denominazioneMittente"><bean:message
          key="protocollo.mittente.denominazione" /></label>&nbsp;<span
          class="obbligatorio">*</span>&nbsp;:
    </td>
    <td>
      <html:text styleClass="obbligatorio" styleId="denominazioneMittente" property="mittente.descrizioneDitta" size="45" maxlength="250" />
    </td>
        <script type='text/javascript'>
				$().ready(function(){
				
				$("#denominazioneMittente").autocomplete("searchgiuridica.do",{
							minChars: 3,
							autoFill: false
							});
				
				$("#denominazioneMittente").result(function(event, data, formatted) {
					//alert(data[1]);
					if (data) {
						//$("#denominazioneMittente").val(data[1]);
						$(this).val(data[1]);
						$("#indirizzoMittente").val(data[2]);
						$("#localitaMittente").val(data[3]);
						$("#capMittente").val(data[4]);
						$("#provinciaId").val(data[5]);
						
					}
						
				});
				});
			</script>
  </tr>
</logic:equal>
<logic:notEqual name="protocolloForm" property="mittente.tipo" value="M">
  <tr>
    <td class="label">
      <label for="indirizzoMittente"><bean:message key="soggetto.indirizzo" /></label>&nbsp;:
    </td>
    <td>
      <html:text property="mittente.indirizzo.toponimo" styleId="indirizzoMittente" size="45" maxlength="250" />
    </td>
  </tr>
  <tr>
    <td class="label">
      <label for="localitaMittente"><bean:message key="soggetto.localita" /></label>&nbsp;:
    </td>
    <td>
      <html:text property="mittente.indirizzo.comune" styleId="localitaMittente" size="45" maxlength="100" />
    </td>
  </tr>
  <tr>
    <td class="label">
      <label title="Codice di Avviamento Postale" for="capMittente"><bean:message key="soggetto.cap" /></label>&nbsp;:
    </td>
    <td class="subtable">
      <table summary="">
        <tr>
          <td>
            <html:text property="mittente.indirizzo.cap" styleId="capMittente" size="6" maxlength="5" />
          </td>
          <td>&nbsp;&nbsp;</td>
          <td class="label">
            <label for="provinciaMittente"><bean:message key="soggetto.provincia" /></label>&nbsp;:
          </td>
          <td>
            <html:select property="mittente.indirizzo.provinciaId" styleId="provinciaId">
              <html:optionsCollection property="province" value="provinciaId" label="descrizioneProvincia" />
            </html:select>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  </logic:notEqual>
</table>
</logic:equal>
<logic:notEqual name="protocolloForm" property="protocolloId" value="0" >
	<jsp:include page="/WEB-INF/subpages/protocollo/ingresso/mittenteView.jsp" />
</logic:notEqual>
