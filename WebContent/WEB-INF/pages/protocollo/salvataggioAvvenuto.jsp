<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:html>
<head>
    <title><bean:message key="protocolloingresso.title"/></title>
    <link rel="stylesheet" type="text/css" href="/siged/style/style.css" />
    <!-- <script type="text/javascript" src="../../script/calendar.js"></script> --> 
</head>

<body style='background-color: #ddd;' leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<jsp:include page="/page/subpages/header.jsp" />
<jsp:include page="/page/subpages/sidebar.jsp" />
<html:errors/>
<div id="body" style='background-color: white;'>
<jsp:include page="/page/subpages/navbar.jsp" />
<table border="0" width="100%" height="100%">
<tr>
<td>
	Il Protocollo &egrave; stato salvato.
</td>
</tr>	
</table>
</div>
<jsp:include page="/page/subpages/footer.jsp" />
</body>
</html:html>