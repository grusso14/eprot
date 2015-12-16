package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.organizzazione.AmministrazioneVO;

import java.util.ArrayList;
import java.util.Collection;

public interface OrganizzazioneDAO {
    public AmministrazioneVO getAmministrazione() throws DataException;

    public Collection getUffici() throws DataException;

    public ArrayList getIdentificativiUffici(int utenteId) throws DataException;

    public AmministrazioneVO updateAmministrazione(AmministrazioneVO ammVO)
            throws DataException;
}