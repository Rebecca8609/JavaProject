package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface ItemPhotoRepositry extends JpaRepository<ItemPhoto, Integer> {
	
	@Query("select id from ItemPhoto where item.itemId = :id")
	List<Integer> findItemPhotoIdByItemId(@Param("id") Integer id);
	
    // 根據 itemId 刪除所有相關的圖片
    @Transactional
    void deleteByItem_ItemId(int itemId);
	
    
    List<ItemPhoto> findByItem(Item item);
	
}