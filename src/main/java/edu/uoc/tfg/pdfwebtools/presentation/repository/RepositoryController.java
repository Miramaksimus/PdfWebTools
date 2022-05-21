package edu.uoc.tfg.pdfwebtools.presentation.repository;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.profile.ProfileService;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Document;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    RepositoryService repositoryService;

    ProfileService profileService;


    @Autowired
    public RepositoryController(RepositoryService repositoryService, ProfileService profileService) {
        this.repositoryService = repositoryService;
        this.profileService = profileService;
    }

    @GetMapping("/repository")
    public ModelAndView consultAndListDocuments(Model model, @RequestParam(value = "message_error", required = false) String message_error,
                                                @RequestParam(value = "message_info", required = false) String message_info,
                                                @RequestParam(value = "folder_id", required = false) String folderId,
                                                @RequestParam(value = "doc_id", required = false) String docId) {
        logger.debug("repository...: /");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        model.addAttribute("newFolder", new Folder());
        model.addAttribute("newDoc", new UplodedDocument());

        Folder folder;
        try {
            if (folderId != null) {
                folder = repositoryService.findFolderByUser_UsernameAndId(username, Integer.valueOf(folderId));
            } else {
                folder = repositoryService.findFolderByUser_UsernameAndParentFolder(username, null);
            }
            if (folder != null) {

                folder.setFolders(folder.getFolders().stream()
                        .sorted((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()))
                        .collect(Collectors.toCollection(LinkedHashSet::new)
                        ));

                folder.setDocuments(folder.getDocuments().stream()
                        .sorted((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()))
                        .collect(Collectors.toCollection(LinkedHashSet::new)
                        ));
                model.addAttribute("folder", folder);

                if (docId != null) {
                    Document doc = repositoryService.findDocumentByIdAdnFolderId(Integer.valueOf(docId), folder.getId());
                    model.addAttribute("selected_doc", doc);
                    model.addAttribute("selected_doc_id", doc.getId());
                }
            }

        } catch (NumberFormatException e) {
            message_error = "Error type: " + e.getCause() + ". Error message: " + e.getMessage();
        }
        if (message_error != null) model.addAttribute("message_error", message_error);
        if (message_info != null) model.addAttribute("message_info", message_info);

        return new ModelAndView("repository", model.asMap());

    }

    @GetMapping("/")
    public ModelAndView root(Model model) {
        logger.debug("root...: /");
        return new ModelAndView("redirect:/repository", model.asMap());

    }

    @PostMapping("/repository/upload")
    public Object uploadDocument(Model model, @ModelAttribute UplodedDocument newDoc,
                                 @RequestParam("file") MultipartFile file) {
        logger.debug("upload.../repository/upload");
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!file.isEmpty()) {
                logger.debug("Filename: " + newDoc.getFile().getOriginalFilename());
                validateFile(file);
                model.addAttribute("folder_id", newDoc.getParentFolder().getId());
                Document doc = repositoryService.uploadDocument(newDoc.getFile(), newDoc.getParentFolder(), username);
            } else {
                throw new PdfAppException("It is mandatory to attach a file", PdfAppException.Type.CONSTRAINT_VIOLATION);
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


    @GetMapping("/repository/delete")
    public Object deleteDocument(Model model, @RequestParam(value = "folder_id", required = true) String folderId,
                                 @RequestParam(value = "id", required = true) String docId) {
        logger.debug("deleteDocument.../repository/delete  docId: " + docId);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Document doc = repositoryService.findDocumentByIdAdnFolderId(Integer.valueOf(docId), Integer.valueOf(folderId));
            if (doc != null && doc.getFolder().getUser().getUsername().equals(username)) {
                model.addAttribute("folder_id", folderId);
                Boolean isDeleted = repositoryService.deleteDocument(doc);
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


    @GetMapping("/repository/download")
    public void downloadDocument(String id, HttpServletResponse response) throws IOException {
        logger.debug("download.../repository/download");
        DocumentECM doc = repositoryService.downloadDocument(Integer.valueOf(id));
        response.setContentType(doc.getMimeType());
        response.setHeader("Content-Disposition", "attachment;filename=" + doc.getName());
        OutputStream out = response.getOutputStream();
        out.write(IOUtils.toByteArray(doc.getInputStream()));
    }

    @PostMapping("/repository/folder")
    public Object createFolder(Model model, @ModelAttribute Folder newFolder) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Folder folder = repositoryService.createFolder(username, newFolder);
            model.addAttribute("folder_id", folder.getParentFolder().getId());
        } catch (PdfAppException e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getType() + ". Error message: " + e.getMessage());

        } catch (Exception e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getCause() + ". Error message: " + e.getMessage());
        }
        return new ModelAndView("redirect:/repository", model.asMap());
    }

    /*    private methods*/

    private void validateFile(MultipartFile file) {
        List<String> mimes = Arrays.asList("application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain", "image/png", "image/jpeg");
        if (!mimes.contains(file.getContentType()))
            throw new PdfAppException("This file type is not supported", PdfAppException.Type.CONSTRAINT_VIOLATION);
    }


}
