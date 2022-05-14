package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


public class RepositoriDocumentsException extends RuntimeException {

    public enum Tipus {
        INNACCESSIBLE, NOT_FOUND, INESPERAT, MIDA, INPUTSTREAM
    }

    private final Tipus tipus;

    public RepositoriDocumentsException(String message, Throwable cause, Tipus tipus) {
        super(message, cause);
        this.tipus = tipus;
    }

    public Tipus getTipus() {
        return tipus;
    }

}
