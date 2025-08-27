<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>커뮤니티</title>
    <link rel="stylesheet" href="/static/css/common.css">
    <link rel="stylesheet" href="/static/css/layout.css">
    <link rel="stylesheet" href="/static/css/components.css">
    <link rel="stylesheet" href="/static/css/pages.css">
    <link rel="stylesheet" href="/static/css/responsive.css">
</head>
<body>
    <header class="header">
        <div class="header-container">
            <a href="/index" class="logo">
                <h1>커뮤니티</h1>
            </a>
            <nav class="nav" id="navigation">
                <div class="nav-guest" id="nav-guest">
                    <a href="/login" class="btn btn-outline btn-sm">로그인</a>
                    <a href="/register" class="btn btn-primary btn-sm">회원가입</a>
                </div>
                <div class="nav-user hidden" id="nav-user">
                    <span class="user-nickname" id="user-nickname"></span>
                    <a href="/my-page" class="btn btn-outline btn-sm">마이페이지</a>
                    
                    <a href="/bookmarks" class="btn btn-outline btn-sm">북마크</a>
                    <a href="/create-post" class="btn btn-primary btn-sm">글쓰기</a>
                    <a href="/admin" class="btn btn-secondary btn-sm hidden" id="admin-link">관리자</a>
                    <button class="btn btn-outline btn-sm" id="logout-btn">로그아웃</button>
                </div>
            </nav>
        </div>
    </header>

    <main class="main-content">
        <div class="container">
            <div class="page-header">
                <h1>커뮤니티 게시판</h1>
                <p class="text-muted">자유롭게 소통해보세요!</p>
            </div>

            <div class="search-container">
                <div class="search-bar">
                    <input type="text" id="search-input" placeholder="게시글, 작성자, 내용 검색..." maxlength="100">
                    <button id="search-btn" class="btn btn-primary">검색</button>
                    <button id="clear-search-btn" class="btn btn-outline" style="display: none;">초기화</button>
                </div>
            </div>

            <div class="loading hidden" id="loading">
                <div class="spinner"></div>
                <p>게시글을 불러오는 중...</p>
            </div>

            <div class="error-banner hidden" id="error-banner">
                <p id="error-message"></p>
                <button class="btn btn-sm btn-primary" id="retry-btn">다시 시도</button>
            </div>

            <div class="empty-state hidden" id="empty-state">
                <h3>아직 게시글이 없습니다</h3>
                <p>첫 번째 글을 작성해보세요!</p>
                <a href="/create-post" class="btn btn-primary">글쓰기</a>
            </div>

            <div id="post-list"></div>

            <div class="pagination hidden" id="pagination">
                <button class="btn btn-outline btn-sm" id="prev-btn" disabled>이전</button>
                <div class="page-numbers" id="page-numbers"></div>
                <button class="btn btn-outline btn-sm" id="next-btn">다음</button>
            </div>
        </div>
    </main>

    <style>
        .search-container {
            margin-bottom: 30px;
            display: flex;
            justify-content: center;
        }

        .search-bar {
            display: flex;
            align-items: center;
            gap: 10px;
            max-width: 600px;
            width: 100%;
        }

        #search-input {
            flex: 1;
            padding: 12px 16px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.2s ease;
        }

        #search-input:focus {
            outline: none;
            border-color: #007bff;
        }

        .search-results-header {
            margin-bottom: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            border-left: 4px solid #007bff;
        }

        .search-results-header h3 {
            margin: 0;
            color: #495057;
            font-size: 18px;
        }

        .no-results {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }

        .no-results p {
            margin: 10px 0;
            font-size: 16px;
        }

        mark {
            background: #fff3cd;
            padding: 1px 2px;
            border-radius: 2px;
        }

        .loading-spinner {
            text-align: center;
            padding: 50px;
            color: #6c757d;
        }
    </style>

    <script src="/static/js/utils/constants.js"></script>
    <script src="/static/js/utils/helpers.js"></script>
    <script src="/static/js/utils/validation.js"></script>
    <script src="/static/js/utils/auth.js"></script>
    <script src="/static/js/api/apiClient.js"></script>
    <script src="/static/js/api/authAPI.js"></script>
    <script src="/static/js/api/postAPI.js"></script>
    <script src="/static/js/components/header.js"></script>
    <script src="/static/js/components/postCard.js"></script>
    <script src="/static/js/components/pagination.js"></script>
    <script src="/static/js/pages/index.js"></script>
    <script src="/static/js/pages/search.js"></script>
    <script src="/static/js/app.js"></script>
</body>
</html>