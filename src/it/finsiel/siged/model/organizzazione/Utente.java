package it.finsiel.siged.model.organizzazione;

import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.RegistroDelegate;
import it.finsiel.siged.mvc.vo.organizzazione.AreaOrganizzativaVO;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * @author Almaviva sud.
 */

public final class Utente {
    private UtenteVO valueObject;

    private Map registri = new HashMap(2);

    private int registroUfficialeId;

    private Map uffici = new HashMap(2);

    private String sessionId;

    private int ufficioInUso;

    private int registroInUso;

    public Utente(UtenteVO vo) {
        this.valueObject = vo;
    }

    public UtenteVO getValueObject() {
        return valueObject;
    }

    public void setValueObject(UtenteVO valueObject) {
        this.valueObject = valueObject;
    }

    public AreaOrganizzativaVO getAreaOrganizzativa() {
        int aooId = getValueObject().getAooId();
        Organizzazione org = Organizzazione.getInstance();
        return org.getAreaOrganizzativa(aooId).getValueObject();
    }

    /**
     * @return Returns the registroUfficialeId.
     */
    public int getRegistroUfficialeId() {
        return registroUfficialeId;
    }

    /**
     * @param registroUfficialeId
     *            The registroUfficialeId to set.
     */
    public void setRegistroUfficialeId(int registroUfficialeId) {
        this.registroUfficialeId = registroUfficialeId;
    }

    /**
     * @return Returns the registri.
     */
    public Map getRegistri() {
        return registri;
    }

    /**
     * @param registri
     *            The registri to set.
     */
    public void setRegistri(Map registri) {
        this.registri = registri;
    }

    public RegistroVO getRegistroUfficiale() {
        if (registri.containsKey(new Integer(getRegistroUfficialeId())))
            return (RegistroVO) registri.get(new Integer(
                    getRegistroUfficialeId()));
        else
            return null;
    }

    public Collection getRegistriCollection() {
        return registri.values();
    }

    /**
     * @return Returns the registroInUso.
     */
    public int getRegistroInUso() {
        return registroInUso;
    }

    /**
     * @return Returns the registroInUso.
     */
    public RegistroVO getRegistroVOInUso() {
        return (RegistroVO) getRegistri().get(new Integer(getRegistroInUso()));
    }

    /**
     * @param registroInUso
     *            The registroInUso to set.
     */
    public void setRegistroInUso(int registroInUso) {
        this.registroInUso = registroInUso;
    }

    /**
     * @return Returns the ufficioInUso.
     */
    public int getUfficioInUso() {
        return ufficioInUso;
    }

    /**
     * @return Returns the ufficioInUso.
     */
    public UfficioVO getUfficioVOInUso() {
        return (UfficioVO) getUffici().get(new Integer(getUfficioInUso()));
    }

    /**
     * @param ufficioInUso
     *            The ufficioInUso to set.
     */
    public void setUfficioInUso(int ufficioInUso) {
        this.ufficioInUso = ufficioInUso;
    }

    /**
     * @return Returns the sessionId.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId
     *            The sessionId to set.
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return Returns the uffici.
     */
    public Map getUffici() {
        return uffici;
    }

    public String getUfficiIds() {
        String ids = String.valueOf(getUfficioInUso());
        for (Iterator i = getUffici().values().iterator(); i.hasNext();) {
            UfficioVO uff = (UfficioVO) i.next();
            if (uff.getId().intValue() != getUfficioInUso())
                ids += ',' + uff.getId().toString();
        }
        return ids;
    }

    /**
     * @param uffici
     *            The uffici to set.
     */
    public void setUffici(HashMap uffici) {
        this.uffici = uffici;
    }

    public Collection getUfficiCollection() {
        return uffici.values();
    }

    /**
     * @return ultimo numero di protocollo (con indicazione dell'anno) per il
     *         registro in uso.
     */
    public String getUltimoProtocollo() {
        String numero = null;
        ProtocolloDelegate delegate = ProtocolloDelegate.getInstance();
        RegistroVO registro = (RegistroVO) getRegistri().get(
                new Integer(getRegistroInUso()));
        if (registro != null) {
            int anno = registro.getAnnoCorrente();
            numero = delegate.getUltimoProtocollo(anno, registro.getId()
                    .intValue())
                    + "/" + anno;
        }
        return numero;
    }

    public boolean isAbilitatoModifica(int ufficioId) {
        return (getUffici().containsKey(new Integer(ufficioId)) && RegistroDelegate
                .getInstance().isAbilitatoRegistro(getRegistroInUso(),
                        ufficioId, getValueObject().getId().intValue()));
    }

    public boolean isUtenteAbilitatoSuUfficio(int ufficioId) {
        return ((getUfficioVOInUso().getTipo()
                .equals(UfficioVO.UFFICIO_CENTRALE)) || (getUffici()
                .containsKey(new Integer(ufficioId)) && RegistroDelegate
                .getInstance().isAbilitatoRegistro(getRegistroInUso(),
                        ufficioId, getValueObject().getId().intValue())));
    }
}