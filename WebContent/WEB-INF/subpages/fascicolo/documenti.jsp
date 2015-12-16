<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html:xhtml />


<bean:define value="/page/fascicoli.do?documentoSelezionato=" scope="request" id="urlDocumento" />
<bean:define value="/page/fascicoli.do?" scope="request" id="url" />
<logic:empty name="fascicoloForm" property="documentiFascicolo">
<span><bean:message key="fascicolo.messaggio3"/></span><br /><br />
</logic:empty>

<logic:notEmpty name="fascicoloForm" property="documentiFascicolo">
	<display:table class="simple" width="95%" requestURI="/page/fascicoli.do"
		name="sessionScope.fascicoloForm.documentiFascicolo"
		export="false" sort="list" pagesize="10" id="row">
		
		<logic:equal name="fascicoloForm" property="statoFascicolo" value="0">
			<logic:equal name="fascicoloForm" property="modificabile" value="true">
				<logic:equal name="fascicoloForm" property="versioneDefault" value="true">
					<display:column title="">
						<html:radio property="documentoSelezionato" idName="row" value="id"></html:radio>
					</display:column>
				</logic:equal>
			</logic:equal>
		</logic:equal>					
		<display:column property="nomeFile" title="File name" />
		<display:column property="descrizione" title="Descrizione"  />
		<display:column property="dataDocumento" title="Data" 
		decorator="it.finsiel.siged.mvc.presentation.helper.DateDecorator"/>
		<display:column title="Stato">		
		<logic:equal name="row" property="statoArchivio" value="L"><bean:message key="fascicolo.lavorazione"/></logic:equal>
		<logic:equal name="row" property="statoArchivio" value="C"><bean:message key="fascicolo.classificato"/></logic:equal>
		</display:column>				
	</display:table>
</logic:notEmpty>
<div>

<logic:equal name="fascicoloForm" property="modificabile" value="true">
<br class="hidden" />
	<logic:equal name="fascicoloForm" property="versioneDefault" value="true">
		<logic:equal name="fascicoloForm" property="statoFascicolo" value="0">
			<html:submit styleClass="submit" property="btnAggiungiDocumenti" value="Aggiungi" title="Inserisce un documento dell'archivio corrente nel fascicolo" />
			<logic:notEmpty name="fascicoloForm" property="documentiFascicolo">
				<html:submit styleClass="submit" property="btnRimuoviDocumento" value="Rimuovi" title="Rimuove il documento selezionato dal fascicolo" />
			</logic:notEmpty>	
		</logic:equal>
	</logic:equal>
</logic:equal>

</div>
