/*
 * Created on 22-nov-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.mvc.vo.lookup;

import it.finsiel.siged.mvc.vo.VersioneVO;

import java.util.Date;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SoggettoVO extends VersioneVO {
    private String tipo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public SoggettoVO(String tipo) {
        setTipo(tipo);
    }

    public SoggettoVO(char tipo) {
        setTipo(tipo + "");
    }

    /**
     * @param indirizzo
     *            The indirizzo to set.
     */
    public void setIndirizzo(Indirizzo indirizzo) {
        this.indirizzo = indirizzo;
    }

    public String getIndirizzoCompleto() {
        return getIndirizzo().getToponimo()
                + (getIndirizzo().getCivico() != null ? ", "
                        + getIndirizzo().getCivico() : "");
    }

    public class Indirizzo {
        private Indirizzo() {
        }

        /**
         * @rdbPhysicalName INDI_COMUNE
         * @rdbSize 50
         * @rdbDigits 0
         * @rdbLogicalType VARCHAR
         */
        private String comune;

        /**
         * @return Returns the comune.
         */
        public String getComune() {
            return comune;
        }

        /**
         * @param comune
         *            The comune to set.
         */
        public void setComune(String comune) {
            this.comune = comune;
        }

        /**
         * @rdbPhysicalName INDI_CAP
         * @rdbSize 8
         * @rdbDigits 0
         * @rdbLogicalType VARCHAR
         */
        private String cap;

        /**
         * @rdbPhysicalName INDI_DUG
         * @rdbSize 10
         * @rdbDigits 0
         * @rdbLogicalType VARCHAR
         */
        private String dug;

        /**
         * @rdbPhysicalName INDI_TOPONIMO
         * @rdbSize 30
         * @rdbDigits 0
         * @rdbLogicalType VARCHAR
         */
        private String toponimo;

        /**
         * @rdbPhysicalName INDI_CIVICO
         * @rdbSize 10
         * @rdbDigits 0
         * @rdbLogicalType VARCHAR
         */
        private String civico;

        /**
         * @rdbPhysicalName SYS_C0022074
         * @rdbOptional
         */
        private int provinciaId;

        /**
         * @return Returns the cap.
         */
        public String getCap() {
            return cap;
        }

        /**
         * @param cap
         *            The cap to set.
         */
        public void setCap(String cap) {
            this.cap = cap;
        }

        /**
         * @return Returns the civico.
         */
        public String getCivico() {
            return civico;
        }

        /**
         * @param civico
         *            The civico to set.
         */
        public void setCivico(String civico) {
            this.civico = civico;
        }

        /**
         * @return Returns the dug.
         */
        public String getDug() {
            return dug;
        }

        /**
         * @param dug
         *            The dug to set.
         */
        public void setDug(String dug) {
            this.dug = dug;
        }

        /**
         * @return Returns the provinciaId.
         */
        public int getProvinciaId() {
            return provinciaId;
        }

        /**
         * @param provinciaId
         *            The provinciaId to set.
         */
        public void setProvinciaId(int provinciaId) {
            this.provinciaId = provinciaId;
        }

        /**
         * @return Returns the toponimo.
         */
        public String getToponimo() {
            return toponimo;
        }

        /**
         * @param toponimo
         *            The toponimo to set.
         */
        public void setToponimo(String toponimo) {
            this.toponimo = toponimo;
        }
        
        public String toString(){
        	return this.getToponimo() + " " + this.getCivico() + " " + this.getComune();
        }
    }

    /**
     * indirizzo
     */

    private Indirizzo indirizzo = new Indirizzo();

    public Indirizzo getIndirizzo() {
        return indirizzo;
    }

    /**
     * @rdbPhysicalName TEXT_NOTE
     * @rdbSize 500
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String note;

    /**
     * @rdbPhysicalName TELE_TELEFONO
     * @rdbSize 16
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String telefono;

    /**
     * @rdbPhysicalName TELE_FAX
     * @rdbSize 16
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String teleFax;

    /**
     * @rdbPhysicalName INDI_WEB
     * @rdbSize 50
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String indirizzoWeb;

    /**
     * @rdbPhysicalName INDI_EMAIL
     * @rdbSize 50
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String indirizzoEMail;

    /** @rdbPhysicalName SYS_C0022072 */
    private int aoo;

    /**
     * @return Returns the fkAoo.
     */
    public int getAoo() {
        return aoo;
    }

    /**
     * @param fkAoo
     *            The fkAoo to set.
     */
    public void setAoo(int aoo) {
        this.aoo = aoo;
    }

    /**
     * @return Returns the indirizzoEMail.
     */
    public String getIndirizzoEMail() {
        return indirizzoEMail;
    }

    /**
     * @param indirizzoEMail
     *            The indirizzoEMail to set.
     */
    public void setIndirizzoEMail(String indirizzoEMail) {
        this.indirizzoEMail = indirizzoEMail;
    }

    /**
     * @return Returns the indirizzoWeb.
     */
    public String getIndirizzoWeb() {
        return indirizzoWeb;
    }

    /**
     * @param indirizzoWeb
     *            The indirizzoWeb to set.
     */
    public void setIndirizzoWeb(String indirizzoWeb) {
        this.indirizzoWeb = indirizzoWeb;
    }

    /**
     * @return Returns the teleFax.
     */
    public String getTeleFax() {
        return teleFax;
    }

    /**
     * @param teleFax
     *            The teleFax to set.
     */
    public void setTeleFax(String teleFax) {
        this.teleFax = teleFax;
    }

    /**
     * @return Returns the telefono.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono
     *            The telefono to set.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return Returns the note.
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note
     *            The note to set.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @rdbPhysicalName CODI_FISCALE
     * @rdbSize 16
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String codiceFiscale;

    /**
     * @rdbPhysicalName CODI_CODICE
     * @rdbSize 10
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String codMatricola;

    /**
     * @rdbPhysicalName PERS_COGNOME
     * @rdbSize 100
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String cognome;

    /**
     * @rdbPhysicalName DESC_COMUNE_NASCITA
     * @rdbSize 50
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String comuneNascita;

    /**
     * @rdbPhysicalName DATA_NASCITA
     * @rdbSize 7
     * @rdbDigits 0
     * @rdbLogicalType TIMESTAMP
     */
    private Date dataNascita;

    /**
     * @rdbPhysicalName DESC_DITTA
     * @rdbSize 100
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String nome;

    /**
     * @rdbPhysicalName SYS_C0022073
     * @rdbOptional
     */
    private int provinciaNascitaId;

    /**
     * @rdbPhysicalName DESC_QUALIFICA
     * @rdbSize 30
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String qualifica;

    /**
     * @rdbPhysicalName SESSO
     * @rdbSize 1
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String sesso;

    /**
     * @rdbPhysicalName CODI_STATO_CIVILE
     * @rdbSize 10
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String statoCivile;

    /**
     * @return Returns the codiceFiscale.
     */
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    /**
     * @return Returns the codiceStatoCivile.
     */
    public String getCodiceStatoCivile() {
        return statoCivile;
    }

    /**
     * @return Returns the codMatricola.
     */
    public String getCodMatricola() {
        return codMatricola;
    }

    /**
     * @return Returns the cognome.
     */
    public String getCognome() {
        return cognome;
    }

    /**
     * @return Returns the comuneNascita.
     */
    public String getComuneNascita() {
        return comuneNascita;
    }

    /**
     * @return Returns the dataNascita.
     */
    public Date getDataNascita() {
        return dataNascita;
    }

    /**
     * @return Returns the nome.
     */
    public String getNome() {
        return nome;
    }

    /**
     * @return Returns the provinciaNascitaId.
     */
    public int getProvinciaNascitaId() {
        return provinciaNascitaId;
    }

    /**
     * @return Returns the qualifica.
     */
    public String getQualifica() {
        return qualifica;
    }

    /**
     * @return Returns the sesso.
     */
    public String getSesso() {
        return sesso;
    }

    /**
     * @return Returns the statoCivile.
     */
    public String getStatoCivile() {
        return statoCivile;
    }

    /**
     * @param codiceFiscale
     *            The codiceFiscale to set.
     */
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    /**
     * @param codiceStatoCivile
     *            The codiceStatoCivile to set.
     */
    public void setCodiceStatoCivile(String codiceStatoCivile) {
        this.statoCivile = codiceStatoCivile;
    }

    /**
     * @param codMatricola
     *            The codMatricola to set.
     */
    public void setCodMatricola(String codMatricola) {
        this.codMatricola = codMatricola;
    }

    /**
     * @param cognome
     *            The cognome to set.
     */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @param comuneNascita
     *            The comuneNascita to set.
     */
    public void setComuneNascita(String descrizioneComuneNascita) {
        this.comuneNascita = descrizioneComuneNascita;
    }

    /**
     * @param dataNascita
     *            The dataNascita to set.
     */
    public void setDataNascita(Date dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * @param nome
     *            The nome to set.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @param provinciaNascitaId
     *            The provinciaNascitaId to set.
     */
    public void setProvinciaNascitaId(int provinciaNascitaId) {
        this.provinciaNascitaId = provinciaNascitaId;
    }

    /**
     * @param qualifica
     *            The qualifica to set.
     */
    public void setQualifica(String descrizioneQualifica) {
        this.qualifica = descrizioneQualifica;
    }

    /**
     * @param sesso
     *            The sesso to set.
     */
    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    /**
     * @param statoCivile
     *            The statoCivile to set.
     */
    public void setStatoCivile(String statoCivile) {
        this.statoCivile = statoCivile;
    }

    /**
     * @rdbPhysicalName DESC_DITTA
     * @rdbSize 100
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String descrizioneDitta;

    /**
     * @rdbPhysicalName PERS_REFERENTE
     * @rdbSize 90
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String dug;

    private String emailReferente;

    /**
     * Solo per persone giuridiche 1 Pubblico 2 Privato
     * 
     * @rdbPhysicalName FLAG_SETTORE
     * @rdbSize 1
     * @rdbDigits 0
     * @rdbLogicalType DECIMAL
     */
    private long flagSettoreAppartenenza;

    /**
     * @rdbPhysicalName CODI_PARTITA_IVA
     * @rdbSize 11
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String partitaIva;

    /**
     * @rdbPhysicalName INDI_DUG
     * @rdbSize 10
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String referente;

    /**
     * @rdbPhysicalName TELE_REFERENTE
     * @rdbSize 16
     * @rdbDigits 0
     * @rdbLogicalType VARCHAR
     */
    private String telefonoReferente;

    /**
     * @return Returns the ragioneSociale.
     */
    public String getDescrizioneDitta() {
        return descrizioneDitta;
    }

    /**
     * @return Returns the dug.
     */
    public String getDug() {
        return dug;
    }

    /**
     * @return Returns the emailReferente.
     */
    public String getEmailReferente() {
        return emailReferente;
    }

    /**
     * @return Returns the flagSettoreAppartenenza.
     */
    public long getFlagSettoreAppartenenza() {
        return flagSettoreAppartenenza;
    }

    /**
     * @return Returns the partitaIva.
     */
    public String getPartitaIva() {
        return partitaIva;
    }

    /**
     * @return Returns the referente.
     */
    public String getReferente() {
        return referente;
    }

    /**
     * @return Returns the telefonoReferente.
     */
    public String getTelefonoReferente() {
        return telefonoReferente;
    }

    /**
     * @param ragioneSociale
     *            The ragioneSociale to set.
     */
    public void setDescrizioneDitta(String descrizioneDitta) {
        this.descrizioneDitta = descrizioneDitta;
    }

    /**
     * @param dug
     *            The dug to set.
     */
    public void setDug(String dug) {
        this.dug = dug;
    }

    /**
     * @param emailReferente
     *            The emailReferente to set.
     */
    public void setEmailReferente(String emailReferente) {
        this.emailReferente = emailReferente;
    }

    /**
     * @param flagSettoreAppartenenza
     *            The flagSettoreAppartenenza to set.
     */
    public void setFlagSettoreAppartenenza(long flagSettoreAppartenenza) {
        this.flagSettoreAppartenenza = flagSettoreAppartenenza;
    }

    /**
     * @param partitaIva
     *            The partitaIva to set.
     */
    public void setPartitaIva(String partitaIva) {
        this.partitaIva = partitaIva;
    }

    /**
     * @param referente
     *            The referente to set.
     */
    public void setReferente(String referente) {
        this.referente = referente;
    }

    /**
     * @param telefonoReferente
     *            The telefonoReferente to set.
     */
    public void setTelefonoReferente(String telefonoReferente) {
        this.telefonoReferente = telefonoReferente;
    }
    
    @Override
    public String toString() {
    	return this.cognome + " " + this.nome + " " + this.getIndirizzoCompleto();
    }
}