package edu.uoc.tfg.pdfwebtools.presentation.profile;

import edu.uoc.tfg.pdfwebtools.bussines.profile.ProfileService;
import edu.uoc.tfg.pdfwebtools.integration.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class RegisterController {

    ProfileService profileService;

    @Autowired
    public RegisterController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/register")
    public ModelAndView getRegister(Model model) {

        model.addAttribute("user", new User());
        return new ModelAndView("register", model.asMap());

    }

    @RequestMapping("/register-error")
    public String registerError(Model model) {
        model.addAttribute("errors", true);
        return "register";
    }

    @PostMapping("/register/sign-up")
    String signUp(Model model, @ModelAttribute User user) {

        System.out.println(user);
        profileService.registerUser(user);

        return "redirect:/login";
    }

}
