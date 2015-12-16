package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.UtenteDAO;
import it.finsiel.siged.mvc.presentation.helper.AssegnatarioView;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoMenuVO;
import it.finsiel.siged.mvc.vo.organizzazione.PermessoRegistroVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class UtenteDAOjdbc implements UtenteDAO {

    protected final static String SELECT_UTENTI = "SELECT * FROM utenti ORDER BY COGNOME, NOME";

    protected final static String SELECT_USER_PROFILE = "SELECT * FROM utenti WHERE user_name=? AND passwd=?";

    protected final static String SELECT_USER_PROFILE_BYID = "SELECT * FROM utenti WHERE utente_id=?";

    protected final static String SELECT_USER_PROFILE_BY_USERNAME = "SELECT * FROM utenti WHERE user_name=?";

    protected final static String CHECK_USER_USERNAME = "SELECT COUNT(*) FROM utenti WHERE user_name=? and utente_id!=?";

    protected final static String INSERT_USER_PROFILE = "INSERT INTO utenti (utente_id, user_name, email, cognome, nome, codicefiscale, matricola, "
            + "passwd, flag_abilitato, data_fine_attivita, aoo_id, row_created_time, row_created_user, row_updated_time, row_updated_user, versione ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    protected final static String UPDATE_USER_PROFILE = "update utenti set user_name=?, email=?, cognome=?, nome=?, codicefiscale=?, matricola=?, "
            + "passwd=?, flag_abilitato=?, data_fine_attivita=?, aoo_id=?, row_updated_time=?, row_updated_user=?, versione=? where utente_id=?";

    protected final static String UPDATE_PASSWORD = "UPDATE UTENTI SET PASSWD=? WHERE UTENTE_ID=?";

    protected final static String SELECT_UFFICI_UTENTI = "SELECT distinct ufficio_id FROM permessi_utente WHERE utente_id=?";

    private final static String SELECT_COUNT_UTENTI = "SELECT count(*) FROM utenti";

    static Logger logger = Logger.getLogger(UtenteDAOjdbc.class.getName());

    IdentificativiDAOjdbc identificativiDAO;

    private JDBCManager jdbcMan = new JDBCManager();

    public Collection getUtenti() throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UtenteVO c = null;
        ArrayList utenti = new ArrayList();
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UTENTI);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                c = new UtenteVO();
                // come in getUtente
                c.setId(rs.getInt("utente_id"));
                c.setUsername(rs.getString("user_name"));
                c.setEmailAddress(rs.getString("email"));
                c.setCognome(rs.getString("cognome"));
                c.setNome(rs.getString("nome"));
                c.setCodiceFiscale(rs.getString("codicefiscale"));
                c.setMatricola(rs.getString("matricola"));
                c.setPassword(rs.getString("passwd"));
                c.setDataFineAttivita(rs.getDate("data_fine_attivita"));
                c.setAooId(rs.getInt("aoo_id"));
                c.setRowCreatedTime(rs.getDate("row_created_time"));
                c.setRowCreatedUser(rs.getString("row_created_user"));
                c.setRowUpdatedUser(rs.getString("row_updated_user"));
                c.setRowUpdatedTime(rs.getDate("row_updated_time"));
                c.setVersione(rs.getInt("versione"));
                c.setAbilitato(rs.getBoolean("flag_abilitato"));
                c.setReturnValue(ReturnValues.FOUND);
                utenti.add(c);

            }
        } catch (Exception e) {
            logger.error("getUtenti", e);
            throw new DataException("Cannot load getUtenti");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return utenti;
    }

    public UtenteVO getUtente(String username, String password)
            throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            return getUtente(connection, username, password);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " username :" + username
                    + " password:" + password);
        } finally {
            jdbcMan.close(connection);
        }
    }

    public UtenteVO getUtente(String username) throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            return getUtente(connection, username);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " username :" + username);
        } finally {
            jdbcMan.close(connection);
        }
    }

    /*
     * Get a Utente VO using username and pwd.
     */

    public UtenteVO getUtente(Connection connection, String username,
            String password) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UtenteVO c = new UtenteVO();
        c.setReturnValue(ReturnValues.UNKNOWN);
        try {
            pstmt = connection.prepareStatement(SELECT_USER_PROFILE);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                c = getUtente(connection, id);
            } else {
                c.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Load Utente Profile", e);
            throw new DataException("Cannot load the user profile");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return c;
    }

    public UtenteVO getUtente(Connection connection, String username)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UtenteVO c = new UtenteVO();
        c.setReturnValue(ReturnValues.UNKNOWN);
        try {
            pstmt = connection
                    .prepareStatement(SELECT_USER_PROFILE_BY_USERNAME);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                c = getUtente(connection, id);
            } else {
                c.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Load Utente Profile", e);
            throw new DataException("Cannot load the user profile");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return c;
    }

    /*
     * Get a Utente VO using the id.
     */
    public UtenteVO getUtente(int id) throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            return getUtente(connection, id);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " id :" + id);
        } finally {
            jdbcMan.close(connection);
        }
    }

    /*
     * Get a Utente VO using the id.
     */

    public UtenteVO getUtente(Connection connection, int id)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UtenteVO c = new UtenteVO();
        c.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (connection == null) {
                logger.warn("getUtente() - Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_USER_PROFILE_BYID);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                // per essere pi? performante questo codice ? stato duplicato in
                // un'altro metodo
                // in caso di aggiornamento aggiornare la parte nel metodo
                // getUtenti().
                c.setId(rs.getInt("utente_id"));
                c.setUsername(rs.getString("user_name"));
                c.setEmailAddress(rs.getString("email"));
                c.setCognome(rs.getString("cognome"));
                c.setNome(rs.getString("nome"));
                c.setCodiceFiscale(rs.getString("codicefiscale"));
                c.setMatricola(rs.getString("matricola"));
                c.setPassword(rs.getString("passwd"));
                c.setDataFineAttivita(rs.getDate("data_fine_attivita"));
                c.setAooId(rs.getInt("aoo_id"));
                c.setRowCreatedTime(rs.getDate("row_created_time"));
                c.setRowCreatedUser(rs.getString("row_created_user"));
                c.setRowUpdatedUser(rs.getString("row_updated_user"));
                c.setRowUpdatedTime(rs.getDate("row_updated_time"));
                c.setVersione(rs.getInt("versione"));
                c.setAbilitato(rs.getBoolean("flag_abilitato"));
                // fine
                c.setReturnValue(ReturnValues.FOUND);

            } else {
                c.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Load Utente Profile", e);
            throw new DataException("Cannot load the user profile");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return c;
    }

    /*
     * SINC PASSWORD BETWEEN LDAP AND APPLICATION
     */
    public void sincronizzaPassword(int utenteId, String newPwd)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(UPDATE_PASSWORD);
            pstmt.setString(1, newPwd);
            pstmt.setInt(2, utenteId);
            if (pstmt.executeUpdate() < 1)
                logger
                        .warn("Impossibile sincronizzare la password utente per id:"
                                + utenteId);

        } catch (Exception e) {
            throw new DataException(e.getMessage() + " id :" + utenteId);
        } finally {
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
    }

    public boolean isUsernameUsed(Connection connection, String username,
            int utenteId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean used = true;
        try {
            if (connection == null) {
                logger.warn("isUsernameUsed - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(CHECK_USER_USERNAME);
            pstmt.setString(1, username);
            pstmt.setInt(2, utenteId);
            rs = pstmt.executeQuery();
            rs.next();
            used = rs.getInt(1) > 0;
        } catch (Exception e) {
            logger.error("isUsernameUsed:" + username, e);
            throw new DataException("error.database.missing");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return used;
    }

    public UtenteVO newUtenteVO(Connection connection, UtenteVO utenteVO)
            throws DataException {
        PreparedStatement pstmt = null;
        if (isUsernameUsed(connection, utenteVO.getUsername(), utenteVO.getId()
                .intValue()))
            throw new DataException("username_gia_utilizzato");
        try {
            if (connection == null) {
                logger.warn("newUtenteVO - Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INSERT_USER_PROFILE);
            pstmt.setInt(1, utenteVO.getId().intValue());
            pstmt.setString(2, utenteVO.getUsername());
            pstmt.setString(3, utenteVO.getEmailAddress());
            pstmt.setString(4, utenteVO.getCognome());
            pstmt.setString(5, utenteVO.getNome());
            pstmt.setString(6, utenteVO.getCodiceFiscale());
            pstmt.setString(7, utenteVO.getMatricola());
            pstmt.setString(8, utenteVO.getPassword());
            pstmt.setInt(9, utenteVO.isAbilitato() ? 1 : 0);
            if (utenteVO.getDataFineAttivita() != null)
                pstmt.setDate(10, new Date(utenteVO.getDataFineAttivita()
                        .getTime()));
            else
                pstmt.setNull(10, Types.DATE);
            pstmt.setInt(11, utenteVO.getAooId());
            pstmt.setDate(12, new Date(System.currentTimeMillis()));
            pstmt.setString(13, utenteVO.getRowCreatedUser());
            pstmt.setDate(14, null);
            pstmt.setString(15, null);
            pstmt.setInt(16, 1);

            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Save Utente Profile", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }

        UtenteVO newVO = getUtente(connection, utenteVO.getId().intValue());
        newVO.setReturnValue(ReturnValues.SAVED);
        return newVO;

    }

    public UtenteVO updateUtenteVO(Connection connection, UtenteVO utenteVO)
            throws DataException {
        PreparedStatement pstmt = null;
        if (isUsernameUsed(connection, utenteVO.getUsername(), utenteVO.getId()
                .intValue()))
            throw new DataException("username_gia_utilizzato");
        try {
            if (connection == null) {
                logger.warn("updateUtenteVO - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(UPDATE_USER_PROFILE);
            // TODO: implementare il controllo della versione!!
            int versione = 1;

            pstmt.setString(1, utenteVO.getUsername());
            pstmt.setString(2, utenteVO.getEmailAddress());
            pstmt.setString(3, utenteVO.getCognome());
            pstmt.setString(4, utenteVO.getNome());
            pstmt.setString(5, utenteVO.getCodiceFiscale());
            pstmt.setString(6, utenteVO.getMatricola());
            pstmt.setString(7, utenteVO.getPassword());
            pstmt.setInt(8, utenteVO.isAbilitato() ? 1 : 0);
            if (utenteVO.getDataFineAttivita() != null)
                pstmt.setDate(9, new Date(utenteVO.getDataFineAttivita()
                        .getTime()));
            else
                pstmt.setNull(9, Types.DATE);
            pstmt.setInt(10, utenteVO.getAooId());
            pstmt.setDate(11, new Date(System.currentTimeMillis()));
            pstmt.setString(12, utenteVO.getRowUpdatedUser());
            pstmt.setInt(13, versione);

            pstmt.setInt(14, utenteVO.getId().intValue());
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Save Utente Profile", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        UtenteVO newVO = getUtente(connection, utenteVO.getId().intValue());
        newVO.setReturnValue(ReturnValues.SAVED);
        return newVO;
    }

    public Collection cercaUtenti(int aooId, String username, String cognome,
            String nome) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UtenteVO c = null;
        ArrayList utenti = new ArrayList();
        try {
            connection = jdbcMan.getConnection();

            String sql = "SELECT UTENTE_ID, USER_NAME, COGNOME, NOME, FLAG_ABILITATO, CODICEFISCALE FROM UTENTI WHERE aoo_id = "
                    + aooId;
            if (username != null)
                sql += " AND UPPER(user_name) like ?";
            if (cognome != null)
                sql += " AND UPPER(cognome) like ?";
            if (nome != null)
                sql += " AND UPPER(nome) like ?";
            sql += " ORDER BY cognome,nome";

            pstmt = connection.prepareStatement(sql);
            int i = 1;
            if (username != null)
                pstmt.setString(i++, username.toUpperCase() + "%");
            if (cognome != null)
                pstmt.setString(i++, cognome.toUpperCase() + "%");
            if (nome != null)
                pstmt.setString(i++, nome.toUpperCase() + "%");

            rs = pstmt.executeQuery();
            while (rs.next()) {
                c = new UtenteVO();
                c.setId(rs.getInt("utente_id"));
                c.setUsername(rs.getString("user_name"));
                c.setCognome(rs.getString("cognome"));
                c.setNome(rs.getString("nome"));
                c.setCodiceFiscale(rs.getString("codicefiscale"));
                // c.setDataFineAttivita(rs.getDate("data_fine_attivita"));
                c.setAbilitato(rs.getBoolean("flag_abilitato"));
                c.setReturnValue(ReturnValues.FOUND);
                utenti.add(c);
                logger.debug("Load Utente Profile" + c);
            }
        } catch (Exception e) {
            logger.error("Cerca Utente Profile", e);
            throw new DataException("Errore nella ricerca degli utenti.");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return utenti;
    }

    public Map getUfficiByUtente(Utente utente) throws DataException {
        Map uffici = new HashMap();

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AssegnatarioView ufficio = null;
        try {
            connection = jdbcMan.getConnection();
            String sqlStr = "SELECT distinct(u.*) FROM uffici U, permessi_utente P"
                    + " WHERE U.ufficio_id=P.ufficio_id AND utente_id=? AND aoo_id=? ORDER BY descrizione";
            pstmt = connection.prepareStatement(sqlStr);
            pstmt.setInt(1, utente.getValueObject().getId().intValue());
            pstmt.setInt(2, utente.getValueObject().getAooId());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ufficio = new AssegnatarioView();
                ufficio.setUfficioId(rs.getInt("ufficio_id"));
                ufficio.setDescrizioneUfficio(rs.getString("descrizione"));
                uffici.put(ufficio.getKey(), ufficio);
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

    public void nuovoPermessoMenu(Connection conn, PermessoMenuVO permesso)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (conn == null) {
                logger.warn("permessiUtente() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            String check = "SELECT utente_id FROM permessi_utente WHERE utente_id=? AND ufficio_id=? AND menu_id=?";
            pstmt = conn.prepareStatement(check);
            pstmt.setInt(1, permesso.getUtenteId());
            pstmt.setInt(2, permesso.getUfficioId());
            pstmt.setInt(3, permesso.getMenuId());
            rs = pstmt.executeQuery();
            if(rs.next()){
            	return;
            }
            // riassegno tutti i permessi utente PER
            String sqlStr = "INSERT INTO permessi_utente "
                    + " (utente_id, ufficio_id, menu_id, row_created_time,"
                    + " row_created_user, row_updated_user, row_updated_time, "
                    + "versione) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sqlStr);
            pstmt.setInt(1, permesso.getUtenteId());
            pstmt.setInt(2, permesso.getUfficioId());
            pstmt.setInt(3, permesso.getMenuId());
            pstmt.setTimestamp(4, new Timestamp(permesso.getRowCreatedTime()
                    .getTime()));
            pstmt.setString(5, permesso.getRowCreatedUser());
            pstmt.setString(6, permesso.getRowUpdatedUser());
            pstmt.setTimestamp(7, new Timestamp(permesso.getRowUpdatedTime()
                    .getTime()));
            pstmt.setInt(8, permesso.getVersione());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("permessiUtente", e);
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void cancellaPermessiUtente(Connection conn, int utenteId,
            int ufficioId, Utente utente) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("cancellaPermessiUtente - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            // cancello tutti i permessi utente
            String sqlStr = "DELETE FROM permessi_utente WHERE utente_id=? AND ufficio_id=?";
            pstmt = conn.prepareStatement(sqlStr);
            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, ufficioId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("cancellaPermessiUtente", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public String[] getPermessiRegistri(int utenteId) throws DataException {

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList registri = new ArrayList();
        String[] aRegistri = null;
        try {
            connection = jdbcMan.getConnection();
            String sqlStr = "SELECT registro_id FROM utenti$registri WHERE utente_id=?";
            pstmt = connection.prepareStatement(sqlStr);
            pstmt.setInt(1, utenteId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                registri.add(rs.getString("registro_id"));
            }
            if (registri != null) {
                aRegistri = new String[registri.size()];
                registri.toArray(aRegistri);
            }

        } catch (Exception e) {
            logger.error("Load getPermessiRegistri", e);
            throw new DataException("Cannot load getPermessiRegistri");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return aRegistri;

    }

    public void nuovoPermessoRegistro(Connection conn,
            PermessoRegistroVO permesso) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("permessiRegistriUtente() - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            // riassegno tutti i permessi utente PER
            String sqlStr = "INSERT INTO utenti$registri "
                    + " (utente_id, registro_id, row_created_time, "
                    + "row_created_user, row_updated_time, row_updated_user)"
                    + " VALUES (?, ?, ?, ?, ?, ?)";

            if (permesso.getRegistroId() > 0) {
                pstmt = conn.prepareStatement(sqlStr);
                pstmt.setInt(1, permesso.getUtenteId());
                pstmt.setInt(2, permesso.getRegistroId());
                pstmt.setTimestamp(3, new Timestamp(permesso
                        .getRowCreatedTime().getTime()));
                pstmt.setString(4, permesso.getRowCreatedUser());
                pstmt.setTimestamp(5, new Timestamp(permesso
                        .getRowUpdatedTime().getTime()));
                pstmt.setString(6, permesso.getRowUpdatedUser());
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("nuovoPermessoRegistro", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void cancellaPermessiRegistriUtente(Connection conn, int utenteId,
            Utente utente) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger
                        .warn("cancellaPermessiRegistriUtente - Invalid Connection :"
                                + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            // cancello tutti i permessi utente
            String sqlStr = "DELETE FROM utenti$registri WHERE utente_id=?";
            pstmt = conn.prepareStatement(sqlStr);
            pstmt.setInt(1, utenteId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("cancellaPermessiRegistriUtente", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public String[] getFunzioniByUfficioUtente(int utenteId, int ufficioId)
            throws DataException {
        Collection funzioni = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] aFunzioni = null;
        try {
            connection = jdbcMan.getConnection();
            String sqlStr = "SELECT p.menu_id FROM permessi_utente p, menu m"
                    + " WHERE p.menu_id = m.menu_id and link is not null and utente_id=? AND ufficio_id=?";
            pstmt = connection.prepareStatement(sqlStr);
            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, ufficioId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                funzioni.add(rs.getString("menu_id"));
            }
            aFunzioni = new String[funzioni.size()];
            funzioni.toArray(aFunzioni);

        } catch (Exception e) {
            logger.error("Load getFunzioniByUfficioUtente", e);
            throw new DataException("Cannot load getFunzioniByUfficioUtente");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return aFunzioni;
    }

    public Collection getUfficiByUtente(int utenteId) throws DataException {
        Collection uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // TODO: Organizzazione deve essere rimossa da questo file.
        // accoppia due classi la cui reciproca interazione non dovrebbe
        // esistere.

        Organizzazione org = Organizzazione.getInstance();
        Ufficio uff;

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_UTENTI);
            pstmt.setInt(1, utenteId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                uff = org.getUfficio(rs.getInt("ufficio_id"));
                uffici.add(uff);
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

    public long getCountUtenti() throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long num = -1;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_COUNT_UTENTI);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                num = rs.getLong(1);
            }
        } catch (Exception e) {
            logger.error("getCountUtenti", e);
            throw new DataException("Cannot load getCountUtenti");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return num;
    }

}