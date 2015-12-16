/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   SearcherDelegate.java 
 * Created:  6 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.mvc.business;

import org.apache.log4j.Logger;

/**
 * @author roberto
 *
 */
public class SearcherDelegate {
	
	
    private static Logger logger = Logger
    .getLogger(SearcherDelegate.class.getName());
    
    private static SearcherDelegate searcher = null;
    
    private SearcherDelegate() {
    	
    }
    
    public static SearcherDelegate getInstance() {
        if (searcher == null)
        	searcher = new SearcherDelegate();
        return searcher;
    }

}
