package it.finsiel.siged.mvc.presentation.action.report;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.FileConstants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.business.ProtocolloDelegate;
import it.finsiel.siged.mvc.business.ReportProtocolloDelegate;
import it.finsiel.siged.mvc.business.TitoliDestinatarioDelegate;
import it.finsiel.siged.mvc.presentation.actionform.report.ReportEtichetteForm;
import it.finsiel.siged.mvc.presentation.helper.DestinatarioView;
import it.finsiel.siged.mvc.presentation.helper.ReportType;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.protocollo.DestinatarioVO;
import it.finsiel.siged.report.protocollo.CommonReportDS;
import it.finsiel.siged.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.MessageResources;

public final class ReportEtichetteAction extends Action {

    static Logger logger = Logger.getLogger(ReportStatisticheAction.class
            .getName());

    private static final String FLAG_PROTOCOLLI_SOSPESI = "S";

    private static final String FLAG_PROTOCOLLI_LAVORAZIONE = "N";

    private static final String FLAG_PROTOCOLLI_ATTI = "A";

    private static final String FLAG_PROTOCOLLI_RISPOSTA = "R";

    private static final String FLAG_PROTOCOLLI_ANNULLATI = "C";

    private static final String FLAG_PROTOCOLLI_RIFIUTATI = "F";

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();

        ReportEtichetteForm reportForm = (ReportEtichetteForm) form;
        session.setAttribute("reportForm", reportForm);
        Collection destinatari = new ArrayList((Collection) session
                .getAttribute("destinatari"));
        int protocolloId = Integer.parseInt(session
                .getAttribute("protocolloId").toString());
        logger.info("Ci sono " + destinatari.size() + " destinatari.");
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        reportForm.setVisualizzaDettagli(Boolean.FALSE);
        if (form == null) {
            logger.info(" Creating new ReportEtichetteForm Form");
            form = new ReportEtichetteForm();
            request.setAttribute(mapping.getAttribute(), form);
        }
        if (reportForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, reportForm, true);
        }

        if (request.getParameter("selezionaUfficio") != null) {
            reportForm.setUfficioCorrenteId(Integer.parseInt(request
                    .getParameter("selezionaUfficio")));

        } else if (reportForm.getImpostaUfficioAction() != null) {
            reportForm.setImpostaUfficioAction(null);
            reportForm.setUfficioCorrenteId(reportForm
                    .getUfficioSelezionatoId());

        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            AlberoUfficiBO.impostaUfficio(utente, reportForm, true);
            reportForm.setUfficioCorrenteId(reportForm.getUfficioCorrente()
                    .getParentId());

            // } else if (request.getParameter("Cerca") != null) {
            // int ufficioSelezionatoId = reportForm.getUfficioCorrenteId();
            // Date dataDa = DateUtil.toDate(reportForm.getDataInizio());
            // Date dataA = DateUtil.toDate(reportForm.getDataFine());
            // reportForm.setStatistiche(statistiche(ufficioSelezionatoId,
            // dataDa,
            // dataA, utente));

        } else if (request.getParameter("ufficioId") != null) {
            int ufficioId = Integer.parseInt(request.getParameter("ufficioId"));
            Integer utenteId = null;
            if (request.getParameter("utenteId") != null
                    && !"".equals(request.getParameter("utenteId").trim())) {
                utenteId = new Integer(request.getParameter("utenteId"));
            }

            String statoProtocollo = request.getParameter("statoProtocollo");
            reportForm.setDataInizio(request.getParameter("dataInizio"));
            reportForm.setDataFine(request.getParameter("dataFine"));
            dettaglioStatistiche(reportForm, ufficioId, utenteId,
                    statoProtocollo, utente);
            /*
             * AlberoUfficiBO.impostaUfficio(utente, reportForm);
             * request.setAttribute(mapping.getAttribute(), reportForm); return
             * mapping.findForward("dettaglio");
             */
        } else if (request.getParameter("protocolloId") != null) {
            request.setAttribute("protocolloId", new Integer(request
                    .getParameter("protocolloId")));

            return (mapping.findForward("visualizzaProtocolloIngresso"));

        } else if (request.getParameter("stampaEtichetteDestinatari") != null) {
            logger.info("Stampa etichette destinatari");
            reportForm.setReportFormats(new HashMap(1));
            /*
             * int totalRecords = ReportProtocolloDelegate.getInstance()
             * .countProtocolliScaricati(utente,
             * DateUtil.toDate(reportForm.getDataInizio()),
             * DateUtil.toDate(reportForm.getDataFine()),
             * reportForm.getUfficioCorrenteId()); if (totalRecords < 1) {
             * ActionMessages msg = new ActionMessages();
             * msg.add("recordNotFound", new ActionMessage("nessun_dato"));
             * saveMessages(request, msg); return
             * (mapping.findForward("input")); }
             */
            // carica report parameter
            HashMap common = new HashMap();
            common.put("destinatari", destinatari);
            common.put("ReportTitle", messages.getMessage("report.title.stampa_protocolli_statistiche"));
            common.put("ReportSubTitle", Organizzazione.getInstance().getUfficio(reportForm.getUfficioCorrenteId()).getValueObject().getDescription());
            common.put("Ufficio", String.valueOf(reportForm.getUfficioCorrenteId()));
            common.put("print", "true");
            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_PDF, common, messages));
            session.setAttribute("protocolloId", protocolloId + "");

            // remove form from memory
            // session.removeAttribute(mapping.getAttribute());
            // forward to page for print choices

        } else if (reportForm.getBtnStampa() != null) {
            // reportForm.setBtnStampa(null);
            reportForm.setReportFormats(new HashMap(1));
            /*
             * int totalRecords = ReportProtocolloDelegate.getInstance()
             * .countProtocolliScaricati(utente,
             * DateUtil.toDate(reportForm.getDataInizio()),
             * DateUtil.toDate(reportForm.getDataFine()),
             * reportForm.getUfficioCorrenteId()); if (totalRecords < 1) {
             * ActionMessages msg = new ActionMessages();
             * msg.add("recordNotFound", new ActionMessage("nessun_dato"));
             * saveMessages(request, msg); return
             * (mapping.findForward("input")); }
             */
            // carica report parameter
            HashMap common = new HashMap();
            common.put("DataInizio", reportForm.getDataInizio());
            common.put("DataFine", reportForm.getDataFine());
            common.put("ReportTitle", messages.getMessage("report.title.stampa_protocolli_statistiche"));
            common.put("ReportSubTitle", Organizzazione.getInstance().getUfficio(reportForm.getUfficioCorrenteId()).getValueObject().getDescription());
            common.put("Ufficio", String.valueOf(reportForm.getUfficioCorrenteId()));
            common.put("print", "true");

            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_PDF, common, messages));
            

            // remove form from memory
            // session.removeAttribute(mapping.getAttribute());
            // forward to page for print choices
        } else if ("true".equals(request.getParameter("print"))) {
            if (request.getParameter("destinatari") != null) {
                stampaEtichette(request, response);
            }
            // else
            // stampaReport(request, response);
            return null;
        }
        AlberoUfficiBO.impostaUfficio(utente, reportForm, true);
        request.setAttribute(mapping.getAttribute(), reportForm);
        return (mapping.findForward("input"));
    }

    public void stampaEtichette(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        ServletContext context = getServlet().getServletContext();
        HttpSession session = request.getSession();
        OutputStream os = response.getOutputStream();

        try {

            File reportFile = new File(context.getRealPath("/")
                            + FileConstants.STAMPA_ETICHETTE_DESTINATARI_REPORT_COMPILED);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
            Map parameters = new HashMap();
            int protocolloId = Integer.parseInt(session.getAttribute("protocolloId").toString());
            parameters.put("ReportTitle", "");
            parameters.put("ReportSubTitle", "");
            parameters.put("BaseDir", "");

            Collection c = etichette(protocolloId);

            CommonReportDS ds = new CommonReportDS(c, DestinatarioView.class);

            // TODO: Attenzione il report viene immagazinato in memoria,
            // grossi report potrebbero causare
            // problemi, in tali casi predisporre una struttura a Stream che
            // scrive su FS su file
            // temporanei
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);

            String exportFormat = request.getParameter("ReportFormat");
            response.setHeader("Content-Disposition",
                    "attachment;filename=etichettaProtocollo." + exportFormat);
            response.setHeader("Cache-control", "");
            if ("PDF".equals(exportFormat)) {
                response.setContentType("application/pdf");
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
            } else if ("XLS".equals(exportFormat)) {
                response.setContentType("application/vnd.ms-excel");
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,Boolean.FALSE);
                exporter.exportReport();
            } else if ("TXT".equals(exportFormat)) {
            } else if ("XML".equals(exportFormat)) {
                response.setContentType("text/html");
                JRXmlExporter exporter = new JRXmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
            } else if ("CSV".equals(exportFormat)) {
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                // default format is HTML
            } else if ("".equals(exportFormat) || "HTML".equals(exportFormat)) {
                Map imagesMap = new HashMap();
                // session.setAttribute("IMAGES_MAP", imagesMap);
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP,imagesMap);
                exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,new Boolean(false));
                /*
                 * exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI,
                 * "image.jsp?image=");
                 */
                exporter.exportReport();
            }
        } catch (Exception e) {
            logger.error("", e);
            response.setContentType("text/plain");
            e.printStackTrace(new PrintStream(os));
        } finally {
            os.close();
        }

    }

    private Collection etichette(int protocolloId) throws Exception {
        Collection etichette = new ArrayList();

        try {
            for (Iterator i = getDestinatari(protocolloId).iterator(); i
                    .hasNext();) {
                DestinatarioVO dest = (DestinatarioVO) i.next();
                DestinatarioView st = new DestinatarioView();
                TitoliDestinatarioDelegate td = TitoliDestinatarioDelegate
                        .getInstance();
                int titoloId = dest.getTitoloId();
                String titoloDestinatario = td.getTitoloDestinatario(titoloId)
                        .getDescription();
                if (titoloDestinatario == null) {
                    titoloDestinatario = "";
                }
                st.setTitoloDestinatario(titoloDestinatario);
                st.setTitoloId(dest.getTitoloId());
                st.setDestinatario(dest.getDestinatario());
                if (dest.getIndirizzo() == null) {
                    st.setIndirizzo("");
                } else {
                    st.setIndirizzo(dest.getIndirizzo());
                }
                if (dest.getCitta() == null) {
                    st.setCitta("");
                } else {
                    st.setCitta(dest.getCitta());
                }

                if (dest.getCodicePostale() == null) {
                    st.setCapDestinatario("");
                } else {
                    st.setCapDestinatario(dest.getCodicePostale());
                }
                etichette.add(st);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return etichette;
    }

    private Collection destinatari(int protocolloId) throws Exception {
        Collection dest = new ArrayList();
        try {
            dest = ProtocolloDelegate.getInstance().getDestinatariProtocollo(
                    protocolloId).values();
        } catch (Exception e) {
            logger.error("", e);
        }
        return dest;
    }

    private Collection getUffici(int ufficioSelezionatoId) {
        Organizzazione org = Organizzazione.getInstance();
        Ufficio ufficioCorrente = org.getUfficio(ufficioSelezionatoId);
        Collection uffici = ufficioCorrente.getUfficiDipendenti();
        List listaUffici = new ArrayList();
        listaUffici.add(ufficioCorrente.getValueObject());
        for (Iterator i = uffici.iterator(); i.hasNext();) {
            listaUffici.add(((Ufficio) i.next()).getValueObject());
        }
        Comparator c = new Comparator() {
            public int compare(Object obj1, Object obj2) {
                UfficioVO uff1 = (UfficioVO) obj1;
                UfficioVO uff2 = (UfficioVO) obj2;
                return uff1.getDescription().compareToIgnoreCase(
                        uff2.getDescription());
            }
        };
        Collections.sort(listaUffici, c);
        return listaUffici;
    }

    private Collection getDestinatari(int protocolloId) {
        Map dest = ProtocolloDelegate.getInstance().getDestinatariProtocollo(
                protocolloId);
        Collection destinatari = dest.values();
        return destinatari;
    }

    private void dettaglioStatistiche(ReportEtichetteForm reportForm,
            int ufficioId, Integer utenteId, String statoProtocollo,
            Utente utente) throws Exception {

        try {
            Date dataDa = DateUtil.toDate(reportForm.getDataInizio());
            Date dataA = DateUtil.toDate(reportForm.getDataFine());
            Organizzazione org = Organizzazione.getInstance();
            ReportProtocolloDelegate rp = ReportProtocolloDelegate
                    .getInstance();
            String titolo = "";
            titolo += "Ufficio: "
                    + org.getUfficio(ufficioId).getValueObject()
                            .getDescription();
            if (utenteId.intValue() > 0) {
                titolo += " Utente:"
                        + org.getUtente(utenteId.intValue()).getValueObject()
                                .getFullName();
            }
            if (statoProtocollo == null) {
                reportForm.setDettaglioStatistiche(rp
                        .getDettaglioStatisticheProtocolli(ufficioId, utenteId,
                                null, null, dataDa, dataA, utente));
            } else {
                if (FLAG_PROTOCOLLI_SOSPESI.equals(statoProtocollo))
                    titolo += " - Protocolli Sospesi";
                else if (FLAG_PROTOCOLLI_RISPOSTA.equals(statoProtocollo))
                    titolo += " - Protocolli in Risposta";
                else if (FLAG_PROTOCOLLI_LAVORAZIONE.equals(statoProtocollo))
                    titolo += " - Protocolli in Lavorazione";
                else if (FLAG_PROTOCOLLI_ATTI.equals(statoProtocollo))
                    titolo += " - Protocolli agli Atti";
                else if (FLAG_PROTOCOLLI_ANNULLATI.equals(statoProtocollo))
                    titolo += " - Protocolli Annullati";
                else if (FLAG_PROTOCOLLI_RIFIUTATI.equals(statoProtocollo))
                    titolo += " - Protocolli Rifiutati";
                reportForm.setDettaglioStatistiche(rp
                        .getDettaglioStatisticheProtocolli(ufficioId, utenteId,
                                statoProtocollo, null, dataDa, dataA, utente));

            }
            reportForm.setTitoloDettaglioStatistiche(titolo);
            reportForm.setVisualizzaDettagli(Boolean.TRUE);
        } catch (Exception e) {
            logger.error("", e);

        }

    }

}