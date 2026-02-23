package edu.example.shopapp.controller;

import edu.example.shopapp.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        return switch (userDetails.getUser().getRole().name()) {
            case "ROLE_MANAGER"  -> "redirect:/manager/dashboard";
            case "ROLE_SALESMAN" -> "redirect:/salesman/dashboard";
            default              -> "redirect:/buyer/dashboard";
        };
    }
}
