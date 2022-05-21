package edu.uoc.tfg.pdfwebtools.bussines.converter;

import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;


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
    public Document convertPfdToDocx(Document doc) throws Exception {
        DocumentECM docx = repositoryService.downloadDocument(doc.getId());
        DocumentECM pdf = procesPdfToDocx(docx);
        Document newDoc = repositoryService.uploadDocument(pdf, doc.getFolder(), doc.getFolder().getUser().getUsername());
        return newDoc;

    }


    private DocumentECM procesDocToPdf(DocumentECM doc) throws Exception {

        DocumentECM retDoc = new DocumentECM();
        retDoc.setName(removeExtension(doc.getName()) + ".pdf");
        retDoc.setMimeType("application/pdf");

        ByteArrayOutputStream os = new ByteArrayOutputStream();


        XWPFDocument document = new XWPFDocument(doc.getInputStream());

        PdfConverter.getInstance().convert(document, os, null);

        InputStream is = new ByteArrayInputStream(os.toByteArray());

        retDoc.setContentLength(calculateSizeAndClose(is));
        retDoc.setInputStream(new ByteArrayInputStream(os.toByteArray()));

        return retDoc;
    }

    private DocumentECM procesPdfToDocx(DocumentECM doc) throws Exception {

        DocumentECM retDoc = new DocumentECM();
        retDoc.setName(removeExtension(doc.getName()) + ".docx");
        retDoc.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        //create a PdfDocument object
        PdfDocument docx = new PdfDocument();
        //load a sample PDF file
        docx.loadFromStream(doc.getInputStream());
        //write to stream
        docx.saveToStream(os, FileFormat.DOCX);
        docx.close();

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
