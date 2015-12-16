<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />



<html:form action="page/amministrazione/tipiProcedimento.do" >

<logic:notEmpty name="tipoProcedimentoForm" property="tipiProcedimento">
<display:table class="simple" width="100%"
	requestURI="/page/amministrazione/tipiProcedimento.do"
	name="tipoProcedimentoForm" property="tipiProcedimento" export="false"
	sort="list" pagesize="15" id="row">
	<display:column property="ufficio" title="Ufficio" />
	<display:column title="Descrizione" property="descrizione"	
		url="/page/amministrazione/tipiProcedimento.do" 
		paramId="parId" paramProperty="idTipo" />
		
	
</display:table>
</logic:notEmpty>


</html:form>


