<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!--<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>-->
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />
<html:errors  bundle="bundleErroriProtocollo" property="destinatario_nome_obbligatorio"/>
<html:errors  bundle="bundleErroriProtocollo" property="formato_data"/>
<div class="sezione">
	<span class="title"><strong>Destinatari</strong></span>	    
<table summary="">
	<tr>
    <td class="label">
      <span><bean:message key="protocollo.mittente.tipo"/> :</span>
    </td>
    <td>
      <table summary="">
        <tr>
          <td>
            <html:radio property="tipoDestinatario"
                  value="F" onclick="document.forms[0].submit()">
              <label for="personaFisica">Persona fisica</label>
            </html:radio>
          </td>
          <td>&nbsp;&nbsp;</td>
          <td>
            <html:radio property="tipoDestinatario"
                  value="G" onclick="document.forms[0].submit()">
              <label for="personaGiuridica">Persona giuridica</label>
            </html:radio>
          </td>
          <td>&nbsp;&nbsp;</td>
          <td>
            <script type="text/javascript"></script>
            <noscript>
              <div>
              <%--html:submit styleClass="button" value="Imposta" title="Imposta il tipo di Mittente"/--%>
              &nbsp;&nbsp;
              </div>
            </noscript>
			<html:submit styleClass="button" property="cercaDestinatari" value="Seleziona" title="Seleziona i destinatari della rubrica"/>            
          </td>
        </tr>
      </table>
    </td>
	</tr>
	<tr>
		<td class="label">
			<label for="nominativoDestinatario">
			<bean:message key="protocollo.destinatario.nominativo"/>
		    </label>
			<span class="obbligatorio">*</span> :
		</td>
		<td>
		 	<html:text property="nominativoDestinatario" styleClass="obbligatorio"></html:text>
			&nbsp;
			<html:submit styleClass="button" property="aggiungiDestinatario" value="Aggiungi" title="Aggiunge il destinatario alla lista"/>
		</td>
	</tr>
</table>

<logic:notEmpty name="documentoForm" property="destinatari">
<hr></hr>
<table summary="">
	<tr>
		<th>
		</th>		
		<th>
			<span>
			<bean:message key="protocollo.destinatario.tipo"/>
			</span>
		</th>		
		<th>
			<span>
			<bean:message key="protocollo.destinatario.nominativo"/>
			</span>
		</th>
	</tr>

	<logic:iterate id="currentRecord" name="documentoForm" property="destinatari">
	<tr>
		<td>
			<html:multibox property="destinatarioSelezionatoId">

				<bean:write name="currentRecord" property="destinatario"/>
			</html:multibox>
		</td>
		<td>
			<span>

				<bean:write name="currentRecord" property="flagTipoDestinatario"/>
			</span>
		</td>
		<td>
			<span>

				<bean:write name="currentRecord" property="destinatario"/>
			</span>
		</td>
	</tr>
	</logic:iterate>

</table>
</logic:notEmpty>

<p>

<logic:notEmpty name="documentoForm" property="destinatari">
  <html:submit styleClass="button" property="rimuoviDestinatari" value="Rimuovi" title="Rimuovi i destinatari selezionati dall'elenco dei destinatari"/>
</logic:notEmpty>  

</p>
</div>

