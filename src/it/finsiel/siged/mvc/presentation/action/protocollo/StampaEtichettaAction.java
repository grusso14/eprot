package it.finsiel.siged.mvc.presentation.action.protocollo;

import it.finsiel.siged.constant.Constants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.model.organizzazione.Utente;
import it.finsiel.siged.mvc.business.ConfigurazioneUtenteDelegate;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloForm;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.StampaEtichettaForm;
import it.finsiel.siged.mvc.vo.IdentityVO;
import it.finsiel.siged.mvc.vo.protocollo.ConfigurazioneUtenteVO;
import it.finsiel.siged.util.StringUtil;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.Globals;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.util.MessageResources;

public class StampaEtichettaAction extends Action {

    static Logger logger = Logger.getLogger(StampaEtichettaAction.class
            .getName());

    final String IN_ALTO_DX = "In alto a destra";

    final String IN_ALTO_SX = "In alto a sinistra";

    final String IN_BASSO_DX = "In basso a destra";

    final String IN_BASSO_SX = "In basso a sinistra";

    final String STAMPA_FOGLIO_A4 = "S";

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession(true);
        StampaEtichettaForm etichettaForm = (StampaEtichettaForm) form;
        ActionMessages errors = new ActionMessages();
        ProtocolloForm pForm = (ProtocolloForm) session
                .getAttribute("protocolloForm");
        IdentityVO identityVO;
        Collection modalitaStampaA4 = new ArrayList();
        identityVO = new IdentityVO("0", IN_ALTO_DX);
        modalitaStampaA4.add(identityVO);
        identityVO = new IdentityVO("1", IN_ALTO_SX);
        modalitaStampaA4.add(identityVO);
        identityVO = new IdentityVO("2", IN_BASSO_DX);
        modalitaStampaA4.add(identityVO);
        identityVO = new IdentityVO("3", IN_BASSO_SX);
        modalitaStampaA4.add(identityVO);
        etichettaForm.setModalitaStampaA4(modalitaStampaA4);

        if (request.getParameter("btnImpostaParametriStampa") != null) {
            errors = etichettaForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            } else {
                impostaOffSet(request, etichettaForm);

            }
//            return mapping.getInputForward();

        } else if (request.getParameter("btnSalvaConfigurazione") != null) {
            ConfigurazioneUtenteDelegate cd = ConfigurazioneUtenteDelegate
                    .getInstance();
            Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
            int utenteId = utente.getValueObject().getId().intValue();
            errors = etichettaForm.validate(mapping, request);
            if (!errors.isEmpty()) {
                saveErrors(request, errors);
            } else {
                ConfigurazioneUtenteVO configurazioneUtenteVO = cd
                        .getConfigurazione(utenteId);
                impostaOffSet(request, etichettaForm);
                if (configurazioneUtenteVO != null
                        && configurazioneUtenteVO.getReturnValue() == ReturnValues.FOUND) {
                    impostaParametriStampaVO(configurazioneUtenteVO,
                            etichettaForm);
                    configurazioneUtenteVO = cd
                            .aggiornaParametriStampante(configurazioneUtenteVO);
                } else {
                    impostaConfigurazioneVO(configurazioneUtenteVO,
                            etichettaForm);
                    configurazioneUtenteVO = cd.salvaConfigurazione(
                            configurazioneUtenteVO, utenteId);
                }

                if (configurazioneUtenteVO.getReturnValue() == ReturnValues.FOUND) {
                    errors.add("CONFIGURAZIONE_UTENTE", new ActionMessage(
                            "operazione_ok", "", ""));
                } else {
                    errors.add("CONFIGURAZIONE_UTENTE", new ActionMessage(
                            "errore_nel_salvataggio", "", ""));
                }

                session.setAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO",
                        configurazioneUtenteVO);

            }
        } else if (request.getParameter("btnAnnullaStampa") != null) {

            if ("I".equals(pForm.getFlagTipo())) {
                return (mapping.findForward("visualizzaProtocolloIngresso"));
            } else if ("U".equals(pForm.getFlagTipo())) {
                return (mapping.findForward("visualizzaProtocolloUscita"));
            }

        }
        impostaForm(etichettaForm, request, pForm.getProtocolloId());
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }
        logger.info("Execute StampaEtichettaAction");
        return (mapping.findForward("input"));

    }

    // public void stampaReport(HttpServletRequest request,
    // HttpServletResponse response, ProtocolloForm pForm,
    // StampaEtichettaForm etichettaForm) throws IOException,
    // ServletException {
    // ServletContext context = getServlet().getServletContext();
    // OutputStream os = response.getOutputStream();
    // try {
    // File reportFile = new File(context.getRealPath("/")
    // + FileConstants.STAMPA_ETICHETTA_PROTOCOLLO_REPORT_COMPILED);
    //
    // JasperReport jasperReport = (JasperReport) JRLoader
    // .loadObject(reportFile.getPath());
    // jasperReport.setProperty("leftMargin", String.valueOf(50));
    // Map parameters = new HashMap();
    // // impostazione dei margini sinistro e superiore
    // parameters.put("ProtocolloId", StringUtil.formattaNumeroProtocollo(
    // String.valueOf(pForm.getProtocolloId()), 12));
    // parameters.put("AOO", Organizzazione.getInstance()
    // .getAreaOrganizzativa(pForm.getAooId()).getValueObject()
    // .getCodi_aoo());
    //
    // parameters.put("ImgBarcode", getImage(StringUtil
    // .formattaNumeroProtocollo(String.valueOf(pForm
    // .getProtocolloId()), 12)));
    // if ("I".equals(pForm.getFlagTipo())) {
    // parameters.put("DataProtocollo", "Ingresso del "
    // + pForm.getDataRegistrazione());
    // } else if ("U".equals(pForm.getFlagTipo())) {
    // parameters.put("DataProtocollo", "Uscita del "
    // + pForm.getDataRegistrazione());
    // }
    //
    // parameters.put("NumeroProtocollo", StringUtil
    // .formattaNumeroProtocollo(pForm.getNumeroProtocollo(), 7));
    //
    // if (pForm.getTitolario() != null) {
    // parameters.put("Titolario", pForm.getTitolario().getCodice());
    // }
    //
    // Collection c = new ArrayList();
    // c.add("");
    // CommonReportDS ds = new CommonReportDS(c);
    //
    // JasperPrint jasperPrint = JasperFillManager.fillReport(
    // jasperReport, parameters, ds);
    // response.setHeader("Content-Disposition",
    // "attachment;filename=report");
    //
    // response.setContentType("application/pdf");
    // JRPdfExporter exporter = new JRPdfExporter();
    // exporter
    // .setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
    // exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
    // MessageResources bundle = (MessageResources) request
    // .getAttribute(Globals.MESSAGES_KEY);
    //
    // // in alto a dx
    // if (etichettaForm.getModoStampaA4().equals("0")) {
    // exporter
    // .setParameter(JRExporterParameter.OFFSET_X,
    // new Integer((String) bundle.getMessage(
    // "protocollo.stampa.etichette.OFFSET.X")
    // .trim()));
    // exporter.setParameter(JRExporterParameter.OFFSET_Y,
    // new Integer((String) bundle.getMessage(
    // "protocollo.stampa.etichette.OFFSET").trim()));
    // // in alto a sx
    // } else if (etichettaForm.getModoStampaA4().equals("1")) {
    // exporter.setParameter(JRExporterParameter.OFFSET_X,
    // new Integer((String) bundle.getMessage(
    // "protocollo.stampa.etichette.OFFSET").trim()));
    // exporter.setParameter(JRExporterParameter.OFFSET_Y,
    // new Integer((String) bundle.getMessage(
    // "protocollo.stampa.etichette.OFFSET").trim()));
    //
    // // in basso a dx
    // } else if (etichettaForm.getModoStampaA4().equals("2")) {
    // exporter
    // .setParameter(
    // JRExporterParameter.OFFSET_X,
    // new Integer(
    // (String) bundle
    // .getMessage("protocollo.stampa.etichette.OFFSET.X")));
    // exporter
    // .setParameter(
    // JRExporterParameter.OFFSET_Y,
    // new Integer(
    // (String) bundle
    // .getMessage("protocollo.stampa.etichette.OFFSET.Y")));
    //
    // // in basso a sx
    // } else if (etichettaForm.getModoStampaA4().equals("3")) {
    // exporter.setParameter(JRExporterParameter.OFFSET_X,
    // new Integer((String) bundle.getMessage(
    // "protocollo.stampa.etichette.OFFSET").trim()));
    // exporter
    // .setParameter(JRExporterParameter.OFFSET_Y,
    // new Integer((String) bundle.getMessage(
    // "protocollo.stampa.etichette.OFFSET.Y")
    // .trim()));
    //
    // }
    // exporter.exportReport();
    // } catch (Exception e) {
    // logger.debug("Errore di compilazione", e);
    // response.setContentType("text/plain");
    // e.printStackTrace(new PrintStream(os));
    // } finally {
    // os.close();
    // }
    //
    // }
    //
    // public void compile(ServletContext context) throws JRException {
    // System.setProperty("jasper.reports.compile.class.path", context
    // .getRealPath("/WEB-INF/lib/jasperreports-0.6.6.jar")
    // + System.getProperty("path.separator")
    // + context.getRealPath("/WEB-INF/classes/"));
    //
    // System.setProperty("jasper.reports.compile.temp", context
    // .getRealPath("/")
    // + "/WEB-INF/reports/");
    // JasperCompileManager.compileReportToFile(context.getRealPath("/")
    // + FileConstants.STAMPA_ETICHETTA_PROTOCOLLO_REPORT_TEMPLATE);
    //
    // }
    //
    // private Image getImage(String code) {
    //
    // Image image = null;
    // logger.info(code);
    // try {
    // // Create the barcode bean
    // EAN13Bean bean = new EAN13Bean();
    // int dpi = FileConstants.BARCODE_DPI;
    // double height = FileConstants.BARCODE_HEIGHT;
    // // Configure the barcode generator
    // bean.setModuleWidth(UnitConv.in2mm(1.5f / dpi)); // makes the
    // // narrow
    // // bar
    // bean.setBarHeight(height);
    // bean.setFontName("Courier");// request.getParameter("font"));
    // bean.setFontSize(2.48);//
    // Double.parseDouble(request.getParameter("size")));
    // bean.doQuietZone(true);
    // bean.setQuietZone(5);
    // bean.setChecksumMode(ChecksumMode.CP_ADD);// aggiunge l'ultima
    // // cifra
    // try {
    // // Set up the canvas provider for monochrome JPEG output
    // BitmapCanvasProvider canvas = new BitmapCanvasProvider(dpi,
    // BufferedImage.TYPE_BYTE_BINARY, false);
    //
    // // Generate the barcode
    // bean.generateBarcode(canvas, code);
    //
    // image = canvas.getBufferedImage().getScaledInstance(-1, -1,
    // Image.SCALE_DEFAULT);
    //
    // // Signal end of generation
    // canvas.finish();
    // } finally {
    //
    // }
    // } catch (Exception e) {
    // logger.error("", e);
    // }
    // return image;
    // }

    private void impostaForm(StampaEtichettaForm etichettaForm,
            HttpServletRequest request, int protocolloId) {

        HttpSession session = request.getSession(true);
        Utente utente = (Utente) session.getAttribute(Constants.UTENTE_KEY);
        String codiceDipartimento = utente.getAreaOrganizzativa()
                .getDipartimento_codice();
        String barCode = (codiceDipartimento == null ? "000" : StringUtil
                .formattaNumeroProtocollo(codiceDipartimento, 3))
                + StringUtil.formattaNumeroProtocollo(String
                        .valueOf(protocolloId), 9);
        etichettaForm.setBarCode(barCode);

        ConfigurazioneUtenteVO configurazioneVO = null;

        if (session.getAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO") == null) {
            configurazioneVO = ConfigurazioneUtenteDelegate.getInstance()
                    .getConfigurazione(
                            utente.getUfficioVOInUso().getId().intValue());
            session.setAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO",
                    configurazioneVO);
        } else {
            configurazioneVO = (ConfigurazioneUtenteVO) session
                    .getAttribute("CONFIGURAZIONE_UTENTE_PROTOCOLLO");
        }

        if (configurazioneVO != null) {
            String parametriStampante = configurazioneVO
                    .getParametriStampante();
            impostaParametriStampante(request, etichettaForm,
                    parametriStampante);
        } else {

        }

    }

    private void impostaFoglioA4() {

    }

    private void impostaParametriStampante(HttpServletRequest request,
            StampaEtichettaForm etichettaForm, String parametri) {

        if (parametri != null) {
            String[] strings = parametri.split(";");
            if (strings != null && strings.length == 9) {
                etichettaForm.setMargineSinistro(strings[0]);
                etichettaForm.setMargineSuperiore(strings[1]);
                etichettaForm.setLarghezzaEtichetta(strings[2]);
                etichettaForm.setAltezzaEtichetta(strings[3]);
                etichettaForm.setTipoStampa(strings[4]);
                etichettaForm.setModoStampaA4(strings[5]);
                etichettaForm.setRotazione(strings[6]);
                etichettaForm.setDeltaXMM(Integer.parseInt(strings[7]));
                etichettaForm.setDeltaYMM(Integer.parseInt(strings[8]));
            }
        } else {
            MessageResources bundle = (MessageResources) request
                    .getAttribute(Globals.MESSAGES_KEY);

            etichettaForm.setModoStampaA4("1");
            etichettaForm.setTipoStampa("N");
            etichettaForm.setAltezzaEtichetta(bundle
                    .getMessage("protocollo.stampa.etichette.altezza"));
            etichettaForm.setLarghezzaEtichetta(bundle
                    .getMessage("protocollo.stampa.etichette.larghezza"));
            etichettaForm.setMargineSinistro(bundle
                    .getMessage("protocollo.stampa.etichette.margine.sx"));
            etichettaForm
                    .setMargineSuperiore(bundle
                            .getMessage("protocollo.stampa.etichette.margine.superiore"));
            impostaOffSet(request, etichettaForm);
        }
    }

    public void impostaConfigurazioneVO(ConfigurazioneUtenteVO vo,
            StampaEtichettaForm form) {
        vo.setOggetto(null);
        vo.setDataDocumento(null);
        vo.setDestinatario(null);
        vo.setMittente(null);
        vo.setTipoDocumentoId(0);
        vo.setTipoDocumentoId(0);
        vo.setTipoMittente("F");
        vo.setTitolario(0);
        vo.setTitolarioId(0);
        vo.setCheckAssegnatari(null);
        vo.setCheckDataDocumento(null);
        vo.setCheckDestinatari(null);
        vo.setCheckMittente(null);
        vo.setCheckOggetto(null);
        vo.setCheckRicevutoIl(null);
        vo.setCheckTipoDocumento(null);
        vo.setCheckTipoMittente(null);
        vo.setCheckTitolario(null);
        impostaParametriStampaVO(vo, form);

    }

    private void impostaOffSet(HttpServletRequest request,
            StampaEtichettaForm etichettaForm) {
        MessageResources bundle = (MessageResources) request
                .getAttribute(Globals.MESSAGES_KEY);

        if (STAMPA_FOGLIO_A4.equals(etichettaForm.getTipoStampa())) {
            if (etichettaForm.getModoStampaA4().equals("0")) {
                etichettaForm.setDeltaXMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET.X")
                        .trim()));
                etichettaForm.setDeltaYMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET")
                        .trim()));
                // in alto a sx
            } else if (etichettaForm.getModoStampaA4().equals("1")) {
                etichettaForm.setDeltaXMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET")
                        .trim()));
                etichettaForm.setDeltaYMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET")
                        .trim()));
                // in basso a dx
            } else if (etichettaForm.getModoStampaA4().equals("2")) {
                etichettaForm.setDeltaXMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET.X")
                        .trim()));
                etichettaForm.setDeltaYMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET.Y")
                        .trim()));

                // in basso a sx
            } else if (etichettaForm.getModoStampaA4().equals("3")) {
                etichettaForm.setDeltaXMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET")
                        .trim()));
                etichettaForm.setDeltaYMM(Integer.parseInt((String) bundle
                        .getMessage("protocollo.stampa.etichette.OFFSET.Y")
                        .trim()));
            }
        } else {
            etichettaForm.setDeltaXMM(0);
            etichettaForm.setDeltaYMM(0);
        }

    }

    public void impostaParametriStampaVO(
            ConfigurazioneUtenteVO configurazioneVO, StampaEtichettaForm form) {
        StringBuffer parametriStampante = new StringBuffer("");
        parametriStampante.append(form.getMargineSinistro()).append(";");
        parametriStampante.append(form.getMargineSuperiore()).append(";");
        parametriStampante.append(form.getLarghezzaEtichetta()).append(";");
        parametriStampante.append(form.getAltezzaEtichetta()).append(";");
        parametriStampante.append(form.getTipoStampa()).append(";");
        parametriStampante.append(form.getModoStampaA4()).append(";");
        parametriStampante.append(form.getRotazione()).append(";");
        parametriStampante.append(form.getDeltaXMM()).append(";");
        parametriStampante.append(form.getDeltaYMM());

        configurazioneVO.setParametriStampante(parametriStampante.toString());

    }

}
