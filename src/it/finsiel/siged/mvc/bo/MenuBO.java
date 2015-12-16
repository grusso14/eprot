/*
 * Created on 25-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.organizzazione.Menu;
import it.finsiel.siged.mvc.presentation.helper.MenuView;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MenuBO {

    public static void aggiungiMenu(String prefix, Menu menu,
            Collection menuLista) {
        for (Iterator i = menu.getChildren().iterator(); i.hasNext();) {
            Menu child = (Menu) i.next();
            if (child.getChildren().size() == 0) {
                MenuView menuView = new MenuView();
                menuView.setId(child.getValueObject().getId().intValue());
                menuView.setDescrizione(prefix
                        + child.getValueObject().getTitle());
                menuLista.add(menuView);
            } else {
                aggiungiMenu(prefix + child.getValueObject().getTitle() + "/",
                        child, menuLista);
            }
        }

    }

}