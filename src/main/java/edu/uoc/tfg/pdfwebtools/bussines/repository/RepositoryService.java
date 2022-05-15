package edu.uoc.tfg.pdfwebtools.bussines.repository;


import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;

public interface RepositoryService {

    DocumentECM downloadDocument(Integer id);
}
