package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.EmailConstants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.mvc.integration.EmailDAO;
import it.finsiel.siged.mvc.presentation.helper.EmailView;
import it.finsiel.siged.mvc.vo.log.EventoVO;
import it.finsiel.siged.mvc.vo.posta.CodaInvioVO;
import it.finsiel.siged.mvc.vo.posta.EmailVO;
import it.finsiel.siged.mvc.vo.posta.PecDestVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class EmailDAOjdbc implements EmailDAO {

    static Logger logger = Logger.getLogger(EmailDAOjdbc.class.getName());

    public static String INSERT_ALLEGATO = "insert into email_ingresso_allegati ("
            + "id, email_id, descrizione, filename, content_type, dimensione, impronta, data, row_created_time, row_created_user ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static String INSERT_EMAIL_CODA_INVIO = "INSERT INTO EMAIL_CODA_INVIO ( ID, AOO_ID, PROTOCOLLO_ID, DATA_CREAZIONE ) VALUES ( ?, ?, ?, ? )";

    public static String INSERT_DESTINATARI_EMAIL_CODA_INVIO = "INSERT INTO EMAIL_CODA_INVIO_DESTINATARI ( MSG_ID, EMAIL, NOMINATIVO, TIPO ) VALUES ( ?, ?, ?, ? )";

    public static String UPDATE_EMAIL_CODA_INVIO = "UPDATE EMAIL_CODA_INVIO SET STATO=? WHERE ID=?";

    public static String INSERT_EMAIL = "insert into email_ingresso ( email_id , descrizione, filename, content_type, dimensione, impronta, testo_messaggio, row_created_time, row_created_user, email_mittente, nome_mittente, email_oggetto, data_spedizione, data_ricezione, segnatura, aoo_id) values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";

    public static String INSERT_EMAIL_LOG = "INSERT INTO EMAIL_LOGS ( EMAIL_ID, OGGETTO, TIPO, ERRORE, DESTINATARI, TIPO_LOG, AOO_ID ) VALUES ( ?, ?, ?, ?, ? , ? , ? )";

    public static String SELECT_EMAIL_CODA_INVIO = "SELECT ID FROM EMAIL_CODA_INVIO WHERE AOO_ID=? AND STATO="
            + EmailConstants.MESSAGGIO_NON_INVIATO;

    public static String SELECT_EMAIL_LOG = "SELECT * FROM EMAIL_LOGS WHERE AOO_ID=? AND TIPO_LOG=?";

    public static String SELECT_MESSAGGIO_USCITA = "SELECT EM.*, P.NUME_PROTOCOLLO FROM EMAIL_CODA_INVIO EM, PROTOCOLLI P WHERE ID=? AND P.PROTOCOLLO_ID=EM.PROTOCOLLO_ID";

    public static String SELECT_DESTINATARI_MESSAGGIO = "SELECT * FROM EMAIL_CODA_INVIO_DESTINATARI WHERE MSG_ID=?";

    public static String SELECT_MESSAGGIO_ENTRATA = "SELECT * FROM EMAIL_INGRESSO WHERE EMAIL_ID = ?";

    public static String SELECT_MESSAGGI_ENTRATA_NON_PROTOCOLLATI = "SELECT e.email_id,e.email_oggetto, e.data_spedizione,e.email_mittente,"
            + "e.nome_mittente, (select count(*) from email_ingresso_allegati al where al.email_id=e.email_id) as allegati  FROM EMAIL_INGRESSO e "
            + "where e.aoo_id = ? and e.flag_stato="
            + EmailConstants.MESSAGGIO_INGRESSO_NON_PROTOCOLLATO
            + " order by e.data_spedizione desc";

    public static String SELECT_ALLEGATI_MESSAGGIO_ENTRATA = "SELECT * FROM EMAIL_INGRESSO_ALLEGATI WHERE EMAIL_ID = ?";

    public static String SELECT_ALLEGATO_MESSAGGIO_ENTRATA = "SELECT * FROM EMAIL_INGRESSO_ALLEGATI WHERE ID = ?";

    public static String UPDATE_STATO_MESSAGGIO_ENTRATA = "UPDATE EMAIL_INGRESSO SET FLAG_STATO=? WHERE EMAIL_ID=?";

    private JDBCManager jdbcMan = new JDBCManager();

    public void salvaMessaggioPerInvio(Connection connection, int id,
            int aooId, int protocolloId, Collection destinatari)
            throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non e' valida.");
            }

            pstmt = connection.prepareStatement(INSERT_EMAIL_CODA_INVIO);
            pstmt.setInt(1, id);
            pstmt.setInt(2, aooId);
            pstmt.setInt(3, protocolloId);
            pstmt.setDate(4, new Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            jdbcMan.close(pstmt);

            Iterator it = destinatari.iterator();
            while (it.hasNext()) {
                DestinatarioVO d = (DestinatarioVO) it.next();
                pstmt = connection
                        .prepareStatement(INSERT_DESTINATARI_EMAIL_CODA_INVIO);
                pstmt.setInt(1, id);
                pstmt.setString(2, d.getEmail());
                pstmt.setString(3, d.getDestinatario());
                pstmt.setString(4, d.getFlagTipoDestinatario());
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }
            logger.debug("Email Accodata per invio, protocollo Id:\n"
                    + protocolloId);

        } catch (Exception e) {
            logger.error("salvaMessaggioPerInvio", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public Collection getListaMessaggiUscita(int aooId) throws DataException {
        ArrayList lista = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_EMAIL_CODA_INVIO);
            pstmt.setInt(1, aooId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                lista.add(new Integer(rs.getInt("id")));
            }
        } catch (Exception e) {
            logger.error("getListaMessaggiUscita", e);
            throw new DataException("Cannot load getListaMessaggiUscita");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return lista;
    }

    public Collection getListaLog(int aooId, int tipoLog) throws DataException {
        ArrayList lista = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_EMAIL_LOG);
            pstmt.setInt(1, aooId);
            pstmt.setInt(2, tipoLog);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                EventoVO ev = new EventoVO();
                ev.setData(rs.getDate("data_log"));
                ev.setDestinatari(rs.getString("destinatari"));
                ev.setErrore(rs.getString("errore"));
                ev.setEventoId(rs.getInt("email_id"));
                ev.setOggetto(rs.getString("oggetto"));
                ev.setTipo(rs.getString("tipo"));
                ev.setTipoLog(rs.getInt("tipo_log"));
                lista.add(ev);
            }
        } catch (Exception e) {
            logger.error("getListaLog", e);
            throw new DataException("Cannot load getListaLog");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return lista;
    }

    public Collection getDestinatariMessaggioUscita(int msgId)
            throws DataException {
        ArrayList lista = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_DESTINATARI_MESSAGGIO);
            pstmt.setInt(1, msgId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                PecDestVO d = new PecDestVO();
                d.setTipo(rs.getString("tipo"));
                d.setEmail(rs.getString("email"));
                d.setNominativo(rs.getString("nominativo"));
                lista.add(d);
            }
        } catch (Exception e) {
            logger.error("getDestinatariMessaggioUscita", e);
            throw new DataException("Cannot load getDestinatariMessaggioUscita");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return lista;
    }

    public void segnaMessaggioComeInviato(int msgId) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(UPDATE_EMAIL_CODA_INVIO);

            pstmt.setInt(1, EmailConstants.MESSAGGIO_INVIATO);
            pstmt.setInt(2, msgId);

            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("segnaMessaggioComeInviato", e);
            throw new DataException(
                    "Impossibile aggiornare lo stato del messaggio sulla base dati.");
        } finally {
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
    }

    public CodaInvioVO getMessaggioDaInviare(int id) throws DataException {
        CodaInvioVO vo = new CodaInvioVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_MESSAGGIO_USCITA);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                vo.setId(id);
                vo.setAooId(rs.getInt("aoo_id"));
                vo.setDataCreazione(rs.getDate("data_creazione"));
                vo.setDataInvio(rs.getDate("data_invio"));
                vo.setStato(rs.getInt("stato"));
                vo.setProtocolloId(rs.getInt("protocollo_id"));
                vo.setNumeroProtocollo(rs.getInt("NUME_PROTOCOLLO"));
                vo.setReturnValue(ReturnValues.FOUND);
            } else {
                vo.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return vo;
    }

    public void salvaAllegato(Connection connection, DocumentoVO documento,
            int email_id) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non e' valida.");
            }

            File in = new File(documento.getPath());
            FileInputStream fis = new FileInputStream(in);

            pstmt = connection.prepareStatement(INSERT_ALLEGATO);
            pstmt.setInt(1, documento.getId().intValue());
            pstmt.setInt(2, email_id);
            pstmt.setString(3, documento.getDescrizione());
            pstmt.setString(4, documento.getFileName());
            pstmt.setString(5, documento.getContentType());
            pstmt.setInt(6, documento.getSize());
            pstmt.setString(7, documento.getImpronta());
            pstmt.setBinaryStream(8, fis, (int) in.length());
            pstmt.setDate(9, new Date(System.currentTimeMillis()));
            pstmt.setString(10, documento.getRowCreatedUser());
            pstmt.executeUpdate();
            fis.close();
            logger.debug("Allegato Email inserito - id="
                    + documento.getId().intValue());

        } catch (Exception e) {
            logger.error("Salva Allegato", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaEmail(EmailVO email, Connection connection)
            throws DataException {
        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            InputStream isSegnatura = null;
            int segnaturaSize = 0;
            if (email.getSegnatura() != null) {
                isSegnatura = new ByteArrayInputStream(email.getSegnatura()
                        .getBytes());
                segnaturaSize = email.getSegnatura().length();
            } else {
                isSegnatura = new ByteArrayInputStream("".getBytes());
            }

            InputStream isTesto = null;
            int testoSize = 0;
            if (email.getTestoMessaggio() != null) {
                isTesto = new ByteArrayInputStream(email.getTestoMessaggio()
                        .getBytes());
                testoSize = email.getTestoMessaggio().length();
            } else {
                isTesto = new ByteArrayInputStream("".getBytes());
            }

            pstmt = connection.prepareStatement(INSERT_EMAIL);

            pstmt.setInt(1, email.getId().intValue());
            pstmt.setString(2, email.getOggetto());
            pstmt.setString(3, "  ");
            pstmt.setString(4, email.getContentType());
            pstmt.setInt(5, testoSize);
            pstmt.setString(6, "  ");
            pstmt.setBinaryStream(7, isTesto, testoSize);
            pstmt.setDate(8, new Date(System.currentTimeMillis()));
            pstmt.setString(9, "auto");
            pstmt.setString(10, email.getEmailMittente());
            pstmt.setString(11, email.getNomeMittente());
            pstmt.setString(12, email.getOggetto());
            if (email.getDataSpedizione() == null)
                pstmt.setNull(13, Types.DATE);
            else
                pstmt
                        .setDate(13, new Date(email.getDataSpedizione()
                                .getTime()));

            if (email.getDataRicezione() == null)
                pstmt.setNull(14, Types.DATE);
            else
                pstmt.setDate(14, new Date(email.getDataRicezione().getTime()));

            pstmt.setBinaryStream(15, isSegnatura, segnaturaSize);
            pstmt.setInt(16, email.getAooId());

            pstmt.executeUpdate();
            isTesto.close();
            isSegnatura.close();

        } catch (Exception e) {
            logger.error("Salva Email", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaEmailLog(MessaggioEmailEntrata messaggio,
            Connection connection) throws DataException {
        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }

            pstmt = connection.prepareStatement(INSERT_EMAIL_LOG);

            pstmt.setInt(1, messaggio.getEmail().getId().intValue());
            pstmt.setString(2, messaggio.getEmail().getOggetto());
            pstmt.setString(3, messaggio.getTipoEmail());
            pstmt.setString(4, messaggio.getMessaggioErrore());
            if (messaggio.getDaticertXML().isValid())
                pstmt.setString(5, messaggio.getDaticertXML()
                        .getDestinatariAsString());
            else
                pstmt.setString(5, "  ");
            pstmt.setInt(6, EmailConstants.LOG_EMAIL_INGRESSO);
            pstmt.setInt(7, messaggio.getEmail().getAooId());
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Salva Email Log", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaEmailLog(int id, String oggetto, String tipo,
            String errore, Connection connection, int tipoLog, int aooId)
            throws DataException {
        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }

            pstmt = connection.prepareStatement(INSERT_EMAIL_LOG);

            pstmt.setInt(1, id);
            pstmt.setString(2, oggetto);
            pstmt.setString(3, tipo);
            pstmt.setString(4, errore);
            pstmt.setString(5, "  ");
            pstmt.setInt(6, tipoLog);
            pstmt.setInt(7, aooId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Salva Email Log", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void aggiornaStatoEmailIngresso(Connection connection,
            int messaggioId, int stato) throws DataException {

        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            pstmt = connection.prepareStatement(UPDATE_STATO_MESSAGGIO_ENTRATA);
            pstmt.setInt(1, stato);
            pstmt.setInt(2, messaggioId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.error("aggiornaStatoEmailIngresso", e);
            throw new DataException(
                    "Impossibile aggiornare lo stato del messaggio sulla base dati.");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public EmailVO getEmailEntrata(int emailId) throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            return getEmailEntrata(connection, emailId);
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
    }

    public EmailVO getEmailEntrata(Connection connection, int emailId)
            throws DataException {
        EmailVO vo = new EmailVO();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_MESSAGGIO_ENTRATA);
            pstmt.setInt(1, emailId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                vo.setId(emailId);
                vo.setOggetto(rs.getString("email_oggetto"));
                vo.setContentType(rs.getString("content_type"));
                vo.setDataRicezione(rs.getDate("data_ricezione"));
                vo.setDataSpedizione(rs.getDate("data_spedizione"));
                vo.setEmailMittente(rs.getString("email_mittente"));
                vo.setNomeMittente(rs.getString("nome_mittente"));
                vo.setFlagEliminato(rs.getInt("flag_stato"));
                vo.setRowCreatedTime(rs.getDate("row_created_time"));
                vo.setRowCreatedUser(rs.getString("row_created_user"));
                InputStream isTesto = rs.getBinaryStream("testo_messaggio");
                ByteArrayOutputStream bosTesto = new ByteArrayOutputStream();
                FileUtil.writeFile(isTesto, bosTesto);
                isTesto.close();
                vo.setTestoMessaggio(new String(bosTesto.toByteArray()));
                bosTesto.close();
                
                
                // Dobbiamo capire a cosa serve ???????
                //InputStream isSegnatura = rs.getBinaryStream("segnatura");
                //ByteArrayOutputStream bosSegnatura = new ByteArrayOutputStream();
                //FileUtil.writeFile(isTesto, bosSegnatura);
                //isSegnatura.close();
                //vo.setSegnatura(new String(bosSegnatura.toByteArray()));
                //bosSegnatura.close();
                //------------------------------------------------------------
                
                vo.setReturnValue(ReturnValues.FOUND);
            } else {
                vo.setReturnValue(ReturnValues.NOT_FOUND);
            }
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return vo;
    }

    public ArrayList getAllegatiEmailEntrata(Connection connection, int emailId)
            throws DataException {
        ArrayList allegati = new ArrayList(1);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            pstmt = connection
                    .prepareStatement(SELECT_ALLEGATI_MESSAGGIO_ENTRATA);
            pstmt.setInt(1, emailId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                DocumentoVO doc = new DocumentoVO();
                doc.setReturnValue(ReturnValues.FOUND);
                doc.setId(rs.getInt("id"));
                doc.setDescrizione(rs.getString("descrizione"));
                doc.setFileName(rs.getString("filename"));
                doc.setContentType(rs.getString("content_type"));
                doc.setSize(rs.getInt("dimensione"));
                doc.setImpronta(rs.getString("impronta"));
                doc.setRowCreatedTime(rs.getDate("row_created_time"));
                doc.setRowCreatedUser("row_created_user");
                allegati.add(doc);
            }
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return allegati;
    }

    public void writeDocumentoToStream(Connection connection, int docId,
            OutputStream os) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            pstmt = connection
                    .prepareStatement(SELECT_ALLEGATO_MESSAGGIO_ENTRATA);
            pstmt.setInt(1, docId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                InputStream in = rs.getBinaryStream("data");
                byte[] buffer = new byte[32768];
                int n = 0;
                while ((n = in.read(buffer)) != -1)
                    os.write(buffer, 0, n);
                in.close();
            } else {
                throw new DataException("Documento non trovato.");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
    }

    public void writeDocumentoToStream(int docId, OutputStream os)
            throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            writeDocumentoToStream(connection, docId, os);
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
    }

    public Collection getMessaggiDaProtocollare(int aooId) throws DataException {

        Collection email = new ArrayList();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            email = getMessaggiDaProtocollare(connection, aooId);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " getEmail");
        } finally {
            jdbcMan.close(connection);
        }
        return email;
    }

    public Collection getMessaggiDaProtocollare(Connection connection, int aooId)
            throws DataException {
        ArrayList listaEmail = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            pstmt = connection
                    .prepareStatement(SELECT_MESSAGGI_ENTRATA_NON_PROTOCOLLATI);
            pstmt.setInt(1, aooId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                EmailView email = new EmailView();
                email.setId(rs.getInt("email_id"));
                email.setTestoMessaggio(rs.getString("email_oggetto"));
                email.setDataSpedizione(DateUtil.formattaData(rs.getDate(
                        "data_spedizione").getTime()));
                email.setEmailMittente(rs.getString("email_mittente"));
                email.setNomeMittente(rs.getString("nome_mittente"));
                email.setNumeroAllegati(rs.getInt("allegati"));
                listaEmail.add(email);
            }
        } catch (Exception e) {
            logger.error("getListaEmail", e);
            throw new DataException("Cannot load getListaEmail");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return listaEmail;
    }

    public void eliminaEmail(Connection connection, int emailId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            if (emailId > 0) {
                pstmt = connection
                        .prepareStatement("DELETE FROM email_ingresso_allegati WHERE email_id=?");

                pstmt.setInt(1, emailId);
                pstmt.executeUpdate();

                pstmt = connection
                        .prepareStatement("DELETE FROM email_ingresso WHERE email_id=?");

                pstmt.setInt(1, emailId);
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("elimina Email", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void eliminaEmailLog(Connection connection, String[] ids)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("Connection is " + connection);
                throw new DataException("La connessione fornita non valida.");
            }
            if (ids != null) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < ids.length; i++) {
                    sb.append(ids[i]);
                    if (i < ids.length - 1)
                        sb.append(",");
                }
                pstmt = connection
                        .prepareStatement("DELETE FROM email_logs WHERE email_id in("
                                + sb.toString() + ")");
                pstmt.executeUpdate();
            }

        } catch (Exception e) {
            logger.error("elimina email log ", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

};