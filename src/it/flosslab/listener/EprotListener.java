/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   EprotListener.java 
 * Created:  10 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.listener;

import it.finsiel.siged.dao.jdbc.DocDaoInterface;
import it.finsiel.siged.dao.jdbc.DocumentaleDAOjdbc;
import it.finsiel.siged.dao.jdbc.DocumentoDAOjdbc;
import it.finsiel.siged.exception.DataException;
import it.flosslab.dao.indexer.IndexerDAO;
import it.flosslab.parser.ContentParserException;
import it.flosslab.parser.TikaDocumentParser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author roberto onnis
 *
 *
 */
public class EprotListener implements ServletContextListener {
	
    private static ResourceBundle bundle;
    
    private static String SYSTEM_PARAMS = "systemParams";

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
	}

	private void clearIndexDirectory(String fileSystemIndexPath) {
		File index = new File(fileSystemIndexPath);
		if(index.exists() && index.isDirectory()) {
			for(File file :index.listFiles()) {
				this.clearIndexDirectory(file.getAbsolutePath());
			}
		} else if (index.exists() && !index.isDirectory()){
			index.delete();
		}
		
	}

	/**
	 * 
	 */
	private void startIndexing(String fileSystemIndexPath, boolean isProtocollo) {
		HashMap<Integer, String> map;
		DocDaoInterface dao = null;
		if(isProtocollo) {
			dao = new DocumentoDAOjdbc();
		} else {
			dao = new DocumentaleDAOjdbc();
		}
		try {
			map = dao.getDocumenti();
			
			if(map != null) {
				IndexerDAO indexer = new IndexerDAO(fileSystemIndexPath);
				
				for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
			        Map.Entry entry = (Map.Entry) it.next();
			        Integer key = (Integer)entry.getKey();
			        String value = (String)entry.getValue();
			        File file = new File(value != null ? value : "");
			        if(file.exists() && !file.isDirectory()) {
			        	indexer.setContentParser(new TikaDocumentParser(key.intValue()));
			        	indexer.index(fileSystemIndexPath, file.getAbsolutePath());
			        }
				}
			}
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ContentParserException e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		System.out.println("####  Startup Eprot Listener  ####");
		bundle = ResourceBundle.getBundle(SYSTEM_PARAMS);
		
		// reindicizza protocollo
		String fileSystemIndexPath = bundle.getString("file_system_index_path_protocollo");
        File file = new File(fileSystemIndexPath);
        if(!file.exists())file.mkdirs();
		clearIndexDirectory(fileSystemIndexPath);
		startIndexing(fileSystemIndexPath, true);
		
		// reindicizza documentale
		fileSystemIndexPath = bundle.getString("file_system_index_path_documentale");
		file = new File(fileSystemIndexPath);
        if(!file.exists())file.mkdirs();
		clearIndexDirectory(fileSystemIndexPath);
		startIndexing(fileSystemIndexPath, false);
	}

}
