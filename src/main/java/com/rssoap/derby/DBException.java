package com.rssoap.derby;

/**
 * An exception used in the ps4.directions package to indicate an input file with 
 * incorrect format.
 */
public class DBException extends Exception {

	final static long serialVersionUID = 0;
    private String message;

    public DBException(String s) {
        message = s+"\n";
    }

    public DBException() {
        message = "DBException";
    }

    public String toString() {
        return message;
    }
}