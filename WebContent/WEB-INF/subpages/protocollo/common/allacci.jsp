<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<html:errors  bundle="bundleErroriProtocollo" property="sezione_allaccio"/>

<jsp:include page="/WEB-INF/subpages/protocollo/allacci/testataGestioneAllacci.jsp" />





<br />

<logic:notEmpty name="protocolloForm" property="protocolliAllacciati">
	<p>
  	<logic:iterate id="currentRecord" name="protocolloForm" property="protocolliAllacciati" >
	  	<logic:present name="currentRecord" property="protocolloAllacciatoId">
			<html:multibox property="allaccioSelezionatoId"><bean:write name="currentRecord" property="protocolloAllacciatoId"/>
			</html:multibox>
		</logic:present>
  		<span><bean:write name="currentRecord" property="allaccioDescrizione" /></span>
  		<br/>
  	</logic:iterate>
</p>
</logic:notEmpty>

<br />

<p>
<logic:notEmpty name="protocolloForm" property="protocolliAllacciati">
  <html:submit styleClass="button" property="rimuoviAllacciAction" value="Rimuovi" title="Rimuovi i protocolli selezionati dall'elenco degli allacci"/>
</logic:notEmpty>
  <html:submit styleClass="button" property="btnAllacci" value="Aggiungi" title="Cerca protocolli allacciabili"/>
</p>
