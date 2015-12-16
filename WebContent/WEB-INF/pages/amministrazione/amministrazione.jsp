<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot"%>

<html:xhtml />

<eprot:page title="Gestione amministrazione">
	<div id="protocollo-errori">
	<jsp:include page="/WEB-INF/subpages/amministrazione/common/errori.jsp" />
	</div>
	<html:form action="/page/amministrazione.do">
	<div class="sezione">
	<span class="title"><strong><bean:message key="amministrazione.datigenarali"/></strong></span><br />
	<jsp:include page="/WEB-INF/subpages/amministrazione/dati_generali.jsp"/>
	</div>
	<div class="sezione">
	<span class="title"><strong><bean:message key="amministrazione.parametri"/></strong></span><br />
	<jsp:include page="/WEB-INF/subpages/amministrazione/dati_ldap.jsp"/>
	</div>
	<p>
    <html:submit styleClass="submit" property="btnSalva" value="Salva" alt="Salva" /> 
	<html:submit styleClass="button" property="btnAnnulla" value="Annulla" alt="Annulla" />
	</p>
	</html:form>
</eprot:page>
