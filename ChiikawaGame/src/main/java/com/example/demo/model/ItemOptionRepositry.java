package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOptionRepositry extends JpaRepository<ItemOption, Integer> {
	List<ItemOption> findByItem(Item item); //Mantle
}