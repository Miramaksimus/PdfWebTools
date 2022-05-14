package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


public interface AlfrescoServiceLocal {

    void getDocument(int documentId);

    Boolean updateDocument(int documentId, DocumentSignatura docSigned);

    Boolean updateDocument();
}
