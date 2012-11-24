package derby;

/**
 * An exception used in the ps4.directions package to indicate an input file with 
 * incorrect format.
 */
public class DBHandlerException extends Exception {
	final static long serialVersionUID = 0;
    private String message;

    public DBHandlerException(String s) {
        message = s+"\n";
    }

    public DBHandlerException() {
        message = "DBHandlerException";
    }

    public String toString() {
        return message;
    }
}