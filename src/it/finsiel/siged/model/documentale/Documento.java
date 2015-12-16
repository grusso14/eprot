package it.finsiel.siged.model.documentale;

import it.finsiel.siged.mvc.vo.documentale.FileVO;
import it.finsiel.siged.mvc.vo.documentale.PermessoFileVO;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Calli
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Documento {

    private FileVO fileVO = new FileVO();

    private Collection permessi = new ArrayList(); // di tipo AssegnatarioVO

    private int permessoCorrente;

    private boolean versioneDefault = false;

    public Collection getPermessi() {
        return permessi;
    }

    public boolean isVersioneDefault() {
        return versioneDefault;
    }

    public void setVersioneDefault(boolean versioneDefault) {
        this.versioneDefault = versioneDefault;
    }

    public FileVO getFileVO() {
        return fileVO;
    }

    public void setFileVO(FileVO documento) {
        this.fileVO = documento;
    }

    public void aggiungiPermessi(Collection assegnatari) {
        this.permessi.addAll(assegnatari);
    }

    public void aggiungiPermesso(PermessoFileVO permesso) {
        if (permesso != null) {
            permessi.add(permesso);
        }
    }

    public void removePermessi() {
        permessi.clear();
    }

    public int getPermessoCorrente() {
        return permessoCorrente;
    }

    public void setPermessoCorrente(int permessoCorrente) {
        this.permessoCorrente = permessoCorrente;
    }

    public void setPermessi(Collection permessi) {
        this.permessi = permessi;
    }
}