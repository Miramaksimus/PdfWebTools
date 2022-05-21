package edu.uoc.tfg.pdfwebtools.bussines.repository;


import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.springframework.web.multipart.MultipartFile;

public interface RepositoryService {

    DocumentECM downloadDocument(Integer id);

    Folder createFolder(String username, Folder folder);

    Folder findFolderByUser_UsernameAndId(String username, Integer folderId);

    Folder findFolderByUser_UsernameAndParentFolder(String username, Folder parentFolder);

    Document uploadDocument(MultipartFile file, Folder parentFolder, String username);

    Document uploadDocument(DocumentECM doc, Folder parentFolder, String username);

    Document findDocumentByIdAdnFolderId(Integer docId, Integer folderId);

    Boolean deleteDocument(Document doc);
}
