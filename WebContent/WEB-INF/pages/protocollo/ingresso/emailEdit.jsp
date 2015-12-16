<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Protocollo in ingresso email">
	
	
	<bean:define id="url" value="/page/protocollo/ingresso/email.do" scope="request"/>
	<div id="protocollo-errori">
    	<jsp:include page="/WEB-INF/subpages/protocollo/common/errori.jsp" />
	</div>
	<html:form action="/page/protocollo/ingresso/email.do">
		<div>
		    <jsp:include page="/WEB-INF/subpages/protocollo/ingresso/dettaglioEmail.jsp" />
		</div>
		<div>
			<html:submit styleClass="submit" property="protocolla" value="Protocolla" alt="Protocolla" />
			<html:submit styleClass="submit" property="cancella" value="Elimina" alt="Elimina" />
		</div>
	</html:form>

</eprot:page>

