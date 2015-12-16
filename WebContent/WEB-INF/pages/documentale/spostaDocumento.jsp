<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione documentale">

<html:form action="/page/documentale/spostaDocumento.do">

<div id="messaggi">
    <jsp:include page="/WEB-INF/subpages/documentale/common/messaggi.jsp" />
</div>
<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/documentale/common/errori.jsp" />
</div>
<jsp:include page="/WEB-INF/subpages/documentale/common/pathSposta.jsp" />
<jsp:include page="/WEB-INF/subpages/documentale/common/cartelleSposta.jsp" />
<jsp:include page="/WEB-INF/subpages/documentale/common/filesSposta.jsp" />
</html:form>
</eprot:page>
