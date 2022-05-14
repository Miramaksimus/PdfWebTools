package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AlfrescoServiceBean implements AlfrescoServiceLocal {

    private static final Logger logger = LoggerFactory.getLogger(AlfrescoServiceBean.class);


    @Autowired
    public AlfrescoServiceBean() {
    }

    @Override
    public void getDocument(int documentId) {
        logger.debug("documentId...: {} ", documentId);

    }

    @Override
    public Boolean updateDocument(int documentId, DocumentSignatura docSigned) {
        logger.debug("documentId...: {}", documentId);
       return false;
    }

    @Override
    public Boolean updateDocument() {
        logger.debug("");
        return false;

    }




}
