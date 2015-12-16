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
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.flosslab.mvc.presentation.integration.MittentiDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class MittentiDAOjdbc implements MittentiDAO {
    static Logger logger = Logger.getLogger(MittentiDAOjdbc.class
            .getName());

    private JDBCManager jdbcMan = new JDBCManager();

    
    private static final String INSERT_MITTENTE = "INSERT INTO protocollo_mittenti (mittente_id, indirizzo, email, cognome, nome, citta, provincia, protocollo_id, versione, codice_postale) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String GET_MITTENTI = "SELECT * FROM protocollo_mittenti WHERE protocollo_id=?";

   

	@Override
	public void saveMittente(Connection conn, SoggettoVO mittente, int idProtocollo, int idMittente) throws DataException {
		PreparedStatement pstmt = null;
		 try {
            if (conn == null) {
                logger.warn("newOggetto() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
                pstmt = conn.prepareStatement(INSERT_MITTENTE);
                pstmt.setInt(1, idMittente);
                pstmt.setString(2, mittente.getIndirizzo().getToponimo());
                pstmt.setString(3, mittente.getIndirizzoEMail());
                pstmt.setString(4, mittente.getCognome());
                pstmt.setString(5, mittente.getNome());
                pstmt.setString(6, mittente.getIndirizzo().getComune());
                pstmt.setInt(7,  mittente.getIndirizzo().getProvinciaId());
                pstmt.setInt(8, idProtocollo);
                pstmt.setInt(9, mittente.getVersione());
                pstmt.setString(10, mittente.getIndirizzo().getCap());
                pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Save Mittente", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        
		
	}




	@Override
	public List<SoggettoVO> getMittenti(Connection conn, int protocolloId) throws DataException {
		List<SoggettoVO> mittenti = new ArrayList<SoggettoVO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
           if (conn == null) {
               logger.warn("newOggetto() - Invalid Connection :" + conn);
               throw new DataException(
                       "Connessione alla base dati non valida.");
           }
		   pstmt = conn.prepareStatement(GET_MITTENTI);
		   pstmt.setInt(1, protocolloId);
		   rs = pstmt.executeQuery();
		   while(rs.next()){
			   SoggettoVO mittente = new SoggettoVO("F");
			   mittente.getIndirizzo().setToponimo(rs.getString("indirizzo"));
			   mittente.getIndirizzo().setComune(rs.getString("citta"));
			   mittente.getIndirizzo().setCap(rs.getString("codice_postale"));
			   mittente.getIndirizzo().setProvinciaId(rs.getInt("provincia"));
			   mittente.setNome(rs.getString("nome"));
			   mittente.setCognome(rs.getString("cognome"));
			   mittente.setIndirizzoEMail(rs.getString("email"));
			   mittenti.add(mittente);
		   }
		   
		}catch (Exception e) {
		    logger.error("Save Mittente", e);
		    throw new DataException("error.database.cannotsave");
		} finally {
		    jdbcMan.close(pstmt);
		}
		return mittenti;
	}

}