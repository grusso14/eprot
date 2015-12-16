package it.finsiel.siged.mvc.vo;

public class ListaDistribuzioneVO extends VersioneVO {
    private int aooId;

    private String description;

    public ListaDistribuzioneVO() {
    }

    public ListaDistribuzioneVO(int aooId) {
        this.aooId = aooId;
    }

    public int getAooId() {
        return aooId;
    }

    public void setAooId(int aooId) {
        this.aooId = aooId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}