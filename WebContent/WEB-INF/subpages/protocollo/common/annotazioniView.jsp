<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<table summary="">
  <tr>  
    <td class="label">
      <span><bean:message key="protocollo.annotazioni.posizione"/>:</span>
    </td>  
    <td>
    	<span><strong><bean:write name="protocolloForm" property="posizioneAnnotazione"/></strong></span>
    </td>
  </tr>
  <tr>  
    <td class="label">
      <span><bean:message key="protocollo.annotazioni.chiave"/>:</span>
    </td>  
    <td>
    	<span><strong><bean:write name="protocolloForm" property="chiaveAnnotazione"/></strong></span>
    </td>
  </tr>
  <tr>  
    <td class="label">
      <label for="descrizioneAnnotazione"><bean:message key="protocollo.annotazioni.note"/>:</label>
    </td>  
    <td>   
      <html:textarea disabled="true" property="descrizioneAnnotazione" cols="50" rows="3"></html:textarea>
    </td>  
  </tr>
</table>