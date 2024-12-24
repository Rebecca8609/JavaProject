package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPhotosRepository extends JpaRepository<ReviewPhotos, Long> {
	
	public List<ReviewPhotos> findByReviewsReviewId(Long reviewId);
	
}
