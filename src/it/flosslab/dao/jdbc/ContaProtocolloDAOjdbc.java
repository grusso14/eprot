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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.dao.jdbc.ProtocolloDAOjdbc;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.flosslab.mvc.presentation.integration.ContaProtocolloDAO;

import org.apache.log4j.Logger;

public class ContaProtocolloDAOjdbc implements ContaProtocolloDAO{
	static Logger logger = Logger.getLogger(ProtocolloDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    
    public int contaProtocolliAllacciabili(Utente utente,
            int numeroProtocolloDa, int numeroProtocolloA, int annoProtocollo,
            int protocolloId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int numeroProtocolli = 0;
        StringBuffer strQuery = new StringBuffer(CONTA_ALLACCI);
        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff = org.getUfficio(utente.getUfficioVOInUso().getId()
                .intValue());

        if (!uff.getValueObject().getTipo().equals(UfficioVO.UFFICIO_CENTRALE)) {
            String ufficiUtenti = uff.getListaUfficiDiscendentiId(utente);
            strQuery.append(" AND (EXISTS (SELECT * FROM ").append(
                    "protocollo_assegnatari ass ").append(
                    "WHERE ass.protocollo_id=p.protocollo_id ").append(
                    "AND ass.ufficio_assegnatario_id IN (")
                    .append(ufficiUtenti).append(")) OR").append(
                            " p.ufficio_protocollatore_id IN (").append(
                            ufficiUtenti).append(") OR").append(
                            " p.ufficio_mittente_id IN (").append(ufficiUtenti)
                    .append("))");
        }

        try {
            if (numeroProtocolloDa > 0) {
                strQuery.append(" AND NUME_PROTOCOLLO>=" + numeroProtocolloDa);
            }
            if (numeroProtocolloA > 0) {
                strQuery.append(" AND NUME_PROTOCOLLO<=" + numeroProtocolloA);
            }
            if (annoProtocollo > 0) {
                strQuery.append(" AND ANNO_REGISTRAZIONE=" + annoProtocollo);
            }
            strQuery
                    .append(" ORDER BY p.ANNO_REGISTRAZIONE DESC, p.NUME_PROTOCOLLO DESC");
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery.toString());

            pstmt.setInt(1, utente.getRegistroInUso());
            pstmt.setInt(2, protocolloId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                numeroProtocolli = rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("getProtocolliAllacciabili", e);
            throw new DataException("Cannot load getProtocolliAllacciabili");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return numeroProtocolli;

    }
    
    
    public int contaProtocolliAssegnati(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA,
            String statoProtocollo, String statoScarico,
            String tipoUtenteUfficio) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int numeroProtocolliAssegnati = 0;

        String strQuery = "SELECT count (p.protocollo_id) "
                + " FROM PROTOCOLLI p, PROTOCOLLO_ASSEGNATARI a "
                + " WHERE registro_id=? AND stato_protocollo=? AND p.protocollo_id =a.protocollo_id AND ";
        if (annoProtocolloDa > 0) {
            strQuery = strQuery + " ANNO_REGISTRAZIONE>=" + annoProtocolloDa
                    + " AND";
        }
        if (annoProtocolloA > 0) {
            strQuery = strQuery + " ANNO_REGISTRAZIONE<=" + annoProtocolloA
                    + " AND";
        }
        if (numeroProtocolloDa > 0) {
            strQuery = strQuery + " NUME_PROTOCOLLO>=" + numeroProtocolloDa
                    + " AND";
        }
        if (dataDa != null) {
            strQuery = strQuery + " data_registrazione >=? AND";
        }
        if (dataA != null) {
            strQuery = strQuery + " data_registrazione <=? AND";
        }
        if (numeroProtocolloA > 0) {
            strQuery = strQuery + " NUME_PROTOCOLLO<=" + numeroProtocolloA
                    + " AND";
        }

        if ("S".equals(statoScarico) && "T".equals(tipoUtenteUfficio)) {
            strQuery = strQuery
                    + " utente_assegnatario_id=? AND flag_competente=1 AND STAT_ASSEGNAZIONE=?";

        } else if (("S".equals(statoScarico) && "U".equals(tipoUtenteUfficio))
                || "A".equals(statoScarico) || "R".equals(statoScarico)) {
            strQuery = strQuery + " ufficio_assegnatario_id IN ("
                    + utente.getUfficiIds() + ") ";
            strQuery = strQuery + " AND utente_assegnatario_id IS NULL";
            strQuery = strQuery
                    + " AND flag_competente=1 AND STAT_ASSEGNAZIONE=?";

        } else if ("N".equals(statoScarico)) {
            strQuery = strQuery
                    + " utente_assegnatario_id=? AND flag_competente=1 AND STAT_ASSEGNAZIONE=?";
        }

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery);
            int currPstmt = 1;
            pstmt.setInt(currPstmt++, utente.getRegistroInUso());
            pstmt.setString(currPstmt++, statoScarico);
            if (dataDa != null) {
                pstmt.setDate(currPstmt++, new java.sql.Date(dataDa.getTime()));
            }
            if (dataA != null) {
                pstmt.setTimestamp(currPstmt++, new Timestamp(dataA.getTime()
                        + Constants.GIORNO_MILLISECONDS - 1));
            }
            if (("S".equals(statoScarico) && "T".equals(tipoUtenteUfficio))
                    || "N".equals(statoScarico)) {
                pstmt.setInt(currPstmt++, utente.getValueObject().getId()
                        .intValue());
            }
            pstmt.setString(currPstmt++, statoProtocollo);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                numeroProtocolliAssegnati = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("contaProtocolliAssegnati", e);
            throw new DataException("contaProtocolliAssegnati");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return numeroProtocolliAssegnati;

    }
    
    
    public int contaProtocolliRespinti(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA) throws DataException {

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int numeroProtocolliRespinti = 0;
        String strQuery = "SELECT count (p.protocollo_id)"
                + " FROM PROTOCOLLI P, PROTOCOLLO_ASSEGNATARI A"
                + " WHERE p.registro_id=? AND A.protocollo_id = P.protocollo_id AND flag_competente=1"
                + " AND stat_assegnazione='R' "
                + " AND utente_assegnatario_id ="
                + utente.getValueObject().getId().intValue();

        if (annoProtocolloDa > 0) {
            strQuery = strQuery + " AND ANNO_REGISTRAZIONE>="
                    + annoProtocolloDa + " ";
        }
        if (annoProtocolloA > 0) {
            strQuery = strQuery + " AND ANNO_REGISTRAZIONE<=" + annoProtocolloA;
        }
        if (numeroProtocolloDa > 0) {
            strQuery = strQuery + " AND  NUME_PROTOCOLLO>="
                    + numeroProtocolloDa;
        }
        if (dataDa != null) {
            strQuery = strQuery + " AND  data_registrazione >=? ";
        }
        if (dataA != null) {
            strQuery = strQuery + "  AND data_registrazione <=? ";
        }
        if (numeroProtocolloA > 0) {
            strQuery = strQuery + " AND  NUME_PROTOCOLLO<=" + numeroProtocolloA;
        }

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery);
            int currPstmt = 1;
            pstmt.setInt(currPstmt++, utente.getRegistroInUso());
            if (dataDa != null) {
                pstmt.setDate(currPstmt++, new java.sql.Date(dataDa.getTime()));
            }
            if (dataA != null) {
                pstmt.setTimestamp(currPstmt++, new Timestamp(dataA.getTime()
                        + Constants.GIORNO_MILLISECONDS - 1));
            }

            rs = pstmt.executeQuery();
            if (rs.next()) {
                numeroProtocolliRespinti = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("contaProtocolliRespinti", e);
            throw new DataException("Cannot load contaProtocolliRespinti");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return numeroProtocolliRespinti;

    }
    
    
    public int contaProtocolli(Utente utente, Ufficio uff, HashMap sqlDB)
    throws DataException {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int numeroProtocolli = 0;
		
		StringBuffer strQuery = new StringBuffer(SELECT_COUNT__PROTOCOLLI);
		if (!uff.getValueObject().getTipo().equals(UfficioVO.UFFICIO_CENTRALE)) {
		    String ufficiUtenti = uff.getListaUfficiDiscendentiId(utente);
		    strQuery.append(" AND (EXISTS (SELECT * FROM ").append(
		            "protocollo_assegnatari ass ").append(
		            "WHERE ass.protocollo_id=p.protocollo_id ").append(
		            "AND ass.ufficio_assegnatario_id IN (")
		            .append(ufficiUtenti).append(")) OR").append(
		                    " p.ufficio_protocollatore_id IN (").append(
		                    ufficiUtenti).append(") OR").append(
		                    " p.ufficio_mittente_id IN (").append(ufficiUtenti)
		            .append("))");
		}
		if (sqlDB != null) {
		    for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
		        Map.Entry entry = (Map.Entry) it.next();
		        Object key = entry.getKey();
		        strQuery.append(" AND ").append(key.toString());
		        if(key.toString().contains(" IN ")) {
		        	List<Integer> documents_id = (List<Integer>)entry.getValue();
		        	strQuery.append(" (");
		        	for (int i = 0; i < documents_id.size(); i++) {
		        		strQuery.append("?");
		        		if(i < documents_id.size() -1) {
		        			strQuery.append(", ");
		        		}
					}
		        	strQuery.append(")");
		        }
		    }
		}
		System.out.println("Query: " + strQuery.toString());
		int indiceQuery = 1;
		try {
		    connection = jdbcMan.getConnection();
		    pstmt = connection.prepareStatement(strQuery.toString());
		    pstmt.setInt(indiceQuery++, utente.getRegistroInUso());
		
		    if (sqlDB != null) {
		        for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
		            Map.Entry entry = (Map.Entry) it.next();
		            Object key = entry.getKey();
		            Object value = entry.getValue();
		            if (value instanceof Integer) {
		                pstmt.setInt(indiceQuery, ((Integer) value).intValue());
		            } else if (value instanceof String) {
		                if (key.toString().indexOf("LIKE") > 0) {
		                    pstmt
		                            .setString(indiceQuery, value.toString()
		                                    );
		                } else {
		                    pstmt.setString(indiceQuery, value.toString());
		                }
		            } else if (value instanceof java.util.Date) {
		                java.util.Date d = (java.util.Date) value;
		                pstmt.setTimestamp(indiceQuery, new java.sql.Timestamp(
		                        d.getTime()));
		            } else if (value instanceof Boolean) {
		                pstmt.setBoolean(indiceQuery, ((Boolean) value)
		                        .booleanValue());
		            } else if (value instanceof Long) {
		                pstmt.setLong(indiceQuery, ((Long) value).longValue());
		            } else if (value instanceof List) {
		            	 if (key.toString().indexOf(" IN ") > 0) {
		            		 for(int j = 0; j < ((List<Integer>)value).size(); j++) {
		            			 pstmt.setInt(indiceQuery, ((List<Integer>)value).get(j).intValue());
		            			 if(j < ((List<Integer>)value).size() -1) {
		            				 indiceQuery++;
		            			 }
		            		 }
		            	 }
		            }
		            indiceQuery++;
		        }
		    }
		
		    rs = pstmt.executeQuery();
		
		    if (rs.next()) {
		        numeroProtocolli = rs.getInt("total");
		    }
		
		} catch (Exception e) {
		    logger.error("contaProtocolli", e);
		    throw new DataException("Cannot load contaProtocolli");
		} finally {
			jdbcMan.closeAll(rs, pstmt, connection);
		}
		return numeroProtocolli;
		
		}
    
    
    public final static String CONTA_ALLACCI = "SELECT count( "
        + "p.protocollo_id)"
        + "FROM protocolli p"
        + " LEFT JOIN protocollo_assegnatari a ON a.protocollo_id=p.protocollo_id"
        + " LEFT JOIN uffici uf ON a.ufficio_assegnatario_id=uf.ufficio_id"
        + " LEFT JOIN utenti ut ON a.utente_assegnatario_id=ut.utente_id"
        + " LEFT JOIN protocollo_destinatari d ON d.protocollo_id=p.protocollo_id "
        + "WHERE p.registro_id=? AND p.protocollo_id!=? ";
    
    public final static String SELECT_COUNT__PROTOCOLLI = "SELECT COUNT(p.protocollo_id) as total"
        + " FROM protocolli p"
        + " LEFT JOIN protocollo_assegnatari a ON a.protocollo_id=p.protocollo_id"
        + " LEFT JOIN uffici uf ON a.ufficio_assegnatario_id=uf.ufficio_id"
        + " LEFT JOIN utenti ut ON a.utente_assegnatario_id=ut.utente_id"
        + " LEFT JOIN protocollo_destinatari d ON d.protocollo_id=p.protocollo_id "
        + "WHERE p.registro_id=?";
}
