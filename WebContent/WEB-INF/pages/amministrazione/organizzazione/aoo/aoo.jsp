<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<eprot:page title="Area Organizzativa">
	<div id="protocollo-errori">
	<jsp:include page="/WEB-INF/subpages/amministrazione/common/errori.jsp" />
	</div>
	<html:form action="/page/amministrazione/organizzazione/aoo/aoo.do">
		<table summary="">
			
				<logic:empty name="areaOrganizzativaForm" property="areeOrganizzative">
					<p><span><strong><bean:message key="amministrazione.organizzazione.aoo.messaggio"/>.</strong></span></p>
				</logic:empty>
				<logic:notEmpty name="areaOrganizzativaForm" property="areeOrganizzative">
				<logic:iterate id="aoo" name="areaOrganizzativaForm" property="areeOrganizzative">
					<tr>
					<td>
					<html:radio property="id" value="id" idName="aoo">
						<span><bean:write name="aoo" property="description" /></span>
					</html:radio>
					</td>
					</tr>
					
				</logic:iterate>
				</logic:notEmpty>	
				
		</table>
		<br />
		<br />
		<p>
		    <html:submit styleClass="submit" property="btnModifica" value="Modifica" alt="Modifica" /> 
			<html:submit styleClass="submit" property="btnNuovo" value="Nuovo" alt="Inserisce una nuova area organizzativa" /> 
			<html:submit styleClass="submit" property="btnCancella" value="Cancella" alt="Cancella l'area organizzativa" />
		</p>
	</html:form>
</eprot:page>
