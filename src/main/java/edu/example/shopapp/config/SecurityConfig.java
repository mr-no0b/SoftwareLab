package edu.example.shopapp.config;

import edu.example.shopapp.security.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Exposes AuthenticationManager so ApiAuthController can use it. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /** After form login, redirect each role to its own dashboard. */
    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (HttpServletRequest req, HttpServletResponse res, Authentication auth) -> {
            String role = auth.getAuthorities().iterator().next().getAuthority();
            switch (role) {
                case "ROLE_MANAGER"  -> res.sendRedirect("/manager/dashboard");
                case "ROLE_SALESMAN" -> res.sendRedirect("/salesman/dashboard");
                default              -> res.sendRedirect("/buyer/dashboard");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtFilter jwtFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.disable()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**",
                                 "/h2-console/**",
                                 "/register", "/login", "/",
                                 "/api/login")          // JWT login endpoint is public
                    .permitAll()
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/salesman/**").hasRole("SALESMAN")
                .requestMatchers("/buyer/**").hasRole("BUYER")
                .anyRequest().authenticated()
            )
            // JWT filter runs before Spring's own username/password filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(roleBasedSuccessHandler())
                .failureUrl("/login?error")
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
