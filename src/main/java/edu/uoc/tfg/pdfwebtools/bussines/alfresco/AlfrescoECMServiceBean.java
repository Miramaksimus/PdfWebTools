package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import org.apache.chemistry.opencmis.client.api.*;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AlfrescoECMServiceBean implements AlfrescoECMService {

    private static final Logger logger = LoggerFactory.getLogger(AlfrescoECMServiceBean.class);

    @Value("${alfresco.api.atompub.url}")
    private String alfrescoAtompubUrl;


    @Value("${alfresco.api.browser.url}")
    private String alfrescoBrowserUrl;

    private static String ALFRESCO_BROWSER_URL;

    @Value("${alfresco.api.browser.url}")
    public void setAlfrescoBrowserUrlStatic(String alfrescoBrowserUrl) {
        AlfrescoECMServiceBean.ALFRESCO_BROWSER_URL = alfrescoBrowserUrl;
    }

    private static String ROOT_FOLDER;

    @Value("${alfresco.cmis.folder}")
    private static String rootFolder;

    @Value("${alfresco.cmis.folder}")
    public void setRootFolderStatic(String rootFolder) {
        AlfrescoECMServiceBean.ROOT_FOLDER = rootFolder;
    }


    private static Session session;


    @Autowired
    public AlfrescoECMServiceBean() {
    }

    @Override
    public String uploadDocument(String docName, String userName, MultipartFile file, String parentFolder) {
        logger.debug("uploadDocument  docName: {}, userName: {}", docName, userName);
        Map<String, Object> props = new HashMap<>();
        props.put(PropertyIds.NAME, docName);
        props.put(PropertyIds.CREATED_BY, userName);
        props.put(PropertyIds.CONTENT_STREAM_LENGTH, file.getSize());
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        props.put(PropertyIds.CONTENT_STREAM_MIME_TYPE, file.getContentType());
        Folder root = (Folder) getCmisObject(parentFolder != null ? parentFolder : ROOT_FOLDER);

        Session session = getSession();
        ObjectFactory objectFactory = session.getObjectFactory();
        ContentStream stream = null;
        try {
            stream = objectFactory.createContentStream(
                    docName, file.getSize(), file.getContentType(), file.getInputStream());
        } catch (IOException e) {
            logger.error(e.toString());
            throw new PdfAppException("Error obtaining File input stream", PdfAppException.Type.UNEXPECTED);
        }

        Document doc = root.createDocument(props, stream, VersioningState.MAJOR);


        if (logger.isDebugEnabled()) {
            print(doc.getProperties());
        }
        return doc.getId();

    }

    @Override
    public String createFolder(String folderName, String parentId, String userName) {
        logger.debug("uploadDocument  folderName: {}, userName: {}, parentId: {}", folderName, userName, parentId);
        Map<String, Object> props = new HashMap<>();
        props.put(PropertyIds.NAME, folderName);
        props.put(PropertyIds.CREATED_BY, userName);
        props.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
        Folder root = (Folder) getCmisObject(parentId);
        return root.createFolder(props).getId();
    }

    @Override
    public boolean deleteDocument(String docId) {
        return false;
    }

    @Override
    public InputStream downloadDocument(String ecmId) {

        Session session = getSession();
        try {
            Document r = session.getLatestDocumentVersion(new ObjectIdImpl(ecmId),
                    createOperationContext(session));
            // force reload to prevent receive cached object
            r.refresh();
            return r.getContentStream().getStream();
        } catch (CmisObjectNotFoundException e) {
            logger.error(e.toString());
            throw new PdfAppException("The document is not found in the Alfresco repository", PdfAppException.Type.ALFRESCO);
        } catch (CmisPermissionDeniedException ex) {
            logger.error(ex.toString());
            throw new PdfAppException("Alfresco repository access error", PdfAppException.Type.ALFRESCO);
        }
    }


    /*Private methods*/

    private static CmisObject getCmisObject(String ecmId) throws
            CmisConnectionException {

        Session session = getSession();
        try {
            CmisObject r = session.getObject(new ObjectIdImpl(ecmId),
                    createOperationContext(session));
            // force reload to prevent receive cached object
            r.refresh();
            return r;
        } catch (CmisObjectNotFoundException e) {
            logger.error(e.toString());
            throw new PdfAppException("The document is not found in the Alfresco repository", PdfAppException.Type.ALFRESCO);
        } catch (CmisPermissionDeniedException ex) {
            logger.error(ex.toString());
            throw new PdfAppException("Alfresco repository access error", PdfAppException.Type.ALFRESCO);
        }

    }

    private static OperationContext createOperationContext(Session session) {
        OperationContext operationContext = session.createOperationContext();
        // cache false for not obtaining documents that have already been deleted or modified
        // externally
        operationContext.setCacheEnabled(false);
        operationContext.setRenditionFilterString("cmis:thumbnail");
        operationContext.setIncludeAcls(false);
        operationContext.setIncludeAllowableActions(false);
        operationContext.setIncludePolicies(false);
        operationContext.setIncludePathSegments(true);
        operationContext.setIncludeRelationships(IncludeRelationships.BOTH);
        return operationContext;
    }


    private static Session getSession() throws CmisConnectionException {
        if (session == null) {
            synchronized (AlfrescoECMServiceBean.class) {
                try {
                    session = createSession();
                } catch (CmisConnectionException ex) {
                    logger.error(ex.toString());
                    throw new PdfAppException("Alfresco Session creation failed", PdfAppException.Type.ALFRESCO);
                }
            }
        }
        return session;
    }

    private static Session createSession() throws CmisConnectionException {
        logger.trace("+createSession ...");
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> params = new HashMap<>();
        params.put(SessionParameter.USER, "admin");
        params.put(SessionParameter.PASSWORD, "admin");
        /*parameter.put(SessionParameter.ATOMPUB_URL, alfrescoAtompubUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());*/
        params.put(SessionParameter.BROWSER_URL, ALFRESCO_BROWSER_URL);
        params.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

        List<Repository> repos = sessionFactory.getRepositories(params);
        if (repos.isEmpty()) {
            throw new CmisConnectionException("There is no repository - " +
                    ALFRESCO_BROWSER_URL);
        } else {
            if (repos.size() > 1) {
                logger.warn("More than one repository found, the first one will be taken");
            }
            Repository r = repos.get(0);
            params.put(SessionParameter.REPOSITORY_ID, r.getId());
            Session s = sessionFactory.createSession(params);
            logger.info("CMIS session created :)");
            printRepositoryInfo(r);
            return s;
        }
    }

    private static void printRepositoryInfo(Repository r) {
        RepositoryCapabilities rcap = r.getCapabilities();

        StringBuilder sb = new StringBuilder();
        sb.append("Information Repository").
                append("\n========================================").
                append("\nURL: ").append(ALFRESCO_BROWSER_URL).
                append("\nID: ").append(r.getId()).
                append("\nName: ").append(r.getName()).
                append("\nDescription: ").append(r.getDescription()).
                append("\nProduct name: ").append(r.getProductName()).
                append("\nProduct version: ").append(r.getProductVersion()).
                append("\n\n").
                append("\nCapacities").
                append("\n----------------------------").
                append("\nACL: ").append(rcap.getAclCapability()).
                append("\nChanges: ").append(rcap.getChangesCapability()).
                append("\nContent stream updates: ").append(rcap.getContentStreamUpdatesCapability()).
                append("\nCreatable property type: ").append(rcap.getCreatablePropertyTypes()).
                append("\nJoin: ").append(rcap.getJoinCapability()).
                append("\nNew type settable attributes: ").append(rcap.getNewTypeSettableAttributes()).
                append("\nOrder by: ").append(rcap.getOrderByCapability()).
                append("\nQuery: ").append(rcap.getQueryCapability()).
                append("\nRenditions: ").append(rcap.getRenditionsCapability()).
                append("\nAll versions searchable: ").append(rcap.isAllVersionsSearchableSupported()).
                append("\nis get descendants: ").append(rcap.isGetDescendantsSupported()).
                append("\nget folder tree: ").append(rcap.isGetFolderTreeSupported()).
                append("\nMulti-filing: ").append(rcap.isMultifilingSupported()).
                append("\nPwc searchable: ").append(rcap.isPwcSearchableSupported()).
                append("\nPwc updatable: ").append(rcap.isPwcUpdatableSupported()).
                append("\nUnfiling: ").append(rcap.isUnfilingSupported()).
                append("\nVersion specific filing: ").append(rcap.isVersionSpecificFilingSupported());
        logger.info(sb.toString());
    }

    private void print(List<Property<?>> props) {
        logger.trace("-----------------");
        if (props != null) {
            for (Property p : props) {
                logger.trace(p.getDisplayName() + ": " + p.getValueAsString());
            }
        }
    }

}
