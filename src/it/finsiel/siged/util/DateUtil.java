package it.finsiel.siged.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * @author Almaviva sud.
 */
public class DateUtil {
    private DateUtil() {
    }

    public static int getYear(Date date) {
        int d = 0;
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            d = Integer.parseInt(df.format(date));
        }
        return d;
    }

    public static Date toDate(String date) {
        Date d = null;
        try {
            d = _data.parse(date);
        } catch (Exception e) {

        }
        return d;
    }
    
    public static Date toDateTime(String date) {
        Date d = null;
        try {
            d = _dataOra.parse(date);
        } catch (Exception e) {

        }
        return d;
    }

    public static boolean isData(long date) {
        return !"".equals(formattaData(date));
    }

    public static boolean isData(String date) {
        Date d = toDate(date);
        return d != null && _data.format(d).equals(date);
    }
    
    public static boolean isDataOra(String date) {
        Date d = toDateTime(date);
        return d != null && _dataOra.format(d).equals(date);
    }

    /*
     * Format a long into a Date using the pattern MM/dd/yyyy - HH:mm:ss return
     * empty if an error occurs
     */
    public static String formattaDataOra(long date) {
        try {
            return _dataOra.format(new Date(date));
        } catch (Exception e) {

        }
        return "";
    }

    /*
     * dataOra di tipo "dd/MM/yyyy - HH:mm:ss"
     */
    public static Date getDataOra(String dataOra) {
        try {
            return _dataOra.parse(dataOra);
        } catch (Exception e) {

        }
        return null;
    }

    public static String formattaData(long date) {
        try {
            return _data.format(new Date(date));
        } catch (Exception e) {

        }
        return "";
    }

    public static String getDataYYYYMMDD(long date) {
        try {
            return _data_yyyymmdd.format(new Date(date));
        } catch (Exception e) {

        }
        return "";
    }

    public static Date getDataFutura(long data, int anno) {
        try {

            return _dataCompatta.parse((String) String.valueOf(Integer
                    .parseInt(_dataCompatta.format(new Date(data))))
                    + anno);
        } catch (Exception e) {
            return null;
        }
    }

    private final static SimpleDateFormat _dataOra = new SimpleDateFormat(
            "dd/MM/yyyy - HH:mm");

    private final static SimpleDateFormat _data = new SimpleDateFormat(
            "dd/MM/yyyy");

    private final static SimpleDateFormat _dataCompatta = new SimpleDateFormat(
            "ddMMyyyy");

    private final static SimpleDateFormat _data_yyyymmdd = new SimpleDateFormat(
            "yyyy-MM-dd");

}