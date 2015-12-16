/*
 * Created on 10-dic-2004
 *
 * 
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.RegistroDelegate;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

/**
 * @author Almaviva sud
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class RegistroBO {

    public static Date getDataAperturaRegistro(RegistroVO registro) {
        if (!registro.getDataBloccata()) {
            Date oggi = DateUtils.truncate(new Date(), Calendar.DATE);
            if (!oggi.equals(registro.getDataAperturaRegistro())) {
                RegistroDelegate delegate = RegistroDelegate.getInstance();
                int registroId = registro.getId().intValue();
                delegate.setDataAperturaRegistro(registroId, oggi.getTime());
                registro.setDataAperturaRegistro(oggi);
            }
        }
        return registro.getDataAperturaRegistro();
    }

    public static int getRegistroUfficialeId(Collection registri) {
        int registroId = -1;
        Iterator i = registri.iterator();
        while (i.hasNext()) {
            RegistroVO registro = (RegistroVO) i.next();
            if (registro.getUfficiale()) {
                registroId = registro.getId().intValue();
                break;
            }
        }
        return registroId;
    }

    public static RegistroVO getUnicoRegistro(Map registri) {
        Iterator i = registri.values().iterator();
        if (i.hasNext())
            return (RegistroVO) i.next();
        return null;
    }

    public static boolean controllaPermessoRegistro(int registroId,
            Utente utente) {
        return utente.getRegistri().get(new Integer(registroId)) != null;
    }

    public static Collection getRegistriOrdinatiByFlagUfficiale(
            Collection registriUtente) {
        List list = new ArrayList();
        RegistroVO reg;
        for (Iterator i = registriUtente.iterator(); i.hasNext();) {
            reg = (RegistroVO) i.next();
            list.add(reg);
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                RegistroVO reg1 = (RegistroVO) obj1;
                RegistroVO reg2 = (RegistroVO) obj2;
                if ((reg1.getUfficiale() && reg2.getUfficiale())
                        || (!reg1.getUfficiale() && !reg2.getUfficiale()))
                    return 0;
                else if (reg1.getUfficiale() && !reg2.getUfficiale())
                    return -1;
                else
                    return 1;
                /*
                 * return Boolean.toString(reg2.getUfficiale())
                 * .compareToIgnoreCase( Boolean.toString(reg1.getUfficiale()));
                 */
            }
        };
        Collections.sort(list, c);
        return list;
    }

}
