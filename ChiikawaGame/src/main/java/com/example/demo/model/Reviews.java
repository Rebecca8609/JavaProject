package com.example.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity 
@Table(name="reviews")
public class Reviews {
	
	@Id @Column(name = "reviewId")
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	private long reviewId;
	
	@Column(name = "reviewEvaluation")
	private int reviewEvaluation;
	
	@Column(name = "reviewComment")
	private String reviewComment;
	
	@Column(name = "reviewStatus")
	private int reviewStatus;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "reviewDate")
	private Date reviewDate;
	
	@Column(name = "reviewOrderId")
	private long reviewOrderId;
	
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_orderItemId")
    @JsonIgnore
    private OrderItem orderItem;
    
	@Column(name = "reviewItemId")
	private int reviewItemId;
	
	@Column(name = "reviewSellerId")
	private int reviewSellerId;
	
	@Column(name = "reviewBuyerId")
	private int reviewBuyerId;
	
	@JsonIgnore
	@OneToMany(mappedBy = "reviews")
	private List<ReviewPhotos> reviewPhotos = new ArrayList<>();
	
	@PrePersist // 當物件要轉換成 Persistent 狀態以前，執行這個方法
	public void onCreate() {
		if (reviewDate == null) {
			reviewDate = new Date();
		}
	}

	public Reviews() {
	}

	public long getReviewId() {
		return reviewId;
	}

	public void setReviewId(long reviewId) {
		this.reviewId = reviewId;
	}

	public int getReviewEvaluation() {
		return reviewEvaluation;
	}

	public void setReviewEvaluation(int reviewEvaluation) {
		this.reviewEvaluation = reviewEvaluation;
	}

	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	public int getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(int reviewStatus) {
		this.reviewStatus = reviewStatus;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public long getReviewOrderId() {
		return reviewOrderId;
	}

	public void setReviewOrderId(long reviewOrderId) {
		this.reviewOrderId = reviewOrderId;
	}

	public OrderItem getOrderItem() {
		return orderItem;
	}

	public void setOrderItem(OrderItem orderItem) {
		this.orderItem = orderItem;
	}

	public int getReviewItemId() {
		return reviewItemId;
	}

	public void setReviewItemId(int reviewItemId) {
		this.reviewItemId = reviewItemId;
	}

	public int getReviewSellerId() {
		return reviewSellerId;
	}

	public void setReviewSellerId(int reviewSellerId) {
		this.reviewSellerId = reviewSellerId;
	}

	public int getReviewBuyerId() {
		return reviewBuyerId;
	}

	public void setReviewBuyerId(int reviewBuyerId) {
		this.reviewBuyerId = reviewBuyerId;
	}

	public List<ReviewPhotos> getReviewPhotos() {
		return reviewPhotos;
	}

	public void setReviewPhotos(List<ReviewPhotos> reviewPhotos) {
		this.reviewPhotos = reviewPhotos;
	}
	
	
}
