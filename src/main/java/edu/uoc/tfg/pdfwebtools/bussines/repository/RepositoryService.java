package edu.uoc.tfg.pdfwebtools.bussines.repository;


import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;

import java.util.List;

public interface RepositoryService {

    DocumentECM downloadDocument(Integer id);

    Folder findFolderByUser_UsernameAndId(String username, Integer folderId);

    Folder findFolderByUser_UsernameAndParentFolder(String principal, Folder parentFolder);
}
