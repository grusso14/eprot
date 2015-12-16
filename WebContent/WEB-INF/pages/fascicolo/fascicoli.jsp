<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione fascicoli">
<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/errori.jsp" />
</div>
<html:form action="/page/fascicoli.do">
<div class="sezione">
	<span class="title"><strong><bean:message key="fascicolo.datiricerca"/></strong></span>
	<jsp:include page="/WEB-INF/subpages/fascicolo/cerca.jsp" />
</div>
<logic:notEmpty name="fascicoloForm" property="fascicoli">
<div class="sezione">
	<span class="title"><strong><bean:message key="fascicolo.fascicoli"/></strong></span>
	<jsp:include page="/WEB-INF/subpages/fascicolo/lista.jsp" />
</div>
</logic:notEmpty>
</html:form>
</eprot:page>

