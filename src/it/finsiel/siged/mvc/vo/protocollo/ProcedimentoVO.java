package it.finsiel.siged.mvc.vo.protocollo;

import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.mvc.bo.TitolarioBO;
import it.finsiel.siged.mvc.vo.VersioneVO;

import java.util.Date;

public class ProcedimentoVO extends VersioneVO {

    private Date dataAvvio;
    
    private String dataAvvioPro;

    private int aooId;

    private int ufficioId;

    private int titolarioId;

    private int statoId;
    
    private int tipoFinalitaId;
    
    private String tipoFinalita;

    private String oggetto;

    private int tipoProcedimentoId;

    private int referenteId;

    private String responsabile;

    private String posizione;

    private Date dataEvidenza;

    private String note;

    private int protocolloId;

    private String numeroProcedimento;

    private int anno;

    private int numero;

    private String numeroProtovollo;
    
    private String tipoProcedimentoDesc;
    
    private String posizioneSelezionata;
    
    private int posizioneSelezionataId;

   
    public ProcedimentoVO() {
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public String getNumeroProtovollo() {
        return numeroProtovollo;
    }

    public void setNumeroProtovollo(String numeroProtovollo) {
        this.numeroProtovollo = numeroProtovollo;
    }

    public Date getDataAvvio() {
        return dataAvvio;
    }

    public void setDataAvvio(Date dataAvvio) {
        this.dataAvvio = dataAvvio;
    }

    public Date getDataEvidenza() {
        return dataEvidenza;
    }

    public void setDataEvidenza(Date dataEvidenza) {
        this.dataEvidenza = dataEvidenza;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getPosizione() {
        return posizione;
    }

    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    public int getReferenteId() {
        return referenteId;
    }

    public void setReferenteId(int responsabileId) {
        this.referenteId = responsabileId;
    }

    public int getStatoId() {
        return statoId;
    }

    public void setStatoId(int stato) {
        this.statoId = stato;
    }

    public int getTipoFinalitaId() {
        return tipoFinalitaId;
    }

    public void setTipoFinalitaId(int tipoFinalita) {
        this.tipoFinalitaId = tipoFinalita;
    }

    public int getTitolarioId() {
        return titolarioId;
    }

    public void setTitolarioId(int titolarioId) {
        this.titolarioId = titolarioId;
    }

    public int getUfficioId() {
        return ufficioId;
    }

    public void setUfficioId(int ufficioId) {
        this.ufficioId = ufficioId;
    }

    public int getTipoProcedimentoId() {
        return tipoProcedimentoId;
    }

    public void setTipoProcedimentoId(int tipoProcedimentoId) {
        this.tipoProcedimentoId = tipoProcedimentoId;
    }

    public String getResponsabile() {
        return responsabile;
    }

    public void setResponsabile(String referente) {
        this.responsabile = referente;
    }

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

    public String getNumeroProcedimento() {
        return numeroProcedimento;
    }

    public void setNumeroProcedimento(String numeroProcedimento) {
        this.numeroProcedimento = numeroProcedimento;
    }

    public int getProtocolloId() {
        return protocolloId;
    }

    public void setProtocolloId(int protocolloId) {
        this.protocolloId = protocolloId;
    }

    public String getPathTitolario() {
        return TitolarioBO.getPathDescrizioneTitolario(getTitolarioId());
    }

    public String getDescUfficio() {
        Ufficio u = Organizzazione.getInstance().getUfficio(getUfficioId());
        if (u != null && u.getValueObject() != null)
            return u.getValueObject().getDescription();
        else
            return "";
    }
//
//    public String getTipoProcedimentoDesc() {
//        IdentityVO vo = (IdentityVO) LookupDelegate.getTipiProcedimento().get(
//                String.valueOf(getTipoProcedimentoId()));
//        if (vo != null)
//            return vo.getDescription();
//        else
//            return "-";
//    }

//    public String getTipoProcedimentoDesc() {
//        return tipoProcedimentoDesc;
//    }

    public void setTipoProcedimentoDesc(String tipoProcedimentoDesc) {
        this.tipoProcedimentoDesc = tipoProcedimentoDesc;
    }

    public String getTipoProcedimentoDesc() {
       
        if (this.tipoProcedimentoDesc != null)
            return this.tipoProcedimentoDesc;
        else
            return "-";
    }

    public String getPosizioneSelezionata() {
        return posizioneSelezionata;
    }

    public void setPosizioneSelezionata(String posizioneSelezionata) {
        this.posizioneSelezionata = posizioneSelezionata;
    }

    public int getPosizioneSelezionataId() {
        return posizioneSelezionataId;
    }

    public void setPosizioneSelezionataId(int posizioneSelezionataId) {
        this.posizioneSelezionataId = posizioneSelezionataId;
    }

    public String getTipoFinalita() {
        return tipoFinalita;
    }

    public void setTipoFinalita(String tipoFinalita) {
        this.tipoFinalita = tipoFinalita;
    }

    public String getDataAvvioPro() {
        return dataAvvioPro;
    }

    public void setDataAvvioPro(String dataAvvioPro) {
        this.dataAvvioPro = dataAvvioPro;
    }
  
}