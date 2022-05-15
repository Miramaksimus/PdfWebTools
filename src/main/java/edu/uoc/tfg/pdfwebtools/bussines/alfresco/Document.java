package edu.uoc.tfg.pdfwebtools.bussines.alfresco;

import java.io.InputStream;
import java.util.Date;


public interface Document {

    Integer getEcmid();

    String getName();

    InputStream getFile();

    Long getFileSize();

    String getMimeType();

    String getExtension();

    String getTitle();
}
