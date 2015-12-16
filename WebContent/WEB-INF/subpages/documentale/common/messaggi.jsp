<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<html:xhtml />

<logic:messagesPresent message="true">
   <ul>
   <html:messages id="actionMessage" message="true">
      <li>
      <bean:write name="actionMessage"/>
      </li>
   </html:messages> 
   </ul>
</logic:messagesPresent>
