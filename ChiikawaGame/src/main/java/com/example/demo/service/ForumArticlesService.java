package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ForumArticleDTO;
import com.example.demo.model.ForumArticlePhotos;
import com.example.demo.model.ForumArticlePhotosRepository;
import com.example.demo.model.ForumArticles;
import com.example.demo.model.ForumArticlesRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ForumArticlesService {

	@Autowired
	private ForumArticlesRepository forumArticlesRepository;
	
    @Autowired
    private ForumArticlePhotosRepository forumArticlePhotosRepository;
    
    @Autowired
    private ImageStorageService imageStorageService;
    
    @Value("${upload.directory:C:/Photos/reviewImages/}")
    private String uploadDirectory;
    
	
	// 顯示所有資料
	public Page<ForumArticles> findForumArticlesByPage(Integer pageNumber){
		PageRequest pgr = PageRequest.of(pageNumber-1, 5,Sort.Direction.DESC ,"forumArticleCreatedDate");
		Page<ForumArticles> page = forumArticlesRepository.findAll(pgr);
		return page != null ? page : Page.empty(); // 保證返回非空的 Page
	}
	
	// 只顯示forumArticleStatus=1的資料
	public Page<ForumArticles> findForumArticlesByPageAndStatus(Integer pageNumber) {
	    PageRequest pgr = PageRequest.of(pageNumber-1, 9, Sort.Direction.DESC, "forumArticleCreatedDate");
	    Page<ForumArticles> page = forumArticlesRepository.findAllByStatus(1, pgr);
	    return page != null ? page : Page.empty();
	}
	
    //搜尋文章顯示資料
    public Page<ForumArticleDTO> getArticlesWithSpec(Specification<ForumArticles> spec, Pageable pageable) {
        Page<ForumArticles> articlesPage = forumArticlesRepository.findAll(spec, pageable);
        return articlesPage.map(ForumArticleDTO::fromEntity);
    }
    
    // 會員: 搜尋文章顯示forumArticleStatus=1資料(根據userId來找)
    public Page<ForumArticleDTO> getArticlesByUserIdWithSpec(Integer userId, Specification<ForumArticles> spec, Pageable pageable) {
        // 建立 userId 條件的 Specification
        Specification<ForumArticles> userIdSpec = (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("userInfo").get("userId"), userId);

        // 建立 forumArticleStatus == 1 的條件 Specification
        Specification<ForumArticles> statusSpec = (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("forumArticleStatus"), 1);
        
        // 合併 userIdSpec, statusSpec 與傳入的 spec
        Specification<ForumArticles> combinedSpec = Specification.where(userIdSpec)
                                                                  .and(statusSpec)
                                                                  .and(spec);

        // 執行查詢
        Page<ForumArticles> articlesPage = forumArticlesRepository.findAll(combinedSpec, pageable);

        // 將查詢結果轉換為 DTO
        return articlesPage.map(ForumArticleDTO::fromEntity);
    }


	
	// 新增文章
	public ForumArticles addArticle(ForumArticles article, List<MultipartFile> images) {
        try {
            // 設置創建時間
            article.onCreate();
            
            // 保存文章
            ForumArticles savedArticle = forumArticlesRepository.save(article);
            
            // 處理圖片
            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        // 使用 ImageStorageService 儲存圖片並獲取檔案名
                        String fileName = imageStorageService.saveImage(image);
                        
                        // 創建圖片記錄
                        ForumArticlePhotos photo = new ForumArticlePhotos();
                        photo.setForumArticlePhotoImg(fileName); // 直接儲存文件名
                        photo.setForumArticles(savedArticle);
                        
                        // 保存圖片記錄
                        forumArticlePhotosRepository.save(photo);
                    }
                }
            }
            
            return savedArticle;
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save article with images: " + e.getMessage(), e);
        }
    }
    
	//用文章id來找圖片
    public Page<ForumArticleDTO> getArticles(Pageable pageable) {
        Page<ForumArticles> articlesPage = forumArticlesRepository.findAll(pageable);
        
        return articlesPage.map(article -> {
            ForumArticleDTO dto = ForumArticleDTO.fromEntity(article);
            // 確保載入圖片資訊
            List<ForumArticlePhotos> photos = forumArticlePhotosRepository
                .findByForumArticles_ForumArticleId(article.getForumArticleId());
            dto.setForumArticlePhotos(photos);
            return dto;
        });
    }
    
    //用文章id來找資料
    public ForumArticles getArticleById(int id) {
        return forumArticlesRepository.findById(id).orElse(null);
    }
    
    //編輯更新文章
    public ForumArticles updateArticle(ForumArticles article) {
        return forumArticlesRepository.save(article);
    }
    
    //後台:更新內容
    @Transactional
    public ForumArticleDTO updateArticleStatus(int articleId, int status, List<Integer> deletePhotoIds) {
        ForumArticles article = forumArticlesRepository.findById(articleId)
            .orElseThrow(() -> new EntityNotFoundException("找不到文章，ID: " + articleId));
        
        // 更新狀態
        article.setForumArticleStatus(status);
        
        // 如果有要刪除的圖片
        if (deletePhotoIds != null && !deletePhotoIds.isEmpty()) {
            // 刪除指定的圖片
            for (Integer photoId : deletePhotoIds) {
                ForumArticlePhotos photo = forumArticlePhotosRepository.findById(photoId)
                    .orElseThrow(() -> new EntityNotFoundException("找不到圖片，ID: " + photoId));
                    
                // 刪除實體檔案
                String fileName = photo.getForumArticlePhotoImg();
                if (fileName != null) {
                    try {
                        Path filePath = Paths.get(uploadDirectory, fileName);
                        Files.deleteIfExists(filePath);
                    } catch (IOException e) {
                        log.error("刪除圖片檔案失敗: " + fileName, e);
                    }
                }
                
                // 從文章的圖片列表中移除
                article.getForumArticlePhotos().removeIf(p -> p.getForumArticlePhotoId() == photoId);
                
                // 刪除資料庫記錄
                forumArticlePhotosRepository.delete(photo);
            }
        }
        
        // 儲存更新
        ForumArticles updatedArticle = forumArticlesRepository.save(article);
        
        return ForumArticleDTO.fromEntity(updatedArticle);
    }
    
    //後台: 刪除圖片
    @Transactional
    public void deleteArticlePhoto(int photoId) {
        ForumArticlePhotos photo = forumArticlePhotosRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("找不到圖片"));

        // 刪除實體檔案
        String fileName = photo.getForumArticlePhotoImg();
        if (fileName != null) {
            try {
                Path filePath = Paths.get(uploadDirectory, fileName);
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("刪除圖片檔案失敗: " + fileName, e);
                throw new RuntimeException("刪除圖片檔案失敗");
            }
        }

        // 刪除資料庫記錄
        forumArticlePhotosRepository.delete(photo);
    }
    
}
