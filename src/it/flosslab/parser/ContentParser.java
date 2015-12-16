/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   ContentParser.java 
 * Created:  6 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.parser;

import java.io.File;
import java.io.InputStream;

import org.apache.lucene.document.Document;

/**
 * 
 * @author Christoph Hartmann
 *
 */
public interface ContentParser {
	/**
	   * Creates a Lucene Document from an InputStream.
	   * This method can return <code>null</code>.
	   *
	   * @param is the InputStream of the content
	   * @return a lucene index document
	   */
	  Document getDocument(InputStream is)
	    throws ContentParserException;
	  
	  /**
	   * Creates a Lucene Document from a File.
	   * This method can return <code>null</code>.
	   *
	   * @param file with the content
	   * @return a lucene index document
	   */
	  Document getDocument(File file)
	    throws ContentParserException;
}
