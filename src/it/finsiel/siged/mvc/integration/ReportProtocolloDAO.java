package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;

import java.util.Collection;
import java.util.Date;

/*
 * @author G.Calli.
 */

public interface ReportProtocolloDAO {

    public int countStampaRegistroReport(int registroId, String tipoProtocollo,
            Date dataInizio, Date dataFine, int ufficioId) throws DataException;

    public Collection getRegistroReport(int registroId, String tipoprotocollo,
            Date dataInizio, Date dataFine, int ufficioId) throws DataException;

    public Collection getProtocolliScaricati(int registroId, Date dataInizio,
            Date dataFine, UfficioVO ufficio, int assegnatario)
            throws DataException;

    public Collection getProtocolliInLavorazione(int registroId,
            Date dataInizio, Date dataFine, UfficioVO ufficio, int assegnatario)
            throws DataException;

    public int countProtocolliInLavorazione(int registroId, Date dataInizio,
            Date dataFine, UfficioVO ufficio, int assegnatario)
            throws DataException;

    public int countProtocolliDaScartare(Utente utente, int ufficioId,
            java.util.Date dataInizio, java.util.Date dataFine)
            throws DataException;

    public Collection cercaProtocolliDaScartare(Utente utente, int ufficioId,
            java.util.Date dataInizio, java.util.Date dataFine)
            throws DataException;

    public int countProtocolliScaricati(int registroId, Date dataInizio,
            Date dataFine, UfficioVO ufficio, int assegnatario)
            throws DataException;

    public Collection getProtocolliAssegnati(int registroId, UfficioVO ufficio,
            int assegnatario) throws DataException;

    public int countProtocolliAssegnati(int registroId, UfficioVO ufficio,
            int assegnatario) throws DataException;

    public Collection getProtocolliAnnullati(int registroId, UfficioVO ufficio,
            int ufficioId) throws DataException;

    public int countProtocolliAnnullati(int registroId, UfficioVO ufficio,
            int ufficioId) throws DataException;

    public Collection getProtocolliSpediti(int registroId, Date dataInizio,
            Date dataFine, int ufficioId, String mezzoSpedizione, int mezzoSpedizioneId)
            throws DataException;

    public int countProtocolliSpediti(int registroId, Date dataInizio,
            Date dataFine, int ufficioId, String mezzoSpedizione, int mezzoSpedizioneId)
            throws DataException;

    public Collection getRubrica(String flagTipo, int aooId)
            throws DataException;

    public int countRubrica(String tipoPersona, int aooId) throws DataException;

    public int getNumeroProtocolli(int ufficioAssegnatarioId,
            Integer utenteAssegnatario, String statoProtocollo,
            String statoAssegnazione, Date dataDa, Date dataA, Utente utente)
            throws DataException;

    public Collection getDettaglioStatisticheProtocolli(
            int ufficioAssegnatarioId, Integer utenteAssegnatario,
            String statoProtocollo, String statoAssegnazione, Date dataDa,
            Date dataA, Utente utente) throws DataException;
}