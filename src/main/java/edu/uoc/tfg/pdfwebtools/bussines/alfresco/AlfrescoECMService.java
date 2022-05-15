package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


public interface AlfrescoECMService {

    void getDocument(int documentId);

    Boolean updateDocument(int documentId, DocumentECM docSigned);

    Boolean updateDocument();
}
