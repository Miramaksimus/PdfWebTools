package edu.uoc.tfg.pdfwebtools.presentation.profile;

import edu.uoc.tfg.pdfwebtools.bussines.profile.ProfileService;
import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import edu.uoc.tfg.pdfwebtools.integration.repositories.profile.UserPagingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ManageUsersController {

	private static final Logger logger = LoggerFactory.getLogger(ManageUsersController.class);


	UserPagingRepository userPagingRepository;

	ProfileService profileService;

	@Autowired
	public ManageUsersController(UserPagingRepository userPagingRepository, ProfileService profileService) {
		this.userPagingRepository = userPagingRepository;
		this.profileService = profileService;
	}

	@GetMapping("/users")
	public ModelMap getAll(@PageableDefault(size = 10) Pageable pageable, @RequestParam(name = "value", required = false) String value, Model model, String token_id) {

		logger.debug("token_id...: {}", token_id);

		String principal = SecurityContextHolder.getContext().getAuthentication().getName();
		model.addAttribute("username", principal);

		Pageable sortedByRegisterDate =
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("registerDate").descending());
		if (value != null) {
			model.addAttribute("key", value);
			Page<User> page = userPagingRepository.findByUsernameContainingOrNameContainingAllIgnoreCase(value, value, sortedByRegisterDate);
			return new ModelMap().addAttribute("users", page);
		} else {
			Page<User> page = userPagingRepository.findAll(sortedByRegisterDate);
			return new ModelMap().addAttribute("users", page);
		}
	}

	@RequestMapping(value = "/users/ajax/activate", method = RequestMethod.POST)
	public String ajaxActivate(@RequestParam(name="id") String id,@RequestParam(name = "value", required = false) String value,
							   @PageableDefault(size = 10) Pageable pageable, Model model) {
		logger.debug("id...: {}", id);

		try {
			User user = profileService.activateUser(Integer.valueOf(id));
		} catch (Exception e) {
			logger.error(e.getMessage());
			model.addAttribute("error_action", "An error occurred during activation");
		}

		Pageable sortedByRegisterDate =
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("registerDate").descending());
		Page<User> page = userPagingRepository.findAll(sortedByRegisterDate);
		model.addAttribute("users", page);

		return "/users::users";
	}

	@RequestMapping(value = "/users/ajax/deactivate", method = RequestMethod.POST)
	public String ajaxDeactivate(@RequestParam(name="id") String id,@RequestParam(name = "value", required = false) String value,
							   @PageableDefault(size = 10) Pageable pageable, Model model) {
		logger.debug("id...: {}", id);

		try {
			User user = profileService.deactivateUser(Integer.valueOf(id));
		} catch (Exception e) {
			logger.error(e.getMessage());
			model.addAttribute("error_action", "An error occurred during deactivation");
		}

		Pageable sortedByRegisterDate =
				PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("registerDate").descending());
		Page<User> page = userPagingRepository.findAll(sortedByRegisterDate);
		model.addAttribute("users", page);

		return "/users::users";
	}


}


