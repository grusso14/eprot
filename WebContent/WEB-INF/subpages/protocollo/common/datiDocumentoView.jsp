<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<div class="sezione">
<span class="title"><strong><bean:message key="protocollo.documento"/></strong></span>
<table summary="">
  <tr>
    <td class="label">
      <label for="tipoDocumentoId"><bean:message key="protocollo.documento.tipo"/></label>&nbsp;:
    </td>
    <td>
      <html:select styleClass="obbligatorio" disabled="true" property="tipoDocumentoId">
        <html:optionsCollection property="tipiDocumento" value="id" label="descrizione" />
      </html:select>
		&nbsp;&nbsp;
		<label for="protocolloRiservato"><bean:message key="protocollo.mittente.riservato"/></label>&nbsp;:
		<html:checkbox property="riservato" styleId="protocolloRiservato" disabled="true" />

    </td>
  </tr>
  <tr>
    <td class="label">
      	<span title="Data del documento"><bean:message key="protocollo.documento.data"/></span>&nbsp;:
    </td>
    <td>
    	<span><strong>
    	<bean:write name="protocolloForm" property="dataDocumento" />
    	</strong></span>&nbsp;
    </td>

    <logic:equal name="protocolloForm" property="flagTipo" value="I">
	    <td class="label">
	    	<span title="Data in cui il documento &egrave; stato ricevuto"><bean:message key="protocollo.documento.ricevuto"/> </span>:
	    </td>
	    <td>
			<span><strong>
			<bean:write name="protocolloForm" property="dataRicezione" />
			</strong></span>
		</td>    
	</logic:equal>

  </tr>
  <tr>
    <td class="label">
      <span><bean:message key="protocollo.oggetto"/></span>&nbsp;:
    </td>
    <td colspan="3">
	    <span><strong>
		<input type="hidden" value="<bean:write name="protocolloForm" property="oggetto"/>" name="oggetto"></input>
	    <bean:write name="protocolloForm" property="oggetto" filter="false" />
	    </strong></span>
    </td>
  </tr>
<logic:equal name="protocolloForm"  property= "documentoVisibile" value="false">
	<tr>
	    <td class="label">
	      <span><bean:message key="protocollo.documento.file"/></span>:
	    </td>
	    <td colspan="3">
			<span><strong>*** Riservato ***</strong></span>
		</td>
	</tr>
</logic:equal>
<logic:equal name="protocolloForm"  property= "documentoVisibile" value="true">
	<logic:notEmpty name="protocolloForm" property="documentoPrincipale.fileName">	
	  <tr>
	    <td class="label">
	      <span><bean:message key="protocollo.documento.file"/></span>:
	    </td>
	    <td colspan="3">
	    <logic:equal name="protocolloForm" property="flagTipo" value="I">
		  <html:link action="/page/protocollo/ingresso/documento.do" 
		             paramId="downloadDocumentoPrincipale" 
		             paramName="protocolloForm" 
		             paramProperty="documentoPrincipale.fileName" 
		             target="_blank"
		             title="Download File">
	        	<span><strong>
					<bean:write name="protocolloForm" property="documentoPrincipale.fileName" />
	        	</strong></span>
	      </html:link>
		</logic:equal>
		<logic:equal name="protocolloForm" property="flagTipo" value="U">
			<html:link action="/page/protocollo/uscita/documento.do" 
		             paramId="downloadDocumentoPrincipale" 
		             paramName="protocolloForm" 
		             paramProperty="documentoPrincipale.fileName" 
		             target="_blank"
		             title="Download File">
	        	<span><strong>
					<bean:write name="protocolloForm" property="documentoPrincipale.fileName" />
	        	</strong></span>
			</html:link>
		</logic:equal>    
	    </td>
	  </tr>
	</logic:notEmpty>  
</logic:equal>

</table>
</div>