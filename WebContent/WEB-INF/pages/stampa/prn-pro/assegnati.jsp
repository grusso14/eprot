<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Stampa Protocolli assegnati">


<html:form action="/page/stampa/prn-pro/assegnati.do" >

<div>
	<jsp:include page="/WEB-INF/subpages/stampa/prn-pro/assegnati/cerca.jsp" />
	<hr></hr>
</div>
<div>
	<jsp:include page="/WEB-INF/subpages/stampa/prn-pro/assegnati/lista.jsp" />
</div>

</html:form>

</eprot:page>
