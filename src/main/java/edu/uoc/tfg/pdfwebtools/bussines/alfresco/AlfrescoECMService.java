package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface AlfrescoECMService {


    String uploadDocument(String docName, String userName, MultipartFile file, String parentFolderEcmId);

    String createFolder(String folderName, String parentIdEcmid, String userName);

    boolean deleteDocument(String docId);

    DocumentECM downloadDocument(String ecmid);

    String uploadDocument(DocumentECM doc, String userName, String ecmid) throws IOException;
}
