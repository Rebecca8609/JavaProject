package com.example.demo.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageStorageService {

//  可以在application.properties定義圖片路徑image.upload.directory=C:/Photos/reviewImages/
	
//  @Value("${image.upload.directory}")
//  private String uploadDirectory;
	
	private final String uploadDirectory = "C:/Photos/reviewImages/";
    
    public String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Uploaded file is empty.");
        }

        try {
            // 檢查檔案類型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("File must be an image");
            }
        	
            // 產生唯一檔案名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadDirectory).resolve(fileName);

            // 確保目錄存在
            Files.createDirectories(targetLocation.getParent());

            // 儲存圖片
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回檔案名稱 (儲存到資料庫)
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file. Error: " + e.getMessage());
        }
    }
    
    // 添加 getter 方法
    public String getUploadDirectory() {
        return uploadDirectory;
    }
    
}

