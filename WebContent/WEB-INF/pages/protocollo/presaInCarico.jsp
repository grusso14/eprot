<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Presa in carico">
<div id="protocollo-errori">
  <html:errors bundle="bundleErroriProtocollo" />
</div>
<html:form action="/page/protocollo/presaInCarico.do" focus="dataRegistrazioneDa">
<div>
	<jsp:include page="/WEB-INF/subpages/protocollo/presaincarico/cerca.jsp" />
	<hr></hr>
</div>
<div>
	<jsp:include page="/WEB-INF/subpages/protocollo/presaincarico/tabella.jsp" />
</div>
</html:form>

</eprot:page>




