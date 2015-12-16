<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<div>  

<logic:greaterThan name="scartoProtocolliForm" property="risultatiScarto" value="0">
<div class="sezione">
	<span class="title"><strong><bean:message key="amministrazione.scarto.messaggio3"/></strong></span>
	<br />
	<span><bean:message key="amministrazione.scarto.messaggio4"/>Numero totale protocolli scartati :<strong><bean:write name="scartoProtocolliForm" property="risultatiScarto" /></strong></span>  
</div>

</logic:greaterThan>
</div>
