<%@ attribute name="albero_cartelle" required="true" type="javax.swing.tree.DefaultMutableTreeNode" %>
<%@ attribute name="cartellaId" required="false" type="java.lang.Integer" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="apps" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:xhtml />

<logic:notEmpty name="albero_cartelle">

	<bean:define id="cartellaId" name="cartellaId"/>
	<ul id="submenu_<%=cartellaId%>">

	<bean:define id="children" name="albero_cartelle" property="children"/>
	<c:forEach var="item" items="<%=children%>">
		<logic:equal name="item" property="userObject.leaf" value="true">
		<bean:define id="treeId" name="item" property="userObject.treeId"/>
		<bean:define id="treeDescription" name="item" property="userObject.treeDescription"/>
	<li id="menu_<%=treeId%>">
		<html:link title="<%= treeDescription%>"
				action="/cartelle.do?cartellaId=<%= treeId %>"><bean:write name="item" property="userObject.treeDescription"/></html:link>
	</li>
		</logic:equal>
		<logic:equal name="item" property="userObject.leaf" value="false">
			<logic:equal name="item" property="userObject.nodeSelected" value="true" >
				<li id="menu_<%=treeId%>" class="opened">
					<span id="menugroup_<%=treeId%>"><bean:write name="item" property="userObject.treeDescription" /></span>
					<apps:cartelle albero_cartelle="<%= children %>" cartellaId="<%= treeId%>" />
				</li>
			</logic:equal>
			<logic:equal name="item" property="userObject.nodeSelected" value="false" >
				<li id="menu_<%=treeId%>" class="closed">
					<span id="menugroup_<%=treeId%>"><bean:write name="item" property="userObject.treeDescription" /></span>
					<apps:cartelle albero_cartelle="<%=children%>" cartellaId="<%=treeId%>" />
				</li>
			</logic:equal>	
		</logic:equal>
	<script type="text/javascript">

	<%--
		initMenu(<bean:write name="item" property="userObject.treeIid" />);
	--%>	
	</script>
	</c:forEach>

	</ul>
</logic:notEmpty>	

