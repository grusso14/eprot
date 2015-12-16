<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Protocollo in ingresso email">
	
	
	
	<bean:define id="url" value="/page/protocollo/ingresso/email.do" scope="request" />
	<div id="protocollo-errori">
    	<jsp:include page="/WEB-INF/subpages/protocollo/common/errori.jsp" />
	</div>
	<html:form action="/page/protocollo/ingresso/email.do">

		<div>
		<logic:notEmpty name="listaEmailForm" property="listaEmail">
			<display:table class="simple" width="100%"
				requestURI="/page/protocollo/ingresso/email.do"
				name="requestScope.listaEmailForm.listaEmail" export="false"
				sort="list" pagesize="10" id="row">
				<display:column title="">
					<bean:define id="id" name="row" property="id"/>
					<html:radio property="emailSelezionataId" idName="row" value="id"></html:radio>
				</display:column>

				<display:column title="Inviata da" property="nomeMittente" />
				<display:column title="Email" property="emailMittente" />
				<display:column title="Data spedizione" property="dataSpedizione" />
				<display:column title="Testo" property="testoMessaggio" />
				<display:column title="Allegati" property="numeroAllegati" />
			</display:table>
			<p>
				<html:submit styleClass="submit" property="visualizza" value="Visualizza" alt="Visualizza il messaggio selezionato" />
				<html:submit styleClass="submit" property="cancella" value="Elimina" alt="Elimina" />
			</p>
		</logic:notEmpty>
		<logic:empty name="listaEmailForm" property="listaEmail">
		<bean:message key="email.ingresso"/>.		 
		</logic:empty>
		</div>
	</html:form>

</eprot:page>
