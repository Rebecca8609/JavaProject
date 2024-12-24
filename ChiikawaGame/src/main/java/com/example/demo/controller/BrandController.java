package com.example.demo.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.BrandService;
import com.example.demo.dto.BrandDTO;
import com.example.demo.model.Brand;
import com.example.demo.model.BrandRepository;


@Controller
public class BrandController {

	@Autowired
	private BrandService brandService;
	@Autowired
	private BrandRepository brandRepo;

	@GetMapping("/brand/list")
	public String listBrand(Model model) {

		List<Brand> brandList = brandService.findAll();

		model.addAttribute("brandList", brandList);

		return "brand/brandListView";
	}

	@GetMapping("/api/brand/photo")
	public ResponseEntity<byte[]> getCategoryPhoto(@RequestParam("id") Integer brandId) {
		Optional<Brand> brandOpt = brandRepo.findById(brandId);

		if (brandOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Brand brand = brandOpt.get();
		byte[] photo = brand.getBrandPhoto();

		if (photo == null || photo.length == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 若圖片不存在或為空
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<>(photo, headers, HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/api/brand/addPost")
	public String postBrand(@RequestParam(name = "name", required = true) String brandName,
			@RequestParam(name = "info") String brandInfo,
			@RequestParam(name = "file", required = true) MultipartFile files) throws IOException {

		Brand brand = new Brand();
		brand.setBrandName(brandName);
		brand.setBrandInfo(brandInfo);
		brand.setBrandPhoto(files.getBytes());
		

		brandRepo.save(brand);

		return "上傳成功";
	}

	@GetMapping("/api/brand/list")
	@ResponseBody
	public List<BrandDTO> getBrandList() {
	    // 查詢所有分類
	    List<Brand> brandList = brandService.findAll();
	    
	    // 將每個 brand 轉換為 brandDTO
	    return brandList.stream()
	        .map(brand -> {
	            // 將圖片轉換為 Base64 字串
	            String brandPhoto = brand.getBrandPhoto() != null ? 
	                Base64.getEncoder().encodeToString(brand.getBrandPhoto()) : "";
	            
	            // 返回 DTO
	            return new BrandDTO(
	                brand.getBrandId(),
	                brand.getBrandName(),
	                brand.getBrandInfo(),
	                brandPhoto
	            );
	        })
	        .collect(Collectors.toList());
	}
	// 更新品牌資料
	@PutMapping("/api/brand/update/{brandId}")
	public ResponseEntity<String> updateBrand(@PathVariable("brandId") Integer brandId,
	                                             @RequestParam(name = "name", required = true) String brandName,
	                                             @RequestParam(name = "info", required = false) String brandInfo,
	                                             @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
	    Optional<Brand> brandOpt = brandRepo.findById(brandId);

	    if (brandOpt.isEmpty()) {
	        return new ResponseEntity<>("品牌不存在", HttpStatus.NOT_FOUND);
	    }

	    Brand brand = brandOpt.get();
	    brand.setBrandName(brandName);
	    brand.setBrandInfo(brandInfo != null ? brandInfo : brand.getBrandInfo());
	    
	    if (file != null && !file.isEmpty()) {
	        brand.setBrandPhoto(file.getBytes());
	    }

	    brandRepo.save(brand);
	    return new ResponseEntity<>("更新成功", HttpStatus.OK);
	}
	// 刪除分類
	@DeleteMapping("/api/brand/delete/{brandId}")
	public ResponseEntity<String> deleteBrand(@PathVariable("brandId") Integer brandId) {
	    Optional<Brand> brandOpt = brandRepo.findById(brandId);

	    if (brandOpt.isEmpty()) {
	        return new ResponseEntity<>("品牌不存在", HttpStatus.NOT_FOUND);
	    }

	    brandRepo.deleteById(brandId);
	    return new ResponseEntity<>("刪除成功", HttpStatus.OK);
	}
	
	@GetMapping("/api/brand/{brandId}")
	public ResponseEntity<BrandDTO> getBrand(@PathVariable Integer brandId) {
	    Optional<Brand> brandOpt = brandRepo.findById(brandId);
	    if (brandOpt.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    Brand brand = brandOpt.get();
	    String brandPhoto = brand.getBrandPhoto() != null ?
	        Base64.getEncoder().encodeToString(brand.getBrandPhoto()) : "";
	    BrandDTO brandDTO = new BrandDTO(
	    		brand.getBrandId(),
	    		brand.getBrandName(),
	    		brand.getBrandInfo(),
	    		brandPhoto
	    );
	    return new ResponseEntity<>(brandDTO, HttpStatus.OK);
	}
	
	



}