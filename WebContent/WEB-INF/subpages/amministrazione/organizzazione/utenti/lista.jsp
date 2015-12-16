<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<div>
	
<logic:notEmpty name="cercaUtentiForm" property="risultatoRicerca">
<display:table class="simple" width="100%" name="requestScope.cercaUtentiForm.risultatoRicerca" 
	export="false" sort="page" pagesize="15" id="utente" requestURI="/page/amministrazione/organizzazione/utenti/cerca.do">
	<display:column title="Nominativo">
		<html:link action="/page/amministrazione/organizzazione/utenti/profilo.do" paramName="utente" paramId="parId" paramProperty="id">
			<bean:write name="utente" property="cognome"/> 
			<bean:write name="utente" property="nome"/> 
		</html:link >
	</display:column>
	<display:column property="username" title="Username" />

</display:table>   
</logic:notEmpty>    
</div>
