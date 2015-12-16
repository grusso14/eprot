/*
 * Created on 10-dic-2004
 *
 * 
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Almaviva sud
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class OrganizzazioneBO {

    public static HashMap getUfficiUtente(ArrayList ufficiIds) {
        Organizzazione org = Organizzazione.getInstance();
        Map uffici = new HashMap(2);
        ufficiIds.trimToSize();
        Iterator i = ufficiIds.iterator();

        if (ufficiIds.size() == 0)
            return new HashMap(uffici);
        while (i.hasNext()) {
            Ufficio u = org.getUfficio(((Integer) i.next()).intValue());
            uffici.put(u.getValueObject().getId(), u.getValueObject());
        }
        return new HashMap(uffici);
    }
}
