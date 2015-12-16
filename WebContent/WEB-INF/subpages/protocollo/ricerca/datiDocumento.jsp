<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<table  summary="">
<tr>
 <td  class="label">
 <div>
	<label ><bean:message key="protocollo.documento.tipo"/>:</label>
	</td>
	 <td>
	<html:select name="ricercaForm" property="tipoDocumento">
		<option value="<bean:write name="ricercaForm" property="tutti" />"><bean:write name="ricercaForm" property="tutti" /></option>
		<bean:define id="tipiDocumento" name="ricercaForm" property="tipiDocumento" />
		<html:optionsCollection name="tipiDocumento" value="id" label="descrizione"/>
	</html:select>&nbsp;&nbsp;
	</td>
	 <td>
	<logic:equal name="ricercaForm" property="tipoProtocollo" value="I">
		<label title="Data in cui il documento e' stato ricevuto" for="dataRicevutoDa">
	    	<bean:message key="protocollo.documento.ricevuto"/>&nbsp;
	   		<bean:message key="protocollo.da"/>: 
	    </label>
	   	<html:text property="dataRicevutoDa" styleId="dataRicevutoDa" size="10" maxlength="10" />
	    <eprot:calendar textField="dataRicevutoDa" hasTime="false"/>
	    &nbsp;
		<label  for="dataRicevutoA">
	   		<bean:message key="protocollo.a"/>:
	    </label>
	   	<html:text property="dataRicevutoA" styleId="dataRicevutoA" size="10" maxlength="10" />
	   <eprot:calendar textField="dataRicevutoA" hasTime="false"/>
	</logic:equal>
</td>

<tr>
<td class="label">
	<label for="dataDocumentoDa"><bean:message key="protocollo.documento.data"/>&nbsp;
	<bean:message key="protocollo.da"/>:</label>
</td>
<td >	
	<html:text property="dataDocumentoDa" styleId="dataDocumentoDa" size="10" maxlength="10" />
      <eprot:calendar textField="dataDocumentoDa" hasTime="false"/>
      </td>
      <td>
	&nbsp;&nbsp;
	<label for="dataDocumentoA"><bean:message key="protocollo.a"/>:</label>
	</td>
	<td>
	<html:text property="dataDocumentoA" styleId="dataDocumentoA" size="10" maxlength="10" />
    <eprot:calendar textField="dataDocumentoA" hasTime="false"/>
    </td>
    </tr>

<tr>
	<td class="label" colspan="1">
	 <label  for="oggetto"><bean:message key="protocollo.oggetto"/>:</label>
	</td>
	<td colspan="3">
		<html:text name="ricercaForm" property="oggetto" styleId="oggetto" size="60" />
	</td>

<tr>
	<td class="label">
		<label for="progressivoFascicolo"><bean:message key="fascicolo.progressivo" />:</label>
	</td>
	<td>
		<html:text property="progressivoFascicolo" styleId="progressivoFascicolo" size="15" />
	</td>

</tr>
  </table>