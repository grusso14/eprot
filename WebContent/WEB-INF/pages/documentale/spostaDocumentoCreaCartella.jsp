<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione documentale">

<div id="messaggi">
    <jsp:include page="/WEB-INF/subpages/documentale/common/messaggi.jsp" />
</div>
<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/documentale/common/errori.jsp" />
</div>
<html:form action="/page/documentale/spostaDocumento.do">
<html:hidden name="cartelleForm" property="cartellaCorrenteId"/>
<html:hidden name="cartelleForm" property="cartellaId"/>
<html:text name="cartelleForm" property="nomeCartella"/>
<html:submit styleClass="submit" property="salvaCartella" value="Salva Cartella" alt="Salva Cartella" />
</html:form>
</eprot:page>
