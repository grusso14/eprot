package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.OrganizzazioneDAO;
import it.finsiel.siged.mvc.vo.lookup.ParametriLdapVO;
import it.finsiel.siged.mvc.vo.organizzazione.AmministrazioneVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class OrganizzazioneDAOjdbc implements OrganizzazioneDAO {
    private final static String SELECT_AMMINISTRAZIONE = "SELECT * FROM amministrazione";

    private final static String SELECT_UFFICI = "SELECT * FROM uffici";

    private final static String SELECT_UFFICI_IDS_BY_UTENTE = "SELECT DISTINCT(ufficio_id) FROM permessi_utente WHERE utente_id = ?";

    private final static String UPDATE_AMMINISTRAZIONE = "UPDATE amministrazione set codi_amministrazione=?, desc_amministrazione=?,"
            + "flag_ldap=?, row_updated_user=?, row_updated_time=?, versione=versione+1, "
            + "ldap_versione=?, ldap_porta=?, ldap_use_ssl=?, ldap_host=?, ldap_dn=?, path_doc =? WHERE amministrazione_id=?";

    static Logger logger = Logger.getLogger(OrganizzazioneDAOjdbc.class
            .getName());

    private JDBCManager jdbcMan = new JDBCManager();

    public AmministrazioneVO getAmministrazione() throws DataException {
        AmministrazioneVO amm = null;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_AMMINISTRAZIONE);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                amm = new AmministrazioneVO();
                amm.setId(rs.getInt("amministrazione_id"));
                amm.setCodice(rs.getString("codi_amministrazione"));
                amm.setDescription(rs.getString("desc_amministrazione"));
                amm.setFlagLdap(rs.getString("flag_ldap"));
                ParametriLdapVO pVO = new ParametriLdapVO();
                pVO.setVersione(rs.getInt("ldap_versione"));
                pVO.setPorta(rs.getInt("ldap_porta"));
                pVO.setUse_ssl(rs.getString("ldap_use_ssl"));
                pVO.setHost(rs.getString("ldap_host"));
                pVO.setDn(rs.getString("ldap_dn"));
                amm.setParametriLdap(pVO);
                amm.setPathDoc(rs.getString("path_doc"));
                logger.debug("Caricata Amministrazione: " + amm);
            }
        } catch (Exception e) {
            amm = null;
            logger.error("Caricamento Amministrazione ", e);
            throw new DataException("Cannot load Amministrazione");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return amm;
    }

    public Collection getUffici() throws DataException {
        Collection uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                UfficioVO uff = new UfficioVO();
                uff.setId(rs.getInt("ufficio_id"));
                uff.setAttivo(rs.getBoolean("flag_attivo"));
                uff.setTipo(rs.getString("tipo"));
                uff.setAccettazioneAutomatica(rs
                        .getBoolean("flag_accettazione_automatica"));
                uff.setAooId(rs.getInt("aoo_id"));
                uff.setParentId(rs.getInt("parent_id"));
                uff.setDescription(rs.getString("descrizione"));
                uffici.add(uff);

            }
        } catch (Exception e) {
            uffici.clear();
            logger.error("Load Uffici ", e);
            throw new DataException("Cannot load Uffici");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
    }

    public ArrayList getIdentificativiUffici(int utenteId) throws DataException {
        ArrayList uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_IDS_BY_UTENTE);
            pstmt.setInt(1, utenteId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                uffici.add(new Integer(rs.getInt("ufficio_id")));
            }
        } catch (Exception e) {
            logger.error("getUfficiUtente", e);
            throw new DataException(
                    "Impossibile leggere gli uffici dell'utente.");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
    }

    public AmministrazioneVO updateAmministrazione(AmministrazioneVO ammVO)
            throws DataException {
        AmministrazioneVO ammSalvata = new AmministrazioneVO();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            ammSalvata = updateAmministrazione(connection, ammVO);
        } catch (Exception e) {
            throw new DataException(e.getMessage()
                    + " updateAmministrazione Amministrazione Id :"
                    + ammVO.getId().intValue());
        } finally {
            jdbcMan.close(connection);
        }
        return ammSalvata;
    }

    private AmministrazioneVO updateAmministrazione(Connection conn,
            AmministrazioneVO ammVO) throws DataException {
        PreparedStatement pstmt = null;
        ammVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("updateAmministrazione() - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(UPDATE_AMMINISTRAZIONE);
            pstmt.setString(1, ammVO.getCodice());
            pstmt.setString(2, ammVO.getDescription());
            pstmt.setString(3, ammVO.getFlagLdap());
            pstmt.setString(4, ammVO.getRowUpdatedUser());
            pstmt.setDate(5, new Date(System.currentTimeMillis()));
            pstmt.setInt(6, ammVO.getParametriLdap().getVersione());
            pstmt.setInt(7, ammVO.getParametriLdap().getPorta());
            pstmt.setString(8, ammVO.getParametriLdap().getUse_ssl());
            pstmt.setString(9, ammVO.getParametriLdap().getHost());
            pstmt.setString(10, ammVO.getParametriLdap().getDn());
            pstmt.setString(11, ammVO.getPathDoc());
            pstmt.setInt(12, ammVO.getId().intValue());
            pstmt.executeUpdate();
            ammVO.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("Update updateAmministrazione", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return ammVO;
    }
};