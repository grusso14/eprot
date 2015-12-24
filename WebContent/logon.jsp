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

<table id="layout" summary="">
<tr>
  <td>
  <div id="header" style="border: 1px solid #000; border-radius:5px;">
  	<img  title="eProtrotocollo" align="center" border="0" src="<html:rewrite page='/images/logo/logoEprot.gif'/>">    
  </div>  
  <hr />
  
  <logic:equal name="ORGANIZZAZIONE_ROOT" property="valueObject.flagLdap" value="1">
  <tr>
  	<td colspan="2" align="left"><span>Autenticazione su server LDAP</span></td>
  </tr>  
  </logic:equal>

	<html:errors/>


	<html:form action="/logon.do" focus="username">
	   <div id="header" style="border: 1px solid #000; border-radius:5px;">
		<table>
		  <tr>
		    <th align="right">
		      <label for="username"><bean:message key="prompt.username"/></label>:
		    </th>
		    <td>
		      <html:text styleId="username" property="username" size="18" maxlength="32"/>
		    </td>
		  </tr>
		
		  <tr>
		    <th align="right">
		      <label for="password"><bean:message key="prompt.password"/></label>:
		    </th>
		    <td>
		      <html:password styleId="password" property="password" size="18" maxlength="20" redisplay="false" />
		    </td>
		  </tr>
		  <logic:notEmpty scope="request" name="mostra_forzatura">
		  <tr>
		    <th align="right">
		      <label for="forzatura"><bean:message key="prompt.forzatura"/></label>:
		    </th>
		    <td>
		      <html:checkbox styleId="forzatura" property="forzatura" />
		    </td>
		  </tr>
		  </logic:notEmpty>
		  <tr><td> </td></tr>
		  <tr>
		    <td> </td>
		    <td>
		      <html:submit styleClass="submit" property="login" value="Log in"/>
		      <html:reset styleClass="submit" />
		    </td>
		  </tr>
		</table>
  </td>
</tr>
		</table>
       </div> 
</html:form>
</html:html>
