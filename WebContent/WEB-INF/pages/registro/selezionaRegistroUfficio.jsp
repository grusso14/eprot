<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<html:html>
<head>
  <title>e-Prot - <bean:message key="home.title"/></title>
  
  <link rel="stylesheet" type="text/css" href='<html:rewrite page="/style/login.css" paramScope="request" paramName="url" />' />
</head>
<body>
  <div id="header">
    <span class="title"><bean:message key="registro.title"/></span>
    <span class="aoo">
      - <strong>
      		
      		<bean:write name="ORGANIZZAZIONE_ROOT" property="valueObject.description" /> 
      	</strong>
    </span>
  </div>
  <hr />
	<html:errors />
	<html:form action="/selezionaRegistroUfficio.do" >
<table>
  <tr>
    <th align="right">
      <label for="ufficioId"><bean:message key="registro.ufficio"/></label>:
    </th>
    <td>
		  <html:select name="SelezionaRegistroUfficioForm" property="ufficioId">
		      <html:optionsCollection name="SelezionaRegistroUfficioForm" property="uffici" value="id" label="description"/>
		  </html:select>
    </td>
  </tr>
  <tr>
    <th align="right">
      <label for="registroId"><bean:message key="registro.registro"/></label>:
    </th>
    <td>
      <html:select  property="registroId">
          <html:optionsCollection  name="SelezionaRegistroUfficioForm" property="registri" value="id" label="descrizioneRegistro"/>
      </html:select>
    </td>
  </tr>
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td />
    <td>
		<html:submit styleClass="submit" property="buttonSubmit" value="Seleziona" ></html:submit>
    </td>
  </tr>
</table>
	</html:form>
</body>
</html:html>
