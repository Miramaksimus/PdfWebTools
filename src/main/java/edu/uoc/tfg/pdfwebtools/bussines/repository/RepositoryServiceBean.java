package edu.uoc.tfg.pdfwebtools.bussines.repository;

import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.AlfrescoECMService;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import edu.uoc.tfg.pdfwebtools.integration.repos.profile.UserRepository;
import edu.uoc.tfg.pdfwebtools.integration.repos.repository.DocumentRepository;
import edu.uoc.tfg.pdfwebtools.integration.repos.repository.FolderRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Service
public class RepositoryServiceBean implements RepositoryService {

    FolderRepository folderRepository;

    UserRepository userRepository;

    DocumentRepository documentRepository;

    AlfrescoECMService alfrescoECMService;


    @Autowired
    public RepositoryServiceBean(FolderRepository folderRepository, UserRepository userRepository,
                                 DocumentRepository documentRepository, AlfrescoECMService alfrescoECMService) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
        this.documentRepository = documentRepository;
        this.alfrescoECMService = alfrescoECMService;
    }

    @Override
    @Transactional
    public DocumentECM downloadDocument(Integer id) {
        Document doc = documentRepository.getById(id);
        DocumentECM ret = new  DocumentECM();
        InputStream is = alfrescoECMService.downloadDocument(doc.getEcmid());
        ret.setInputStream(is);
        ret.setName(doc.getTitle());
        ret.setMimeType(doc.getMimeType());
        return ret;
    }

    @Override
    public Folder createFolder(String username, Folder folder) {

        try {
            User user = userRepository.findByUsername(username);
            folder.setUser(user);
            return folderRepository.save(folder);
        } catch (DataIntegrityViolationException e) {
            throw new PdfAppException("It does not allow two folders with the same name in the same directory", PdfAppException.Type.CONSTRAINT_VIOLATION);
        }
    }

    @Override
    public Folder findFolderByUser_UsernameAndId(String username, Integer folderId) {
        return  folderRepository.findByUser_UsernameAndId(username, folderId);
    }

    @Override
    @Transactional
    public Folder findFolderByUser_UsernameAndParentFolder(String username, Folder parentFolder) {
        return  folderRepository.findByUser_UsernameAndParentFolder(username,parentFolder);
    }

    @Override
    public Document uploadDocument(MultipartFile file, Folder parentFolder, String username) {
        try {
            String ecmId = alfrescoECMService.uploadDocument(file.getOriginalFilename(), username, file);
            Document document = new Document();
            document.setFolder(parentFolder);
            document.setDate(Instant.now());
            document.setTitle(file.getOriginalFilename());
            document.setMimeType(file.getContentType());
            document.setEcmid(ecmId);
            return documentRepository.save(document);
        } catch (DataIntegrityViolationException e) {
            throw new PdfAppException("It doesn't allow two documents with the same name in the same folder", PdfAppException.Type.CONSTRAINT_VIOLATION);
        }
    }

    @Override
    @Transactional
    public Document findDocumentByIdAdnFolderId(Integer docId, Integer folderId) {
        return documentRepository.findByIdAndFolder_Id(docId, folderId);
    }
}
