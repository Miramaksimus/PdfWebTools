package edu.uoc.tfg.pdfwebtools.bussines.alfresco;


import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.RepositoryCapabilities;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AlfrescoECMServiceBean implements AlfrescoECMService {

    private static final Logger logger = LoggerFactory.getLogger(AlfrescoECMServiceBean.class);

    @Value("${alfresco.api.atompub.url}")
    private static String alfrescoAtompubUrl;

    private static Session session;



    @Autowired
    public AlfrescoECMServiceBean() {
    }

    @Override
    public void getDocument(int documentId) {
        logger.debug("documentId...: {} ", documentId);

    }

    @Override
    public Boolean updateDocument(int documentId, DocumentECM docSigned) {
        logger.debug("documentId...: {}", documentId);
       return false;
    }

    @Override
    public Boolean updateDocument() {
        logger.debug("");
        return false;

    }

/*Private methods*/


    private static Session getSession() throws CmisConnectionException {
        if (session == null) {
            synchronized (AlfrescoECMServiceBean.class) {
                try  {
                    session = createSession();
                } catch (org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException ex) {
                    throw new CmisConnectionException(ex.getMessage());
                }
            }
        }
        return session;
    }

    private static Session createSession() throws CmisConnectionException {
        logger.trace("+createSession ...");
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put(SessionParameter.USER, "admin");
        parameter.put(SessionParameter.PASSWORD, "admin");
        parameter.put(SessionParameter.ATOMPUB_URL, alfrescoAtompubUrl);
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        List<Repository> repos = sessionFactory.getRepositories(parameter);
        if (repos.isEmpty()) {
            throw new CmisConnectionException("There is no repository - " +
                    alfrescoAtompubUrl );
        } else {
            if (repos.size() > 1) {
                logger.warn("S'ha trobat més d'un repositori, s'agafarà el primer");
            }
            Repository r = repos.get(0);
            parameter.put(SessionParameter.REPOSITORY_ID, r.getId());
            Session s = sessionFactory.createSession(parameter);
            if (logger.isInfoEnabled()) {
                logger.info("Sessió CMIS creada :)");
                printRepositoryInfo(r);
            }
            return s;
        }
    }

    private static void printRepositoryInfo(Repository r) {
        RepositoryCapabilities rcap = r.getCapabilities();

        StringBuilder sb = new StringBuilder();
        sb.append("Information Repository").
                append("\n========================================").
                append("\nURL: ").append(alfrescoAtompubUrl).
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
                append("\nNew type setteable attributes: ").append(rcap.getNewTypeSettableAttributes()).
                append("\nOrder by: ").append(rcap.getOrderByCapability()).
                append("\nQuery: ").append(rcap.getQueryCapability()).
                append("\nRenditions: ").append(rcap.getRenditionsCapability()).
                append("\nAll versions searchable: ").append(rcap.isAllVersionsSearchableSupported()).
                append("\nis get descendants: ").append(rcap.isGetDescendantsSupported()).
                append("\nget folder tree: ").append(rcap.isGetFolderTreeSupported()).
                append("\nMultifiling: ").append(rcap.isMultifilingSupported()).
                append("\nPwc searchable: ").append(rcap.isPwcSearchableSupported()).
                append("\nPwc updatable: ").append(rcap.isPwcUpdatableSupported()).
                append("\nUnfiling: ").append(rcap.isUnfilingSupported()).
                append("\nVersion specific filing: ").append(rcap.isVersionSpecificFilingSupported());
        logger.info(sb.toString());
    }

}
