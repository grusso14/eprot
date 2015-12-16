<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Riassegna protocollo">


<html:form action="/page/protocollo/ingresso/riassegna.do">

<div id="protocollo">

<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/errori.jsp" />
</div>
<br class="hidden" />

<jsp:include page="/WEB-INF/subpages/protocollo/common/datiProtocollo.jsp" />

<br class="hidden" />

<jsp:include page="/WEB-INF/subpages/protocollo/common/datiDocumentoView.jsp" />

<br class="hidden" />

<div id="sezioni-protocolli" class="sezione">
    <jsp:include page="/WEB-INF/subpages/protocollo/ingresso/assegnatari.jsp" />
</div>

<br class="hidden" />
<div>
	<html:submit styleClass="submit" property="salvaAction" value="Riassegna" alt="Riassegna protocollo" />
	<html:submit styleClass="submit" property="annullaAction" value="Annulla" alt="Annulla l'operazione" />
</div>

</div>

</html:form>

</eprot:page>