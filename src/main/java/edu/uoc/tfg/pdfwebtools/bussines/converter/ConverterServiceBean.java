package edu.uoc.tfg.pdfwebtools.bussines.converter;

import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.docx4j.Docx4J;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ConverterServiceBean implements ConverterService {

    RepositoryService repositoryService;

    @Autowired
    public ConverterServiceBean(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @Override
    public Document convertDocxToPfd(Document doc) throws Exception {
        DocumentECM docx = repositoryService.downloadDocument(doc.getId());
        DocumentECM pdf = procesDocToPdf(docx);
        Document newDoc = repositoryService.uploadDocument(pdf, doc.getFolder(), doc.getFolder().getUser().getUsername());
        return newDoc;
    }

    @Override
    public Document convertPfdToDocx(Document doc) {
        return null;
    }

    private DocumentECM procesDocToPdf(DocumentECM doc) throws Exception {

        DocumentECM retDoc = new DocumentECM();
        retDoc.setName(removeExtension(doc.getName()) + ".pdf");
        retDoc.setMimeType("application/pdf");

        PhysicalFonts.setRegex(null);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(doc.getInputStream());
        // set up font mapper (optional)
        //Mapper fontMapper = new IdentityPlusMapper();  // Only for Windows, unless you have Microsoft's fonts installed
        Mapper fontMapper = new BestMatchingMapper();  // Good for Linux (and OSX?)
        wordMLPackage.setFontMapper(fontMapper);

        FOSettings foSettings = new FOSettings(wordMLPackage);

        FopFactoryBuilder fopFactoryBuilder = FORendererApacheFOP.getFopFactoryBuilder(foSettings);
        FopFactory fopFactory = fopFactoryBuilder.build();
        FOUserAgent foUserAgent = FORendererApacheFOP.getFOUserAgent(foSettings, fopFactory);

        foUserAgent.setTitle(retDoc.getName());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Don't care what type of exporter you use
        Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

        InputStream is = new ByteArrayInputStream(os.toByteArray());

        retDoc.setContentLength(calculateSizeAndClose(is));
        retDoc.setInputStream(new ByteArrayInputStream(os.toByteArray()));

        return retDoc;
    }

    private long calculateSizeAndClose(InputStream inputStream) {
        try {
            long size = 0;
            byte[] buffer = new byte[1024];
            long rode = inputStream.read(buffer);
            while (rode != -1) {
                size += rode;
                rode = inputStream.read(buffer);
            }
            return size;
        } catch (Exception e) {
            throw new PdfAppException("Error calculating file size", PdfAppException.Type.UNEXPECTED);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    private static String removeExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex != -1) {
            fileName = fileName.substring(0, lastIndex);
        }
        return fileName;
    }
}
