package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.UfficioDAO;
import it.finsiel.siged.mvc.presentation.helper.UtenteView;
import it.finsiel.siged.mvc.vo.organizzazione.ReferenteUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.TitolarioUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

public class UfficioDAOjdbc implements UfficioDAO {
    static Logger logger = Logger.getLogger(UfficioDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    private UfficioVO getUfficioVO(ResultSet rsUfficio) throws SQLException {
        UfficioVO ufficioVO = new UfficioVO();
        ufficioVO.setId(rsUfficio.getInt("ufficio_id"));
        ufficioVO.setAooId(rsUfficio.getInt("aoo_id"));
        ufficioVO.setDescription(rsUfficio.getString("descrizione"));
        ufficioVO.setParentId(rsUfficio.getInt("parent_id"));
        ufficioVO.setAttivo(rsUfficio.getBoolean("flag_attivo"));
        ufficioVO.setAccettazioneAutomatica(rsUfficio
                .getBoolean("flag_accettazione_automatica"));
        ufficioVO.setTipo(rsUfficio.getString("tipo"));
        ufficioVO.setVersione(rsUfficio.getInt("versione"));
        return ufficioVO;
    }

    public UfficioVO nuovoUfficio(Connection conn, UfficioVO ufficioVO)
            throws DataException {
        PreparedStatement pstmt = null;
        ufficioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("nuovoUfficio() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (isDescrizioneUsed(conn, ufficioVO.getDescription(), ufficioVO
                    .getId().intValue())) {
                ufficioVO.setReturnValue(ReturnValues.FOUND);
                logger.warn("descrizione ufficio già utilizzata");
            } else {

                pstmt = conn.prepareStatement(INSERT_UFFICIO);
                pstmt.setInt(1, ufficioVO.getId().intValue());
                pstmt.setString(2, ufficioVO.getDescription());
                if (ufficioVO.getParentId() == 0) {
                    pstmt.setNull(3, Types.INTEGER);
                } else {
                    pstmt.setInt(3, ufficioVO.getParentId());
                }
                pstmt.setInt(4, ufficioVO.getAooId());
                pstmt.setInt(5, ufficioVO.isAttivo() ? 1 : 0);
                pstmt.setString(6, ufficioVO.getTipo());
                pstmt.setInt(7, ufficioVO.isAccettazioneAutomatica() ? 1 : 0);
                pstmt.setString(8, ufficioVO.getRowCreatedUser());
                pstmt.setString(9, ufficioVO.getRowUpdatedUser());
                pstmt.executeUpdate();

                ufficioVO.setReturnValue(ReturnValues.SAVED);
            }
        } catch (Exception e) {
            logger.error("Save nuovoUfficio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return ufficioVO;
    }

    public UfficioVO modificaUfficio(Connection conn, UfficioVO ufficioVO)
            throws DataException {
        PreparedStatement pstmt = null;
        ufficioVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("modificaUfficio() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (isDescrizioneUsed(conn, ufficioVO.getDescription(), ufficioVO
                    .getId().intValue())) {
                ufficioVO.setReturnValue(ReturnValues.FOUND);
                logger.warn("descrizione ufficio già utilizzata");
            } else {

                pstmt = conn.prepareStatement(UPDATE_UFFICIO);
                pstmt.setString(1, ufficioVO.getDescription());
                if (ufficioVO.getParentId() == 0) {
                    pstmt.setNull(2, Types.INTEGER);
                } else {
                    pstmt.setInt(2, ufficioVO.getParentId());
                }
                pstmt.setInt(3, ufficioVO.isAttivo() ? 1 : 0);
                pstmt.setString(4, ufficioVO.getTipo());
                pstmt.setInt(5, ufficioVO.isAccettazioneAutomatica() ? 1 : 0);
                pstmt.setString(6, ufficioVO.getRowUpdatedUser());
                pstmt.setInt(7, ufficioVO.getVersione() + 1);
                pstmt.setInt(8, ufficioVO.getId().intValue());
                pstmt.executeUpdate();

                ufficioVO.setReturnValue(ReturnValues.SAVED);
            }
        } catch (Exception e) {
            logger.error("Save modificaUfficio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return ufficioVO;
    }

    public void cancellaUfficio(Connection conn, int ufficioId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("cancellaUfficio() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(DELETE_UFFICIO);
            pstmt.setInt(1, ufficioId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Error cancellaUfficio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public boolean isUfficioCancellabile(int ufficioId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean cancellabile = true;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_PERMESSI_UTENTE);
            pstmt.setInt(1, ufficioId);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                cancellabile = false;
            } else {
                jdbcMan.close(rs);
                pstmt = connection
                        .prepareStatement(SELECT_UFFICIO_ASSEGNATARI_PROTOCOLLI);
                pstmt.setInt(1, ufficioId);
                pstmt.setInt(2, ufficioId);
                rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    cancellabile = false;
                } else {
                    jdbcMan.close(rs);
                    pstmt = connection
                            .prepareStatement(SELECT_UFFICIO_PROTOCOLLI);
                    pstmt.setInt(1, ufficioId);
                    pstmt.setInt(2, ufficioId);
                    rs = pstmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        cancellabile = false;
                    } else {
                        jdbcMan.close(rs);
                        pstmt = connection
                                .prepareStatement(SELECT_FILE_CARTELLA_UFFICIO);
                        pstmt.setInt(1, ufficioId);
                        rs = pstmt.executeQuery();
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            cancellabile = false;
                        }
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Load isUfficioCancellabile", e);
            throw new DataException("Cannot load isUfficioCancellabile");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return cancellabile;
    }

    public Collection getUfficiByParent(int ufficioId) throws DataException {
        Collection uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_BY_PARENT);
            pstmt.setInt(1, ufficioId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                uffici.add(getUfficioVO(rs));
            }
        } catch (Exception e) {
            logger.error("Load getUfficiByParent", e);
            throw new DataException("Cannot load getUfficiByParent");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
    }

    public UfficioVO getUfficioVO(int ufficioId) throws DataException {
        UfficioVO vo = new UfficioVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICIO_BY_ID);
            pstmt.setInt(1, ufficioId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                vo = getUfficioVO(rs);
                vo.setReturnValue(ReturnValues.FOUND);
            } else {
                vo.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("getUfficioVO", e);
            throw new DataException("Cannot load getUfficiByParent");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return vo;
    }

    public Collection getUfficiByUtente(int utenteId) throws DataException {
        Collection uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_UTENTE);
            pstmt.setInt(1, utenteId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                UfficioVO vo = getUfficioVO(rs);
                vo.setReturnValue(ReturnValues.FOUND);
                uffici.add(vo);
            }

        } catch (Exception e) {
            logger.error("Load getUfficiByUtente", e);
            throw new DataException("Cannot load getUfficiByUtente");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
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
                UfficioVO vo = getUfficioVO(rs);
                vo.setReturnValue(ReturnValues.FOUND);
                uffici.add(vo);
            }

        } catch (Exception e) {
            logger.error("Load getUffici", e);
            throw new DataException("Cannot load getUffici");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
    }

    public Collection getUffici(int aooId) throws DataException {
        Collection uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_AOO);
            pstmt.setInt(1, aooId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                UfficioVO vo = getUfficioVO(rs);
                vo.setReturnValue(ReturnValues.FOUND);
                uffici.add(vo);
            }

        } catch (Exception e) {
            logger.error("Load getUffici", e);
            throw new DataException("Cannot load getUffici");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
    }

    /*
     * Get a collection of UtenteView using aooId.
     */
    public Collection getUtentiByUfficio(int ufficioId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UtenteView c = null;
        ArrayList utenti = new ArrayList();
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UTENTI_BY_UFFICIO);
            pstmt.setInt(1, ufficioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                c = new UtenteView();
                // come in getUtente
                c.setId(rs.getInt("utente_id"));
                c.setCognome(rs.getString("cognome"));
                c.setNome(rs.getString("nome"));
                // TODO: DACOMPLETARE
                // c.setReferente(rs.getBoolean("flag_abilitato"));
                c.setReferente(false);
                utenti.add(c);
            }
        } catch (Exception e) {
            logger.error("Load getUtentiByUfficio", e);
            throw new DataException("Cannot getUtentiByUfficio");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return utenti;
    }

    public void cancellaUtentiReferenti(Connection conn, int ufficioId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("cancellaUtentiReferenti - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            // cancello tutti i permessi utente
            String sqlStr = "DELETE FROM referenti_uffici WHERE ufficio_id=?";
            pstmt = conn.prepareStatement(sqlStr);
            pstmt.setInt(1, ufficioId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("cancellaUtentiReferenti", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void inserisciUtentiReferenti(Connection conn,
            ReferenteUfficioVO referenteufficioVO) throws DataException {
        PreparedStatement pstmt = null;

        try {

            if (conn == null) {
                logger.warn("aggiornaUtentiReferenti - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(INSERT_REFERENTI_UFFICI);
            pstmt.setInt(1, referenteufficioVO.getUtenteId());
            pstmt.setInt(2, referenteufficioVO.getUfficioId());
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Load aggiornaUtentiReferenti", e);
            throw new DataException("Cannot load inserisciUtentiReferenti");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaUfficiTitolari(Connection conn,
            TitolarioUfficioVO titolarioufficioVO) throws DataException {
        PreparedStatement pstmt = null;

        try {

            if (conn == null) {
                logger.warn("aggiornaUtentiReferenti - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(INSERT_TITOLARIO_UFFICI);
            pstmt.setInt(1, titolarioufficioVO.getUfficioId());
            pstmt.setInt(2, titolarioufficioVO.getTitolarioId());
            if (titolarioufficioVO.getRowCreatedTime() == null) {
                pstmt.setNull(3, Types.DATE);
            } else {
                pstmt.setDate(3, new Date(titolarioufficioVO
                        .getRowCreatedTime().getTime()));
            }
            pstmt.setString(4, titolarioufficioVO.getRowCreatedUser());
            pstmt.setString(5, titolarioufficioVO.getRowUpdatedUser());
            if (titolarioufficioVO.getRowUpdatedTime() == null) {
                pstmt.setNull(6, Types.DATE);
            } else {
                pstmt.setDate(6, new Date(titolarioufficioVO
                        .getRowUpdatedTime().getTime()));
            }
            pstmt.setInt(7, titolarioufficioVO.getVersione());
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Load salvaUfficiTitolari", e);
            throw new DataException("Cannot load inserisciUtentiReferenti");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void aggiornaUtentiReferenti(Connection conn, int ufficioId,
            String[] utentiId) throws DataException {
        PreparedStatement pstmt = null;

        try {

            if (conn == null) {
                logger.warn("aggiornaUtentiReferenti - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            int i = 0;
            while (i < utentiId.length) {
                pstmt = conn.prepareStatement(INSERT_REFERENTI_UFFICI);
                int utenteId = Integer.parseInt(utentiId[i]);
                pstmt.setInt(1, utenteId);
                pstmt.setInt(2, ufficioId);
                pstmt.executeUpdate();
                i = i + 1;
            }
        } catch (Exception e) {
            logger.error("Load aggiornaUtentiReferenti", e);
            throw new DataException("Cannot load aggiornaUtentiReferenti");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public String[] getReferentiByUfficio(int ufficioId) throws DataException {
        Collection referenti = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] aFunzioni = null;
        try {
            connection = jdbcMan.getConnection();
            String sqlStr = "SELECT utente_id FROM referenti_uffici "
                    + " WHERE  ufficio_id=?";
            pstmt = connection.prepareStatement(sqlStr);
            pstmt.setInt(1, ufficioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                referenti.add(rs.getString("utente_id"));
            }
            aFunzioni = new String[referenti.size()];
            referenti.toArray(aFunzioni);

        } catch (Exception e) {
            logger.error("Load getReferentiByUfficioReferenti", e);
            throw new DataException(
                    "Cannot load getReferentiByUfficioReferenti");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return aFunzioni;
    }

    public int getNumeroReferentiByUfficio(int ufficioId) throws DataException {

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int numeroUtenti;

        try {
            connection = jdbcMan.getConnection();
            String sqlStr = "SELECT COUNT(*) FROM referenti_uffici "
                    + " WHERE  ufficio_id=?";
            pstmt = connection.prepareStatement(sqlStr);
            pstmt.setInt(1, ufficioId);
            rs = pstmt.executeQuery();
            rs.next();
            numeroUtenti = rs.getInt(1);
        } catch (Exception e) {
            logger.error("Load getReferentiByUfficioReferenti", e);
            throw new DataException(
                    "Cannot load getReferentiByUfficioReferenti");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return numeroUtenti;
    }

    public long getCountUffici() throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long num = -1;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_COUNT_UFFICI);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                num = rs.getLong(1);
            }
        } catch (Exception e) {
            logger.error("getCountUffici", e);
            throw new DataException("Cannot load getCountUffici");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return num;
    }

    public boolean isUtenteReferenteUfficio(int ufficioId, int utenteId)
            throws DataException {

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isReferente = false;

        try {
            connection = jdbcMan.getConnection();
            String sqlStr = "SELECT COUNT(*) FROM referenti_uffici "
                    + " WHERE  ufficio_id=? AND utente_id=?";
            pstmt = connection.prepareStatement(sqlStr);
            pstmt.setInt(1, ufficioId);
            pstmt.setInt(2, utenteId);
            rs = pstmt.executeQuery();
            rs.next();
            isReferente = (rs.getInt(1) > 0);
        } catch (Exception e) {
            logger.error("Load isUtenteReferenteUfficio", e);
            throw new DataException("Cannot load isUtenteReferenteUfficio");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return isReferente;
    }

    public boolean isDescrizioneUsed(Connection connection, String descrizione,
            int ufficioId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean used = true;
        try {
            if (connection == null) {
                logger.warn("isDescrizioneUsed - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(CHECK_NAME);
            pstmt.setString(1, descrizione.toUpperCase().trim());
            pstmt.setInt(2, ufficioId);
            rs = pstmt.executeQuery();
            rs.next();
            used = rs.getInt(1) > 0;
        } catch (Exception e) {
            logger.error("isDescrizioneUsed:" + descrizione, e);
            throw new DataException("error.database.missing");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return used;
    }

    protected final static String SELECT_UTENTI_BY_UFFICIO = "SELECT distinct u.* FROM utenti U, permessi_utente P"
            + " WHERE U.utente_id=P.utente_id AND ufficio_id=? ORDER BY cognome,nome";

    private final static String SELECT_UFFICI_BY_PARENT = "SELECT * FROM uffici"
            + " WHERE parent_id=?";

    private final static String SELECT_UFFICIO_BY_ID = "SELECT * FROM UFFICI WHERE UFFICIO_ID=?";

    private final static String SELECT_UFFICIO_PROTOCOLLI = "SELECT count(protocollo_id) FROM protocolli"
            + " WHERE ufficio_protocollatore_id=? OR ufficio_mittente_id=?";

    private final static String SELECT_FILE_CARTELLA_UFFICIO = "SELECT COUNT(dfa_id) FROM doc_file_attr f, doc_cartelle c "
            + "where c.dc_id = f.dc_id and c.ufficio_id= ?";

    private final static String SELECT_UFFICIO_ASSEGNATARI_PROTOCOLLI = "SELECT count(protocollo_id) FROM protocollo_assegnatari"
            + " WHERE ufficio_assegnante_id=? OR ufficio_assegnatario_id=?";

    private final static String SELECT_PERMESSI_UTENTE = "SELECT count(ufficio_id) FROM permessi_utente"
            + " WHERE ufficio_id=?";

    protected final static String SELECT_UFFICI_UTENTE = "SELECT distinct uf.* FROM uffici uf , permessi_utente p WHERE uf.ufficio_id=p.ufficio_id AND p.utente_id=? ";

    protected final static String SELECT_UFFICI = "SELECT distinct uf.* FROM uffici uf ";

    protected final static String SELECT_UFFICI_AOO = "SELECT distinct uf.* FROM uffici uf  WHERE aoo_id=?";

    private final static String INSERT_UFFICIO = "INSERT INTO uffici "
            + " (ufficio_id, descrizione, parent_id, aoo_id, "
            + "flag_attivo, tipo,flag_accettazione_automatica, "
            + "row_created_user, row_updated_user) VALUES(?,?,?,?,?,?,?,?,?)";

    private final static String UPDATE_UFFICIO = "UPDATE uffici "
            + " SET descrizione=?, parent_id=?, flag_attivo=?, tipo=?, flag_accettazione_automatica=?, "
            + "row_updated_user=?, versione=? WHERE ufficio_id=?";

    private final static String DELETE_UFFICIO = "DELETE FROM uffici WHERE ufficio_id=?";

    protected final static String INSERT_REFERENTI_UFFICI = "INSERT INTO referenti_uffici (utente_id, ufficio_id) VALUES (?,?)";

    protected final static String INSERT_TITOLARIO_UFFICI = "INSERT INTO titolario$uffici (ufficio_id,titolario_id,row_created_time,row_created_user,row_updated_user"
            + ",row_updated_time,versione) VALUES (?,?,?,?,?,?,?)";

    protected final static String SELECT_REFERENTI_UFFICI = "SELECT COUNT(*) FROM referenti_uffici WHERE ufficio_id=? ";

    private final static String SELECT_COUNT_UFFICI = "SELECT count(*) FROM uffici";

    protected final static String CHECK_NAME = "SELECT COUNT(*) FROM uffici WHERE upper(descrizione)=? and ufficio_id!=?";
    // private final static String DELETE_CARTELLA_UFFICIO = "DELETE FROM
    // doc_cartelle WHERE ufficio_id=?";
};