/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   DocDao.java 
 * Created:  10 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.exception.DataException;

import java.util.HashMap;

/**
 * @author roberto onnis
 *
 */
public interface DocDaoInterface {
	 public HashMap<Integer, String> getDocumenti() 
 	throws DataException;

}
