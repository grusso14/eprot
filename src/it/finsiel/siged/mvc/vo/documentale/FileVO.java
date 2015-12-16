package it.finsiel.siged.mvc.vo.documentale;

import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.mvc.vo.VersioneVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.finsiel.siged.mvc.vo.protocollo.FascicoloVO;

import java.util.Collection;
import java.util.Date;

/**
 * @author Almaviva sud
 * 
 */
public class FileVO extends VersioneVO {

    private int repositoryFileId;

    private int cartellaId;

    private String nomeFile;

    private DocumentoVO documentoVO;

    private Date dataDocumento;
    
    private String dataDocumentoC;

    private String oggetto;

    private String note;

    private String descrizione;

    private String descrizioneArgomento;

    private int tipoDocumentoId;

    private int titolarioId;

    private String statoDocumento = String.valueOf(Parametri.CHECKED_IN);

    private String statoArchivio = String.valueOf(Parametri.STATO_LAVORAZIONE);

    private int userLavId;

    private String usernameLav;

    private int ownerId;
    
    private String owner;
    
    private String assegnatoDa;
    
    private String assegnatario;
    
    private String messaggio;

    private Collection fascicoli;

    private FascicoloVO fascicoloVO;

    public FileVO() {
    }

    public String getUsernameLav() {
        return usernameLav;
    }

    public void setUsernameLav(String usernameLav) {
        this.usernameLav = usernameLav;
    }

    public int getCartellaId() {
        return cartellaId;
    }

    public void setCartellaId(int cartellaId) {
        this.cartellaId = cartellaId;
    }

    public Date getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizioneArgomento() {
        return descrizioneArgomento;
    }

    public void setDescrizioneArgomento(String descrizioneArgomento) {
        this.descrizioneArgomento = descrizioneArgomento;
    }

    public DocumentoVO getDocumentoVO() {
        return documentoVO;
    }

    public void setDocumentoVO(DocumentoVO documento) {
        this.documentoVO = documento;
    }

    public String getNomeFile() {
        return nomeFile;
    }

    public void setNomeFile(String nomeFile) {
        this.nomeFile = nomeFile;
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

    public String getStatoArchivio() {
        return statoArchivio;
    }

    public void setStatoArchivio(String statoArc) {
        this.statoArchivio = statoArc;
    }

    public String getStatoDocumento() {
        return statoDocumento;
    }

    public void setStatoDocumento(String statoDocumento) {
        this.statoDocumento = statoDocumento;
    }

    public int getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public void setTipoDocumentoId(int tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }

    public int getTitolarioId() {
        return titolarioId;
    }

    public void setTitolarioId(int titolarioId) {
        this.titolarioId = titolarioId;
    }

    public int getUserLavId() {
        return userLavId;
    }

    public void setUserLavId(int userLavId) {
        this.userLavId = userLavId;
    }

    public Collection getFascicoli() {
        return fascicoli;
    }

    public void setFascicoli(Collection fascicoli) {
        this.fascicoli = fascicoli;
    }

    public FascicoloVO getFascicoloVO() {
        return fascicoloVO;
    }

    public void setFascicoloVO(FascicoloVO fascicoloVO) {
        this.fascicoloVO = fascicoloVO;
    }

    public int getRepositoryFileId() {
        return repositoryFileId;
    }

    public void setRepositoryFileId(int repositoryFileId) {
        this.repositoryFileId = repositoryFileId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getAssegnatario() {
        return assegnatario;
    }

    public void setAssegnatario(String assegnatario) {
        this.assegnatario = assegnatario;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssegnatoDa() {
        return assegnatoDa;
    }

    public void setAssegnatoDa(String assegnatoDa) {
        this.assegnatoDa = assegnatoDa;
    }

    public String getDataDocumentoC() {
        return dataDocumentoC;
    }

    public void setDataDocumentoC(String dataDocumentoC) {
        this.dataDocumentoC = dataDocumentoC;
    }
}