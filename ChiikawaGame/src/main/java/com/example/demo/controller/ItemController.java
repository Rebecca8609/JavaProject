package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.service.BrandService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ItemService;
import com.example.demo.model.Brand;
import com.example.demo.model.BrandRepository;
import com.example.demo.model.Category;
import com.example.demo.model.CategoryRepository;
import com.example.demo.model.Item;
import com.example.demo.model.ItemOption;
import com.example.demo.model.ItemPhoto;
import com.example.demo.model.ItemPhotoRepositry;
import com.example.demo.model.ItemRepository;
import com.example.demo.model.ItemTransportation;
import com.example.demo.model.TransportationRepository;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private TransportationRepository transportationRepo;
    @Autowired
    private ItemPhotoRepositry itemPhotoRepo;
    

    // 顯示商品列表
    @GetMapping("/item/itemList")
    public String itemList(Model model) {
        List<Item> itemList = itemService.findAllItem();
        model.addAttribute("itemList", itemList);
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("brandList", brandService.findAll());
        return "/item/itemListView";
    }

    // 刪除商品
    @GetMapping("/item/deleteItem")
    public String deleteItem(@RequestParam Integer id) {
        try {
            itemService.deleteItemById(id);
            return "redirect:/item/itemList";
        } catch (Exception e) {
            System.out.println("刪除商品失敗: " + e.getMessage());
            return "errorPage";
        }
    }

    // 顯示新增商品頁面
    @GetMapping("/item/addItem")
    public String addItem(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("brandList", brandService.findAll());
        model.addAttribute("transportationList", transportationRepo.findAll());
        return "/item/itemAddView";
    }

    // 新增商品
    @PostMapping("/item/add")
    public String addItem(
            @ModelAttribute Item item,
            @RequestParam(required = false) List<Integer> transportationMethods,
            @RequestParam(name = "files", required = false) MultipartFile[] files) throws IOException {

        // 呼叫 Service 處理新增邏輯
        itemService.addItem(item, transportationMethods, files);
        return "redirect:/item/itemList";
    }

    // 顯示編輯商品頁面
    @GetMapping("/item/editItem")
    public String editItem(@RequestParam Integer id, Model model) {
        Item item = itemService.findItemById(id);
        model.addAttribute("item", item);
        model.addAttribute("categoryList", categoryService.findAll());
        model.addAttribute("brandList", brandService.findAll());
        model.addAttribute("transportationList", transportationRepo.findAll());
        return "/item/itemEditView";
    }

    // 編輯商品
    @PostMapping("/item/editItemPost")
    public String editItemPost(
            @ModelAttribute Item item,
            @RequestParam(required = false) List<Integer> transportationMethods,
            @RequestParam(name = "files", required = false) MultipartFile[] files) throws IOException {
    	
    	
        // 呼叫 Service 處理編輯邏輯
        itemService.updateItem(item, transportationMethods, files);
        return "redirect:/item/itemList";
    }
    //顯示圖片
	@GetMapping("/item/photo")
	public ResponseEntity<?> downloadItemPhoto(@RequestParam Integer id) {
		Optional<ItemPhoto> op = itemPhotoRepo.findById(id);
		
		if(op.isEmpty()) {
//			return new ResponseEntity<>(HttpStatusCode.valueOf(404));
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		ItemPhoto itemPhoto = op.get();
		byte[] image = itemPhoto.getPhotoFile();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		
		                          // body , header  , http status code
		return new ResponseEntity<byte[]>(image, headers, HttpStatus.OK);
	}
	
	
	
}
