package edu.uoc.tfg.pdfwebtools.presentation.repository;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
import edu.uoc.tfg.pdfwebtools.bussines.alfresco.DocumentECM;
import edu.uoc.tfg.pdfwebtools.bussines.repository.RepositoryService;
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


@Controller
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);

    RepositoryService repositoryService;


    @Autowired
    public RepositoryController() {
    }

    @GetMapping("/repository")
    public ModelAndView repository(Model model, @RequestParam(value = "message_error", required = false) String message_error,
                                   @RequestParam(value = "message_info", required = false) String message_info) {
        logger.debug("repository...: /");
        String principal = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("username", principal);
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
