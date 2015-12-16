<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>

<html:xhtml />

<eprot:page title="Tipi procedimento">
<div id="protocollo-errori">
  <html:errors bundle="bundleErroriProtocollo" />
</div>

<html:form action="page/amministrazione/tipiProcedimento.do">
<table summary="">
	<tr>  
		<td class="label">
			<label for="descrizione"><bean:message bundle="bundleMessaggiAmministrazione" key="descrizione"/></label>
			<span class="obbligatorio"> * </span>:
		</td>  
		
		<td>
			<html:text styleClass="obbligatorio" property="descrizione" styleId="descrizione" size="60" maxlength="254"></html:text>
		</td>  
	</tr>
	<tr>  
	   	<td class="label">
	   	<html:hidden property="idTipo"/>
	   	 
	   		<label for="descrizioneDoc"><bean:message key="amministrazione.tipoProcedimento.ufficio"/></label>:
	   	</td>  
	   	<td>
 
  	<logic:greaterThan value="0" name="tipoProcedimentoForm" property="idTipo">


	<span>
 
 		<bean:write name="tipoProcedimentoForm" property="ufficio" /> 
	</span>
	
	</logic:greaterThan>

	<logic:equal value="0" name="tipoProcedimentoForm" property="idTipo">
	
	   	<logic:notEmpty name="tipoProcedimentoForm" property="uffici">
		<p>
  		<logic:iterate id="currentRecord" name="tipoProcedimentoForm" property="uffici" >
				<html:multibox property="idUffici"><bean:write name="currentRecord" property="id"/>
				</html:multibox>
				<span>
					<bean:write name="currentRecord" property="description" />
				</span>
  				<br/>
  		</logic:iterate>
		</p>
		</logic:notEmpty>
	</logic:equal>

   	</td>  
	</tr>
	<tr>
		<td></td>
		<td>		
			<logic:greaterThan value="0" name="tipoProcedimentoForm" property="idTipo">
				<html:submit styleClass="submit" property="btnModifica" value="Modifica" title="Modifica i dati del tipo documento" />
				<html:submit styleClass="submit" property="btnCancella" value="Cancella" title="Cancella il tipo documento"/>
			</logic:greaterThan>
			<logic:equal value="0" name="tipoProcedimentoForm" property="idTipo">
				<html:submit styleClass="submit" property="btnConferma" value="Salva" title="Inserisce il nuovo tipo documento" />
			</logic:equal>
			<html:submit styleClass="submit" property="btnAnnulla" value="Annulla" title="Annulla l'operazione" />
		</td>
	</tr>
</table>
</html:form>
</eprot:page>
