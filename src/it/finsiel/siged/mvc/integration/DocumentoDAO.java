package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;

import java.io.OutputStream;
import java.sql.Connection;

/*
 * @author G.Calli.
 */

public interface DocumentoDAO {

    public DocumentoVO getDocumento(int id) throws DataException;

    public DocumentoVO getDocumento(Connection connection, int id)
            throws DataException;

    public DocumentoVO newDocumentoVO(Connection connection,
            DocumentoVO documento) throws DataException;

    public void writeDocumentToStream(int docId, OutputStream os)
            throws DataException;
}