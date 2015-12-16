<%@ page contentType="text/html;charset=iso-8859-1" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot"%>

<html:xhtml />

<eprot:page title="Gestione documentale">
	<logic:equal parameter="documentoRegistrato" value="true" scope="request">
		<div id="protocollo_registrato"><bean:message key="documentale.salvatocorrettamente"/>.</div>
		<br />
	</logic:equal>

	<jsp:include page="/WEB-INF/subpages/documentale/common/pathSenzaLink.jsp" />

	<html:form action="/page/documentale/documentoView.do">
		<div id="protocollo">
		<div id="messaggi">
			<jsp:include page="/WEB-INF/subpages/documentale/common/messaggi.jsp" />
		</div>
		<div id="protocollo-errori">
			<jsp:include page="/WEB-INF/subpages/documentale/common/errori.jsp" />
		</div>
		<jsp:include page="/WEB-INF/subpages/documentale/datiDocumentoView.jsp" /> 
		<logic:notEmpty name="documentoForm" property="permessi">
			<div class="sezione">
				<span class="title"><strong><bean:message key="documentale.permessi"/></strong></span>
				<jsp:include page="/WEB-INF/subpages/documentale/permessiView.jsp" />
			</div>
		</logic:notEmpty>
		<div>

		<logic:equal name="documentoForm" property="versioneDefault" value="true">
			<logic:notEqual name="documentoForm" property="documentoId" value="0">

				<logic:equal name="documentoForm" property="statoArchivio" value="L">
					<!-- in Lavorazione -->
					<logic:equal name="documentoForm" property="permessoCorrente" value="2">
						<logic:equal name="documentoForm" property="statoDocumento" value="1">							
							<logic:equal name="documentoForm" property="owner" value="true">
								<html:submit styleClass="submit" property="modificaDocumentoAction" value="Modifica" alt="Modifica documento" />
								<html:submit styleClass="submit" property="visualizzaClassificaDocumentoAction" value="Classifica" alt="Classifica il documento" />
								<html:submit styleClass="submit" property="checkinDocumentoAction" value="Check-In" alt="Check-In documento" />
							</logic:equal>
						</logic:equal>							
						<logic:equal name="documentoForm" property="statoDocumento" value="0">
							<html:submit styleClass="submit" property="visualizzaClassificaDocumentoAction" value="Classifica" alt="Classifica il documento" />
							<html:submit styleClass="submit" property="checkoutDocumentoAction" value="Check-Out" alt="Check-Out documento" />
						</logic:equal>							

					</logic:equal>
					
					<logic:equal name="documentoForm" property="permessoCorrente" value="3">
						<logic:equal name="documentoForm" property="statoDocumento"	value="1">
							<logic:equal name="documentoForm" property="owner" value="true">
								<html:submit styleClass="submit" property="modificaDocumentoAction" value="Modifica" alt="Modifica documento" />
								<html:submit styleClass="submit" property="visualizzaClassificaDocumentoAction" value="Classifica" alt="Classifica il documento" />
								<html:submit styleClass="submit" property="checkinDocumentoAction" value="Check-In" alt="Check-In documento" />
							</logic:equal>
						</logic:equal>
						<logic:equal name="documentoForm" property="statoDocumento" value="0">
							<html:submit styleClass="submit" property="visualizzaClassificaDocumentoAction" value="Classifica" alt="Classifica il documento" />
							<html:submit styleClass="submit" property="checkoutDocumentoAction" value="Check-Out" alt="Check-Out documento" />
							<html:submit styleClass="submit" property="eliminaDocumentoAction" value="Elimina" alt="Elimina documento" />							
						</logic:equal>
					</logic:equal>
					
					<logic:equal name="documentoForm" property="permessoCorrente" value="4">
						<logic:equal name="documentoForm" property="statoDocumento" value="1">
							<logic:equal name="documentoForm" property="owner" value="true">
								<html:submit styleClass="submit" property="modificaDocumentoAction" value="Modifica" alt="Modifica documento" />
								<html:submit styleClass="submit" property="visualizzaClassificaDocumentoAction" value="Classifica" alt="Classifica il documento" />
								<html:submit styleClass="submit" property="spostaDocumentoAction" value="Sposta" alt="Sposta nella cartella..." />
								<html:submit styleClass="submit" property="checkinDocumentoAction" value="Check-In" alt="Check-In documento" />
							</logic:equal>
						</logic:equal>
						<logic:equal name="documentoForm" property="statoDocumento" value="0">
							<html:submit styleClass="submit" property="visualizzaClassificaDocumentoAction" value="Classifica" alt="Classifica il documento" />
							<html:submit styleClass="submit" property="checkoutDocumentoAction" value="Check-Out" alt="Check-Out documento" />
							<html:submit styleClass="submit" property="eliminaDocumentoAction" value="Elimina" alt="Elimina documento" />
						</logic:equal>
					</logic:equal>				
				</logic:equal>

				<!-- Classificato -->
				<logic:equal name="documentoForm" property="statoArchivio" value="C">
					<logic:equal name="documentoForm" property="permessoCorrente" value="3">
						<logic:empty name="documentoForm" property="fascicoliDocumento">
							<html:submit styleClass="submit" property="spostaDocumentoInLavorazioneAction" value="Sposta in lavorazione" alt="Imposta lo stato del documento per modifiche" />
						</logic:empty>
						<html:submit styleClass="submit" property="inviaProtocolloAction" value="Invia al protocollo" alt="Invia al protocollo" />
						<html:submit styleClass="submit" property="documentiArchivioAction" value="Protocolla" alt="Protocolla" />		
						<html:submit styleClass="submit"property="visualizzaFascicolaDocumentoAction" value="Fascicola Documento" alt="Fascicola il Documento" />
					</logic:equal>
					<logic:equal name="documentoForm" property="permessoCorrente" value="4">
						<logic:empty name="documentoForm" property="fascicoliDocumento">
							<html:submit styleClass="submit"property="spostaDocumentoInLavorazioneAction" value="Sposta in lavorazione" alt="Imposta lo stato del documento per modifiche" />
						</logic:empty>
						<html:submit styleClass="submit" property="inviaProtocolloAction" value="Invia al protocollo" alt="Invia al protocollo" />
						<html:submit styleClass="submit" property="protocolla" value="Protocolla" alt="Protocolla" />	
						<html:submit styleClass="submit" property="visualizzaFascicolaDocumentoAction" value="Fascicola Documento" alt="Fascicola il Documento" />
					</logic:equal>
				</logic:equal>

				<logic:greaterThan name="documentoForm" property="versione" value="0">
					<logic:equal name="documentoForm" property="versioneDefault" value="true">
						<logic:greaterThan name="documentoForm" property="permessoCorrente" value="0">
							<html:submit styleClass="submit"
								property="viewStoriaDocumentoAction" value="Storia documento"
								alt="Storia documento" />
						</logic:greaterThan>
					</logic:equal>
				</logic:greaterThan>
			</logic:notEqual>
		</logic:equal> 
		<!-- fine condizione su  versioneDefault TRUE -->
		
		<logic:equal name="documentoForm" property="versioneDefault" value="false">
			<logic:equal name="documentoForm" property="statoDocumento" value="1">
				<logic:equal name="documentoForm" property="owner" value="true">
					<html:submit styleClass="submit"property="ripristinaVersioneDocumentoAction" value="Ripristina questa versione" alt="Ripristina questa versione" />
				</logic:equal>
			</logic:equal>
			<logic:greaterThan name="documentoForm" property="permessoCorrente" value="0">
				<html:submit styleClass="submit" property="viewStoriaDocumentoAction"
					value="Storia documento" alt="Storia documento" />
			</logic:greaterThan>
		</logic:equal></div>
		</div>
	</html:form>
</eprot:page>
