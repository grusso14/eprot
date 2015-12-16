/*
 * Created on 3-gen-2005
 *
 * 
 */
package it.finsiel.siged.util;

import it.finsiel.siged.constant.FileConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Almaviva sud
 * 
 */
public final class PdfUtil {

    static Logger logger = Logger.getLogger(PdfUtil.class.getName());

    // Formato Timbro: AOO - Ufficio Protocollo - dd/mm/yyyy - 0000001
    /*
     * Scrive il timbro sul pdf nel formato: "AOO - Ufficio Protocollo -
     * dd/mm/yyyy - 0000001". Calcola automaticamente le posizioni del testo in
     * base alla grandezza della pagina e ai margini. @return Il nome del file
     * temporaneo su cui è stato salvato il nuovo File PDF con il timbro.
     */
    public static String scriviTimbro(String testo, String inPdf)
            throws Exception {
        File tempFile = null;
        try {
            File inFile = new File(inPdf);
            File tempFolder = inFile.getParentFile();
            FileInputStream fis = new FileInputStream(inPdf);
            tempFile = File.createTempFile("timbro_", ".tmp", tempFolder);
            FileOutputStream fos = new FileOutputStream(tempFile);

            PdfReader reader = new PdfReader(fis);
            Rectangle r = reader.getPageSize(1);

            Document document = new Document(r);
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            for (int pag = 1; pag <= reader.getNumberOfPages(); ++pag) {
                document.setPageSize(reader.getPageSize(pag));
                document.newPage();
                PdfContentByte cb = writer.getDirectContent();
                cb.addTemplate(writer.getImportedPage(reader, pag), 0, 0);
                if (pag == 1) {
                    cb.beginText();
                    cb
                            .setRGBColorFill(
                                    FileConstants.PROTOCOLLO_HEADER_FONTCOLOR
                                            .getRed(),
                                    FileConstants.PROTOCOLLO_HEADER_FONTCOLOR
                                            .getGreen(),
                                    FileConstants.PROTOCOLLO_HEADER_FONTCOLOR
                                            .getBlue());
                    BaseFont bf = BaseFont.createFont(
                            FileConstants.PROTOCOLLO_HEADER_FONTNAME,
                            BaseFont.CP1250, true);
                    cb.setFontAndSize(bf,
                            FileConstants.PROTOCOLLO_HEADER_FONTSIZE);
                    cb.setTextMatrix(r.left() + 20, r.top() - 20);
                    cb.showText(testo);
                    cb.endText();
                }
            }
            document.close();
            fis.close();
            fos.close();
            writer.close();
            inFile.delete();
            return tempFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            if (tempFile != null)
                tempFile.delete();
            logger.debug("", e);
            throw new Exception("Impossibile aprire il file: " + inPdf);
        } catch (IOException e) {
            if (tempFile != null)
                tempFile.delete();
            logger.debug("", e);
            throw new Exception("Errore di lettura scrittura su file.");
        } catch (DocumentException e) {
            if (tempFile != null)
                tempFile.delete();
            logger.debug("", e);
            throw new Exception("Imossibile scriver sul Pdf.");
        } finally {

        }
    }

    public static void main(String[] args) {
        try {
            long st = System.currentTimeMillis();
            PdfUtil
                    .scriviTimbro("Protocollo",
                            "D:\\Work\\Intersiel\\Successioni\\Doc\\Success_mod al 27-2-04.pdf");
            System.out.println("Ms:" + (System.currentTimeMillis() - st));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.exit(0);
    }

}
