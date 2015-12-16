/*
 * Created on 14-mar-2005
 *
 */
package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.MessaggioEmailEntrata;
import it.finsiel.siged.mvc.vo.posta.CodaInvioVO;
import it.finsiel.siged.mvc.vo.posta.EmailVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Almaviva sud
 * 
 */
public interface EmailDAO {

    public void salvaMessaggioPerInvio(Connection connection, int id,
            int aooId, int protocolloId, Collection destinatari)
            throws DataException;

    public Collection getListaMessaggiUscita(int aooId) throws DataException;

    public Collection getListaLog(int aooId, int tipoLog) throws DataException;

    public Collection getDestinatariMessaggioUscita(int msgId)
            throws DataException;

    public CodaInvioVO getMessaggioDaInviare(int id) throws DataException;

    public void segnaMessaggioComeInviato(int msgId) throws DataException;

    public void salvaAllegato(Connection connection, DocumentoVO documento,
            int email_id) throws DataException;

    public void salvaEmail(EmailVO email, Connection connection)
            throws DataException;

    public void salvaEmailLog(MessaggioEmailEntrata email, Connection connection)
            throws DataException;

    public void salvaEmailLog(int id, String oggetto, String tipo,
            String errore, Connection connection, int tipoLog, int aooId)
            throws DataException;

    // ----------------- //
    public EmailVO getEmailEntrata(int emailId) throws DataException;

    public EmailVO getEmailEntrata(Connection connection, int emailId)
            throws DataException;

    public ArrayList getAllegatiEmailEntrata(Connection connection, int emailId)
            throws DataException;

    public void writeDocumentoToStream(Connection connection, int docId,
            OutputStream os) throws DataException;

    public void writeDocumentoToStream(int docId, OutputStream os)
            throws DataException;

    public void aggiornaStatoEmailIngresso(Connection connection,
            int messaggioId, int stato) throws DataException;

    // -------------------------- //
    public Collection getMessaggiDaProtocollare(Connection connection, int aooId)
            throws DataException;

    public Collection getMessaggiDaProtocollare(int aooId) throws DataException;

    public void eliminaEmail(Connection connection, int emailId)
            throws DataException;

    public void eliminaEmailLog(Connection connection, String[] ids)
            throws DataException;
}