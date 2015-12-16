<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>


<html:xhtml />

<jsp:include page="/WEB-INF/subpages/protocollo/common/uffici.jsp" />

<br />

<logic:notEmpty name="protocolloForm" property="assegnatari">
<table id="assegnatari">
  <tr>
    <th />
    <th><span><bean:message key="protocollo.assegnatari.competente"/></span></th>
    <th><span><bean:message key="protocollo.assegnatari.ufficio"/></span></th>
    <th><span><bean:message key="protocollo.assegnatari.utente"/></span></th>
  </tr>
	<logic:notEmpty name="protocolloForm" property="assegnatari">
	<logic:iterate id="ass" name="protocolloForm" property="assegnatari">
	  
	  <tr>
	    <td>
	    	<html:multibox property="assegnatariSelezionatiId">
	    		<bean:write name="ass" property="key"/>
	    	</html:multibox>
	    </td>    
	    <td>
	    <html:multibox property="assegnatariCompetenti" >
	    		<bean:write name="ass" property="key"/>
	    </html:multibox>
	    </td>
	    <td width="50%"><span title='<bean:write name="ass" property="descrizioneUfficio"/>'>
	    	<bean:write name="ass" property="nomeUfficio"/></span>
	    </td>
	    <td width="50%"><span><bean:write name="ass" property="nomeUtente"/></span></td>
	  </tr>
	</logic:iterate>
	<label for="messaggio"><bean:message key="protocollo.assegnatari.messaggio"/>:</label>
	<html:text styleId="messaggio" property="msgAssegnatarioCompetente" size="60" maxlength="250"></html:text>
	<br />	<br />
	</logic:notEmpty>

</table>
<br />
<html:submit styleClass="button" property="rimuoviAssegnatariAction" value="Rimuovi" title="Rimuove gli assegnatari selezionati dall'elenco" />
</logic:notEmpty>


<br />
