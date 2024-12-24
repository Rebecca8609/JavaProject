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

import com.example.demo.service.CategoryService;
import com.example.demo.dto.CategoryDTO;
import com.example.demo.model.Category;
import com.example.demo.model.CategoryRepository;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private CategoryRepository categoryRepo;

	@GetMapping("/category/list")
	public String listCategory(Model model) {

		List<Category> categoryList = categoryService.findAll();

		model.addAttribute("categoryList", categoryList);

		return "category/categoryListView";
	}

	@GetMapping("/api/category/photo")
	public ResponseEntity<byte[]> getCategoryPhoto(@RequestParam("id") Integer categoryId) {
		Optional<Category> categoryOpt = categoryRepo.findById(categoryId);

		if (categoryOpt.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		Category category = categoryOpt.get();
		byte[] photo = category.getCategoryPhoto();

		if (photo == null || photo.length == 0) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 若圖片不存在或為空
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);

		return new ResponseEntity<>(photo, headers, HttpStatus.OK);
	}

	@ResponseBody
	@PostMapping("/api/category/addPost")
	public String postCategory(@RequestParam(name = "name", required = true) String categoryName,
			@RequestParam(name = "desc") String categoryDesc,
			@RequestParam(name = "file", required = true) MultipartFile files) throws IOException {

		Category category = new Category();
		category.setCategoryName(categoryName);
		category.setCategoryInfo(categoryDesc);
		category.setCategoryPhoto(files.getBytes());

		categoryRepo.save(category);

		return "上傳成功";
	}

	@GetMapping("/api/category/list")
	@ResponseBody
	public List<CategoryDTO> getCategoryList() {
	    // 查詢所有分類
	    List<Category> categoryList = categoryService.findAll();
	    
	    // 將每個 Category 轉換為 CategoryDTO
	    return categoryList.stream()
	        .map(category -> {
	            // 將圖片轉換為 Base64 字串
	            String categoryPhoto = category.getCategoryPhoto() != null ? 
	                Base64.getEncoder().encodeToString(category.getCategoryPhoto()) : "";
	            
	            // 返回 DTO
	            return new CategoryDTO(
	                category.getCategoryId(),
	                category.getCategoryName(),
	                category.getCategoryInfo(),
	                categoryPhoto
	            );
	        })
	        .collect(Collectors.toList());
	}
	// 更新分類資料
	@PutMapping("/api/category/update/{categoryId}")
	public ResponseEntity<String> updateCategory(@PathVariable("categoryId") Integer categoryId,
	                                             @RequestParam(name = "name", required = true) String categoryName,
	                                             @RequestParam(name = "desc", required = false) String categoryDesc,
	                                             @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
	    Optional<Category> categoryOpt = categoryRepo.findById(categoryId);

	    if (categoryOpt.isEmpty()) {
	        return new ResponseEntity<>("分類不存在", HttpStatus.NOT_FOUND);
	    }

	    Category category = categoryOpt.get();
	    category.setCategoryName(categoryName);
	    category.setCategoryInfo(categoryDesc != null ? categoryDesc : category.getCategoryInfo());
	    
	    if (file != null && !file.isEmpty()) {
	        category.setCategoryPhoto(file.getBytes());
	    }

	    categoryRepo.save(category);
	    return new ResponseEntity<>("更新成功", HttpStatus.OK);
	}
	// 刪除分類
	@DeleteMapping("/api/category/delete/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Integer categoryId) {
	    Optional<Category> categoryOpt = categoryRepo.findById(categoryId);

	    if (categoryOpt.isEmpty()) {
	        return new ResponseEntity<>("分類不存在", HttpStatus.NOT_FOUND);
	    }

	    categoryRepo.deleteById(categoryId);
	    return new ResponseEntity<>("刪除成功", HttpStatus.OK);
	}
	@GetMapping("/api/category/{categoryId}")
	public ResponseEntity<CategoryDTO> getCategory(@PathVariable Integer categoryId) {
	    Optional<Category> categoryOpt = categoryRepo.findById(categoryId);
	    if (categoryOpt.isEmpty()) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    Category category = categoryOpt.get();
	    String categoryPhoto = category.getCategoryPhoto() != null ?
	        Base64.getEncoder().encodeToString(category.getCategoryPhoto()) : "";
	    CategoryDTO categoryDTO = new CategoryDTO(
	        category.getCategoryId(),
	        category.getCategoryName(),
	        category.getCategoryInfo(),
	        categoryPhoto
	    );
	    return new ResponseEntity<>(categoryDTO, HttpStatus.OK);
	}



}