/* Project:  eprotDyna
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * File:   ContentParserException.java 
 * Created:  6 Feb 2009
 * Author:  roberto.onnis@flosslab.it
 *
 * (C) FlossLab S.r.l. 2009
 */
package it.flosslab.parser;

/**
 * 
 * @author Christoph Hartmann
 *
 */
public class ContentParserException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4246493212705468250L;

	/**
	 * Default constructor.
	 */
	public ContentParserException() {
		super();
	}

	/**
	 * Constructor with message.
	 */
	public ContentParserException(String message) {
		super(message);
	}

	/**
	 * Constructor with nested exception.
	 */
	public ContentParserException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs with message and nested exception.
	 */
	public ContentParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
