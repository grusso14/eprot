<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>

<html:xhtml />

<%! String[] sezioni = {"Protocolli", "Documenti", "Faldoni", "Procedimenti"};
%>
<%  request.setAttribute("sezioni", sezioni); %>
<bean:define id="sezioneVisualizzata" name="fascicoloForm" property="sezioneVisualizzata" />
  <span class="hidden">Sezione corrente: 
		<strong><bean:write name="fascicoloForm" property="sezioneVisualizzata" /></strong>
  </span>
  <br class="hidden" />
  <span class="hidden">Sezioni: </span>
<div id="link-sezioni">


<logic:notEmpty name="sezioni">
<bean:define id="sezioni" name="sezioni" />
<logic:iterate id="sez" name="sezioni">

	<logic:equal name="sez" value="Protocolli" >
		<logic:equal name="sezioneVisualizzata" value="Protocolli" >
			<html:submit property="sezioneVisualizzata" value="Protocolli" styleClass="corrente" />
		</logic:equal>
		<logic:notEqual name="sezioneVisualizzata" value="Protocolli" >
			<html:submit property="sezioneVisualizzata" value="Protocolli" styleClass="" />
		</logic:notEqual>	
	</logic:equal>
	<logic:equal name="sez" value="Documenti" >
		<logic:equal name="sezioneVisualizzata" value="Documenti" >
			<html:submit property="sezioneVisualizzata" value="Documenti" styleClass="corrente" />
		</logic:equal>
		<logic:notEqual name="sezioneVisualizzata" value="Documenti" >
			<html:submit property="sezioneVisualizzata" value="Documenti" styleClass="" />
		</logic:notEqual>	
	</logic:equal>
	<logic:equal name="sez" value="Faldoni" >
		<logic:equal name="sezioneVisualizzata" value="Faldoni" >
			<html:submit property="sezioneVisualizzata" value="Faldoni" styleClass="corrente" />
		</logic:equal>
		<logic:notEqual name="sezioneVisualizzata" value="Faldoni" >
			<html:submit property="sezioneVisualizzata" value="Faldoni" styleClass="" />
		</logic:notEqual>	
	</logic:equal>
	<logic:equal name="sez" value="Procedimenti" >
		<logic:equal name="sezioneVisualizzata" value="Procedimenti" >
			<html:submit property="sezioneVisualizzata" value="Procedimenti" styleClass="corrente" />
		</logic:equal>
		<logic:notEqual name="sezioneVisualizzata" value="Procedimenti" >
			<html:submit property="sezioneVisualizzata" value="Procedimenti" styleClass="" />
		</logic:notEqual>	
	</logic:equal>
</logic:iterate>
</logic:notEmpty>
</div>
