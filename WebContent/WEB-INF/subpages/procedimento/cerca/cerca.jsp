<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>


<html:xhtml />
<table >
	<tr> <td colspan="4">
	</td>
		</tr>   
	<tr>  
	 <td colspan="4">
	 <table >
	 <tr>
	    <td>
	  
			<label for="anno"><bean:message key="procedimento.annoprocedimento"/> &nbsp;:</label>
		</td>
		<td colspan="1">
			<html:text name="ricercaProcedimentoForm" property="anno" styleId="anno" size="10" maxlength="4"/>
	    </td>
	 
	    <td>&nbsp;
			<label for="numero"><bean:message key="procedimento.numeroProcedimento"/> &nbsp;:</label>
		</td>
		<td colspan="1">
		<html:text name="ricercaProcedimentoForm" property="numero" styleId="numero" size="10" maxlength="6"/>
	    </td>
	    </tr>
	    <tr>
  		<td>
	    	<label for="dataAvvioInizio">
				<bean:message key="procedimento.data.avvioda"/> &nbsp;:
			</label>
		</td>
		<td colspan="1">
			<html:text name="ricercaProcedimentoForm" property="dataAvvioInizio" styleId="dataAvvioInizio" size="10" maxlength="10" />
			<eprot:calendar textField="dataAvvioInizio" hasTime="false"/>
		</td>
	  		<td>
	    	<label for="dataAvvioFine">&nbsp;
				<bean:message key="procedimento.data.avvioa"/> &nbsp;:
			</label>
		</td>
		<td colspan="1">
			<html:text name="ricercaProcedimentoForm" property="dataAvvioFine" styleId="dataAvvioFine" size="10" maxlength="10" />
			<eprot:calendar textField="dataAvvioFine" hasTime="false"/>
    	</td>   
	</tr>
	    </table>
	     </td>
	</tr> 
	
	
	<tr>
	    <td >
	      <span><bean:message key="protocollo.ricerca.assegnatario.ufficioCorrente"/>: </span>
	    </td>
	    <td  colspan="3">
			<html:select property="ufficioRicercaId">
				<logic:equal name="ricercaProcedimentoForm" property="tuttiUffici" value="true">
		    	    <option value="0"><bean:message key="protocollo.ricerca.assegnatario.tutti"/></option>
		        </logic:equal>
		        <option value='<bean:write name="ricercaProcedimentoForm" property="ufficioCorrenteId" />'>
					<bean:write name="ricercaProcedimentoForm" property="ufficioCorrente.description" />
				</option>
		 	</html:select>
	      	<html:submit styleClass="button" property="ufficioPrecedenteAction" value=".." title="Vai al livello superiore" />
	    </td>
	  </tr>
	
	<logic:notEmpty name="ricercaProcedimentoForm" property="ufficiDipendenti">
		<tr>  
		    <td>
		      <label for="ufficioSelezionatoId"><bean:message key="protocollo.ricerca.assegnatario.ufficio"/> :</label>
		    </td>
		    <td>
		 	    <html:select property="ufficioSelezionatoId">
			        <option value="0"><bean:message key="procedimento.tutti"/></option>
			        <bean:define id="ufficiDipendenti" name="ricercaProcedimentoForm" property="ufficiDipendenti"/>
					<html:optionsCollection name="ufficiDipendenti" value="id" label="description" />
		 	    </html:select>
		      <html:submit styleClass="button" property="impostaUfficioAction" value="Vai" title="Imposta l'ufficio come corrente" />
		    </td>
		 </tr> 
	</logic:notEmpty> 
	
	
	<tr>
		<td colspan="3">
			<div><jsp:include page="/WEB-INF/subpages/procedimento/cerca/titolario.jsp"/></div>
		</td>	
	</tr>
	<tr>
	<td colspan="4">
	<table > 
	<tr>
  		<td colspan="1">
			<label for="statoId">
		 	<bean:message key="procedimento.stato" />&nbsp;: </label>
		</td>
		<td colspan="1">
			<html:select name="ricercaProcedimentoForm" property="statoId">
			<html:option value="-1"><bean:message key="procedimento.tutti"/></html:option>
			<html:optionsCollection name="ricercaProcedimentoForm" 
				property="statiProcedimentoCollection" value="id"
				label="description" />
			</html:select>
		</td>
 
		<td colspan="1"><label for="posizione"> &nbsp;&nbsp;&nbsp;&nbsp;<bean:message
			key="procedimento.posizione" />&nbsp;: </label>
		</td>
		<td colspan="1">
			<html:select name="ricercaProcedimentoForm" property="posizione">
			<html:option value="ALL"><bean:message key="procedimento.tutti"/></html:option>
			<html:optionsCollection name="ricercaProcedimentoForm"
				property="posizioniProcedimentoCollection" value="codice"
				label="description" />
			</html:select>
		</td>
		</tr>
		<tr>
  		<td colspan="1">
	    	<label for="dataEvidenzaInizio">
				<bean:message key="procedimento.data.evidenzada"/> &nbsp;:
			</label>
		</td>
		<td colspan="1">
			<html:text name="ricercaProcedimentoForm" property="dataEvidenzaInizio" styleId="dataEvidenzaInizio" size="10" maxlength="10" />
			<eprot:calendar textField="dataEvidenzaInizio" hasTime="false"/>
		</td>
	
  		<td  colspan="1">
	    	<label for="dataEvidenzaFine">&nbsp;&nbsp;&nbsp;
				<bean:message key="procedimento.data.evidenzaa"/>&nbsp;:
			</label>
		</td>
		<td colspan="1">
			<html:text name="ricercaProcedimentoForm" property="dataEvidenzaFine" styleId="dataEvidenzaFine" size="10" maxlength="10" />
			<eprot:calendar textField="dataEvidenzaFine" hasTime="false"/>
    	</td>   
	</tr>
		</table>
	 </td>	
	</tr> 
	
 	<tr>
	    <td >
	      <label for="oggettoProcedimento"><bean:message key="procedimento.oggetto"/></label>&nbsp;:
	      <br/>
				<span><html:link action="/page/unicode.do?campo=oggettoProcedimento" target="_blank" ><bean:message key="procedimento.segni"/></html:link></span>
	    </td>
		<td colspan="2">
	      <html:text name="ricercaProcedimentoForm" property="oggettoProcedimento" styleId="oggettoProcedimento" size="50" maxlength="255" />
	    </td>
	</tr>
	<tr>
		<td><label for="note"> <bean:message
			key="procedimento.note" />&nbsp;: </label>
			<br/>
				<span><html:link action="/page/unicode.do?campo=note" target="_blank" ><bean:message key="procedimento.segni"/></html:link></span></td>
		<td colspan="2"><html:textarea name="ricercaProcedimentoForm" property="note" styleId="note"
			rows="1" cols="50" /></td>
	</tr>
	
	<tr>
 		<td colspan="3">
     		<html:submit styleClass="submit" property="btnCerca" value="Cerca" alt="Cerca"/>
<%--		    <html:submit styleClass="button" property="btnAnnulla" value="Annulla" alt="Annulla i parametri di ricerca"/> 	--%>
				<html:reset styleClass="submit"	property="ResetAction" value="Annulla" alt="Annulla i parametri di ricerca" />
		 <logic:equal name="ricercaProcedimentoForm" property="indietroVisibile" value="true" >
		<html:submit styleClass ="submit" property="btnAnnulla" value="Indietro" alt="Indietro" />
	</logic:equal>	    
		</td>  
 	</tr> 
 
</table>	

  

