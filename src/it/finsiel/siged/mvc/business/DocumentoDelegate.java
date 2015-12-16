package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.integration.DocumentoDAO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.rdb.JDBCManager;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class DocumentoDelegate implements ComponentStatus {

    private static Logger logger = Logger.getLogger(DocumentoDelegate.class
            .getName());

    private int status;

    private DocumentoDAO documentoDAO = null;

    private ServletConfig config = null;

    private static DocumentoDelegate delegate = null;

    private DocumentoDelegate() {
        try {
            if (documentoDAO == null) {
                documentoDAO = (DocumentoDAO) DAOFactory
                        .getDAO(Constants.DOCUMENTO_DAO_CLASS);

                logger.debug("UserDAO instantiated:"
                        + Constants.DOCUMENTO_DAO_CLASS);
                status = STATUS_OK;
            }
        } catch (Exception e) {
            status = STATUS_ERROR;
            logger.error("", e);
        }

    }

    public static DocumentoDelegate getInstance() {
        if (delegate == null)
            delegate = new DocumentoDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.DOCUMENTO_DELEGATE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.finsiel.siged.mvc.business.ComponentStatus#getStatus()
     */
    public int getStatus() {
        return status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.finsiel.siged.mvc.business.ComponentStatus#setStatus(int)
     */
    public void setStatus(int s) {
        this.status = s;
    }

    // fine metodi interfaccia
    public DocumentoVO getDocumento(Connection connection, int documentoId) {
        DocumentoVO doc = null;
        try {
            doc = documentoDAO.getDocumento(connection, documentoId);
            logger.info("getting documento id: " + doc.getId());
        } catch (DataException de) {
            logger.error("Failed getting Documento: " + documentoId);
        }
        return doc;
    }

    public DocumentoVO getDocumento(int documentoId) {
        DocumentoVO doc = null;
        try {
            doc = documentoDAO.getDocumento(documentoId);
            logger.info("getting documento id: " + doc.getId());
        } catch (DataException de) {
            logger.error("Failed getting Documento: " + documentoId);
        }
        return doc;
    }

    public DocumentoVO salvaDocumento(Connection connection, DocumentoVO doc)
            throws DataException {
        if (doc != null) {
            if (doc.getId() == null || doc.isMustCreateNew()) { // il documento
                // se nuovo non
                // ha Id
                doc.setId(IdentificativiDelegate.getInstance().getNextId(
                        connection, NomiTabelle.DOCUMENTI));
                doc = documentoDAO.newDocumentoVO(connection, doc);
            }
        }
        return doc;
    }

    public DocumentoVO salvaDocumento(DocumentoVO doc) {

        JDBCManager jdbcMan = null;
        Connection connection = null;
        DocumentoVO dVo = new DocumentoVO();
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            dVo = salvaDocumento(connection, doc);
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return dVo;
    }

    public void writeDocumentToStream(int docId, OutputStream os)
            throws DataException {
        documentoDAO.writeDocumentToStream(docId, os);
    }

}