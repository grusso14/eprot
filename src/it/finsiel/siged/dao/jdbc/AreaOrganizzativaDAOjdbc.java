package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.AreaOrganizzativaDAO;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
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
import java.util.List;

import org.apache.log4j.Logger;

public class AreaOrganizzativaDAOjdbc implements AreaOrganizzativaDAO {
    static Logger logger = Logger.getLogger(AreaOrganizzativaDAOjdbc.class
            .getName());

    private JDBCManager jdbcMan = new JDBCManager();

    public void cancellaAreaOrganizzativa(Connection conn,
            int areaorganizzativaId) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger
                        .warn("cancellaAreaOrganizzativa() - Invalid Connection :"
                                + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = conn.prepareStatement(DELETE_AREA_ORGANIZZATIVA);
            pstmt.setInt(1, areaorganizzativaId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("cancellaAreaOrganizzativa", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public boolean esisteAreaOrganizzativa(String aooDescrizione, int aooId)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean esiste = false;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_AOO_BY_DESC);
            pstmt.setString(1, aooDescrizione.toUpperCase());
            pstmt.setInt(2, aooId);

            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                esiste = true;
            }
        } catch (Exception e) {
            logger.error("esisteAreaOrganizzativa", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return esiste;
    }

    public boolean isAreaOrganizzativaCancellabile(int aooId)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean cancellabile = true;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_AOO_REGISTRI);
            pstmt.setInt(1, aooId);
            rs = pstmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                cancellabile = false;
            } else {
                jdbcMan.close(rs);
                jdbcMan.close(pstmt);
                pstmt = connection.prepareStatement(SELECT_AOO_UFFICI);
                pstmt.setInt(1, aooId);
                rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    cancellabile = false;
                } else {
                    jdbcMan.close(rs);
                    jdbcMan.close(pstmt);
                    pstmt = connection.prepareStatement(SELECT_AOO_UTENTI);
                    pstmt.setInt(1, aooId);
                    rs = pstmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        cancellabile = false;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("isUfficioCancellabile", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return cancellabile;
    }

    public AreaOrganizzativaVO newAreaOrganizzativa(Connection conn,
            AreaOrganizzativaVO areaorganizzativaVO) throws DataException {
        PreparedStatement pstmt = null;
        areaorganizzativaVO.setReturnValue(ReturnValues.UNKNOWN);
        try {
            if (conn == null) {
                logger.warn("newAreaOrganizzativa() - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = conn.prepareStatement(INSERT_AREA_ORGANIZZATIVA);
            int n = 0;
            pstmt.setInt(++n, areaorganizzativaVO.getId().intValue());
            pstmt.setString(++n, areaorganizzativaVO.getCodi_aoo());
            pstmt.setString(++n, areaorganizzativaVO.getDescription());
            if (areaorganizzativaVO.getData_istituzione() == null) {
                pstmt.setDate(++n, new Date(System.currentTimeMillis()));
            } else {
                pstmt.setDate(++n, new Date(areaorganizzativaVO
                        .getData_istituzione().getTime()));
            }

            pstmt.setString(++n, areaorganizzativaVO.getResponsabile_nome());
            pstmt.setString(++n, areaorganizzativaVO.getResponsabile_cognome());
            pstmt.setString(++n, areaorganizzativaVO.getResponsabile_email());
            pstmt
                    .setString(++n, areaorganizzativaVO
                            .getResponsabile_telefono());
            if (areaorganizzativaVO.getData_soppressione() == null) {
                pstmt.setNull(++n, Types.DATE);
            } else {
                pstmt.setDate(++n, new Date(areaorganizzativaVO
                        .getData_soppressione().getTime()));
            }
            pstmt.setString(++n, areaorganizzativaVO.getTelefono());
            pstmt.setString(++n, areaorganizzativaVO.getFax());
            pstmt.setString(++n, areaorganizzativaVO.getIndi_dug());
            pstmt.setString(++n, areaorganizzativaVO.getIndi_toponimo());
            pstmt.setString(++n, areaorganizzativaVO.getIndi_civico());
            pstmt.setString(++n, areaorganizzativaVO.getIndi_cap());
            pstmt.setString(++n, areaorganizzativaVO.getIndi_comune());
            pstmt.setString(++n, areaorganizzativaVO.getEmail());
            pstmt.setString(++n, areaorganizzativaVO.getDipartimento_codice());
            pstmt.setString(++n, areaorganizzativaVO
                    .getDipartimento_descrizione());
            pstmt.setString(++n, areaorganizzativaVO.getTipo_aoo());
            pstmt.setInt(++n, areaorganizzativaVO.getProvincia_id());
            pstmt.setString(++n, areaorganizzativaVO.getCodi_documento_doc());
            pstmt.setInt(++n, areaorganizzativaVO.getFlag_pdf());
            pstmt.setInt(++n, areaorganizzativaVO.getAmministrazione_id());
            pstmt.setDate(++n, new Date(System.currentTimeMillis()));
            pstmt.setString(++n, areaorganizzativaVO.getRowCreatedUser());
            pstmt.setInt(++n, areaorganizzativaVO.getVersione());
            pstmt.setString(++n, areaorganizzativaVO.getPec_indirizzo());
            pstmt.setString(++n, areaorganizzativaVO.getPec_username());
            pstmt.setString(++n, areaorganizzativaVO.getPec_pwd());
            pstmt.setInt(++n, areaorganizzativaVO.getPecAbilitata() ? 1 : 0);
            pstmt.setString(++n, areaorganizzativaVO.getPec_ssl_port());
            pstmt.setString(++n, areaorganizzativaVO.getPec_pop3());
            pstmt.setString(++n, areaorganizzativaVO.getPec_smtp());
            pstmt.setString(++n, areaorganizzativaVO.getPec_smtp_port());
            pstmt.setString(++n, areaorganizzativaVO.getPn_indirizzo());
            pstmt.setString(++n, areaorganizzativaVO.getPn_username());
            pstmt.setString(++n, areaorganizzativaVO.getPn_pwd());
            pstmt.setString(++n, areaorganizzativaVO.getPn_ssl() ? "1" : "0");
            pstmt.setString(++n, areaorganizzativaVO.getPn_ssl_port());
            pstmt.setString(++n, areaorganizzativaVO.getPn_pop3());
            pstmt.setString(++n, areaorganizzativaVO.getPn_smtp());
            pstmt.setInt(++n, areaorganizzativaVO.getPecTimer());
            pstmt.setInt(++n, areaorganizzativaVO
                    .getDipendenzaTitolarioUfficio());
            pstmt.setInt(++n, areaorganizzativaVO.getTitolarioLivelloMinimo());
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("Save Area Organizzativa", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        areaorganizzativaVO = getAreaOrganizzativa(conn, areaorganizzativaVO
                .getId().intValue());
        areaorganizzativaVO.setReturnValue(ReturnValues.SAVED);
        return areaorganizzativaVO;
    }

    public AreaOrganizzativaVO updateAreaOrganizzativa(Connection conn,
            AreaOrganizzativaVO aooVO)

    throws DataException {
        PreparedStatement pstmt = null;
        aooVO.setReturnValue(ReturnValues.UNKNOWN);

        try {
            if (conn == null) {
                logger.warn("updateAreaOrganizzativa() - Invalid Connection :"
                        + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            int n = 0;

            pstmt = conn.prepareStatement(UPDATE_AREA_ORGANIZZATIVA);
            pstmt.setString(++n, aooVO.getCodi_aoo());
            pstmt.setString(++n, aooVO.getDescription());
            if (aooVO.getData_istituzione() == null) {
                pstmt.setDate(++n, new Date(System.currentTimeMillis()));
            } else {
                pstmt.setDate(++n, new Date(aooVO.getData_istituzione()
                        .getTime()));
            }
            pstmt.setString(++n, aooVO.getResponsabile_nome());
            pstmt.setString(++n, aooVO.getResponsabile_cognome());
            pstmt.setString(++n, aooVO.getResponsabile_email());
            pstmt.setString(++n, aooVO.getResponsabile_telefono());
            if (aooVO.getData_soppressione() == null) {
                pstmt.setNull(++n, Types.DATE);
            } else {
                pstmt.setDate(++n, new Date(aooVO.getData_soppressione()
                        .getTime()));
            }
            pstmt.setString(++n, aooVO.getTelefono());
            pstmt.setString(++n, aooVO.getFax());
            pstmt.setString(++n, aooVO.getIndi_dug());
            pstmt.setString(++n, aooVO.getIndi_toponimo());
            pstmt.setString(++n, aooVO.getIndi_civico());
            pstmt.setString(++n, aooVO.getIndi_cap());
            pstmt.setString(++n, aooVO.getIndi_comune());
            pstmt.setInt(++n, aooVO.getProvincia_id());
            pstmt.setString(++n, aooVO.getEmail());
            pstmt.setString(++n, aooVO.getDipartimento_codice());
            pstmt.setString(++n, aooVO.getDipartimento_descrizione());
            pstmt.setString(++n, aooVO.getTipo_aoo());
            pstmt.setString(++n, aooVO.getCodi_documento_doc());
            pstmt.setString(++n, aooVO.getRowUpdatedUser());
            if (aooVO.getRowUpdatedTime() == null) {
                pstmt.setDate(++n, new Date(System.currentTimeMillis()));
            } else {
                pstmt.setDate(++n,
                        new Date(aooVO.getRowUpdatedTime().getTime()));
            }
            pstmt.setString(++n, aooVO.getPec_indirizzo());
            pstmt.setString(++n, aooVO.getPec_username());
            pstmt.setString(++n, aooVO.getPec_pwd());
            pstmt.setInt(++n, aooVO.getPecAbilitata() ? 1 : 0);
            pstmt.setString(++n, aooVO.getPec_ssl_port());
            pstmt.setString(++n, aooVO.getPec_pop3());
            pstmt.setString(++n, aooVO.getPec_smtp());
            pstmt.setString(++n, aooVO.getPec_smtp_port());

            pstmt.setString(++n, aooVO.getPn_indirizzo());
            pstmt.setString(++n, aooVO.getPn_username());
            pstmt.setString(++n, aooVO.getPn_pwd());
            pstmt.setString(++n, aooVO.getPn_ssl() ? "1" : "0");
            pstmt.setString(++n, aooVO.getPn_ssl_port());
            pstmt.setString(++n, aooVO.getPn_pop3());
            pstmt.setString(++n, aooVO.getPn_smtp());
            pstmt.setInt(++n, aooVO.getPecTimer());
            pstmt.setInt(++n, aooVO.getVersione() + 1);
            pstmt.setInt(++n, aooVO.getDipendenzaTitolarioUfficio());
            pstmt.setInt(++n, aooVO.getTitolarioLivelloMinimo());

            pstmt.setInt(++n, aooVO.getId().intValue());
            pstmt.setInt(++n, aooVO.getVersione());

            pstmt.executeUpdate();
            aooVO.setReturnValue(ReturnValues.SAVED);

        } catch (Exception e) {
            logger.error("updateAreaOrganizzativa AreaOrganizzativa", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return aooVO;
    }

    public Collection getAreeOrganizzative() throws DataException {
        List areeOrganizzative = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_AOO);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                AreaOrganizzativaVO aoo = new AreaOrganizzativaVO();
                setCampiAOO(aoo, rs);
                areeOrganizzative.add(aoo);
                logger.debug("Caricata Area Organizzativa: " + aoo);
            }
        } catch (Exception e) {
            areeOrganizzative.clear();
            logger.error("Caricamento Aree Organizzative ", e);
            throw new DataException("Cannot load Aree Organizzative");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return areeOrganizzative;
    }

    public Collection getUffici(int areaorganizzativaId) throws DataException {
        List uffici = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_UFFICI_BY_AOO);
            pstmt.setInt(1, areaorganizzativaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                UfficioVO uVO = new UfficioVO();
                uVO.setAooId(rs.getInt("aoo_id"));
                uVO.setId(rs.getInt("ufficio_id"));
                uVO.setDescription(rs.getString("descrizione"));
                uffici.add(uVO);
                logger.debug("Caricata Lista Uffici Area Organizzativa: "
                        + areaorganizzativaId);
            }
        } catch (Exception e) {
            uffici.clear();
            logger.error("Caricamento Uffici", e);
            throw new DataException("Cannot load Uffici");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return uffici;
    }

    public AreaOrganizzativaVO getAreaOrganizzativa(int areaorganizzativaId)
            throws DataException {
        AreaOrganizzativaVO aooVO;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            aooVO = getAreaOrganizzativa(connection, areaorganizzativaId);
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return aooVO;
    }

    public AreaOrganizzativaVO getAreaOrganizzativa(Connection connection,
            int areaorganizzativaId) throws DataException {

        PreparedStatement pstmt = null;
        AreaOrganizzativaVO aooVO = new AreaOrganizzativaVO();
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getAreaOrganizzativa - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(SELECT_AREE_ORGANIZZATIVA);
            pstmt.setInt(1, areaorganizzativaId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                setCampiAOO(aooVO, rs);
            }

        } catch (Exception e) {
            logger.error("Area Organizzativa: getMenuProfilo", e);
            throw new DataException(
                    "Cannot load Area Organizzativa: getMenuProfilo");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return aooVO;
    }

    private void setCampiAOO(AreaOrganizzativaVO aooVO, ResultSet rs)
            throws SQLException {
        aooVO.setId(rs.getInt("aoo_id"));
        aooVO.setCodi_aoo(rs.getString("codi_aoo"));
        aooVO.setDescription(rs.getString("desc_aoo"));
        aooVO.setData_istituzione(rs.getDate("data_istituzione"));
        aooVO.setResponsabile_nome(rs.getString("responsabile_nome"));
        aooVO.setResponsabile_cognome(rs.getString("responsabile_cognome"));
        aooVO.setResponsabile_email(rs.getString("responsabile_email"));
        aooVO.setResponsabile_telefono(rs.getString("responsabile_telefono"));
        aooVO.setData_soppressione(rs.getDate("data_soppressione"));
        aooVO.setTelefono(rs.getString("telefono"));
        aooVO.setFax(rs.getString("fax"));
        aooVO.setIndi_dug(rs.getString("indi_dug"));
        aooVO.setIndi_toponimo(rs.getString("indi_toponimo"));
        aooVO.setIndi_civico(rs.getString("indi_civico"));
        aooVO.setIndi_cap(rs.getString("indi_cap"));
        aooVO.setIndi_comune(rs.getString("indi_comune"));
        aooVO.setEmail(rs.getString("email"));
        aooVO.setDipartimento_codice(rs.getString("dipartimento_codice"));
        aooVO.setDipartimento_descrizione(rs
                .getString("dipartimento_descrizione"));
        aooVO.setTipo_aoo(rs.getString("tipo_aoo"));
        aooVO.setProvincia_id(rs.getInt("provincia_id"));
        aooVO.setCodi_documento_doc(rs.getString("codi_documento_doc"));
        aooVO.setFlag_pdf(rs.getString("flag_pdf").charAt(0));
        aooVO.setAmministrazione_id(rs.getInt("amministrazione_id"));
        aooVO.setPec_indirizzo(rs.getString("pec_email"));
        aooVO.setPec_username(rs.getString("pec_username"));
        aooVO.setPec_pwd(rs.getString("pec_password"));
        aooVO.setPecAbilitata(rs.getBoolean("pec_abilitata"));
        aooVO.setPec_ssl_port(rs.getString("pec_ssl_port"));
        aooVO.setPec_pop3(rs.getString("pec_pop3_host"));
        aooVO.setPec_smtp(rs.getString("pec_smtp_host"));
        aooVO.setPec_smtp_port(rs.getString("pec_smtp_port"));
        aooVO.setPn_indirizzo(rs.getString("pn_email"));
        aooVO.setPn_username(rs.getString("pn_username"));
        aooVO.setPn_pwd(rs.getString("pn_password"));
        aooVO.setPn_ssl("1".equals(rs.getString("pn_use_ssl")) ? true : false);
        aooVO.setPn_ssl_port(rs.getString("pn_ssl_port"));
        aooVO.setPn_pop3(rs.getString("pn_pop3_host"));
        aooVO.setPn_smtp(rs.getString("pn_smtp_host"));

        aooVO.setRowCreatedTime(rs.getDate("row_created_time"));
        aooVO.setRowCreatedUser(rs.getString("row_created_user"));
        aooVO.setRowUpdatedUser(rs.getString("row_updated_user"));
        aooVO.setRowUpdatedTime(rs.getDate("row_updated_time"));
        aooVO.setPecTimer(rs.getInt("pec_timer"));
        aooVO.setDipendenzaTitolarioUfficio(rs
                .getInt("dipendenza_titolario_ufficio"));
        aooVO.setTitolarioLivelloMinimo(rs.getInt("titolario_livello_minimo"));
        aooVO.setVersione(rs.getInt("versione"));
    }

    public boolean isModificabileDipendenzaTitolarioUfficio(int aooId)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean found = true;
        String sql;
        try {
            connection = jdbcMan.getConnection();
            if (connection == null) {
                logger
                        .warn("isModificabileDipendenzaTitolarioUfficio - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            sql = "SELECT count(*) from protocolli where aoo_id =?";
            pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, aooId);
            rs = pstmt.executeQuery();
            rs.next();
            found = rs.getInt(1) > 0;
            if (!found) {
                jdbcMan.close(pstmt);
                sql = "SELECT count(*) from fascicoli where aoo_id =?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, aooId);
                rs = pstmt.executeQuery();
                rs.next();
                found = rs.getInt(1) > 0;
                if (!found) {
                    jdbcMan.close(pstmt);
                    sql = "SELECT count(*) from faldoni where aoo_id =?";
                    pstmt = connection.prepareStatement(sql);
                    pstmt.setInt(1, aooId);
                    rs = pstmt.executeQuery();
                    rs.next();
                    found = rs.getInt(1) > 0;
                    if (!found) {
                        jdbcMan.close(pstmt);
                        sql = "SELECT count(*) from procedimenti where aoo_id =?";
                        pstmt = connection.prepareStatement(sql);
                        pstmt.setInt(1, aooId);
                        rs = pstmt.executeQuery();
                        rs.next();
                        found = rs.getInt(1) > 0;
                    }
                }

            }
        } catch (Exception e) {
            logger.error("isModificabileDipendenzaTitolarioUfficio:", e);
            throw new DataException("error.database.missing");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return !found;
    }

    private final static String SELECT_AOO = "SELECT * FROM aree_organizzative order by desc_aoo";

    public final static String SELECT_AREE_ORGANIZZATIVA = "SELECT * FROM AREE_ORGANIZZATIVE "
            + " WHERE aoo_id=?";

    public final static String SELECT_UFFICI_BY_AOO = "SELECT * FROM UFFICI "
            + " WHERE aoo_id=?";

    public final static String INSERT_AREA_ORGANIZZATIVA = "INSERT INTO AREE_ORGANIZZATIVE"
            + " (aoo_id, codi_aoo, desc_aoo, data_istituzione, responsabile_nome, responsabile_cognome, "
            + " responsabile_email, responsabile_telefono, data_soppressione, telefono, fax, "
            + " indi_dug, indi_toponimo, indi_civico, indi_cap, indi_comune, email, "
            + " dipartimento_codice, dipartimento_descrizione, tipo_aoo, provincia_id, codi_documento_doc, flag_pdf, "
            + " amministrazione_id, row_created_time, row_created_user, versione,"
            + " pec_email, pec_username, pec_password, pec_abilitata, pec_ssl_port, pec_pop3_host, pec_smtp_host, pec_smtp_port, "
            + " pn_email, pn_username, pn_password, pn_use_ssl, pn_ssl_port,  pn_pop3_host, pn_smtp_host, pec_timer,"
            + " dipendenza_titolario_ufficio, titolario_livello_minimo)"
            + " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
            + "?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public final static String UPDATE_AREA_ORGANIZZATIVA = "UPDATE AREE_ORGANIZZATIVE"
            + " SET codi_aoo=?, desc_aoo=?, data_istituzione=?, responsabile_nome=?, "
            + " responsabile_cognome=?, responsabile_email=?, responsabile_telefono=?, data_soppressione=?, "
            + " telefono=?, fax=?, indi_dug=?, indi_toponimo=?, indi_civico=?, indi_cap=?, indi_comune=?, provincia_id=?, email=?,"
            + " dipartimento_codice=?, dipartimento_descrizione=?, tipo_aoo=?, codi_documento_doc=?, "
            + " row_updated_user=?, row_updated_time=?, " // ,versione=?
            + " pec_email=?, pec_username=?, pec_password=?, pec_abilitata=?,pec_ssl_port=?, pec_pop3_host=?,"
            + " pec_smtp_host=?, pec_smtp_port=?, pn_email=?, pn_username=?, pn_password=?, pn_use_ssl=?, pn_ssl_port=?,"
            + " pn_pop3_host=?, pn_smtp_host=?, pec_timer=?, versione=?, dipendenza_titolario_ufficio=?, titolario_livello_minimo=?"
            + " WHERE aoo_id=? and versione=?";

    // versione=?

    public final static String DELETE_AREA_ORGANIZZATIVA = "DELETE FROM AREE_ORGANIZZATIVE "
            + " WHERE aoo_id=?";

    private final static String SELECT_AOO_REGISTRI = "SELECT count(aoo_id) FROM REGISTRI where aoo_id=?";

    private final static String SELECT_AOO_UFFICI = "SELECT count(aoo_id) FROM UFFICI where aoo_id=?";

    private final static String SELECT_AOO_UTENTI = "SELECT count(aoo_id) FROM UTENTI where aoo_id=?";

    private final static String SELECT_AOO_BY_DESC = "SELECT count(aoo_id) FROM AREE_ORGANIZZATIVE where UPPER(desc_aoo)=? and aoo_id !=?";

}