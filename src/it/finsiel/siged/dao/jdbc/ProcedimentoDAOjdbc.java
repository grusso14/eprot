package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.ProcedimentoDAO;
import it.finsiel.siged.mvc.presentation.helper.ProcedimentoView;
import it.finsiel.siged.mvc.vo.MigrazioneVO;
import it.finsiel.siged.mvc.vo.lookup.TipoProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoFaldoneVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoFascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.StringUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class ProcedimentoDAOjdbc implements ProcedimentoDAO {
    static Logger logger = Logger
            .getLogger(ProcedimentoDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();

    public ProcedimentoVO newProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            vo.setRowCreatedTime(new Date(System.currentTimeMillis()));

            pstmt = connection.prepareStatement(INSERT_PROCEDIMENTO);
            pstmt.setInt(1, vo.getId().intValue());
            if (vo.getDataAvvio() != null) {
                pstmt.setDate(2, new Date(vo.getDataAvvio().getTime()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setInt(3, vo.getUfficioId());
            if (vo.getTitolarioId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, vo.getTitolarioId());
            }
            pstmt.setInt(5, vo.getStatoId());
            pstmt.setInt(6, vo.getTipoFinalitaId());
            pstmt.setString(7, vo.getOggetto());
            pstmt.setInt(8, vo.getTipoProcedimentoId());
            pstmt.setInt(9, vo.getReferenteId());
            pstmt.setString(10, vo.getPosizione());
            if (vo.getDataEvidenza() != null) {
                pstmt.setDate(11, new Date(vo.getDataEvidenza().getTime()));
            } else {
                pstmt.setNull(11, Types.DATE);
            }
            pstmt.setString(12, vo.getNote());
            pstmt.setInt(13, vo.getProtocolloId());
            pstmt.setString(14, vo.getNumeroProcedimento());
            pstmt.setInt(15, vo.getAnno());
            pstmt.setInt(16, vo.getNumero());
            if (vo.getRowCreatedTime() != null) {
                pstmt.setDate(17, new Date(vo.getRowCreatedTime().getTime()));
            } else {
                pstmt.setNull(17, Types.DATE);
            }
            pstmt.setString(18, vo.getRowCreatedUser());
            pstmt.setString(19, vo.getRowUpdatedUser());
            pstmt.setNull(20, Types.DATE);
            pstmt.setInt(21, 0);
            pstmt.setString(22, vo.getResponsabile());
            pstmt.setInt(23, vo.getAooId());
            pstmt.executeUpdate();
            vo = getProcedimentoById(connection, vo.getId().intValue());
            vo.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("newProcedimento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    // STORIA PROCEDIMENTO
    public Collection getStoriaProcedimenti(int procedimentoId)
            throws DataException {
        ArrayList storiaProcedimento = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            String sqlStoriaProcedimento = "  SELECT procedimento_id, STORIA_PROCEDIMENTI.aoo_id, "
                    + "STORIA_PROCEDIMENTI.oggetto, STORIA_PROCEDIMENTI.ufficio_id, STORIA_PROCEDIMENTI.titolario_id, "
                    + "STORIA_PROCEDIMENTI.row_created_time, STORIA_PROCEDIMENTI.row_created_user, "
                    + "STORIA_PROCEDIMENTI.row_updated_user,"
                    + "STORIA_PROCEDIMENTI.row_updated_time, "
                    + "STORIA_PROCEDIMENTI.note, STORIA_PROCEDIMENTI.numero_procedimento, "
                    + "STORIA_PROCEDIMENTI.anno, STORIA_PROCEDIMENTI.numero, STORIA_PROCEDIMENTI.data_avvio,"
                    + "STORIA_PROCEDIMENTI.data_evidenza, "
                    + "STORIA_PROCEDIMENTI.stato_id, STORIA_PROCEDIMENTI.versione, STORIA_PROCEDIMENTI.posizione_id AS posizione,"
                    + "uffici.descrizione AS ufficio "
                    + "FROM STORIA_PROCEDIMENTI , uffici WHERE    procedimento_id=? "
                    + "AND STORIA_PROCEDIMENTI.ufficio_id=Uffici.ufficio_id "
                    + "ORDER BY VERSIONE desc";

            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(sqlStoriaProcedimento);
            pstmt.setInt(1, procedimentoId);
            rs = pstmt.executeQuery();
            ProcedimentoView procedimento;
            while (rs.next()) {
                procedimento = new ProcedimentoView();
                procedimento.setProcedimentoId(rs.getInt("procedimento_id"));
                procedimento.setAooId(rs.getInt("aoo_id"));
                procedimento.setOggetto(rs.getString("oggetto"));
                procedimento.setDescUfficioId(rs.getString("ufficio"));
                procedimento.setTitolarioId(rs.getInt("titolario_id"));
                procedimento.setNote(rs.getString("note"));
                procedimento.setNumeroProcedimento(rs
                        .getInt("numero_procedimento"));
                procedimento.setAnno(rs.getInt("anno"));
                procedimento.setNumero(rs.getInt("numero"));

                if (rs.getDate("data_avvio") != null)

                    procedimento.setDataAvvioStr(DateUtil.formattaData(rs
                            .getDate("data_avvio").getTime()));
                procedimento.setDataAvvio(rs.getDate("data_avvio"));
                /*
                 * procedimento.setDataAvvio(Date.valueOf(rs
                 * .getString("data_avvio")));
                 */
                if (rs.getDate("data_evidenza") != null)
                    procedimento.setDataEvidenza(DateUtil.formattaData(rs
                            .getDate("data_evidenza").getTime()));

                if (rs.getString("posizione").equals("T")) {
                    procedimento.setPosizione("In Trattazione");
                } else if (rs.getString("posizione").equals("A")) {
                    procedimento.setPosizione("Agli Atti");
                } else if (rs.getString("posizione").equals("E")) {
                    procedimento.setPosizione("In Evidenza");
                } else {
                    procedimento.setPosizione(rs.getString("posizione"));
                }
                procedimento.setVersione(rs.getInt("versione"));

                storiaProcedimento.add(procedimento);
            }
        } catch (Exception e) {
            logger.error("getStoriaProcedimento", e);
            throw new DataException("Cannot load getStoriaProcedimento");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return storiaProcedimento;

    }

    public ProcedimentoVO getProcedimentoByIdVersione(int id, int versione)
            throws DataException {
        ProcedimentoVO procedimentoVO = new ProcedimentoVO();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            procedimentoVO = getProcedimentoByIdVersione(connection, id,
                    versione);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " ProcedimentoId :" + id
                    + " Versione :" + versione);
        } finally {
            jdbcMan.close(connection);
        }
        return procedimentoVO;
    }

    public ProcedimentoVO getProcedimentoByIdVersione(Connection connection,
            int id, int versione) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ProcedimentoVO procedimento = new ProcedimentoVO();
        try {
            if (connection == null) {
                logger
                        .warn("getProcedimentoByIdVersione() - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            /*
             * String sqlProcedimento ="SELECT f.* " + "FROM STORIA_PROCEDIMENTI
             * f" + "WHERE procedimento_id = ? and f.versione = ? ";
             */
            pstmt = connection
                    .prepareStatement(SELECT_PROCEDIMENTO_BY_ID_VERSIONE);
            pstmt.setInt(1, id);
            pstmt.setInt(2, versione);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                procedimento = getProcedimento(rs);
                procedimento.setReturnValue(ReturnValues.FOUND);
            } else {
                procedimento.setReturnValue(ReturnValues.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.error("Load Versione faldone by ID", e);
            throw new DataException("Cannot load Versione procedimento by ID");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return procedimento;
    }

    private ProcedimentoVO getProcedimento(ResultSet rs) throws DataException {
        ProcedimentoVO vo = new ProcedimentoVO();
        try {
            vo.setId(rs.getInt("procedimento_id"));
            vo.setDataAvvio(rs.getDate("data_avvio"));
            vo.setDataEvidenza(rs.getDate("data_evidenza"));
            vo.setUfficioId(rs.getInt("ufficio_id"));
            vo.setTitolarioId(rs.getInt("titolario_id"));
            vo.setStatoId(rs.getInt("stato_id"));
            vo.setTipoFinalitaId(rs.getInt("tipo_finalita_id"));
            vo.setOggetto(rs.getString("oggetto"));
            vo.setTipoProcedimentoId(rs.getInt("tipo_procedimento"));
            vo.setTipoProcedimentoDesc(rs.getString("tipo"));
            vo.setReferenteId(rs.getInt("referente_id"));

            /*
             * if (rs.getString("posizione_id").equals("T")) {
             * vo.setPosizione("In Trattazione"); } else if
             * (rs.getString("posizione_id").equals("A")) {
             * vo.setPosizione("Agli Atti"); } else if
             * (rs.getString("posizione_id").equals("E")) { vo.setPosizione("In
             * Evidenza"); } else {
             * vo.setPosizione(rs.getString("posizione_id")); }
             */
            vo.setPosizione(rs.getString("posizione_id"));
            vo.setDataEvidenza(rs.getDate("data_evidenza"));
            vo.setNote("note");
            vo.setProtocolloId(rs.getInt("protocollo_id"));
            /* Modifica del 03032006 Greco Bosco */

            vo.setNumeroProcedimento(rs.getString("anno")
                    + StringUtil.formattaNumeroProcedimento(rs
                            .getString("numero"), 7));
            // vo.setNumeroProcedimento(
            // rs.getString("numero_procedimento"));
            vo.setAnno(rs.getInt("anno"));
            vo.setNumero(rs.getInt("numero"));
            vo.setRowCreatedTime(rs.getDate("row_created_time"));
            vo.setRowCreatedUser(rs.getString("row_created_user"));
            vo.setRowUpdatedUser(rs.getString("row_updated_user"));
            vo.setRowUpdatedTime(rs.getDate("row_updated_time"));
            vo.setVersione(rs.getInt("versione"));
            vo.setResponsabile(rs.getString("responsabile"));

        } catch (SQLException e) {
            logger.error("Load getFaldone()", e);
            throw new DataException("Cannot load getProcedimento()");
        }
        return vo;
    }

    public ProcedimentoVO newStoriaProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            vo.setRowCreatedTime(new Date(System.currentTimeMillis()));

            pstmt = connection.prepareStatement(INSERT_STORIA_PROCEDIMENTO);
            pstmt.setInt(1, vo.getId().intValue());
            if (vo.getDataAvvio() != null) {
                pstmt.setDate(2, new Date(vo.getDataAvvio().getTime()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setInt(3, vo.getUfficioId());
            if (vo.getTitolarioId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, vo.getTitolarioId());
            }
            pstmt.setInt(5, vo.getStatoId());
            pstmt.setInt(6, vo.getTipoFinalitaId());
            pstmt.setString(7, vo.getOggetto());
            pstmt.setInt(8, vo.getTipoProcedimentoId());
            pstmt.setInt(9, vo.getReferenteId());
            pstmt.setString(10, vo.getPosizione());
            if (vo.getDataEvidenza() != null) {
                pstmt.setDate(11, new Date(vo.getDataEvidenza().getTime()));
            } else {
                pstmt.setNull(11, Types.DATE);
            }
            pstmt.setString(12, vo.getNote());
            pstmt.setInt(13, vo.getProtocolloId());
            pstmt.setString(14, vo.getNumeroProcedimento());
            pstmt.setInt(15, vo.getAnno());
            pstmt.setInt(16, vo.getNumero());
            if (vo.getRowCreatedTime() != null) {
                pstmt.setDate(17, new Date(vo.getRowCreatedTime().getTime()));
            } else {
                pstmt.setNull(17, Types.DATE);
            }
            pstmt.setString(18, vo.getRowCreatedUser());
            pstmt.setString(19, vo.getRowUpdatedUser());
            pstmt.setNull(20, Types.DATE);
            pstmt.setInt(21, 0);
            pstmt.setString(22, vo.getResponsabile());
            pstmt.setInt(23, vo.getAooId());
            pstmt.executeUpdate();
            vo = getProcedimentoById(connection, vo.getId().intValue());
            vo.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("newProcedimento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    public Collection getProcedimentiAnnoNumero(Connection connection)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Collection procedimenti = new ArrayList();

        try {
            if (connection == null) {
                logger
                        .warn("getProcedimentiAnnoNumero() - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            String sql = "SELECT ANNO,Max(NUMERO)AS Numero  FROM procedimenti GROUP BY ANNO";
            pstmt = connection.prepareStatement(sql);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                MigrazioneVO migrazione = new MigrazioneVO();
                migrazione.setAnno(rs.getInt(1));
                migrazione.setNumero(rs.getInt(2));
                procedimenti.add(migrazione);
            }

        } catch (Exception e) {
            logger.error("Load getProcedimentiAnnoNumero ", e);
            throw new DataException("Cannot load Procedimenti by ID");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return procedimenti;
    }

    private void archiviaVersione(Connection connection, int procedimentoId,
            int versione) throws DataException {

        String[] tables = { "procedimenti" };
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("storia Procedimenti- Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            // ProcedimentoVO procedimentoVO = new ProcedimentoVO();

            for (int i = 0; i < tables.length; i++) {
                String sql = "INSERT INTO storia_" + tables[i]
                        + " SELECT * FROM " + tables[i]
                        + " WHERE procedimento_id = ?";

                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, procedimentoId);
                int r = pstmt.executeUpdate();
                logger.info(sql + "record inseriti in storia_" + tables[i]
                        + ": " + r);
                jdbcMan.close(pstmt);
                /*
                 * sql = "UPDATE " + tables[i] + " SET versione = ?" + " WHERE
                 * procedimento_id = ?"; pstmt =
                 * connection.prepareStatement(sql); pstmt.setInt(1, versione);
                 * pstmt.setInt(2, procedimentoId); logger.info(sql);
                 * pstmt.executeUpdate(); //procedimentoVO =
                 * getProcedimentoById(procedimentoId);
                 * 
                 * //updateProcedimento(connection, procedimentoVO);
                 */
                jdbcMan.close(pstmt);
            }

        } catch (Exception e) {
            logger.error("storia procedimento" + procedimentoId, e);
            throw new DataException("Cannot insert Storia Procedimento");

        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public ProcedimentoVO updateProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            archiviaVersione(connection, vo.getId().intValue(), vo
                    .getVersione());
            vo.setRowCreatedTime(new Date(System.currentTimeMillis()));

            pstmt = connection.prepareStatement(UPDATE_PROCEDIMENTO);

            if (vo.getDataAvvio() != null) {
                pstmt.setDate(1, new Date(vo.getDataAvvio().getTime()));
            } else {
                pstmt.setNull(1, Types.DATE);
            }
            pstmt.setInt(2, vo.getUfficioId());
            if (vo.getTitolarioId() > 0) {
                pstmt.setInt(3, vo.getTitolarioId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, vo.getStatoId());
            pstmt.setInt(5, vo.getTipoFinalitaId());
            pstmt.setString(6, vo.getOggetto());
            pstmt.setInt(7, vo.getTipoProcedimentoId());
            pstmt.setInt(8, vo.getReferenteId());
            pstmt.setString(9, vo.getPosizione());
            if (vo.getDataEvidenza() != null) {
                pstmt.setDate(10, new Date(vo.getDataEvidenza().getTime()));
            } else {
                pstmt.setNull(10, Types.DATE);
            }
            pstmt.setString(11, vo.getNote());
            pstmt.setInt(12, vo.getProtocolloId());
            pstmt.setString(13, vo.getRowUpdatedUser());
            if (vo.getRowUpdatedTime() != null) {
                pstmt.setDate(14, new Date(vo.getRowUpdatedTime().getTime()));
            } else {
                pstmt.setNull(14, Types.DATE);
            }
            pstmt.setInt(15, vo.getVersione() + 1);
            pstmt.setString(16, vo.getResponsabile());
            pstmt.setInt(17, vo.getAooId());
            pstmt.setInt(18, vo.getId().intValue());
            pstmt.executeUpdate();
            vo = getProcedimentoById(connection, vo.getId().intValue());
            vo.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("updateProcedimento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    public ProcedimentoVO aggiornaProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            vo.setRowCreatedTime(new Date(System.currentTimeMillis()));

            pstmt = connection.prepareStatement(UPDATE_PROCEDIMENTO);

            if (vo.getDataAvvio() != null) {
                pstmt.setDate(1, new Date(vo.getDataAvvio().getTime()));
            } else {
                pstmt.setNull(1, Types.DATE);
            }
            pstmt.setInt(2, vo.getUfficioId());
            if (vo.getTitolarioId() > 0) {
                pstmt.setInt(3, vo.getTitolarioId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setInt(4, vo.getStatoId());
            pstmt.setInt(5, vo.getTipoFinalitaId());
            pstmt.setString(6, vo.getOggetto());
            pstmt.setInt(7, vo.getTipoProcedimentoId());
            pstmt.setInt(8, vo.getReferenteId());
            pstmt.setString(9, vo.getPosizione());
            if (vo.getDataEvidenza() != null) {
                pstmt.setDate(10, new Date(vo.getDataEvidenza().getTime()));
            } else {
                pstmt.setNull(10, Types.DATE);
            }
            pstmt.setString(11, vo.getNote());
            pstmt.setInt(12, vo.getProtocolloId());
            pstmt.setString(13, vo.getRowUpdatedUser());
            if (vo.getRowUpdatedTime() != null) {
                pstmt.setDate(14, new Date(vo.getRowUpdatedTime().getTime()));
            } else {
                pstmt.setNull(14, Types.DATE);
            }
            pstmt.setInt(15, vo.getVersione());
            // ?? pstmt.setInt(15, vo.getVersione() + 1);
            pstmt.setString(16, vo.getResponsabile());
            pstmt.setInt(17, vo.getAooId());
            pstmt.setInt(18, vo.getId().intValue());
            pstmt.executeUpdate();
            vo = getProcedimentoById(connection, vo.getId().intValue());
            vo.setReturnValue(ReturnValues.SAVED);
        } catch (Exception e) {
            logger.error("updateProcedimento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    public ProcedimentoVO aggiornaProcedimento(ProcedimentoVO procedimento)
            throws DataException {
        ProcedimentoVO procedimentoSalvato = new ProcedimentoVO();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            procedimentoSalvato = aggiornaProcedimento(connection, procedimento);
        } catch (Exception e) {
            throw new DataException(e.getMessage()
                    + " aggiornaprocedimento procedimentoId :"
                    + procedimento.getId().intValue());
        } finally {
            jdbcMan.close(connection);
        }
        return procedimentoSalvato;
    }

    public int getMaxNumProcedimento(Connection connection, int aooId, int anno)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int max = 1;
        try {
            if (connection == null) {
                logger.warn("getMaxNumProcedimento() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_ULTIMO_PROCEDIMENTO);
            pstmt.setInt(1, aooId);
            pstmt.setInt(2, anno);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                max = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            logger.error("getMaxNumProcedimento", e);
            throw new DataException("Cannot load getMaxNumProcedimento");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return max;
    }

    public void inserisciFaldoni(Connection connection, Integer[] ids,
            int procedimentoId) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            for (int i = 0; i < ids.length; i++) {
                pstmt = connection
                        .prepareStatement(INSERT_PROCEDIMENTO_FALDONI);
                pstmt.setInt(1, procedimentoId);
                pstmt.setInt(2, ids[i].intValue());
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }
        } catch (Exception e) {
            logger.error("inserisciFaldoni", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void inserisciProcedimentoFaldone(Connection connection,
            ProcedimentoFaldoneVO procedimentoFaldoneVO) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(INSERT_PROCEDIMENTO_FALDONI);
            pstmt.setInt(1, procedimentoFaldoneVO.getProcedimentoId());
            pstmt.setInt(2, procedimentoFaldoneVO.getFaldoneId());
            pstmt.executeUpdate();
            jdbcMan.close(pstmt);

        } catch (Exception e) {
            logger.error("inserisciFaldoni", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void inserisciFascicoli(Connection connection, Integer[] ids,
            int procedimentoId) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            for (int i = 0; i < ids.length; i++) {
                pstmt = connection
                        .prepareStatement(INSERT_PROCEDIMENTO_FASCICOLI);
                pstmt.setInt(1, procedimentoId);
                pstmt.setInt(2, ids[i].intValue());
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }
        } catch (Exception e) {
            logger.error("inserisciFascicoli", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void inserisciFascicoloProcedimento(Connection connection,
            ProcedimentoFascicoloVO procedimentofascicolo) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(INSERT_PROCEDIMENTO_FASCICOLI);
            pstmt.setInt(1, procedimentofascicolo.getProcedimentoId());
            pstmt.setInt(2, procedimentofascicolo.getFascicoloId());
            pstmt.executeUpdate();
            jdbcMan.close(pstmt);

        } catch (Exception e) {
            logger.error("inserisciFascicoli", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void inserisciProtocolli(Connection connection, ArrayList ids)
            throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            for (Iterator it = ids.iterator(); it.hasNext();) {
                ProtocolloProcedimentoVO ppVO = (ProtocolloProcedimentoVO) it
                        .next();
                pstmt = connection
                        .prepareStatement(INSERT_PROCEDIMENTO_PROTOCOLLI);
                pstmt.setInt(1, ppVO.getProcedimentoId());
                pstmt.setInt(2, ppVO.getProtocolloId());
                pstmt.setDate(3, new Date(ppVO.getRowCreatedTime().getTime()));
                pstmt.setString(4, ppVO.getRowCreatedUser());
                pstmt.setString(5, ppVO.getRowUpdatedUser());
                pstmt.setDate(6, new Date(ppVO.getRowUpdatedTime().getTime()));
                pstmt.setInt(7, ppVO.getVersione());
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }
        } catch (Exception e) {
            logger.error("inserisciProtocolli(Connection, Integer[], int)", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void cancellaFascicoli(Connection connection, int procedimentoId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(DELETE_PROCEDIMENTO_FASCICOLI);
            pstmt.setInt(1, procedimentoId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("cancellaFascicoli", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void cancellaProtocolli(Connection connection, int procedimentoId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(DELETE_PROCEDIMENTO_PROTOCOLLI);
            pstmt.setInt(1, procedimentoId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("cancellaProtocolli", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void cancellaFaldoni(Connection connection, int procedimentoId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Invalid Connection :" + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(DELETE_PROCEDIMENTO_FALDONI);
            pstmt.setInt(1, procedimentoId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("cancellaFaldoni", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public ProcedimentoVO getProcedimentoById(int procedimentoId)
            throws DataException {
        ProcedimentoVO vo = new ProcedimentoVO();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            vo = getProcedimentoById(connection, procedimentoId);
        } catch (Exception e) {
            logger.error("getProvedimentoById", e);
            throw new DataException("getProvedimentoById fallito!");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return vo;
    }

    public ProcedimentoVO getProcedimentoById(Connection connection,
            int procedimentoId) throws DataException {

        PreparedStatement pstmt = null;
        ProcedimentoVO vo = new ProcedimentoVO();
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProvedimentoById - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(SELECT_PROCEDIMENTO);
            pstmt.setInt(1, procedimentoId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                vo.setId(rs.getInt("procedimento_id"));
                vo.setDataAvvio(rs.getDate("data_avvio"));
                vo.setDataAvvioPro(DateUtil.formattaData(rs.getDate(
                        "data_avvio").getTime()));
                vo.setUfficioId(rs.getInt("ufficio_id"));
                vo.setTitolarioId(rs.getInt("titolario_id"));
                vo.setStatoId(rs.getInt("stato_id"));
                vo.setTipoFinalitaId(rs.getInt("tipo_finalita_id"));
                vo.setOggetto(rs.getString("oggetto"));
                vo.setTipoProcedimentoId(rs.getInt("tipo_procedimento"));
                vo.setTipoProcedimentoDesc(rs.getString("tipo"));
                vo.setReferenteId(rs.getInt("referente_id"));

                vo.setDataEvidenza(rs.getDate("data_evidenza"));
                /*
                 * if (rs.getString("posizione_id").equals("T")) {
                 * vo.setPosizione("In Trattazione"); } else if
                 * (rs.getString("posizione_id").equals("A")) {
                 * vo.setPosizione("Agli Atti"); } else if
                 * (rs.getString("posizione_id").equals("E")) {
                 * vo.setPosizione("In Evidenza"); } else {
                 * vo.setPosizione(rs.getString("posizione_id")); }
                 */
                vo.setPosizione(rs.getString("posizione_id"));
                vo.setDataEvidenza(rs.getDate("data_evidenza"));
                vo.setNote(rs.getString("note"));
                vo.setProtocolloId(rs.getInt("protocollo_id"));
                /* Modifica del 03032006 Greco Bosco */

                vo.setNumeroProcedimento(rs.getString("anno")
                        + StringUtil.formattaNumeroProcedimento(rs
                                .getString("numero"), 7));
                // vo.setNumeroProcedimento(
                // rs.getString("numero_procedimento"));
                vo.setAnno(rs.getInt("anno"));
                vo.setNumero(rs.getInt("numero"));
                vo.setRowCreatedTime(rs.getDate("row_created_time"));
                vo.setRowCreatedUser(rs.getString("row_created_user"));
                vo.setRowUpdatedUser(rs.getString("row_updated_user"));
                vo.setRowUpdatedTime(rs.getDate("row_updated_time"));
                vo.setVersione(rs.getInt("versione"));
                vo.setResponsabile(rs.getString("responsabile"));
                vo.setReturnValue(ReturnValues.FOUND);
            } else {
                vo.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("getProvedimentoById", e);
            throw new DataException("getProvedimentoById");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    public ProcedimentoVO getProcedimentoByAnnoNumero(Connection connection,
            int anno, int numero) throws DataException {

        PreparedStatement pstmt = null;
        ProcedimentoVO vo = new ProcedimentoVO();
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProvedimentoById - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection
                    .prepareStatement(SELECT_PROCEDIMENTO_BY_ANNO_NUMERO);
            pstmt.setInt(1, anno);
            pstmt.setInt(2, numero);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                vo.setId(rs.getInt("procedimento_id"));
                vo.setDataAvvio(rs.getDate("data_avvio"));
                vo.setUfficioId(rs.getInt("ufficio_id"));
                vo.setTitolarioId(rs.getInt("titolario_id"));
                vo.setStatoId(rs.getInt("stato_id"));
                vo.setTipoFinalitaId(rs.getInt("tipo_finalita_id"));
                vo.setOggetto(rs.getString("oggetto"));
                vo.setTipoProcedimentoId(rs.getInt("tipo_procedimento"));
                vo.setReferenteId(rs.getInt("referente_id"));
                vo.setPosizione(rs.getString("posizione_id"));
                vo.setDataEvidenza(rs.getDate("data_evidenza"));
                vo.setNote("note");
                vo.setProtocolloId(rs.getInt("protocollo_id"));
                /* Modifica del 03032006 Greco Bosco */

                vo.setNumeroProcedimento(rs.getString("anno")
                        + StringUtil.formattaNumeroProcedimento(rs
                                .getString("numero"), 6));
                // vo.setNumeroProcedimento(
                // rs.getString("numero_procedimento"));
                vo.setAnno(rs.getInt("anno"));
                vo.setNumero(rs.getInt("numero"));
                vo.setRowCreatedTime(rs.getDate("row_created_time"));
                vo.setRowCreatedUser(rs.getString("row_created_user"));
                vo.setRowUpdatedUser(rs.getString("row_updated_user"));
                vo.setRowUpdatedTime(rs.getDate("row_updated_time"));
                vo.setVersione(rs.getInt("versione"));
                vo.setResponsabile(rs.getString("responsabile"));
                vo.setReturnValue(ReturnValues.FOUND);
            } else {
                vo.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("getProvedimentoById", e);
            throw new DataException("getProvedimentoById");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    /**
     * 
     * @param connection
     * @param procedimentoId
     * @return collection of Integer of faldone_id
     * @throws DataException
     */

    public Collection getProcedimentoFaldoni(Connection connection,
            int procedimentoId) throws DataException {

        PreparedStatement pstmt = null;
        ArrayList ids = new ArrayList();
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProvedimentoFaldoni - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(SELECT_PROCEDIMENTO_FALDONI);
            pstmt.setInt(1, procedimentoId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Integer id = new Integer(rs.getInt("faldone_id"));
                ids.add(id);
            }
        } catch (Exception e) {
            logger.error("getProvedimentoFaldoni", e);
            throw new DataException("getProvedimentoFaldoni");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return ids;
    }

    public Collection getProcedimentoFascicoli(Connection connection,
            int procedimentoId) throws DataException {

        PreparedStatement pstmt = null;
        ArrayList ids = new ArrayList();
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProvedimentoFascicoli - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(SELECT_PROCEDIMENTO_FASCICOLI);
            pstmt.setInt(1, procedimentoId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Integer id = new Integer(rs.getInt("fascicolo_id"));
                ids.add(id);
            }
        } catch (Exception e) {
            logger.error("getProvedimentoFascicoli", e);
            throw new DataException("getProvedimentoFascicoli");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return ids;
    }

    public Collection getProcedimentoProtocolli(Connection connection,
            int procedimentoId) throws DataException {

        PreparedStatement pstmt = null;
        ArrayList ids = new ArrayList();
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("getProvedimentoFascicoli - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(SELECT_PROCEDIMENTO_PROTOCOLLI);
            pstmt.setInt(1, procedimentoId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Integer id = new Integer(rs.getInt("protocollo_id"));
                ids.add(id);
            }
        } catch (Exception e) {
            logger.error("getProcedimentoProtocolli", e);
            throw new DataException("getProcedimentoProtocolli");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return ids;
    }

    public void setStatoProtocolloAssociato(Connection connection,
            int protocolloId, String stato) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("getProvedimentoFascicoli - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(UPDATE_STATO_PROTOCOLLO);
            pstmt.setString(1, stato);
            pstmt.setInt(2, protocolloId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("setStatoProtocolloAssociato", e);
            throw new DataException("setStatoProtocolloAssociato");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public int contaProcedimenti(Utente utente, HashMap sqlDB)
            throws DataException {

        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int risultato = 0;
        StringBuffer strQuery = new StringBuffer(CONTA_LISTA_PROCEDIMENTI);

        if (sqlDB != null) {
            for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                strQuery.append(" AND ").append(key.toString());
            }
        }
        int indiceQuery = 1;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery.toString());
            pstmt.setInt(indiceQuery++, utente.getAreaOrganizzativa().getId()
                    .intValue());
            ;
            if (sqlDB != null) {
                for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer) {
                        pstmt.setInt(indiceQuery++, ((Integer) value)
                                .intValue());
                    } else if (value instanceof String) {
                        if (key.toString().indexOf("LIKE") > 0) {
                            pstmt.setString(indiceQuery++, value.toString()
                                    + "%");
                        } else {
                            pstmt.setString(indiceQuery++, value.toString());
                        }
                    } else if (value instanceof java.util.Date) {
                        java.util.Date d = (java.util.Date) value;
                        pstmt.setDate(indiceQuery++, new java.sql.Date(d
                                .getTime()));
                    } else if (value instanceof Boolean) {
                        pstmt.setBoolean(indiceQuery++, ((Boolean) value)
                                .booleanValue());
                    }
                }
            }
            rs = pstmt.executeQuery();
            if (rs.next()) {

                risultato = rs.getInt(1);

            }

        } catch (Exception e) {
            logger.error("cerca", e);
            throw new DataException("errore nella ricerca");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return risultato;

    }

    public SortedMap cerca(Utente utente, HashMap sqlDB) throws DataException {
        SortedMap procedimenti = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return i2.intValue() - i1.intValue();
            }
        });
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strQuery = new StringBuffer(SELECT_LISTA_PROCEDIMENTI);

        if (sqlDB != null) {
            for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                strQuery.append(" AND ").append(key.toString());
            }
        }

        int indiceQuery = 1;

        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(strQuery.toString());
            pstmt.setInt(indiceQuery++, utente.getAreaOrganizzativa().getId()
                    .intValue());

            if (sqlDB != null) {
                for (Iterator it = sqlDB.entrySet().iterator(); it.hasNext();) {
                    Map.Entry entry = (Map.Entry) it.next();
                    Object key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Integer) {
                        pstmt.setInt(indiceQuery++, ((Integer) value)
                                .intValue());
                    } else if (value instanceof String) {
                        if (key.toString().indexOf("LIKE") > 0) {
                            pstmt.setString(indiceQuery++, value.toString()
                                    + "%");
                        } else {
                            pstmt.setString(indiceQuery++, value.toString());
                        }
                    } else if (value instanceof java.util.Date) {
                        java.util.Date d = (java.util.Date) value;
                        pstmt.setDate(indiceQuery++, new java.sql.Date(d
                                .getTime()));
                    } else if (value instanceof Boolean) {
                        pstmt.setBoolean(indiceQuery++, ((Boolean) value)
                                .booleanValue());
                    }
                    // indiceQuery++;
                }
            }

            rs = pstmt.executeQuery();
            ProcedimentoVO vo = null;
            while (rs.next()) {
                vo = new ProcedimentoVO();
                vo.setId(rs.getInt("procedimento_id"));
                // vo.setDataAvvio(rs.getDate("data_avvio"));
                vo.setDataAvvioPro(DateUtil.formattaData(rs.getDate(
                        "data_avvio").getTime()));
                vo.setUfficioId(rs.getInt("ufficio_id"));
                vo.setTitolarioId(rs.getInt("titolario_id"));
                vo.setStatoId(rs.getInt("stato_id"));
                vo.setTipoFinalitaId(rs.getInt("tipo_finalita_id"));
                vo.setOggetto(rs.getString("oggetto"));
                vo.setTipoProcedimentoId(rs.getInt("tipo_procedimento"));
                vo.setTipoProcedimentoDesc(rs.getString("tipo"));
                vo.setReferenteId(rs.getInt("referente_id"));
                vo.setPosizione(rs.getString("posizione_id"));
                vo.setDataEvidenza(rs.getDate("data_evidenza"));
                vo.setNote("note");
                vo.setProtocolloId(rs.getInt("protocollo_id"));
                vo.setNumeroProcedimento(rs.getString("numero_procedimento"));
                vo.setAnno(rs.getInt("anno"));
                vo.setNumero(rs.getInt("numero"));
                vo.setRowCreatedTime(rs.getDate("row_created_time"));
                vo.setRowCreatedUser(rs.getString("row_created_user"));
                vo.setRowUpdatedUser(rs.getString("row_updated_user"));
                vo.setRowUpdatedTime(rs.getDate("row_updated_time"));
                vo.setVersione(rs.getInt("versione"));
                vo.setResponsabile(rs.getString("responsabile"));
                procedimenti.put(vo.getId(), vo);
            }

        } catch (Exception e) {
            logger.error("cerca", e);
            throw new DataException("errore nella ricerca");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return procedimenti;

    }

    public void salvaTipoProcedimento(Connection connection,
            TipoProcedimentoVO tipoProcedimentoVO) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("salvaTipoProcedimento() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INSERT_TIPO_PROCEDIMENTO);
            pstmt.setInt(1, tipoProcedimentoVO.getIdTipo());
            pstmt.setInt(2, tipoProcedimentoVO.getIdUfficio());
            pstmt.setString(3, tipoProcedimentoVO.getDescrizione());
            pstmt.executeUpdate();
            logger.info("Inserito Tipo Procedimento: TipoProcedimentoId "
                    + tipoProcedimentoVO.getIdTipo() + ", UfficioId "
                    + tipoProcedimentoVO.getIdUfficio() + ".");

        } catch (Exception e) {
            logger.error("salvaTipoProcedimento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    private static final String INSERT_PROCEDIMENTO = "insert into procedimenti(procedimento_id ,data_avvio ,ufficio_id ,titolario_id ,stato_id ,tipo_finalita_id ,oggetto ,tipo_procedimento ,referente_id ,posizione_id ,data_evidenza ,note ,protocollo_id ,numero_procedimento ,anno ,numero ,row_created_time ,row_created_user ,row_updated_user ,row_updated_time ,versione ,responsabile, aoo_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

    private static final String INSERT_STORIA_PROCEDIMENTO = "insert into storia_procedimenti(procedimento_id ,data_avvio ,ufficio_id ,titolario_id ,stato_id ,tipo_finalita_id ,oggetto ,tipo_procedimento ,referente_id ,posizione_id ,data_evidenza ,note ,protocollo_id ,numero_procedimento ,anno ,numero ,row_created_time ,row_created_user ,row_updated_user ,row_updated_time ,versione ,responsabile, aoo_id) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";

    private static final String UPDATE_PROCEDIMENTO = "update procedimenti set data_avvio=? ,ufficio_id=? ,titolario_id=? ,stato_id=? ,tipo_finalita_id=? ,oggetto=? ,tipo_procedimento=? ,referente_id=? ,posizione_id=? ,data_evidenza=? ,note=? ,protocollo_id=? ,row_updated_user=? ,row_updated_time=? ,versione=? ,responsabile=?, aoo_id=? where procedimento_id=?";

    private static final String INSERT_PROCEDIMENTO_FALDONI = "insert into procedimenti_faldone (procedimento_id,faldone_id) values (?,?)";

    private static final String INSERT_TIPO_PROCEDIMENTO = "insert into tipi_procedimento (tipo_procedimenti_id,ufficio_id,tipo) values (?,?,?)";

    private static final String INSERT_PROCEDIMENTO_FASCICOLI = "insert into procedimenti_fascicolo (procedimento_id,fascicolo_id) values (?,?)";

    private static final String INSERT_PROCEDIMENTO_PROTOCOLLI = "insert into protocollo_procedimenti ("
            + "procedimento_id, protocollo_id, row_created_time, row_created_user, "
            + "row_updated_user, row_updated_time, versione) values (?,?,?,?,?,?,?)";

    private static final String DELETE_PROCEDIMENTO_FASCICOLI = "delete from procedimenti_fascicolo where procedimento_id=?";

    private static final String DELETE_PROCEDIMENTO_PROTOCOLLI = "delete from protocollo_procedimenti where procedimento_id=?";

    private static final String DELETE_PROCEDIMENTO_FALDONI = "delete from procedimenti_faldone where procedimento_id=?";

    private final static String SELECT_ULTIMO_PROCEDIMENTO = "SELECT MAX(numero) FROM procedimenti WHERE aoo_id = ? and anno=?";

    // private final static String SELECT_PROCEDIMENTO = "select * from
    // procedimenti where procedimento_id=?";

    private final static String SELECT_PROCEDIMENTO = " SELECT  distinct tipo_procedimenti_id, tipo, procedimenti.*"
            + "FROM  procedimenti  left outer join TIPI_PROCEDIMENTO on (procedimenti.tipo_procedimento=tipi_procedimento.tipo_procedimenti_id)"
            + " where procedimento_id=?";

    private final static String SELECT_PROCEDIMENTO_BY_ANNO_NUMERO = "select * from procedimenti where anno=? and numero=?";

    private final static String SELECT_PROCEDIMENTO_FALDONI = "select * from procedimenti_faldone where procedimento_id=?";

    private final static String SELECT_PROCEDIMENTO_FASCICOLI = "select * from procedimenti_fascicolo where procedimento_id=?";

    private final static String SELECT_PROCEDIMENTO_PROTOCOLLI = "select * from protocollo_procedimenti where procedimento_id=?";

    private final static String UPDATE_STATO_PROTOCOLLO = "update protocolli set stato_protocollo=? where protocollo_id=?";

    // private final static String SELECT_LISTA_PROCEDIMENTI = "SELECT * FROM
    // PROCEDIMENTI WHERE 1=1 ";

    // private final static String SELECT_LISTA_PROCEDIMENTI = " SELECT distinct
    // tipo_procedimenti_id, tipo, procedimenti.* "
    // + "FROM procedimenti left outer join TIPI_PROCEDIMENTO on
    // (procedimenti.tipo_procedimento=tipi_procedimento.tipo_procedimenti_id)";
    // private final static String SELECT_LISTA_PROCEDIMENTI = "SELECT * FROM
    // PROCEDIMENTI WHERE 1=1 ";

    private final static String CONTA_LISTA_PROCEDIMENTI = " SELECT  COUNT(*)"
            + "FROM  procedimenti  left outer join TIPI_PROCEDIMENTO on (procedimenti.tipo_procedimento=tipi_procedimento.tipo_procedimenti_id) WHERE aoo_id=? ";

    private final static String SELECT_LISTA_PROCEDIMENTI = " SELECT  distinct tipo_procedimenti_id, tipo, procedimenti.* "
            + "FROM  procedimenti  left outer join TIPI_PROCEDIMENTO on (procedimenti.tipo_procedimento=tipi_procedimento.tipo_procedimenti_id) WHERE aoo_id=? ";

    public final static String SELECT_PROCEDIMENTO_BY_ID_VERSIONE = "SELECT  distinct tipo_procedimenti_id, tipo, storia_procedimenti.* "
            + " FROM  storia_procedimenti   left outer join TIPI_PROCEDIMENTO on (storia_procedimenti.tipo_procedimento=tipi_procedimento.tipo_procedimenti_id) "
            + " WHERE procedimento_id = ? and versione = ? ";

}