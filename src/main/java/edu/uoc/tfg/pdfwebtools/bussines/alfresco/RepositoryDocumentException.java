package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


public class RepositoryDocumentException extends RuntimeException {

    public enum Tipus {
        INNACCESSIBLE, NOT_FOUND, INESPERAT, MIDA, INPUTSTREAM
    }

    private final Tipus tipus;

    public RepositoryDocumentException(String message, Throwable cause, Tipus tipus) {
        super(message, cause);
        this.tipus = tipus;
    }

    public Tipus getTipus() {
        return tipus;
    }

}
