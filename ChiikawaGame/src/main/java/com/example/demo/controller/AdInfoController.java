package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.AdInfo;
import com.example.demo.model.AdInfoRepository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.util.stream.Collectors;

@Controller
public class AdInfoController {

	@Autowired
	private AdInfoRepository adRepository;

	// 顯示廣告資料總覽頁面 (http://localhost:8080/adInfo)
	@GetMapping("/adInfo")
	public String showAdInfo() {
		return "adInfo/adInfo";
	}

	// 取得所有廣告資料，回傳 JSON 格式 (http://localhost:8080/adInfo/json)
	@GetMapping("/adInfo/json")
	@ResponseBody
	public List<Map<String, Object>> getAdInfoJson() {
		return adRepository.findAll().stream().map(ad -> {
			Map<String, Object> adMap = new HashMap<>();
			adMap.put("adId", ad.getAdId());
			adMap.put("adName", ad.getAdName());
			adMap.put("adInfo", ad.getAdInfo());
			adMap.put("adImageBase64",
					ad.getAdImage() != null ? Base64.getEncoder().encodeToString(ad.getAdImage()) : null);
			return adMap;
		}).collect(Collectors.toList());
	}

	// 新增廣告資料 (http://localhost:8080/adInfo/add)
	@PostMapping("/adInfo/add")
	@ResponseBody
	public ResponseEntity<String> addAdInfo(@RequestParam("adName") String adName,
			@RequestParam("adInfo") String adInfo, @RequestParam("adImage") MultipartFile adImage) {
		try {
			AdInfo ad = new AdInfo();
			ad.setAdName(adName);
			ad.setAdInfo(adInfo);

			// 將圖片文件轉換為二進位數據
			if (!adImage.isEmpty()) {
				ad.setAdImage(adImage.getBytes());
			}

			adRepository.save(ad);
			return ResponseEntity.ok("新增成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("新增失敗：" + e.getMessage());
		}
	}

	// 更新廣告資料 (http://localhost:8080/adInfo/update)
	@PostMapping("/adInfo/update")
	@ResponseBody
	public ResponseEntity<String> updateAdInfo(@RequestParam("adId") Integer adId,
			@RequestParam("adName") String adName, @RequestParam("adInfo") String adInfo,
			@RequestParam(value = "adImage", required = false) MultipartFile adImage) {
		try {
			// 根據 ID 查找現有的廣告資料
			AdInfo existingAd = adRepository.findById(adId).orElseThrow(() -> new RuntimeException("廣告資料不存在"));

			// 更新文字欄位
			existingAd.setAdName(adName);
			existingAd.setAdInfo(adInfo);

			// 如果有新圖片，則更新圖片數據；否則保留原圖片
			if (adImage != null && !adImage.isEmpty()) {
				existingAd.setAdImage(adImage.getBytes());
			}

			// 儲存更新後的廣告資料
			adRepository.save(existingAd);
			return ResponseEntity.ok("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("更新失敗：" + e.getMessage());
		}
	}
}