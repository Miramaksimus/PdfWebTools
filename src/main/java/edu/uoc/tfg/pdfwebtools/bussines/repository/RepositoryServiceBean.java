package edu.uoc.tfg.pdfwebtools.bussines.repository;

import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import edu.uoc.tfg.pdfwebtools.integration.repos.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RepositoryServiceBean implements RepositoryService {

    FolderRepository folderRepository;

    @Autowired
    public RepositoryServiceBean(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public DocumentECM downloadDocument(Integer id) {
        return null;
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
}
