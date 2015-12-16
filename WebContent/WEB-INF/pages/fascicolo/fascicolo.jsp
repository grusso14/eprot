<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Gestione fascicoli">
<html:form action="/page/fascicolo.do" enctype="multipart/form-data">

<div id="protocollo">

<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/fascicolo/errori.jsp" />
</div>

<br class="hidden" />
<html:hidden property="id" />
<jsp:include page="/WEB-INF/subpages/fascicolo/fascicoloView.jsp" />


<br class="hidden" />
<div class="sezione">
<bean:define id="section" name="fascicoloForm" property="sezioneVisualizzata" />
<jsp:include page="/WEB-INF/subpages/fascicolo/link-sezioni.jsp" />
	<logic:equal name="section" value="Protocolli" >
	    <jsp:include page="/WEB-INF/subpages/fascicolo/protocolli.jsp" />
	</logic:equal>
	<logic:equal name="section" value="Documenti" >
	    <jsp:include page="/WEB-INF/subpages/fascicolo/documenti.jsp" />
	</logic:equal>    
	<logic:equal name="section" value="Faldoni" >
	    <jsp:include page="/WEB-INF/subpages/fascicolo/faldoni.jsp" />
	</logic:equal>    
	<logic:equal name="section" value="Procedimenti" >
	    <jsp:include page="/WEB-INF/subpages/fascicolo/procedimenti.jsp" />
	</logic:equal>    

	
</div>

<div>
<logic:equal name="fascicoloForm" property="versioneDefault" value="true">
	<logic:notEqual name="fascicoloForm" property="id" value="0" >		 
		<logic:equal name="fascicoloForm" property="modificabile" value="true" >			
			<logic:equal name="fascicoloForm" property="statoFascicolo" value="4" >
				<html:submit styleClass="submit" property="btnModifica" title="Modifica i dati del fascicolo" >
					<bean:message key="fascicolo.button.modifica"/>
				</html:submit>
				<html:submit styleClass="submit" property="btnRiapri" title="Riapre il fascicolo">
					<bean:message key="fascicolo.button.riapri"/>
				</html:submit>				
				<html:submit styleClass="submit" property="btnChiudi" title="Il fascicolo viene messo agli Atti" >
					<bean:message key="fascicolo.button.chiudi"/>
				</html:submit>
				<html:submit styleClass="submit" property="btnInvio" title="Invia il fascicolo al protocollo" >
					<bean:message key="fascicolo.button.invia.protocollo"/>
				</html:submit>
				<%--html:submit styleClass="submit" property="btnCancella" title="Cancella il fascicolo">
					<bean:message key="fascicolo.button.cancella"/>
				</html:submit--%>
			</logic:equal>
	
			<logic:equal name="fascicoloForm" property="statoFascicolo" value="0" >
				<html:submit styleClass="submit" property="btnModifica" title="Modifica i dati del fascicolo" >
					<bean:message key="fascicolo.button.modifica"/>
				</html:submit>
				<html:submit styleClass="submit" property="btnChiudi" title="Il fascicolo viene messo agli Atti" >
					<bean:message key="fascicolo.button.chiudi"/>
				</html:submit>
				<html:submit styleClass="submit" property="btnInvio" title="Invia il fascicolo al protocollo" >
					<bean:message key="fascicolo.button.invia.protocollo"/>
				</html:submit>
				<%--html:submit styleClass="submit" property="btnCancella" title="Cancella il fascicolo">
					<bean:message key="fascicolo.button.cancella"/>
				</html:submit--%>
			</logic:equal>
			<logic:equal name="fascicoloForm" property="statoFascicolo" value="1" >
				<html:submit styleClass="submit" property="btnRiapri" title="Riapre il fascicolo">
					<bean:message key="fascicolo.button.riapri"/>
				</html:submit>
				<html:submit styleClass="submit" property="btnScarta" title="Scarta il fascicolo">
					<bean:message key="fascicolo.button.scarta"/>
				</html:submit>
			</logic:equal>
			<logic:equal name="fascicoloForm" property="statoFascicolo" value="2" >
				<html:submit styleClass="submit" property="btnAnnullaInvio" title="Annulla l'invio al protocollo, riapre il fascicolo">
					<bean:message key="fascicolo.button.annulla.invio"/>
				</html:submit>
			</logic:equal>	
		</logic:equal>
	</logic:notEqual>
	<html:submit styleClass="button" property="btnAnnullaSelezione" value="Annulla" title="Annulla l'operazione" />
	<logic:greaterThan name="fascicoloForm" property="versione" value="0" >	
			<html:submit styleClass="submit" property="btnStoria" value="Storia" title="Storia del Fascicolo" />
	</logic:greaterThan>
	
</logic:equal>
<logic:notEqual name="fascicoloForm" property="versioneDefault" value="true">
	<html:submit styleClass="submit" property="btnStoria" value="Storia" title="Storia del Fascicolo" />
</logic:notEqual>

</div>
</div>
</html:form>
</eprot:page>
