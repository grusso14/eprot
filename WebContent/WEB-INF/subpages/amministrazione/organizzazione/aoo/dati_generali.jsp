<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
	<html:hidden property="id"/>
	<html:hidden property="versione"/>
	<label for="tipo_aoo"><bean:message key="amministrazione.organizzazione.aoo.tipo"/><span class="obbligatorio"> * </span></label>:
	<html:select styleClass="obbligatorio" property="tipo_aoo">
		<html:optionsCollection property="tipiAoo" value="codice" label="description" />
	</html:select>
	&nbsp;
	<label for="description"><bean:message key="amministrazione.organizzazione.aoo.descrizione"/><span class="obbligatorio"> * </span></label>:
	<html:text property="description" styleClass="obbligatorio" maxlength="100" size="25"></html:text>
	&nbsp;
	<label for="codi_aoo"><bean:message key="amministrazione.organizzazione.aoo.codice"/></label>: 
	<html:text property="codi_aoo" styleClass="obbligatorio" maxlength="100" size="20"></html:text>

	<p><label for="data_istituzione"><bean:message key="amministrazione.organizzazione.aoo.dataistituzione"/><span class="obbligatorio"> * </span></label>:
		<jsp:include page="/WEB-INF/subpages/amministrazione/organizzazione/aoo/dataIstituzione.jsp" />&nbsp;
		<label for="data_soppressione"><bean:message key="amministrazione.organizzazione.aoo.datasoppressione"/></label>:
		<jsp:include page="/WEB-INF/subpages/amministrazione/organizzazione/aoo/dataSoppressione.jsp" />
	</p>
	<p>
		<label for="dipartimento_codice"><bean:message key="amministrazione.organizzazione.aoo.codicedipartimento"/></label>:
		<html:text property="dipartimento_codice" maxlength="3" size="4"></html:text>&nbsp;
		<label for="dipartimento_descrizione"><bean:message key="amministrazione.organizzazione.aoo.descrizionedipartimento"/></label>:
		<html:text property="dipartimento_descrizione" maxlength="20" size="20"></html:text>
	</p>
	
	<p>
		<logic:equal name="areaOrganizzativaForm" property="modificabileDipendenzaTitolarioUfficio" value="true">
			<label for="dipendenzaTitolarioUfficio"><bean:message key="amministrazione.organizzazione.aoo.dipendenzatitolariouffici"/>
			<span class="obbligatorio"> * </span></label>:
			<html:select styleClass="obbligatorio" property="dipendenzaTitolarioUfficio">
				<html:option value="0"><bean:message key="amministrazione.organizzazione.aoo.no"/></html:option>
				<html:option value="1"><bean:message key="amministrazione.organizzazione.aoo.si"/></html:option>
			</html:select>
			
			<label for="data_soppressione"><bean:message key="amministrazione.organizzazione.aoo.livellominimotitolario"/></label>:
			<html:text property="titolarioLivelloMinimo" maxlength="2" size="4" />
		</logic:equal>		
		
		<logic:equal name="areaOrganizzativaForm" property="modificabileDipendenzaTitolarioUfficio" value="false">
			<label for="dipendenzaTitolarioUfficio"><bean:message key="amministrazione.organizzazione.aoo.dipendenzatitolariouffici"/>
			<span class="obbligatorio"> * </span></label>:
			<logic:equal name="areaOrganizzativaForm" property="dipendenzaTitolarioUfficio" value="0">
				<span><strong><bean:message key="amministrazione.organizzazione.aoo.no"/></strong></span>
			</logic:equal>
			<logic:equal name="areaOrganizzativaForm" property="dipendenzaTitolarioUfficio" value="1">
				<span><strong><bean:message key="amministrazione.organizzazione.aoo.si"/></strong></span>
			</logic:equal>
			&nbsp;&nbsp;
			<label for="data_soppressione"><bean:message key="amministrazione.organizzazione.aoo.livellominimotitolario"/></label>:
			<span><strong><bean:write name="areaOrganizzativaForm"  property="titolarioLivelloMinimo" /></strong></span>			
			<html:hidden property="dipendenzaTitolarioUfficio" />
			<html:hidden property="titolarioLivelloMinimo" />
		</logic:equal>		

	</p>
	