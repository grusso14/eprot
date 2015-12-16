package it.finsiel.siged.mvc.presentation.actionform;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public abstract class UploaderForm extends ActionForm {

    protected String nomeFileUpload;

    protected FormFile formFileUpload;

    protected FormFile filePrincipaleUpload;

    /**
     * @return Returns the formFileUpload.
     */
    public FormFile getFormFileUpload() {
        return formFileUpload;
    }

    /**
     * @param formFileUpload
     *            The formFileUpload to set.
     */
    public void setFormFileUpload(FormFile formFileUpload) {
        this.formFileUpload = formFileUpload;
    }

    /**
     * @return Returns the nomeFileUpload.
     */
    public String getNomeFileUpload() {
        return nomeFileUpload;
    }

    /**
     * @param nomeFileUpload
     *            The nomeFileUpload to set.
     */
    public void setNomeFileUpload(String nomeFileUpload) {
        this.nomeFileUpload = nomeFileUpload;
    }

    /**
     * @return Returns the filePrincipaleUpload.
     */
    public FormFile getFilePrincipaleUpload() {
        return filePrincipaleUpload;
    }

    /**
     * @param filePrincipaleUpload
     *            The filePrincipaleUpload to set.
     */
    public void setFilePrincipaleUpload(FormFile filePrincipaleUpload) {
        this.filePrincipaleUpload = filePrincipaleUpload;
    }
}