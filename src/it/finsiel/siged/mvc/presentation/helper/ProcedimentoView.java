package it.finsiel.siged.mvc.presentation.helper;

import it.finsiel.siged.mvc.bo.TitolarioBO;

import java.util.Date;

public class ProcedimentoView {

    private String posizione;

    private String descUfficioId;

    private Date dataProtocollo;

    /**
     * @db: procedimento_id ,
     */

    private int procedimentoId;

    /**
     * @db: numero_procedimento ,
     */

    private int numeroProcedimento;
    
    private String numeroProcedimentoStr;

    /**
     * @db: data_avvio date NOT NULL,
     */
    private Date dataAvvio;
    
    private String dataAvvioStr;

    /**
     * @db: tipo_id int4 NOT NULL,
     */
    private int tipoId;

    /**
     * @db: responsabile varchar(100),
     */
    private String responsabile;

    /**
     * @db: posizione_id int4 NOT NULL,
     */
    private int posizioneId;

    /**
     * @db: stato_id int4 NOT NULL,
     */
    private int statoId;

    /**
     * @db: data_evidenza date,
     */
    private String dataEvidenza;

    /**
     * @db: oggetto varchar(250),
     */
    private String oggetto;

    /**
     * @db: note varchar(250),
     */
    private String note;

    /**
     * @db: data_annullamento date,
     */
    private Date dataAnnullamento;

    /**
     * @db: protocollo_id int4,
     */
    private int protocolloId;

    /**
     * @db: numerorifdoc varchar(37),
     */
    private String numeroRifDoc;

    /**
     * @db: sottocategoria varchar(300),
     */
    private String sottoCategoria;

    /**
     * @db: ufficio_id int4 NOT NULL,
     */
    private int ufficioId;

    /**
     * @db: utente_id int4,
     */
    private int utenteId;

    /**
     * @db: servizio_titolario_id int4,
     */
    private int servizioTitolarioId;

    /**
     * @db: categoria_titolario_id int4,
     */

    private int categoriaTitolarioId;

    /**
     * @db: keysicurezza int4,
     */

    private int keySicurezza;

    /**
     * @db: versione int4,
     */

    private int versione;
    
    
    
    /**
     * @db: titolario_id int4,
     */
     
    private int titolarioId;
    
    
    /**
     * @db:  aoo_id int4,
     */
    
    private int aooId;
    
    /**
     * @db:  anno int4,
     */
    
    private int anno;
    
    
    /**
     * @db:  numero int4,
     */
    
    
    
    private int numero;

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getVersione() {
        return versione;
    }

    public void setVersione(int versione) {
        this.versione = versione;
    }

    public Date getDataAnnullamento() {
        return dataAnnullamento;
    }

    public void setDataAnnullamento(Date dataAnnullamento) {
        this.dataAnnullamento = dataAnnullamento;
    }

    public Date getDataAvvio() {
        return dataAvvio;
    }

    public void setDataAvvio(Date dataAvvio) {
        this.dataAvvio = dataAvvio;
    }

    public String getDataEvidenza() {
        return dataEvidenza;
    }

    public void setDataEvidenza(String dataEvidenza) {
        this.dataEvidenza = dataEvidenza;
    }

    public Date getDataProtocollo() {
        return dataProtocollo;
    }

    public void setDataProtocollo(Date dataProtocollo) {
        this.dataProtocollo = dataProtocollo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNumeroRifDoc() {
        return numeroRifDoc;
    }

    public void setNumeroRifDoc(String numeroRifDoc) {
        this.numeroRifDoc = numeroRifDoc;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public int getPosizioneId() {
        return posizioneId;
    }

    public void setPosizioneId(int posizioneId) {
        this.posizioneId = posizioneId;
    }

    public String getPathTitolario() {
        return TitolarioBO.getPathDescrizioneTitolario(getTitolarioId());
    }
    
    public int getProtocolloId() {
        return protocolloId;
    }

    public void setProtocolloId(int protocolloId) {
        this.protocolloId = protocolloId;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String responsabile) {
        this.responsabile = responsabile;
    }

    public String getSottoCategoria() {
        return sottoCategoria;
    }

    public void setSottoCategoria(String sottoCategoria) {
        this.sottoCategoria = sottoCategoria;
    }

    public int getStatoId() {
        return statoId;
    }

    public void setStatoId(int statoId) {
        this.statoId = statoId;
    }

    public int getTipoId() {
        return tipoId;
    }

    public void setTipoId(int tipoId) {
        this.tipoId = tipoId;
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

    public int getCategoriaTitolarioId() {
        return categoriaTitolarioId;
    }

    public void setCategoriaTitolarioId(int categoriaTitolarioId) {
        this.categoriaTitolarioId = categoriaTitolarioId;
    }

    public int getServizioTitolarioId() {
        return servizioTitolarioId;
    }

    public void setServizioTitolarioId(int servizioTitolarioId) {
        this.servizioTitolarioId = servizioTitolarioId;
    }

    public int getKeySicurezza() {
        return keySicurezza;
    }

    public void setKeySicurezza(int keySicurezza) {
        this.keySicurezza = keySicurezza;
    }

    public int getProcedimentoId() {
        return procedimentoId;
    }

    public void setProcedimentoId(int procedimentoId) {
        this.procedimentoId = procedimentoId;
    }

    public int getNumeroProcedimento() {
        return numeroProcedimento;
    }

    public void setNumeroProcedimento(int numeroProcedimento) {
        this.numeroProcedimento = numeroProcedimento;
    }

    public String getDescUfficioId() {
        return descUfficioId;
    }

    public void setDescUfficioId(String descUfficioId) {
        this.descUfficioId = descUfficioId;
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public int getTitolarioId() {
        return titolarioId;
    }

    public void setTitolarioId(int titolarioId) {
        this.titolarioId = titolarioId;
    }

    public String getPosizione() {
        return posizione;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    public String getDataAvvioStr() {
        return dataAvvioStr;
    }

    public void setDataAvvioStr(String dataAvvioStr) {
        this.dataAvvioStr = dataAvvioStr;
    }

    public String getNumeroProcedimentoStr() {
        return numeroProcedimentoStr;
    }

    public void setNumeroProcedimentoStr(String numeroProcedimentoStr) {
        this.numeroProcedimentoStr = numeroProcedimentoStr;
    }

}