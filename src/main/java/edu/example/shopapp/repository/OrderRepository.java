package edu.example.shopapp.repository;

import edu.example.shopapp.model.Order;
import edu.example.shopapp.model.Product;
import edu.example.shopapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyer(User buyer);
    List<Order> findByProductSalesman(User salesman);   // nested: order.product.salesman

    @Transactional
    void deleteByBuyer(User buyer);

    @Transactional
    void deleteByProduct(Product product);
}
