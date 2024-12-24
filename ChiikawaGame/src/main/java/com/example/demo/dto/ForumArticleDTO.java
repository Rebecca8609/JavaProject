package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.example.demo.model.ForumArticlePhotos;
import com.example.demo.model.ForumArticles;

public class ForumArticleDTO {
    private int forumArticleId;
    private String forumArticleTitle;
    private String forumArticleContent;
    private String forumArticleItemType;
    private int forumArticleStatus;
    private BigDecimal forumArticlePrice;
    private String forumArticleItemCondition;
    private Date forumArticleCreatedDate;
    private Integer userId;
    private List<ForumArticlePhotos> forumArticlePhotos; 

    // 提供一個從 ForumArticles 轉換到 DTO 的方法
    public static ForumArticleDTO fromEntity(ForumArticles article) {
        ForumArticleDTO dto = new ForumArticleDTO();
        dto.setForumArticleId(article.getForumArticleId());
        dto.setForumArticleTitle(article.getForumArticleTitle());
        dto.setForumArticleContent(article.getForumArticleContent());
        dto.setForumArticleItemType(article.getForumArticleItemType());
        dto.setForumArticleStatus(article.getForumArticleStatus());
        dto.setForumArticlePrice(article.getForumArticlePrice());
        dto.setForumArticleItemCondition(article.getForumArticleItemCondition());
        dto.setForumArticleCreatedDate(article.getForumArticleCreatedDate());
        dto.setForumArticlePhotos(article.getForumArticlePhotos());
        if (article.getUserInfo() != null) {
            dto.setUserId(article.getUserInfo().getUserId());
        }
        
        return dto;
    }
    
    public List<ForumArticlePhotos> getForumArticlePhotos() {
        return forumArticlePhotos;
    }

    public void setForumArticlePhotos(List<ForumArticlePhotos> forumArticlePhotos) {
        this.forumArticlePhotos = forumArticlePhotos;
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

	public Date getForumArticleCreatedDate() {
		return forumArticleCreatedDate;
	}

	public void setForumArticleCreatedDate(Date forumArticleCreatedDate) {
		this.forumArticleCreatedDate = forumArticleCreatedDate;
	}

    public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}



	//後台:修改文章
    public static class ArticleUpdateRequest {
        private int forumArticleId;
        private int forumArticleStatus;
        private List<Integer> deletePhotoIds;
        
        // 加入預設建構子
        public ArticleUpdateRequest() {
        }

		public int getForumArticleId() {
			return forumArticleId;
		}

		public void setForumArticleId(int forumArticleId) {
			this.forumArticleId = forumArticleId;
		}

		public int getForumArticleStatus() {
			return forumArticleStatus;
		}

		public void setForumArticleStatus(int forumArticleStatus) {
			this.forumArticleStatus = forumArticleStatus;
		}

		public List<Integer> getDeletePhotoIds() {
			return deletePhotoIds;
		}

		public void setDeletePhotoIds(List<Integer> deletePhotoIds) {
			this.deletePhotoIds = deletePhotoIds;
		}

    }
    

}
