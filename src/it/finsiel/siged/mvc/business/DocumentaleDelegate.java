package it.finsiel.siged.mvc.business;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.NomiTabelle;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.DAOFactory;
import it.finsiel.siged.exception.CannotDeleteException;
import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.InvioClassificati;
import it.finsiel.siged.model.documentale.Documento;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.integration.DocumentaleDAO;
import it.finsiel.siged.mvc.vo.documentale.CartellaVO;
import it.finsiel.siged.mvc.vo.documentale.FileVO;
import it.finsiel.siged.mvc.vo.documentale.PermessoFileVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioClassificatiDestinatariVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioClassificatiVO;
import it.finsiel.siged.rdb.JDBCManager;
import it.finsiel.siged.util.StringUtil;

import java.io.File;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;

public class DocumentaleDelegate implements ComponentStatus {

    private static Logger logger = Logger.getLogger(DocumentaleDelegate.class
            .getName());

    private int status;

    private DocumentaleDAO docDAO = null;

    private ServletConfig config = null;

    private static DocumentaleDelegate delegate = null;

    private DocumentaleDelegate() {
        try {
            if (docDAO == null) {
                docDAO = (DocumentaleDAO) DAOFactory
                        .getDAO(Constants.DOCUMENTALE_DAO_CLASS);
                logger.debug("UserDAO instantiated:"
                        + Constants.DOCUMENTALE_DAO_CLASS);
                status = STATUS_OK;
            }
        } catch (Exception e) {
            status = STATUS_ERROR;
            logger.error("", e);
        }

    }

    public static DocumentaleDelegate getInstance() {
        if (delegate == null)
            delegate = new DocumentaleDelegate();
        return delegate;
    }

    public static String getIdentifier() {
        return Constants.DOCUMENTALE_DELEGATE;
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

    /*
     * Ritorna un oggetto di tipo CartellaVO letto dalla base dati cha ha come
     * id univoco l'id passato per argomento. L'oggetto restituito non è mai
     * nullo per convenzione, consultare il valore del metodo getReturnValue()
     * per capire se è stato trovato o meno.
     * 
     * @see it.finsiel.siged.constant.ReturnValues
     * 
     * Genera un'eccezione se l'operazione di lettura non viene effettuata.
     */
    private CartellaVO getCartella(Connection connection, int cartellaId) {
        CartellaVO c = null;
        try {
            c = docDAO.getCartellaVO(connection, cartellaId);
            logger.info("getting cartella id: " + c.getId());
        } catch (DataException de) {
            logger.error("Failed getting CertellaVO: " + cartellaId);
        }
        return c;
    }

    public CartellaVO getCartellaVO(int cId) throws DataException {
        return docDAO.getCartellaVO(cId);
    }

    /*
     * Aggiunge una cartella nell'albero del documentale. La cartella avrà come
     * nome il "nome utente", parentId il parent id della cartella padre (del
     * documentale), ufficioId l'ufficio id corrispondente nella struttura
     * dell'aoo, ed infine l'aooId dell'aoo di appartenenza.
     * 
     * Restituisce sempre una cartellaVO quindi bisogna testare il valore di
     * ritorno per capire se il salvataggio è avvenuto.
     * 
     * Genera un'eccezione solo nel caso in cui vi è un errore della base dati.
     */
    public CartellaVO creaCartellaUtente(Connection connection, CartellaVO c)
            throws DataException {
        CartellaVO verificaCartella = docDAO.getCartellaVOByUfficioUtenteNome(
                connection, c.getUfficioId(), c.getUtenteId(), c.getNome()
                        .toUpperCase().trim(), c.getId());
        if (verificaCartella.getReturnValue() == ReturnValues.FOUND) {
            return verificaCartella;
        } else {
            c.setId(IdentificativiDelegate.getInstance().getNextId(connection,
                    NomiTabelle.DOC_CARTELLE));
            return docDAO.newCartellaVO(connection, c);
        }
    }

    public CartellaVO creaCartellaUtente(CartellaVO c) throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        CartellaVO nuova = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            nuova = creaCartellaUtente(connection, c);
            connection.commit();
        } catch (Exception de) {
            jdbcMan.rollback(connection);
            logger.error("Errore nella creazione della cartella  "
                    + c.getNome(), de);
            throw new DataException(de.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return nuova;
    }

    public int cancellaCartella(int dc_id) throws DataException,
            CannotDeleteException {
        return docDAO.deleteCartella(dc_id);
    }

    public void eliminaDocumento(int dfaId, int dfrId) throws DataException {
        docDAO.deleteFile(dfaId, dfrId);
    }

    public void eliminaDocumento(Connection connection, int dfaId, int dfrId)
            throws DataException {
        docDAO.deleteFile(connection, dfaId, dfrId);
    }

    /*
     * Elimina i record che servono a generare l'albero dell'utente per
     * l'ufficio a cui ha accesso nell'aoo di appertenenza.
     */
    public void cancellaAlberoUtentePerUfficio(Connection connection,
            int utenteId, int ufficioId, int aooId) throws DataException {
        docDAO.cancellaAlberoUtentePerUfficio(connection, utenteId, ufficioId,
                aooId);
    }

    // note: aooId non necessario in quanto ufficioId è univoco per la base
    // dati.
    public CartellaVO getCartellaVOByUfficioUtenteId(int ufficioId, int utenteId)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        CartellaVO c = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            c = docDAO.getCartellaVOByUfficioUtenteId(connection, ufficioId,
                    utenteId);
        } catch (Exception de) {
            logger.error("Impossibile leggere la cartella per l'ufficio: "
                    + ufficioId, de);
            throw new DataException(de.getMessage());
        } finally {
            jdbcMan.close(connection);
        }
        return c;
    }

    public CartellaVO getCartellaVOByUfficioUtenteId(Connection connection,
            int ufficioId, int utenteId) throws DataException {
        CartellaVO c = null;
        try {
            c = docDAO.getCartellaVOByUfficioUtenteId(connection, ufficioId,
                    utenteId);
        } catch (Exception de) {
            logger.error("Impossibile leggere la cartella per l'ufficio: "
                    + ufficioId, de);
            throw new DataException(de.getMessage());
        }
        return c;
    }

    public Collection getSottoCartelle(int cartellaId) throws DataException {
        return docDAO.getSottoCartelle(cartellaId);
    }

    /*
     * Restituisce un HashMap ( K=Integer (fileId), V=FileVO (file) ) con tutti
     * i file nella cartella con id <cartellaId> secondo
     */
    public HashMap getFiles(int cartellaId) throws DataException {
        return docDAO.getFiles(cartellaId);
    }

    public HashMap getFilesLista(int cartellaId) throws DataException {
        return docDAO.getFilesLista(cartellaId);
    }

    /*
     * Restituisce un HashMap ( K=Integer (fileId), V=FileVO (file) ) con tutti
     * i file nella cartella con id <cartellaId> secondo
     */
    public HashMap getFileCondivisi(String ufficiIds, int utenteId)
            throws DataException {
        return docDAO.getFileCondivisi(ufficiIds, utenteId);
    }

    public HashMap getFileCondivisiC(String ufficiIds, int utenteId)
            throws DataException {
        return docDAO.getFileCondivisiC(ufficiIds, utenteId);
    }

    public Documento aggiornaDocumento(Documento documento, Utente utente,
            boolean ripristino) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        Documento doc = null;

        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            doc = aggiornaDocumento(connection, documento, utente, ripristino);
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "Salvataggio Documento fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "Salvataggio Documento fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return doc;

    }

    public CartellaVO updateNomeCartellaVO(CartellaVO cartella)
            throws DataException {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        CartellaVO c = new CartellaVO();
        try {
            jdbcMan = new JDBCManager();

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            c = updateNomeCartellaVO(connection, cartella);
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "updateNomeCartellaVO fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "updateNomeCartellaVO fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return c;
    }

    public CartellaVO updateNomeCartellaVO(Connection connection,
            CartellaVO cartella) throws DataException {
        String nomeCartellaUpdate = cartella.getNome();
        cartella = docDAO
                .getCartellaVO(connection, cartella.getId().intValue());
        cartella.setNome(nomeCartellaUpdate);
        CartellaVO verificaCartella = docDAO.getCartellaVOByUfficioUtenteNome(
                connection, cartella.getUfficioId(), cartella.getUtenteId(),
                nomeCartellaUpdate.toUpperCase().trim(), cartella.getId());
        if (verificaCartella.getReturnValue() == ReturnValues.FOUND)
            return verificaCartella;
        else
            return docDAO.updateNomeCartellaVO(connection, cartella);
    }

    public Documento aggiornaDocumento(Connection connection,
            Documento documento, Utente utente, boolean ripristino)
            throws Exception {

        FileVO fileVO = documento.getFileVO();

        if (documento.getFileVO().getDocumentoVO().getId() == null
                || documento.getFileVO().getDocumentoVO().getId().intValue() == 0
                || documento.getFileVO().getDocumentoVO().isMustCreateNew()) {
            int fileRepId = IdentificativiDelegate.getInstance().getNextId(
                    connection, NomiTabelle.DOCUMENTALE_DOCUMENTI);
            fileVO.getDocumentoVO().setId(fileRepId);
            fileVO.setRepositoryFileId(fileRepId);
            documento.getFileVO().setDocumentoVO(
                    salvaFile(connection, fileVO.getDocumentoVO()));
        }
        FileVO fileSalvato = docDAO
                .updateFileVO(connection, fileVO, ripristino);

        docDAO.cancellaPermessi(connection, fileVO.getId().intValue());
        // salva permessi
        Collection permessi = documento.getPermessi();
        if (permessi != null) {
            for (Iterator i = permessi.iterator(); i.hasNext();) {
                PermessoFileVO permesso = (PermessoFileVO) i.next();
                permesso
                        .setId(IdentificativiDelegate.getInstance().getNextId(
                                connection,
                                NomiTabelle.DOCUMENTALE_PERMESSI_DOCUMENTO));
                permesso.setFileAttributeId(fileVO.getId().intValue());
                docDAO.salvaPermesso(connection, permesso, fileSalvato
                        .getVersione());
            }
        }
        // salvo i dati dei fascicoli
        fileVO.setVersione(fileSalvato.getVersione());
        salvaFascicoli(connection, fileVO, utente);
        // eliminiamo il file temporaneo del
        // documento principale
        if (fileSalvato.getReturnValue() == ReturnValues.SAVED) {
            DocumentoVO doc = (DocumentoVO) documento.getFileVO()
                    .getDocumentoVO();
            if (doc != null && doc.getPath() != null) {
                File f = new File(doc.getPath());
                f.delete();
            }
            documento.setFileVO(fileSalvato);
        }
        return documento;
    }

    private Documento salvaDocumento(Connection connection,
            Documento documento, Utente utente) throws Exception {

        // RegistroVO registro = utente.getRegistroVOInUso();
        FileVO fileVO = documento.getFileVO();

        fileVO.setId(IdentificativiDelegate.getInstance().getNextId(connection,
                NomiTabelle.DOCUMENTALE));
        int fileId = IdentificativiDelegate.getInstance().getNextId(connection,
                NomiTabelle.DOCUMENTALE_DOCUMENTI);

        fileVO.getDocumentoVO().setId(fileId);
        fileVO.setRepositoryFileId(fileId);
        documento.getFileVO().setRowCreatedUser(
                utente.getValueObject().getCognome()
                        + " "
                        + StringUtil.getStringa(utente.getValueObject()
                                .getNome()));

        //		 		
        documento.getFileVO().setDocumentoVO(
                salvaFile(connection, fileVO.getDocumentoVO()));

        FileVO fileSalvato = docDAO.newFileVO(connection, fileVO);
        Integer documentoId = fileSalvato.getId();
        int versione = fileSalvato.getVersione();
        // salvo il documento principale

        // salva permessi
        Collection permessi = documento.getPermessi();
        if (permessi != null) {
            for (Iterator i = permessi.iterator(); i.hasNext();) {
                PermessoFileVO permesso = (PermessoFileVO) i.next();
                permesso
                        .setId(IdentificativiDelegate.getInstance().getNextId(
                                connection,
                                NomiTabelle.DOCUMENTALE_PERMESSI_DOCUMENTO));
                permesso.setFileAttributeId(documentoId.intValue());
                docDAO.salvaPermesso(connection, permesso, versione);
            }
        }
        // salvo i dati dei fascicoli
        salvaFascicoli(connection, fileVO, utente);
        // se il protocollo è stato salvato eliminiamo il file temporaneo del
        // documento principale
        if (fileSalvato.getReturnValue() == ReturnValues.SAVED) {
            DocumentoVO doc = (DocumentoVO) documento.getFileVO()
                    .getDocumentoVO();
            if (doc != null && doc.getPath() != null) {
                File f = new File(doc.getPath());
                f.delete();
            }
            documento.setFileVO(fileSalvato);
        }

        return documento;
    }

    private DocumentoVO salvaFile(Connection connection, DocumentoVO documento)
            throws Exception {
        if (documento != null && documento.getPath() != null
                && documento.getSize() > 0) {
            documento = docDAO.newDocumentoVO(connection, documento);
        }
        return documento;
    }

    public int classificaDocumento(int dfrId, int titolarioId)
            throws DataException {
        int recUpdate = 0;
        try {
            recUpdate = docDAO.classificaDocumento(dfrId, titolarioId);
        } catch (DataException e) {
            logger.error("", e);
            throw new DataException("Errore classificaDocumento.");
        }
        return recUpdate;
    }

    private void salvaFascicoli(Connection connection, FileVO documento,
            Utente utente) throws Exception {
        int documentoId = documento.getId().intValue();
        FascicoloDelegate fd = FascicoloDelegate.getInstance();
        fd.rimuoviFascicoliDocumento(connection, documentoId);
        for (Iterator i = documento.getFascicoli().iterator(); i.hasNext();) {
            FascicoloVO fVO = (FascicoloVO) i.next();
            //fVO.setVersione(documento.getVersione());
            fd.salvaFascicoloDocumento(connection, fVO, documentoId, utente
                    .getValueObject().getUsername());

        }
    }

    public boolean spostaInLavorazione(int docId) throws DataException {
        try {
            return docDAO.spostaInLavorazione(docId);
        } catch (DataException e) {
            logger.error("", e);
            throw new DataException("Errore classificaDocumento.");
        }
    }

    public boolean spostaDocumento(int cartellaDestinazioneId, int dfaId)
            throws DataException {
        return docDAO.spostaDocumento(cartellaDestinazioneId, dfaId);
    }

    public void checkinDocumento(int docId) throws DataException {
        docDAO.checkinDocumento(docId);
    }

    public int checkoutDocumento(int docId, int utenteId) throws DataException {
        try {
            return docDAO.checkoutDocumento(docId, utenteId);
        } catch (DataException e) {
            logger.error("", e);
            throw new DataException("Errore checkoutDocumento.");
        }
    }

    public Collection getVersioniDocumento(int dfaId) throws DataException {
        return docDAO.getVersioniDocumento(dfaId);
    }

    /*
     * Questo metodo deve essere sincrono. Sincronizzare questo metodo per
     * eveitare "buchi" nei numeri di protocollo Errore: Due transazioni che
     * iniziano allo stesso tempo, se una fallisce vi sar? un buco nei numeri
     * progressivi del protocollo, dovrebbe fallire la transazione che per prima
     * ha ottenuto l'id.
     */

    public synchronized Documento salvaDocumento(Documento documento,
            Utente utente) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        Documento doc = null;

        try {
            jdbcMan = new JDBCManager();

            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);
            doc = salvaDocumento(connection, documento, utente);
            connection.commit();

        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "Salvataggio Documento fallito, rolling back transction..",
                    de);

        } catch (SQLException se) {
            jdbcMan.rollback(connection);
            logger.warn(
                    "Salvataggio Documento fallito, rolling back transction..",
                    se);

        } catch (Exception e) {
            jdbcMan.rollback(connection);
            logger.warn("Si e' verificata un eccezione non gestita.", e);

        } finally {
            jdbcMan.close(connection);
        }
        return doc;

    }

    public Documento getDocumentoById(int id) throws DataException {
        Documento doc = new Documento();
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            doc.setFileVO(docDAO.getFileVO(connection, id));
            doc.getFileVO().setDocumentoVO(
                    docDAO.getDocumento(connection, doc.getFileVO()
                            .getRepositoryFileId()));
            doc.aggiungiPermessi(docDAO.getPermessiDocumento(connection, id));
            setFascicoliDocumento(connection, doc, id);
            doc.setVersioneDefault(true);
            return doc;
        } catch (Exception de) {
            logger.error("DocumentaleDelegate: failed getDocumentoById: ");
            throw new DataException("Impossibile leggere dalla base dati");
        } finally {
            jdbcMan.close(connection);
        }
    }

    public Documento getDocumentoStoriaById(int id, int versione)
            throws DataException {
        Documento doc = new Documento();
        JDBCManager jdbcMan = null;
        Connection connection = null;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            doc.setFileVO(docDAO.getStoriaFileVO(connection, id, versione));
            doc.getFileVO().setDocumentoVO(
                    docDAO.getDocumento(connection, doc.getFileVO()
                            .getRepositoryFileId()));
            doc.aggiungiPermessi(docDAO.getStoriaPermessiDocumento(connection,
                    id, versione));
            setStoriaFascicoliDocumento(connection, doc, id, versione);
            doc.setVersioneDefault(false);
            return doc;
        } catch (Exception de) {
            logger.error("DocumentaleDelegate: failed getDocumentoById: ");
            throw new DataException("Impossibile leggere dalla base dati");
        } finally {
            jdbcMan.close(connection);
        }
    }

    public Collection getPermessiDocumento(int documentoId) {
        try {
            return docDAO.getPermessiDocumento(documentoId);
        } catch (DataException de) {
            logger
                    .error("DocumentaleDelegate: failed getting getPermessidocumento: ");
            return null;
        }
    }

    public void writeDocumentToStream(int docId, OutputStream os)
            throws DataException {
        docDAO.writeFileToStream(docId, os);
    }

    public SortedMap cercaDocumenti(Utente utente, HashMap sqlDB, String testo) {
        try {

            return docDAO.cercaDocumenti(utente, sqlDB, testo);
        } catch (DataException de) {
            logger
                    .error("DocumentaleDelegate: failed getting cercaDocumenti: ");
            return null;
        }

    }

    public int contaDocumenti(Utente utente, HashMap sqlDB, String testo) {
        try {

            return docDAO.contaDocumenti(utente, sqlDB, testo);
        } catch (DataException de) {
            logger
                    .error("ProtocolloDelegate: failed getting contaProtocolli: ");
            return 0;
        }

    }

    private void setFascicoliDocumento(Documento d, int documentoId) {
        d.getFileVO().setFascicoli(
                FascicoloDelegate.getInstance().getFascicoliByDocumentoId(
                        documentoId));
    }

    private void setFascicoliDocumento(Connection connection, Documento d,
            int documentoId) throws DataException {
        d.getFileVO().setFascicoli(
                FascicoloDelegate.getInstance().getFascicoliByDocumentoId(
                        connection, documentoId));
    }

    private void setStoriaFascicoliDocumento(Connection connection,
            Documento d, int documentoId, int versione) throws DataException {
        d.getFileVO().setFascicoli(
                FascicoloDelegate.getInstance()
                        .getStoriaFascicoliByDocumentoId(connection,
                                documentoId, versione));
    }

    public boolean hasAccessToFolder(int cartellaId, int utenteId)
            throws DataException {
        return docDAO.hasAccessToFolder(cartellaId, utenteId);
    }

    public boolean isOwnerDocumento(int documentoId, int utenteId)
            throws DataException {
        return docDAO.isOwnerDocumento(documentoId, utenteId);
    }

    public int getTipoPermessoSuDocumento(int documentoId, int utenteId,
            String ufficiIds) throws DataException {
        return docDAO.getTipoPermessoSuDocumento(documentoId, utenteId,
                ufficiIds);
    }

    public Map getDestinatariDocumentiInvio(Connection connection,
            int fascicoloId) {
        try {
            return docDAO.getDestinatariDocumentiInvio(connection, fascicoloId);
        } catch (Exception de) {
            logger
                    .error("DocumentaleDelegate: failed getting getDestinatariFascicoliInvio: ");
            return null;
        }
    }

    public Map getDestinatariDocumentiInvio(int fascicoloId) {
        try {
            return docDAO.getDestinatariDocumentiInvio(fascicoloId);
        } catch (Exception de) {
            logger
                    .error("DocumentaleDelegate: failed getting getDestinatariDocumentiInvio: ");
            return null;
        }
    }

    public Collection getDocumentiArchivioInvio(int aooId) {
        try {

            return docDAO.getDocumentiArchivioInvio(aooId);
        } catch (DataException de) {
            logger
                    .error("DocumentaleDelegate: failed getting getDocumentiArchivioInvio: ");
            return null;
        }

    }

    public Map getDestinatariDocumentoInvio(int documentoId) {
        try {
            return docDAO.getDestinatariDocumentiInvio(documentoId);
        } catch (Exception de) {
            logger
                    .error("DocumentaleDelegate: failed getting getDestinatariDocumentoInvio: ");
            return null;
        }
    }

    public InvioClassificati getDocumentoClassificatoById(int documentoId) {
        try {
            InvioClassificati fInviato = new InvioClassificati();
            fInviato.setIcVO(docDAO.getInvioClassificatiVO(documentoId));

            // destinatari fascicolo
            Map destinatari = getDestinatariDocumentoInvio(documentoId);
            fInviato.setDestinatari(destinatari);

            return fInviato;
        } catch (Exception de) {
            logger
                    .error("DocumentaleDelegate: failed getting getDocumentoClassificatoById: ");
            return null;
        }
    }

    public int eliminaCodaInvioDocumento(Connection connection,
            int documentoId, String stato) throws DataException {
        int recUpdate = 0;
        try {
            recUpdate = docDAO.eliminaCodaInvioDocumento(connection,
                    documentoId);
        } catch (DataException e) {
            e.printStackTrace();
            throw new DataException("Errore eliminaCodaInvioDocumento.");
        }
        return recUpdate;
    }

    public int invioClassificati(InvioClassificati invioClassificati) {
        JDBCManager jdbcMan = null;
        Connection connection = null;
        int statusFlag = 0;
        try {
            jdbcMan = new JDBCManager();
            connection = jdbcMan.getConnection();
            connection.setAutoCommit(false);

            InvioClassificatiVO icVO = invioClassificati.getIcVO();
            icVO.setId(IdentificativiDelegate.getInstance().getNextId(
                    connection, NomiTabelle.INVIO_CLASSIFICATI));
            docDAO.salvaInvioClassificati(connection, icVO);
            for (Iterator y = invioClassificati.getDestinatariCollection()
                    .iterator(); y.hasNext();) {
                InvioClassificatiDestinatariVO icdVO = new InvioClassificatiDestinatariVO();
                DestinatarioVO destinatario = (DestinatarioVO) y.next();
                icdVO.setDocumentoId(icVO.getDocumentoId());
                icdVO
                        .setId(IdentificativiDelegate.getInstance().getNextId(
                                connection,
                                NomiTabelle.INVIO_CLASSIFICATI_DESTINATARI));
                icdVO.setDestinatario(destinatario);
                docDAO.salvaDestinatariInvioClassificati(connection, icdVO);
            }

            statusFlag = docDAO.invioDocumento(connection, icVO
                    .getDocumentoId());
            connection.commit();
        } catch (DataException de) {
            jdbcMan.rollback(connection);
            logger.error("DocumentaleDelegate: failed invioClassificati: ");
        } catch (SQLException se) {
            jdbcMan.rollback(connection);
        } finally {
            jdbcMan.close(connection);
        }
        return statusFlag;
    }

    public int getFileId(Connection connection, int dfaId) throws DataException {
        return docDAO.getFileId(connection, dfaId);
    }

}