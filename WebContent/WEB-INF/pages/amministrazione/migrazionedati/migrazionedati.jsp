<%@ page contentType="text/html;charset=ISO-8859-1" language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display"%>

<html:xhtml />

<html:html>
<body>
	<html:errors bundle="bundleErroriAmministrazione" />
	<html:form action="/install/migrazioneDati.do"
		enctype="multipart/form-data">
		<table summary="">
			<tr>
				<td class="label"><label title="File uffici " for="fileUffici"> <bean:message
					key="amministrazione.migrazione.fileuffici" /></label>&nbsp;:</td>
				<td><html:file property="fileUffici" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File utenti " for="fileUtenti"> <bean:message
					key="amministrazione.migrazione.fileutenti" /></label>&nbsp;:</td>
				<td><html:file property="fileUtenti" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File permessi " for="filePermessi">
				<bean:message key="amministrazione.migrazione.permessiutenti" /></label>&nbsp;:</td>
				<td><html:file property="filePermessi" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File utenti registro "
					for="fileUteRegistri"> <bean:message
					key="amministrazione.migrazione.utentiregistri" /></label>&nbsp;:</td>
				<td><html:file property="fileUteRegistri" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File titolario " for="fileTitolario">
				<bean:message key="amministrazione.migrazione.titolari" /></label>&nbsp;:</td>
				<td><html:file property="fileTitolario" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File protocolli "
					for="fileProtocolli"> <bean:message
					key="amministrazione.migrazione.protocolli" /></label>&nbsp;:</td>
				<td><html:file property="fileProtocolli" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File protocolli assegnatari"
					for="fileProtocolliAss"> <bean:message
					key="amministrazione.migrazione.protocolliassegnatari" /></label>&nbsp;:</td>
				<td><html:file property="fileProtocolliAss" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File protocolli destinatari"
					for="fileProtocolliDest"> <bean:message
					key="amministrazione.migrazione.protocollidestinatari" /></label>&nbsp;:</td>
				<td><html:file property="fileProtocolliDest" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File storia protocolli "
					for="fileStoriaProtocolli"> <bean:message
					key="amministrazione.migrazione.storiaprotocolli" /></label>&nbsp;:</td>
				<td><html:file property="fileStoriaProtocolli" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File faldoni "
					for="fileFaldoni"> <bean:message
					key="amministrazione.migrazione.faldoni" /></label>&nbsp;:</td>
				<td><html:file property="fileFaldoni" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File fascicoli "
					for="fileFascicoli"> <bean:message
					key="amministrazione.migrazione.fascicoli" /></label>&nbsp;:</td>
				<td><html:file property="fileFascicoli" size="50" /></td>
			</tr>
			
			
			<tr>
				<td class="label"><label title="File Storia fascicoli "
					for="fileStoriaFascicoli"> <bean:message
					key="amministrazione.migrazione.storiafascicoli" /></label>&nbsp;:</td>
				<td><html:file property="fileStoriaFascicoli" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File faldoni fascicoli "
					for="fileFaldoniFascicoli"> <bean:message
					key="amministrazione.migrazione.faldonifascicoli" /></label>&nbsp;:</td>
				<td><html:file property="fileFaldoniFascicoli" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File fascicoli protocollo "
					for="fileFascicoliProtocolli"> <bean:message
					key="amministrazione.migrazione.fascicoliprotocolli" /></label>&nbsp;:</td>
				<td><html:file property="fileFascicoliProtocolli" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File procedimenti "
					for="fileProcedimenti"> <bean:message
					key="amministrazione.migrazione.procedimenti" /></label>&nbsp;:</td>
				<td><html:file property="fileProcedimenti" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File storia procedimenti "
					for="fileStoriaProcedimenti"> <bean:message
					key="amministrazione.migrazione.storiaprocedimenti" /></label>&nbsp;:</td>
				<td><html:file property="fileStoriaProcedimenti" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File  procedimenti  faldoni "
					for="fileProcedimentiFaldone"> <bean:message
					key="amministrazione.migrazione.procedimentifaldoni" /></label>&nbsp;:</td>
				<td><html:file property="fileProcedimentiFaldone" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File  procedimenti  fascicoli "
					for="fileProcedimentiFascicoli"> <bean:message
					key="amministrazione.migrazione.procedimentifascicoli" /></label>&nbsp;:</td>
				<td><html:file property="fileProcedimentiFascicoli" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File referenti uffici "
					for="fileReferentiUfficio"> <bean:message
					key="amministrazione.migrazione.referentiuffici" /></label>&nbsp;:</td>
				<td><html:file property="fileReferentiUfficio" size="50" /></td>
			</tr>
			<tr>
				<td class="label"><label title="File tipi procedimenti "
					for="fileTipiProcedimenti"> <bean:message
					key="amministrazione.migrazione.tipiprocedimenti" /></label>&nbsp;:</td>
				<td><html:file property="fileTipiProcedimenti" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File titolari procedimenti "
					for="fileTitolariUffici"> <bean:message
					key="amministrazione.migrazione.titolariuffici" /></label>&nbsp;:</td>
				<td><html:file property="fileTitolariUffici" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File protocolli procedimenti "
					for="fileProtocolliProcedimenti"> <bean:message
					key="amministrazione.migrazione.protocolliprocedimenti" /></label>&nbsp;:</td>
				<td><html:file property="fileProtocolliProcedimenti" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File rubrica "
					for="fileRubrica"> <bean:message
					key="amministrazione.migrazione.rubrica" /></label>&nbsp;:</td>
				<td><html:file property="fileRubrica" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File Lista distribuzione "
					for="fileListaDistribuzione"> <bean:message
					key="amministrazione.migrazione.listadistribuzione" /></label>&nbsp;:</td>
				<td><html:file property="fileListaDistribuzione" size="50" /></td>
			</tr>
			
			<tr>
				<td class="label"><label title="File Rubrica Lista Distribuzione "
					for="fileRubricaListaDistribuzione"> <bean:message
					key="amministrazione.migrazione.rubricalistadistribuzione" /></label>&nbsp;:</td>
				<td><html:file property="fileRubricaListaDistribuzione" size="50" /></td>
			</tr>
			
			
			
			
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="2">
				<html:submit styleClass="submit" property="SalvaAction" value="Salva" alt="Migrazione dati" />
				Directory files <html:text property="dirFilesMigrazione" />
				<html:submit styleClass="submit" property="CaricaDaCartellaAction" value="Migrazione dati da cartella" alt="Migrazione dati" />
				</td>
			</tr>

		</table>
	</html:form>
</body>
</html:html>