package it.finsiel.siged.mvc.vo;

import java.io.Serializable;

/**
 * <p>
 * Base class for all value objects.
 * </p>
 * 
 * @author Paolo Spadafora
 */
public class BaseVO implements Serializable {

    /**
     * @rdbPhysicalName ID
     * @rdbNotNull
     * @rdbSize 22
     * @rdbDigits 0
     * @rdbLogicalType DECIMAL
     * @rdbPrimaryKey
     */
    // Attributes
    private Integer id;

    private int returnValue;

    // Constructors
    public BaseVO() {
    }

    public BaseVO(Integer id) {
        this.id = id;
    }

    public BaseVO(int id) {
        this.id = new Integer(id);
    }

    // Getters
    public Integer getId() {
        return this.id;
    }

    public int getReturnValue() {
        return this.returnValue;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }

    public void setId(int id) {
        this.id = new Integer(id);
    }

    public void setId(String id) {
        this.id = str2Integer(id);
    }

    public void setReturnValue(int i) {
        this.returnValue = i;
    }

    // Utility

    protected Integer str2Integer(String str) {
        Integer tempInt = null;
        try {
            if (str != null)
                tempInt = Integer.valueOf(str);
        } catch (NumberFormatException e) {
        }
        return tempInt;
    }

    protected Boolean str2Boolean(String s) {
        return Boolean.valueOf(s);
    }
}
