package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.log.LogAcquisizioneMassivaVO;
import it.finsiel.siged.mvc.vo.lookup.ProfiloVO;
import it.finsiel.siged.mvc.vo.lookup.TipoDocumentoVO;
import it.finsiel.siged.mvc.vo.lookup.TipoProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.SpedizioneVO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

// import java.sql.Connection;

/*
 * @author G.Calli.
 */

public interface AmministrazioneDAO {
    public ArrayList getSpedizioni(String descrizioneSpedizione, int aooId)
            throws DataException;

    public SpedizioneVO getMezzoSpedizione(int id) throws DataException;

    public void newMezzoSpedizione(Connection conn, SpedizioneVO spedizioneVO)
            throws DataException;

    public SpedizioneVO aggiornaMezzoSpedizione(Connection conn,
            SpedizioneVO spedizioneVO) throws DataException;

    public boolean cancellaMezzoSpedizione(Connection conn, int spedizioneId)
            throws DataException;

    public ArrayList getFunzioniMenu() throws DataException;
    
    public ArrayList getFunzioniMenuByFunction(int function) throws DataException;

    public ProfiloVO getProfilo(int profiloId) throws DataException;

    public ProfiloVO newProfilo(Connection conn, ProfiloVO profiloVO)
            throws DataException;

    public ArrayList getProfili(int aooId) throws DataException;

    public ProfiloVO updateProfilo(Connection conn, ProfiloVO profiloVO)
            throws DataException;

    public void cancellaProfilo(int profiloId) throws DataException;

    public Collection getMenuByProfilo(int profiloId) throws DataException;

    public TipoDocumentoVO getTipoDocumento(int tipoDocumentoId)
            throws DataException;

    public ArrayList getTipiDocumento(int aooId) throws DataException;

    public TipoDocumentoVO newTipoDocumento(Connection conn,
            TipoDocumentoVO tipoDocumentoVO) throws DataException;

    public TipoDocumentoVO updateTipoDocumento(Connection conn,
            TipoDocumentoVO tipoDocumentoVO) throws DataException;

    public boolean isTipoDocumentoUtilizzato(Connection connection,
            int tipoDocumentoId) throws DataException;

    public boolean cancellaTipoDocumento(Connection conn, int tipoDocumentoId)
            throws DataException;

    public TipoProcedimentoVO getTipoProcedimento(int tipoProcedimentoId)
            throws DataException;

    public ArrayList getTipiProcedimento(int aooId ) throws DataException;
    
    public ArrayList getTipiProcedimento(int aooId, int ufficioCorrente) throws DataException;

    public TipoProcedimentoVO newTipoProcedimento(Connection conn,
            TipoProcedimentoVO tipoProcedimentoVO) throws DataException;

    public TipoProcedimentoVO updateTipoProcedimento(Connection conn,
            TipoProcedimentoVO tipoProcedimentoVO) throws DataException;

    public boolean cancellaTipoProcedimento(Connection conn,
            TipoProcedimentoVO tipoProcedimentoVO) throws DataException;

    public ArrayList getUfficiPerTipoProcedimento(Connection conn,
            int tipoProcedimentoId) throws DataException;

    public boolean isTipoProcedimentoUtilizzato(Connection connection,
            String tipo, int tipoProcedimentoId, int ufficioId)
            throws DataException;

    public boolean cancellaTipoProcedimento(Connection conn,
            int tipoProcedimentoId, int idUfficio) throws DataException;

    public ArrayList getLogsAcquisizioneMassiva(int aooId) throws DataException;

    public boolean cancellaLogsAcquisizioneMassiva(int aooId)
            throws DataException;

    public LogAcquisizioneMassivaVO newLogAcquisizioneMassivaVO(
            Connection conn, LogAcquisizioneMassivaVO logVO)
            throws DataException;

    public Collection getElencoTitoliDestinatario() throws DataException;

    public IdentityVO newTitoloDestinatario(Connection conn, IdentityVO titoloVO)
            throws DataException;

    public IdentityVO getTitoloDestinatario(int id) throws DataException;

    public IdentityVO updateTitolo(Connection conn, IdentityVO titoloVO)
            throws DataException;

    public boolean deleteTitolo(int titoloId) throws DataException;

    public boolean esisteTitolo(String descrizione) throws DataException;

    public IdentityVO getTitoloDestinatarioDaTitolo(String titolo)
            throws DataException;

    public boolean esisteTitoloInProtocolloDestinatari(int id)
            throws DataException;

    public boolean esisteTitoloInStoriaProtDest(int id) throws DataException;

    public boolean esisteTitoloInInvioClassificatiProtDest(int id)
            throws DataException;

    public boolean esisteTitoloInInvioFascicoliDestinatari(int id)
            throws DataException;
    
    public ArrayList getTipiProcedimento(int aooId, String descrizione)
    throws DataException;

  
}