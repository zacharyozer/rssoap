package com.rssoap.view;
/**
 * Exception that is thrown when a selection is asked for but no item is selected.
 * @author pgroudas
 *
 */
public class NoItemSelectedException extends Exception {
    private String message;
    /**
     * Constructor based on a String.
     * @param s
     */
    public NoItemSelectedException(String s) {
        message = s+"\n";
    }
    /**
     * Default Constructor.
     *
     */
    public NoItemSelectedException() {
        message = "NoItemSelectedException";
    }
    /**
     * Returns a String representation of the exception.
     * @return String
     */
    public String toString() {
        return message;
    }
}