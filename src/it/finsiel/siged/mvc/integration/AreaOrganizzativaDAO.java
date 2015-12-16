package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;

import java.sql.Connection;
import java.util.Collection;

/*
 * @author G.Calli.
 */

public interface AreaOrganizzativaDAO {

    public Collection getAreeOrganizzative() throws DataException;

    public AreaOrganizzativaVO getAreaOrganizzativa(int areaorganizzativaId)
            throws DataException;

    public AreaOrganizzativaVO getAreaOrganizzativa(Connection connection,
            int areaorganizzativaId) throws DataException;

    public AreaOrganizzativaVO newAreaOrganizzativa(Connection conn,
            AreaOrganizzativaVO areaorganizzativaVO) throws DataException;

    public AreaOrganizzativaVO updateAreaOrganizzativa(Connection conn,
            AreaOrganizzativaVO areaorganizzativaVO) throws DataException;

    public void cancellaAreaOrganizzativa(Connection conn,
            int areaorganizzativaId) throws DataException;

    public boolean isAreaOrganizzativaCancellabile(int aooId)
            throws DataException;

    public boolean esisteAreaOrganizzativa(String aooDescrizione, int aooId)
            throws DataException;

    public boolean isModificabileDipendenzaTitolarioUfficio(int aooId)
            throws DataException;

    // Luigi 01/02/2006
    public Collection getUffici(int areaorganizzativaId) throws DataException;;
}