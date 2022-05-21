package edu.uoc.tfg.pdfwebtools.bussines.scanner;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Barcode;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import edu.uoc.tfg.pdfwebtools.integration.repos.repository.DocumentRepository;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class ScannerServiceBean implements ScannerService {

    private static final Logger logger = LoggerFactory.getLogger(ScannerServiceBean.class);

    RepositoryService repositoryService;

    DocumentRepository documentRepository;

    @Autowired
    public ScannerServiceBean(RepositoryService repositoryService, DocumentRepository documentRepository) {
        this.repositoryService = repositoryService;
        this.documentRepository = documentRepository;
    }

    @Override
    public Document scanCodes(Document doc) throws IOException {
        DocumentECM repoDoc = repositoryService.downloadDocument(doc.getId());

        Set<String> codesText = procesDocument(repoDoc);
        Set<Barcode> barcodes = new LinkedHashSet<>();
        for (String code : codesText) {
            Barcode barcode = new Barcode();
            barcode.setText(code);
            barcode.setDocument(doc);
            barcodes.add(barcode);
        }
        doc.setBarcodes(barcodes);
        return documentRepository.save(doc);
    }

    private Set<String> procesDocument(DocumentECM repoDoc) throws IOException {

        Set<String> codes = new LinkedHashSet<>();
        PDDocument document = null;
        try {
            document = PDDocument.load(repoDoc.getInputStream());
            ImagesFromPdf scanner = new ImagesFromPdf();
            int pageNum = 0;
            for (PDPage page : document.getPages()) {
                pageNum++;
                logger.debug("Processing page: " + pageNum);
                scanner.processPage(page);
            }
            String code = null;
            for (BufferedImage image : scanner.getImages()) {
                code = readCode(image);
                if (code != null) codes.add(code);
            }
        } finally {
            if (document != null) {
                document.close();
            }
        }
        return codes;
    }

    private static String readCode(BufferedImage image) {
        BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(image);
        HybridBinarizer hb = new HybridBinarizer(bils);
        BinaryBitmap binaryBitmap = new BinaryBitmap(hb);
        Result result = null;
        try {
            result = new MultiFormatReader().decode(binaryBitmap);
            if (result != null) logger.info("Code/QrCode code read: " + result.getText());
        } catch (NotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        return result.getText();
    }

    public static class ImagesFromPdf extends PDFStreamEngine {

        private List<BufferedImage> images;

        public ImagesFromPdf() throws IOException {
            this.images = new ArrayList<>();
        }

        public List<BufferedImage> getImages() {
            return images;
        }

        @Override
        protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
            String operation = operator.getName();
            if ("Do".equals(operation)) {
                COSName objectName = (COSName) operands.get(0);
                PDXObject xobject = getResources().getXObject(objectName);
                if (xobject instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) xobject;
                    images.add(image.getImage());
                } else if (xobject instanceof PDFormXObject) {
                    PDFormXObject form = (PDFormXObject) xobject;
                    showForm(form);
                }
            } else {
                super.processOperator(operator, operands);
            }
        }

    }


}
