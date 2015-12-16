package it.finsiel.siged.mvc.vo.firma;

import it.finsiel.siged.mvc.vo.BaseVO;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CaVO extends BaseVO {

    private String issuerCN;

    private byte[] publicKey;

    private Date validoDal;

    private Date validoAl;

    private Map crlUrls = new HashMap(1);

    public String getIssuerCN() {
        return issuerCN;
    }

    public void setIssuerCN(String issuerDN) {
        this.issuerCN = issuerDN;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public Date getValidoAl() {
        return validoAl;
    }

    public void setValidoAl(Date validoAl) {
        this.validoAl = validoAl;
    }

    public Date getValidoDal() {
        return validoDal;
    }

    public void setValidoDal(Date validoDal) {
        this.validoDal = validoDal;
    }

    public void aggiungiCrlUrl(CrlUrlVO vo) {
        crlUrls.put(vo.getUrl(), vo);
    }

    public Map getCrlUrls() {
        return crlUrls;
    }

    public void setCrlUrls(Map crlUrls) {
        this.crlUrls = crlUrls;
    }

    public Collection getCrlUrlsCollection() {
        return crlUrls.values();
    }
}