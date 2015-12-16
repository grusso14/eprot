/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   Indexer.java 
 * Created:  6 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.dao.indexer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import it.flosslab.parser.ContentParser;
import it.flosslab.parser.ContentParserException;
import it.flosslab.parser.TikaDocumentParser;

/**
 * 
 * @author Roberto Onnis started from Christoph Hartmann example
 *
 */
public class IndexerDAO {

	static Logger logger = Logger.getLogger(IndexerDAO.class);

	volatile protected ContentParser contentParser;
	
	String file_system_index_path;
	
	Boolean contentParserAccess = true;

	public IndexerDAO(String indexPath) throws IOException {
        this.file_system_index_path = indexPath;
	}

	/**
	 * Index the given file and use the given writer to store it.
	 * 
	 * @param writer
	 * @param file
	 * @throws ContentParserException
	 */
	public void index(IndexWriter writer, File file)
			throws ContentParserException {

		logger.debug(file.getAbsolutePath());

		if (contentParser == null)
			return;

		// iterate over folders and files
		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						try {
							index(writer, new File(file, files[i]));
						} catch (Exception e) {
							logger.error(e);
						}
					}
				}
			} else {
				logger.debug("Indexing " + file);
				try {
					Document doc = null;
					// parse the document
					synchronized (contentParserAccess) {
						doc = contentParser.getDocument(file);
					}

					// put it into Lucene
					if (doc != null) {
						writer.addDocument(doc);
					} else {
						logger.error("Cannot handle "
								+ file.getAbsolutePath() + "; skipping");
					}
				} catch (IOException e) {
					logger.error("Cannot index " + file.getAbsolutePath()
							+ "; skipping (" + e.getMessage() + ")");
				}
			}
		}
	}

	/**
	 * Index files form the resourcelocation recursively and store the index at
	 * the indexRoot position
	 * 
	 * @param indexRoot
	 * @param resourcelocation
	 * @throws IOException
	 * @throws ContentParserException
	 */
	public void index(String indexRoot, String resourcelocation)
			throws IOException, ContentParserException {
		boolean isNew = false;
		File segments = new File(this.file_system_index_path, "segments.gen");
		if(!segments.exists()) isNew = true;
		// get Lucene instances
		Directory dir = FSDirectory.getDirectory(indexRoot);
		Analyzer analyzer = new SimpleAnalyzer();
		IndexWriter writer = new IndexWriter(dir, analyzer, isNew,
				MaxFieldLength.UNLIMITED);

		//long start = new Date().getTime();
		// start indexing
		index(writer, new File(resourcelocation));
		writer.optimize();
		writer.close();
		//long end = new Date().getTime();

		//stats(dir, start, end);

	}
	
	/**
	 * Index files form the resourcelocation recursively and store the index at
	 * the indexRoot position
	 * @param indexRoot
	 * @param documentId
	 * @throws LockObtainFailedException
	 */
	public void delete(String indexRoot, String documentId) throws LockObtainFailedException{
		Directory dir = null;
		Analyzer analyzer = new SimpleAnalyzer();
		IndexWriter writer = null;
		QueryParser parser = new QueryParser("documento_id", new SimpleAnalyzer());
		Query query = null;
		try {
			dir = FSDirectory.getDirectory(indexRoot);
			writer = new IndexWriter(dir, analyzer, false, MaxFieldLength.UNLIMITED);
			query = parser.parse(documentId);
			writer.deleteDocuments(query);
			writer.optimize();
			writer.close();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * just print some stats
	 * 
	 * @param dir
	 * @param start
	 * @param end
	 * @throws CorruptIndexException
	 * @throws IOException
	 */
	private void stats(Directory dir, long start, long end)
			throws CorruptIndexException, IOException {

		IndexReader reader = IndexReader.open(dir);
		logger.debug("Documents indexed: " + reader.numDocs());
		logger.debug("Total time: " + (end - start) + " ms");
		reader.close();
	}

	/**
	 * set the content parser
	 * 
	 * @param contentParser
	 */
	public void setContentParser(ContentParser contentParser) {
		synchronized (contentParserAccess) {
			this.contentParser = contentParser;
		}

	}

	/**
	 * return the current content parser
	 * 
	 * @return
	 */
	public ContentParser getContentParser() {
		return contentParser;
	}

	public static void main(String[] args) {
		IndexerDAO indexer;
		try {
			// initialize indexer implementation
			indexer = new IndexerDAO("");
			
			// start indexing process
			
			//URL test_documents = IndexerDAO.class.getClassLoader().getResource("test-documents");
			String test_documents = "file://";
			// set our tika parser
			//indexer.setContentParser(new TikaDocumentParser(test_documents));
			logger.debug(test_documents);
			
			indexer.index("index", test_documents);
		} catch (Exception e) {
			logger.error(e);
		} 
	}

}
