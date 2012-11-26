package com.rssoap.control;
/**
 * ControlExceptions are thrown by the Controller class when a method does not complete successfully.
 * @author pgroudas
 *
 */
public class ControlException extends Exception {
	/**
	 * Constructs a new ControlException
	 * @param s String message
	 */
	public ControlException(String s){
		super(s);
	}
}
