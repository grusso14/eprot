<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Scarto protocolli">
	<div id="protocollo-errori">
	<jsp:include page="/WEB-INF/subpages/amministrazione/common/errori.jsp" />
	</div>
	<html:form action="/page/amministrazione/scarto.do">
		<jsp:include page="/WEB-INF/subpages/amministrazione/scarto/cerca.jsp" />
	</html:form>
	<jsp:include page="/WEB-INF/subpages/amministrazione/scarto/risultatiScarto.jsp" />
</eprot:page>
