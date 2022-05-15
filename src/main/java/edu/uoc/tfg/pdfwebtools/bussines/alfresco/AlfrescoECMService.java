package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


public interface AlfrescoECMService {

    void getDocument(int documentId);

    Boolean updateDocument(int documentId, Document docSigned);

    Boolean updateDocument();
}
