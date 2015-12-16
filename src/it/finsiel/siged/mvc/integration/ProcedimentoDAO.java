package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.lookup.TipoProcedimentoVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoFaldoneVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoFascicoloVO;
import it.finsiel.siged.mvc.vo.protocollo.ProcedimentoVO;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.SortedMap;

public interface ProcedimentoDAO {

    public ProcedimentoVO newProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException;

    public ProcedimentoVO updateProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException;

    public ProcedimentoVO getProcedimentoById(Connection connection,
            int procedimentoId) throws DataException;
    
    public void salvaTipoProcedimento(Connection connection,
            TipoProcedimentoVO tipoProcedimentoVO) throws DataException;

    public ProcedimentoVO getProcedimentoById(int procedimentoId)
            throws DataException;

    public Collection getProcedimentoFaldoni(Connection connection,
            int procedimentoId) throws DataException;

    public Collection getProcedimentoProtocolli(Connection connection,
            int procedimentoId) throws DataException;

    public Collection getProcedimentoFascicoli(Connection connection,
            int procedimentoId) throws DataException;

    public void inserisciFaldoni(Connection connection, Integer[] ids,
            int procedimentoId) throws DataException;

    public void inserisciFascicoli(Connection connection, Integer[] ids,
            int procedimentoId) throws DataException;

    public void cancellaFaldoni(Connection connection, int procedimentoId)
            throws DataException;

    public void cancellaFascicoli(Connection connection, int procedimentoId)
            throws DataException;

    public int getMaxNumProcedimento(Connection connection, int aooId, int anno)
            throws DataException;

    public void inserisciProtocolli(Connection connection, ArrayList ids) throws DataException;

    public void cancellaProtocolli(Connection connection, int procedimentoId)
            throws DataException;

    public void setStatoProtocolloAssociato(Connection connection,
            int protocolloId, String stato) throws DataException;

    public SortedMap cerca(Utente utente, HashMap sqlDB) throws DataException;
    
    public int contaProcedimenti(Utente utente, HashMap sqlDB) throws DataException ;
    
    public ProcedimentoVO getProcedimentoByAnnoNumero(Connection connection,
            int anno, int numero) throws DataException;
    
    public void inserisciFascicoloProcedimento(Connection connection, ProcedimentoFascicoloVO procedimentofascicolo) 
    throws DataException;
    
    public void inserisciProcedimentoFaldone(Connection connection, ProcedimentoFaldoneVO procedimentoFaldoneVO) 
    throws DataException;
    
    public Collection getProcedimentiAnnoNumero(Connection connection)
    throws DataException;
    
    public ProcedimentoVO newStoriaProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException;
    
    public ProcedimentoVO aggiornaProcedimento(Connection connection,
            ProcedimentoVO vo) throws DataException;
    
    public Collection getStoriaProcedimenti(int procedimentoId) throws DataException;
    
    public ProcedimentoVO getProcedimentoByIdVersione(int id, int versione)
    throws DataException;
    
    public ProcedimentoVO getProcedimentoByIdVersione(Connection connection, int id,
            int versione) throws DataException;

}