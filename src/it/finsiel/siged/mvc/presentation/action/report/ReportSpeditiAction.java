package it.finsiel.siged.mvc.presentation.action.report;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.FileConstants;
import it.finsiel.siged.constant.Parametri;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.bo.AlberoUfficiBO;
import it.finsiel.siged.mvc.business.AmministrazioneDelegate;
import it.finsiel.siged.mvc.business.ReportProtocolloDelegate;
import it.finsiel.siged.mvc.presentation.actionform.report.ReportSpeditiForm;
import it.finsiel.siged.mvc.presentation.helper.ReportType;
import it.finsiel.siged.mvc.vo.organizzazione.UfficioVO;
import it.finsiel.siged.report.protocollo.CommonReportDS;
import it.finsiel.siged.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
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
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

public final class ReportSpeditiAction extends Action {

    static Logger logger = Logger
            .getLogger(ReportSpeditiAction.class.getName());

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        MessageResources messages = getResources(request);
        MessageResources bundle = (MessageResources) request
                .getAttribute(Globals.MESSAGES_KEY);
        ActionMessages errors = new ActionMessages();
        HttpSession session = request.getSession();

        ReportSpeditiForm reportForm = (ReportSpeditiForm) form;
        // reportForm.inizializza();
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        boolean ufficioCompleto = (utente.getUfficioVOInUso().getTipo()
                .equals(UfficioVO.UFFICIO_CENTRALE));
        session.setAttribute("reportForm", reportForm);
        if (form == null) {
            logger.info(" Creating new ReportSpeditiForm Form");
            form = new ReportSpeditiForm();
            request.setAttribute(mapping.getAttribute(), form);
        }

        if (reportForm.getUfficioCorrenteId() == 0) {
            AlberoUfficiBO.impostaUfficio(utente, reportForm, ufficioCompleto);
        }

        if (request.getParameter("selezionaUfficio") != null) {
            reportForm.setUfficioCorrenteId(Integer.parseInt(request
                    .getParameter("selezionaUfficio")));

        } else if (reportForm.getImpostaUfficioAction() != null) {
            reportForm.setImpostaUfficioAction(null);
            reportForm.setUfficioCorrenteId(reportForm
                    .getUfficioSelezionatoId());

        } else if (request.getParameter("ufficioPrecedenteAction") != null) {
            AlberoUfficiBO.impostaUfficio(utente, reportForm, ufficioCompleto);
            reportForm.setUfficioCorrenteId(reportForm.getUfficioCorrente()
                    .getParentId());
        } else if (reportForm.getBtnStampa() != null) {
            reportForm.setBtnStampa(null);
            reportForm.setReportFormats(new HashMap(1));
            AlberoUfficiBO.impostaUfficio(utente, reportForm, ufficioCompleto);
            // mimmo
            reportForm.setMezziSpedizione(AmministrazioneDelegate.getInstance()
                    .getMezziSpedizione("",
                            utente.getRegistroVOInUso().getAooId()));

            errors = reportForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
                return (mapping.findForward("edit"));
            }
            int maxRighe = Integer.parseInt(bundle
                    .getMessage("report.max.righe.lista"));
            int totalRecords = ReportProtocolloDelegate
                    .getInstance()
                    .countProtocolliSpediti(utente,
                            DateUtil.toDate(reportForm.getDataInizio()),
                            DateUtil.toDate(reportForm.getDataFine()),
                            reportForm.getUfficioCorrenteId(),
                            reportForm.getMezzoSpedizione(), reportForm.getId());
            if (totalRecords < 1) {
                ActionMessages msg = new ActionMessages();
                msg.add("recordNotFound", new ActionMessage("nessun_dato"));
                saveMessages(request, msg);
                return (mapping.findForward("input"));
            }

            if (totalRecords > maxRighe) {
                errors.add("controllo.maxrighe", new ActionMessage(
                        "controllo.maxrighe", "" + totalRecords,
                        "registro protocolli spediti", "" + maxRighe));
                if (!errors.isEmpty()) {
                    saveErrors(request, errors);
                }
                return (mapping.findForward("input"));
            }
            logger.info("Ci sono " + totalRecords + " protocolli spediti.");
            // carica report parameter
            HashMap common = new HashMap();
            common.put("DataInizio", reportForm.getDataInizio());
            common.put("DataFine", reportForm.getDataFine());
            common.put("MezzoSpedizione", reportForm.getMezzoSpedizione());
            common.put("ReportTitle", messages
                    .getMessage("report.title.stampa_protocolli_spediti"));
            common.put("ReportSubTitle", reportForm.getUfficioCorrentePath()
                    .substring(0,
                            reportForm.getUfficioCorrentePath().length() - 1));
            common.put("Assegnatario", String.valueOf(reportForm
                    .getUfficioCorrenteId()));
            common.put("MezzoSpedizioneId", String.valueOf(reportForm.getId()));
            common.put("print", "true");
            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_HTML, common, messages));
            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_PDF, common, messages));
            reportForm.addReportType(ReportType.getIstanceByType(Parametri.FORMATO_XLS, common, messages));

            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            }
            return (mapping.findForward("input"));
        } else if ("true".equals(request.getParameter("print"))) {
            stampaReport(request, response);
            return null;
        }
        reportForm
                .setMezziSpedizione(AmministrazioneDelegate.getInstance()
                        .getMezziSpedizione("",
                                utente.getRegistroVOInUso().getAooId()));

        AlberoUfficiBO.impostaUfficio(utente, reportForm, ufficioCompleto);
        request.setAttribute(mapping.getAttribute(), reportForm);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        return (mapping.findForward("input"));
    }

    public void stampaReport(HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        ServletContext context = getServlet().getServletContext();
        HttpSession session = request.getSession();
        OutputStream os = response.getOutputStream();

        try {

            File reportFile = new File(context.getRealPath("/")
                    + FileConstants.STAMPA_PROTOCOLLI_SPEDITI_REPORT_COMPILED);
          

            JasperReport jasperReport = (JasperReport) JRLoader
                    .loadObject(reportFile.getPath());

            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            Date dataInizio = DateUtil.toDate(request
                    .getParameter("DataInizio"));
            Date dataFine = DateUtil.toDate(request.getParameter("DataFine"));
            String ufficio = request.getParameter("Assegnatario");
            String mezzoSpedizione = request.getParameter("MezzoSpedizione");
            int ufficioId = Integer.parseInt(ufficio);
            int mezzoSpedizioneId = Integer.parseInt(request
                    .getParameter("MezzoSpedizione"));

            Map parameters = new HashMap();
            parameters.put("ReportTitle", request.getParameter("ReportTitle"));
            parameters.put("ReportSubTitle", request
                    .getParameter("ReportSubTitle"));
            parameters.put("BaseDir", reportFile.getParentFile());
            if (dataInizio != null)
                parameters.put("DataInizio", DateUtil.formattaData(dataInizio
                        .getTime()));
            if (dataFine != null)
                parameters.put("DataFine", DateUtil.formattaData(dataFine
                        .getTime()));

            Collection c = ReportProtocolloDelegate.getInstance()
                    .stampaProtocolliSpediti(utente, dataInizio, dataFine,
                            ufficioId, mezzoSpedizione, mezzoSpedizioneId);// TODO:assegnatario
            CommonReportDS ds = new CommonReportDS(c);
            // TODO: Attenzione il report viene immagazinato in memoria,
            // grossi report potrebbero causare
            // problemi, in tali casi predisporre una struttura a Stream che
            // scrive su FS su file
            // temporanei
            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport, parameters, ds);

            String exportFormat = request.getParameter("ReportFormat");
            response.setHeader("Content-Disposition",
                    "attachment;filename=protocolliSpediti." + exportFormat);
            response.setHeader("Cache-control", "");
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
            logger.debug("Errore di compilazione", e);
            response.setContentType("text/plain");
            e.printStackTrace(new PrintStream(os));
        } finally {
            os.close();
        }

    }

    
}