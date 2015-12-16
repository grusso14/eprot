<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione documentale">
<jsp:include page="/WEB-INF/subpages/documentale/common/pathSenzaLink.jsp" />
<html:form action="/page/documentale/documento.do">

<div id="messaggi">
    <jsp:include page="/WEB-INF/subpages/documentale/common/messaggi.jsp" />
</div>
<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/documentale/common/errori.jsp" />
</div>

	<br class="hidden" />
	<jsp:include page="/WEB-INF/subpages/documentale/datiDocumentoView.jsp" />

	
	<logic:equal name="documentoForm" property="statoArchivio" value="C" >
	<br class="hidden" />
		<jsp:include page="/WEB-INF/subpages/documentale/datiFascicoli.jsp" />
	</logic:equal>
	
	<html:submit styleClass="submit" property="fascicolaDocumentoAction" value="Fascicola" alt="Fascicola il documento" />
    <html:submit styleClass="submit" property="annullaAction" value="Annulla" alt="Torna al documentale" />
</html:form>
</eprot:page>
