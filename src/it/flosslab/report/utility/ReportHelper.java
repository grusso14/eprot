/*
*
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
*
* This file is part of e-prot 1.1 software.
* e-prot 1.1 is a free software; 
* you can redistribute it and/or modify it
* under the terms of the GNU General Public License (GPL) as published by the Free Software Foundation; version 2.
* 
* See the file License for the specific language governing permissions   
* and limitations under the License
* 
* 
* 
* Copyright 2008 Flosslab s.r.l. (http://www.flosslab.it) All rights reserved.
* Version: e-prot 1.1
*/
package it.flosslab.report.utility;

import it.finsiel.siged.constant.FileConstants;
import it.finsiel.siged.mvc.presentation.actionform.protocollo.ProtocolloIngressoForm;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.struts.action.ActionForm;

/**
 * @author roberto onnis
 *
 */
public class ReportHelper {

	 public static final String STAMPA_RICEVUTA_PROTOCOLLO_COMPILED = "/WEB-INF/reports/StampaRicevuta.jasper";
	 private static final String LIBRERIA_JASPER_REPORT = "jasperreports-3.0.0.jar";

	 
	 public static void reportToOutputStream(ActionForm form,
				HttpServletResponse response, OutputStream os, File reportFile)
				throws JRException {
			JasperPrint jasperPrint = getJasperPrint(form, reportFile);
			response.setContentType("application/pdf");
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT,jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, os);
			exporter.exportReport();
		}


	/**
	 * @param form
	 * @param reportFile
	 * @return
	 * @throws JRException
	 */
	public static JasperPrint getJasperPrint(ActionForm form, File reportFile)
			throws JRException {
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportFile.getPath());
		HashMap<String, String> parameters = new HashMap<String, String>();
		ReportGenerator reportGenerator = new ReportGenerator((ProtocolloIngressoForm)form);
		JRDataSource ds = new JREmptyDataSource();
		parameters = reportGenerator.getParameters();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, ds);
		return jasperPrint;
	}
	
	
	 /*
     * Compila il report template leggendolo dal file system, TODO: si potrebbe
     * leggere il file direttamente dalle risorse, in un jar file per esempio,
     * per offrire maggiore portabilit? dell'applicazione
     */
    public static void compile(ServletContext context, String file)
            throws JRException {
    	System.setProperty("jasper.reports.compile.class.path", context
                .getRealPath("/WEB-INF/lib/"+LIBRERIA_JASPER_REPORT)
                + System.getProperty("path.separator")
                + context.getRealPath("/WEB-INF/classes/"));

        System.setProperty("jasper.reports.compile.temp", context
                .getRealPath("/")
                + "/WEB-INF/reports/");
        JasperCompileManager.compileReportToFile(context.getRealPath("/")
                + file);

    }
}
