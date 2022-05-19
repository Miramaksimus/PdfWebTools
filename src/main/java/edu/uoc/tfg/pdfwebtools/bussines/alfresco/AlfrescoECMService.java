package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface AlfrescoECMService {


    String uploadDocument(String docName, String userName, MultipartFile file, String parentFolderEcmId);

    String createFolder(String folderName, String parentIdEcmid, String userName);

    boolean deleteDocument(String docId);

    InputStream downloadDocument(String ecmid);
}
