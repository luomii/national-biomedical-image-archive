/*L
 *  Copyright SAIC, Ellumen and RSNA (CTP)
 *
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/national-biomedical-image-archive/LICENSE.txt for details.
 */

/**
 * UserException.java
 *
 * This file was generated by MapForce 2011r2sp1.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the MapForce Documentation for further details.
 * http://www.altova.com/mapforce
 */


package com.altova;


public class UserException extends RuntimeException 
{
	protected String	message;
	protected org.w3c.dom.Node node;

	public UserException(String text) 
	{
		message = text;
		node = null;
	}

	public UserException(org.w3c.dom.Node n) 
	{
		message = null;
		node = n;
	}

	public UserException(String text, org.w3c.dom.Node n) 
	{
		message = text;
		node = n;
	}

	public String getMessage() {
		return message;
	}

	public org.w3c.dom.Node getNode() {
		return node;
	}
}
