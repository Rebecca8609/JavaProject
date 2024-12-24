package com.example.demo.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends JpaRepository<Reviews, Long> {

    List<Reviews> findByReviewOrderId(Long reviewOrderId);
 
    //模糊搜尋+日期範圍查詢+狀態
    @Query(value = "SELECT * FROM reviews r WHERE " +
            "(" +
            "(:reviewId IS NULL OR CAST(r.reviewId AS NVARCHAR) LIKE CONCAT('%', :reviewId, '%')) OR " +
            "(:reviewOrderId IS NULL OR CAST(r.reviewOrderId AS NVARCHAR) LIKE CONCAT('%', :reviewOrderId, '%')) OR " +
            "(:reviewComment IS NULL OR r.reviewComment LIKE CONCAT('%', :reviewComment, '%')) OR " +
            "(:reviewBuyerId IS NULL OR CAST(r.reviewBuyerId AS NVARCHAR) LIKE CONCAT('%', :reviewBuyerId, '%'))" +
            ")" +
            "AND (:reviewStatus IS NULL OR r.reviewStatus = :reviewStatus) " +
            "AND (:startDate IS NULL OR r.reviewDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.reviewDate <= :endDate)", 
            nativeQuery = true)
    Page<Reviews> findByFiltersAndDateRange(
        @Param("reviewId") String reviewId,
        @Param("reviewOrderId") String reviewOrderId,
        @Param("reviewComment") String reviewComment,
        @Param("reviewBuyerId") String reviewBuyerId,
        @Param("reviewStatus") String reviewStatus,
        @Param("startDate") Date startDate,
        @Param("endDate") Date endDate,
        Pageable pageable);
    
    //用賣家和評分找出評論
    @Query(value = "SELECT * FROM reviews r WHERE r.reviewSellerId = :reviewSellerId AND (:reviewEvaluation IS NULL OR r.reviewEvaluation = :reviewEvaluation)", nativeQuery = true)
    Page<Reviews> findByBeReviewedAndReviewEvaluation(@Param("reviewSellerId") Integer reviewSellerId, 
    	    @Param("reviewEvaluation") Integer reviewEvaluation, 
    	    Pageable pageable);
    
    // 自訂查詢：計算某個賣家 reviewSellerId 的平均評分
    @Query("SELECT AVG(r.reviewEvaluation) FROM Reviews r WHERE r.reviewSellerId = :reviewSellerId")
    Double findAverageRatingByBeReviewed(@Param("reviewSellerId") String reviewSellerId);
    
    // 根據賣家 reviewSellerId 計算評論數量
    int countByReviewSellerId(Integer integer); //Mantle
    
    // 買家對比reviewOrderId是否存在
    boolean existsByReviewBuyerIdAndReviewOrderId(int reviewBuyerId, long reviewOrderId);

    //用reviewItemId和reviewEvaluation找出評論
    @Query(value = "SELECT * FROM reviews r WHERE r.reviewItemId = :reviewItemId AND (:reviewEvaluation IS NULL OR r.reviewEvaluation = :reviewEvaluation)", nativeQuery = true)
    Page<Reviews> findByReviewItemIdAndReviewEvaluation(@Param("reviewItemId") Integer reviewItemId, 
    		@Param("reviewEvaluation") Integer reviewEvaluation, 
    		Pageable pageable);
    
    // 自訂查詢：計算某個 reviewItemId 的平均評分
    @Query("SELECT AVG(reviewEvaluation) FROM Reviews r WHERE r.reviewItemId = :reviewItemId")
    Double findAverageRatingByReviewItemId(@Param("reviewItemId") Integer reviewItemId);
    
    
    
 // 計算某商品的評價總數
    long countByReviewItemId(Integer itemId); //Mantle
    
    List<Reviews> findByReviewItemId(Integer reviewItemId); //Mantle
    
    Page<Reviews> findByReviewItemId(Integer reviewItemId, Pageable pageable); //Mantle
    
    
}