package edu.uoc.tfg.pdfwebtools.presentation.signature;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.profile.ProfileService;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.bussines.signature.SignatureService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;


@Controller
public class SignatureController {

    private static final Logger logger = LoggerFactory.getLogger(SignatureController.class);

    RepositoryService repositoryService;

    ProfileService profileService;

    SignatureService signatureService;


    @Autowired
    public SignatureController(RepositoryService repositoryService, ProfileService profileService, SignatureService signatureService) {
        this.repositoryService = repositoryService;
        this.profileService = profileService;
        this.signatureService = signatureService;
    }

    @GetMapping("/signature")
    public Object scantSignaturesFromPfd(Model model, @RequestParam(value = "folder_id", required = true) String folderId,
                                         @RequestParam(value = "doc_id", required = true) String docId) {
        logger.debug("scantSignaturesFromPfd.../signature/  docId: {} , folderId {}", docId, folderId);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Document doc = repositoryService.findDocumentByIdAdnFolderId(Integer.valueOf(docId), Integer.valueOf(folderId));
            if (doc != null && doc.getFolder().getUser().getUsername().equals(username)) {
                model.addAttribute("folder_id", folderId);
                validatePdfFormat(doc);
                Document scannedDoc = signatureService.scanSignaturesFromPfd(doc);
                if (scannedDoc != null) model.addAttribute("doc_id", scannedDoc.getId());
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


    private void validatePdfFormat(Document doc) {
        List<String> mimes = Collections.singletonList("application/pdf");
        if (!mimes.contains(doc.getMimeType()))
            throw new PdfAppException("This file type is not supported", PdfAppException.Type.CONSTRAINT_VIOLATION);
    }

}
