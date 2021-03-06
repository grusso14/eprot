/* Generated by Together */

package it.finsiel.siged.mvc.vo.protocollo;

import it.finsiel.siged.mvc.vo.VersioneVO;

/**
 * @persistent
 * @rdbPhysicalName SEGNATURE
 * @rdbPhysicalPrimaryKeyName SYS_C0022241
 */
public class SegnaturaVO extends VersioneVO {
    public SegnaturaVO() {
    }

    private String tipoProtocollo;

    private String textSegnatura;

    private int fkProtocolloId;

    /**
     * @return Returns the fkProtocolloId.
     */
    public int getFkProtocolloId() {
        return fkProtocolloId;
    }

    /**
     * @param fkProtocolloId
     *            The fkProtocolloId to set.
     */
    public void setFkProtocolloId(int fkProtocolloId) {
        this.fkProtocolloId = fkProtocolloId;
    }

    /**
     * @return Returns the statoTipoSegnatura.
     */
    public String getTipoProtocollo() {
        return tipoProtocollo;
    }

    /**
     * @param statoTipoSegnatura
     *            The statoTipoSegnatura to set.
     */
    public void setTipoProtocollo(String tipoProtocollo) {
        this.tipoProtocollo = tipoProtocollo;
    }

    /**
     * @return Returns the textSegnatura.
     */
    public String getTextSegnatura() {
        return textSegnatura;
    }

    /**
     * @param textSegnatura
     *            The textSegnatura to set.
     */
    public void setTextSegnatura(String textSegnatura) {
        this.textSegnatura = textSegnatura;
    }
}