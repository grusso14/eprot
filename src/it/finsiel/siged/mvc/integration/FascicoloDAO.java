package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.presentation.helper.FascicoloView;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioFascicoliDestinatariVO;
import it.finsiel.siged.mvc.vo.protocollo.InvioFascicoliVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloFascicoloVO;

import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

/*
 * @author G.Calli.
 */

public interface FascicoloDAO {
    public FascicoloVO newFascicolo(Connection connection, FascicoloVO fascicolo)
            throws DataException;

    public FascicoloVO getFascicoloById(Connection connection, int id)
            throws DataException;

    public FascicoloVO getFascicoloById(int id) throws DataException;

    public Collection getFascicoloByAooId(int id) throws DataException;

    public void salvaDocumentoFascicolo(Connection connection,
            FascicoloVO fascicolo, int documentoId, String utente)
            throws DataException;

    public void salvaProtocolloFascicolo(Connection connection,
            int fascicoloId, int protocolloId, String utente, int versione)
            throws DataException;

    public void salvaProcedimentoFascicolo(Connection connection,
            int fascicoloId, int procedimentoId, String utente, int versione)
            throws DataException;

    public FascicoloVO aggiornaFascicolo(FascicoloVO fascicolo)
            throws DataException;

    public FascicoloVO aggiornaFascicolo(Connection connection,
            FascicoloVO fascicolo) throws DataException;

    public int deleteFascicolo(int fascicoloId) throws DataException;

    public void deleteDocumentoFascicolo(Connection conn, int fascicoloId,
            int documentoId, int versione) throws DataException;

    public void deleteFascicoliProtocollo(Connection conn, int protocolloId)
            throws DataException;

    public void deleteFascicoliDocumento(Connection conn, int documentoId)
            throws DataException;

    public Collection getStatiFascicolo(String statiSelezionati)
            throws DataException;

    public Collection getFascicoliByProtocolloId(Connection connection,
            int protocolloId) throws DataException;

    public Collection getFascicoliByProtocolloId(int protocolloId)
            throws DataException;

    public Collection getFascicoliByDocumentoId(Connection connection,
            int protocolloId) throws DataException;

    public Collection getStoriaFascicoliByDocumentoId(Connection connection,
            int documentoId, int versione) throws DataException;

    public Collection getFascicoliByDocumentoId(int protocolloId)
            throws DataException;

    public Collection getProtocolliFascicolo(int fascicoloId, Utente utente)
            throws DataException;

    public Collection getProtocolliFascicoloById(int fascicoloId)
            throws DataException;

    public Collection getDocumentiFascicoloById(int fascicoloId)
            throws DataException;

    public void deleteDocumentoFascicolo(int fascicoloId, int documentoId,
            int versione) throws DataException;

    public void annullaInvioFascicolo(Connection conn, int fascicoloId,
            int versione) throws DataException;

    public void annullaInvioFascicolo(int fascicoloId, int versione)
            throws DataException;

    public void salvaDestinatariInvioFascicolo(Connection connection,
            InvioFascicoliDestinatariVO ifdVO) throws DataException;

    public void salvaDocumentiInvioFascicolo(Connection connection,
            InvioFascicoliVO ifVO) throws DataException;

    public int aggiornaStatoFascicolo(Connection connection, int fascicoloId,
            int stato, String userName, int versione) throws DataException;

    public int scartaFascicolo(Connection connection, int fascicoloId,
            String destinazioneScarto, String userName, int versione)
            throws DataException;

    public SortedMap getFascicoliArchivioInvio(int aooId) throws DataException;

    public Collection getDocumentiFascicoliInvio(int fascicoloId)
            throws DataException;

    public Map getDestinatariFascicoliInvio(Connection connection,
            int fascicoloId) throws DataException;

    public Map getDestinatariFascicoliInvio(int fascicoloId)
            throws DataException;

    public int eliminaCodaInvioFascicolo(Connection connection, int fascicoloId)
            throws DataException;

    public boolean esisteFascicoloInCodaInvio(int fascicoloId)
            throws DataException;

    public int getMaxProgrFascicolo(Connection connection, int aooId, int anno)
            throws DataException;

    public FascicoloView getFascicoloViewById(int id) throws DataException;

    public FascicoloView getFascicoloViewById(Connection connection, int id)
            throws DataException;

    public Collection getFaldoniFascicoloById(int fascicoloId)
            throws DataException;

    public Collection getProcedimentiFascicoloById(int fascicoloId)
            throws DataException;

    public void deleteFascicoloProtocollo(Connection conn, int protocolloId,
            int fascicoloId) throws DataException;

    public void deleteFascicoloProcedimento(Connection conn,
            int procedimentoId, int fascicoloId) throws DataException;

    // STORIA DEL FASCICOLO
    public Collection getStoriaFascicolo(int fascicoloId) throws DataException;

    public Collection getFaldoniFascicoloByIdVersione(int fascicoloId,
            int versione) throws DataException;

    public Collection getProcedimentiFascicoloByIdVersione(int fascicoloId,
            int versione) throws DataException;

    public Collection getDocumentiFascicoloByIdVersione(int fascicoloId,
            int versione) throws DataException;

    public Collection getProtocolliFascicoloByIdVersione(int fascicoloId,
            int versione) throws DataException;

    public FascicoloVO getFascicoloByIdVersione(int fascicoloId, int versione)
            throws DataException;

    public void salvaProtocolloFascicolo(Connection connection,
            ProtocolloFascicoloVO protocollofascicoloVO) throws DataException;

    public FascicoloVO getFascicoloByProgressivo(Connection connection,
            int anno, int progressivo) throws DataException;

    public FascicoloVO newFascicoloStoria(Connection connection,
            FascicoloVO fascicolo) throws DataException;

    public Collection getFascicoliAnnoNumero(Connection connection)
            throws DataException;

    public Collection getFascicoli(Utente utente, int progressivo, int anno,
            String oggetto, String note, String stato, int titolarioId,
            java.util.Date dataAperturaDa, java.util.Date dataAperturaA,
            Date dataEvidenzaDa, Date dataEvidenzaA, int ufficioId)
            throws DataException;

    public int contaFascicoli(Utente utente, int progressivo, int anno,
            String oggetto, String note, String stato, int titolarioId,
            Date dataAperturaDa, Date dataAperturaA, Date dataEvidenzaDa,
            Date dataEvidenzaA, int ufficioId) throws DataException;

    public boolean isUtenteAbilitatoView(Utente utente, Ufficio uff,
            int fascicoloId) throws DataException;
}
