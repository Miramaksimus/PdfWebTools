package edu.uoc.tfg.pdfwebtools.presentation.repository;


import edu.uoc.tfg.pdfwebtools.appexceptions.PdfAppException;
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


@Controller
public class RepositoryController {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);


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
	public Object startSignature(Model model, @RequestParam("file") MultipartFile file) {
		logger.debug("..");
		try {
			if (!file.isEmpty()) {
				//TODO
			} else{
				throw new PdfAppException("És obligatori adjuntar un arxiu pdf per signar", PdfAppException.Type.UNEXPECTED);
			}

			return new ModelAndView("repository", model.asMap());

		} catch (PdfAppException e) {
			logger.error("Error: " + e.getMessage(), e);
			model.addAttribute("message_error", "Tipus d'error: " + e.getType() + ". Missatge: " + e.getMessage());
			return new ModelAndView("redirect:/", model.asMap());

		} catch (Exception e) {
			logger.error("Error: " + e.getMessage(), e);
			model.addAttribute("message_error", "Tipus d'error: " + e.getCause() + ". Missatge: " + e.getMessage());
			return new ModelAndView("redirect:/", model.asMap());
		}

	}


}
