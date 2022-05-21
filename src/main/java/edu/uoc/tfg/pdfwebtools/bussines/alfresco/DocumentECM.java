package edu.uoc.tfg.pdfwebtools.bussines.alfresco;

import java.io.InputStream;
import java.io.OutputStream;


public class DocumentECM {

    private String name;
    private InputStream inputStream;
    private String mimeType;
    private Long contentLength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getContentLength() {
        return contentLength;
    }

    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }
}
