<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<div> 
<br /> 
<span>
	<logic:messagesPresent property="recordNotFound" message="true">
		<bean:message key="report.messaggio1"/>.
	</logic:messagesPresent>
	<logic:messagesNotPresent property="recordNotFound" message="true">
		<logic:notEmpty name="reportAnagraficaForm" property="reportFormatsCollection">
			<bean:message key="report.messaggio2"/>: &nbsp;
			<logic:iterate id="currentRecord" name="reportAnagraficaForm" property="reportFormatsCollection">
 				<html:link action="/page/stampa/rubrica" name="currentRecord" property="parameters"  target="_blank">					
					<bean:write name="currentRecord" property="descReport"/>
			  	</html:link >
				&nbsp;|
			</logic:iterate>     
		</logic:notEmpty>  
	</logic:messagesNotPresent>
</span>
</div>
