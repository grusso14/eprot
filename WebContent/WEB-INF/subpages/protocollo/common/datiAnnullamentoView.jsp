<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html:xhtml />
<div class="sezione">
<span class="title"><strong><bean:message key="protocollo.annullamento.dati"/></strong></span>
<table summary="">
<tr>
	<td class="label">
		<span><bean:message key="protocollo.data"/>:</span>
	</td>
	<td><span><strong><bean:write name="protocolloForm" property="dataAnnullamento" /></strong></span>
</tr>
<tr>
	<td class="label">
		<span><bean:message key="protocollo.annullamento.provvedimento"/>:</span>
	</td>
	<td><span><strong><bean:write name="protocolloForm" property="provvedimentoAnnullamento"/></strong></span>
</tr>
<tr>
	<td class="label">
		<span><bean:message key="protocollo.annullamento.note"/>:</span>
	</td>
	<td><span><strong><bean:write name="protocolloForm" property="notaAnnullamento"/></strong></span>
</tr>
</table>
</div>