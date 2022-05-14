package edu.uoc.tfg.pdfwebtools.bussines.alfresco;

import java.io.InputStream;
import java.util.Date;


public interface DocumentSignatura {

    Integer getOid();

    String getNom();

    InputStream getFitxer();

    Long getMidaFitxer();

    Date getDataSignatura();

    String getMimeType();

    String getExtension();

    String getTitol();
}
