<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<logic:notEmpty name="protocolloForm" property="protocolliAllacciati">
<p>
  <logic:iterate id="currentRecord" property="protocolliAllacciati" name="protocolloForm">
  	<span><bean:write name="currentRecord" property="allaccioDescrizione" /></span>&nbsp;&nbsp;
  </logic:iterate>
</p>
</logic:notEmpty>
