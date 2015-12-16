/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   SearcherDao.java 
 * Created:  9 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.dao.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.FSDirectory;

/**
 * @author roberto onnis
 *
 */
public class SearcherProtocolloDao {
	
    private static ResourceBundle bundle;
    
    private static String SYSTEM_PARAMS = "systemParams";
    
    /**
     * Metodo statico per i test unitari
     * @param param
     */
    public static void setSystemParams(String param) {
    	SYSTEM_PARAMS = param;
    }
	
	private IndexSearcher getSearcher() throws IOException { 
		bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
		String fileSystemIndexPath = bundle.getString("file_system_index_path_protocollo");
		File file = new File(fileSystemIndexPath);
		if(!file.exists())file.mkdirs();
		FSDirectory indexDir = FSDirectory.getDirectory(fileSystemIndexPath); 
		IndexSearcher searcher = new IndexSearcher(indexDir);
		return searcher;
	}
	
	public List<String> searchText(String text) {
		List<String> result = new ArrayList<String>();
		IndexSearcher searcher = null;
		try {
			searcher = this.getSearcher();
			QueryParser parser = new QueryParser("body", new SimpleAnalyzer());
			Query query = parser.parse(text);
			TopDocCollector collector = new TopDocCollector(1000);
			   searcher.search(query, collector);
			   ScoreDoc[] hits = collector.topDocs().scoreDocs;
			   for (int i = 0; i < hits.length; i++) {
			     int docId = hits[i].doc;
			     Document doc = searcher.doc(docId);
			     result.add(doc.get("documento_id"));
			   }
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
}
