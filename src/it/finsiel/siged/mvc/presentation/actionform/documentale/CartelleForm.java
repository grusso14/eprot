/*
 * Created on 13-giu-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.mvc.presentation.actionform.documentale;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * @author Almaviva sud
 */
public class CartelleForm extends ActionForm {

    private Collection sottoCartelle = new ArrayList();

    private Collection pathCartella = new ArrayList();

    private int cartellaCorrenteId;

    private String nomeCartella;

    private String curNomeCartella;

    private int cartellaId;

    private int documentoId;

    private Collection files = new ArrayList();

    public Collection getFiles() {
        return files;
    }

    public int getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(int documentoId) {
        this.documentoId = documentoId;
    }

    public void setFiles(Collection files) {
        this.files = files;
    }

    public int getCartellaId() {
        return cartellaId;
    }

    public void setCartellaId(int cartellaId) {
        this.cartellaId = cartellaId;
    }

    public String getNomeCartella() {
        return nomeCartella;
    }

    public void setNomeCartella(String nomeCartella) {
        this.nomeCartella = nomeCartella;
    }

    public int getCartellaCorrenteId() {
        return cartellaCorrenteId;
    }

    public void setCartellaCorrenteId(int cartellaCorrenteId) {
        this.cartellaCorrenteId = cartellaCorrenteId;
    }

    public Collection getPathCartella() {
        return pathCartella;
    }

    public void setPathCartella(Collection pathCartella) {
        this.pathCartella = pathCartella;
    }

    public Collection getSottoCartelle() {
        return sottoCartelle;
    }

    public void setSottoCartelle(Collection sottoCartelle) {
        this.sottoCartelle = sottoCartelle;
    }

    public String getCurNomeCartella() {
        return curNomeCartella;
    }

    public void setCurNomeCartella(String curNomeCartella) {
        this.curNomeCartella = curNomeCartella;
    }

    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        if (request.getParameter("salvaCartella") != null) {
            if (getNomeCartella() == null
                    || "".equals(getNomeCartella().trim())) {
                errors.add("salvaCartella", new ActionMessage(
                        "selezione.obbligatoria", "nome cartella", ""));
            }
        }
        return errors;

    }

}
