package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name="reviewPhotos")
public class ReviewPhotos {

	@Id @Column(name = "reviewPhotoId")
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	private long reviewPhotoId;
	
	@Column(name = "reviewPhoto")
	private String reviewPhoto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="fk_reviewId")
	private Reviews reviews;
	

	public ReviewPhotos() {
	}

	public long getReviewPhotoId() {
		return reviewPhotoId;
	}

	public void setReviewPhotoId(long reviewPhotoId) {
		this.reviewPhotoId = reviewPhotoId;
	}

	public String getReviewPhoto() {
		return reviewPhoto;
	}

	public void setReviewPhoto(String reviewPhoto) {
		this.reviewPhoto = reviewPhoto;
	}

	public Reviews getReviews() {
		return reviews;
	}

	public void setReviews(Reviews reviews) {
		this.reviews = reviews;
	}
	
	
}
