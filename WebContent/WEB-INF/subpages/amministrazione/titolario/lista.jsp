<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<div>  
<logic:notEmpty name="titolarioForm" property="listaTitolario">
<TABLE>
<tr>
    <td><bean:message key="protocollo.argomento.codice"/></td>
    <td><bean:message key="protocollo.argomento.descrizione"/></td>
</tr>
<logic:iterate id="currentRecord" property="listaTitolario" name="titolarioForm">
<tr>
    <td>
   	<html:link action="/titolario.do" paramId="parTitolarioId" paramName="currentRecord" paramProperty="id">
	<bean:write name="currentRecord" property="codTitolario"/>
	</html:link >
	</td>
    <td>
	<bean:write name="currentRecord" property="descrizioneTitolario"/>
	</td>
</tr>	
</logic:iterate>     
</TABLE>
</logic:notEmpty>    
</div>

