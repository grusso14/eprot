<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Storia Procedimento">
<html:form action="/page/procedimento/storia.do">
<span>
	<bean:message key="procedimento.progressivo"/> : <strong><bean:write name="procedimentoForm" property="numeroProcedimento"/></strong> <br />
	<bean:message key="procedimento.versione"/> : <strong>
	<html:link action="/page/procedimento/storia.do?versioneCorrente=true">
		<bean:write name="procedimentoForm" property="versione"/>
	</html:link>
	</strong> <br />		
	<bean:message key="procedimento.dataavvio"/>  : <strong><bean:write name="procedimentoForm" property="dataAvvio"/></strong> <br />		
	<bean:message key="procedimento.oggetto"/> : <strong><bean:write name="procedimentoForm" property="oggettoProcedimento"/></strong> <br />		
    <bean:message key="procedimento.posizione"/> : 
    <logic:equal name="procedimentoForm" property="posizione" value="A">
	   
				<strong><bean:message key="procedimento.posizione1"/></strong> <br />
			
	</logic:equal> 
	<logic:equal name="procedimentoForm" property="posizione" value="E">
	    
				<strong><bean:message key="procedimento.posizione2"/></strong><br />
			
	</logic:equal>
	<logic:equal name="procedimentoForm" property="posizione" value="T">
	    
				<strong><bean:message key="procedimento.posizione3"/></strong><br />
			
	</logic:equal>
	
	 
	<bean:message key="procedimento.dataevidenza"/> : <strong><bean:write name="procedimentoForm" property="dataEvidenza"/></strong> <br />	
		

</span>
<br />	
<br />	
<logic:notEmpty name="storiaProcedimentoForm" property="versioniProcedimento">
<div>
	<display:table class="simple" width="100%"
		name="requestScope.storiaProcedimentoForm.versioniProcedimento"
		requestURI="/page/procedimento/storia.do" export="false"
		sort="list" pagesize="20" id="row">
		<display:column title="Versione">
		<html:link action="/page/procedimento/storia.do" paramId="versioneSelezionata" paramName="row" paramProperty="versione">
			<bean:write name="row" property="versione" />
		</html:link>
		</display:column>
	
		<display:column property="pathTitolario" title="Titolario" />	
		<display:column property="descUfficioId" title="Ufficio" />									
		<display:column property="posizione" title="Posizione" />
		<display:column property="dataAvvioStr" title="Data avvio" />	 			
		<display:column property="dataEvidenza" title="Data evidenza" />
	</display:table>
	<br /><br />
	
	<html:submit styleClass="submit" property="btnStoriaProcedimento"
		value="Ritorna al procedimento" alt="Ritorna al procedimento" />
</div>
</logic:notEmpty>
</html:form>

</eprot:page>




