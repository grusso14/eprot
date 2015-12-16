/*
 * Created on 2-dic-2004
 *
 * 
 */
package it.finsiel.siged.util;

import java.util.Vector;

/**
 * @author p_finsiel
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class StringUtil {

    public static String getString(long l) {
        return String.valueOf(l);
    }

    public static String formattaNumerProtocollo(long num) {
        String n = String.valueOf(num);
        while (n.length() < 13) {
            n = "0" + n;
        }
        return n;
    }

    public static String formattaNumeroProtocollo(String num, int lunghezza) {
        String n = num == null ? "" : num;
        while (n.length() < lunghezza) {
            n = "0" + n;
        }
        return n;
    }
    
    public static String formattaNumeroProcedimento(String num, int lunghezza) {
        String n = num == null ? "" : num;
        while (n.length() < lunghezza) {
            n = "0" + n;
        }
        return n;
    }

    public static String formattaNumeroFaldone(String num, int lunghezza) {
        String n = num == null ? "" : num;
        while (n.length() < lunghezza) {
            n = "0" + n;
        }
        return n;
    }

    public static String formattaNumeroProtocollo(String num) {
        String n = num;
        while (n.length() < 12) {
            n = "0" + n;
        }
        return n;
    }
 

    public static String formattaNumeroProtocolli(String num) {
        String n = num;
        while (n.length() < 7) {
            n = "0" + n;
        }
        return n;
    }
 
    
    public static String getStringa(String s) {
        if (s == null)
            return "";
        return s;
    }
    
    public static String[] split(String str,char x)
    {
            Vector v=new Vector();
            String str1=new String();
            for(int i=0;i<str.length();i++) {
                    if(str.charAt(i)==x){
                            v.add(str1);
                            str1=new String();
                    }
                    else{
                            str1+=str.charAt(i);
                    }
            }
            v.add(str1);
            String array[];
            array=new String[v.size()];
            for(int i=0;i<array.length;i++){
                if (v.elementAt(i).equals("")) {
                    array[i]=null;
                    
                }else {
                    array[i]=new String((String)v.elementAt(i));
            }
            }
            return array;
    }
}
