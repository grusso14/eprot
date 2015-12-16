package it.finsiel.siged.mvc.presentation.action.report;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.FileConstants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Organizzazione;
import it.finsiel.siged.model.organizzazione.Ufficio;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.business.ReportProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.report.ReportStatisticheForm;
import it.finsiel.siged.mvc.presentation.helper.ReportType;
import it.finsiel.siged.mvc.presentation.helper.StatisticheView;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.mvc.vo.organizzazione.UtenteVO;
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

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
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

public final class ReportStatisticheAction extends Action {

    static Logger logger = Logger.getLogger(ReportStatisticheAction.class
            .getName());

    private static final String FLAG_PROTOCOLLI_SOSPESI = "S";

    private static final String FLAG_PROTOCOLLI_LAVORAZIONE = "N";

    private static final String FLAG_PROTOCOLLI_ATTI = "A";

    private static final String FLAG_PROTOCOLLI_RISPOSTA = "R";

    private static final String FLAG_PROTOCOLLI_ANNULLATI = "C";

    private static final String FLAG_PROTOCOLLI_RIFIUTATI = "F";

    private static final String FLAG_PROTOCOLLI_ASSOCIATI_PROCEDIMENTO = "P";

    List listaUffici = new ArrayList();

    @SuppressWarnings("unchecked")
	public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        MessageResources messages = getResources(request);
        HttpSession session = request.getSession();

        ReportStatisticheForm reportForm = (ReportStatisticheForm) form;
        session.setAttribute("reportForm", reportForm);
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        reportForm.setVisualizzaDettagli(Boolean.FALSE);
        if (form == null) {
            logger.info(" Creating new ReportStatisticheForm Form");
            form = new ReportStatisticheForm();
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

        } else if (request.getParameter("Cerca") != null) {
            int ufficioSelezionatoId = reportForm.getUfficioCorrenteId();
            Date dataDa = DateUtil.toDate(reportForm.getDataInizio());
            Date dataA = DateUtil.toDate(reportForm.getDataFine());
            reportForm.setStatistiche(statistiche(ufficioSelezionatoId, dataDa,
                    dataA, utente));

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
            common.put("ReportTitle", messages
                    .getMessage("report.title.stampa_protocolli_statistiche"));
            common.put("ReportSubTitle", Organizzazione.getInstance()
                    .getUfficio(reportForm.getUfficioCorrenteId())
                    .getValueObject().getDescription());
            common.put("Ufficio", String.valueOf(reportForm
                    .getUfficioCorrenteId()));
            common.put("print", "true");

            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_HTML, common, messages));
            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_PDF, common, messages));
            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_XLS, common, messages));


        } else if ("true".equals(request.getParameter("print"))) {
            stampaReport(request, response);
            return null;
        }
        AlberoUfficiBO.impostaUfficio(utente, reportForm, true);
        request.setAttribute(mapping.getAttribute(), reportForm);
        return (mapping.findForward("input"));
    }

    @SuppressWarnings("unchecked")
	public void stampaReport(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        ServletContext context = getServlet().getServletContext();
        HttpSession session = request.getSession();
        OutputStream os = response.getOutputStream();

        try {

            File reportFile = new File(
                    context.getRealPath("/")
                            + FileConstants.STAMPA_STATISTICHE_PROTOCOLLI_REPORT_COMPILED);
            // Compile Report only if we are in debug mode
           
            JasperReport jasperReport = (JasperReport) JRLoader
                    .loadObject(reportFile.getPath());

            Map parameters = new HashMap();
            Date dataInizio = DateUtil.toDate(request
                    .getParameter("DataInizio"));
            Date dataFine = DateUtil.toDate(request.getParameter("DataFine"));
            String Ufficio = request.getParameter("Ufficio");
            int UfficioId = Integer.parseInt(Ufficio);

            parameters.put("ReportTitle", request.getParameter("ReportTitle"));
            parameters.put("ReportSubTitle", request
                    .getParameter("ReportSubTitle"));
            parameters.put("DataInizio", DateUtil.formattaData(dataInizio
                    .getTime()));
            parameters.put("DataFine", DateUtil
                    .formattaData(dataFine.getTime()));
            parameters.put("BaseDir", reportFile.getParentFile());

            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);

            Collection c = statistiche(UfficioId, dataInizio, dataFine, utente);

            CommonReportDS ds = new CommonReportDS(c, StatisticheView.class);
            // TODO: Attenzione il report viene immagazinato in memoria,
            // grossi report potrebbero causare
            // problemi, in tali casi predisporre una struttura a Stream che
            // scrive su FS su file
            // temporanei
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, ds);

            String exportFormat = request.getParameter("ReportFormat");
            response
                    .setHeader("Content-Disposition",
                            "attachment;filename=StatisticheProtocolli."
                                    + exportFormat);
            if ("PDF".equals(exportFormat)) {
                response.setContentType("application/pdf");
                JRPdfExporter exporter = new JRPdfExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
            } else if ("XLS".equals(exportFormat)) {
                response.setContentType("application/vnd.ms-excel");
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.setParameter(
                        JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
                        Boolean.FALSE);
                exporter.exportReport();
            } else if ("TXT".equals(exportFormat)) {
            } else if ("XML".equals(exportFormat)) {
                response.setContentType("text/html");
                JRXmlExporter exporter = new JRXmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
            } else if ("CSV".equals(exportFormat)) {
                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.exportReport();
                // default format is HTML
            } else if ("".equals(exportFormat) || "HTML".equals(exportFormat)) {
                Map imagesMap = new HashMap();
                // session.setAttribute("IMAGES_MAP", imagesMap);
                JRHtmlExporter exporter = new JRHtmlExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT,
                        jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
                exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP,
                        imagesMap);
                exporter.setParameter(
                        JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
                        new Boolean(false));
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

    private Collection statistiche(int ufficioSelezionatoId, Date dataDa,
            Date dataA, Utente utente) throws Exception {
        Organizzazione org = Organizzazione.getInstance();
        Collection statistiche = new ArrayList();

        StatisticheView st;
        try {
            listaUffici = new ArrayList();
            Collection uffi = getUffici(ufficioSelezionatoId);
            for (Iterator i = uffi.iterator(); i.hasNext();) {
                UfficioVO uff = (UfficioVO) i.next();
                int ufficioId = uff.getId().intValue();
                st = impostaRigaContatori(ufficioId, null, dataDa, dataA,
                        utente);
                st.setUfficio(uff.getDescription());
                st.setUfficioId(ufficioId);
                statistiche.add(st);
                Ufficio ufficioCorrente = org.getUfficio(ufficioId);
                List list = new ArrayList();
                for (Iterator y = ufficioCorrente.getUtenti().iterator(); y
                        .hasNext();) {
                    Utente ute = (Utente) y.next();
                    list.add(ute.getValueObject());
                }
                Comparator c = new Comparator() {
                    public int compare(Object obj1, Object obj2) {
                        UtenteVO ute1 = (UtenteVO) obj1;
                        UtenteVO ute2 = (UtenteVO) obj2;
                        return ute1.getUsername().compareToIgnoreCase(
                                ute2.getUsername());
                    }
                };
                Collections.sort(list, c);

                for (Iterator z = list.iterator(); z.hasNext();) {
                    UtenteVO ute = (UtenteVO) z.next();
                    st = impostaRigaContatori(ufficioId, ute.getId(), dataDa,
                            dataA, utente);
                    st.setUfficio(uff.getDescription());
                    st.setUfficioId(ufficioId);
                    st.setUtente(ute.getUsername());
                    st.setUtenteId(ute.getId().intValue());
                    statistiche.add(st);
                }
            }

        } catch (Exception e) {
            logger.error("", e);
        }
        return statistiche;
    }

    private StatisticheView impostaRigaContatori(int ufficioId,
            Integer utenteId, Date dataDa, Date dataA, Utente utente)
            throws Exception {

        StatisticheView st = new StatisticheView();
        try {

            ReportProtocolloDelegate rp = ReportProtocolloDelegate
                    .getInstance();
            String totaleProtocolli = rp.getNumeroProtocolli(ufficioId,
                    utenteId, null, null, dataDa, dataA, utente);
            st.setNumProt(totaleProtocolli);
            if (!"0".equals(totaleProtocolli)) {
                st.setNumProtAnnullati(rp.getNumeroProtocolli(ufficioId,
                        utenteId, FLAG_PROTOCOLLI_ANNULLATI, null, dataDa,
                        dataA, utente));
                st.setNumProtAtti(rp.getNumeroProtocolli(ufficioId, utenteId,
                        FLAG_PROTOCOLLI_ATTI, null, dataDa, dataA, utente));
                st.setNumProtLavorazione(rp.getNumeroProtocolli(ufficioId,
                        utenteId, FLAG_PROTOCOLLI_LAVORAZIONE, null, dataDa,
                        dataA, utente));
                st.setNumProtRisposta(rp.getNumeroProtocolli(ufficioId,
                        utenteId, FLAG_PROTOCOLLI_RISPOSTA, null, dataDa,
                        dataA, utente));
                st.setNumProtSospesi(rp.getNumeroProtocolli(ufficioId,
                        utenteId, FLAG_PROTOCOLLI_SOSPESI, null, dataDa, dataA,
                        utente));
                st.setNumProtRifiutati(rp.getNumeroProtocolli(ufficioId,
                        utenteId, FLAG_PROTOCOLLI_RIFIUTATI, null, dataDa,
                        dataA, utente));
                st.setNumProtProcedimento(rp.getNumeroProtocolli(ufficioId,
                        utenteId, FLAG_PROTOCOLLI_ASSOCIATI_PROCEDIMENTO, null,
                        dataDa, dataA, utente));
            }
        } catch (Exception e) {
            logger.error("", e);

        }
        return st;
    }

    private Collection getUffici(int ufficioSelezionatoId) {
        Organizzazione org = Organizzazione.getInstance();
        try {
            if (org.getUfficio(ufficioSelezionatoId) != null
                    && org.getUfficio(ufficioSelezionatoId).getValueObject() != null) {
                // System.out.println("[uff id: "
                // + ufficioSelezionatoId
                // + "] "
                // + org.getUfficio(ufficioSelezionatoId).getValueObject()
                // .getId().intValue()
                // + "-"
                // + org.getUfficio(ufficioSelezionatoId).getValueObject()
                //                                .getDescription());
                listaUffici.add(org.getUfficio(ufficioSelezionatoId)
                        .getValueObject());
                if (org.getUfficio(ufficioSelezionatoId) != null
                        && org.getUfficio(ufficioSelezionatoId)
                                .getUfficiDipendenti() != null) {
                    for (Iterator i = org.getUfficio(ufficioSelezionatoId)
                            .getUfficiDipendenti().iterator(); i.hasNext();) {
                        Ufficio uffCorr = (Ufficio) i.next();
                        if (uffCorr != null && uffCorr.getValueObject() != null) {

                            getUffici((uffCorr).getValueObject().getId()
                                    .intValue());
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    private void dettaglioStatistiche(ReportStatisticheForm reportForm,
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
                else if (FLAG_PROTOCOLLI_ASSOCIATI_PROCEDIMENTO
                        .equals(statoProtocollo))
                    titolo += " - Protocolli associati a Procedimento";
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