<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>글쓰기 - 커뮤니티</title>
    <link rel="stylesheet" href="/static/css/common.css">
    <link rel="stylesheet" href="/static/css/layout.css">
    <link rel="stylesheet" href="/static/css/components.css">
</head>
<body>
    <header class="header">
        <div class="header-container">
            <a href="/index" class="logo">
                <h1>커뮤니티</h1>
            </a>
            <nav class="nav" id="navigation">
                <div class="nav-user">
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
        <div class="container" style="max-width: 800px;">
            <div class="page-header">
                <h1>새 글 작성</h1>
                <p>커뮤니티에 새로운 이야기를 공유해보세요</p>
            </div>

            <div class="card">
                <form id="post-form" enctype="multipart/form-data">
                    <div class="error-banner hidden" id="error-banner">
                        <p id="error-message"></p>
                    </div>

                    <div class="form-group">
                        <label for="title" class="form-label">제목</label>
                        <input type="text" id="title" name="title" class="form-control" placeholder="제목을 입력하세요" maxlength="255" required>
                        <div class="error-message hidden" id="title-error"></div>
                    </div>

                    <div class="form-group">
                        <label for="content" class="form-label">내용</label>
                        <textarea id="content" name="content" class="form-control" placeholder="내용을 입력하세요" rows="15" required></textarea>
                        <div class="error-message hidden" id="content-error"></div>
                        <small class="text-muted">최소 10자 이상 입력해주세요</small>
                    </div>

                    <div class="form-group">
                        <label for="image-files" class="form-label">이미지 첨부</label>
                        <input type="file" id="image-files" name="files" class="form-control" multiple accept="image/*">
                        <small class="text-muted">최대 5개의 이미지를 업로드할 수 있습니다 (JPG, PNG, GIF)</small>
                        <div id="image-preview" class="image-preview mt-2"></div>
                    </div>

                    <div class="form-actions" style="justify-content: space-between;">
                        <a href="/index" class="btn btn-secondary">취소</a>
                        <button type="submit" class="btn btn-primary" id="submit-btn">작성하기</button>
                    </div>
                </form>
            </div>
        </div>
    </main>
    <script src="/static/js/utils/constants.js"></script>
    <script src="/static/js/utils/helpers.js"></script>
    <script src="/static/js/utils/validation.js"></script>
    <script src="/static/js/utils/auth.js"></script>
    <script src="/static/js/utils/notifications.js"></script>
    <script src="/static/js/api/apiClient.js"></script>
    <script src="/static/js/api/authAPI.js"></script>
    <script src="/static/js/api/postAPI.js"></script>
    <script src="/static/js/components/header.js"></script>
    <script src="/static/js/pages/createPost.js"></script>
    <script src="/static/js/app.js"></script>
</body>
</html>