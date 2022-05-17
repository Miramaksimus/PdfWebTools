package edu.uoc.tfg.pdfwebtools.presentation.repository;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.profile.ProfileService;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
import edu.uoc.tfg.pdfwebtools.integration.entities.Folder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
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
    public ModelAndView repository(Model model, @RequestParam(value = "message_error", required = false) String message_error,
                                   @RequestParam(value = "message_info", required = false) String message_info,
                                   @RequestParam(value = "folder_id", required = false) String folderId) {
        logger.debug("repository...: /");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", username);
        /* User user = userRepository.findByUsername(username);*/
        Folder folder;
        if (folderId != null) {
            folder = repositoryService.findFolderByUser_UsernameAndId(username, Integer.valueOf(folderId));
        } else {
            folder = repositoryService.findFolderByUser_UsernameAndParentFolder(username, null);
        }
        if (folder != null){
            folder.setFolders( folder.getFolders().stream()
                    .sorted(Comparator.comparing(Folder::getName))
                    .collect(Collectors.toCollection(LinkedHashSet::new)
                    ));
            model.addAttribute("folder", folder);
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
    public Object upload(Model model, @RequestParam("file") MultipartFile file) {
        logger.debug("startSignature.../repository/upload");
        try {
            if (!file.isEmpty()) {
                //TODO
            } else {
                throw new PdfAppException("It is mandatory to attach a file", PdfAppException.Type.UNEXPECTED);
            }

            return new ModelAndView("repository", model.asMap());

        } catch (PdfAppException e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getType() + ". Error message: " + e.getMessage());
            return new ModelAndView("redirect:/", model.asMap());

        } catch (Exception e) {
            logger.error("Error: " + e.getMessage(), e);
            model.addAttribute("message_error", "Error type: " + e.getCause() + ". Error message: " + e.getMessage());
            return new ModelAndView("redirect:/", model.asMap());
        }

    }

    @GetMapping("/repository/download")
    public void download(String id, HttpServletResponse response) throws IOException {
        logger.debug("download.../repository/download");
        DocumentECM doc = repositoryService.downloadDocument(Integer.valueOf(id));
        response.setContentType(doc.getMimeType());
        response.setHeader("Content-Disposition", "attachment;filename=" + doc.getName());
        OutputStream out = response.getOutputStream();
        out.write(IOUtils.toByteArray(doc.getFile()));
    }


}
