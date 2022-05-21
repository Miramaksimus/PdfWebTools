package edu.uoc.tfg.pdfwebtools.presentation.converter;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.converter.ConverterService;
import edu.uoc.tfg.pdfwebtools.bussines.profile.ProfileService;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;


@Controller
public class ConverterController {

    private static final Logger logger = LoggerFactory.getLogger(ConverterController.class);

    RepositoryService repositoryService;

    ProfileService profileService;

    ConverterService converterService;


    @Autowired
    public ConverterController(RepositoryService repositoryService, ProfileService profileService, ConverterService converterService) {
        this.repositoryService = repositoryService;
        this.profileService = profileService;
        this.converterService = converterService;
    }

    @GetMapping("/converter/pdf/to/docx")
    public Object convertPdfToDoc(Model model, @RequestParam(value = "folder_id", required = true) String folderId,
                                  @RequestParam(value = "doc_id", required = true) String docId) {
        logger.debug("convertDocToPfd.../converter/doctopdf  docId: {} , folderId {}", docId, folderId);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Document doc = repositoryService.findDocumentByIdAdnFolderId(Integer.valueOf(docId), Integer.valueOf(folderId));
            if (doc != null && doc.getFolder().getUser().getUsername().equals(username)) {
                model.addAttribute("folder_id", folderId);
                validatePdfFormat(doc);
                Document convertedDoc = converterService.convertPfdToDocx(doc);
                if (convertedDoc != null) model.addAttribute("doc_id", convertedDoc.getId());
            } else {
                throw new PdfAppException("Document not exist or user not have permissions", PdfAppException.Type.NOT_FOUND);
            }

        } catch (PdfAppException e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getType() + ". Error message: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getCause() + ". Error message: " + e.getMessage());
        }
        return new ModelAndView("redirect:/repository", model.asMap());

    }


    @GetMapping("/converter/docx/to/pdf")
    public Object convertDocToPfd(Model model, @RequestParam(value = "folder_id", required = true) String folderId,
                                  @RequestParam(value = "doc_id", required = true) String docId) {
        logger.debug("convertDocToPfd.../converter/doctopdf  docId: {} , folderId {}", docId, folderId);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Document doc = repositoryService.findDocumentByIdAdnFolderId(Integer.valueOf(docId), Integer.valueOf(folderId));
            if (doc != null && doc.getFolder().getUser().getUsername().equals(username)) {
                model.addAttribute("folder_id", folderId);
                validateDocFormat(doc);
                Document convertedDoc = converterService.convertDocxToPfd(doc);
                if (convertedDoc != null) model.addAttribute("doc_id", convertedDoc.getId());
            } else {
                throw new PdfAppException("Document not exist or user not have permissions", PdfAppException.Type.NOT_FOUND);
            }

        } catch (PdfAppException e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getType() + ". Error message: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getCause() + ". Error message: " + e.getMessage());
        }
        return new ModelAndView("redirect:/repository", model.asMap());

    }

    private void validateDocFormat(Document doc) {
        List<String> mimes = Arrays.asList("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        if (!mimes.contains(doc.getMimeType()))
            throw new PdfAppException("This file type is not supported", PdfAppException.Type.CONSTRAINT_VIOLATION);
    }

    private void validatePdfFormat(Document doc) {
        List<String> mimes = Arrays.asList("application/pdf");
        if (!mimes.contains(doc.getMimeType()))
            throw new PdfAppException("This file type is not supported", PdfAppException.Type.CONSTRAINT_VIOLATION);
    }

}
