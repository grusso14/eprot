<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<p><label for="mittente"><bean:message key="protocollo.mittente"/>:</label>
<html:text property="mittente" styleId="mittente" size="30" maxlength="100" />
<html:submit property="btnCercaMittente" value="Cerca" alt="Cerca" />
</p>

