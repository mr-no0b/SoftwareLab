package edu.example.shopapp.repository;

import edu.example.shopapp.model.Product;
import edu.example.shopapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findBySalesman(User salesman);
}
