package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.ListaDistribuzioneVO;
import it.finsiel.siged.mvc.vo.RubricaListaVO;
import it.finsiel.siged.mvc.vo.lookup.SoggettoVO;

import java.sql.Connection;
import java.util.ArrayList;

public interface SoggettoDAO {
    public ArrayList getListaPersonaFisica(int aooId,String cognome, String nome,
            String codiceFiscale) throws DataException;

    public ArrayList getListaPersonaGiuridica(int aooId,String denominazione, String pIva )
            throws DataException;

    public SoggettoVO getPersonaGiuridica(int id) throws DataException;

    public SoggettoVO getPersonaGiuridica(Connection connection, int id)
            throws DataException;

    public SoggettoVO getPersonaFisica(int id) throws DataException;

    public SoggettoVO getPersonaFisica(Connection connection, int id)
            throws DataException;

    public SoggettoVO newPersonaFisica(Connection conn, SoggettoVO personaFisica)
            throws DataException;

    public SoggettoVO newPersonaGiuridica(Connection conn,
            SoggettoVO personaGiuridica) throws DataException;

    public SoggettoVO editPersonaGiuridica(Connection conn,
            SoggettoVO personaGiuridica) throws DataException;

    public SoggettoVO editPersonaFisica(Connection conn,
            SoggettoVO personaFisica) throws DataException;

    public int deleteSoggetto(Connection conn, long id) throws DataException;

    /* Lista Distribuzione */

    public ListaDistribuzioneVO newListaDistribuzione(Connection conn,
            ListaDistribuzioneVO listaDistribuzione) throws DataException;

    public ListaDistribuzioneVO editListaDistribuzione(Connection conn,
            ListaDistribuzioneVO ListaDistribuzione) throws DataException;

    public int deleteListaDistribuzione(Connection conn, long id)
            throws DataException;

    public ArrayList getElencoListaDistribuzione(String descrizione, int aooId)
            throws DataException;

    public ListaDistribuzioneVO getListaDistribuzione(int id)
            throws DataException;

    public ArrayList getElencoListeDistribuzione() throws DataException;

    public ArrayList getDestinatariListaDistribuzione(int lista_id)
            throws DataException;

    public void inserisciSoggettoLista(Connection connection, int listaId,
            int soggettoId, String tipoSoggetto, String utente)
            throws DataException;

    public int deleteRubricaListaDistribuzione(Connection connection, long id)
            throws DataException;

    public void inserisciSoggettoLista(Connection connection,
            RubricaListaVO rubricaLista) throws DataException;

}