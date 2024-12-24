package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;

import com.example.demo.dto.ForumArticleDTO;
import com.example.demo.model.ForumArticlePhotos;
import com.example.demo.model.ForumArticlePhotosRepository;
import com.example.demo.model.ForumArticles;
import com.example.demo.model.LoginBean;
import com.example.demo.model.UserInfo;
import com.example.demo.model.UserInfoRepository;
import com.example.demo.service.ForumArticlesService;
import com.example.demo.service.ImageStorageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpSession;

@Slf4j
@Controller
@RequestMapping("/forum")
public class ForumArticlesController {
	
	@Autowired
	private ForumArticlesService forumArticlesService;
	
	@Autowired
	private ForumArticlePhotosRepository forumArticlePhotosRepository;
	
	@Autowired
	private ImageStorageService imageStorageService;
	
    @Autowired
    private UserInfoRepository userInfoRepository; // 添加這行
	
	//　顯示文章後台頁面 http://localhost:8080/forum/forumArticlesManagement
	@GetMapping("forumArticlesManagement")
	public String forumArticleList() {
		return "forum/forumArticlesManagement";
	}
	
	// 後台:取得所有評論的分頁資料(後端是否能正常抓取)
	// http://localhost:8080/forum/forumArticlesAll
	@GetMapping("/forumArticlesAll")
	@ResponseBody
	public Page<ForumArticleDTO> getAllForumArticles(
	        @PageableDefault(size = 10, sort = "forumArticleId", direction = Sort.Direction.DESC) Pageable pageable,
	        @RequestParam(required = false) String category,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        @RequestParam(required = false) Integer status) {

	    Specification<ForumArticles> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();
	        
	        // 根據狀態篩選
	        if (status != null) {
	            predicates.add(cb.equal(root.get("forumArticleStatus"), status));
	        }
	        
	        // 處理日期範圍搜尋
	        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
	            try {
	                LocalDate start = LocalDate.parse(startDate);
	                LocalDate end = LocalDate.parse(endDate);
	                LocalDateTime startOfDay = start.atStartOfDay();
	                LocalDateTime endOfDay = end.plusDays(1).atStartOfDay();
	                predicates.add(cb.between(
	                    root.get("forumArticleCreatedDate"),
	                    startOfDay,
	                    endOfDay
	                ));
	            } catch (DateTimeParseException e) {
	                log.error("日期格式錯誤", e);
	            }
	        }

	        // 處理分類與關鍵字搜尋
	        if (category != null && !category.isEmpty() && search != null && !search.isEmpty()) {
	            try {
	                switch (category) {
	                    case "forumArticleId":
	                        // 處理 ID 搜尋，嘗試轉換為數字
	                        try {
	                            Long searchId = Long.parseLong(search);
	                            predicates.add(cb.equal(root.get("forumArticleId"), searchId));
	                        } catch (NumberFormatException e) {
	                            log.error("ID 格式錯誤", e);
	                        }
	                        break;
	                        
	                    case "forumArticleTitle":
	                        predicates.add(cb.like(root.get("forumArticleTitle"), "%" + search + "%"));
	                        break;
	                        
	                    case "forumArticleContent":
	                        predicates.add(cb.like(root.get("forumArticleContent"), "%" + search + "%"));
	                        break;
	                        
	                    case "userId":
	                        // 處理關聯表的搜尋
	                        jakarta.persistence.criteria.Join<ForumArticles, UserInfo> userJoin = 
	                            root.join("userInfo");
	                        predicates.add(cb.like(userJoin.get("userId"), "%" + search + "%"));
	                        break;
	                        
	                    default:
	                        throw new IllegalArgumentException("無效的分類欄位");
	                }
	            } catch (IllegalArgumentException e) {
	                log.error("無效的分類欄位: {}", category, e);
	                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無效的分類欄位");
	            }
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    return forumArticlesService.getArticlesWithSpec(spec, pageable);
	}
	
	// 顯示文章首頁 http://localhost:8080/forum/forumArticlesDisplay
	@GetMapping("/forumArticlesDisplay")
	public String forumArticlesDisplay() {
		return "forum/forumArticlesDisplay";
	}

	// 首頁:取得forumArticleStatus=1的全部資料
	// http://localhost:8080/forum/forumArticles?p=1
	@GetMapping("/forumArticles")
	@ResponseBody  // 添加這個註解
	public Page<ForumArticleDTO> getArticles(
	        @PageableDefault(size = 9, sort = "forumArticleCreatedDate", direction = Sort.Direction.DESC) Pageable pageable,
	        @RequestParam(required = false) String category,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate) {

	    // 建立查詢規則
	    Specification<ForumArticles> spec = (root, query, cb) -> {
	    	
	    	// 使用 fetch join 一次性獲取關聯資料
	    	if (query.getResultType().equals(ForumArticles.class)) {
	    		root.fetch("userInfo", JoinType.LEFT);
	    	}
	    	
	        List<Predicate> predicates = new ArrayList<>();
	        
	        // 分類篩選
	        if (category != null && !category.isEmpty()) {
	            predicates.add(cb.equal(root.get("forumArticleItemType"), category));
	        }
	        
	        // 文字搜尋（標題和內容）
	        if (search != null && !search.isEmpty()) {
	            String searchPattern = "%" + search.toLowerCase() + "%";
	            predicates.add(cb.or(
	                cb.like(cb.lower(root.get("forumArticleTitle")), searchPattern),
	                cb.like(cb.lower(root.get("forumArticleContent")), searchPattern)
	            ));
	        }
	        
	        // 日期範圍搜尋
	        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
	            try {
	                LocalDate start = LocalDate.parse(startDate);
	                LocalDate end = LocalDate.parse(endDate);

	                LocalDateTime startOfDay = start.atStartOfDay();
	                LocalDateTime endOfDay = end.plusDays(1).atStartOfDay();

	                predicates.add(cb.between(
	                    root.get("forumArticleCreatedDate"),
	                    startOfDay,
	                    endOfDay
	                ));
	            } catch (DateTimeParseException e) {
	                // 處理日期格式錯誤
	            }
	        }
	        
	        // 只顯示狀態為1的文章
	        predicates.add(cb.equal(root.get("forumArticleStatus"), 1));
	        
	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    return forumArticlesService.getArticlesWithSpec(spec, pageable);
	}
	
	// 首頁:新增文章
    @PostMapping("/addArticle")
    @ResponseBody
    public ResponseEntity<?> addArticle(
            @RequestParam String forumArticleTitle,
            @RequestParam String forumArticleContent,
            @RequestParam String forumArticleItemType,
            @RequestParam BigDecimal forumArticlePrice,
            @RequestParam String forumArticleItemCondition,
            @RequestParam(required = false) List<MultipartFile> images,
            HttpSession session) {
        
        try {
        	
            // 從 session 中取得 LoginBean
            LoginBean user = (LoginBean) session.getAttribute("user");
            // 檢查使用者是否登入
            if (user == null) {
                throw new IllegalStateException("使用者尚未登入，無法取得 userId");
            }
            
            // 取得 userId 並找到對應的 UserInfo
            Integer userId = Integer.valueOf(user.getUserId());
            UserInfo userInfo = userInfoRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("找不到使用者資訊"));
        	
            ForumArticles article = new ForumArticles();
            article.setForumArticleTitle(forumArticleTitle);
            article.setForumArticleContent(forumArticleContent);
            article.setForumArticleItemType(forumArticleItemType);
            article.setForumArticleStatus(1);
            article.setForumArticlePrice(forumArticlePrice);  // 設置價格
            article.setForumArticleItemCondition(forumArticleItemCondition);  // 設置商品狀態      
            article.setUserInfo(userInfo);  // 直接設置 UserInfo 物件
            
            ForumArticles savedArticle = forumArticlesService.addArticle(article, images);
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "articleId", savedArticle.getForumArticleId()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // 首頁:輪播圖片顯示 index=0是第一張
    // http://localhost:8080/forum/images/25/0
    @GetMapping("/images/{articleId}/{index}")
    public ResponseEntity<Resource> getArticleImage(@PathVariable int articleId, @PathVariable int index) throws IOException {
        log.info("Accessing image for article: " + articleId + ", index: " + index);
    	try {
            List<ForumArticlePhotos> photos = forumArticlePhotosRepository.findByForumArticles_ForumArticleId(articleId);

            if (!photos.isEmpty() && index < photos.size()) {
                String fileName = photos.get(index).getForumArticlePhotoImg();
                Path filePath = Paths.get(imageStorageService.getUploadDirectory()).resolve(fileName);
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                               "inline; filename=\"" + fileName + "\"")
                        .body(resource);
                }
            }
            log.warn("Image not found for article: " + articleId + ", index: " + index);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error accessing image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 首頁: 檢查是否有登入才能新增文章
    @GetMapping("/checkLogin")
    @ResponseBody
    public ResponseEntity<?> checkLogin(HttpSession session) {
        LoginBean user = (LoginBean) session.getAttribute("user");
        return ResponseEntity.ok(Map.of("isLoggedIn", user != null));
    }
    
    //首頁: 轉跳查看詳情畫面 http://localhost:8080/forum/article/1
    @GetMapping("/article/{articleId}")
    public String getArticleDetails(@PathVariable int articleId, Model model) {
        ForumArticles article = forumArticlesService.getArticleById(articleId);
        if (article == null) {
            return "redirect:/forum/forumArticlesDisplay";  // 如果文章不存在，重定向到文章列表
        }
        model.addAttribute("article", article);
        return "forum/forumArticleDetails";  // 返回文章詳情頁面
    }
    
    // 文章詳情:編輯文章
    // http://localhost:8080/forum/editArticle
    @PostMapping("/editArticle")
    @ResponseBody
    public ResponseEntity<?> editArticle(
        @RequestParam int forumArticleId,
        @RequestParam String forumArticleTitle,
        @RequestParam String forumArticleContent,
        @RequestParam String forumArticleItemType,
        @RequestParam BigDecimal forumArticlePrice,
        @RequestParam String forumArticleItemCondition,
        @RequestParam(required = false) List<String> existingImageIds,
        @RequestParam(required = false) List<MultipartFile> newImages,
        HttpSession session) {

        try {
            LoginBean user = (LoginBean) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "請先登入"));
            }

            ForumArticles article = forumArticlesService.getArticleById(forumArticleId);
            if (article == null || !article.getUserInfo().getUserId().equals(user.getUserId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("success", false, "message", "無權編輯此文章"));
            }

            // 更新文章內容
            article.setForumArticleTitle(forumArticleTitle);
            article.setForumArticleContent(forumArticleContent);
            article.setForumArticleItemType(forumArticleItemType);
            article.setForumArticlePrice(forumArticlePrice);
            article.setForumArticleItemCondition(forumArticleItemCondition);

            // 處理現有圖片
            List<ForumArticlePhotos> currentPhotos = article.getForumArticlePhotos();
            currentPhotos.removeIf(photo -> existingImageIds == null || !existingImageIds.contains(String.valueOf(photo.getForumArticlePhotoId())));

            // 刪除不在 existingImageIds 中的圖片
            currentPhotos.removeIf(photo -> !existingImageIds.contains(photo.getForumArticlePhotoId()));
            
            // 處理新圖片
            if (newImages != null && !newImages.isEmpty()) {
                for (MultipartFile image : newImages) {
                    if (!image.isEmpty()) {
                        String fileName = imageStorageService.saveImage(image);
                        ForumArticlePhotos newPhoto = new ForumArticlePhotos();
                        newPhoto.setForumArticlePhotoImg(fileName);
                        newPhoto.setForumArticles(article);
                        currentPhotos.add(newPhoto);
                    }
                }
            }

            ForumArticles updatedArticle = forumArticlesService.updateArticle(article);

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "articleId", updatedArticle.getForumArticleId()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    // 更新後台內容
    // http://localhost:8080/forum/updateArticleStatus
    @PutMapping("/updateArticleStatus")
    @ResponseBody
    public ResponseEntity<ForumArticleDTO> updateArticleStatus(@RequestBody ForumArticleDTO.ArticleUpdateRequest request) {
        try {
            ForumArticleDTO updatedArticle = forumArticlesService.updateArticleStatus(
                request.getForumArticleId(), 
                request.getForumArticleStatus(), 
                request.getDeletePhotoIds()
            );
            return ResponseEntity.ok(updatedArticle);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 刪除圖片
    @DeleteMapping("/deleteArticlePhoto/{photoId}")
    @ResponseBody
    public ResponseEntity<?> deleteArticlePhoto(@PathVariable int photoId) {
        try {
            forumArticlesService.deleteArticlePhoto(photoId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
	//　顯示會員文章頁面 http://localhost:8080/forum/forumArticlesMember
	@GetMapping("forumArticlesMember")
	public String forumArticleMemberList() {
		return "forum/forumArticlesMember";
	}
	
	// 會員:顯示資料
	// http://localhost:8080/forum/getAllForumArticlesByUser
	@GetMapping("/getAllForumArticlesByUser")
	@ResponseBody
	public Page<ForumArticleDTO> getAllForumArticlesByUser(
	        @PageableDefault(size = 5, sort = "forumArticleCreatedDate", direction = Sort.Direction.DESC) Pageable pageable,
	        @RequestParam(required = false) String category,
	        @RequestParam(required = false) String search,
	        @RequestParam(required = false) String startDate,
	        @RequestParam(required = false) String endDate,
	        HttpSession session) {

		// 從 session 中獲取 userId
		LoginBean user = (LoginBean) session.getAttribute("user");
		int userId = user.getUserId();

	    Specification<ForumArticles> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        // 日期範圍搜尋
	        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
	            try {
	                LocalDateTime startOfDay = LocalDate.parse(startDate).atStartOfDay();
	                LocalDateTime endOfDay = LocalDate.parse(endDate).plusDays(1).atStartOfDay();
	                predicates.add(cb.between(root.get("forumArticleCreatedDate"), startOfDay, endOfDay));
	            } catch (DateTimeParseException e) {
	                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "日期格式錯誤", e);
	            }
	        }

	        // 分類與關鍵字搜尋
	        if (category != null && !category.isEmpty() && search != null && !search.isEmpty()) {
	            switch (category) {
	                case "forumArticleId":
	                    try {
	                        Long searchId = Long.parseLong(search);
	                        predicates.add(cb.equal(root.get("forumArticleId"), searchId));
	                    } catch (NumberFormatException e) {
	                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "文章ID格式錯誤");
	                    }
	                    break;

	                case "forumArticleTitle":
	                    predicates.add(cb.like(root.get("forumArticleTitle"), "%" + search + "%"));
	                    break;

	                case "forumArticleContent":
	                    predicates.add(cb.like(root.get("forumArticleContent"), "%" + search + "%"));
	                    break;

	                case "userId":
	                    jakarta.persistence.criteria.Join<ForumArticles, UserInfo> userJoin = root.join("userInfo");
	                    predicates.add(cb.like(userJoin.get("userId").as(String.class), "%" + search + "%"));
	                    break;

	                default:
	                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無效的分類欄位");
	            }
	        }

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    // 呼叫 Service 層，傳遞合併條件進行查詢
	    return forumArticlesService.getArticlesByUserIdWithSpec(userId, spec, pageable);
	}

	// 會員: 圖片顯示
	@GetMapping("/article/image/{filename}")
	@ResponseBody
	public ResponseEntity<Resource> getArticleImage(@PathVariable String filename) throws IOException {
	    Path imagePath = Paths.get("C:/Photos/reviewImages/", filename);
	    Resource resource = new FileSystemResource(imagePath.toFile());
	    
	    if (resource.exists()) {
	        return ResponseEntity.ok()
	                .contentType(MediaType.IMAGE_JPEG)
	                .body(resource);
	    }
	    return ResponseEntity.notFound().build();
	}
    
    
}
