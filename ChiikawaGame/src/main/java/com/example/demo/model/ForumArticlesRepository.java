package com.example.demo.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ForumArticlesRepository extends JpaRepository<ForumArticles, Integer>, 
JpaSpecificationExecutor<ForumArticles> {

	// 只顯示forumArticleStatus=1的資料
	@Query("SELECT f FROM ForumArticles f WHERE f.forumArticleStatus = :status")
    Page<ForumArticles> findAllByStatus(@Param("status") int status, Pageable pageable);

	
}
