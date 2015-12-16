<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Cerca soggetto">


<html:form action="/page/protocollo/ricerca.do">

<div id="cerca">
	<jsp:include page="/WEB-INF/subpages/protocollo/ricerca/destinatario/cerca.jsp" />
</div>
<div id="elenco">
	<jsp:include page="/WEB-INF/subpages/protocollo/ricerca/destinatario/listaDestinatari.jsp" />
</div>

</html:form>

</eprot:page>