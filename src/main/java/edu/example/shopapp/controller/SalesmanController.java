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
@RequestMapping("/salesman")
public class SalesmanController {

    private final ProductRepository productRepo;
    private final OrderRepository   orderRepo;
    private final UserRepository    userRepo;
    private final PasswordEncoder   passwordEncoder;

    public SalesmanController(ProductRepository productRepo, OrderRepository orderRepo,
                              UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.productRepo     = productRepo;
        this.orderRepo       = orderRepo;
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(defaultValue = "products") String tab,
                            Model model) {
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("tab",         tab);
        model.addAttribute("myProducts",  productRepo.findBySalesman(userDetails.getUser()));
        model.addAttribute("orders",      orderRepo.findByProductSalesman(userDetails.getUser()));
        return "salesman-dashboard";
    }

    // ── Add product ──────────────────────────────────────────────────────────
    @PostMapping("/products")
    public String addProduct(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam Double price,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setSalesman(userDetails.getUser());
        productRepo.save(p);
        return "redirect:/salesman/dashboard?tab=products";
    }

    // ── Delete own product (also removes its orders) ─────────────────────────
    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product product = productRepo.findById(id).orElse(null);
        if (product != null
                && product.getSalesman().getId().equals(userDetails.getUser().getId())) {
            orderRepo.deleteByProduct(product);
            productRepo.delete(product);
        }
        return "redirect:/salesman/dashboard?tab=products";
    }

    // ── Confirm order ────────────────────────────────────────────────────────
    @PostMapping("/orders/confirm/{id}")
    public String confirmOrder(@PathVariable Long id) {
        orderRepo.findById(id).ifPresent(order -> {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepo.save(order);
        });
        return "redirect:/salesman/dashboard?tab=orders";
    }

    // ── Cancel order ─────────────────────────────────────────────────────────
    @PostMapping("/orders/cancel/{id}")
    public String cancelOrder(@PathVariable Long id) {
        orderRepo.findById(id).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
        });
        return "redirect:/salesman/dashboard?tab=orders";
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
        return "redirect:/salesman/dashboard?tab=profile";
    }
}
