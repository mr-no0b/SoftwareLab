package edu.example.shopapp.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired MockMvc mockMvc;

    @Test
    void loginPage_isAccessibleWithout_authentication() throws Exception {
        mockMvc.perform(get("/login"))
               .andExpect(status().isOk())
               .andExpect(view().name("login"));
    }

    @Test
    void register_newBuyer_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/register")
                       .param("username", "testBuyer_" + System.nanoTime())
                       .param("password", "pass123")
                       .param("role", "buyer"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrl("/login?registered"));
    }

    @Test
    void protectedPage_redirectsToLogin_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/buyer/dashboard"))
               .andExpect(status().is3xxRedirection());
    }

    @Test
    void apiLogin_withValidCredentials_returnsJwtToken() throws Exception {
        // register a user first
        String username = "jwtUser_" + System.nanoTime();
        mockMvc.perform(post("/register")
                       .param("username", username)
                       .param("password", "pass123")
                       .param("role", "buyer"))
               .andExpect(status().is3xxRedirection());

        // call JWT login endpoint
        MvcResult result = mockMvc.perform(post("/api/login")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"username\":\"" + username + "\",\"password\":\"pass123\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").exists())
               .andReturn();

        String response = result.getResponse().getContentAsString();
        assertTrue(response.contains("token"));
    }
}
