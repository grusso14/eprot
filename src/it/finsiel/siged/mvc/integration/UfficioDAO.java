package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.organizzazione.ReferenteUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.TitolarioUfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;

import java.sql.Connection;
import java.util.Collection;

/*
 * @author G.Calli.
 */

public interface UfficioDAO {

    public UfficioVO nuovoUfficio(Connection conn, UfficioVO ufficioVO)
            throws DataException;

    public void cancellaUfficio(Connection conn, int ufficioId)
            throws DataException;

    public UfficioVO modificaUfficio(Connection conn, UfficioVO ufficioVO)
            throws DataException;

    public boolean isUfficioCancellabile(int ufficioId) throws DataException;

    public void salvaUfficiTitolari(Connection conn,
            TitolarioUfficioVO titolarioufficioVO) throws DataException;

    public Collection getUfficiByParent(int ufficioId) throws DataException;

    public Collection getUtentiByUfficio(int ufficioId) throws DataException;

    public UfficioVO getUfficioVO(int ufficioId) throws DataException;

    public Collection getUfficiByUtente(int utenteId) throws DataException;
    
    public Collection getUffici() throws DataException;
    
    public Collection getUffici(int aooId) throws DataException;

    public void aggiornaUtentiReferenti(Connection conn, int ufficioId,
            String[] utentiId) throws DataException;

    public String[] getReferentiByUfficio(int ufficioId) throws DataException;

    public int getNumeroReferentiByUfficio(int ufficioId) throws DataException;

    public void cancellaUtentiReferenti(Connection conn, int ufficioId)
            throws DataException;

    public void inserisciUtentiReferenti(Connection conn,
            ReferenteUfficioVO referenteufficioVO) throws DataException;

    public long getCountUffici() throws DataException;

    public boolean isUtenteReferenteUfficio(int ufficioId, int utenteId)
            throws DataException;
}