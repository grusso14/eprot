/*
 * Created on 12-gen-2005
 *
 * 
 */
package it.finsiel.siged.servlet;

import it.finsiel.siged.constant.FileConstants;
import it.finsiel.siged.util.StringUtil;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.krysalis.barcode4j.ChecksumMode;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

/**
 * @author Almaviva sud
 * 
 */
public class BarcodeServlet extends HttpServlet {

    static Logger logger = Logger.getLogger(BarcodeServlet.class.getName());

    /*
     * 
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        OutputStream os = response.getOutputStream();

        String barcode_msg = (String) request.getParameter("barcode_msg");

        logger.info(barcode_msg);
        try {
            // Create the barcode bean
            EAN13Bean bean = new EAN13Bean();
            int dpi = FileConstants.BARCODE_DPI;
            double height = FileConstants.BARCODE_HEIGHT;
            // Configure the barcode generator
            bean.setModuleWidth(UnitConv.in2mm(1.5f / dpi)); // makes the
            // narrow
            // bar
            bean.setBarHeight(height);
            bean.setFontName("Arial Bold");// request.getParameter("font"));
            bean.setFontSize(2.48);// Double.parseDouble(request.getParameter("size")));
            bean.doQuietZone(true);
            bean.setQuietZone(5);
            bean.setChecksumMode(ChecksumMode.CP_ADD);// aggiunge l'ultima
            // cifra
            try {
                // Set up the canvas provider for monochrome JPEG output
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(os,
                        "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false);

                // Generate the barcode
                bean.generateBarcode(canvas, StringUtil
                        .formattaNumeroProtocollo(barcode_msg));

                // Signal end of generation
                canvas.finish();
            } finally {
                os.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }

    }
}
