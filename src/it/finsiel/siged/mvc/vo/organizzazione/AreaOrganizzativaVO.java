package it.finsiel.siged.mvc.vo.organizzazione;

import it.finsiel.siged.mvc.vo.IdentityVO;

import java.util.Date;

public class AreaOrganizzativaVO extends IdentityVO {

    private String codi_aoo;

    private Date data_istituzione;

    private String responsabile_nome;

    private String responsabile_cognome;

    private String responsabile_email;

    private String responsabile_telefono;

    private Date data_soppressione;

    private String telefono;

    private String fax;

    private String indi_dug;

    private String indi_toponimo;

    private String indi_civico;

    private String indi_cap;

    private String indi_comune;

    private int provincia_id;

    private String email;

    private String dipartimento_codice;

    private String dipartimento_descrizione;

    private String tipo_aoo;

    private String codi_documento_doc;

    private char flag_pdf;

    private int amministrazione_id;

    // campi dati posta elettronica normale e certificata
    private String pec_indirizzo;

    private String pec_username;

    private String pec_pwd;

    private boolean pecAbilitata = false;

    private String pec_ssl_port;

    private String pec_pop3;

    private String pec_smtp;

    private String pec_smtp_port;

    private String pn_indirizzo;

    private String pn_username;

    private String pn_pwd;

    private boolean pn_ssl = false;

    private String pn_ssl_port;

    private String pn_pop3;

    private String pn_smtp;

    private int pecTimer;

    // modifica 10/02/2006 TODO: caricare da dB
    private int dipendenzaTitolarioUfficio = 1;

    private int titolarioLivelloMinimo = 2;

    /**
     * @return Returns the provincia_id.
     */
    public int getProvincia_id() {
        return provincia_id;
    }

    /**
     * @param provincia_id
     *            The provincia_id to set.
     */
    public void setProvincia_id(int provincia_id) {
        this.provincia_id = provincia_id;
    }

    /**
     * @return Returns the codi_aoo.
     */
    public String getCodi_aoo() {
        return codi_aoo;
    }

    /**
     * @param codi_aoo
     *            The codi_aoo to set.
     */
    public void setCodi_aoo(String codi_aoo) {
        this.codi_aoo = codi_aoo;
    }

    /**
     * @return Returns the codi_documento_doc.
     */
    public String getCodi_documento_doc() {
        return codi_documento_doc;
    }

    /**
     * @param codi_documento_doc
     *            The codi_documento_doc to set.
     */
    public void setCodi_documento_doc(String codi_documento_doc) {
        this.codi_documento_doc = codi_documento_doc;
    }

    /**
     * @return Returns the flag_pdf.
     */
    public char getFlag_pdf() {
        return flag_pdf;
    }

    /**
     * @param flag_pdf
     *            The flag_pdf to set.
     */
    public void setFlag_pdf(char flag_pdf) {
        this.flag_pdf = flag_pdf;
    }

    /**
     * @return Returns the data_istituzione.
     */
    public Date getData_istituzione() {
        return data_istituzione;
    }

    /**
     * @param data_istituzione
     *            The data_istituzione to set.
     */
    public void setData_istituzione(Date data_istituzione) {
        this.data_istituzione = data_istituzione;
    }

    /**
     * @return Returns the data_soppressione.
     */
    public Date getData_soppressione() {
        return data_soppressione;
    }

    /**
     * @param data_soppressione
     *            The data_soppressione to set.
     */
    public void setData_soppressione(Date data_soppressione) {
        this.data_soppressione = data_soppressione;
    }

    /**
     * @return Returns the dipartimento_codice.
     */
    public String getDipartimento_codice() {
        return dipartimento_codice;
    }

    /**
     * @param dipartimento_codice
     *            The dipartimento_codice to set.
     */
    public void setDipartimento_codice(String dipartimento_codice) {
        this.dipartimento_codice = dipartimento_codice;
    }

    /**
     * @return Returns the dipartimento_descrizione.
     */
    public String getDipartimento_descrizione() {
        return dipartimento_descrizione;
    }

    /**
     * @param dipartimento_descrizione
     *            The dipartimento_descrizione to set.
     */
    public void setDipartimento_descrizione(String dipartimento_descrizione) {
        this.dipartimento_descrizione = dipartimento_descrizione;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the fax.
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax
     *            The fax to set.
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return Returns the indi_cap.
     */
    public String getIndi_cap() {
        return indi_cap;
    }

    /**
     * @param indi_cap
     *            The indi_cap to set.
     */
    public void setIndi_cap(String indi_cap) {
        this.indi_cap = indi_cap;
    }

    /**
     * @return Returns the indi_civico.
     */
    public String getIndi_civico() {
        return indi_civico;
    }

    /**
     * @param indi_civico
     *            The indi_civico to set.
     */
    public void setIndi_civico(String indi_civico) {
        this.indi_civico = indi_civico;
    }

    /**
     * @return Returns the indi_comune.
     */
    public String getIndi_comune() {
        return indi_comune;
    }

    /**
     * @param indi_comune
     *            The indi_comune to set.
     */
    public void setIndi_comune(String indi_comune) {
        this.indi_comune = indi_comune;
    }

    /**
     * @return Returns the indi_dug.
     */
    public String getIndi_dug() {
        return indi_dug;
    }

    /**
     * @param indi_dug
     *            The indi_dug to set.
     */
    public void setIndi_dug(String indi_dug) {
        this.indi_dug = indi_dug;
    }

    /**
     * @return Returns the indi_toponimo.
     */
    public String getIndi_toponimo() {
        return indi_toponimo;
    }

    /**
     * @param indi_toponimo
     *            The indi_toponimo to set.
     */
    public void setIndi_toponimo(String indi_toponimo) {
        this.indi_toponimo = indi_toponimo;
    }

    /**
     * @return Returns the responsabile_cognome.
     */
    public String getResponsabile_cognome() {
        return responsabile_cognome;
    }

    /**
     * @param responsabile_cognome
     *            The responsabile_cognome to set.
     */
    public void setResponsabile_cognome(String responsabile_cognome) {
        this.responsabile_cognome = responsabile_cognome;
    }

    /**
     * @return Returns the responsabile_email.
     */
    public String getResponsabile_email() {
        return responsabile_email;
    }

    /**
     * @param responsabile_email
     *            The responsabile_email to set.
     */
    public void setResponsabile_email(String responsabile_email) {
        this.responsabile_email = responsabile_email;
    }

    /**
     * @return Returns the responsabile_nome.
     */
    public String getResponsabile_nome() {
        return responsabile_nome;
    }

    /**
     * @param responsabile_nome
     *            The responsabile_nome to set.
     */
    public void setResponsabile_nome(String responsabile_nome) {
        this.responsabile_nome = responsabile_nome;
    }

    /**
     * @return Returns the responsabile_telefono.
     */
    public String getResponsabile_telefono() {
        return responsabile_telefono;
    }

    /**
     * @param responsabile_telefono
     *            The responsabile_telefono to set.
     */
    public void setResponsabile_telefono(String responsabile_telefono) {
        this.responsabile_telefono = responsabile_telefono;
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
     * @return Returns the tipo_aoo.
     */
    public String getTipo_aoo() {
        return tipo_aoo;
    }

    /**
     * @param tipo_aoo
     *            The tipo_aoo to set.
     */
    public void setTipo_aoo(String tipo_aoo) {
        this.tipo_aoo = tipo_aoo;
    }

    /**
     * @return Returns the amministrazione_id.
     */
    public int getAmministrazione_id() {
        return amministrazione_id;
    }

    /**
     * @param amministrazione_id
     *            The amministrazione_id to set.
     */
    public void setAmministrazione_id(int amministrazione_id) {
        this.amministrazione_id = amministrazione_id;
    }

    public String getPec_indirizzo() {
        return pec_indirizzo;
    }

    public void setPec_indirizzo(String pec_indirizzo) {
        this.pec_indirizzo = pec_indirizzo;
    }

    public String getPec_pop3() {
        return pec_pop3;
    }

    public void setPec_pop3(String pec_pop3) {
        this.pec_pop3 = pec_pop3;
    }

    public String getPec_pwd() {
        return pec_pwd;
    }

    public void setPec_pwd(String pec_pwd) {
        this.pec_pwd = pec_pwd;
    }

    public String getPec_smtp() {
        return pec_smtp;
    }

    public void setPec_smtp(String pec_smtp) {
        this.pec_smtp = pec_smtp;
    }

    public boolean getPecAbilitata() {
        return pecAbilitata;
    }

    public void setPecAbilitata(boolean pecAbilitata) {
        this.pecAbilitata = pecAbilitata;
    }

    public String getPec_ssl_port() {
        return pec_ssl_port;
    }

    public void setPec_ssl_port(String pec_ssl_port) {
        this.pec_ssl_port = pec_ssl_port;
    }

    public String getPec_username() {
        return pec_username;
    }

    public void setPec_username(String pec_username) {
        this.pec_username = pec_username;
    }

    public String getPn_indirizzo() {
        return pn_indirizzo;
    }

    public void setPn_indirizzo(String pn_indirizzo) {
        this.pn_indirizzo = pn_indirizzo;
    }

    public String getPn_pop3() {
        return pn_pop3;
    }

    public void setPn_pop3(String pn_pop3) {
        this.pn_pop3 = pn_pop3;
    }

    public String getPn_pwd() {
        return pn_pwd;
    }

    public void setPn_pwd(String pn_pwd) {
        this.pn_pwd = pn_pwd;
    }

    public String getPn_smtp() {
        return pn_smtp;
    }

    public void setPn_smtp(String pn_smtp) {
        this.pn_smtp = pn_smtp;
    }

    public boolean getPn_ssl() {
        return pn_ssl;
    }

    public void setPn_ssl(boolean pn_ssl) {
        this.pn_ssl = pn_ssl;
    }

    public String getPn_ssl_port() {
        return pn_ssl_port;
    }

    public void setPn_ssl_port(String pn_ssl_port) {
        this.pn_ssl_port = pn_ssl_port;
    }

    public String getPn_username() {
        return pn_username;
    }

    public void setPn_username(String pn_username) {
        this.pn_username = pn_username;
    }

    public String getPec_smtp_port() {
        return pec_smtp_port;
    }

    public void setPec_smtp_port(String pec_smtp_port) {
        this.pec_smtp_port = pec_smtp_port;
    }

    public int getPecTimer() {
        return pecTimer;
    }

    public void setPecTimer(int pecTimer) {
        this.pecTimer = pecTimer;
    }

    public int getDipendenzaTitolarioUfficio() {
        return dipendenzaTitolarioUfficio;
    }

    public void setDipendenzaTitolarioUfficio(int dipendenzaTitolarioUfficio) {
        this.dipendenzaTitolarioUfficio = dipendenzaTitolarioUfficio;
    }

    public int getTitolarioLivelloMinimo() {
        return titolarioLivelloMinimo;
    }

    public void setTitolarioLivelloMinimo(int titolarioLivelloMinimo) {
        this.titolarioLivelloMinimo = titolarioLivelloMinimo;
    }

}