package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.RegistroEmergenzaDAO;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class RegistroEmergenzaDAOjdbc implements RegistroEmergenzaDAO {
    static Logger logger = Logger.getLogger(RegistroEmergenzaDAOjdbc.class
            .getName());

    private JDBCManager jdbcMan = new JDBCManager();

    public Collection getProtocolliPrenotati(int registroId)
            throws DataException {

        Collection protocolli = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String strQuery = "select * from protocolli where num_prot_emergenza=-1"
                + " AND registro_id=? ORDER BY flag_tipo, protocollo_id DESC";
        logger.info(strQuery);
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery);
            pstmt.setInt(1, registroId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ReportProtocolloView protocollo = new ReportProtocolloView();
                protocollo.setProtocolloId(rs.getInt("protocollo_id"));
                protocollo.setAnnoProtocollo(rs.getInt("ANNO_REGISTRAZIONE"));
                protocollo.setNumeroProtocollo(rs.getInt("NUME_PROTOCOLLO"));
                protocollo.setOggetto(rs.getString("TEXT_OGGETTO"));
                protocollo.setTipoProtocollo(rs.getString("FLAG_TIPO"));
                protocollo.setDataProtocollo(DateUtil.formattaData(rs.getDate(
                        "DATA_REGISTRAZIONE").getTime()));
                protocolli.add(protocollo);
            }
        } catch (Exception e) {
            logger.error("getProtocolliPrenotati", e);
            throw new DataException("Cannot load getProtocolliPrenotati");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return protocolli;

    }

    public int getNumeroProtocolliPrenotati(int registroId)
            throws DataException {

        int protocolli = 0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String strQuery = "select count(protocollo_id) from protocolli where num_prot_emergenza=-1"
                + " AND registro_id=?";
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery);
            pstmt.setInt(1, registroId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                protocolli = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("geNumeroProtocolliPrenotati", e);
            throw new DataException("Cannot load geNumeroProtocolliPrenotati");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return protocolli;

    }

}
