package edu.example.shopapp.integration;

import edu.example.shopapp.controller.AuthController;
import edu.example.shopapp.repository.OrderRepository;
import edu.example.shopapp.repository.ProductRepository;
import edu.example.shopapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AppContextIntegrationTest {

    @Autowired AuthController    authController;
    @Autowired UserRepository    userRepository;
    @Autowired ProductRepository productRepository;
    @Autowired OrderRepository   orderRepository;

    @Test
    void contextLoads_andKeyBeansAreWired() {
        assertNotNull(authController);
        assertNotNull(userRepository);
        assertNotNull(productRepository);
        assertNotNull(orderRepository);
    }
}
