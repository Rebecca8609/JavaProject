package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Reviews;
import com.example.demo.model.OrderItem;
import com.example.demo.model.OrderItemRepository;
import com.example.demo.model.ReviewPhotos;
import com.example.demo.model.ReviewPhotosRepository;
import com.example.demo.model.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewPhotosRepository reviewPhotosRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    //新增商品評論
    public Reviews addReviewWithImage(Long orderId, Long orderItemId, Integer reviewItemId, MultipartFile[] files, Integer reviewEvaluation, String reviewComment) {
        // 確認該商品屬於指定訂單
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));

        if (!orderItem.getOrder().getOrderId().equals(orderId)) {
            throw new RuntimeException("OrderItem does not belong to the specified order");
        }

        // 建立評論
        Reviews review = new Reviews();
        review.setReviewOrderId(orderItem.getOrder().getOrderId());// 設定訂單 ID
        review.setReviewSellerId(orderItem.getSeller().getUserId());         // 設定賣家 ID
        review.setReviewBuyerId(orderItem.getOrder().getBuyer().getUserId());// 設定買家 ID
        review.setReviewEvaluation(reviewEvaluation);              // 設定評分
        review.setReviewComment(reviewComment);    				   // 設定評論
        review.setReviewItemId(reviewItemId);					   // 設定商品ID
        review.setReviewStatus(1); 								   // 設定狀態
        review.setReviewDate(new Date());                          // 設定評論日期
        review.setOrderItem(orderItem);                            // 設定關聯的 OrderItem

        // 儲存評論到資料庫
        Reviews savedReview = reviewRepository.save(review);

        // 儲存圖片
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String photoPath = imageStorageService.saveImage(file);
                    ReviewPhotos reviewPhoto = new ReviewPhotos();
                    reviewPhoto.setReviews(savedReview);
                    reviewPhoto.setReviewPhoto(photoPath);
                    reviewPhotosRepository.save(reviewPhoto);
                }
            }
        }

        return savedReview;
    }
    
    // 查詢是否已存在該買家對該訂單的評論
    public boolean hasReviewedOrder(int reviewBuyerId, long reviewOrderId) {
        return reviewRepository.existsByReviewBuyerIdAndReviewOrderId(reviewBuyerId, reviewOrderId);
    }
    
    // 評論: 根據訂單編號獲取商品資訊(OrderController)
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        return orderItemRepository.findByOrderOrderId(orderId);
    }
    
    // 評論: 根據訂單商品編號獲取商品資訊(ReviewController)
    public Integer getItemIdByOrderItemId(Long orderItemId) throws Exception {
        // 使用 Repository 查詢對應的 OrderItem
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItemId);
        
        if (orderItemOptional.isPresent()) {
            // 返回對應的 itemId
            return orderItemOptional.get().getItem().getItemId();
        } else {
            // 若找不到，拋出異常或處理錯誤
            throw new Exception("OrderItem not found for ID: " + orderItemId);
        }
    } 
    
    // 查詢所有評論資料並分頁
    public Page<Reviews> findReviewsByPage(int pageNumber) {
        // 每頁 5 筆資料，按照 reviewDate 降序排列
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 5, Sort.Direction.DESC, "reviewDate");
        return reviewRepository.findAll(pageRequest);
    }
    
    //狀態+模糊搜尋+日期範圍搜尋
    public Page<Reviews> searchReviewsWithDateRange(
            Integer pageNumber, String reviewId, String reviewOrderId, String reviewComment, String reviewBuyerId, String reviewStatus,
            String startDate, String endDate) {

        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 5, Sort.Direction.DESC, "reviewDate");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date start = null;
        Date end = null;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = sdf.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = sdf.parse(endDate);
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return Page.empty();
        }

        return reviewRepository.findByFiltersAndDateRange(reviewId, reviewOrderId, reviewComment, reviewBuyerId, reviewStatus, start, end, pageRequest);
    }
    
	//用ID對應每筆資料
	public Reviews findReviewById(Long id) {
		Optional<Reviews> op = reviewRepository.findById(id);
		
		if(op.isPresent()) {
			return op.get();
		}
		return null;
	}
    
	//修改評論的狀態
	@Transactional
	public Reviews updateReviewStatusById(Long id, Integer reviewStatus) {
        Optional<Reviews> op = reviewRepository.findById(id);
		
		if(op.isPresent()) {
			Reviews rvw = op.get();
			rvw.setReviewStatus(reviewStatus);
			return rvw;
		}
		return null;
	}
	
	//利用賣家來找尋評論
	public Page<Reviews> findReviewByBeReviewedAndEvaluation(Integer reviewSellerId, Integer reviewEvaluation, Integer pageNumber) {
	    PageRequest pgr = PageRequest.of(pageNumber - 1, 5, Sort.Direction.DESC, "reviewDate");
	    return reviewRepository.findByBeReviewedAndReviewEvaluation(reviewSellerId, reviewEvaluation, pgr);
	}
	
	//計算某賣家的平均評分
    public double getAverageRating(String reviewSellerId) {
        Double averageRating = reviewRepository.findAverageRatingByBeReviewed(reviewSellerId);
        return averageRating != null ? averageRating : 0.0; // 防止空值返回
    }
    
    //計算某賣家的總評論數量
    public int countReviewsBySellerId(Integer reviewSellerId) {
        return reviewRepository.countByReviewSellerId(reviewSellerId);
    }
    
    //利用reviewItemId(商品ID)來找尋評論
    public Page<Reviews> findByReviewItemIdAndReviewEvaluation(Integer reviewItemId, Integer reviewEvaluation, Integer pageNumber) {
    	PageRequest pgr = PageRequest.of(pageNumber - 1, 5, Sort.Direction.DESC, "reviewDate");
    	return reviewRepository.findByReviewItemIdAndReviewEvaluation(reviewItemId, reviewEvaluation, pgr);
    }
    
	//計算某reviewItemId的平均評分
    public double getItemsAverageRating(Integer reviewItemId) {
        Double averageRating = reviewRepository.findAverageRatingByReviewItemId(reviewItemId);
        return averageRating != null ? averageRating : 0.0; // 防止空值返回
    }
    
}
