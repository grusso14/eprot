<%@ page contentType="text/html;charset=iso-8859-1" language="java" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="/WEB-INF/eprot.tld" prefix="eprot" %>
<html:xhtml />
<eprot:page title="Protocollo in uscita">
<bean:define id="baseUrl" value="/page/protocollo/uscita/documento.do" scope="request" />


<html:form action="/page/protocollo/uscita/documento.do" enctype="multipart/form-data">

<div id="protocollo">

<div id="protocollo-errori">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/errori.jsp" />
</div>

<br class="hidden" />

<div>
	<bean:message key="campo.obbligatorio.msg" />
	<br class="hidden" />
</div>

<jsp:include page="/WEB-INF/subpages/protocollo/common/datiProtocollo.jsp" />

<br class="hidden" />

<jsp:include page="/WEB-INF/subpages/protocollo/common/datiDocumento.jsp" />

<br class="hidden" />

<div class="sezione">
<bean:define id="sezioneVisualizzata" name="protocolloForm" property="sezioneVisualizzata" />
<jsp:include page="/WEB-INF/subpages/protocollo/common/link-sezioni.jsp" />

	<logic:equal name="sezioneVisualizzata" value="Mittente">
    <jsp:include page="/WEB-INF/subpages/protocollo/uscita/mittente.jsp" />
	</logic:equal>

	<logic:match name="sezioneVisualizzata" value="Allegati">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/allegati.jsp" />
	</logic:match>

	<logic:match name="sezioneVisualizzata" value="Allacci">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/allacci.jsp" />
	</logic:match>

	<logic:match name="sezioneVisualizzata" value="Destinatari">
    <jsp:include page="/WEB-INF/subpages/protocollo/uscita/destinatari.jsp" />
	</logic:match>

	<logic:equal name="sezioneVisualizzata" value="Annotazioni">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/annotazioni.jsp" />
	</logic:equal>

	<logic:equal name="sezioneVisualizzata" value="Titolario">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/titolario.jsp" />
	</logic:equal>

	<logic:match name="sezioneVisualizzata" value="Fascicoli">
    <jsp:include page="/WEB-INF/subpages/protocollo/common/datiFascicoli.jsp" />
	</logic:match>

	<logic:match name="sezioneVisualizzata" value="Procedimenti">
    	<jsp:include page="/WEB-INF/subpages/protocollo/common/datiProcedimenti.jsp" />
	</logic:match>
</div>

<div>

<logic:equal name="protocolloForm" property="protocolloId" value="0">
  <html:submit styleClass="submit" property="salvaAction" value="Registra" alt="Salva protocollo" />
  <html:submit styleClass="submit" property="annullaAction" value="Annulla" alt="Annulla l'operazione" />
</logic:equal>  


<logic:notEqual name="protocolloForm" property="protocolloId" value="0">
  <html:submit styleClass="submit" property="salvaAction" value="Salva" alt="Salva protocollo" />
  <html:submit styleClass="submit" property="annullaAction" value="Annulla" alt="Annulla l'operazione" />
</logic:notEqual> 



</div>

</div>

</html:form>

</eprot:page>