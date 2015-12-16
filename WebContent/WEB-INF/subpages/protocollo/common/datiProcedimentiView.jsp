<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<logic:notEmpty name="protocolloForm" property="procedimentiProtocollo">
<div>
<ul>
	<logic:iterate id="proc" property="procedimentiProtocollo" name="protocolloForm">
		<li>
			<span>
		<bean:write name="proc" property="numeroProcedimento"/>-
			</span>
			<span>
		<bean:write name="proc" property="oggetto"/>
			</span>
		</li>
	</logic:iterate>
</ul>
</div>
</logic:notEmpty>
