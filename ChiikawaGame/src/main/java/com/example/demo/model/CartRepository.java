package com.example.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByBuyer(UserInfo buyer);
    
    Optional<Cart> findByBuyerUserId(Integer buyerId);

}
