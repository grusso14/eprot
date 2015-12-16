/*
 * Created on 2-dic-2004
 *
 * 
 */
package it.finsiel.siged.util;

import org.apache.log4j.Logger;

/**
 * @author p_finsiel
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class NumberUtil {

    static Logger logger = Logger.getLogger(NumberUtil.class.getName());

    public static int getInt(String n) {
        try {
            return Integer.parseInt(n);
        } catch (NumberFormatException ne) {
            return -1;
        }
    }

    public static boolean isInteger(String n) {
        try {
            Integer.parseInt(n);
            return true;
        } catch (NumberFormatException ne) {
            return false;
        }
    }

}
