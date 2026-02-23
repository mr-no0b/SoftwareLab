package edu.example.shopapp.config;

import edu.example.shopapp.model.Product;
import edu.example.shopapp.model.Role;
import edu.example.shopapp.model.User;
import edu.example.shopapp.repository.ProductRepository;
import edu.example.shopapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Seeds three demo accounts and sample products on first startup.
 * Kept separate from ShopappApplication so @DataJpaTest tests
 * (which don't load SecurityConfig / PasswordEncoder) won't fail.
 */
@Configuration
public class DataSeedConfig {

    @Bean
    public CommandLineRunner seed(UserRepository userRepo,
                                  ProductRepository productRepo,
                                  PasswordEncoder encoder) {
        return args -> {
            // ── Manager ──────────────────────────────────────────────────────
            if (userRepo.findByUsername("manager1").isEmpty()) {
                User m = new User();
                m.setUsername("manager1");
                m.setPassword(encoder.encode("pass"));
                m.setRole(Role.ROLE_MANAGER);
                userRepo.save(m);
            }

            // ── Salesman ─────────────────────────────────────────────────────
            User salesman;
            if (userRepo.findByUsername("salesman1").isEmpty()) {
                salesman = new User();
                salesman.setUsername("salesman1");
                salesman.setPassword(encoder.encode("pass"));
                salesman.setRole(Role.ROLE_SALESMAN);
                userRepo.save(salesman);
            } else {
                salesman = userRepo.findByUsername("salesman1").get();
            }

            // ── Buyer ─────────────────────────────────────────────────────────
            if (userRepo.findByUsername("buyer1").isEmpty()) {
                User b = new User();
                b.setUsername("buyer1");
                b.setPassword(encoder.encode("pass"));
                b.setRole(Role.ROLE_BUYER);
                userRepo.save(b);
            }

            // ── Sample products ────────────────────────────────────────────
            if (productRepo.findBySalesman(salesman).isEmpty()) {
                productRepo.save(new Product(null, "Laptop Pro",    "High-performance laptop",          1299.99, salesman));
                productRepo.save(new Product(null, "Wireless Mouse", "Ergonomic wireless mouse",          29.99, salesman));
                productRepo.save(new Product(null, "Mechanical Keyboard", "RGB mechanical keyboard",      89.99, salesman));
                productRepo.save(new Product(null, "4K Monitor",    "27-inch 4K IPS display",           449.99, salesman));
                productRepo.save(new Product(null, "USB-C Hub",     "7-in-1 USB-C hub",                  39.99, salesman));
            }
        };
    }
}
