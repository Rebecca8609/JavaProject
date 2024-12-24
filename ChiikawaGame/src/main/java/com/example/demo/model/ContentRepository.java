package com.example.demo.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Long> {

    // 根據建立時間排序
    List<ContentEntity> findAllByOrderByCreatedAtDesc();

    // 自定義查詢 - 搜尋 HTML 內容中包含關鍵字
    @Query("SELECT c FROM ContentEntity c WHERE c.htmlContent LIKE %:keyword%")
    List<ContentEntity> searchByKeyword(String keyword);
}