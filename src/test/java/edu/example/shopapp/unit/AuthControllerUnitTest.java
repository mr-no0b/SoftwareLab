package edu.example.shopapp.unit;

import edu.example.shopapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerUnitTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepo;

    @Test
    void register_newUser_redirectsToLogin() throws Exception {
        String unique = "bob_" + System.nanoTime();
        mockMvc.perform(post("/register")
                       .param("username", unique)
                       .param("password", "pass")
                       .param("role", "buyer"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login?registered"));

        assertTrue(userRepo.findByUsername(unique).isPresent());
    }

    @Test
    void register_duplicateUsername_returnsRegisterWithError() throws Exception {
        String unique = "bob_" + System.nanoTime();

        // first registration succeeds
        mockMvc.perform(post("/register")
                       .param("username", unique)
                       .param("password", "pass")
                       .param("role", "buyer"))
               .andExpect(status().is3xxRedirection());

        // second registration with same username shows error
        mockMvc.perform(post("/register")
                       .param("username", unique)
                       .param("password", "pass")
                       .param("role", "buyer"))
               .andExpect(status().isOk())
               .andExpect(view().name("register"))
               .andExpect(model().attributeExists("error"));
    }
}
