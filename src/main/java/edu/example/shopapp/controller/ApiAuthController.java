package edu.example.shopapp.controller;

import edu.example.shopapp.security.CustomUserDetails;
import edu.example.shopapp.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiAuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public ApiAuthController(AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * POST /api/login
     * Body: { "username": "alice", "password": "secret" }
     * Returns: { "token": "<jwt>" }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        body.get("username"),
                        body.get("password")
                )
        );
        CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(
                details.getUsername(),
                details.getUser().getRole().name()
        );
        return ResponseEntity.ok(Map.of("token", token));
    }
}
