package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.protocollo.ProtocolloVO;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

/*
 * @author G.Calli.
 */

public interface StoriaProtocolloDAO {
    public Collection getStoriaProtocollo(Utente utente, int protocolloId)
            throws DataException;

    public Collection getStoriaProtocollo(Utente utente, int protocolloId,
            String flagTipo) throws DataException;

    public Collection getScartoProtocollo(Utente utente, int protocolloId)
            throws DataException;

    public ProtocolloVO getVersioneProtocollo(int id, int versione)
            throws DataException;

    public Map getAllegatiVersioneProtocollo(int protocolloId, int versione)
            throws DataException;

    public Collection getAssegnatariVersioneProtocollo(int protocolloId,
            int versione) throws DataException;

    public Collection getAllacciVersioneProtocollo(int protocolloId,
            int versione) throws DataException;

    public Map getDestinatariVersioneProtocollo(int protocolloId, int versione)
            throws DataException;

    public Collection getVersioneProcedimentiProtocollo(int protocolloId,
            int versione) throws DataException;

    public Collection getAnniScartabili(int registroId) throws DataException;

    public int getNumProcolliNonScartabili(int registroId, int anno)
            throws DataException;

    public int scarto(Utente utente, int anno) throws DataException;

    public SortedMap cercaScartoProtocolli(Utente utente, String tipo,
            String mozione, String stato, String riservato, Date dataRegDa,
            Date dataRegA, int NumeroDa, int NumeroA, int AnnoDa, int AnnoA)
            throws DataException;

    public boolean isScartato(int protocolloId) throws DataException;

    public SortedMap cercaProtocolliDaScartare(Utente utente, int ufficio,
            int servizio, Date dataRegDa, Date dataRegA) throws DataException;
}