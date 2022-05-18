package edu.uoc.tfg.pdfwebtools.appexceptions;

public class PdfAppException extends RuntimeException {

    public enum Type {
        CONSTRAINT_VIOLATION, UNEXPECTED, NOT_FOUND
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
