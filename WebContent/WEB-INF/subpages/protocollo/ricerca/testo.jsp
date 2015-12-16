<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html:xhtml />
<table summary="">				
	<tr>
		<td class="label">
			<label for="descrizioneAnnotazione"><bean:message key="protocollo.annotazioni.note"/>:</label>
		</td>
		<td>
			<html:text property="text" styleId="descrizioneAnnotazione" size="80%"></html:text>
		</td>
	</tr>					
</table>	