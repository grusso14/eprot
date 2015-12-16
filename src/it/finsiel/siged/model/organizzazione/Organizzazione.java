package it.finsiel.siged.model.organizzazione;

import it.finsiel.siged.mvc.vo.organizzazione.AmministrazioneVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
import it.finsiel.siged.mvc.vo.registro.RegistroVO;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.FastHashMap;

public class Organizzazione {
    private static Organizzazione organizzazione;

    private AmministrazioneVO valueObject;

    private Map menuMap = new FastHashMap();

    private Map menuMapByLink = new FastHashMap();

    private Map areeOrganizzative = Collections.synchronizedMap(new HashMap(2));

    private Map uffici = new FastHashMap();

    private Map utentiById = new FastHashMap();

    private Map utentiByUsername = new FastHashMap();

    private Map registri = new HashMap(4);

    private Map utentiConnessi = Collections.synchronizedMap(new HashMap(10));

    private Organizzazione() {
    }

    public static Organizzazione getInstance() {
        if (organizzazione == null) {
            organizzazione = new Organizzazione();
        }
        return organizzazione;
    }

    public void setValueObject(AmministrazioneVO vo) {
        this.valueObject = vo;
    }

    public AmministrazioneVO getValueObject() {
        return valueObject;
    }

    public Collection getMenuList() {
        return menuMap.values();
    }

    public Menu getMenu(Integer menuId) {
        return (Menu) menuMap.get(menuId);
    }

    public Menu getMenu(int menuId) {
        return getMenu(new Integer(menuId));
    }

    public Menu getMenu(String link) {
        return (Menu) menuMapByLink.get(link);
    }

    public void addMenu(Menu menu) {
        this.menuMap.put(menu.getValueObject().getId(), menu);
        String link = menu.getValueObject().getLink();
        if (link != null) {
            int i = link.indexOf('?');
            if (i >= 0) {
                link = link.substring(0, i);
            }
            this.menuMapByLink.put(link, menu);
        }
    }

    public Collection getAreeOrganizzative() {
        return areeOrganizzative.values();
    }

    public AreaOrganizzativa getAreaOrganizzativa(int areaId) {
        return (AreaOrganizzativa) areeOrganizzative.get(new Integer(areaId));
    }

    public void addAreaOrganizzativa(AreaOrganizzativa aoo) {
        if (aoo != null) {
            areeOrganizzative.put(aoo.getValueObject().getId(), aoo);
            aoo.setAmministrazione(this);
        }
    }

    public void removeAreaOrganizzativa(Integer aooId) {
        if (areeOrganizzative.containsKey(aooId)) {
            areeOrganizzative.remove(aooId);
        }
    }

    /*
     * public Utente getUtenteConnesso(String sessionId) { return (Utente)
     * utentiConnessi.get(sessionId); }
     */

    /**
     * Restituisce il numero di utenti connessi
     * 
     * @param
     * @return int
     */

    public int getNumeroUtentiConnessi() {
        return utentiConnessi.size();
    }

    /**
     * Restituisce una collection degli utenti connessi
     * 
     * @param
     * @return int
     */
    public Collection getUtentiConnessi() {
        return utentiConnessi.values();
    }

    /**
     * controlla se esiste un utente nella lista per chiave sessionId
     * 
     * @param id
     *            String
     * @return boolean
     */

    public boolean existUtenteBySessionId(String id) {
        return utentiConnessi.containsKey(id);
    }

    public void removeSessionIdUtente(String id) {
        utentiConnessi.remove(id);
    }

    /**
     * Aggiunge un utente nella lista degli utenti con chiave username e
     * sessionId
     * 
     * @param u
     *            Utente
     */
    public void aggiungiUtenteConnesso(Utente u) {
        utentiConnessi.put(u.getSessionId(), u);
    }

    public void disconnettiUtente(Utente u) {
        if (u != null) {
            utentiConnessi.remove(u.getSessionId());
            u.setSessionId(null);
        }
    }

    public void addUfficio(Ufficio ufficio) {
        uffici.put(ufficio.getValueObject().getId(), ufficio);
    }

    public void removeUfficio(Integer ufficioId) {
        if (uffici.containsKey(ufficioId))
            uffici.remove(ufficioId);
    }

    public void addUtente(Utente utente) {
        UtenteVO ute = utente.getValueObject();
        utentiById.put(ute.getId(), utente);
        utentiByUsername.put(ute.getUsername(), utente);
    }

    public void addRegistro(RegistroVO registro) {
        registri.put(registro.getId(), registro);
    }

    public void removeRegistro(Integer registroId) {
        registri.remove(registroId);
    }

    public Collection getUffici() {
        return uffici.values();
    }

    public Ufficio getUfficio(int id) {
        return (Ufficio) uffici.get(new Integer(id));
    }

    public Utente getUtente(int id) {
        return (Utente) utentiById.get(new Integer(id));
    }

    public Utente getUtente(String username) {
        return (Utente) utentiByUsername.get(username);
    }

    public Collection getUtentiById() {
        return utentiById.values();
    }

    public RegistroVO getRegistro(Integer id) {
        return (RegistroVO) registri.get(id);
    }

    public RegistroVO getRegistro(int id) {
        return getRegistro(new Integer(id));
    }

    public Collection getRegistri() {
        return registri.values();
    }

    public void resetOrganizzazione() {
        organizzazione.menuMap.clear();
        organizzazione.areeOrganizzative.clear();
        organizzazione.menuMapByLink.clear();
        organizzazione.registri.clear();
        organizzazione.uffici.clear();
        organizzazione.utentiById.clear();
        organizzazione.utentiByUsername.clear();
        organizzazione.utentiConnessi.clear();
    }
}