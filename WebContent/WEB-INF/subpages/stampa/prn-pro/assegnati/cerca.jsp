<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<html:xhtml />
<div id="protocollo-errori">
  <html:errors bundle="bundleErroriProtocollo" />
</div>
<p>
	<jsp:include page="../uffici.jsp" /><br />
	<html:submit styleClass="submit" property="btnStampa" value="Stampa" alt="Stampa protocolli assegnati"/>
</p>
