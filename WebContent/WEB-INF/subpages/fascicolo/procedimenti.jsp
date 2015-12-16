<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html:xhtml />

<logic:empty name="fascicoloForm" property="procedimentiFascicolo">
<span><bean:message key="fascicolo.messaggio.procedimenti"/></span><br /><br />
</logic:empty>

<logic:notEmpty name="fascicoloForm" property="procedimentiFascicolo">
	<display:table class="simple" width="95%" requestURI="/page/fascicoli.do"
		name="sessionScope.fascicoloForm.procedimentiFascicolo"
		export="false" sort="list" pagesize="10" id="row">
		
		<logic:equal name="fascicoloForm" property="statoFascicolo" value="0">
			<logic:equal name="fascicoloForm" property="modificabile" value="true">
				<logic:equal name="fascicoloForm" property="versioneDefault" value="true">
				<display:column title="">
				    <input type="checkbox" name="procedimentiSelezionati" value="<bean:write name='row' property='id'/>" />
				</display:column>
				</logic:equal>
			</logic:equal>
		</logic:equal>					
		<display:column property="numeroProcedimento" title="Numero" />
		<display:column property="oggetto" title="Oggetto" />
	</display:table>
</logic:notEmpty>
<div>

<logic:equal name="fascicoloForm" property="modificabile" value="true">
<br class="hidden" />
	<logic:equal name="fascicoloForm" property="versioneDefault" value="true">
		<logic:equal name="fascicoloForm" property="statoFascicolo" value="0">
			<html:submit styleClass="submit" property="btnAggiungiProcedimenti" value="Aggiungi" title="Associa uno o pi� procedimenti al fascicolo" />
			<logic:notEmpty name="fascicoloForm" property="procedimentiFascicolo">
				<html:submit styleClass="submit" property="btnRimuoviProcedimenti" value="Rimuovi" title="Rimuove i procedimenti selezionati dal fascicolo" />
			</logic:notEmpty>	
		</logic:equal>
	</logic:equal>
</logic:equal>

</div>
