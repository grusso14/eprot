<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione uffici">
<div id="protocollo-errori">
	<html:errors bundle="bundleErroriAmministrazione" />
</div>
<div id="messaggi">
	<jsp:include page="/WEB-INF/subpages/documentale/common/messaggi.jsp" />
</div>
<html:form action="/page/amministrazione/organizzazione/selezionaUfficio.do" >

<jsp:include page="/WEB-INF/subpages/amministrazione/organizzazione/uffici/uffici.jsp" />

<br/><br/>
<p>

	<html:submit styleClass="submit" property="btnNuovo" value="Nuovo" alt="Inserisce un nuovo Ufficio dipendente da quello selezionato"/>
    <html:submit styleClass="submit" property="btnModifica" value="Modifica" alt="Modifica l'ufficio selezionato"/>

	
	<logic:greaterThan name="ufficioForm" property="parentId" value="0" >
		<html:submit styleClass="submit" property="btnCancella" value="Cancella" alt="Cancella l'ufficio"/>
	</logic:greaterThan>	
	<logic:notEmpty name="ufficioForm" property="ufficiDipendenti" >
		<html:submit styleClass="button" property="impostaUfficioAction" value="Seleziona ufficio" title="Seleziona l'ufficio come corrente" />
		 <bean:define id="ufficioCorrentePath" name="ufficioForm" property="ufficioCorrentePath"/>
	</logic:notEmpty>	
	
</p>

</html:form>
</eprot:page>
