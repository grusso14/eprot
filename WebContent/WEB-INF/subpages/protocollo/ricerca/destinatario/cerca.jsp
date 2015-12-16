<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<p>
	<label for="destinatario">
	<bean:message key="protocollo.destinatario"/>:
	</label>
	<html:text property="destinatario" styleId="destinatario" size="30" maxlength="100" />
	<html:submit styleClass="submit" property="btnCercaDestinatario" value="Cerca" alt="Cerca" />
</p>
<br/>

