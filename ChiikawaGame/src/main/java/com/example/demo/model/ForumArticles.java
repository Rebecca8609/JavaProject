package com.example.demo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity @Table(name="forumArticles")
public class ForumArticles {
	
	@Id @Column(name = "forumArticleId")
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	private int forumArticleId;
	
	@Column(name = "forumArticleTitle")
	private String forumArticleTitle;
	
	@Column(name = "forumArticleContent")
	private String forumArticleContent;
	
	@Column(name = "forumArticleItemType")
	private String forumArticleItemType;
	
	@Column(name = "forumArticleStatus")
	private int forumArticleStatus;
	
    @Column(name = "forumArticlePrice")
    private BigDecimal forumArticlePrice;
    
    @Column(name = "forumArticleItemCondition")
    private String forumArticleItemCondition;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "forumArticleCreatedDate")
	private Date forumArticleCreatedDate;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forumArticleAuthorId")
	private UserInfo userInfo;
	
	@OneToMany(cascade = CascadeType.ALL,mappedBy = "forumArticles")
	private List<ForumArticlePhotos> forumArticlePhotos = new ArrayList<>();
	
	@PrePersist // 當物件要轉換成 Persistent 狀態以前，執行這個方法
	public void onCreate() {
		if (forumArticleCreatedDate == null) {
			forumArticleCreatedDate = new Date();
		}
	}

	public int getForumArticleId() {
		return forumArticleId;
	}

	public void setForumArticleId(int forumArticleId) {
		this.forumArticleId = forumArticleId;
	}

	public String getForumArticleTitle() {
		return forumArticleTitle;
	}

	public void setForumArticleTitle(String forumArticleTitle) {
		this.forumArticleTitle = forumArticleTitle;
	}

	public String getForumArticleContent() {
		return forumArticleContent;
	}

	public void setForumArticleContent(String forumArticleContent) {
		this.forumArticleContent = forumArticleContent;
	}

	public String getForumArticleItemType() {
		return forumArticleItemType;
	}

	public void setForumArticleItemType(String forumArticleItemType) {
		this.forumArticleItemType = forumArticleItemType;
	}

	public int getForumArticleStatus() {
		return forumArticleStatus;
	}

	public void setForumArticleStatus(int forumArticleStatus) {
		this.forumArticleStatus = forumArticleStatus;
	}

	public Date getForumArticleCreatedDate() {
		return forumArticleCreatedDate;
	}

	public void setForumArticleCreatedDate(Date forumArticleCreatedDate) {
		this.forumArticleCreatedDate = forumArticleCreatedDate;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public List<ForumArticlePhotos> getForumArticlePhotos() {
		return forumArticlePhotos;
	}

	public void setForumArticlePhotos(List<ForumArticlePhotos> forumArticlePhotos) {
		this.forumArticlePhotos = forumArticlePhotos;
	}

	public BigDecimal getForumArticlePrice() {
		return forumArticlePrice;
	}

	public void setForumArticlePrice(BigDecimal forumArticlePrice) {
		this.forumArticlePrice = forumArticlePrice;
	}

	public String getForumArticleItemCondition() {
		return forumArticleItemCondition;
	}

	public void setForumArticleItemCondition(String forumArticleItemCondition) {
		this.forumArticleItemCondition = forumArticleItemCondition;
	}
	
	
}
