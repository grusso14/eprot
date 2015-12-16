<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Area Organizzativa">
<br />
<br />
	<html:form action="/page/amministrazione/organizzazione/aoo/aoo.do">
		<div>
			<span>
					I dati della Area organizzativa omogenea sono stati inseriti correttamente.<br/>
					Per poter accedere alla struttura ed organizzare gli uffici, gli utenti dovranno<br/>
					utilizzare le seguenti credenziali:<br /><br />
					<strong>
							<bean:write name="areaOrganizzativaForm" property="msgSuccess"/>
					</strong>
					
			</span>
		</div>
		<br />
		<br />
		<p>
		    <html:submit styleClass="submit" property="btnAnnulla" value="Esci" alt="Esci" /> 
		</p>
	</html:form>
</eprot:page>
