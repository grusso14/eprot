/*
 * Created on 10-dic-2004
 *
 * 
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Almaviva sud
 * 
 */
public final class UfficioBO {

    public static UfficioVO getUnicoUfficio(Map uffici) {
        Iterator i = uffici.values().iterator();
        if (i.hasNext())
            return (UfficioVO) i.next();
        return null;
    }

    public static boolean controllaPermessoUfficio(int ufficioId, Utente utente) {
        return (utente.getUfficioVOInUso() != null && utente
                .getUfficioVOInUso().getTipo().equals(
                        UfficioVO.UFFICIO_CENTRALE))
                || utente.getUffici().get(new Integer(ufficioId)) != null;

    }

    public static Collection getUfficiOrdinati(Collection ufficiSource) {
        List list = new ArrayList();
        Ufficio uff;
        for (Iterator i = ufficiSource.iterator(); i.hasNext();) {
            uff = new Ufficio((UfficioVO) i.next());
            list.add(uff.getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UfficioVO uff1 = (UfficioVO) obj1;
                UfficioVO uff2 = (UfficioVO) obj2;
                return uff1.getDescription().compareToIgnoreCase(
                        uff2.getDescription());
            }
        };
        Collections.sort(list, c);
        return list;
    }

}
