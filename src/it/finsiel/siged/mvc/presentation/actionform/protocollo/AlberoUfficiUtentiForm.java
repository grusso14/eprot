package it.finsiel.siged.mvc.presentation.actionform.protocollo;

import java.util.Collection;

public interface AlberoUfficiUtentiForm extends AlberoUfficiForm {

    public int getUtenteSelezionatoId();

    public void setUtenteSelezionatoId(int utenteSelezionatoId);

    public Collection getUtenti();

    public void setUtenti(Collection utenti);
}