package it.finsiel.siged.dao.jdbc;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.DocumentoDAO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.flosslab.dao.indexer.IndexerDAO;
import it.flosslab.parser.TikaDocumentParser;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.mail.Session;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

public class DocumentoDAOjdbc implements DocumentoDAO, DocDaoInterface {

    static Logger logger = Logger.getLogger(DocumentoDAOjdbc.class.getName());

    private JDBCManager jdbcMan = new JDBCManager();
    
    private static ResourceBundle bundle;
    
    private static String SYSTEM_PARAMS = "systemParams";
    
    private final static String slash = "/";
    private final static String[] monthName = {"Gennaio","Febbraio","Marzo","Aprile","Maggio","Giugno","Luglio","Agosto","Settembre","Ottobre","Novembre","Dicembre"};

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
        String fileSystemPath = "";

        try {
            if (connection == null) {
                logger.warn("Connection is:" + connection);
                throw new DataException("La connessione fornita ? invalida.");
            }

            pstmt = connection.prepareStatement(INSERT_DOCUMENTO);
            
            bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
            fileSystemPath = bundle.getString("file_system_path_protocollo").endsWith("/") ? bundle.getString("file_system_path_protocollo") : bundle.getString("file_system_path_protocollo") + "/";
            
            File f = new File(fileSystemPath);
            if(!f.exists())f.mkdirs();

            String year = String.valueOf((new GregorianCalendar()).get(Calendar.YEAR));
            String month = monthName[(new GregorianCalendar()).get(Calendar.MONTH)];
            String day = String.valueOf((new GregorianCalendar()).get(Calendar.DAY_OF_MONTH));
            String oraCompleta =  String.valueOf((new GregorianCalendar()).get(Calendar.HOUR_OF_DAY)) + "_" + String.valueOf((new GregorianCalendar()).get(Calendar.MINUTE)) + "_" + String.valueOf((new GregorianCalendar()).get(Calendar.SECOND))+ "_" + String.valueOf((new GregorianCalendar()).get(Calendar.MILLISECOND));
            
            //String dir = fileSystemPath + year + slash + month + slash + day + slash;
            String dir = year + slash + month + slash + day + slash;
            boolean success = (new File(fileSystemPath + dir)).mkdirs();
            logger.debug("creation dir = " + success);
            
            //String fullFileSystemPath = dir + oraCompleta + "_" + documento.getFileName();
            String relFileSystemPath = dir + oraCompleta + "_" + documento.getFileName();
            pstmt.setInt(1, documento.getId().intValue());
            pstmt.setString(2, documento.getDescrizione());
            pstmt.setString(3, documento.getContentType());
            pstmt.setString(4, documento.getImpronta());
            pstmt.setString(5, oraCompleta + "_" + documento.getFileName());
            //pstmt.setString(6, fullFileSystemPath);
            pstmt.setString(6, relFileSystemPath);
            pstmt.setInt(7, documento.getSize());
            pstmt.setString(8, documento.getRowCreatedUser());
            pstmt.setDate(9, new Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            
            //attached files are saved in the file system
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
            
            //OutputStream out = new BufferedOutputStream(new FileOutputStream(fullFileSystemPath));  
            OutputStream out = new BufferedOutputStream(new FileOutputStream(fileSystemPath + relFileSystemPath));
            out.write(bytes);
            out.close();
            
            String fileSystemIndexPath = bundle.getString("file_system_index_path_protocollo");
            File file = new File(fileSystemIndexPath);
            if(!file.exists())file.mkdirs();
             
            //indexing
            indexer = new IndexerDAO(fileSystemIndexPath);
            indexer.setContentParser(new TikaDocumentParser(documento.getId().intValue()));
            //indexer.index(fileSystemIndexPath, fullFileSystemPath);
            indexer.index(fileSystemIndexPath, fileSystemPath + relFileSystemPath);
            //end indexing
            //FileUtil.writeFile(fis, fullFileSystemPath);
            


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
                doc.setDescrizione(rs.getString("descrizione"));
                // doc.setPath(rs.getString("path"));
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

    /*
     * Salva un documento in un file temporaneo 'destFile' utilizzando l'id del
     * documento 'docId'. @return flag che indica l'esito dell'azione.
     */
    public void writeDocumentToStream(int docId, OutputStream os)
            throws DataException {
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
        String fileSystemPath = bundle.getString("file_system_path_protocollo").endsWith("/") ? bundle.getString("file_system_path_protocollo") : bundle.getString("file_system_path_protocollo") + "/";
        
        File f = new File(fileSystemPath);
        if(!f.exists())f.mkdirs();
        
        try {
            connection = jdbcMan.getConnection();
            pstmt = connection.prepareStatement(SELECT_DOCUMENTO_BY_ID);
            pstmt.setInt(1, docId);
            rs = pstmt.executeQuery();
            if (rs.next()) {

                // Blob b = rs.getBlob("data");
            	//String path = rs.getString("filesystem_path");
            	String path = fileSystemPath + rs.getString("filesystem_path");
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
                throw new DataException("Documento non trovato.");
            }
        } catch (Exception e) {
            logger.error("", e);
            throw new DataException(e.getMessage());
        } finally {
        	jdbcMan.closeAll(rs, pstmt, connection);
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
	            	map.put(rs.getInt("documento_id"), rs.getString("filesystem_path"));
	            }
        	} catch (Exception e) {
                logger.error("", e);
                throw new DataException(e.getMessage());
            } finally {
            	jdbcMan.closeAll(rs, pstmt, connection);
            }
    	return map;
    }

    private final static String SELECT_DOCUMENTO_BY_ID = "SELECT * FROM documenti WHERE documento_id = ?";

    private final static String INSERT_DOCUMENTO = "INSERT INTO DOCUMENTI "
            + "(documento_id, DESCRIZIONE, CONTENT_TYPE, IMPRONTA,"
            + " FILENAME, FILESYSTEM_PATH, FILE_SIZE, ROW_CREATED_USER, ROW_CREATED_TIME)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private final static String GET_DOCUMENTI = "SELECT * FROM documenti";

};