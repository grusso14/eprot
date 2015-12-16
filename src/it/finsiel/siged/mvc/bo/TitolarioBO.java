/*
 * Created on 25-feb-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package it.finsiel.siged.mvc.bo;

import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.mvc.business.TitolarioDelegate;
import it.finsiel.siged.mvc.presentation.actionform.amministrazione.TitolarioForm;
import it.finsiel.siged.mvc.presentation.actionform.documentale.DocumentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ConfigurazioneUtenteForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.FaldoneForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.FascicoloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProcedimentoForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaEvidenzaForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaFaldoneForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaFascicoliForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.RicercaProcedimentoForm;
import it.finsiel.siged.mvc.vo.lookup.TitolarioVO;

/**
 * @author Calli
 * 
 * 
 */
public class TitolarioBO {

    public static void impostaTitolario(ProtocolloForm form, int ufficioId,
            int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(RicercaForm form, int ufficioId,
            int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(ProcedimentoForm form, int ufficioId,
            int titolarioId) {
        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(FaldoneForm form, int ufficioId,
            int titolarioId) {
        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(RicercaFaldoneForm form, int ufficioId,
            int titolarioId) {
        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(RicercaProcedimentoForm form,
            int ufficioId, int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }
    
    public static void impostaTitolario(RicercaEvidenzaForm form,
            int ufficioId, int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(TitolarioForm form, int ufficioId,
            int titolarioId) {
        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));

    }

    public static void impostaTitolario(FascicoloForm form, int ufficioId,
            int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(DocumentoForm form, int ufficioId,
            int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(RicercaFascicoliForm form,
            int ufficioId, int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static void impostaTitolario(ConfigurazioneUtenteForm form,
            int ufficioId, int titolarioId) {

        TitolarioDelegate td = TitolarioDelegate.getInstance();
        TitolarioVO tVO = td.getTitolario(ufficioId, titolarioId,
                getAooId(ufficioId));
        form.setTitolario(tVO);
        if (tVO != null) {
            form.getTitolario().setDescrizione(td.getPathName(tVO));
        }
        form.setTitolariFigli(td.getTitolariByParent(ufficioId, titolarioId,
                getAooId(ufficioId)));
    }

    public static String getPathDescrizioneTitolario(int titolarioId) {

        if (titolarioId > 0) {
            TitolarioDelegate td = TitolarioDelegate.getInstance();
            TitolarioVO tVO = td.getTitolario(titolarioId);
            if (tVO != null) {
                return td.getPathName(tVO);
            }
        }
        return null;

    }

    private static int getAooId(int ufficioId) {
        Organizzazione org = Organizzazione.getInstance();
        return org.getUfficio(ufficioId).getValueObject().getAooId();

    }
}