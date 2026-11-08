package reading_tracker.web;

import java.io.IOException;

import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import reading_tracker.config.DefaultDataInitializer;
import reading_tracker.domain.User;
import reading_tracker.repository.UserRepository;

@Controller
public class AccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultDataInitializer defaultDataInitializer;

    public AccountController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                             DefaultDataInitializer defaultDataInitializer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultDataInitializer = defaultDataInitializer;
    }

    @GetMapping("/login")
    public String login(@RequestParam(name = "form", required = false) String form,
                        @RequestParam(name = "error", required = false) String error,
                        @RequestParam(name = "logout", required = false) String logout,
                        Model model) {
        model.addAttribute("showLoginForm", form != null || error != null);
        model.addAttribute("loggedOut", logout != null);
        return "login";
    }

    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/signup")
    @Transactional(rollbackFor = IOException.class)
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                           BindingResult bindingResult) throws IOException {
        if (form.getUsername() != null && userRepository.existsByUsername(form.getUsername().trim())) {
            bindingResult.rejectValue("username", "signup.username.taken");
        }
        if (bindingResult.hasErrors()) {
            return "signup";
        }

        User user = new User(form.getUsername().trim(), passwordEncoder.encode(form.getPassword()),
                form.getEmail(), "USER");
        userRepository.save(user);
        defaultDataInitializer.initializeFor(user);
        return "redirect:/login?registered";
    }
}
