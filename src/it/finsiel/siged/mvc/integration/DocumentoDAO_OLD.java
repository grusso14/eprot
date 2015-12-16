package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;

/*
 * @author G.Calli.
 */

public interface DocumentoDAO_OLD {

    public DocumentoVO getDocumento(int id) throws DataException;

    public DocumentoVO getDocumento(Connection connection, int id)
            throws DataException;

    public DocumentoVO newDocumentoVO(DocumentoVO documento)
            throws DataException;

    public DocumentoVO newDocumentoVO(Connection connection,
            DocumentoVO documento) throws DataException;

    public void updateDocumentoPrincipaleId(Connection connection,
            int protocolloId, Integer documentoId) throws DataException;

    public HashMap getDocumentiAllegatiProtocollo(int idProtocollo)
            throws DataException;

    public HashMap getDocumentiProtocollo(int idProtocollo)
            throws DataException;

    public void writeDocumentToStream(int docId, OutputStream os)
            throws DataException;

    public void deleteDocuments(Connection connection, Collection docIds)
            throws DataException;

}