<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<logic:notEmpty name="protocolloForm" property="fascicoliProtocollo">
<div>
<ul>
	<logic:iterate id="currentRecord" property="fascicoliProtocollo" name="protocolloForm">
		<li>
		<span>
		<logic:equal name="protocolloForm" property="flagTipo" value="I">
			<html:link action="/page/protocollo/ingresso/documento.do" paramName="currentRecord" paramId="fascicoloId" paramProperty="id">			
			<bean:write name="currentRecord" property="annoProgressivo"/>
			- 
			<bean:write name="currentRecord" property="oggetto"/>
			</html:link>
		</logic:equal>		
		<logic:notEqual name="protocolloForm" property="flagTipo" value="I">
			<html:link action="/page/protocollo/uscita/documento.do" paramName="currentRecord" paramId="fascicoloId" paramProperty="id">			
			<bean:write name="currentRecord" property="annoProgressivo"/>
			- 
			<bean:write name="currentRecord" property="oggetto"/>
			</html:link>
		</logic:notEqual>		

		</span>
		
		</li><br/>
	</logic:iterate>
</ul>
</div>
</logic:notEmpty>
