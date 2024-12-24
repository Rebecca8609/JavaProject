package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity @Table(name="forumArticlePhotos")
public class ForumArticlePhotos {

	@Id 
	@Column(name = "forumArticlePhotoId")
	@GeneratedValue(strategy  = GenerationType.IDENTITY)
	private int forumArticlePhotoId;
	
	@Column(name = "forumArticlePhotoImg")
	private String forumArticlePhotoImg;
	
	@ManyToOne
	@JoinColumn(name="fk_forumArticles_id")
    @JsonIgnore  // 只在這裡使用 @JsonIgnore 以避免循環引用
	private ForumArticles forumArticles;

	public int getForumArticlePhotoId() {
		return forumArticlePhotoId;
	}

	public void setForumArticlePhotoId(int forumArticlePhotoId) {
		this.forumArticlePhotoId = forumArticlePhotoId;
	}

	public String getForumArticlePhotoImg() {
		return forumArticlePhotoImg;
	}

	public void setForumArticlePhotoImg(String forumArticlePhotoImg) {
		this.forumArticlePhotoImg = forumArticlePhotoImg;
	}

	public ForumArticles getForumArticles() {
		return forumArticles;
	}

	public void setForumArticles(ForumArticles forumArticles) {
		this.forumArticles = forumArticles;
	}



	
}
