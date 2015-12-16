<%@ page contentType="text/html;charset=iso-8859-1" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/displaytag-11.tld" prefix="display"%>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot"%>
<html:xhtml />

<eprot:page title="Ricerca procedimenti">
  <div class="sezione" styleClass="obbligatorio">
	<span class="title"><strong><bean:message key="procedimento.procedimento"/></strong></span>

	<html:form action="/page/procedimento.do">
	
		<div id="protocollo-errori"><html:errors
			bundle="bundleErroriProtocollo" /></div>

		<div><jsp:include
			page="/WEB-INF/subpages/procedimento/datiProcedimento.jsp"/></div>

		<logic:notEqual name="procedimentoForm" property="procedimentoId" value="0">
		<div class="sezione">
		    <span class="title"><strong><bean:message key="procedimento.fascicoli"/></strong></span>
			<jsp:include page="/WEB-INF/subpages/procedimento/listaFascicoli.jsp" />
   			<jsp:include page="/WEB-INF/subpages/procedimento/cercaFascicoli.jsp" />
		</div> 
		
		<div class="sezione">
			<span class="title"><strong><strong><bean:message key="procedimento.faldoni"/></strong></span>
   			<jsp:include page="/WEB-INF/subpages/procedimento/listaFaldoni.jsp" />
   			<jsp:include page="/WEB-INF/subpages/procedimento/cercaFaldoni.jsp" />
		</div>
		
		<div class="sezione">
			<span class="title"><strong><bean:message key="procedimento.protocolli"/></strong></span>
   			<jsp:include page="/WEB-INF/subpages/procedimento/listaProtocolli.jsp" />
   			<jsp:include page="/WEB-INF/subpages/procedimento/cercaProtocolli.jsp" />
		</div>
		</logic:notEqual>
		
		<div>
			<html:submit styleClass="submit" property="salvaAction" value="Salva" alt="Salva" />
            <logic:equal name="procedimentoForm" property="indietroVisibile" value="true" >
			<html:submit styleClass ="submit" property="btnAnnulla" value="Indietro" alt="Indietro" />
			</logic:equal>	 	
			<html:reset styleClass="submit" property="ResetAction" value="Pulisci" alt="Pulisci" />		
			<logic:greaterThan name="procedimentoForm" property="versione" value="0" >	
				<html:submit styleClass="submit" property="btnStoria" value="Storia" title="Storia del Faldone" />
			</logic:greaterThan>	
		</div> 
	</html:form>
</div>
</eprot:page>
