package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.TitolarioDAO;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class TitolarioDAOjdbc implements TitolarioDAO {
    static Logger logger = Logger.getLogger(TitolarioDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    private TitolarioVO getTitolario(ResultSet rsTitolario) throws SQLException {
        TitolarioVO titolario = new TitolarioVO();
        titolario.setId(rsTitolario.getInt("titolario_id"));
        titolario.setAooId(rsTitolario.getInt("aoo_id"));
        titolario.setCodice(rsTitolario.getString("codi_titolario"));
        titolario.setDescrizione(rsTitolario.getString("desc_titolario"));
        titolario.setPath(rsTitolario.getString("path_titolario"));
        titolario.setParentId(rsTitolario.getInt("parent_id"));
        titolario.setMassimario(rsTitolario.getInt("massimario"));
        titolario.setVersione(rsTitolario.getInt("versione"));
        return titolario;
    }

    public Collection getStoriaTitolarioById(int titolarioId)
            throws DataException {
        Collection listaTitolario = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_STORIA_TITOLARIO_BY_IDF);
            pstmt.setInt(1, titolarioId);
            rs = pstmt.executeQuery();
            TitolarioVO titolario;
            while (rs.next()) {
                titolario = new TitolarioVO();
                titolario.setId(rs.getInt("titolario_id"));
                titolario.setAooId(rs.getInt("aoo_id"));
                titolario.setDescrizione(rs.getString("desc_titolario"));
                titolario.setParentId(rs.getInt("parent_id"));
                titolario.setMassimario(rs.getInt("massimario"));
                titolario.setRowCreatedTime(rs.getDate("row_created_time"));
                titolario.setRowCreatedUser(rs.getString("row_created_user"));
                titolario.setVersione(rs.getInt("versione"));
                titolario.setFullPathDescription(rs
                        .getString("parent_full_desc"));
                listaTitolario.add(titolario);
            }
        } catch (Exception e) {
            logger.error("Load Storia titolario", e);
            throw new DataException("Cannot load Storia titolario");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return listaTitolario;
    }

    public Collection getListaTitolario(int aoo, String codice,
            String descrizione) throws DataException {
        Collection listaTitolario = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_TITOLARIO);
            pstmt.setInt(1, aoo);
            pstmt.setString(2, "%" + codice + "%");
            pstmt.setString(3, "%" + descrizione + "%");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                listaTitolario.add(getTitolario(rs));
            }
        } catch (Exception e) {
            logger.error("Load getListaTitolario", e);
            throw new DataException("Cannot load getListaTitolario");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return listaTitolario;
    }

    private TitolarioVO getTitolario(Connection connection, int ufficioId,
            int titolarioId, int aooId) throws DataException {
        TitolarioVO titolario = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(SELECT_TITOLARIO_BY_ID_UFF);
            pstmt.setInt(1, ufficioId);
            pstmt.setInt(2, titolarioId);
            pstmt.setInt(3, aooId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                titolario = getTitolario(rs);
            }
        } catch (Exception e) {
            logger.error("Load Titolario", e);
            throw new DataException("Cannot load Titolario");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return titolario;
    }

    public TitolarioVO getTitolario(int ufficioId, int titolarioId, int aooId)
            throws DataException {
        TitolarioVO titolario = null;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            titolario = getTitolario(connection, ufficioId, titolarioId, aooId);
        } catch (Exception e) {
            logger.error("Load Titolario", e);
            throw new DataException("Cannot load Titolario");
        } finally {
            jdbcMan.close(connection);
        }
        return titolario;
    }

    public String[] getUfficiTitolario(int titolarioId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] ar;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_ARGOMENTO);

            pstmt.setInt(1, titolarioId);
            rs = pstmt.executeQuery();
            Collection tit = new ArrayList();
            while (rs.next()) {
                tit.add(rs.getString("ufficio_id"));
            }
            ar = new String[tit.size()];
            tit.toArray(ar);

        } catch (Exception e) {
            logger.error("Load getUfficiTitolario", e);
            throw new DataException("Cannot load getUfficiTitolario");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return ar;
    }

    private TitolarioVO getTitolario(Connection connection, int titolarioId)
            throws DataException {
        TitolarioVO titolario = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(SELECT_TITOLARIO_BY_ID);

            pstmt.setInt(1, titolarioId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                titolario = getTitolario(rs);
            }

        } catch (Exception e) {
            logger.error("Load getTitolarioById", e);
            throw new DataException("Cannot load getTitolarioById");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return titolario;
    }

    public TitolarioVO getTitolario(int titolarioId) throws DataException {
        TitolarioVO titolario = null;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            titolario = getTitolario(connection, titolarioId);

        } catch (Exception e) {
            logger.error("Load getTitolarioById", e);
            throw new DataException("Cannot load getTitolarioById");
        } finally {
            jdbcMan.close(connection);
        }
        return titolario;
    }

    public Collection getTitolariByParent(int ufficioId, int parentId, int aooId)
            throws DataException {
        Collection titolari = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            if (parentId == 0) {
                pstmt = connection.prepareStatement(SELECT_TITOLARI_ROOT_UFF);
                pstmt.setInt(2, aooId);
            } else {
                pstmt = connection
                        .prepareStatement(SELECT_TITOLARI_BY_PARENT_UFF);
                pstmt.setInt(2, parentId);
            }
            pstmt.setInt(1, ufficioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                titolari.add(getTitolario(rs));
            }
        } catch (Exception e) {
            logger.error("Load Titolario", e);
            throw new DataException("Cannot load Titolario");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return titolari;
    }

    // Luigi 01/02/06
    public Collection getCategorie(int servizioId) throws DataException {
        Collection categorie = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_CATEGORIE);
            pstmt.setInt(1, servizioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                categorie.add(getTitolario(rs));
            }
        } catch (Exception e) {
            logger.error("getCategorie", e);
            throw new DataException("Cannot load getCategorie");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return categorie;
    }

    // Luigi 01/02/06
    public int getUfficioByServizio(int servizioId) throws DataException {
        int ufficioId = 0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICIO_BY_SERVIZIO);
            pstmt.setInt(1, servizioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ufficioId = (rs.getInt(1));
            }
        } catch (Exception e) {
            logger.error("getUfficioByservizio", e);
            throw new DataException("Cannot load getUfficioByServizio");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return ufficioId;
    }

    public Collection getTitolariByParent(int parentId, int aooId)
            throws DataException {
        Collection titolari = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            if (parentId == 0) {
                pstmt = connection.prepareStatement(SELECT_TITOLARI_ROOT);
                pstmt.setInt(1, aooId);
            } else {
                pstmt = connection.prepareStatement(SELECT_TITOLARI_BY_PARENT);
                pstmt.setInt(1, parentId);
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                titolari.add(getTitolario(rs));
            }
        } catch (Exception e) {
            logger.error("Load getTitolariParent", e);
            throw new DataException("Cannot load getTitolariParent");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return titolari;
    }

    public void associaTitolarioUfficio(Connection conn,
            TitolarioVO titolarioVO, int ufficioId) throws DataException {
        PreparedStatement pstmt = null;
        titolarioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("newArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (ufficioId > 0) {
                pstmt = conn.prepareStatement(INSERT_ARGOMENTO_UFFICIO);
                pstmt.setInt(1, ufficioId);
                pstmt.setInt(2, titolarioVO.getId().intValue());
                pstmt.setString(3, titolarioVO.getRowCreatedUser());
                pstmt.setString(4, titolarioVO.getRowUpdatedUser());
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("Save Argomento Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void associaArgomentoUfficio(Connection conn,
            TitolarioVO titolarioVO, int ufficioId) throws DataException {
        PreparedStatement pstmt = null;
        titolarioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("newArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (ufficioId > 0) {
                pstmt = conn.prepareStatement(INSERT_ARGOMENTO_UFFICIO);
                pstmt.setInt(1, ufficioId);
                pstmt.setInt(2, titolarioVO.getId().intValue());
                pstmt.setString(3, titolarioVO.getRowCreatedUser());
                pstmt.setString(4, titolarioVO.getRowUpdatedUser());
                int recIns = pstmt.executeUpdate();
                logger.info("titolario:" + titolarioVO.getId().intValue()
                        + " ufficio:" + ufficioId
                        + " inserito in titolario$ufficio: " + recIns);
            }

        } catch (Exception e) {
            logger.error("Save Argomento Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void associaArgomentoUffici(Connection conn,
            TitolarioVO titolarioVO, String[] uffici) throws DataException {
        PreparedStatement pstmt = null;
        titolarioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("newArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (uffici != null) {
                for (int i = 0; i < uffici.length; i++) {
                    if (uffici[i] != null) {
                        int ufficioId = new Integer(uffici[i]).intValue();
                        pstmt = conn.prepareStatement(INSERT_ARGOMENTO_UFFICIO);
                        pstmt.setInt(1, ufficioId);
                        pstmt.setInt(2, titolarioVO.getId().intValue());
                        pstmt.setString(3, titolarioVO.getRowCreatedUser());
                        pstmt.setString(4, titolarioVO.getRowUpdatedUser());
                        pstmt.executeUpdate();
                    }
                }
            }

            titolarioVO.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("Save Argomento Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    // private boolean getArgomentoUfficio(int titolarioId, int ufficioId)
    // throws DataException {
    // Connection connection = null;
    // PreparedStatement pstmt = null;
    // ResultSet rs = null;
    // boolean trovato = false;
    // try {
    // connection = jdbcMan.getConnection();
    // pstmt = connection.prepareStatement(SELECT_UFFICI_ARGOMENTO_BY_ID);
    // pstmt.setInt(1, titolarioId);
    // pstmt.setInt(2, ufficioId);
    // rs = pstmt.executeQuery();
    // rs.next();
    // if (rs.next()) {
    // trovato = true;
    // }
    // } catch (Exception e) {
    // logger.error("Load getArgomentoProtocolli", e);
    // throw new DataException("Cannot load getArgomentoProtocolli");
    // } finally {
    // jdbcMan.close(rs);
    // jdbcMan.close(pstmt);
    // jdbcMan.close(connection);
    // }
    // return trovato;
    // }

    public TitolarioVO newArgomento(Connection conn, TitolarioVO titolarioVO)
            throws DataException {
        PreparedStatement pstmt = null;
        titolarioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("newArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(INSERT_ARGOMENTO);
            pstmt.setInt(1, titolarioVO.getId().intValue());
            pstmt.setString(2, titolarioVO.getCodice());
            pstmt.setString(3, titolarioVO.getDescrizione());
            if (titolarioVO.getParentId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, titolarioVO.getParentId());
            }

            pstmt.setInt(5, titolarioVO.getAooId());
            pstmt.setString(6, titolarioVO.getPath());
            pstmt.setString(7, titolarioVO.getRowCreatedUser());
            pstmt.setString(8, titolarioVO.getRowUpdatedUser());
            pstmt.setInt(9, titolarioVO.getMassimario());
            pstmt.executeUpdate();

            titolarioVO.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("Save Argomento Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return titolarioVO;
    }

    private void archiviaVersione(Connection connection, TitolarioVO titolarioVO)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("storia - Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            logger.info(INSERT_STORIA_TITOLARIO);
            pstmt = connection.prepareStatement(INSERT_STORIA_TITOLARIO);
            pstmt.setInt(1, titolarioVO.getId().intValue());
            if (titolarioVO.getParentId() == 0) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, titolarioVO.getParentId());
            }

            pstmt.setInt(3, titolarioVO.getAooId());
            pstmt.setString(4, titolarioVO.getDescrizione());
            pstmt.setString(5, titolarioVO.getFullPathDescription());
            pstmt.setInt(6, titolarioVO.getMassimario());
            pstmt.setTimestamp(7, new Timestamp(System.currentTimeMillis())); // ROW_CREATED_TIME
            pstmt.setString(8, titolarioVO.getRowCreatedUser());
            pstmt.setInt(9, titolarioVO.getVersione());

            pstmt.executeUpdate();
            jdbcMan.close(pstmt);

        } catch (Exception e) {
            logger
                    .error("storia titolario" + titolarioVO.getId().intValue(),
                            e);
            throw new DataException("Cannot insert Storia titolario");

        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public TitolarioVO updateArgomento(Connection conn, TitolarioVO titolarioVO)
            throws DataException {
        PreparedStatement pstmt = null;
        titolarioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("updateArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            // archivio la versione precedente
            archiviaVersione(conn, titolarioVO);

            pstmt = conn.prepareStatement(UPDATE_ARGOMENTO);
            pstmt.setString(1, titolarioVO.getCodice());
            pstmt.setString(2, titolarioVO.getDescrizione());
            pstmt.setString(3, titolarioVO.getPath());
            pstmt.setString(4, titolarioVO.getRowUpdatedUser());
            pstmt.setInt(5, titolarioVO.getMassimario());
            pstmt.setInt(6, titolarioVO.getVersione() + 1);
            if (titolarioVO.getParentId() == 0) {
                pstmt.setNull(7, Types.INTEGER);
            } else {
                pstmt.setInt(7, titolarioVO.getParentId());
            }

            pstmt.setInt(8, titolarioVO.getId().intValue());

            pstmt.executeUpdate();
            titolarioVO = getTitolario(conn, titolarioVO.getId().intValue());
            titolarioVO.setReturnValue(ReturnValues.SAVED);

        } catch (Exception e) {
            logger.error("Update Argomento Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return titolarioVO;
    }

    public boolean getArgomentoProtocolli(int titolarioId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean protocolliTrovati = false;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_ARGOMENTO_PROTOCOLLI);
            pstmt.setInt(1, titolarioId);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                protocolliTrovati = true;
            }
        } catch (Exception e) {
            logger.error("Load getArgomentoProtocolli", e);
            throw new DataException("Cannot load getArgomentoProtocolli");
        } finally {
            jdbcMan.closeAll(rs, pstmt, connection);
        }
        return protocolliTrovati;
    }

    public void deleteArgomentoUfficio(Connection conn, int ufficioId,
            int titolarioId) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("deleteArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            String sqlStr = "DELETE FROM titolario$uffici WHERE ufficio_id=? AND titolario_id=?";
            pstmt = conn.prepareStatement(sqlStr);
            pstmt.setInt(1, ufficioId);
            pstmt.setInt(2, titolarioId);
            int recDelete = pstmt.executeUpdate();
            logger.info("deleteArgomentoUfficio: Record cancellati:"
                    + recDelete);
        } catch (Exception e) {
            logger.error("delete deleteArgomentoUfficio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void deleteArgomentoUffici(Connection conn, int titolarioId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("deleteArgomentoUffici() - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(DELETE_ARGOMENTO_UFFICIO);
            pstmt.setInt(1, titolarioId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("delete relazione Argomento Uffici Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void deleteArgomentiPerUfficio(Connection conn, int ufficioId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("deleteArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            String sqlStr = "DELETE FROM titolario$uffici WHERE ufficio_id=? ";
            pstmt = conn.prepareStatement(sqlStr);
            pstmt.setInt(1, ufficioId);
            int recDelete = pstmt.executeUpdate();
            logger.info("deleteArgomentiUfficio: Record cancellati:"
                    + recDelete);
        } catch (Exception e) {
            logger.error("delete deleteArgomentiUfficio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void deleteArgomento(Connection conn, int titolarioId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("deleteArgomento() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(DELETE_ARGOMENTO_UFFICIO);
            pstmt.setInt(1, titolarioId);
            pstmt.executeUpdate();

            pstmt = conn.prepareStatement(DELETE_ARGOMENTO);
            pstmt.setInt(1, titolarioId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("delete Argomento Titolario", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public int controlloTitolarioByDescrizioneEdUfficio(int ufficioId,
            String descrizione) throws DataException {
        int titolarioId = 0;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection
                    .prepareStatement(SELECT_TITOLARIO_BY_DESCR_UFFICIO);
            pstmt.setInt(1, ufficioId);
            pstmt.setString(2, descrizione.toUpperCase().trim());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                titolarioId = rs.getInt("titolario_id");
            }

        } catch (Exception e) {
            logger.error("Load controlloTitolarioByDescrizioneEdUfficio", e);
            throw new DataException(
                    "Cannot load controlloTitolarioByDescrizioneEdUfficio");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }

        return titolarioId;
    }

    public boolean isTitolarioCancellabile(Connection connection,
            int titolarioId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean cancellabile = true;
        try {
            pstmt = connection
                    .prepareStatement(SELECT_COUNT_TITOLARIO_PROTOCOLLI);
            pstmt.setInt(1, titolarioId);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                cancellabile = false;
            } else {
                jdbcMan.close(rs);
                pstmt = connection
                        .prepareStatement(SELECT_COUNT_TITOLARIO_FASCICOLI);
                pstmt.setInt(1, titolarioId);
                rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    cancellabile = false;
                } else {
                    jdbcMan.close(rs);
                    pstmt = connection
                            .prepareStatement(SELECT_COUNT_TITOLARIO_DOCUMENTI);
                    pstmt.setInt(1, titolarioId);
                    rs = pstmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        cancellabile = false;
                    } else {
                        jdbcMan.close(rs);
                        pstmt = connection
                                .prepareStatement(SELECT_COUNT_TITOLARIO_FALDONI);
                        pstmt.setInt(1, titolarioId);
                        rs = pstmt.executeQuery();
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            cancellabile = false;
                        } else {
                            jdbcMan.close(rs);
                            pstmt = connection
                                    .prepareStatement(SELECT_COUNT_TITOLARIO_PROCEDIMENTI);
                            pstmt.setInt(1, titolarioId);
                            rs = pstmt.executeQuery();
                            rs.next();
                            if (rs.getInt(1) > 0) {
                                cancellabile = false;
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Load isTitolarioCancellabile", e);
            throw new DataException("Cannot load isTitolarioCancellabile");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return cancellabile;
    }
    
    @Override
	public boolean deleteAll(Connection connection) throws SQLException {
    	 PreparedStatement pstmt = null;
    	 boolean result = true;
    	 try {
             pstmt = connection.prepareStatement(DELETE_ALL);
             pstmt.executeUpdate(); 
         }catch (Exception e){
        	 result = false;
        	 throw new SQLException();
         }
         return result;
	}
    
    public boolean deleteReferenceTitolario(Connection connection) throws SQLException {
   	 PreparedStatement pstmt = null;
   	 boolean result = true;
   	 try {
            pstmt = connection.prepareStatement(DELETE_TIT_REFERENCE);
            pstmt.executeUpdate(); 
        }catch (Exception e){
       	 result = false;
       	 throw new SQLException();
        }
        return result;
	}

    private final static String DELETE_TIT_REFERENCE = "DELETE FROM eprot.identificativi WHERE nome_tabella='TITOLARIO'";
    
    private final static String DELETE_ALL = "DELETE FROM eprot.titolario";
    
    private final static String SELECT_COUNT_TITOLARIO_PROTOCOLLI = "SELECT count(protocollo_id) FROM protocolli WHERE titolario_id=?";

    private final static String SELECT_COUNT_TITOLARIO_DOCUMENTI = "SELECT count(dfa_id) FROM doc_file_attr WHERE titolario_id=?";

    private final static String SELECT_COUNT_TITOLARIO_FASCICOLI = "SELECT count(fascicolo_id) FROM FASCICOLI WHERE titolario_id=?";

    private final static String SELECT_COUNT_TITOLARIO_FALDONI = "SELECT count(faldone_id) FROM FALDONI WHERE titolario_id=?";

    private final static String SELECT_COUNT_TITOLARIO_PROCEDIMENTI = "SELECT count(procedimento_id) FROM PROCEDIMENTI WHERE titolario_id=?";

    private final static String SELECT_UFFICI_ARGOMENTO = "SELECT * FROM titolario$uffici WHERE titolario_id=?";

    private final static String SELECT_ARGOMENTO_PROTOCOLLI = "SELECT count(protocollo_id) FROM protocolli"
            + " WHERE titolario_id=?";

    private final static String DELETE_ARGOMENTO_UFFICIO = "DELETE FROM titolario$uffici WHERE titolario_id=?";

    private final static String DELETE_ARGOMENTO = "DELETE FROM titolario WHERE titolario_id=?";

    private final static String UPDATE_ARGOMENTO = "UPDATE titolario "
            + " SET codi_titolario=?, desc_titolario=?, path_titolario=?, row_updated_user=?, massimario=?, "
            + " versione =?, parent_id=?  WHERE titolario_id=?";

    private final static String INSERT_ARGOMENTO_UFFICIO = "INSERT INTO titolario$uffici "
            + " (ufficio_id, titolario_id, row_created_user, row_updated_user) VALUES(?,?,?,?)";

    private final static String INSERT_ARGOMENTO = "INSERT INTO titolario "
            + " (titolario_id, codi_titolario, desc_titolario, parent_id, aoo_id, "
            + "path_titolario, row_created_user, row_updated_user, massimario) VALUES(?,?,?,?,?,?,?,?,?)";

    private final static String SELECT_TITOLARIO_BY_ID_UFF = "SELECT t.* FROM titolario t, titolario$uffici u"
            + " WHERE t.titolario_id = u.titolario_id AND"
            + " u.ufficio_id = ? AND t.titolario_id = ? AND t.aoo_id=? ORDER BY codi_titolario, desc_titolario";

    private final static String SELECT_TITOLARI_ROOT_UFF = "SELECT t.* FROM titolario t, titolario$uffici u"
            + " WHERE t.titolario_id = u.titolario_id AND"
            + " u.ufficio_id = ? AND t.parent_id IS NULL AND t.aoo_id=? ORDER BY codi_titolario, desc_titolario";

    private final static String SELECT_TITOLARI_BY_PARENT_UFF = "SELECT t.* FROM titolario t, titolario$uffici u"
            + " WHERE t.titolario_id = u.titolario_id AND"
            + " u.ufficio_id = ? AND t.parent_id = ? ORDER BY codi_titolario,desc_titolario";

    private final static String SELECT_TITOLARIO_BY_ID = "SELECT * FROM titolario"
            + " WHERE titolario_id = ?";

    private final static String SELECT_TITOLARI_ROOT = "SELECT * FROM titolario"
            + " WHERE parent_id IS NULL AND aoo_id=?  ORDER BY codi_titolario,desc_titolario";

    private final static String SELECT_TITOLARI_BY_PARENT = "SELECT * FROM titolario"
            + " WHERE parent_id = ? ORDER BY codi_titolario, desc_titolario";

    private final static String SELECT_TITOLARIO = "SELECT * FROM titolario"
            + " WHERE aoo_id = ? AND lower(codi_titolario) LIKE lower(?) AND"
            + " lower(desc_titolario) LIKE lower(?)" + " ORDER BY parent_id";

    private final static String SELECT_CATEGORIE = "SELECT t.* FROM titolario t WHERE t.parent_id = ?";

    private final static String SELECT_UFFICIO_BY_SERVIZIO = "SELECT ufficio_id FROM titolario$uffici WHERE titolario_id = ?";

    private final static String INSERT_STORIA_TITOLARIO = "INSERT INTO storia_titolario"
            + " (titolario_id,  parent_id,  aoo_id,  desc_titolario,  parent_full_desc,  massimario, "
            + "row_created_time,  row_created_user,  versione) VALUES(?,?,?,?,?,?,?,?,?)";

    private final static String SELECT_STORIA_TITOLARIO_BY_IDF = "SELECT * FROM storia_titolario t"
            + " WHERE titolario_id = ? ORDER BY versione Desc";

    private final static String SELECT_TITOLARIO_BY_DESCR_UFFICIO = "SELECT distinct t.titolario_id"
            + " from titolario t, TITOLARIO$UFFICI u where t.titolario_id = u.titolario_id"
            + " and u.ufficio_id=? and upper(t.desc_titolario)=?";

	
};