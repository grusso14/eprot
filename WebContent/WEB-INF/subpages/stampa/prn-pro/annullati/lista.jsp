<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<div>  
<logic:messagesPresent property="recordNotFound" message="true">
<bean:message key="report.messaggio1"/>.
</logic:messagesPresent>
<logic:messagesNotPresent property="recordNotFound" message="true">
<logic:notEmpty name="reportAnnullatiForm" property="reportFormatsCollection">
<bean:define id="url" value="/page/stampa/prn-pro/annullati.do?" scope="request" />

<bean:message key="report.messaggio2"/>:
<br />
<ul>
<logic:iterate id="currentRecord" name="reportAnnullatiForm" property="reportFormatsCollection" >
	<li>
	<html:link action="/page/stampa/prn-pro/annullati" name="currentRecord" property="parameters" target="_blank">
		<bean:write name="currentRecord" property="descReport"/>
	</html:link >
	</li>
</logic:iterate>
</ul>
</logic:notEmpty>  
</logic:messagesNotPresent>
</div>
