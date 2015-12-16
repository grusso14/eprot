<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Cerca Soggetto">


					
<html:form action="page/protocollo/anagrafica/listaDistribuzione">
<div id="protocollo-errori">
  <html:errors bundle="bundleErroriProtocollo" />
</div>

<div id="LDPF_cerca">
	<jsp:include page="/WEB-INF/subpages/protocollo/anagrafica/listaDistribuzioni/cercaPF.jsp" />
</div>
<hr />
<div id="LDPF_elenco">
	<jsp:include page="/WEB-INF/subpages/protocollo/anagrafica/listaDistribuzioni/elencoPF.jsp" />
</div>

</html:form>

</eprot:page>