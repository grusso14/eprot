<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml /> <label for="dataNascita"></label>
<html:text property="dataNascita" styleId="dataNascita" size="10" maxlength="10" />
<eprot:calendar textField="dataNascita" hasTime="false"/>
