package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ForumArticlePhotosRepository extends JpaRepository<ForumArticlePhotos, Integer> {
	
	//用forumArticleId找圖片
    List<ForumArticlePhotos> findByForumArticles_ForumArticleId(int forumArticleId);
    
    void deleteByForumArticles_ForumArticleId(int articleId);


}
