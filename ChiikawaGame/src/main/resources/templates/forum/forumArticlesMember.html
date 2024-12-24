<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org/" lang="zh-TW">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>會員收購文章列表</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.7.2/font/bootstrap-icons.css">

  <style>
      #mainContent {
          width: 70%;
          max-width: 1400px;
          min-width: 900px;
          margin: 0 auto;
          padding: 32px;
          border-radius: 10px;
      }

      .form-title {
          font-size: 2rem;
          font-weight: bold;
          margin-bottom: 20px;
          text-align: center;
      }

      .search-section {
          background-color: #f8f9fa;
          padding: 20px;
          border-radius: 8px;
          margin-bottom: 20px;
      }

      .container {
          margin-bottom: 20px;
      }

      .content-card {
          background-color: #f9f9f9;
          padding: 15px;
          border-radius: 8px;
          box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
      }

      .content-card h5 {
          margin-bottom: 10px;
          font-size: 1.2rem;
          color: #333;
      }

      .content-card p {
          font-size: 0.9rem;
          color: #666;
      }

      .card-footer {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 1rem;
      }

      .card-footer .d-flex {
          gap: 10px;
      }
  </style>
</head>
<body>
  <div th:replace="~{layout/userHeader}"></div>

  <div class="d-flex" style="height: 100vh;">
      <div th:replace="~{layout/memberCenterSidebar}"></div>

      <div id="mainContent" class="p-4">
          <h2 class="form-title">會員文章列表</h2>

          <div class="search-section">
              <div class="row g-3 align-items-center">
                  <div class="col-md-2">
                      <select class="form-select" id="category-filter">
                          <option value="">請選擇分類</option>
                          <option value="forumArticleTitle">文章標題</option>
                          <option value="forumArticleContent">文章內容</option>
                      </select>
                  </div>

                  <div class="col-md-3">
                      <input type="text" id="search-bar" class="form-control" placeholder="輸入搜尋條件..." disabled>
                  </div>

                  <div class="col-md-5">
                      <div class="row g-2">
                          <div class="col-md-6">
                              <div class="input-group">
                                  <span class="input-group-text">起始日期</span>
                                  <input type="date" id="start-date-filter" class="form-control">
                              </div>
                          </div>
                          <div class="col-md-6">
                              <div class="input-group">
                                  <span class="input-group-text">結束日期</span>
                                  <input type="date" id="end-date-filter" class="form-control">
                              </div>
                          </div>
                      </div>
                  </div>

                  <div class="col-md-2 d-flex">
                      <button class="btn btn-secondary" id="clear-button">清除</button>
                      <button class="btn btn-primary" id="search-button">搜尋</button>
                  </div>
              </div>
          </div>

          <div id="contentArea"></div>
      </div>
  </div>
  
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>

  <script>
      async function fetchArticles() {
          try {
              const response = await fetch('/forum/getAllForumArticlesByUser');
              const data = await response.json();
              displayArticles(data.content, data.totalPages, data.number);
          } catch (error) {
              console.error('Error fetching articles:', error);
          }
      }

      function displayArticles(articles, totalPages, currentPage) {
          const contentArea = document.getElementById('contentArea');
          contentArea.innerHTML = '';

          articles.forEach(article => {
              const card = document.createElement('div');
              card.classList.add('container', 'content-card');

              const date = new Date(article.forumArticleCreatedDate);
              const formattedDate = date.toISOString().split('T')[0].replace(/-/g, '/');

              const photoHtml = article.forumArticlePhotos && article.forumArticlePhotos.length > 0 
                  ? `<div class="d-flex gap-2">
                      ${article.forumArticlePhotos.map(photo => 
                          `<img src="/forum/article/image/${photo.forumArticlePhotoImg}" 
                          alt="文章圖片" class="img-fluid" style="max-height: 80px;">`
                      ).join('')}
                     </div>`
                  : '';

              card.innerHTML = `
                  <div class="d-flex justify-content-between align-items-center border-bottom pb-2">
                      <h5>${article.forumArticleTitle}</h5>
                      <span class="text-muted">發文日期: ${formattedDate}</span>
                  </div>
                  <span class="badge bg-dark">${article.forumArticleItemType}</span>
                  <span class="badge bg-warning">${article.forumArticleItemCondition}</span>
                  <p class="mt-3">${article.forumArticleContent}</p>
                  <div class="card-footer">
                      ${photoHtml}
                      <a href="/forum/article/${article.forumArticleId}" class="btn btn-outline-dark">查看文章</a>
                  </div>
              `;

              contentArea.appendChild(card);
          });

          const paginationHtml = `
              <nav aria-label="Page navigation" class="mt-4">
                  <ul class="pagination justify-content-center">
                      <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
                          <button class="page-link" onclick="changePage(${currentPage - 1})" ${currentPage === 0 ? 'disabled' : ''}>上一頁</button>
                      </li>
                      ${Array.from({length: totalPages}, (_, i) => `
                          <li class="page-item ${currentPage === i ? 'active' : ''}">
                              <button class="page-link" onclick="changePage(${i})">${i + 1}</button>
                          </li>
                      `).join('')}
                      <li class="page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}">
                          <button class="page-link" onclick="changePage(${currentPage + 1})" ${currentPage === totalPages - 1 ? 'disabled' : ''}>下一頁</button>
                      </li>
                  </ul>
              </nav>
          `;
          contentArea.insertAdjacentHTML('beforeend', paginationHtml);
      }

      async function changePage(page) {
          try {
              const response = await fetch(`/forum/getAllForumArticlesByUser?page=${page}`);
              const data = await response.json();
              displayArticles(data.content, data.totalPages, data.number);
          } catch (error) {
              console.error('Error changing page:', error);
          }
      }

      window.onload = fetchArticles;

      document.getElementById('category-filter').addEventListener('change', function() {
          const searchBar = document.getElementById('search-bar');
          searchBar.disabled = !this.value;
      });

      document.getElementById('search-button').addEventListener('click', async function() {
          const category = document.getElementById('category-filter').value;
          const search = document.getElementById('search-bar').value;
          const startDate = document.getElementById('start-date-filter').value;
          const endDate = document.getElementById('end-date-filter').value;

          const params = new URLSearchParams();
          if (category && search) {
              params.append('category', category);
              params.append('search', search);
          }
          if (startDate) params.append('startDate', startDate);
          if (endDate) params.append('endDate', endDate);

          try {
              const response = await fetch(`/forum/getAllForumArticlesByUser?${params}`);
              const data = await response.json();
              displayArticles(data.content, data.totalPages, data.number);
          } catch (error) {
              console.error('搜尋錯誤:', error);
          }
      });

      document.getElementById('clear-button').addEventListener('click', function() {
          document.getElementById('category-filter').value = '';
          document.getElementById('search-bar').value = '';
          document.getElementById('search-bar').disabled = true;
          document.getElementById('start-date-filter').value = '';
          document.getElementById('end-date-filter').value = '';
          fetchArticles();
      });
  </script>
</body>
</html>