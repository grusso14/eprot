package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.documentale.Documento;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.vo.documentale.CartellaVO;
import it.finsiel.siged.mvc.vo.documentale.FileVO;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;

import java.util.Date;
import java.util.Map;

public class DocumentaleBO {

    public static Documento getDefaultDocumento(Utente utente) {

        FileVO fileVo = new FileVO();
        DocumentoVO documentoVO = new DocumentoVO();
        fileVo.setRowCreatedUser(utente.getValueObject().getUsername());
        fileVo.setRowUpdatedUser(utente.getValueObject().getUsername());
        fileVo.setRowCreatedTime(new Date(System.currentTimeMillis()));
        fileVo.setRowUpdatedTime(new Date(System.currentTimeMillis()));
        fileVo.setOwnerId(utente.getValueObject().getId().intValue());
        Documento doc = new Documento();
        doc.setFileVO(fileVo);
        doc.getFileVO().setDocumentoVO(documentoVO);
        return doc;
    }

    /*
     * public static void aggiornaCartelleUtente(HttpServletRequest request,
     * ActionMessages errors) { // imposto le variabili.. HttpSession session =
     * request.getSession(); Utente utente = (Utente)
     * session.getAttribute(Constants.UTENTE_KEY); int cartellaId = -1; if
     * (NumberUtil.isInteger(request.getParameter("cartellaId"))) cartellaId =
     * NumberUtil.getInt(request.getParameter("cartellaId"));
     * 
     * DefaultMutableTreeNode sottoAlbero;
     * 
     * try { DefaultMutableTreeNode albero = DocumentaleDelegate.getInstance()
     * .getAlberoUtente( utente.getValueObject().getId().intValue(),
     * utente.getValueObject().getAooId()); CartellaVO c =
     * DocumentaleDelegate.getInstance()
     * .getCartellaVOByUfficioId(utente.getUfficioInUso()); sottoAlbero =
     * TreeUtil.getSubTree(albero, c.getTreeId()); if (cartellaId >= 0) { //
     * imposta il valore dei nodi da aprire fino a giungere alla // cartella
     * selezionata DefaultMutableTreeNode[] path = (DefaultMutableTreeNode[])
     * TreeUtil .getPathToNode(sottoAlbero, cartellaId);
     * TreeUtil.selectNodes(path); } request.setAttribute("albero_cartelle",
     * sottoAlbero); request.setAttribute("cartellaId", new
     * Integer(cartellaId)); } catch (DataException e) { errors.add("generale",
     * new ActionMessage( "error.load.albero.cartelle")); } }
     */
    public static void putObject(CartellaVO c, Map map) {
        int idx = map.size() + 1;
        c.setIdx(idx);
        map.put(String.valueOf(idx), c);
    }

}