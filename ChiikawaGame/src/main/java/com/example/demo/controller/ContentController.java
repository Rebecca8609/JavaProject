package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.ContentEntity;
import com.example.demo.model.ContentRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
public class ContentController {

    private final String UPLOAD_DIR = "uploads/";
    @Autowired
    private ContentRepository contentRepository;
    
    
    
    // 儲存文字內容到資料庫
    @PostMapping("/save-content")
    public ResponseEntity<String> saveContent(@RequestParam("content") String content) {
        // 假設使用 JPA 儲存 (Repository 未展示)
        ContentEntity entity = new ContentEntity();
        entity.setHtmlContent(content);
        contentRepository.save(entity);

        return ResponseEntity.ok("內容儲存成功");
    }

    // 圖片上傳處理
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("upload") MultipartFile file) {
        try {
            // 檢查是否為圖片類型
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "僅允許上傳圖片"));
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);

            // 儲存文件
            Files.createDirectories(filePath.getParent()); // 確保目錄存在
            Files.write(filePath, file.getBytes());

            // 返回圖片 URL
            Map<String, String> response = new HashMap<>();
            response.put("uploaded", "1");
            response.put("fileName", fileName);
            response.put("url", "/uploads/" + fileName);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "圖片上傳失敗"));
        }
    }
}