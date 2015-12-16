<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Storia Protocollo">
<bean:define id="url" value="/page/protocollo/storia.do?versioneCorrente=true" scope="request"  />
<bean:define id="versione" name="storiaProtocolloForm" property="versione"/>
<%--<logic:equal name="storiaProtocolloForm" property="scartato" value="true">-->
<!--	<bean:define id="url" value="/page/protocollo/storia.do?versioneSelezionata=<bean:write name='storiaProtocolloForm' property='versione'/>" scope="request"  />-->
<!--</logic:equal>--%>
<logic:notEqual name="storiaProtocolloForm" property="scartato" value="true">
	<bean:define id="url2" value="/page/protocollo/storia.do?versioneCorrente=true" scope="request"  />
</logic:notEqual>

<html:form action="/page/protocollo/storia.do">
<div>
<label><bean:message key="protocollo.numero"/></label>: 
<html:link action="/page/protocollo/storia.do?versioneCorrente=true">
	<span><strong>
		<bean:write name="storiaProtocolloForm" property="numeroProtocollo" />
	</strong></span>
</html:link>
<br/>
<label><bean:message key="protocollo.tipo"/></label>: <span><strong>

<bean:write name="storiaProtocolloForm" property="flagTipo" />
</strong></span><br/>
<label><bean:message key="protocollo.data"/></label>: <span><strong>

<bean:write name="storiaProtocolloForm" property="dataRegistrazione" />
</strong></span><br/>
<label><bean:message key="protocollo.protocollatore"/></label>: <span><strong>

<bean:write name="storiaProtocolloForm" property="protocollatore" />
</strong></span><br/>


<logic:notEmpty name="storiaProtocolloForm" property="versioniProtocollo">
<hr></hr>
<table summary="" cellpadding="2" cellspacing="2" border="1">
	<tr>
		<th>
			<span><bean:message key="protocollo.versione"/></span>
		</th>
		<th>
			<span><bean:message key="protocollo.data.versione"/></span>
		</th>	
		<th>
			<span><bean:message key="protocollo.userupdate"/></span>
		</th>
		<th>
			<span><bean:message key="protocollo.stato"/></span>
		</th>
<%--		<th>-->
<!--			<span><bean:message key="protocollo.data.versione"/></span>-->
<!--		</th>	--%>
<%--		<th>-->
<!--			<span><bean:message key="protocollo.userupdate"/></span>-->
<!--		</th>	--%>
		<th>
			<span><bean:message key="protocollo.oggetto"/></span>
		</th>
		<th>
			<span><bean:message key="protocollo.mittente"/></span>
		</th>	
		
		<logic:equal name="storiaProtocolloForm" property="flagTipo" value="I">
			<th>
			<span><bean:message key="protocollo.assegnatari"/></span>
			</th>
			</logic:equal>
	<th>
			<span><bean:message key="protocollo.motivazione"/></span>
		</th>	
		
				
	</tr>
	<tr>
		<td>

			<span>
				<html:link action="/page/protocollo/storia.do?versioneCorrente=true">
					<bean:write name="storiaProtocolloForm" property="versione"/>
				</html:link></span>
		</td>
		
		<td>
			<span><bean:write name="storiaProtocolloForm" property="dataRegistrazione" /></span>
		</td>
		<td>
			<span><bean:write name="storiaProtocolloForm" property="userUpdate" /></span>
		</td>
		<td>
			<span><bean:write name="storiaProtocolloForm" property="descrizioneStatoProtocollo" /></span>
		</td>
<%--		<td>-->
<!--			<span><bean:write name="storiaProtocolloForm" property="dataRegistrazione" /></span>-->
<!--		</td>--%>
<%--		<td>-->
<!--			<span><bean:write name="storiaProtocolloForm" property="userUpdate" /></span>-->
<!--		</td>--%>
		<td>
			<span><bean:write name="storiaProtocolloForm" property="oggetto" filter="false"/></span>
		</td>
		<td>
			<span><bean:write name="storiaProtocolloForm" property="cognomeMittente" /></span>
		</td>
		
		<logic:equal name="storiaProtocolloForm" property="flagTipo" value="I">
			<td>
				<span><bean:write name="storiaProtocolloForm" property="assegnatario" /></span>
			</td>		
		</logic:equal>
		<td>
			<span><bean:write name="storiaProtocolloForm" property="motivazione"/></span>
		</td>
		
	</tr>

	
	<bean:define id="versioniProtocollo" name="storiaProtocolloForm" property="versioniProtocollo" />
	<logic:iterate id="currentRecord" name="storiaProtocolloForm" property="versioniProtocollo">
	<tr>
		<td>
			<span>
				<html:link action="/page/protocollo/storia.do" paramId="versioneSelezionata" paramName="currentRecord" paramProperty="versione">
					<bean:write name="currentRecord" property="versione" />
				</html:link>
			</span>
		</td>
		<td>
			<span><bean:write name="currentRecord" property="dateUpdated" /></span>
		</td>
		<td>
			<span><bean:write name="currentRecord" property="userUpdated" /></span>
		</td>
		<td>
			<span><bean:write name="currentRecord" property="statoProtocollo" /></span>
		</td>
<%--		<td>-->
<!--			<span><bean:write name="currentRecord" property="dateUpdated" /></span>-->
<!--		</td>--%>
<%--		<td>-->
<!--			<span><bean:write name="currentRecord" property="userUpdated" /></span>-->
<!--		</td>--%>
		<td>
			<span><bean:write name="currentRecord" property="oggetto" filter="false"/></span>
		</td>
		<td>
			<span><bean:write name="currentRecord" property="cognomeMittente" /></span>
		</td>
		
		<logic:equal name="storiaProtocolloForm" property="flagTipo" value="I">
		<td>			
				<span><bean:write name="currentRecord" property="assegnatario" /></span>
		</td>	
		
		</logic:equal>
		<td>
			<span><bean:write name="currentRecord" property="motivazione"/></span>
		</td>
	
	</tr>
	</logic:iterate>
	

</table>
</logic:notEmpty>

</div>
</html:form>


</eprot:page>




