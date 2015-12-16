package it.finsiel.siged.mvc.presentation.helper;

public class AssegnatarioView {
    private int ufficioId;

    private int utenteId;

    private String nomeUfficio;

    private String descrizioneUfficio;

    private String nomeUtente;

    private char stato = 'S';

    private boolean competente = false;

    /**
     * @return Returns the stato.
     */
    public char getStato() {
        return stato;
    }

    /**
     * @param stato
     *            The stato to set.
     */
    public void setStato(char stato) {
        this.stato = stato;
    }

    public String getKey() {
        StringBuffer key = new StringBuffer();
        key.append(getUfficioId());
        if (getUtenteId() > 0) {
            key.append('_').append(getUtenteId());
        }
        return key.toString();
    }

    public String getNomeUfficio() {
        return nomeUfficio;
    }

    public void setNomeUfficio(String descrizione) {
        this.nomeUfficio = descrizione;
    }

    public int getUfficioId() {
        return ufficioId;
    }

    public void setUfficioId(int ufficioId) {
        this.ufficioId = ufficioId;
    }

    public int getUtenteId() {
        return utenteId;
    }

    public void setUtenteId(int utenteId) {
        this.utenteId = utenteId;
    }

    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String descrizioneUtente) {
        this.nomeUtente = descrizioneUtente;
    }

    public String getDescrizioneUfficio() {
        return descrizioneUfficio;
    }

    public void setDescrizioneUfficio(String descrizioneUfficio) {
        this.descrizioneUfficio = descrizioneUfficio;
    }

    public boolean isCompetente() {
        return competente;
    }

    public void setCompetente(boolean competente) {
        this.competente = competente;
    }

}