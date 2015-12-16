package it.finsiel.siged.mvc.vo.organizzazione;

import it.finsiel.siged.mvc.vo.VersioneVO;

import java.util.Date;

/*
 * @author Almaviva sud.
 */

public final class UtenteVO extends VersioneVO {
    private int aooId;

    private String username;

    private String password;

    private String tempFolder;

    private String cognome;

    private String nome;

    private String emailAddress;

    private String codiceFiscale;

    private String matricola;

    private boolean abilitato;

    private Date dataFineAttivita;

    // Constructors
    public UtenteVO() {
    }

    // Getters
    public int getAooId() {
        return this.aooId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    // Setters
    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public void setUsername(String u) {
        this.username = u;
    }

    public void setPassword(String pwd) {
        this.password = pwd;
    }

    public String getFullName() {
        
        return (getCognome() != null ? getCognome() : " ") + ' '
        + (getNome() != null ? getNome() : "");
       /* return (getNome() != null ? getNome() : " ") + ' '
                + (getCognome() != null ? getCognome() : "");*/
    }

    public String getCognomeNome() {
        return (getCognome() != null ? getCognome() : "") + ' '
                + (getNome() != null ? getNome() : " ");
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    public boolean isAbilitato() {
        return abilitato;
    }

    public void setAbilitato(boolean abilitato) {
        this.abilitato = abilitato;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public Date getDataFineAttivita() {
        return dataFineAttivita;
    }

    public void setDataFineAttivita(Date dataFineAttivita) {
        this.dataFineAttivita = dataFineAttivita;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMatricola() {
        return matricola;
    }

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // Utility
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("Utente [Id: " + super.getId());
        str.append(" | Username: " + username);
        str.append(" | Password: " + password);
        str.append(" | Cognome Nome: " + getFullName());
        str.append("]");
        return str.toString();
    }
}