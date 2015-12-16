/*
 * Created on 28-gen-2005
 *
 * 
 */
package it.finsiel.siged.model;

import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.mvc.vo.posta.EmailVO;
import it.finsiel.siged.mvc.vo.posta.PecDaticertVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Almaviva sud
 * 
 */
public class MessaggioEmailEntrata {

    private EmailVO email = new EmailVO();;

    private ArrayList allegati = new ArrayList(2);

    private String tempFolder;

    private String tipoEmail;

    private String messaggioErrore;

    private PecDaticertVO daticertXML = new PecDaticertVO();

    public MessaggioEmailEntrata() {
    }

    public Collection getAllegati() {
        return allegati;
    }

    public void setAllegati(ArrayList allegati) {
        this.allegati = allegati;
    }

    public void addAllegato(DocumentoVO d) {
        allegati.add(d);
    }

    public String getTempFolder() {
        return tempFolder;
    }

    public void setTempFolder(String tempFolder) {
        this.tempFolder = tempFolder;
    }

    /**
     * @return Returns the email.
     */
    public EmailVO getEmail() {
        return email;
    }

    /**
     * @param email
     *            The email to set.
     */
    public void setEmail(EmailVO email) {
        this.email = email;
    }

    public String getTipoEmail() {
        if (daticertXML.getReturnValue() == ReturnValues.VALID)
            return daticertXML.getTipo();
        else
            return tipoEmail;
    }

    public void setTipoEmail(String tipoEmail) {
        this.tipoEmail = tipoEmail;
    }

    public String getMessaggioErrore() {
        if (daticertXML.getReturnValue() == ReturnValues.VALID)
            return daticertXML.getErrore();
        else
            return messaggioErrore;
    }

    public void setMessaggioErrore(String messaggioErrore) {
        this.messaggioErrore = messaggioErrore;
    }

    public PecDaticertVO getDaticertXML() {
        return daticertXML;
    }

    public void setDaticertXML(PecDaticertVO daticertXML) {
        this.daticertXML = daticertXML;
    }

}
