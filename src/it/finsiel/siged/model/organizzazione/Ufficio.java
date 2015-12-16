package it.finsiel.siged.model.organizzazione;

import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Ufficio {
    private UfficioVO valueObject;

    private Ufficio ufficioDiAppartenenza;

    private Map ufficiDipendenti = new HashMap(5);

    private Map utenti = new HashMap();

    private Map utentiReferenti = new HashMap();

    public Ufficio(UfficioVO vo) {
        this.setValueObject(vo);
    }

    public void setValueObject(UfficioVO vo) {
        this.valueObject = vo;
    }

    public UfficioVO getValueObject() {
        return valueObject;
    }

    public Collection getUfficiDipendenti() {
        return ufficiDipendenti.values();
    }

    public Ufficio getUfficioDipendente(int ufficioId) {
        return (Ufficio) ufficiDipendenti.get(new Integer(ufficioId));
    }

    public Ufficio getUfficioDiAppartenenza() {
        return ufficioDiAppartenenza;
    }

    public void setUfficioDiAppartenenza(Ufficio ufficioDiAppartenenza) {
        this.ufficioDiAppartenenza = ufficioDiAppartenenza;
        if (ufficioDiAppartenenza != null) {
            ufficioDiAppartenenza.ufficiDipendenti.put(
                    getValueObject().getId(), this);
        }
    }

    public void removeUfficioDipendente(Integer ufficioId) {
        if (ufficiDipendenti.containsKey(ufficioId))
            ufficiDipendenti.remove(ufficioId);
    }

    public Collection getUtenti() {
        return utenti.values();
    }

    public Utente getUtente(int utenteId) {
        return (Utente) utenti.get(new Integer(utenteId));
    }

    public void addUtente(Utente utente) {
        utenti.put(utente.getValueObject().getId(), utente);
    }

    public Collection getUtentiReferenti() {
        return utentiReferenti.values();
    }

    public Utente getUtenteReferente(int utenteId) {
        return (Utente) utentiReferenti.get(new Integer(utenteId));
    }

    public void addUtenteReferente(Utente utente) {
        utentiReferenti.put(utente.getValueObject().getId(), utente);
    }

    public String getListaUfficiDiscendentiId() {
        String ids = getValueObject().getId().toString();
        for (Iterator i = getUfficiDipendenti().iterator(); i.hasNext();) {
            Ufficio uff = (Ufficio) i.next();
            ids += ',' + uff.getListaUfficiDiscendentiId();
        }
        return ids;
    }

    /**
     * @return lista degli identificativi (separati da ',') di tutti gli uffici
     *         su cui l'utente è abilitato.
     */
    public String getListaUfficiDiscendentiId(Utente utente) {
        String ids = getValueObject().getId().toString();
        Organizzazione org = Organizzazione.getInstance();
        for (Iterator x = utente.getUffici().values().iterator(); x.hasNext();) {
            Ufficio uffPadre = org.getUfficio(((UfficioVO) x.next()).getId()
                    .intValue());
            ids += ',' + String.valueOf(uffPadre.getValueObject().getId());
            for (Iterator i = uffPadre.getUfficiDipendenti().iterator(); i
                    .hasNext();) {
                Ufficio uff = (Ufficio) i.next();
                ids += ',' + uff.getListaUfficiDiscendentiId();
            }
        }
        return ids;
    }

    /**
     * @return lista degli identificativi (separati da ',') di tutti gli uffici
     *         padri.
     */
    public String getListaUfficiPadriId() {
        String ids = getValueObject().getId().toString();
        Ufficio uff = getUfficioDiAppartenenza();
        while (uff != null) {
            ids += ',' + uff.getValueObject().getId().toString();
            uff = uff.getUfficioDiAppartenenza();
        }
        return ids;
    }

    public String getPath() {
        Ufficio uff = this;
        StringBuffer path = new StringBuffer();
        while (uff != null) {
            path.insert(0, '/')
                    .insert(0, uff.getValueObject().getDescription());
            uff = uff.getUfficioDiAppartenenza();
        }
        return path.toString();
    }

    public String toString() {
        return valueObject.toString();
    }

    public void removeReferenti() {
        if (utentiReferenti != null)
            utentiReferenti.clear();

    }
}