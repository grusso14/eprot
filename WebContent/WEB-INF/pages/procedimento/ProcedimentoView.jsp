<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml/>

<eprot:page title="Procedimento">
<div id="protocollo-errori">
  <html:errors bundle="bundleErroriFaldone" />
</div>

<html:form action="/page/procedimento.do">
<div id="protocollo">
	<jsp:include page="/WEB-INF/subpages/procedimento/datiProcedimentoView.jsp" />
</div>
<html:submit styleClass="submit" property="btnStoria"
		value="Indietro" alt="Ritorna al procedimento" />		
</html:form>
</eprot:page>