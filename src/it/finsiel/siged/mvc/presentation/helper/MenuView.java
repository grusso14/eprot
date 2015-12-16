/*
 * Created on 18-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.mvc.presentation.helper;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MenuView {
    int id;

    String descrizione;

    /**
     * @return Returns the descrizione.
     */
    public String getDescrizione() {
        return descrizione;
    }
    
    public String getShortDescrizione(){
    	String[] descrizioneSplitted = descrizione.split("/");
    	return descrizioneSplitted[descrizioneSplitted.length-1];
    }

    /**
     * @param descrizione
     *            The descrizione to set.
     */
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }
}
