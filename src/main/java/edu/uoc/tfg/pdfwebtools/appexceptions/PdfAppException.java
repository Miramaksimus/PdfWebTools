package edu.uoc.tfg.pdfwebtools.appexceptions;

public class PdfAppException extends RuntimeException {

    public enum Type {
        PRE_AUTHORIZATION, AUTHORIZATION, START_SIGNATURE , GET_SIGNATURE,  UNEXPECTED, NOT_FOUND, IS_PROCESSED
    }

    private final Type type;

    public PdfAppException(String message, Throwable cause, Type tipus) {
        super(message, cause);
        this.type = tipus;
    }

    public PdfAppException(String message, Type type) {
        super(message);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

}
