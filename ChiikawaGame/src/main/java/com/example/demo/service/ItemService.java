package com.example.demo.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Item;
import com.example.demo.model.ItemOption;
import com.example.demo.model.ItemOptionRepositry;
import com.example.demo.model.ItemPhoto;
import com.example.demo.model.ItemPhotoRepositry;
import com.example.demo.model.ItemRepository;
import com.example.demo.model.ItemTransportation;
import com.example.demo.model.TransportationRepository;

import jakarta.transaction.Transactional;

import com.example.demo.model.Brand;
import com.example.demo.model.BrandRepository;
import com.example.demo.model.Category;
import com.example.demo.model.CategoryRepository;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepo;

	@Autowired
	private CategoryRepository categoryRepo; // 引入 CategoryRepository

	@Autowired
	private BrandRepository brandRepo;

	@Autowired
	private ItemOptionRepositry itemOptionRepo;

	@Autowired
	private ItemPhotoRepositry itemPhotoRepo;

	@Autowired
	private TransportationRepository transportationRepo;

	public List<Item> findAllItem() {
		return itemRepo.findAll();
	}

	public Item findItemById(int id) {

		Optional<Item> op = itemRepo.findById(id);
		if (op.isPresent()) {
			return op.get();
		}
		return null;
	}

	public Item addItem(Item item) {
		return itemRepo.save(item);
	}

	public void addItem(Item item, List<Integer> transportationMethods, MultipartFile[] files) throws IOException {
		// 處理運送方式
		if (transportationMethods != null && !transportationMethods.isEmpty()) {
			List<ItemTransportation> transportationList = transportationRepo.findAllById(transportationMethods);
			item.setTransportationMethods(transportationList); // 設定商品的運送方式
		}

		// 處理圖片
		if (files != null && files.length > 0) {
			List<ItemPhoto> photoList = new ArrayList<>();
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					ItemPhoto photo = new ItemPhoto();
					photo.setPhotoFile(file.getBytes()); // 儲存圖片檔案
					photo.setItem(item); // 關聯圖片到商品
					photoList.add(photo);
				}
			}
			item.setItemPhoto(photoList); // 設定商品的圖片
		}

		// 處理選項
		if (item.getItemOption() != null && !item.getItemOption().isEmpty()) {
			for (ItemOption option : item.getItemOption()) {
				option.setItem(item); // 關聯選項到商品
			}
		}

		// 更新商品價格
		updateItemPrice(item); // 強制更新價格
		// 保存商品資料
		itemRepo.save(item); // 新增商品

	}

	public void updateItem(Item item, List<Integer> transportationMethods, MultipartFile[] files) throws IOException {
	    // 處理運送方式
	    if (transportationMethods != null && !transportationMethods.isEmpty()) {
	        List<ItemTransportation> transportationList = transportationRepo.findAllById(transportationMethods);
	        item.setTransportationMethods(transportationList);
	    }

	    // 處理選項
	    if (item.getItemOption() != null && !item.getItemOption().isEmpty()) {
	        for (ItemOption option : item.getItemOption()) {
	            option.setItem(item);
	        }
	    }

	    // 處理圖片
	    if (files != null && files.length > 0) {
	        boolean hasNewFiles = false;
	        for (MultipartFile file : files) {
	            if (!file.isEmpty()) {
	                hasNewFiles = true;
	                break;
	            }
	        }
	        if (hasNewFiles) {
	            // 有新圖片，刪除舊圖片並新增
	            itemPhotoRepo.deleteByItem_ItemId(item.getItemId());
	            for (MultipartFile file : files) {
	                if (!file.isEmpty()) {
	                    ItemPhoto photo = new ItemPhoto();
	                    photo.setPhotoFile(file.getBytes());
	                    photo.setItem(item);
	                    itemPhotoRepo.save(photo); // 直接保存圖片
	                }
	            }
	        }
	    } 

	    // 更新價格
	    updateItemPrice(item);

	    // 最後保存商品
	    itemRepo.save(item);
	}


	public void updateItemPrice(Item item) {
		if (item.getItemOption() != null && !item.getItemOption().isEmpty()) {
			System.out.println("商品選項數量：" + item.getItemOption().size());
			item.getItemOption().forEach(option -> System.out.println("選項價格：" + option.getOptionPrice()));

			BigDecimal minPrice = item.getItemOption().stream().map(ItemOption::getOptionPrice).filter(Objects::nonNull)
					.min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

			System.out.println("最低價格：" + minPrice);
			item.setItemPrice(minPrice);

		}
	}

	public void deleteItemById(Integer id) {

		itemRepo.deleteById(id);

	}
}
