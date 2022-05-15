package edu.uoc.tfg.pdfwebtools.bussines.alfresco;

import java.io.InputStream;


public interface DocumentECM {

    Integer getEcmid();

    String getName();

    InputStream getFile();

    Long getFileSize();

    String getMimeType();

    String getExtension();

    String getTitle();
}
