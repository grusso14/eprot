<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />


<eprot:page title="Gestione Lista Distribuzione">

<div id="protocollo-errori">
  <html:errors bundle="bundleErroriProtocollo" />
</div>
<logic:equal name="operazioneEffettuata" value="true"
		scope="request">
		<div id="protocollo_registrato"> <bean:message key="soggetto.lista.salvatacorrettamente"/>.</div>
</logic:equal>

<div>
	<bean:message key="campo.obbligatorio.msg" />
</div>
<html:form action="/page/protocollo/anagrafica/listaDistribuzione/nuovo.do">
<div>

<div class="sezione">
  <span class="title"><strong><bean:message key="soggetto.giuridica.altridati"/></strong></span>
	<jsp:include page="/WEB-INF/pages/protocollo/anagrafica/listaDistribuzioni/formListaDistribuzioni.jsp" />
</div>

<logic:greaterThan name="listaDistribuzioneForm" property="codice" value="0"> 
<div class="sezione">
  <span class="title"><strong><bean:message key="soggetto.giuridica.soggetti"/></strong></span>
	<jsp:include page="/WEB-INF/subpages/protocollo/anagrafica/listaDistribuzioni/elencoSoggettiLD.jsp" />
</div>
</logic:greaterThan> 

<html:submit styleClass="submit"  property="salvaAction" value="Salva" alt="Salva" />
<html:reset styleClass="submit" property="ResetAction" value="Pulisci" alt="Pulisci" />	
<logic:greaterThan name="listaDistribuzioneForm" property="codice" value="0"> 
	<html:submit styleClass="submit" property="deleteAction" value="Cancella" alt="Cancella" />

</logic:greaterThan>    

</div>
</html:form>

</eprot:page>