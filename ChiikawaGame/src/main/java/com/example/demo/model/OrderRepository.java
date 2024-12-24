package com.example.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    List<Order> findByBuyer_UserId(Long userId);

    Optional<Order> findByOrderId(Long orderId);

    List<Order> findByOrderStatus(String orderStatus);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.seller.userId = :sellerId")
    List<Order> findAllBySellerUserId(@Param("sellerId") Long sellerId);

    @Query("""
        SELECT o FROM Order o 
        LEFT JOIN FETCH o.orderItems oi 
        LEFT JOIN FETCH oi.item i 
        LEFT JOIN FETCH oi.seller s 
        WHERE o.orderId = :orderId
    """)
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);
}
