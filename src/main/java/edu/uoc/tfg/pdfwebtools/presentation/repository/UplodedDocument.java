package edu.uoc.tfg.pdfwebtools.presentation.repository;

import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.springframework.web.multipart.MultipartFile;

public class UplodedDocument {
//    private Integer folderId;
    private MultipartFile file;

    private Folder parentFolder;



//    public Integer getFolderId() {
//        return folderId;
//    }
//
//    public void setFolderId(Integer folderId) {
//        this.folderId = folderId;    }


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }
}
