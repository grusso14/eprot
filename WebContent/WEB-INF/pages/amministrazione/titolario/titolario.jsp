<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Titolario">
<html:form action="/page/amministrazione/titolario.do">
<div>
	<jsp:include page="/WEB-INF/subpages/amministrazione/titolario/titolario.jsp" />
</div>
<br />
		<logic:notEmpty name="titolarioForm" property="titolario">
		      <html:submit styleClass="submit" property="btnModifica" value="Modifica" title="Modifica/Cancella l'argomento corrente" />
			  <html:submit styleClass="submit" property="btnAssocia" value="Associa uffici" title="Associa l'argomento corrente agli uffici"/>
			  <html:submit styleClass="submit" property="btnAssociaTuttiUffici" value="Associa tutti gli uffici" title="Associa l'argomento a tutti gli uffici"/>			  
			<logic:greaterThan name="titolarioForm" property="titolario.parentId" value="0">	
				<html:submit styleClass="submit" property="btnSposta" value="Sposta" title="Sposta la voce di titolario" />			
			</logic:greaterThan>			
			<logic:greaterThan name="titolarioForm" property="titolario.versione" value="0">	
				<html:submit styleClass="submit" property="btnStoria" value="Storia" title="Storia titolario" />			
			</logic:greaterThan>			

		</logic:notEmpty>
        <html:submit styleClass="submit" property="btnNuovo" value="Nuovo" title="Inserisce un argomento sotto il livello di quello selezionato" />

</html:form>
</eprot:page>