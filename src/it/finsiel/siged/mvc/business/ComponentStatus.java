/*
 * Created on 22-dic-2004
 *
 * 
 */
package it.finsiel.siged.mvc.business;

/**
 * @author Almaviva sud
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface ComponentStatus {

    public static int STATUS_OK = 1;

    public static int STATUS_ERROR = 2;

    public int getStatus();

    public void setStatus(int s);

}
