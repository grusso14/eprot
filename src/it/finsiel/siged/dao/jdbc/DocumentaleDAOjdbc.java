package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.CannotDeleteException;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.DocumentaleDAO;
import it.finsiel.siged.mvc.presentation.helper.DocumentoView;
import it.finsiel.siged.mvc.vo.documentale.CartellaVO;
import it.finsiel.siged.mvc.vo.documentale.FileVO;
import it.finsiel.siged.mvc.vo.documentale.PermessoFileVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioClassificatiDestinatariVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioClassificatiVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.DateUtil;
import it.finsiel.siged.util.FileUtil;
import it.flosslab.dao.indexer.IndexerDAO;
import it.flosslab.dao.search.SearcherDocumentaleDao;
import it.flosslab.dao.search.SearcherProtocolloDao;
import it.flosslab.parser.TikaDocumentParser;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class DocumentaleDAOjdbc implements DocumentaleDAO, DocDaoInterface {

    static Logger logger = Logger.getLogger(DocumentaleDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();
    
    private static ResourceBundle bundle;
    
    private static String SYSTEM_PARAMS = "systemParams";

    /*
     * Salva l'oggetto cartella nella base dati. Genera un'eccezione se
     * l'operazione fallisce, in tal caso non viene scritto niente nella base
     * dati.
     */
    public CartellaVO newCartellaVO(CartellaVO cartella) throws DataException {
        CartellaVO c;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            c = newCartellaVO(connection, cartella);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return c;
    }

    /*
     * Salva l'oggetto cartella sulla base dati e rende tale operazione parte
     * della transazione associata alla connessione connection. Genera
     * un'eccezione se l'operazone non � riuscita: non effettua alcun rollback.
     * Si assume che ogni variabile di tipo int nell'oggetto che sia uguale a 0
     * viene salvato nella base dati come "null".
     */
    public CartellaVO newCartellaVO(Connection connection, CartellaVO cartella)
            throws DataException {

        CartellaVO newCartella = new CartellaVO();
        newCartella.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("Connessione non valida.");
            }

            pstmt = connection.prepareStatement(INSERT_CARTELLA,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pstmt.setInt(1, cartella.getId().intValue());
            if (cartella.getUfficioId() == 0) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, cartella.getUfficioId());
            }
            if (cartella.getUtenteId() == 0) {
                pstmt.setNull(3, Types.INTEGER);
            } else {
                pstmt.setInt(3, cartella.getUtenteId());
            }
            if (cartella.getParentId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, cartella.getParentId());
            }
            pstmt.setString(5, cartella.getNome());
            pstmt.setInt(6, cartella.isRoot() ? 1 : 0);
            pstmt.setInt(7, cartella.getAooId());
            pstmt.executeUpdate();

            logger.debug("CartellaVO inserita - id= "
                    + cartella.getId().intValue());

        } catch (Exception e) {
            logger.error("Salvataggio della nuova carella non riuscito", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }

        newCartella = getCartellaVO(connection, cartella.getId().intValue());
        newCartella.setReturnValue(ReturnValues.SAVED);
        return newCartella;
    }

    public CartellaVO updateNomeCartellaVO(CartellaVO cartella)
            throws DataException {
        CartellaVO c;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            c = updateNomeCartellaVO(connection, cartella);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return c;
    }

    /*
     * Aggiorna il nome della cartella.
     */
    public CartellaVO updateNomeCartellaVO(Connection connection,
            CartellaVO cartella) throws DataException {

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("Connessione non valida.");
            }

            pstmt = connection.prepareStatement(UPDATE_NOME_CARTELLA,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pstmt.setString(1, cartella.getNome());
            pstmt.setInt(2, cartella.getId().intValue());

            pstmt.executeUpdate();
            cartella = getCartellaVO(connection, cartella.getId().intValue());
            cartella.setReturnValue(ReturnValues.SAVED);
            logger.debug("CartellaVO nome aggiornato - id= "
                    + cartella.getId().intValue());

        } catch (Exception e) {
            logger.error("Aggiornamento della carella non riuscito", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }

        return cartella;
    }

    /*
     * Ritorna un oggetto di tipo CartellaVO letto dalla base dati cha ha come
     * id univoco l'id passato per argomento. L'oggetto restituito non � mai
     * nullo per convenzione, consultare il valore del metodo getReturnValue()
     * per capire se � stato trovato o meno.
     * 
     * @see it.finsiel.siged.constant.ReturnValues
     * 
     * Genera un'eccezione se l'operazione di lettura non viene effettuata.
     */
    public CartellaVO getCartellaVO(int id) throws DataException {
        CartellaVO cartella;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            cartella = getCartellaVO(connection, id);
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return cartella;
    }

    /*
     * Ritorna un'oggetto di tipo CartellaVO letto dalla base dati con l'id
     * passato per argomento utilizzando la connessione 'connection'. L'oggetto
     * restituito non � mai nullo per convenzione, consultare il valore del
     * metodo getReturnValue() per capire se � stato trovato o meno.
     * 
     * @see it.finsiel.siged.constant.ReturnValues
     * 
     * Genera un'eccezione se l'operazione di lettura non viene effettuata.
     */
    public CartellaVO getCartellaVO(Connection connection, int id)
            throws DataException {

        CartellaVO cartella = new CartellaVO();
        cartella.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("Connessione non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_CARTELLA_BY_ID);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Imposta Campi Oggetto

                caricaDatiCartellaVO(rs, cartella);

                logger.debug("Cartella: " + cartella);
            } else {
                cartella.setReturnValue(ReturnValues.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.error("Load Cartella", e);
            throw new DataException(
                    "Impossibile caricare i deti della Cartella con id:" + id);
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return cartella;

    }

    /*
     * Ritorna un'oggetto di tipo CartellaVO letto dalla base dati, la cartella
     * rappresenta l'equivalente nel documentale della cartella utente (con
     * id=utenteId) dell'ufficio (con id=ufficioId) presente nella struttura
     * dell'AOO.
     * 
     * Gli utenti che hanno accesso ad un ufficio avranno a disposizione una
     * cartella con il loro nome utente (@see
     * it.finsiel.siged.mvc.vo.organizzazione.UtenteVO#getUsername() )
     * 
     * L'oggetto restituito non � mai nullo per convenzione, consultare il
     * valore del metodo getReturnValue() per capire se � stato trovato o meno.
     * 
     * @see it.finsiel.siged.constant.ReturnValues
     * 
     * Genera un'eccezione se l'operazione di lettura non viene effettuata.
     */

    public CartellaVO getCartellaVOByUfficioUtenteNome(Connection connection,
            int ufficioId, int utenteId, String nome, Integer id)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CartellaVO cartella = new CartellaVO();
        cartella.setReturnValue(ReturnValues.NOT_FOUND);

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("Connessione non valida.");
            }
            pstmt = connection
                    .prepareStatement(SELECT_CARTELLA_BY_UFFICIO_ID_UTENTE_ID_NAME);
            pstmt.setInt(1, ufficioId);
            pstmt.setInt(2, utenteId);
            pstmt.setString(3, nome);
            if (id == null)
                pstmt.setInt(4, 0);
            else
                pstmt.setInt(4, id.intValue());

            rs = pstmt.executeQuery();

            if (rs.next()) {
                caricaDatiCartellaVO(rs, cartella);
            }
        } catch (Exception e) {
            logger.error(
                    "Impossibile caricare i dati della Cartella per ufficio id:"
                            + ufficioId + " ed utente id:" + utenteId
                            + " e nome :" + nome, e);
            throw new DataException(
                    "Impossibile caricare i dati della Cartella per ufficio id:"
                            + ufficioId + " ed utente id:" + utenteId
                            + " e nome :" + nome);
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return cartella;

    }

    public CartellaVO getCartellaVOByUfficioUtenteId(Connection connection,
            int ufficioId, int utenteId) throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        CartellaVO cartella = new CartellaVO();
        cartella.setReturnValue(ReturnValues.NOT_FOUND);

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("Connessione non valida.");
            }
            pstmt = connection
                    .prepareStatement(SELECT_CARTELLA_BY_UFFICIO_ID_UTENTE_ID);
            pstmt.setInt(1, ufficioId);
            pstmt.setInt(2, utenteId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                caricaDatiCartellaVO(rs, cartella);
                // } else {
                // throw new DataException("Cartella non trovata per ufficio
                // id:"
                // + ufficioId + " ed utente id:" + utenteId);
            }
        } catch (Exception e) {
            logger.error(
                    "Impossibile caricare i dati della Cartella per ufficio id:"
                            + ufficioId + " ed utente id:" + utenteId, e);
            throw new DataException(
                    "Impossibile caricare i dati della Cartella per ufficio id:"
                            + ufficioId + " ed utente id:" + utenteId);
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return cartella;

    }

    /*
     * metodo interno usato per popalare l'oggetto CartellaVO con i valori
     * presenti nel record corrente del ResultSet passato.
     * 
     * Genera un'eccezione se vi sono errori nella lettura dei vari campi dal
     * ResultSet.
     */
    private void caricaDatiCartellaVO(ResultSet rs, CartellaVO cartella)
            throws SQLException {
        cartella.setId(rs.getInt("DC_ID"));
        cartella.setParentId(rs.getInt("PARENT_ID"));
        cartella.setUfficioId(rs.getInt("UFFICIO_ID"));
        cartella.setUtenteId(rs.getInt("UTENTE_ID"));
        cartella.setNome(rs.getString("NOME"));
        cartella.setRoot(rs.getBoolean("IS_ROOT"));
        cartella.setAooId(rs.getInt("AOO_ID"));
        cartella.setReturnValue(ReturnValues.FOUND);
    }

    // ========== FILE ============== //
    // ====================================== //

    private void caricaDatiFile(ResultSet rs, FileVO file) throws SQLException {
        file.setId(rs.getInt("DFA_ID"));
        file.setOwnerId(rs.getInt("owner_id"));
        file.setCartellaId(rs.getInt("DC_ID"));
        file.setRepositoryFileId(rs.getInt("DFR_ID"));
        file.setNomeFile(rs.getString("NOME"));
        file.setDataDocumento(new java.util.Date((rs
                .getTimestamp("DATA_DOCUMENTO")).getTime()));
        file.setOggetto(rs.getString("OGGETTO"));
        file.setNote(rs.getString("NOTE"));
        file.setDescrizione(rs.getString("DESCRIZIONE"));
        file.setDescrizioneArgomento(rs.getString("DESCRIZIONE_ARGOMENTO"));
        file.setTipoDocumentoId(rs.getInt("TIPO_DOCUMENTO_ID"));
        file.setTitolarioId(rs.getInt("TITOLARIO_ID"));
        file.setStatoDocumento(rs.getString("STATO_LAV"));
        file.setStatoArchivio(rs.getString("STATO_ARC"));
        file.setUserLavId(rs.getInt("USER_LAV_ID"));
        file.setUsernameLav(rs.getString("FULL_NAME"));
        file.setRowCreatedUser(rs.getString("ROW_CREATED_USER"));
        file.setRowCreatedTime(new java.util.Date((rs
                .getTimestamp("ROW_CREATED_TIME")).getTime()));
        file.setVersione(rs.getInt("VERSIONE"));
        file.setReturnValue(ReturnValues.FOUND);
    }

    private void caricaDatiFileC(ResultSet rs, FileVO file) throws SQLException {
    	caricaDatiFile(rs, file);
    	file.setAssegnatoDa(rs.getString("full_name"));
        file.setMessaggio(rs.getString("msg"));
        
    }

    private void caricaDatiFileLista(ResultSet rs, FileVO file)
            throws SQLException {
    	caricaDatiFile(rs, file);
    	file.setAssegnatoDa(rs.getString("full_name"));
        file.setMessaggio(rs.getString("messaggio"));
    }

    /*
     * Crea un file con relativi attributi nella base dati
     */

    public FileVO newFileVO(FileVO file) throws DataException {
        FileVO f;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            f = newFileVO(connection, file);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return f;
    }

    /*
     * Crea un file nella base dati,
     */
    public FileVO newFileVO(Connection connection, FileVO file)
            throws DataException {

        FileVO newFile = new FileVO();
        newFile.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            int n = 0;

            pstmt = connection.prepareStatement(INSERT_FILE,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pstmt.setInt(++n, file.getId().intValue());
            pstmt.setInt(++n, file.getCartellaId());
            pstmt.setInt(++n, file.getRepositoryFileId());
            pstmt.setString(++n, file.getNomeFile());
            if (file.getDataDocumento() == null) {
                pstmt.setNull(++n, Types.DATE); // DATA_DOCUMENTO
            } else {
                pstmt.setDate(++n, new Date(file.getDataDocumento().getTime())); // DATA_DOCUMENTO
            }
            pstmt.setString(++n, file.getOggetto());
            pstmt.setString(++n, file.getNote());
            pstmt.setString(++n, file.getDescrizione());
            pstmt.setString(++n, file.getDescrizioneArgomento());
            pstmt.setInt(++n, file.getTipoDocumentoId());
            pstmt.setInt(++n, file.getTitolarioId());
            pstmt.setString(++n, file.getStatoDocumento());
            pstmt.setString(++n, file.getStatoArchivio());
            if (file.getUserLavId() == 0) {
                pstmt.setNull(++n, Types.INTEGER);
            } else {
                pstmt.setInt(++n, file.getUserLavId());
            }
            pstmt.setInt(++n, file.getOwnerId());
            if (file.getRowCreatedTime() == null) {
                pstmt.setNull(++n, Types.DATE); // DATA_DOCUMENTO
            } else {
                pstmt
                        .setDate(++n, new Date(file.getRowCreatedTime()
                                .getTime())); // DATA_DOCUMENTO
            }
            pstmt.setString(++n, file.getRowCreatedUser());
            pstmt.setInt(++n, file.getVersione());
            pstmt.executeUpdate();
            logger.debug("FileVO inserito - id= " + file.getId().intValue());

        } catch (Exception e) {
            logger.error("New FileVO ", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }

        // aggiornaIndice(connection);
        newFile = getFileVO(connection, file.getId().intValue());
        newFile.setReturnValue(ReturnValues.SAVED);
        return newFile;
    }

    public FileVO updateFileVO(Connection connection, FileVO file,
            boolean ripristina) throws DataException {

        FileVO newFile = new FileVO();
        newFile.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            int fileId = file.getId().intValue();
            int versioneDb = getVersioneCorrente(connection, fileId);
            if (versioneDb != file.getVersione() && !ripristina) {
                // vecchia
                newFile.setReturnValue(ReturnValues.OLD_VERSION);
            } else {
                archiviaVersione(connection, fileId, ripristina ? versioneDb
                        : file.getVersione());
                pstmt = connection.prepareStatement(UPDATE_FILE,
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                pstmt.setInt(1, file.getRepositoryFileId());
                pstmt.setString(2, file.getNomeFile());
                if (file.getDataDocumento() == null) {
                    pstmt.setNull(3, Types.DATE); // DATA_DOCUMENTO
                } else {
                    pstmt.setDate(3,
                            new Date(file.getDataDocumento().getTime())); // DATA_DOCUMENTO
                }
                pstmt.setString(4, file.getOggetto());
                pstmt.setString(5, file.getNote());
                pstmt.setString(6, file.getDescrizione());
                pstmt.setString(7, file.getDescrizioneArgomento());
                pstmt.setInt(8, file.getTipoDocumentoId());
                pstmt.setInt(9, file.getTitolarioId());
                pstmt.setString(10, ripristina ? Parametri.CHECKED_IN : file
                        .getStatoDocumento());
                pstmt.setString(11, file.getStatoArchivio());
                pstmt.setInt(12,
                        (ripristina ? versioneDb : file.getVersione()) + 1);
                if (ripristina || file.getUserLavId() == 0)
                    pstmt.setNull(13, Types.INTEGER);
                else
                    pstmt.setInt(13, file.getUserLavId());
                pstmt.setTimestamp(14,
                        new Timestamp(System.currentTimeMillis()));
                // pstmt.setTimestamp(14,
                // new Timestamp(System.currentTimeMillis()), Calendar
                // .getInstance());
                pstmt.setInt(15, file.getId().intValue());
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            logger.error("New FileVO ", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        newFile = getFileVO(connection, file.getId().intValue());
        newFile.setReturnValue(ReturnValues.SAVED);
        return newFile;
    }

    public int getVersioneCorrente(Connection connection, int dfaId)
            throws DataException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            pstmt = connection.prepareStatement(SELECT_VERSIONE_FILE);
            pstmt.setInt(1, dfaId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("versione");
            } else {
                return -1;
            }

        } catch (Exception e) {
            logger.error("versione", e);
            throw new DataException("impossibile leggere la versione");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
    }

    // private void aggiornaIndice(Connection connection) throws DataException {
    // PreparedStatement pstmt = null;
    //
    // try {
    // if (connection == null) {
    // logger.warn("Connection is:" + connection);
    // throw new DataException("La connessione fornita ? invalida.");
    // }
    // pstmt = connection.prepareStatement("ALTER INDEX DOC_FILE_REP_IDX "
    // + "REBUILD ONLINE PARAMETERS ('SYNC')");
    // pstmt.executeUpdate();
    // } catch (Exception e) {
    // logger.error("aggiornaIndice ", e);
    // throw new DataException("error.database.cannotsave");
    // } finally {
    // jdbcMan.close(pstmt);
    // }
    //
    // }

    public DocumentoVO getDocumento(int id) throws DataException {
        DocumentoVO docOut;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            docOut = getDocumento(connection, id);
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return docOut;
    }

    public DocumentoVO getDocumento(Connection connection, int id)
            throws DataException {

        DocumentoVO doc = new DocumentoVO();
        doc.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            pstmt = connection.prepareStatement(SELECT_DOCUMENTO_BY_ID);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Imposta Campi Oggetto

                doc.setId(id);
                doc.setRowCreatedUser(rs.getString("row_created_user"));
                doc.setRowCreatedTime(rs.getDate("row_created_time"));
                doc.setContentType(rs.getString("content_type"));
                doc.setImpronta(rs.getString("impronta"));
                doc.setFileName(rs.getString("filename"));
                doc.setSize(rs.getInt("file_size"));
                doc.setReturnValue(ReturnValues.FOUND);

                logger.debug("Load Documento" + doc);
            } else {
                doc.setReturnValue(ReturnValues.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.error("Load Documento", e);
            throw new DataException("Cannot load the Documento");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return doc;

    }

    public DocumentoVO newDocumentoVO(DocumentoVO documento)
            throws DataException {
        DocumentoVO docOut;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            docOut = newDocumentoVO(connection, documento);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return docOut;
    }

    public DocumentoVO newDocumentoVO(Connection connection,
            DocumentoVO documento) throws DataException {

        DocumentoVO newDocumentoVO = new DocumentoVO();
        newDocumentoVO.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;

        // lucene indexer
        IndexerDAO indexer;
        
        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }

            bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
            String fileSystemPath = bundle.getString("file_system_path_documentale");
            File f = new File(fileSystemPath);
            if(!f.exists())f.mkdirs();
            String fullFileSystemPath = fileSystemPath + documento.getFileName();
            pstmt = connection.prepareStatement(INSERT_FILE_REP);
            pstmt.setInt(1, documento.getId().intValue());
            pstmt.setString(2, documento.getContentType());
            pstmt.setString(3, documento.getImpronta());
            pstmt.setString(4, documento.getFileName());
            pstmt.setString(5, fullFileSystemPath);
            pstmt.setInt(6, documento.getSize());
            pstmt.setString(7, documento.getRowCreatedUser());
            pstmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();
            logger.debug("DocumentoVO inserito - id="
                    + documento.getId().intValue());
            File in = new File(documento.getPath());
            long length = in.length();
            FileInputStream fis = new FileInputStream(in);
            byte[] bytes = new byte[(int)length];
            
            // Read in the bytes
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=fis.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
            fis.close();
            
            OutputStream out = new BufferedOutputStream(new FileOutputStream(fullFileSystemPath));
            out.write(bytes);
            out.close();
            
            String fileSystemIndexPath = bundle.getString("file_system_index_path_documentale");
            File file = new File(fileSystemIndexPath);
            if(!file.exists())file.mkdirs();
            FileUtil.indexFile(documento, fullFileSystemPath, fileSystemIndexPath);
            //indexing
            //indexer = new IndexerDAO(fileSystemIndexPath);
            //indexer.setContentParser(new TikaDocumentParser(documento.getId().intValue()));
            //indexer.index(fileSystemIndexPath, fullFileSystemPath);
            
        } catch (Exception e) {
            logger.error("Save Documento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }

        newDocumentoVO = getDocumento(connection, documento.getId().intValue());
        newDocumentoVO.setPath(documento.getPath());
        newDocumentoVO.setReturnValue(ReturnValues.SAVED);
        return newDocumentoVO;
    }

    public FileVO getFileVO(int id) throws DataException {
        FileVO file;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            file = getFileVO(connection, id);
        } catch (Exception e) {
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return file;
    }

    public int getFileId(Connection connection, int dfaId) throws DataException {

        int idFile = 0;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            pstmt = connection.prepareStatement(SELECT_FILE_ID);
            pstmt.setInt(1, dfaId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Imposta Campi Oggetto
                idFile = rs.getInt("dfr_id");
                logger.debug("getFileId " + dfaId);
            }

        } catch (Exception e) {
            logger.error("Load File", e);
            throw new DataException("Impossibile caricare l'id del File:"
                    + dfaId);
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return idFile;

    }

    public FileVO getFileVO(Connection connection, int id) throws DataException {

        FileVO file = new FileVO();
        file.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            pstmt = connection.prepareStatement(SELECT_FILE_BY_ID);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Imposta Campi Oggetto
                caricaDatiFile(rs, file);

                logger.debug("Load File" + file);
            } else {
                file.setReturnValue(ReturnValues.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.error("Load File", e);
            throw new DataException("Impossibile caricare i deti del File:"
                    + id);
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return file;

    }

    public FileVO getStoriaFileVO(Connection connection, int id, int versione)
            throws DataException {

        FileVO file = new FileVO();
        file.setReturnValue(ReturnValues.UNKNOWN);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }
            pstmt = connection.prepareStatement(SELECT_STORIA_FILE_BY_ID);
            pstmt.setInt(1, id);
            pstmt.setInt(2, versione);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Imposta Campi Oggetto
                caricaDatiFile(rs, file);

                logger.debug("Load File" + file);
            } else {
                file.setReturnValue(ReturnValues.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.error("Load File", e);
            throw new DataException("Impossibile caricare i deti del File:"
                    + id);
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }

        return file;

    }

    public HashMap getFiles(int cartellaId) throws DataException {
        HashMap files = new HashMap(5);
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_FILES_BY_CARTELLA_ID);
            pstmt.setInt(1, cartellaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                FileVO file = new FileVO();
                caricaDatiFile(rs, file);
                files.put(file.getId(), file);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return files;
    }

    public HashMap getFilesLista(int cartellaId) throws DataException {
        HashMap files = new HashMap(5);
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection
                    .prepareStatement(SELECT_FILES_BY_CARTELLA_ID_LISTA);
            pstmt.setInt(1, cartellaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                FileVO file = new FileVO();
                caricaDatiFileLista(rs, file);
                files.put(file.getId(), file);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            jdbcMan.close(connection);
        }
        return files;
    }
    
    
    /**
     * 
     */
    public HashMap getFileCondivisi(String ufficiIds, int utenteId)
            throws DataException {
    	return caricaFileCondivisi(ufficiIds, utenteId, false);

    }
  
    
    /**
     * 
     */
    public HashMap getFileCondivisiC(String ufficiIds, int utenteId)
            throws DataException {
    	return caricaFileCondivisi(ufficiIds, utenteId, true);
    }
    
    
    
    private HashMap caricaFileCondivisi(String ufficiIds, int utenteId, boolean isLista) throws DataException{
	    String sql;
    	if(isLista){
    		sql = SELECT_FILE_CONDIVISI_LISTA;
	    }else{
	    	sql = SELECT_FILE_CONDIVISI;
	    }
    	HashMap files = new HashMap(5);
	    Connection connection = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        // TODO: permesso assegnato ad un documento per un ufficio al di
	        // sotto di quello "in uso" dall'utente vengono ignorati:
	        // CORREGGERE.
	        connection = jdbcMan.getConnection();
	        pstmt = connection.prepareStatement(sql.replaceAll("_lista_uffici_", ufficiIds));
	        pstmt.setInt(1, utenteId);// non deve essere owner
	        pstmt.setInt(2, utenteId);
	        rs = pstmt.executeQuery();
	        while (rs.next()) {
	            FileVO file = new FileVO();
	            if(isLista){
	            	caricaDatiFileC(rs, file);
	    	    }else{
	    	    	caricaDatiFile(rs, file);
	    	    }
	            files.put(file.getId(), file);
	        }
	    } catch (Exception e) {
	        logger.error("", e);
	        throw new DataException(e.getMessage());
	    } finally {
	    	jdbcMan.closeAll(rs, pstmt, connection);
	    }
	    return files;
    }
    
    
    
    public Collection getVersioniDocumento(int dfaId) throws DataException {
        ArrayList docs = new ArrayList(2);
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_VERSIONI_DOCUMENTO);
            pstmt.setInt(1, dfaId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                DocumentoView documento = new DocumentoView();
                documento.setDocumentoId(rs.getInt("DFA_ID"));
                documento.setArgomento(rs.getString("DESCRIZIONE_ARGOMENTO"));
                documento.setDataDocumento(DateUtil.formattaData(rs.getDate(
                        "DATA_DOCUMENTO").getTime()));
                documento.setNome(rs.getString("NOME"));
                documento.setOggetto(rs.getString("OGGETTO"));
                documento.setOwner(rs.getString("row_created_user"));
                documento.setStato(rs.getString("STATO_ARC"));
                logger.info("data ultima modifica:"
                        + rs.getTimestamp("row_updated_time"));
                documento.setDataModifica(DateUtil.formattaDataOra(rs
                        .getTimestamp("row_updated_time").getTime()));
                documento.setVersione(rs.getInt("versione"));
                documento.getParams().put("dfaId",
                        String.valueOf(documento.getDocumentoId()));
                documento.getParams().put("versione",
                        String.valueOf(documento.getVersione()));
                documento.getParams().put("mostraVersione", "true");
                docs.add(documento);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return docs;
    }

    public void archiviaVersione(Connection connection, int fileId, int versione)
            throws DataException {

        String[] tables = { "doc_file_attr", "doc_file_permessi",
                "fascicolo_documenti" };

        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("archivia versione - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            for (int i = 0; i < tables.length; i++) {
                String sql = "INSERT INTO storia_" + tables[i]
                        + " SELECT * FROM " + tables[i] + " WHERE dfa_id = ?";
                pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, fileId);
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
                // sql = "UPDATE " + tables[i] + " SET versione = ?"
                // + " WHERE dfa_id = ?";
                // pstmt = connection.prepareStatement(sql);
                // pstmt.setInt(1, versione);
                // pstmt.setInt(2, fileId);
                //
                // pstmt.executeUpdate();
                // jdbcMan.close(pstmt);
            }

        } catch (Exception e) {
            logger.error("storia file", e);
            throw new DataException("Cannot insert Storia File");

        } finally {
            jdbcMan.close(pstmt);
        }
    }

    /*
     * Salva i permessi associati ad un file. @param connection La connessione
     * alla quale questa transazione appartiene @param permessiFile Una
     * Collection di PermessiFileVO da salvare
     */
    public void salvaPermessiFile(Connection connection,
            Collection permessiFile, int versione) throws DataException {

        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("salvaPermessiFile - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            PermessoFileVO[] permessi = (PermessoFileVO[]) permessiFile
                    .toArray(new PermessoFileVO[0]);

            for (int i = 0; permessi != null && i < permessi.length; i++) {
                pstmt = connection.prepareStatement(INSERT_PERMESSO);
                setParametriPermesso(pstmt, (PermessoFileVO) permessi[i],
                        versione);
                pstmt.executeUpdate();
                jdbcMan.close(pstmt);
            }

        } catch (Exception e) {
            logger.error("storia file", e);
            throw new DataException("Cannot insert Storia File");

        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public Collection getSottoCartelle(Connection connection, int cartellaId)
            throws DataException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList cartelle = new ArrayList();

        try {
            if (connection == null) {
                logger.warn("getCartelleUtente - Connessione non valida:"
                        + connection);
                throw new DataException("Connessione non valida.");
            }
            pstmt = connection.prepareStatement(SELECT_SOTTO_CARTELLE);
            pstmt.setInt(1, cartellaId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CartellaVO c = new CartellaVO();
                caricaDatiCartellaVO(rs, c);
                cartelle.add(c);
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return cartelle;
    }

    public Collection getSottoCartelle(int cartellaId) throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            return getSottoCartelle(connection, cartellaId);
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
    }

    public void cancellaAlberoUtentePerUfficio(Connection connection,
            int utenteId, int ufficioId, int aooId) throws DataException {

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger
                        .warn("cancellaAlberoUtentePerUfficio - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection
                    .prepareStatement(SELECT_COUNT_DOCUMENTI_UTENTE_PER_UFFICIO);
            pstmt.setInt(1, aooId);
            pstmt.setInt(2, ufficioId);
            pstmt.setInt(3, utenteId);
            rs = pstmt.executeQuery();
            boolean hasDocs = false;
            if (rs.next()) {
                hasDocs = rs.getInt("file_count") > 0;
            }
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
            if (hasDocs)
                throw new DataException(
                        "Impossibile eliminare le cartelle perche' contengono dei documenti.");

            pstmt = connection
                    .prepareStatement(DELETE_ALBERO_CARTELLE_UTENTE_PER_UFFICIO);

            pstmt.setInt(1, utenteId);
            pstmt.setInt(2, ufficioId);
            pstmt.setInt(3, aooId);

            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("storia file", e);
            throw new DataException("Cannot insert Storia File");

        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void deleteFile(int dfaId, int dfrId) throws DataException {
        Connection connection = null;
        bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
        String fileSystemIndexPath = bundle.getString("file_system_index_path_documentale");
        File f = new File(fileSystemIndexPath);
        if(!f.exists())f.mkdirs();
        // lucene indexer
        IndexerDAO indexer;
        try {
        	indexer = new IndexerDAO(fileSystemIndexPath);
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            deleteFile(connection, dfaId, dfrId);
            connection.commit();
            indexer.delete(fileSystemIndexPath, String.valueOf(dfaId));
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }

    }

    public void deleteFile(Connection connection, int dfaId, int dfrId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            deleteReferenceFile(connection, dfaId, DELETE_STORIA_FASCICOLI_DOCUMENTI_BY_ID);
            deleteReferenceFile(connection, dfaId, DELETE_STORIA_DOC_FILE_PERMESSI_BY_ID);
            deleteReferenceFile(connection, dfaId, DELETE_STORIA_DOC_FILE_ATTR_BY_ID);
            deleteReferenceFile(connection, dfaId, DELETE_FASCICOLI_DOCUMENTI_BY_ID);
            deleteReferenceFile(connection, dfaId, DELETE_DOC_FILE_PERMESSI_BY_ID);
            deleteReferenceFile(connection, dfaId, DELETE_DOC_FILE_ATTR_BY_ID);
            deleteReferenceFile(connection, dfrId, DELETE_DOC_FILE_REP_BY_ID);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(pstmt);
        }
    }

	/**
	 * @param connection
	 * @param dfaId
	 * @return
	 * @throws SQLException
	 */
	private void deleteReferenceFile(Connection connection,
			int dfaId, String sql) throws SQLException {
		PreparedStatement pstmt;
		pstmt = connection.prepareStatement(sql);
		pstmt.setInt(1, dfaId);
		pstmt.executeUpdate();
		pstmt.close();
	}

    public int deleteCartella(int cartellaId) throws DataException,
            CannotDeleteException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int parentId = 0;
        try {
            connection = jdbcMan.getConnection();

            parentId = getCartellaVO(connection, cartellaId).getParentId();

            pstmt = connection.prepareStatement(SELECT_SOTTO_CARTELLE);
            pstmt.setInt(1, cartellaId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                throw new CannotDeleteException("non e' vuota");
            }
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);

            pstmt = connection.prepareStatement(SELECT_FILES_BY_CARTELLA_ID);
            pstmt.setInt(1, cartellaId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                throw new CannotDeleteException("contiene dei file");
            }
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);

            pstmt = connection
                    .prepareStatement(DELETE_CARTELLE_UTENTE_BY_DC_ID);
            pstmt.setInt(1, cartellaId);
            pstmt.executeUpdate();
            connection.commit();
        } catch (DataException e) {
            jdbcMan.rollback(connection);
            logger.error("Error deleteCartella", e);
            throw new DataException("error.database.cannotsave");
        } catch (SQLException e) {
            jdbcMan.rollback(connection);
            logger.error("Error deleteCartella", e);
            throw new DataException("error.database.cannotsave");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return parentId;
    }

    public void cancellaPermessi(Connection conn, int dfaId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (conn == null) {
                logger.warn("cancellaPermessi() - Invalid Connection :" + conn);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = conn.prepareStatement(DELETE_PERMESSI_FILE);
            pstmt.setInt(1, dfaId);
            pstmt.executeUpdate();

        } catch (Exception e) {
            logger.error("Error cancellaPermessi", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    /*
     * Salva un documento in un file temporaneo 'destFile' utilizzando l'id del
     * documento 'docId'. @return flag che indica l'esito dell'azione.
     */
    public void writeFileToStream(int repFileId, OutputStream os)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_DOCUMENTO_BY_ID);
            pstmt.setInt(1, repFileId);
            rs = pstmt.executeQuery();
            if (rs.next()) {

                // Blob b = rs.getBlob("data");
            	String path = rs.getString("filesystem_path");
            	File file = new File(path);
                //long length = file.length();

                InputStream in = new FileInputStream(file);
                // b.getBinaryStream();
                byte[] buffer = new byte[32768];
                int n = 0;
                while ((n = in.read(buffer)) != -1)
                    os.write(buffer, 0, n);
                in.close();
            } else {
                throw new DataException("File non trovato.");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
    }

    public void salvaPermesso(Connection connection, PermessoFileVO permesso,
            int versione) throws DataException {
        PreparedStatement pstmt = null;
        int recIns = 0;

        try {
            if (connection == null) {
                logger.warn("salvaPermesso() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            if (permesso != null) {
                pstmt = connection.prepareStatement(INSERT_PERMESSO);
                setParametriPermesso(pstmt, permesso, versione);

                recIns = pstmt.executeUpdate();
            }
            logger.info("Permessi inseriti:" + recIns);

        } catch (Exception e) {
            logger.error("Save Permessi Documento:  salvaPermesso", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    private void setParametriPermesso(PreparedStatement pstmt,
            PermessoFileVO permesso, int versione) throws Exception {
        pstmt.setInt(1, permesso.getId().intValue());
        pstmt.setInt(2, permesso.getFileAttributeId());
        if (permesso.getUtenteId() == 0) {
            pstmt.setNull(3, Types.INTEGER);
        } else {
            pstmt.setInt(3, permesso.getUtenteId());
        }
        if (permesso.getUfficioId() == 0) {
            pstmt.setNull(4, Types.INTEGER);
        } else {
            pstmt.setInt(4, permesso.getUfficioId());
        }
        pstmt.setInt(4, permesso.getUfficioId());
        pstmt.setInt(5, permesso.getTipoPermesso());
        pstmt.setString(6, permesso.getMsgPermesso());
        pstmt.setInt(7, versione);
    }

    public Collection getPermessiDocumento(Connection connection,
            int documentoId) throws DataException {
        ArrayList permessi = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            pstmt = connection.prepareStatement(PERMESSI_DOCUMENTO);
            pstmt.setInt(1, documentoId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                PermessoFileVO permesso = new PermessoFileVO();
                permesso.setId(rs.getInt("dfp_id"));
                permesso.setFileAttributeId(rs.getInt("dfa_id"));
                permesso.setUfficioId(rs.getInt("ufficio_id"));
                permesso.setUtenteId(rs.getInt("utente_id"));
                permesso.setTipoPermesso(rs.getInt("tipo_permesso"));
                permesso.setMsgPermesso(rs.getString("msg"));
                permessi.add(permesso);
            }
        } catch (Exception e) {
            logger.error("getPermessiDocumento", e);
            throw new DataException("Cannot load getPermessiDocumento");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return permessi;
    }

    public Collection getStoriaPermessiDocumento(Connection connection,
            int documentoId, int versione) throws DataException {
        ArrayList permessi = new ArrayList();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            pstmt = connection.prepareStatement(STORIA_PERMESSI_DOCUMENTO);
            pstmt.setInt(1, documentoId);
            pstmt.setInt(2, versione);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                PermessoFileVO permesso = new PermessoFileVO();
                permesso.setId(rs.getInt("dfp_id"));
                permesso.setFileAttributeId(rs.getInt("dfa_id"));
                permesso.setUfficioId(rs.getInt("ufficio_id"));
                permesso.setUtenteId(rs.getInt("utente_id"));
                permesso.setTipoPermesso(rs.getInt("tipo_permesso"));
                permessi.add(permesso);
            }
        } catch (Exception e) {
            logger.error("getStoriaPermessiDocumento", e);
            throw new DataException("Cannot load getStoriaPermessiDocumento");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return permessi;
    }

    public Collection getPermessiDocumento(int documentoId)
            throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            return getPermessiDocumento(connection, documentoId);
        } catch (Exception e) {
            logger.error("getPermessiDocumento", e);
            throw new DataException("Cannot load getPermessiDocumento");
        } finally {
            jdbcMan.close(connection);
        }
    }

    public int contaDocumenti(Utente utente, HashMap sqlDB, String testo)
            throws DataException {
        // SortedMap documenti = new TreeMap(new Comparator() {
        // public int compare(Object o1, Object o2) {
        // Integer i1 = (Integer) o1;
        // Integer i2 = (Integer) o2;
        // return i2.intValue() - i1.intValue();
        // }
        // });
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int numeroDocumenti = 0;

        StringBuffer strQuery = new StringBuffer(CONTA_LISTA_DOCUMENTI);
        if (testo != null && !"".equals(testo.trim())) {
            strQuery.append("LEFT JOIN doc_file_rep r ON r.dfr_id=f.dfr_id ");
        }
        strQuery.append("WHERE (exists (select * from doc_cartelle c "
                + "where f.dc_id  = c.dc_id and ufficio_id= ?)");
        strQuery.append("or exists (select * from doc_file_permessi p "
                + "where f.dfa_id  = p.dfa_id and ufficio_id= ?))");
        if (testo != null && !"".equals(testo.trim())) {
            strQuery.append("AND contains(r.data,'" + testo + "%',1)>0 ");
        }

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
            pstmt.setInt(indiceQuery++, utente.getUfficioInUso());
            pstmt.setInt(indiceQuery++, utente.getUfficioInUso());
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
                                            + "%");
                        } else {
                            pstmt.setString(indiceQuery, value.toString());
                        }
                    } else if (value instanceof java.util.Date) {
                        java.util.Date d = (java.util.Date) value;
                        pstmt.setDate(indiceQuery, new java.sql.Date(d
                                .getTime()));
                    } else if (value instanceof Boolean) {
                        pstmt.setBoolean(indiceQuery, ((Boolean) value)
                                .booleanValue());
                    }
                    indiceQuery++;
                }
            }

            rs = pstmt.executeQuery();

            if (rs.next()) {

                numeroDocumenti = rs.getInt(1);

            }

        } catch (Exception e) {
            logger.error("cercaDocumenti", e);
            throw new DataException("Cannot load cercaDocumenti");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return numeroDocumenti;

    }

    public SortedMap cercaDocumenti(Utente utente, HashMap sqlDB, String testo)
            throws DataException {
        SortedMap documenti = new TreeMap(new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = (Integer) o1;
                Integer i2 = (Integer) o2;
                return i2.intValue() - i1.intValue();
            }
        });
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuffer strQuery = new StringBuffer(SELECT_LISTA_DOCUMENTI);
        if (testo != null && !"".equals(testo.trim())) {
            strQuery.append("LEFT JOIN doc_file_rep r ON r.dfr_id=f.dfr_id ");
        }
        strQuery.append("WHERE (exists (select * from doc_cartelle c "
                + "where f.dc_id  = c.dc_id and ufficio_id= ?)");
        strQuery.append("or exists (select * from doc_file_permessi p "
                + "where f.dfa_id  = p.dfa_id and ufficio_id= ?))");
        if (testo != null && !"".equals(testo.trim())) {
        	List<String> documentIds = null;
        	SearcherDocumentaleDao searcher = new SearcherDocumentaleDao();
        	documentIds = searcher.searchText(testo);
        	List<Integer> listSql = new ArrayList<Integer>();
        	if(documentIds.size() > 0) {
        		for(int i = 0; i < documentIds.size(); i++) {
        			listSql.add(new Integer(Integer.parseInt(documentIds.get(i))));
        		}
        	}
        	strQuery.append("AND r.dfr_id IN (");
        	for(int i = 0; i< listSql.size(); i++) {
        		strQuery.append(listSql.get(i));
        		if(i < listSql.size() -1) {
        			strQuery.append(", ");
        		}
        	}
        	strQuery.append(") ");
        }

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
            pstmt.setInt(indiceQuery++, utente.getUfficioInUso());
            pstmt.setInt(indiceQuery++, utente.getUfficioInUso());
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
                                            + "%");
                        } else {
                            pstmt.setString(indiceQuery, value.toString());
                        }
                    } else if (value instanceof java.util.Date) {
                        java.util.Date d = (java.util.Date) value;
                        pstmt.setDate(indiceQuery, new java.sql.Date(d
                                .getTime()));
                    } else if (value instanceof Boolean) {
                        pstmt.setBoolean(indiceQuery, ((Boolean) value)
                                .booleanValue());
                    }
                    indiceQuery++;
                }
            }
            
            logger.log(Priority.DEBUG, "la query è:  "+strQuery.toString());  //for debug
            logger.info(strQuery);
            rs = pstmt.executeQuery();
            DocumentoView documento = null;
            while (rs.next()) {
                documento = new DocumentoView();
                documento.setDocumentoId(rs.getInt("DFA_ID"));
                documento.setArgomento(rs.getString("DESCRIZIONE_ARGOMENTO"));
                documento.setDataDocumento(DateUtil.formattaData(rs.getDate(
                        "DATA_DOCUMENTO").getTime()));
                documento.setNome(rs.getString("NOME"));
                documento.setOggetto(rs.getString("OGGETTO"));
                documento.setStato(rs.getString("STATO_ARC"));
                documento.setStatoLav(rs.getString("stato_lav"));
                documento.setUsernameLav(rs.getString("FULL_NAME"));
                documento.setTipoDocumento(rs.getString("desc_tipo_documento"));
                documento.setOwner(rs.getString("row_created_user"));
                documento.setAssegnatoDa(rs.getString("full_name"));
                // documento.setMessaggio(rs.getString("messaggio"));
                // todo: filtro Ricerca archivio Documentale
                documento.setDescrizione(rs.getString("DESCRIZIONE"));
                documento.setNote(rs.getString("NOTE"));
                documento.setTipoDocumentoId(rs.getString("tipo_documento_id"));
                documenti.put(new Integer(documento.getDocumentoId()),
                        documento);
            }

        } catch (Exception e) {
            logger.error("cercaDocumenti", e);
            throw new DataException("Cannot load cercaDocumenti");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return documenti;

    }

    public boolean hasAccessToFolder(int cartellaId, int utenteId)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ret = false;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_CARTELLA_ID_UTENTE_ID);
            pstmt.setInt(1, cartellaId);
            pstmt.setInt(2, utenteId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ret = rs.getInt("total") > 0;
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return ret;
    }

    public int getTipoPermessoSuDocumento(int documentoId, int utenteId,
            String ufficiIds) throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int ret = -1;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_PERMESSO_DOCUMENTO_ID
                    .replaceAll("_lista_uffici_", ufficiIds));
            pstmt.setInt(1, documentoId);
            pstmt.setInt(2, utenteId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ret = rs.getInt("tipo_permesso");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return ret;
    }

    public boolean isOwnerDocumento(int documentoId, int utenteId)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean ret = false;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_OWNER_DOCUMENTO_ID);
            pstmt.setInt(1, documentoId);
            pstmt.setInt(2, utenteId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ret = rs.getInt("FOUND") > 0;
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return ret;
    }

    public int classificaDocumento(int dfrId, int titolarioId)
            throws DataException {
        int redUpd = 0;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            redUpd = classificaDocumento(connection, dfrId, titolarioId);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return redUpd;
    }

    public int classificaDocumento(Connection connection, int dfrId,
            int titolarioId) throws DataException {
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            if (connection == null) {
                logger.warn("classificaDocumento() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(CLASSIFICA_DOCUMENTO);
            pstmt.setInt(1, titolarioId);
            pstmt.setInt(2, dfrId);
            n = pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("classificaDocumento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return n;
    }

    public boolean spostaInLavorazione(int docId) throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            spostaInLavorazione(connection, docId);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return true;
    }

    public boolean spostaInLavorazione(Connection connection, int docId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("spostaInLavorazione() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection
                    .prepareStatement(SPOSTA_DOCUMENTO_IN_LAVORAZIONE);
            pstmt.setNull(1, Types.INTEGER);
            pstmt.setInt(2, docId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("spostaInLavorazione", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return true;
    }

    public boolean spostaDocumento(int cartellaDestinazioneId, int docId)
            throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            spostaDocumento(connection, cartellaDestinazioneId, docId);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return true;
    }

    public boolean spostaDocumento(Connection connection,
            int cartellaDestinazioneId, int docId) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("spostaDocumento() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection
                    .prepareStatement(SPOSTA_DOCUMENTO_NELLA_CARTELLA);
            pstmt.setInt(1, cartellaDestinazioneId);
            pstmt.setInt(2, docId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("spostaDocumento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return true;
    }

    public int invioDocumento(int docId) throws DataException {
        int ret = ReturnValues.UNKNOWN;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            ret = invioDocumento(connection, docId);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return ret;
    }

    public int invioDocumento(Connection connection, int docId)
            throws DataException {
        int ret = ReturnValues.UNKNOWN;
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("invioDocumento() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(INVIO_DOCUMENTO);
            pstmt.setInt(1, docId);
            pstmt.executeUpdate();
            ret = ReturnValues.SAVED;

        } catch (SQLException e) {
            logger.error("invioDocumento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return ret;
    }

    public int checkoutDocumento(int docId, int utenteId) throws DataException {
        int ret = ReturnValues.UNKNOWN;
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            ret = checkoutDocumento(connection, docId, utenteId);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return ret;
    }

    public int checkoutDocumento(Connection connection, int docId, int utenteId)
            throws DataException {
        int ret = ReturnValues.UNKNOWN;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            if (connection == null) {
                logger.warn("spostaInLavorazione() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(GET_STATO_DOCUMENTO);
            pstmt.setInt(1, docId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String stato = rs.getString("stato_lav");
                if (Parametri.CHECKED_OUT.equals(stato)) {
                    ret = ReturnValues.OLD_VERSION;
                } else {
                    jdbcMan.close(rs);
                    jdbcMan.close(pstmt);
                    pstmt = connection.prepareStatement(CHECKOUT_DOCUMENTO);
                    pstmt.setInt(1, utenteId);
                    pstmt.setInt(2, docId);
                    pstmt.executeUpdate();
                    ret = ReturnValues.VALID;
                }
            } else {
                ret = ReturnValues.NOT_FOUND;
            }
        } catch (SQLException e) {
            logger.error("checkoutDocumento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return ret;
    }

    public boolean checkinDocumento(int docId) throws DataException {
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            checkinDocumento(connection, docId);
            connection.commit();
        } catch (Exception e) {
            jdbcMan.rollback(connection);
            throw new DataException(e.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return true;
    }

    public boolean checkinDocumento(Connection connection, int docId)
            throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("spostaInLavorazione() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }

            pstmt = connection.prepareStatement(CHECKIN_DOCUMENTO);
            pstmt.setInt(1, docId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("checkoutDocumento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return true;
    }

    public Collection getDocumentiArchivioInvio(int aooId) throws DataException {

        Collection documenti = new ArrayList();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_INVIO_CLASSIFICATI);
            pstmt.setInt(1, aooId);

            rs = pstmt.executeQuery();
            DocumentoView documento = null;
            while (rs.next()) {
                documento = new DocumentoView();
                documento.setDocumentoId(rs.getInt("dfa_id"));
                documento.setNome(rs.getString("nome"));
                documento.setOggetto(rs.getString("oggetto"));
                documenti.add(documento);

            }

        } catch (Exception e) {
            logger.error("Load getDocumentiArchivioInvio", e);
            throw new DataException("Cannot load getDocumentiArchivioInvio");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return documenti;
    }

    public Map getDestinatariDocumentiInvio(int documentoId)
            throws DataException {
        HashMap destinatari = new HashMap();
        Connection connection = null;
        try {
            connection = jdbcMan.getConnection();
            destinatari = (HashMap) getDestinatariDocumentiInvio(connection,
                    documentoId);
        } catch (Exception e) {
            throw new DataException(e.getMessage() + " DocumentoId :"
                    + documentoId);
        } finally {
            jdbcMan.close(connection);
        }
        return destinatari;
    }

    public Map getDestinatariDocumentiInvio(Connection connection,
            int documentoId) throws DataException {

        HashMap destinatari = new HashMap();
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement(SELECT_DESTINATARI_DOCUMENTO);
            pstmt.setInt(1, documentoId);

            rs = pstmt.executeQuery();
            rs = pstmt.executeQuery();
            while (rs.next()) {
                DestinatarioVO destinatarioVO = new DestinatarioVO();
                destinatarioVO.setFlagTipoDestinatario(rs
                        .getString("flag_tipo_destinatario"));
                destinatarioVO.setIndirizzo(rs.getString("indirizzo"));
                destinatarioVO.setEmail(rs.getString("email"));
                destinatarioVO.setDestinatario(rs.getString("DESTINATARIO"));
                // destinatarioVO.setTitoloId(rs.getInt("titolo_id"));

                destinatarioVO.setMezzoSpedizioneId(rs
                        .getInt("mezzo_spedizione"));
                destinatarioVO.setMezzoDesc(rs.getString("desc_spedizione"));
                destinatarioVO.setCitta(rs.getString("citta"));
                destinatarioVO.setDataSpedizione(rs.getDate("data_spedizione"));
                destinatarioVO.setFlagConoscenza("1".equals(rs
                        .getString("flag_conoscenza")));
                destinatarioVO.setDataEffettivaSpedizione(rs
                        .getDate("data_effettiva_spedizione"));
                destinatarioVO.setVersione(rs.getInt("versione"));
                destinatari.put(destinatarioVO.getDestinatario(),
                        destinatarioVO);
            }

        } catch (Exception e) {
            logger.error("Load getDestinatariDocumentiInvio", e);
            throw new DataException("Cannot load getDestinatariDocumentiInvio");
        } finally {
            jdbcMan.close(rs);
            jdbcMan.close(pstmt);
        }
        return destinatari;
    }

    public InvioClassificatiVO getInvioClassificatiVO(int documentoId)
            throws DataException {

        InvioClassificatiVO documento = new InvioClassificatiVO();
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection
                    .prepareStatement(SELECT_INVIO_CLASSIFICATI_BY_ID);
            pstmt.setInt(1, documentoId);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                documento.setAooId(rs.getInt("aoo_id"));
                documento.setDocumentoId(rs.getInt("dfa_id"));
                documento.setId(rs.getInt("id"));
                documento
                        .setUfficioMittenteId(rs.getInt("ufficio_mittente_id"));
                documento.setUtenteMittenteId(rs.getInt("utente_mittente_id"));
            }

        } catch (Exception e) {
            logger.error("Load InvioClassificatiVO", e);
            throw new DataException("Cannot load InvioClassificatiVO");
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
        }
        return documento;
    }

    public int eliminaCodaInvioDocumento(Connection connection, int documentoId)
            throws DataException {
        PreparedStatement pstmt = null;
        int n = 0;
        try {
            if (connection == null) {
                logger
                        .warn("eliminaCodaInvioDocumento() - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection
                    .prepareStatement(DELETE_CODA_INVIO_CLASSIFICATI_DESTINATARI);
            pstmt.setInt(1, documentoId);
            n = pstmt.executeUpdate();
            pstmt.close();

            pstmt = connection.prepareStatement(DELETE_CODA_INVIO_CLASSIFICATI);
            pstmt.setInt(1, documentoId);
            n = pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            logger.error("eliminaCodaInvioDocumento", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
        return n;
    }

    public void salvaInvioClassificati(Connection connection,
            InvioClassificatiVO icVO) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger.warn("salvaInvioClassificati() - Invalid Connection :"
                        + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INVIO_CLASSIFICATI);
            pstmt.setInt(1, icVO.getId().intValue());
            pstmt.setInt(2, icVO.getDocumentoId());
            pstmt.setInt(3, icVO.getAooId());
            pstmt.setInt(4, icVO.getUfficioMittenteId());
            pstmt.setInt(5, icVO.getUtenteMittenteId());
            pstmt.executeUpdate();
            logger.info("Invio Classificati :" + icVO.getDocumentoId());
            // deleteFile(connection, int dfaId, int dfrId)

        } catch (Exception e) {
            logger.error("salvaInvioClassificati", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }

    public void salvaDestinatariInvioClassificati(Connection connection,
            InvioClassificatiDestinatariVO icdVO) throws DataException {
        PreparedStatement pstmt = null;
        try {
            if (connection == null) {
                logger
                        .warn("salvaDestinatariInvioClassificati() - Invalid Connection :"
                                + connection);
                throw new DataException(
                        "Connessione alla base dati non valida.");
            }
            pstmt = connection.prepareStatement(INVIO_CLASSIFICATI_DESTINATARI);
            pstmt.setInt(1, icdVO.getId().intValue());
            pstmt.setInt(2, icdVO.getDocumentoId());
            DestinatarioVO d = icdVO.getDestinatario();
            pstmt.setString(3, d.getFlagTipoDestinatario());
            pstmt.setString(4, d.getIndirizzo());
            pstmt.setString(5, d.getEmail());
            pstmt.setString(6, d.getDestinatario());

            pstmt.setInt(7, d.getMezzoSpedizioneId());
            // if (d.getTitoloId() == 0)
            // pstmt.setInt(13, 999);
            // else
            // pstmt.setInt(13, d.getTitoloId());
            pstmt.setString(8, d.getCitta());
            if (d.getDataSpedizione() != null) {
                pstmt.setDate(9, new Date(d.getDataSpedizione().getTime())); // data
            } else {
                pstmt.setNull(9, Types.DATE);
            }

            pstmt.setString(10, d.getFlagConoscenza() ? "1" : "0");
            if (d.getDataEffettivaSpedizione() != null) {
                pstmt.setDate(11, new Date(d.getDataEffettivaSpedizione()
                        .getTime()));
            } else {
                pstmt.setNull(11, Types.DATE);
            }
            pstmt.setInt(12, d.getVersione());
            pstmt.executeUpdate();
            logger.info("salvaDestinatariInvioClassificati:"
                    + icdVO.getDocumentoId() + "," + d.getId());

        } catch (Exception e) {
            logger.error("salvaDestinatariInvioClassificati", e);
            throw new DataException("error.database.cannotsave");
        } finally {
            jdbcMan.close(pstmt);
        }
    }
    
    /**
     * Restituisce tutti i documenti salvati per 
     * la reindicizzazione con lucene da listener
     * @return HashMap<Integer, String>
     */
    public HashMap<Integer, String> getDocumenti() 
    	throws DataException {
    	
    	HashMap<Integer, String> map = new HashMap<Integer, String>();
    	Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
	            connection = jdbcMan.getConnection();
	            pstmt = connection.prepareStatement(GET_DOCUMENTI);
	            rs = pstmt.executeQuery();
	            while (rs.next()) {
	            	map.put(rs.getInt("dfr_id"), rs.getString("filesystem_path"));
	            }
        	} catch (Exception e) {
                logger.error("", e);
                throw new DataException(e.getMessage());
            } finally {
            	jdbcMan.closeAll(rs, pstmt, connection);
            }
    	return map;
    }
    
    private final static String GET_DOCUMENTI = "SELECT * FROM doc_file_rep";

    public static final String SELECT_DESTINATARI_DOCUMENTO = "select icd.*, s.desc_spedizione from invio_classificati_destinatari icd left outer join spedizioni s on (icd.mezzo_spedizione=s.spedizioni_id) where icd.dfa_id=?";

    // private final static String SELECT_LISTA_DOCUMENTI = "SELECT f.*,
    // t.desc_tipo_documento, (U.NOME || ' ' || U.COGNOME)AS FULL_NAME ,
    // p.messaggio FROM DOC_FILE_ATTR f "
    // + "LEFT JOIN UTENTI U ON F.USER_LAV_ID=U.UTENTE_ID "
    // + "LEFT JOIN tipi_documento t ON f.tipo_documento_id=t.tipo_documento_id
    // "
    // + "LEFT JOIN protocollo_assegnatari p ON
    // p.utente_assegnatario_id=f.user_lav_id ";

    private final static String SELECT_LISTA_DOCUMENTI = "SELECT f.*, t.desc_tipo_documento, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME FROM DOC_FILE_ATTR f "
            + "LEFT JOIN UTENTI U ON F.USER_LAV_ID=U.UTENTE_ID "
            + "LEFT JOIN tipi_documento t ON f.tipo_documento_id=t.tipo_documento_id ";

    private final static String CONTA_LISTA_DOCUMENTI = "SELECT count (f.dc_id)FROM DOC_FILE_ATTR f "
            + "LEFT JOIN UTENTI U ON F.USER_LAV_ID=U.UTENTE_ID "
            + "LEFT JOIN tipi_documento t ON f.tipo_documento_id=t.tipo_documento_id ";

    private final static String PERMESSI_DOCUMENTO = "SELECT * FROM doc_file_permessi WHERE dfa_id = ?";

    private final static String STORIA_PERMESSI_DOCUMENTO = "SELECT * FROM storia_doc_file_permessi WHERE dfa_id = ? and versione=?";

    private final static String SELECT_DOCUMENTO_BY_ID = "SELECT * FROM doc_file_rep WHERE dfr_id = ?";

    private final static String INSERT_FILE_REP = "INSERT INTO doc_file_rep "
            + "( dfr_id, content_type, impronta, filename, filesystem_path, file_size, row_created_user, row_created_time) "
            + " VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";

    private final static String SELECT_CARTELLA_BY_ID = "SELECT * FROM DOC_CARTELLE WHERE DC_ID = ?";

    private final static String SELECT_CARTELLA_BY_UFFICIO_ID_UTENTE_ID = "SELECT * FROM DOC_CARTELLE WHERE UFFICIO_ID=? AND UTENTE_ID=? AND IS_ROOT=1";

    private final static String SELECT_CARTELLA_BY_UFFICIO_ID_UTENTE_ID_NAME = "SELECT * FROM DOC_CARTELLE WHERE UFFICIO_ID=? AND UTENTE_ID=? AND IS_ROOT=0 AND UPPER(NOME)=? and DC_ID !=?";

    private final static String SELECT_FILE_BY_ID = "SELECT F.*, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME  FROM DOC_FILE_ATTR F LEFT JOIN UTENTI U ON F.USER_LAV_ID=U.UTENTE_ID WHERE DFA_ID = ?";

    private final static String SELECT_STORIA_FILE_BY_ID = "SELECT F.*, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME  FROM STORIA_DOC_FILE_ATTR F LEFT JOIN UTENTI U ON F.USER_LAV_ID=U.UTENTE_ID WHERE F.DFA_ID = ? AND F.VERSIONE=?";

    private final static String SELECT_VERSIONE_FILE = "SELECT VERSIONE FROM DOC_FILE_ATTR WHERE DFA_ID = ?";

    private final static String SELECT_FILES_BY_CARTELLA_ID = "SELECT FILES.*, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME FROM DOC_FILE_ATTR FILES LEFT JOIN UTENTI U ON FILES.USER_LAV_ID=U.UTENTE_ID WHERE FILES.DC_ID=?";

    private final static String SELECT_FILES_BY_CARTELLA_ID_LISTA = "SELECT FILES.*, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME, p.messaggio FROM DOC_FILE_ATTR FILES LEFT JOIN protocollo_assegnatari p ON p.utente_assegnatario_id=FILES.user_lav_id LEFT JOIN UTENTI U ON FILES.USER_LAV_ID=U.UTENTE_ID WHERE FILES.DC_ID=?";

    private final static String SELECT_FILE_CONDIVISI = "SELECT FILES.*, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME  FROM DOC_FILE_ATTR FILES LEFT JOIN UTENTI U ON FILES.USER_LAV_ID=U.UTENTE_ID, DOC_FILE_PERMESSI PERM WHERE FILES.DFA_ID=PERM.DFA_ID AND PERM.UFFICIO_ID IN( _lista_uffici_ ) AND FILES.OWNER_ID<>? AND(PERM.UTENTE_ID IS NULL OR PERM.UTENTE_ID=?)";

    private final static String SELECT_FILE_CONDIVISI_LISTA = "SELECT FILES.*, (U.NOME || ' ' || U.COGNOME) AS FULL_NAME, perm.msg "
            + "FROM DOC_FILE_ATTR FILES, UTENTI U, DOC_FILE_PERMESSI PERM "
            + " WHERE FILES.DFA_ID=PERM.DFA_ID AND FILES.OWNER_ID=U.UTENTE_ID"
            + " AND PERM.UFFICIO_ID IN( _lista_uffici_ ) AND FILES.OWNER_ID<>? AND(PERM.UTENTE_ID IS NULL OR PERM.UTENTE_ID=?)";

    private final static String SELECT_SOTTO_CARTELLE = "SELECT * FROM DOC_CARTELLE WHERE PARENT_ID=?";

    private final static String INSERT_CARTELLA = "INSERT INTO DOC_CARTELLE (DC_ID, UFFICIO_ID, UTENTE_ID, PARENT_ID, NOME, IS_ROOT, AOO_ID ) VALUES ( ?, ?, ?, ?, ?, ?, ?)";

    private final static String UPDATE_NOME_CARTELLA = "UPDATE DOC_CARTELLE SET NOME=? WHERE DC_ID=?";

    private final static String DELETE_ALBERO_CARTELLE_UTENTE_PER_UFFICIO = "DELETE FROM DOC_CARTELLE WHERE UTENTE_ID=? AND UFFICIO_ID=? AND AOO_ID=?";

    private final static String DELETE_PERMESSI_FILE = "DELETE FROM DOC_FILE_PERMESSI WHERE DFA_ID=?";

    private final static String SELECT_COUNT_DOCUMENTI_UTENTE_PER_UFFICIO = "SELECT COUNT(F.*) AS FILE_COUNT FROM DOC_FILE_ATTR F, DOC_CARTELLE DC WHERE F.DC_ID=DC.DC_ID AND DC.AOO_ID=? AND DC.UFFICIO_ID=? AND DC.UTENTE_ID=?";

    private final static String INSERT_FILE = "INSERT INTO DOC_FILE_ATTR "
            + "( DFA_ID, DC_ID, DFR_ID, NOME, DATA_DOCUMENTO, OGGETTO, NOTE, DESCRIZIONE, DESCRIZIONE_ARGOMENTO, "
            + "TIPO_DOCUMENTO_ID, TITOLARIO_ID, STATO_LAV, STATO_ARC, USER_LAV_ID, OWNER_ID, ROW_CREATED_TIME, "
            + "ROW_CREATED_USER, VERSIONE ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static String UPDATE_FILE = "UPDATE DOC_FILE_ATTR SET DFR_ID=?, NOME=?, DATA_DOCUMENTO=?, OGGETTO=?, NOTE=?, DESCRIZIONE=?, DESCRIZIONE_ARGOMENTO=?, TIPO_DOCUMENTO_ID=?, TITOLARIO_ID=?, STATO_LAV=?, STATO_ARC=?, VERSIONE=?, USER_LAV_ID=?, ROW_UPDATED_TIME=? WHERE DFA_ID=?";

    private final static String INSERT_PERMESSO = "INSERT INTO DOC_FILE_PERMESSI (DFP_ID, DFA_ID, UTENTE_ID, UFFICIO_ID, TIPO_PERMESSO, MSG,VERSIONE  ) VALUES ( ?, ?, ?, ?, ?, ?, ?) ";

    // private final static String DELETE_CARTELLA_UFFICIO_BY_DC_ID = "DELETE
    // FROM doc_cartelle WHERE dc_id=?";

    private final static String DELETE_CARTELLE_UTENTE_BY_DC_ID = "DELETE FROM doc_cartelle WHERE dc_id=?";

    private final static String SELECT_CARTELLA_ID_UTENTE_ID = "SELECT COUNT(*) AS TOTAL FROM DOC_CARTELLE WHERE DC_ID=? AND UTENTE_ID=?";

    private final static String SELECT_PERMESSO_DOCUMENTO_ID = "SELECT PERM.TIPO_PERMESSO FROM DOC_FILE_ATTR FILES, DOC_FILE_PERMESSI PERM WHERE PERM.DFA_ID=? AND FILES.DFA_ID=PERM.DFA_ID AND PERM.UFFICIO_ID IN( _lista_uffici_ ) AND (PERM.UTENTE_ID IS NULL OR PERM.UTENTE_ID=?) ";

    private final static String SELECT_OWNER_DOCUMENTO_ID = "SELECT COUNT(*) AS FOUND FROM DOC_FILE_ATTR WHERE DFA_ID=? AND OWNER_ID=?";

    public static final String SELECT_INVIO_CLASSIFICATI = "select i.*, d.dfa_id, d.nome, d.oggetto "
            + "from invio_classificati i left join doc_file_attr d on i.dfa_id=d.dfa_id "
            + "where i.aoo_id=?  order by d.dfa_id desc";

    public static final String SELECT_INVIO_CLASSIFICATI_BY_ID = "select * "
            + "from invio_classificati where dfa_id=?";

    private final static String CLASSIFICA_DOCUMENTO = "UPDATE doc_file_attr SET stato_arc='"
            + Parametri.STATO_CLASSIFICATO
            + "', stato_lav='"
            + Parametri.CHECKED_IN + "',titolario_id=? WHERE dfa_id=?";

    // private final static String UPDATE_STATO_INVIO_CLASSIFICATI = "UPDATE
    // INVIO_CLASSIFICATI SET flag_stato=? WHERE dfa_id=?";

    private final static String SPOSTA_DOCUMENTO_IN_LAVORAZIONE = "UPDATE doc_file_attr SET stato_arc='"
            + Parametri.STATO_LAVORAZIONE + "', titolario_id=? WHERE dfa_id=?";

    private final static String CHECKOUT_DOCUMENTO = "UPDATE doc_file_attr SET stato_lav='"
            + Parametri.CHECKED_OUT + "', user_lav_id=? WHERE dfa_id=?";

    private final static String INVIO_DOCUMENTO = "UPDATE doc_file_attr SET stato_arc='"
            + Parametri.STATO_INVIATO_PROTOCOLLO + "' WHERE dfa_id=?";

    private final static String GET_STATO_DOCUMENTO = "SELECT STATO_LAV FROM DOC_FILE_ATTR WHERE DFA_ID=?";

    private final static String CHECKIN_DOCUMENTO = "UPDATE DOC_FILE_ATTR SET STATO_LAV='"
            + Parametri.CHECKED_IN + "' WHERE DFA_ID=?";

    private final static String DELETE_STORIA_DOC_FILE_PERMESSI_BY_ID = "DELETE FROM STORIA_DOC_FILE_PERMESSI WHERE DFA_ID=?";

    private final static String DELETE_STORIA_DOC_FILE_ATTR_BY_ID = "DELETE FROM STORIA_DOC_FILE_ATTR WHERE DFA_ID=?";

    private final static String DELETE_STORIA_FASCICOLI_DOCUMENTI_BY_ID = "DELETE FROM storia_fascicolo_documenti WHERE dfa_id=?";

    private final static String DELETE_DOC_FILE_PERMESSI_BY_ID = "DELETE FROM DOC_FILE_PERMESSI WHERE DFA_ID=?";

    private final static String DELETE_DOC_FILE_ATTR_BY_ID = "DELETE FROM DOC_FILE_ATTR WHERE DFA_ID=?";

    private final static String DELETE_DOC_FILE_REP_BY_ID = "DELETE FROM DOC_FILE_REP WHERE DFR_ID=?";

    private final static String DELETE_FASCICOLI_DOCUMENTI_BY_ID = "DELETE FROM fascicolo_documenti WHERE dfa_id=?";

    private final static String SELECT_VERSIONI_DOCUMENTO = "SELECT * FROM STORIA_DOC_FILE_ATTR WHERE DFA_ID=? ORDER BY versione DESC";

    private final static String SPOSTA_DOCUMENTO_NELLA_CARTELLA = "UPDATE DOC_FILE_ATTR SET DC_ID=? WHERE DFA_ID=?";

    public static final String INVIO_CLASSIFICATI = "INSERT INTO invio_classificati "
            + " (id, dfa_id, aoo_id, ufficio_mittente_id, utente_mittente_id) VALUES (?, ?, ?, ?, ?)";

    public static final String INVIO_CLASSIFICATI_DESTINATARI = "INSERT INTO invio_classificati_destinatari (id, dfa_id, flag_tipo_destinatario, indirizzo,  email ,  destinatario ,"
            + " mezzo_spedizione, citta ,  data_spedizione ,  flag_conoscenza ,  data_effettiva_spedizione,"
            + "versione"
            // + ", titolo_id"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
            // + ", ?"
            + ")";

    public static final String DELETE_CODA_INVIO_CLASSIFICATI = "DELETE FROM INVIO_CLASSIFICATI WHERE dfa_id=?";

    public static final String DELETE_CODA_INVIO_CLASSIFICATI_DESTINATARI = "DELETE FROM invio_classificati_destinatari WHERE dfa_id=?";

    private final static String SELECT_FILE_ID = "SELECT dfr_id FROM DOC_FILE_ATTR WHERE DFA_ID = ?";

}