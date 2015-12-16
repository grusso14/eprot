<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />
<div class="sezione">
<span class="title"><strong><bean:message key="procedimento.procedimento" /></strong></span>

<table summary="" width="100%" >
  	<logic:greaterThan name="procedimentoForm" property="procedimentoId" value="0">
	  	<tr>
	    	<td class="label">
      		<span><bean:message key="procedimento.numeroprocedimento"/>: </span>&nbsp;
    		</td>
	    	<td>
		    	<span><strong>
		    	<bean:write name="procedimentoForm" property="numeroProcedimento" />
		    	</strong></span>
	    	</td>
	  	</tr>
  	</logic:greaterThan>
  	<tr>
    	<td class="label">
      		<span><bean:message key="procedimento.ufficio"/>: </span>&nbsp;
    	</td>
    	<td>
	    	<span><strong>
	    	<bean:write name="procedimentoForm" property="ufficioCorrentePath" />
	    	</strong></span>
    	</td>
  	</tr>
  	 
        <tr> 
            <td class="label">
      		<span><bean:message key="procedimento.titolario"/>: </span>&nbsp;
    		</td>
    		<logic:notEmpty name="procedimentoForm" property="titolario">
	    	<td>
				<span><strong><bean:write name="procedimentoForm" property="titolario.descrizione" /></strong></span> 
            </td> 
            </logic:notEmpty>
        </tr> 
		
	<tr>
	    <td class="label">
      		<span><bean:message key="procedimento.oggetto"/>: </span>&nbsp;
    		</td>
	    <td>
	    	<span><strong>
		    	<bean:write name="procedimentoForm" property="oggettoProcedimento" filter="false" />
		    </strong></span>	
	    </td> 
	</tr>
	
	<tr>
    	<td class="label">
      		<span><bean:message key="procedimento.note"/>: </span>&nbsp;
    		</td>
	    
    	<td>
	    	<span><strong>
	    	<bean:write name="procedimentoForm" property="note" filter="false" />
	    	</strong></span>
    	</td>
  	</tr>
	
	<tr>
		<td class="label">
      		<span><bean:message key="procedimento.stato"/>: </span>&nbsp;
    		</td>
		
	<logic:equal name="procedimentoForm" property="statoId" value="1">
	    <td>
			<span>
				<strong><bean:message key="procedimento.stato1"/></strong>
			</span>
	    </td>
	</logic:equal>    
	
	 
	<logic:equal name="procedimentoForm" property="statoId" value="2">
	    <td>
			<span>
				<strong><bean:message key="procedimento.stato2"/></strong>
			</span>
	    </td>
	</logic:equal> 
	<logic:equal name="procedimentoForm" property="statoId" value="3">
	    <td>
			<span>
				<strong><bean:message key="procedimento.stato3"/></strong>
			</span>
	    </td>
	</logic:equal> 
	<logic:equal name="procedimentoForm" property="statoId" value="4">
	    <td>
			<span>
				<strong><bean:message key="procedimento.stato4"/></strong>
			</span>
	    </td>
	</logic:equal>
	<logic:equal name="procedimentoForm" property="statoId" value="5">
	    <td>
			<span>
				<strong><bean:message key="procedimento.stato5"/></strong>
			</span>
	    </td>
	</logic:equal>
	</tr> 
	
	<tr>
		<td class="label">
      		<span><bean:message key="procedimento.finalita"/>: </span>&nbsp;
    		</td>
		
		
		
	<td>
		    	<span><strong>
		    	<bean:write name="procedimentoForm" property="tipoProcedimento" />
		    	</strong></span>
	    	</td>
	  </tr> 
	  
	  <tr>
		<td class="label">
      		<span><bean:message key="procedimento.numeroprotocollo"/>: </span>&nbsp;
    		</td>
		
	<td>
		    	<span><strong>
		    	<bean:write name="procedimentoForm" property="numeroProtocollo" />
		    	</strong></span>
	    	</td>
	  </tr>   	
	
	<tr>
		<td class="label">
      		<span><bean:message key="procedimento.posizione"/>: </span>&nbsp;
    		</td>
		
	<logic:equal name="procedimentoForm" property="posizione" value="A">
	    <td>
			<span>
				<strong><bean:message key="procedimento.posizione1"/></strong>
			</span>
	    </td>
	</logic:equal> 
	<logic:equal name="procedimentoForm" property="posizione" value="E">
	    <td>
			<span>
				<strong><bean:message key="procedimento.posizione2"/></strong>
			</span>
	    </td>
	</logic:equal>
	<logic:equal name="procedimentoForm" property="posizione" value="T">
	    <td>
			<span>
				<strong><bean:message key="procedimento.posizione3"/></strong>
			</span>
	    </td>
	</logic:equal>
	</tr>
	
	<tr>
		    <td class="label">
      		<span><bean:message key="procedimento.referente"/>: </span>&nbsp;
    		</td>
		    <td>
		    	<span><strong><bean:write name="procedimentoForm" property="nomeReferente" /></strong></span>	
		    </td> 
		</tr>
		
		<tr>
		    <td class="label">
      		<span><bean:message key="procedimento.responsabile"/>: </span>&nbsp;
    		</td>
		    <td>
		    	<span><strong><bean:write name="procedimentoForm" property="responsabile" /></strong></span>	
		    </td> 
		</tr>
	 
    <logic:greaterThan name="procedimentoForm" property="procedimentoId" value="0">
		<tr>
		    <td class="label">
      		<span><bean:message key="procedimento.dataavvio"/>: </span>&nbsp;
    		</td>
		    <td>
		    	<span><strong><bean:write name="procedimentoForm" property="dataAvvio" /></strong></span>	
		    </td> 
		</tr> 
		
		<tr>
		     <td class="label">
      		<span><bean:message key="procedimento.dataevidenza"/>: </span>&nbsp;
    		</td>
		    <td>
		    	<span><strong><bean:write name="procedimentoForm" property="dataEvidenza" /></strong></span>	
		    </td> 
		</tr> 
		
		<tr>
		     <td class="label">
      		<span><bean:message key="procedimento.versione"/>: </span>&nbsp;
    		</td>
		    <td>
		    	<span><strong><bean:write name="procedimentoForm" property="versione" /></strong></span>	
		    </td> 
		</tr> 
	</logic:greaterThan>
  	  	 	
</table>

</div>

