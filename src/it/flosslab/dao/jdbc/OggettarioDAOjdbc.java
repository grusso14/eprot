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
package it.flosslab.dao.jdbc;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.rdb.JDBCManager;
import it.flosslab.mvc.presentation.integration.OggettarioDAO;
import it.flosslab.mvc.vo.OggettoVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class OggettarioDAOjdbc implements OggettarioDAO {
    static Logger logger = Logger.getLogger(OggettarioDAOjdbc.class
            .getName());

    private JDBCManager jdbcMan = new JDBCManager();

    
    private static final String INSERT_OGGETTO = "INSERT INTO oggetti (id, descrizione) VALUES (?,?)";
    private static final String DELETE_OGGETTO = "DELETE FROM oggetti WHERE id=?";
    private static final String GET_OGGETTO = "SELECT * FROM oggetti WHERE descrizione=?";
    public final static String SELECT_OGGETTI = "SELECT * FROM oggetti";

    public OggettoVO newOggetto(Connection conn, OggettoVO oggettoVO)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int id = new Integer(oggettoVO.getId());
        try {
            if (conn == null) {
                logger.warn("newOggetto() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(GET_OGGETTO);
            pstmt.setString(1, oggettoVO.getDescrizione());
            rs=  pstmt.executeQuery();
            if(!rs.next()){
            	pstmt.close();
                pstmt = conn.prepareStatement(INSERT_OGGETTO);
                pstmt.setInt(1, id);
                pstmt.setString(2, oggettoVO.getDescrizione());
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("Save Titolo", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return oggettoVO;
    }
    
    public void deleteOggetto(Connection conn, int id)throws DataException {
		PreparedStatement pstmt = null;
		try {
		    if (conn == null) {
		        logger.warn("deleteOggetto() - Invalid Connection :" + conn);
		        throw new DataException(
		                "Connessione alla base dati non valida.");
		    }
		        pstmt = conn.prepareStatement(DELETE_OGGETTO);
		        pstmt.setInt(1, id);
		        pstmt.executeUpdate();
		
		} catch (Exception e) {
		    logger.error("Save Titolo", e);
		    throw new DataException("error.database.cannotsave");
		} finally {
		    jdbcMan.close(pstmt);
		}
    }
    
    
	public Collection getOggetti() throws DataException {
		 Collection oggetti = new ArrayList();
	        Connection connection = null;
	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        try {
	            connection = jdbcMan.getConnection();
	            pstmt = connection.prepareStatement(SELECT_OGGETTI);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	                OggettoVO oggettoVO = new OggettoVO();
	                oggettoVO.setId(rs.getInt("id"));
	                oggettoVO.setDescrizione(rs.getString("descrizione"));
	                oggetti.add(oggettoVO);
	            }
	        } catch (Exception e) {
	            logger.error("Load getTitoliDestinatari", e);
	            throw new DataException("Cannot load getElencoTitoliDestinatari");
	        } finally {
	        	jdbcMan.closeAll(rs, pstmt, connection);
	        }
	        return oggetti;
	}    

};