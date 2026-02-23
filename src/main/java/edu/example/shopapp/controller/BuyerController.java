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
@RequestMapping("/buyer")
public class BuyerController {

    private final ProductRepository productRepo;
    private final OrderRepository   orderRepo;
    private final UserRepository    userRepo;
    private final PasswordEncoder   passwordEncoder;

    public BuyerController(ProductRepository productRepo, OrderRepository orderRepo,
                           UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.productRepo     = productRepo;
        this.orderRepo       = orderRepo;
        this.userRepo        = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // ── Dashboard ────────────────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(defaultValue = "shop") String tab,
                            Model model) {
        model.addAttribute("userDetails",  userDetails);
        model.addAttribute("tab",          tab);
        model.addAttribute("products",     productRepo.findAll());
        model.addAttribute("orders",       orderRepo.findByBuyer(userDetails.getUser()));
        return "buyer-dashboard";
    }

    // ── Place order ──────────────────────────────────────────────────────────
    @PostMapping("/order/{productId}")
    public String placeOrder(@PathVariable Long productId,
                             @RequestParam(defaultValue = "1") Integer quantity,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Product product = productRepo.findById(productId).orElseThrow();
        Order order = new Order();
        order.setBuyer(userDetails.getUser());
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus(OrderStatus.PENDING);
        orderRepo.save(order);
        return "redirect:/buyer/dashboard?tab=orders";
    }

    // ── Cancel a pending order ───────────────────────────────────────────────
    @PostMapping("/orders/cancel/{id}")
    public String cancelOrder(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Order order = orderRepo.findById(id).orElse(null);
        if (order != null
                && order.getBuyer().getId().equals(userDetails.getUser().getId())
                && order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepo.save(order);
        }
        return "redirect:/buyer/dashboard?tab=orders";
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
        return "redirect:/buyer/dashboard?tab=profile";
    }
}
