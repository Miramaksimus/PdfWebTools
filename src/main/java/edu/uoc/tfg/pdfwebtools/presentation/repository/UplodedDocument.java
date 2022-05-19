package edu.uoc.tfg.pdfwebtools.presentation.repository;

import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.springframework.web.multipart.MultipartFile;

public class UplodedDocument {

    private MultipartFile file;

    private Folder parentFolder;


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
