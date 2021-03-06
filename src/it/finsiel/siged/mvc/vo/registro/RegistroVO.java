/* Generated by Together */

package it.finsiel.siged.mvc.vo.registro;

import it.finsiel.siged.mvc.vo.VersioneVO;
import it.finsiel.siged.util.DateUtil;

import java.util.Date;

/**
 * @db: registro
 */
public class RegistroVO extends VersioneVO {
    public RegistroVO() {
    }

    /**
     * @db: codi_registro varchar(30) NOT NULL
     */
    private String codRegistro;

    /**
     * @db: desc_registro varchar(100)
     */
    private String descrizioneRegistro;

    /**
     * @db: nume_ultimo_progressivo integer
     */
    private int numUltimoProgressivo;

    /**
     * @db: nume_ultimo_progr_interno integer
     */
    private int numUltimoProgrInterno;

    /**
     * @db: nume_ultimo_fascicolo integer
     */
    private int numUltimoFascicolo;

    /**
     * @db: data_apertura_registro timestamp NOT NULL
     */
    private Date dataAperturaRegistro;

    /**
     * @db: flag_aperto_uscita numeric(1) NOT NULL
     */
    private boolean apertoUscita;

    /**
     * @db: flag_aperto_ingresso numeric(1) NOT NULL
     */
    private boolean apertoIngresso;

    /**
     * @db: flag_ufficiale numeric(1) NOT NULL
     */
    private boolean ufficiale;

    /**
     * @db: flag_data_bloccata numeric(1) NOT NULL DEFAULT 0
     */
    private boolean dataBloccata;

    /**
     * @db: aoo_id integer NOT NULL
     */
    private int aooId;

    /**
     * @return Returns the codRegistro.
     */
    public String getCodRegistro() {
        return codRegistro;
    }

    /**
     * @param codRegistro
     *            The codRegistro to set.
     */
    public void setCodRegistro(String codRegistro) {
        this.codRegistro = codRegistro;
    }

    /**
     * @return Returns the dataAperturaRegistro.
     */
    public Date getDataAperturaRegistro() {
        return dataAperturaRegistro;
    }

    public int getAnnoCorrente() {
        return DateUtil.getYear(getDataAperturaRegistro());
    }

    /**
     * @param dataAperturaRegistro
     *            The dataAperturaRegistro to set.
     */
    public void setDataAperturaRegistro(Date dataAperturaRegistro) {
        this.dataAperturaRegistro = dataAperturaRegistro;
    }

    /**
     * @return Returns the descrizioneRegistro.
     */
    public String getDescrizioneRegistro() {
        return descrizioneRegistro;
    }

    /**
     * @param descrizioneRegistro
     *            The descrizioneRegistro to set.
     */
    public void setDescrizioneRegistro(String descrizioneRegistro) {
        this.descrizioneRegistro = descrizioneRegistro;
    }

    /**
     * @return Returns the fkAoo.
     */
    public int getAooId() {
        return aooId;
    }

    /**
     * @param aooId
     *            The fkAoo to set.
     */
    public void setFkAoo(int aooId) {
        this.aooId = aooId;
    }

    /**
     * @return Returns the flagApertoIngresso.
     */
    public boolean getApertoIngresso() {
        return apertoIngresso;
    }

    /**
     * @param flagApertoIngresso
     *            The flagApertoIngresso to set.
     */
    public void setApertoIngresso(boolean flagApertoIngresso) {
        this.apertoIngresso = flagApertoIngresso;
    }

    /**
     * @return Returns the flagApertoUscita.
     */
    public boolean getApertoUscita() {
        return apertoUscita;
    }

    /**
     * @param flagApertoUscita
     *            The flagApertoUscita to set.
     */
    public void setApertoUscita(boolean flagApertoUscita) {
        this.apertoUscita = flagApertoUscita;
    }

    /**
     * @return Returns the flagApriChiudiAutomatico.
     */
    public boolean getDataBloccata() {
        return dataBloccata;
    }

    /**
     * @param flagApriChiudiAutomatico
     *            The flagApriChiudiAutomatico to set.
     */
    public void setDataBloccata(boolean flagApriChiudiAutomatico) {
        this.dataBloccata = flagApriChiudiAutomatico;
    }

    /**
     * @return Returns the flagUfficiale.
     */
    public boolean getUfficiale() {
        return ufficiale;
    }

    /**
     * @param flagUfficiale
     *            The flagUfficiale to set.
     */
    public void setUfficiale(boolean flagUfficiale) {
        this.ufficiale = flagUfficiale;
    }

    /**
     * @return Returns the numUltimoFascicolo.
     */
    public int getNumUltimoFascicolo() {
        return numUltimoFascicolo;
    }

    /**
     * @param numUltimoFascicolo
     *            The numUltimoFascicolo to set.
     */
    public void setNumUltimoFascicolo(int numUltimoFascicolo) {
        this.numUltimoFascicolo = numUltimoFascicolo;
    }

    /**
     * @return Returns the numUltimoProgressivo.
     */
    public int getNumUltimoProgressivo() {
        return numUltimoProgressivo;
    }

    /**
     * @param numUltimoProgressivo
     *            The numUltimoProgressivo to set.
     */
    public void setNumUltimoProgressivo(int numUltimoProgressivo) {
        this.numUltimoProgressivo = numUltimoProgressivo;
    }

    /**
     * @return Returns the numUltimoProgrInterno.
     */
    public int getNumUltimoProgrInterno() {
        return numUltimoProgrInterno;
    }

    /**
     * @param numUltimoProgrInterno
     *            The numUltimoProgrInterno to set.
     */
    public void setNumUltimoProgrInterno(int numUltimoProgrInterno) {
        this.numUltimoProgrInterno = numUltimoProgrInterno;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }
}