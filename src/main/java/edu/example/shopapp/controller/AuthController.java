package edu.example.shopapp.controller;

import edu.example.shopapp.model.Role;
import edu.example.shopapp.model.User;
import edu.example.shopapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String role,
                                 Model model) {

        if (userRepo.findByUsername(username).isPresent()) {
            model.addAttribute("error", "Username '" + username + "' is already taken.");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("salesman".equalsIgnoreCase(role) ? Role.ROLE_SALESMAN : Role.ROLE_BUYER);
        userRepo.save(user);

        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
