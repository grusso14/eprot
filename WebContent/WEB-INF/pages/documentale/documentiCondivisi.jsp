<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione documentale">
<bean:define id="baseUrl" value="/page/documentale/cartelle.do" scope="request" />
<html:xhtml />
<div id="messaggi">
    <jsp:include page="/WEB-INF/subpages/documentale/common/messaggi.jsp" />
</div>
<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/documentale/common/errori.jsp" />
</div>
<html:form action="/page/documentale/cartelle.do">
<jsp:include page="/WEB-INF/subpages/documentale/common/file_condivisi.jsp" />
</html:form>
</eprot:page>
