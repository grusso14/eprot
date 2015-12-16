<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<div>
<table>
	<tr>
  	  <td colspan="6"> &nbsp; </td>
    </tr>
    <tr>
  	  <td> 
  	  	<label for="dataInizio">
			<bean:message key="report.datainizio"/><span class="obbligatorio"> * </span>:
		</label>
	  </td>
  	  <td>
  	  	<html:text property="dataInizio" size="10" styleId="dataInizio" styleClass="obbligatorio" maxlength="10" />
    	<eprot:calendar textField="dataInizio" hasTime="false"/>&nbsp;
  	  </td>
  	  <td>
  	 	 <label for="dataFine">
			<bean:message key="report.datafine"/><span class="obbligatorio"> * </span>:
		 </label>
  	  </td>
  	  <td>
  		  <html:text property="dataFine" styleId="dataFine" styleClass="obbligatorio" size="10" maxlength="10" />
          <eprot:calendar textField="dataFine" hasTime="false"/>
   		  &nbsp;
  	  </td>
  	  <td> 
  		  <label for="tipoProtocollo">
		  <bean:message key="protocollo.documento.tipo"/>:
		  </label>
  	  </td>
  	  <td> 
  	 	 <html:select name="reportRegistroForm" property="tipoProtocollo">
	     <option value=""><bean:write name="reportRegistroForm" property="tutti"/></option>
		 <html:optionsCollection name="tipiProtocollo" value="codice" label="descrizione"/>
	     <option value="<bean:write name="reportRegistroForm" property="mozioneUscita"/>"><bean:write name="reportRegistroForm" property="mozioneUscita" /></option>
	    </html:select>
  	  </td>
    </tr>
    <tr>
    <td colspan="6"><jsp:include page="uffici.jsp" /><br /> </td>
    </tr>
    <tr>
    <td colspan="6"><html:submit styleClass="submit" property="btnStampa" value="Stampa" alt="Stampa registro"/></td>
    </tr>
</table>
</div>


