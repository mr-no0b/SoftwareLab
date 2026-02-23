package edu.example.shopapp.controller;

import edu.example.shopapp.model.*;
import edu.example.shopapp.repository.*;
import edu.example.shopapp.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final UserRepository    userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository   orderRepo;
    private final PasswordEncoder   passwordEncoder;

    public ManagerController(UserRepository userRepo, ProductRepository productRepo,
                             OrderRepository orderRepo, PasswordEncoder passwordEncoder) {
        this.userRepo        = userRepo;
        this.productRepo     = productRepo;
        this.orderRepo       = orderRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(defaultValue = "users") String tab,
                            Model model) {
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("tab",         tab);
        model.addAttribute("buyers",      userRepo.findByRole(Role.ROLE_BUYER));
        model.addAttribute("salesmens",   userRepo.findByRole(Role.ROLE_SALESMAN));
        model.addAttribute("products",    productRepo.findAll());
        model.addAttribute("orders",      orderRepo.findAll());
        return "manager-dashboard";
    }

    // ── Delete a user (buyer or salesman) ────────────────────────────────────
    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user != null) {
            if (user.getRole() == Role.ROLE_BUYER) {
                orderRepo.deleteByBuyer(user);
            } else if (user.getRole() == Role.ROLE_SALESMAN) {
                productRepo.findBySalesman(user).forEach(product -> {
                    orderRepo.deleteByProduct(product);
                    productRepo.delete(product);
                });
            }
            userRepo.delete(user);
        }
        return "redirect:/manager/dashboard?tab=users";
    }

    // ── Update profile ───────────────────────────────────────────────────────
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String username,
                                @RequestParam(required = false) String password,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        user.setUsername(username);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userRepo.save(user);
        return "redirect:/manager/dashboard?tab=profile";
    }
}
