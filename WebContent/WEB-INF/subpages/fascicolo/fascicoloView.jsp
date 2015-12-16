<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<html:xhtml />
<div class="sezione">
	<span class="title"><strong><bean:message key="fascicolo.fascicolo"/></strong>
	</span>	 
	
	<table summary="">
		<logic:equal name="fascicoloForm" property="versioneDefault" value="false">
			<tr>  
		    	<td class="label">
		    		<label for="codice"><bean:message key="fascicolo.versione"/>:</label>
		    	</td>  
		    	<td>
			    	<span><strong><bean:write name="fascicoloForm" property="versione"/></strong></span>
		    	</td>  
			</tr>
		</logic:equal>

		<tr>  
	    	<td class="label">
	    		<label for="codice"><bean:message key="fascicolo.progressivo"/>:</label>
	    	</td>  
	    	<td>
		    	<span><strong><bean:write name="fascicoloForm" property="annoProgressivo"/></strong></span>
	    	</td>  
		</tr>
		<tr>  
	    	<td class="label">
	    		<label for="oggetto"><bean:message key="fascicolo.oggetto"/>:</label>
	    	</td>  
	    	<td>
		    	<span><strong><bean:write name="fascicoloForm" property="oggettoFascicolo" filter="false"/></strong></span>
	    	</td>  
		</tr>
	    <logic:notEmpty name="fascicoloForm" property="mittente">
			<tr>
		    	<td class="label">
		      		<label for="mittente"><bean:message key="fascicolo.ufficio"/></label>:
		    	</td>
			    <td>
					<span><strong><bean:write name="fascicoloForm" property="mittente.descrizioneUfficio" /></strong></span>
			    </td>
			</tr>
			<tr>
		    	<td class="labelEvidenziata">
		      		<label for="mittente"><bean:message key="fascicolo.referente"/></label>:
		    	</td>
			    <td>
					<span class="evidenziato"><strong><bean:write name="fascicoloForm" property="mittente.nomeUtente" /></strong></span>
			    </td>
			</tr>
        </logic:notEmpty>  
		<tr>
	    	<td class="label">
	      		<label for="titolario"><bean:message key="fascicolo.livelli.titolario"/></label>:
	    	</td>
		    <td>
	   		    <logic:notEmpty name="fascicoloForm" property="titolario">
					<span><strong><bean:write name="fascicoloForm" property="titolario.descrizione"/></strong></span>
	   		    </logic:notEmpty>
		    </td>
		</tr>
		<tr>
	    	<td class="label">
	      		<label for="note"><bean:message key="fascicolo.note"/></label>:
	    	</td>
		    <td>
				<span><strong><bean:write name="fascicoloForm" property="note" filter="false"/></strong></span>
		    </td>
		</tr>
		<tr>
	    	<td class="label">
	      		<label for="a"><bean:message key="fascicolo.anno"/></label>:
	    	</td>
		    <td>
				<span><strong><bean:write name="fascicoloForm" property="annoRiferimento" /></strong></span>
		    </td>
		</tr>
		
		<tr>  
	    	<td class="label">
	    		<span><bean:message key="fascicolo.responsabile"/>:</span>
	    	</td>
	    	<td>
		    	<span><strong><bean:write name="fascicoloForm" property="responsabile" /></strong></span>
	    	</td>  
		</tr>

		<tr>  
	    	<td class="label">
	    		<span><bean:message key="fascicolo.tipo"/>:</span>
	    	</td>
	    	<td>
		    	<span><strong><bean:write name="fascicoloForm" property="descrizioneTipoFascicolo" /></strong></span>
	    	</td>  
		</tr>
		<tr>  
	    	<td class="labelEvidenziata">
				<label for="stato"><bean:message key="fascicolo.stato"/>:</label>
	    	</td>  
	    	<td>
	    		<span class="evidenziato">
	    			<strong><bean:write name="fascicoloForm" property="descrizioneStato"/></strong>
	    		</span>
		    </td>
		</tr>
		<tr>  
	    	<td class="label">
				<label title="Data apertura fascicolo" for="dataApertura"><bean:message key="fascicolo.data.apertura"/></label>:
	    	</td>  
	    	<td>
				<span><strong><bean:write name="fascicoloForm" property="dataApertura"/></strong></span>
		    </td>
		</tr>
		<tr>  
	    	<td class="label">
				<label title="Data carico fascicolo" for="dataCarico"><bean:message key="fascicolo.data.carico"/></label>:
	    	</td>  
	    	<td>
				<span><strong><bean:write name="fascicoloForm" property="dataCarico"/></strong></span>
		    </td>
		</tr>
		<tr>  
	    	<td class="labelEvidenziata">
				<label title="Data evidenza fascicolo" for="dataEvidenza">
					<bean:message key="fascicolo.data.evidenza"/></label>:
	    	</td>  
	    	<td>
				<span class="evidenziato"><strong><bean:write name="fascicoloForm" property="dataEvidenza"/></strong></span>
		    </td>
		</tr>
		<tr>  
	    	<td class="label">
				<label title="Data ultimo movimento" for="dataUltimoMovimento"><bean:message key="fascicolo.data.movimento"/></label>:
	    	</td>  
	    	<td>
				<span><strong><bean:write name="fascicoloForm" property="dataUltimoMovimento"/></strong></span>
		    </td>
		</tr>

	<logic:greaterThan name="fascicoloForm" property="statoFascicolo" value="0">
		<tr>  
	    	<td class="label">
				<label title="Data chiusura fascicolo" for="dataChiusura"><bean:message key="fascicolo.data.chiusura"/></label>:
	    	</td>  
	    	<td>
				<span><strong><bean:write name="fascicoloForm" property="dataChiusura" /></strong></span>
		    </td>
		</tr>
	</logic:greaterThan>

	<logic:equal name="fascicoloForm" property="statoFascicolo" value="3">
		<tr>  
	    	<td class="label">
				<label title="Data scarto fascicolo" for="dataScarto"><bean:message key="fascicolo.data.scarto"/></label>:
	    	</td>  
	    	<td>
				<span><strong><bean:write name="fascicoloForm" property="dataScarto" /></strong></span>
		    </td>
		</tr>
		<tr>  
	    	<td class="label">
				<label title="Destinazione scarto" for="destinazioneScarto"><bean:message key="fascicolo.destinazione.scarto"/></label>:
	    	</td>  
	    	<td>
				<span><strong><bean:write name="fascicoloForm" property="propostaScarto" /></strong></span>
		    </td>
		</tr>
	</logic:equal>
	<logic:notEmpty name="fascicoloForm" property="collocazioneValore1">	
		<tr>	 
			<td class="label">
   			<label title="Collocazione" for="collocazioneValore1">
   				<bean:write name="fascicoloForm" property="collocazioneLabel1" />
   			</label>:
			</td>  
			<td>
				<span><strong><bean:write name="fascicoloForm" property="collocazioneValore1" /></strong></span>
			</td>
		</tr>			
  		</logic:notEmpty>
	<logic:notEmpty name="fascicoloForm" property="collocazioneValore2">	
		<tr>	 
			<td class="label">
   			<label title="Collocazione" for="collocazioneValore2">
   				<bean:write name="fascicoloForm" property="collocazioneLabel2" />
   			</label>:
			</td>  
			<td>
				<span><strong><bean:write name="fascicoloForm" property="collocazioneValore2" /></strong></span>
			</td>
		</tr>			
  		</logic:notEmpty>
	<logic:notEmpty name="fascicoloForm" property="collocazioneValore3">	
		<tr>	 
			<td class="label">
   			<label title="Collocazione" for="collocazioneValore3">
   				<bean:write name="fascicoloForm" property="collocazioneLabel3" />
   			</label>:
			</td>  
			<td>
				<span><strong><bean:write name="fascicoloForm" property="collocazioneValore3" /></strong></span>
			</td>
		</tr>			
  		</logic:notEmpty>
	<logic:notEmpty name="fascicoloForm" property="collocazioneValore4">	
		<tr>	 
			<td class="label">
   			<label title="Collocazione" for="collocazioneValore4">
   				<bean:write name="fascicoloForm" property="collocazioneLabel4" />
   			</label>:
			</td>  
			<td>
				<span><strong><bean:write name="fascicoloForm" property="collocazioneValore4" /></strong></span>
			</td>
		</tr>			
  		</logic:notEmpty>
		</table>
</div>
