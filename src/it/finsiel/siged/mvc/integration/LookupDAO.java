package it.finsiel.siged.mvc.integration;

import it.finsiel.siged.exception.DataException;
import it.finsiel.siged.mvc.vo.IdentityVO;

import java.util.Collection;
import java.util.Map;

/*
 * @author Almaviva sud.
 */

public interface LookupDAO {
    public Map getTipiDocumento(Collection aoo) throws DataException;
    
    public Map getTipiProcedimento(Collection aoo) throws DataException;

    public Collection getProvince() throws DataException;

    public Map getMezziSpedizione(Collection aoo) throws DataException;

    public IdentityVO getMezzoSpedizione(int mezzoId) throws DataException;

    public IdentityVO getMezzoSpedizioneDaMezzo(String m) throws DataException;

    public Collection getTitoliDestinatario() throws DataException;

    // public Collection getMezziSpedizione(int aooId) throws DataException;
}