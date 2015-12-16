/*
 * Created on 30-nov-2004
 *
 * 
 */
package it.finsiel.siged.util;

import it.finsiel.siged.constant.FileConstants;
import it.finsiel.siged.constant.ReturnValues;
import it.finsiel.siged.dao.digitalsignature.VerificaFirma;
import it.finsiel.siged.mvc.presentation.actionform.UploaderForm;
import it.finsiel.siged.mvc.vo.protocollo.DocumentoVO;
import it.flosslab.dao.indexer.IndexerDAO;
import it.flosslab.parser.ContentParserException;
import it.flosslab.parser.TikaDocumentParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.upload.FormFile;
import org.w3c.dom.Document;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;



/**
 * @author Almaviva sud
 * 
 */
public final class FileUtil {

    static Logger logger = Logger.getLogger(FileUtil.class.getName());
    
    public static String leggiFormFile(UploaderForm form,
            HttpServletRequest request, ActionMessages errors) {
    	String folder = ServletUtil.getTempUserPath(request.getSession());
    	return leggiFormFile(form, folder, errors);
    }

    public static String leggiFormFile(UploaderForm form,
            String folder, ActionMessages errors) {
        FormFile file = form.getFormFileUpload(); 
        //String fileName = file.getFileName();
        String mimeType = file.getContentType();  //legge il mimeType del file da uplodare
        String pathFileTemporaneo = null;
        try {
        	/* 
        	 	viene istanziato il document converter di openoffice 
        	   	e sulla base del mimetype del documento in ingresso viene eseguita
        	   	la conversione in pdf
        	 */
            DocumentConverter converter = getOpenOfficeConverter();   
            DefaultDocumentFormatRegistry ddfr = new DefaultDocumentFormatRegistry();
            DocumentFormat documentFormatToConvert = ddfr.getFormatByMimeType(mimeType);
            DocumentFormat pdfDocumentFormat = ddfr.getFormatByFileExtension("pdf");
            InputStream stream = new BufferedInputStream(file.getInputStream(), FileConstants.BUFFER_SIZE);
            File tempFile = null;
            OutputStream bos = null;
            if(null != documentFormatToConvert   && !"pdf".equalsIgnoreCase(documentFormatToConvert.getFileExtension())){
            	tempFile = File.createTempFile("temp_", ".pdf", new File(folder));
            	bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                converter.convert(stream, documentFormatToConvert, bos, pdfDocumentFormat);
                
            }else{
            	/*
            	    Se il mimetype del documento in ingresso non è tra quelli 
            	    supportati da openoffice il documento non viene convertito 
            	     
            	 */
            	tempFile = File.createTempFile("temp_", ".upload", new File(folder));
            	bos = new BufferedOutputStream(new FileOutputStream(tempFile));
                writeToStream(stream, bos);
            }
            pathFileTemporaneo = tempFile.getAbsolutePath();
            bos.close();
            stream.close();
        } catch (FileNotFoundException fnfe) {
            logger.debug("Error uploading file to:" + folder + " - file name:"
                    + file.getFileName());
            logger.error(fnfe.getMessage(), fnfe);
            errors.add("fileFormUpload", new ActionMessage(
                    "upload.error.filenotfound"));
        } catch (IOException ioe) {
            logger.debug("Error uploading file to:" + folder + " - file name:"
                    + file.getFileName());
            logger.error(ioe.getMessage(), ioe);
            errors.add("fileFormUpload", new ActionMessage("upload.error.io"));
        }
        return pathFileTemporaneo;
    }

	
	   public static String leggiFormFilePrincipale(UploaderForm form,
	            HttpServletRequest request, ActionMessages errors) {
	        FormFile file = form.getFilePrincipaleUpload();
	        //String fileName = file.getFileName();
	        String mimeType = file.getContentType();  //legge il mimeType del file da uplodare
	        
	        String pathFileTemporaneo = null;
	        String folder = null;
	        try {
	            folder = ServletUtil.getTempUserPath(request.getSession());
	        	/* 	viene istanziato il document converter di openoffice 
	     	  		e sulla base del mimetype del documento in ingresso viene eseguita
	     	   		la conversione in pdf
	        	 */            
	            DocumentConverter converter = getOpenOfficeConverter();   
	            DefaultDocumentFormatRegistry ddfr = new DefaultDocumentFormatRegistry();
	            DocumentFormat documentFormatToConvert = ddfr.getFormatByMimeType(mimeType);
	            DocumentFormat pdfDocumentFormat = ddfr.getFormatByFileExtension("pdf");
	            InputStream stream = new BufferedInputStream(file.getInputStream(), FileConstants.BUFFER_SIZE);
	            File tempFile = null;
	            OutputStream bos = null;
	            if(null != documentFormatToConvert   && !"pdf".equalsIgnoreCase(documentFormatToConvert.getFileExtension())){
	            	tempFile = File.createTempFile("temp_", ".pdf", new File(folder));
	            	bos = new BufferedOutputStream(new FileOutputStream(tempFile));
	                converter.convert(stream, documentFormatToConvert, bos, pdfDocumentFormat);
	            }else{
	            	/*
	        	    	Se il mimetype del documento in ingresso non è tra quelli 
	        	    	supportati da openoffice il documento non viene convertito 
	        	     
	            	 */            	
	            	tempFile = File.createTempFile("temp_", ".upload", new File(folder));
	            	bos = new BufferedOutputStream(new FileOutputStream(tempFile));
	                writeToStream(stream, bos);
	            }
	            pathFileTemporaneo = tempFile.getAbsolutePath();
	            bos.close();
	            stream.close();
	            
	        } catch (FileNotFoundException fnfe) {
	            logger.debug("Error uploading file to:" + folder + " - file name:"
	                    + file.getFileName());
	            logger.error(fnfe.getMessage(), fnfe);
	            errors.add("filePrincipaleUpload", new ActionMessage(
	                    "upload.error.filenotfound"));
	        } catch (IOException ioe) {
	            logger.debug("Error uploading file to:" + folder + " - file name:"
	                    + file.getFileName());
	            logger.error(ioe.getMessage(), ioe);
	            errors.add("filePrincipaleUpload", new ActionMessage(
	                    "upload.error.io"));
	        } catch (Exception ioe) {
	            logger.debug("Error uploading file to:" + folder + " - file name:"
	                    + file.getFileName());
	            logger.error(ioe.getMessage(), ioe);
	            errors.add("filePrincipaleUpload", new ActionMessage(
	                    "upload.error.io"));
	        }
	        return pathFileTemporaneo;
	    }	

    /*
     * utility che effettua l'upload di un file in formato p7m ed estrae il file
     * "in chiaro".
     */

    public static String leggiFormFileP7M(UploaderForm form,
            HttpServletRequest request, ActionMessages errors) {
        FormFile file = form.getFormFileUpload();
        String fileName = file.getFileName();
        String pathFileTemporaneo = null;
        String folder = null;
        try {
            folder = ServletUtil.getTempUserPath(request.getSession());
            InputStream stream = new BufferedInputStream(file.getInputStream(),
                    FileConstants.BUFFER_SIZE);
            File tempFileP7M = File.createTempFile("temp_", ".upload",
                    new File(folder));
            OutputStream bos = new BufferedOutputStream(new FileOutputStream(
                    tempFileP7M), FileConstants.BUFFER_SIZE);
            writeToStream(stream, bos);
            bos.close();
            stream.close();

            File tempFile = File.createTempFile("temp_", ".upload", new File(
                    folder));
            VerificaFirma.saveContentFromP7M(tempFileP7M.getAbsolutePath(),
                    tempFile.getAbsolutePath());
            pathFileTemporaneo = tempFile.getAbsolutePath();
            deleteFile(tempFileP7M.getAbsolutePath());
        } catch (FileNotFoundException fnfe) {
            logger.debug("Error uploading file to:" + folder + " - file name:"
                    + fileName);
            logger.error(fnfe.getMessage(), fnfe);
            errors.add("fileFormUpload", new ActionMessage(
                    "upload.error.filenotfound"));
        } catch (IOException ioe) {
            logger.debug("Error uploading file to:" + folder + " - file name:"
                    + fileName);
            logger.error(ioe.getMessage(), ioe);
            errors.add("fileFormUpload", new ActionMessage("upload.error.io"));
        }
        return pathFileTemporaneo;
    }

    
	/**
	 * @return
	 * @throws ConnectException
	 */
	public static DocumentConverter getOpenOfficeConverter()
			throws ConnectException {
		ResourceBundle bundle = ResourceBundle.getBundle("systemParams");
        String host = bundle.getString("openoffice_host");
        int port = Integer.parseInt(bundle.getString("openoffice_port"));
        OpenOfficeConnection connection = new SocketOpenOfficeConnection(host, port);
		connection.connect();
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		return converter;
	}

	/**
	 * @param stream
	 * @param bos
	 * @throws IOException
	 */
	public static void writeToStream(InputStream stream, OutputStream bos)
			throws IOException {
		int bytesRead = 0;
		byte buffer[] = new byte[FileConstants.BUFFER_SIZE];
		while ((bytesRead = stream.read(buffer)) != -1) {
		    bos.write(buffer, 0, bytesRead);
		    bos.flush();
		}
	}

    /**
     * Utility per la scrittura dell file sul filesystem
     * 
     * @param os	lo stream di output
     * @param filePath	il path del file in output
     * @return	true se il file è stato scritto correttamente false altrimenti
     */
    public static boolean writeFile(OutputStream os, String filePath) {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(filePath),
                    FileConstants.BUFFER_SIZE);
            writeToStream(is, os);
        } catch (FileNotFoundException e) {
            logger.error("", e);
            return false;
        } catch (IOException e) {
            logger.error("", e);
            return false;
        } finally {
            closeOS(os);
            closeIS(is);
        }
        return true;
    }

    public static boolean writeFile(InputStream is, String destPath) {

        OutputStream os = null;
        try {

            os = new BufferedOutputStream(new FileOutputStream(destPath),
                    FileConstants.BUFFER_SIZE);
            writeToStream(is, os);
        } catch (FileNotFoundException e) {
            logger.error("", e);
            return false;
        } catch (IOException e) {
            logger.error("", e);
            return false;
        } finally {
            closeOS(os);
        }
        return true;
    }
    
   
    public static boolean writeFile(InputStream is, OutputStream os) {

        try {
            writeToStream(is, os);
        } catch (IOException e) {
            logger.error("", e);
            return false;
        }
        return true;
    }

    public static byte[] leggiFileAsBytes(String path) {
        InputStream is = null;
        ByteArrayOutputStream os = null;
        byte[] array = new byte[0];
        try {
            os = new ByteArrayOutputStream();
            is = new BufferedInputStream(new FileInputStream(path),
                    FileConstants.BUFFER_SIZE);
            int bytesRead = 0;
            byte buffer[] = new byte[FileConstants.BUFFER_SIZE];

            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
                os.flush();
            }
            array = os.toByteArray();
        } catch (FileNotFoundException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            closeIS(is);
            closeOS(os);
        }
        return array;
    }

    /**
     * Copia un file da source a destination utilizzando le nuove librerie java
     * "nio".
     * 
     * @param source
     *            il file origine
     * @param destination
     *            il file destinazione
     */
    public static void copyFile(File source, File destination) {
        if (!source.exists()) {
            return;
        }
        if ((destination.getParentFile() != null) && (!destination.getParentFile().exists())) {
            destination.getParentFile().mkdirs();
        }
        try {
            FileChannel srcChannel = new FileInputStream(source).getChannel();
            FileChannel dstChannel = new FileOutputStream(destination).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            dstChannel.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void deltree(String directory) {
        deltree(new File(directory));
    }

    public static void deltree(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] fileArray = directory.listFiles();
            for (int i = 0; i < fileArray.length; i++) {
                if (fileArray[i].isDirectory()) {
                    deltree(fileArray[i]);
                } else {
                    fileArray[i].delete();
                }
            }
            directory.delete();
        }
    }

    /**
     * Funzione che calcola l'impronta di un file utilizzando l'algoritmo SHA.
     * 
     * @param size
     *            la lunghezza dell'impronta.
     * @param filePath
     *            path del file per il quale calcolare l'impronta.
     * @param errors
     *            Oggetto nel quale andranno riportati eventuali errori avvenuti
     *            nel processo di calcolo
     * @return l'impronta del file di tipo String.
     */
    public static String calcolaDigest(String filePath, ActionMessages errors) {
        String strImpronta = "";
        InputStream is = null;
        // SHA = Secure Hash Algorithm
        MessageDigest sha = null;
        try {
            is = new BufferedInputStream(new FileInputStream(filePath),
                    FileConstants.BUFFER_SIZE);
            if (is != null) {
                sha = MessageDigest.getInstance(FileConstants.SHA);
                byte[] messaggio = new byte[FileConstants.BUFFER_SIZE];
                int len = 0;
                while ((len = is.read(messaggio)) != -1) {
                    sha.update(messaggio, 0, len);
                }
                byte[] impronta = sha.digest();
                // convertiamo in stringa l'array di byte
                int size = impronta.length;
                StringBuffer buf = new StringBuffer();
                int unsignedValue = 0;
                String strUnsignedValue = null;
                for (int i = 0; i < size; i++) {
                    // I letterali interi sono stringhe di cifre ottali,
                    // decimali o esadecimali.
                    // L'inizio della costante serve a dichiarare la base del
                    // numero,
                    // uno zero iniziale indica la base otto, 0x o 0X indica la
                    // base 16
                    // e niente indica la notazione decimale.
                    // Ad esempio il numero 15 in base dieci pu� essere
                    // rappresentato come:
                    // 15 in notazione decimale
                    // 017 in notazione ottale
                    // 0xf oppure 0XF in notazione esadecimale

                    // trasformiamo l'intero in unsigned:
                    unsignedValue = ((int) impronta[i]) & 0xff;
                    strUnsignedValue = Integer.toHexString(unsignedValue);
                    if (strUnsignedValue.length() == 1)
                        buf.append("0");
                    buf.append(strUnsignedValue);
                }
                strImpronta = buf.toString();
                logger.debug("Impronta calcolata:" + strImpronta);

            }
        } catch (IOException io) {
            logger.error("Errore nella generazione dell'impronta:", io);
            errors.add("fileFormUpload", new ActionMessage("errore_impronta"));
        } catch (Throwable t) {
            logger.error("Errore nella generazione dell'impronta:", t);
            errors.add("fileFormUpload", new ActionMessage("errore_impronta"));
        }
        return strImpronta;
    }

    /**
     * Utility per cambiare l'estensione ad un file.
     * 
     * @param filename
     *            il nome del file, es: documento.html
     * @param estensione
     *            l'estensione nuova del file, es: pdf
     * @return il nuovo nome del file, nell'esempio: documento.pdf
     */
    public static String cambiaEstensioneFile(String filename, String estensione) {
        if (filename != null && filename.lastIndexOf(".") > 0){
            return filename.substring(0, filename.lastIndexOf(".")) + "." + estensione;
        } else{
            return filename + "." + estensione;
        }
    }

    /**
     * @param pathToFile
     *            path al file da cancellare
     * @return boolean , true se il file esiste ed � stato cancellato
     */
    public static boolean deleteFile(String pathToFile) {
        if (pathToFile == null)
            return true;
        File f = new File(pathToFile);
        if (!f.exists())
            return false;
        else
            return f.delete();
    }

    public static void closeIS(InputStream is) {
        try {
            if (is != null)
                is.close();
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public static void closeOS(OutputStream os) {
        try {
            if (os != null)
                os.close();
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    public static boolean validateXML(String file) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(true);
            dbf.setAttribute(
                    "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");
            dbf.setAttribute(
                    "http://java.sun.com/xml/jaxp/properties/schemaSource",
                    "http://www.example.com/Report.xsd");
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse("http://www.wombats.com/foo.xml");
            return true;
        } catch (Exception de) {
            de.printStackTrace();
            return false;
        }

    }

    public static int gestionePathDoc(String dirDoc) {
        int returnValues = ReturnValues.UNKNOWN;
        File dir = new File(dirDoc);

        if (!dir.isDirectory()) {
            System.out.println("Crea la directory:" + dir.getName());
            try {
                if (dir.mkdirs()) {
                    returnValues = ReturnValues.SAVED;
                    System.out.println("Creata la directory: " + dirDoc);
                }

                else {
                    returnValues = ReturnValues.INVALID;
                    System.out.println("Non � possibile creare la directory: "
                            + dirDoc);
                }
            } catch (RuntimeException e) {
                logger.error("Errore nella creazione del path documenti della Aoo");
                e.printStackTrace();
            }
        } else {
            System.out.println("Esiste gi� la directory: " + dirDoc);
            returnValues = ReturnValues.FOUND;
        }

        return returnValues;
    }

    public static Collection getFilePathDoc(String dirDoc) {
        ArrayList listaFile = new ArrayList();
        File dir = new File(dirDoc);
        String[] files = dir.list();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String filename = files[i];
                File f = new File(dirDoc, filename);
                listaFile.add(f);
            }
        }
        return listaFile;
    }
    
	/**
	 * @param documento
	 * @param fullFileSystemPath
	 * @throws IOException
	 * @throws ContentParserException
	 */
	public static void indexFile(DocumentoVO documento, String fullFileSystemPath, String fileSystemIndexPath)
			throws IOException, ContentParserException {
		// lucene indexer
		IndexerDAO indexer;
		//indexing
		indexer = new IndexerDAO(fileSystemIndexPath);
		indexer.setContentParser(new TikaDocumentParser(documento.getId().intValue()));
		indexer.index(fileSystemIndexPath, fullFileSystemPath);
	}

	/**
	 * @param documento
	 * @param fullFileSystemPath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void saveFile(DocumentoVO documento, String fullFileSystemPath)
			throws FileNotFoundException, IOException {
		File in = new File(documento.getPath());
		long length = in.length();
		FileInputStream fis = new FileInputStream(in);
		byte[] bytes = new byte[(int)length];
		
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead=fis.read(bytes, offset, bytes.length-offset)) >= 0) {
		    offset += numRead;
		}
		fis.close();
		
		OutputStream out = new BufferedOutputStream(new FileOutputStream(fullFileSystemPath));
		out.write(bytes);
		out.close();
	}

}