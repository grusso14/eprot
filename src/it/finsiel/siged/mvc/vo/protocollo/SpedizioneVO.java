/* Generated by Together */

package it.finsiel.siged.mvc.vo.protocollo;

import it.finsiel.siged.mvc.vo.VersioneVO;

/**
 * @persistent
 * @rdbPhysicalName SPEDIZIONI
 * @rdbPhysicalPrimaryKeyName SYS_C0022027
 */
public class SpedizioneVO extends VersioneVO {

    private String codiceSpedizione;

    private String descrizioneSpedizione;

    private int aooId;

    private boolean flagAbilitato;

    private boolean flagCancellabile;
    
    

    public SpedizioneVO() {
    }

    /**
     * @return Returns the codSpedizione.
     */
    public String getCodiceSpedizione() {
        return codiceSpedizione;
    }

    /**
     * @param codSpedizione
     *            The codSpedizione to set.
     */
    public void setCodiceSpedizione(String codiceSpedizione) {
        this.codiceSpedizione = codiceSpedizione;
    }

    /**
     * @return Returns the descrizioneSpedizione.
     */
    public String getDescrizioneSpedizione() {
        return descrizioneSpedizione;
    }

    /**
     * @param descrizioneSpedizione
     *            The descrizioneSpedizione to set.
     */
    public void setDescrizioneSpedizione(String descrizioneSpedizione) {
        this.descrizioneSpedizione = descrizioneSpedizione;
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public boolean getFlagAbilitato() {
        return flagAbilitato;
    }

    public void setFlagAbilitato(boolean flagAbilitato) {
        this.flagAbilitato = flagAbilitato;
    }

    public boolean getFlagCancellabile() {
        return flagCancellabile;
    }

    public void setFlagCancellabile(boolean flagCancellabile) {
        this.flagCancellabile = flagCancellabile;
    }
}