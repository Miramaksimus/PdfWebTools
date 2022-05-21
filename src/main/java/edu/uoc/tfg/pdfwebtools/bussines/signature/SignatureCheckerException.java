package edu.uoc.tfg.pdfwebtools.bussines.signature;

public class SignatureCheckerException extends Exception {

    /**
     * Creates a new instance of <code>ConversioException</code> without detail
     * message.
     */
    public SignatureCheckerException() {
    }

    /**
     * Constructs an instance of <code>ConversioException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SignatureCheckerException(String msg) {
        super(msg);
    }
    
    public SignatureCheckerException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
