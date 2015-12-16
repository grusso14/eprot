/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   TikaDocumentParser.java 
 * Created:  6 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;

/**
 * 
 * This class the is the bridge between Lucene and Tika. It uses Tika to
 * retrieve the file content and metadata and generates a Lucene document.
 * 
 * @author Christoph Hartmann & Roberto Onnis
 * 
 */
public class TikaDocumentParser implements ContentParser {

	Logger logger = Logger.getLogger(this.getClass());

	AutoDetectParser autoDetectParser;
	TikaConfig config;

	private String documento_id = "";
	
	public TikaDocumentParser(int documentoId) {
		try {
			this.documento_id = new Integer(documentoId).toString();
			config = TikaConfig.getDefaultConfig();
			// use tika's auto detect parser
			autoDetectParser = new AutoDetectParser(config);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private Document getDocument(InputStream input, MimeType mimeType)
			throws ContentParserException {
		Document doc = null;
		try {
			Metadata metadata = new Metadata();

			if (mimeType != null) {
				metadata.set(Metadata.CONTENT_TYPE, mimeType.getName());
			}
			ContentHandler handler = new BodyContentHandler();
			try {
				autoDetectParser.parse(input, handler, metadata);
			} catch (Exception e) {
				throw new ContentParserException(e);
			}

			doc = new Document();
			
			doc.add(new Field("documento_id", this.documento_id, Field.Store.YES,
					Field.Index.ANALYZED));
			// add the content to lucene index document
			doc.add(new Field("body", handler.toString(), Field.Store.NO,
					Field.Index.ANALYZED));

			// add meta data
			String[] names = metadata.names();
			for (String name : names) {
				String value = metadata.get(name);
				doc.add(new Field(name, value, Field.Store.YES,
						Field.Index.ANALYZED));
			}

		} finally {
			try {
				input.close();
			} catch (IOException e) {
				throw new ContentParserException(e);
			}
		}

		return doc;
	}

	public Document getDocument(File file) throws ContentParserException {

		InputStream input;
		try {
			input = new FileInputStream(file);

			if (input == null) {
				System.out
						.println("Could not open stream from specified resource: "
								+ file.getName());
			}

			Document doc = getDocument(input);

			// add the file name to the meta data
			if (doc != null) {
				try {
					doc.add(new Field("filename", file.getCanonicalPath(),
							Field.Store.YES, Field.Index.NO));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return doc;

		} catch (FileNotFoundException e) {
			throw new ContentParserException(e);
		}

		
	}

	public Document getDocument(InputStream input)
			throws ContentParserException {

		// try to retrieve the mime type... unfortunately the Tika parser don't
		// handle this automatically

		BufferedInputStream bufIn = new BufferedInputStream(input);
		
		MimeType mimeType = null;
		
		
		if (bufIn.markSupported()) {
			// TODO this may be dangerous...
			bufIn.mark(2048);
			MimeTypes repo = config.getMimeRepository();
			try {
				mimeType = repo.getMimeType(bufIn);
			} catch (IOException e) {
				throw new ContentParserException(e);
			}
			try {
				bufIn.reset();
			} catch (IOException e) {
				logger.error(e);
			}
		}

		Document doc = getDocument(bufIn, mimeType);

		return doc;
	}

}
