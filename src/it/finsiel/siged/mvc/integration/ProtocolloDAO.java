package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.model.protocollo.ProtocolloIngresso;
import it.finsiel.siged.mvc.presentation.helper.ReportProtocolloView;
import it.finsiel.siged.mvc.vo.protocollo.AllaccioVO;
import it.finsiel.siged.mvc.vo.protocollo.AssegnatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;
import it.finsiel.siged.mvc.vo.protocollo.SegnaturaVO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/*
 * @author G.Calli.
 */

public interface ProtocolloDAO {

    public ProtocolloVO getProtocolloById(int id) throws DataException;

    ProtocolloVO getProtocolloByNumero(Connection connection, int anno,
            int registro, int numProtocollo) throws DataException;

    public ProtocolloVO getProtocolloById(Connection connection, int id)
            throws DataException;

    public ReportProtocolloView getProtocolloView(Connection connection, int id)
            throws DataException;

    public ReportProtocolloView getProtocolloView(int id) throws DataException;

    public int getUltimoProtocollo(int anno, int registro) throws DataException;

    public int getMaxNumProtocollo(Connection connection, int anno, int registro)
            throws DataException;

    public ProtocolloVO newProtocollo(Connection conn, ProtocolloVO protocollo)
            throws DataException;

    public ProtocolloVO newStoriaProtocollo(Connection conn,
            ProtocolloVO protocollo) throws DataException;

    public ProtocolloVO aggiornaProtocollo(Connection connection,
            ProtocolloVO protocollo) throws DataException;

    public Map getAllegatiProtocollo(int protocolloId) throws DataException;

    public String getDocId(int documentoId) throws DataException;

    public Collection getAllacciProtocollo(int protocolloId)
            throws DataException;

    public Collection getAllacciProtocollo(Connection connection,
            int protocolloId) throws DataException;

    public AssegnatarioVO getAssegnatarioPerCompetenza(int protocolloId)
            throws DataException;

    public AssegnatarioVO getAssegnatarioPerCompetenza(Connection connection,
            int protocolloId) throws DataException;

    public Collection getAssegnatariProtocollo(int protocolloId)
            throws DataException;

    public Collection getProtocolliAllacciabili(Utente utente,
            int annoProtocolo, int numeroProtocolloDa, int numeroProtocolloA,
            int protocolloId) throws DataException;

    public void aggiornaDocumentoPrincipaleId(Connection connection,
            int protocolloId, int documentoId) throws DataException;

    public void salvaAllegato(Connection connection,
            int protocollo_allegati_id, int protocolloId, int documentoId,
            int versione) throws DataException;

    public void salvaAllaccio(Connection connection, AllaccioVO allaccio)
            throws DataException;

    public void salvaAssegnatario(Connection connection,
            AssegnatarioVO assegnatario, int versione) throws DataException;

    public void eliminaAllacciProtocollo(Connection connection, int protocolloId)
            throws DataException;

    public void eliminaAllaccioProtocollo(Connection connection,
            int protocolloId, int protocolloAllacciatoId) throws DataException;

    public boolean esisteAllaccio(Connection connection, int protocolloId,
            int protocolloAllacciatoId) throws DataException;

    public void eliminaAssegnatariProtocollo(Connection connection,
            int protocolloId) throws DataException;

    public ProtocolloVO getProtocolloByNumero(int anno, int registro,
            int numProtocollo) throws DataException;

    public Map getProtocolliAssegnati(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date DataA,
            String statoProtocollo, String statoScarico,
            String tipoUtenteUfficio) throws DataException;


    public int presaIncarico(Connection connection, ProtocolloVO protocolloVO,
            String tipoAzione, Utente utente) throws DataException;

    public int updateScarico(Connection connection, ProtocolloVO protocolloVO,
            String flagScarico, Utente utente) throws DataException;

    
    public SortedMap cercaProtocolli(Utente utente, Ufficio ufficio,
            HashMap sqlDB) throws DataException;

    public Collection getMittenti(String mittente) throws DataException;

    public Collection getDestinatari(String destinatario) throws DataException;

    public Map getDestinatariProtocollo(int protocolloId) throws DataException;

    public int annullaProtocollo(Connection connection,
            ProtocolloVO protocolloVO, Utente utente) throws DataException;

    public int salvaSegnatura(Connection connection, SegnaturaVO segnaturaVO)
            throws DataException;

    public void eliminaDestinatariProtocollo(Connection connection,
            int protocolloId) throws DataException;

    public void salvaDestinatario(Connection connection,
            DestinatarioVO destinatario, int versione) throws DataException;

    public Collection getProtocolliByProtMittente(Utente utente,
            String protMittente) throws DataException;

    public Map getProtocolliRespinti(Utente utente, int annoProtocolloDa,
            int annoProtocolloA, int numeroProtocolloDa, int numeroProtocolloA,
            java.util.Date dataDa, java.util.Date dataA) throws DataException;

    public int riassegnaProtocollo(ProtocolloIngresso protocollo, Utente utente)
            throws DataException;

    public int getDocumentoDefault(int aooId) throws DataException;

    public Collection getProtocolliToExport(Connection connLocal, int registroId)
            throws DataException;

    public void updateRegistroEmergenza(Connection connection)
            throws DataException;

    public void updateMsgAssegnatarioCompetenteByIdProtocollo(
            Connection connection, String msgAssegnatarioCompetente,
            int protocolloId) throws DataException;

    public Collection getProcedimentiProtocollo(int protocolloId)
            throws DataException;

    public void inserisciProcedimenti(Connection connection, ArrayList ids)
            throws DataException;

    public void inserisciProcedimenti(Connection connection,
            ProtocolloProcedimentoVO protocolloProcedimentiVO)
            throws DataException;

    public void cancellaProcedimenti(Connection connection, int protocolloId)
            throws DataException;

    public boolean isUtenteAbilitatoView(Utente utente, Ufficio uff,
            int protocolloId) throws DataException;
}