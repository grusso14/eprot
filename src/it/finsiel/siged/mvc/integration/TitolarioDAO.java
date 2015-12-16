package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

/*
 * @author G.Calli.
 */

public interface TitolarioDAO {

    public Collection getListaTitolario(int aoo, String codice,
            String descrizione) throws DataException;

    public Collection getTitolariByParent(int ufficioId, int parentId, int aooId)
            throws DataException;

    public TitolarioVO getTitolario(int ufficioId, int titolarioId, int aooId)
            throws DataException;

    public TitolarioVO newArgomento(Connection conn, TitolarioVO titolarioVO)
            throws DataException;

    public TitolarioVO updateArgomento(Connection conn, TitolarioVO titolarioVO)
            throws DataException;

    public void deleteArgomento(Connection conn, int titolarioId)
            throws DataException;

    public void deleteArgomentoUffici(Connection conn, int titolarioId)
            throws DataException;

    public boolean getArgomentoProtocolli(int titolarioId) throws DataException;

    public Collection getTitolariByParent(int parentId, int aooId)
            throws DataException;

    public TitolarioVO getTitolario(int titolarioId) throws DataException;

    public String[] getUfficiTitolario(int titolarioId) throws DataException;

    public void associaArgomentoUffici(Connection conn,
            TitolarioVO titolarioVO, String[] uffici) throws DataException;

    public Collection getCategorie(int servizioId) throws DataException;

    public int getUfficioByServizio(int servizioId) throws DataException;

    public Collection getStoriaTitolarioById(int titolarioId)
            throws DataException;

    public boolean isTitolarioCancellabile(Connection connection,
            int titolarioId) throws DataException;

    public int controlloTitolarioByDescrizioneEdUfficio(int ufficioId,
            String descrizione) throws DataException;

    public void deleteArgomentiPerUfficio(Connection conn, int ufficioId)
            throws DataException;

    public void deleteArgomentoUfficio(Connection conn, int ufficioId,
            int titolarioId) throws DataException;

    public void associaArgomentoUfficio(Connection conn,
            TitolarioVO titolarioVO, int ufficioId) throws DataException;

	public boolean deleteAll(Connection connection) throws SQLException;
	
	public boolean deleteReferenceTitolario(Connection connection) throws SQLException;
}