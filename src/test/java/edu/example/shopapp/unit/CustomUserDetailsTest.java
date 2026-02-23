package edu.example.shopapp.unit;

import edu.example.shopapp.model.Role;
import edu.example.shopapp.model.User;
import edu.example.shopapp.security.CustomUserDetails;
import edu.example.shopapp.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomUserDetailsTest {

    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void returnsCorrectUsernameAndPassword() {
        User user = new User(1L, "alice", passwordEncoder.encode("secret"), Role.ROLE_BUYER);
        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("alice", details.getUsername());
        assertTrue(passwordEncoder.matches("secret", details.getPassword()));
    }

    @Test
    void returnsCorrectRole() {
        User user = new User(1L, "alice", "hashed", Role.ROLE_BUYER);
        CustomUserDetails details = new CustomUserDetails(user);

        assertEquals("ROLE_BUYER", details.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void accountIsActiveAndEnabled() {
        User user = new User(1L, "alice", "hashed", Role.ROLE_BUYER);
        CustomUserDetails details = new CustomUserDetails(user);

        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());
    }
}
