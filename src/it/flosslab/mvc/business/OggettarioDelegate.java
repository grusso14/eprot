/*
*
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
*
* This file is part of e-prot 1.1 software.
* e-prot 1.1 is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
* Version: e-prot 1.1
*/
package it.flosslab.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.business.IdentificativiDelegate;
import it.finsiel.siged.rdb.JDBCManager;
import it.flosslab.mvc.presentation.integration.OggettarioDAO;
import it.flosslab.mvc.vo.OggettoVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

public class OggettarioDelegate {

    private static Logger logger = Logger.getLogger(OggettarioDelegate.class
            .getName());

    private OggettarioDAO oggettarioDAO = null;

    private static OggettarioDelegate delegate = null;

    private OggettarioDelegate() {
        // Connect to DAO
        try {
            if (oggettarioDAO == null) {
            	oggettarioDAO = (OggettarioDAO) DAOFactory
                        .getDAO(Constants.OGGETTARIO_DAO_CLASS);

                logger.debug("oggettoDAO instantiated:"
                        + Constants.OGGETTARIO_DAO_CLASS);

            }
        } catch (Exception e) {
            logger.error("Exception while connecting to UserDAOjdbc!!", e);
        }

    }

    public static OggettarioDelegate getInstance() {
        if (delegate == null)
            delegate = new OggettarioDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.OGGETTARIO_DELEGATE;
    }
    
    public OggettoVO salvaOggetto(OggettoVO oggettoVO) throws DataException, SQLException {
        JDBCManager jdbcManager = new JDBCManager();
        Connection conn = jdbcManager.getConnection();
        int id = 0;
        if (oggettoVO.getId() != null){
        	id =new Integer(oggettoVO.getId());
        }
        if(id > 0){
        	// TODO update oggettoVO = oggettarioDAO.updateTitolo(conn, titoloVO);
        } else {
        	oggettoVO.setId(IdentificativiDelegate.getInstance().getNextId(conn,
                    NomiTabelle.OGGETTI));
        	oggettarioDAO.newOggetto(conn, oggettoVO);
        }
        return oggettoVO;
    }
    
    
    public OggettoVO deleteOggetto(OggettoVO oggettoVO) throws DataException, SQLException {
        JDBCManager jdbcManager = new JDBCManager();
        int id =new Integer(oggettoVO.getId());
        Connection conn = jdbcManager.getConnection();
    	oggettarioDAO.deleteOggetto(conn, id);
        return oggettoVO;
    }
    
    public Collection getOggetti() {

        try {
            return oggettarioDAO.getOggetti();
        } catch (DataException de) {
            logger
                    .error("OggettarioDelegate: failed getting getOggetti: ");
            return null;
        }
    }
    

    

}