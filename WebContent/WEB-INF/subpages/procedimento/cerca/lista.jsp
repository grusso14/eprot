<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<html:xhtml />
			<display:table class="simple" width="100%"
				requestURI="/page/procedimento/ricerca.do"
				name="sessionScope.ricercaProcedimentoForm.procedimenti" export="false"
				sort="list" pagesize="15" id="row">
				<logic:equal scope="session" name="tornaFaldone" value="true">
					<display:column title="">
						<input type="checkbox" name="procedimentiSelezionati" value="<bean:write name='row' property='id'/>" />
					</display:column>
				</logic:equal>
				<logic:equal scope="session" name="tornaFascicolo" value="true">
					<display:column title="">
						<input type="checkbox" name="procedimentiSelezionati" value="<bean:write name='row' property='id'/>" />
					</display:column>
				</logic:equal>
				<logic:equal scope="session" name="tornaProtocollo" value="true">
					<display:column title="">
						<input type="checkbox" name="procedimentiSelezionati" value="<bean:write name='row' property='id'/>" />
					</display:column>
				</logic:equal>
				<logic:equal scope="session" name="risultatiProcedimentiDaProtocollo" value="true">
					<display:column title="">
						<input type="checkbox" name="procedimentiSelezionati" value="<bean:write name='row' property='id'/>" />
					</display:column>
				</logic:equal>

				<display:column title="N. Procedimento">
					<html:link action="/page/procedimento.do" paramName="row" paramId="visualizzaProcedimentoId" paramProperty="id">
						<bean:write name="row" property="numeroProcedimento" />
					</html:link>
				</display:column>
				<display:column property="oggetto" title="Oggetto" />
				<display:column property="dataAvvioPro" title="Data Avvio" />
				<display:column property="descUfficio" title="Ufficio" />
				<display:column property="pathTitolario" titleKey="protocollo.titolario.argomento" />
				<display:column property="tipoProcedimentoDesc" title="Tipo" />	
				
			</display:table>