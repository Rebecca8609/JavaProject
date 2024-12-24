package com.example.demo.controller;

import com.example.demo.model.Brand;
import com.example.demo.model.Category;
import com.example.demo.model.Item;
import com.example.demo.model.ItemOption;
import com.example.demo.model.ItemRepository;
import com.example.demo.model.OrderRepository;
import com.example.demo.model.ReviewRepository;
import com.example.demo.model.UserInfoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Controller
@RequestMapping("/sellerInfo")
public class SellerInfoController {

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping
    public String showDefaultPage() {
        return "defaultPage"; // 返回默認頁面模板
    }

    @GetMapping("/{userId}")
    public String showSellerInfo(@PathVariable Integer userId, Model model) {
        // 1. 根據 userId 找到對應的 UserInfo
        userInfoRepository.findById(userId).ifPresent(userInfo -> {
            // 賣家名稱
            model.addAttribute("sellerName", userInfo.getUserName());
            
            // 2. 呼叫 itemRepository.countByUserInfo(...) 計算該賣家的商品數量
            int itemCount = itemRepository.countByUserInfo(userInfo);
            model.addAttribute("itemCount", itemCount);
        });

        // 3. 取得該賣家的平均評價 (averageRating)
        Double averageRating = reviewRepository.findAverageRatingByBeReviewed(String.valueOf(userId));
        
        // 4. 取得該賣家的評價總數 (reviewCount)
        int reviewCount = reviewRepository.countByReviewSellerId(userId);

        // 5. 將平均評價與評價總數放入 model
        double ratingValue = (averageRating != null) ? averageRating : 0.0;
        model.addAttribute("sellerRating", ratingValue);
        model.addAttribute("reviewCount", reviewCount);

        // 6. 把 userId 也放到 model，以便前端後續呼叫
        model.addAttribute("userId", userId);

        return "sellerInfo"; // 返回賣家資訊頁面的 Thymeleaf 模板
    }

    @RestController
    @RequestMapping("/api/seller")
    public static class SellerApiController {

        @Autowired
        private ItemRepository itemRepository;

        @GetMapping("/{userId}/products")
        public Map<String, Object> getProductsByUserId(
                @PathVariable Integer userId,
                @RequestParam(value = "keyword", required = false) String keyword,
                @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                @RequestParam(value = "categoryId", required = false) Long categoryId,
                @RequestParam(value = "brandIds", required = false) String brandIds,
                Pageable pageable) {

            Specification<Item> specification = (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                // 避免因為多個 ItemOption 造成重複結果
                query.distinct(true);

                // (1) 賣家篩選：相等於 userId
                predicates.add(criteriaBuilder.equal(root.get("userInfo").get("userId"), userId));

                // (2) 關鍵字搜尋 — 包含 itemName、brandName、categoryName、optionName
                if (keyword != null && !keyword.trim().isEmpty()) {
                    // 建立必要的 Join
                    Join<Item, Brand> brandJoin = root.join("brand", JoinType.LEFT);
                    Join<Item, Category> categoryJoin = root.join("category", JoinType.LEFT);
                    Join<Item, ItemOption> optionJoin = root.join("itemOption", JoinType.LEFT);

                    // 建立各個欄位的 like 條件
                    Predicate itemNameLike = criteriaBuilder.like(root.get("itemName"), "%" + keyword + "%");
                    Predicate brandNameLike = criteriaBuilder.like(brandJoin.get("brandName"), "%" + keyword + "%");
                    Predicate categoryNameLike = criteriaBuilder.like(categoryJoin.get("categoryName"), "%" + keyword + "%");
                    Predicate optionNameLike = criteriaBuilder.like(optionJoin.get("optionName"), "%" + keyword + "%");

                    // 將這四個條件用 OR 連接
                    Predicate keywordPredicate = criteriaBuilder.or(itemNameLike, brandNameLike, categoryNameLike, optionNameLike);

                    // 將關鍵字條件加入 predicates
                    predicates.add(keywordPredicate);
                }

                // (3) 價格篩選 (若有傳 minPrice、maxPrice)
                if (minPrice != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("itemPrice"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("itemPrice"), maxPrice));
                }

                // (4) 分類篩選 (若有傳 categoryId)
                if (categoryId != null) {
                    predicates.add(criteriaBuilder.equal(root.get("category").get("categoryId"), categoryId));
                }

                // (5) 品牌篩選 (若有傳 brandIds)
                if (brandIds != null && !brandIds.isEmpty()) {
                    List<String> brandList = Arrays.stream(brandIds.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                    Join<Item, Brand> brandJoinForFilter = root.join("brand", JoinType.LEFT);
                    predicates.add(brandJoinForFilter.get("brandName").in(brandList));
                }

                // 組合所有條件 (AND)
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            };

            Page<Item> pagedResult = itemRepository.findAll(specification, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("content", pagedResult.getContent().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemId", item.getItemId());
                itemMap.put("itemName", item.getItemName());
                itemMap.put("itemPhoto", item.getItemPhoto().isEmpty()
                        ? null
                        : encodeImage(item.getItemPhoto().get(0).getPhotoFile()));
                itemMap.put("itemPrice", item.getItemPrice());
                return itemMap;
            }).collect(Collectors.toList()));
            response.put("totalPages", pagedResult.getTotalPages());

            return response;
        }

        private static String encodeImage(byte[] image) {
            return image == null ? null : "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(image);
        }
    }
}
