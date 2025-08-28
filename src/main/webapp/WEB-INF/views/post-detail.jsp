<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>게시글 상세 - 커뮤니티</title>
    <link rel="stylesheet" href="/static/css/common.css">
    <link rel="stylesheet" href="/static/css/layout.css">
    <link rel="stylesheet" href="/static/css/components.css">
    <link rel="stylesheet" href="/static/css/postDetail.css">
    <style>
        .hidden {
            display: none !important;
        }
        
        .modal {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: none;
            align-items: center;
            justify-content: center;
            z-index: 1000;
        }
        
        .modal:not(.hidden) {
            display: flex;
        }

        .comment-item {
            border-bottom: 1px solid #eee;
            padding: 15px 0;
        }

        .comment-meta {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
        }

        .comment-author {
            font-weight: 600;
            color: #333;
        }

        .comment-date {
            color: #666;
            font-size: 0.9em;
        }

        .comment-content {
            margin: 8px 0;
            line-height: 1.6;
            color: #444;
        }

        .comment-actions {
            margin-top: 8px;
        }

        .no-comments {
            text-align: center;
            color: #666;
            padding: 30px;
            font-style: italic;
        }

        .error-message {
            color: #dc3545;
            font-size: 0.875em;
            margin-top: 5px;
        }
    </style>
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
        <div class="container" style="max-width: 800px;">
            <div class="loading" id="loading">
                <div class="spinner"></div>
                <p>게시글을 불러오는 중...</p>
            </div>

            <div class="error-banner hidden" id="error-banner">
                <p id="error-message"></p>
                <button class="btn btn-sm btn-primary" id="retry-btn">다시 시도</button>
            </div>

            <div class="post-detail hidden" id="post-detail">
                <div class="card">
                    <div class="post-header">
                        <div>
                            <h1 id="post-title"></h1>
                            <div class="post-meta">
                                <span id="post-author"></span>
                                <span id="post-date"></span>
                                <button type="button" class="btn btn-sm btn-outline-primary" id="message-btn" style="display: none;">쪽지</button>
                            </div>
                        </div>
                        <div class="post-owner-actions hidden" id="post-owner-actions">
                            <a href="#" class="btn btn-sm btn-secondary" id="edit-btn">수정</a>
                            <button class="btn btn-sm btn-danger" id="delete-btn">삭제</button>
                        </div>
                    </div>

                    <div class="post-content" id="post-content"></div>

                    <div class="post-images hidden" id="post-images">
                        <div class="images-grid" id="images-grid"></div>
                    </div>

                    <div class="post-actions">
                        <div class="flex" style="gap: 10px;">
                            <button class="like-btn" id="like-btn">
                                <span id="like-icon">🤍</span>
                                <span id="like-count">0</span>
                            </button>
                            <button class="bookmark-btn" id="bookmark-btn">
                                <span id="bookmark-icon">🔖</span>
                            </button>
                        </div>
                        <a href="/index" class="btn btn-outline btn-sm">목록으로</a>
                    </div>
                </div>
            </div>

            <div class="comments-section hidden" id="comments-section">
                <div class="card">
                    <div class="comments-header">
                        <h3>댓글 <span id="comment-count">0</span></h3>
                    </div>

                    <div class="comment-form hidden" id="comment-form">
                        <form id="comment-create-form">
                            <div class="form-group">
                                <textarea id="comment-content" name="content" class="form-control" placeholder="댓글을 입력하세요" rows="3" required></textarea>
                                <div class="error-message hidden" id="content-error"></div>
                            </div>
                            <div class="form-actions" style="justify-content: flex-end;">
                                <button type="submit" class="btn btn-primary btn-sm" id="comment-submit-btn">댓글 작성</button>
                            </div>
                        </form>
                    </div>

                    <div class="comments-list" id="comments-list">
                        <p class="no-comments">아직 댓글이 없습니다.</p>
                    </div>

                    <div class="comment-pagination hidden" id="comment-pagination">
                        <button class="btn btn-outline btn-sm" id="load-more-comments">더 보기</button>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <div class="image-modal" id="image-modal">
        <div class="modal-image-container">
            <img src="" alt="" class="modal-image" id="modal-image">
            <button class="modal-nav modal-prev hidden" id="modal-prev">‹</button>
            <button class="modal-nav modal-next hidden" id="modal-next">›</button>
            <button class="modal-close" id="modal-close">×</button>
            <div class="modal-counter" id="modal-counter"></div>
        </div>
    </div>

    <div class="modal hidden" id="message-modal" style="display: none;">
        <div class="modal-content">
            <div class="modal-header">
                <h3>쪽지 보내기</h3>
            </div>
            <div class="modal-body">
                <div class="form-group">
                    <label class="form-label">받는 사람</label>
                    <input type="text" id="message-recipient" class="form-control" readonly>
                </div>
                <div class="form-group">
                    <label class="form-label">내용</label>
                    <textarea id="message-content" class="form-control" placeholder="쪽지 내용을 입력하세요" rows="4" required></textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" id="message-cancel-btn">취소</button>
                <button type="button" class="btn btn-primary" id="message-send-btn">전송</button>
            </div>
        </div>
    </div>

    <script src="/static/js/utils/constants.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/utils/helpers.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/utils/validation.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/utils/auth.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/api/apiClient.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/api/messageAPI.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/api/authAPI.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/api/postAPI.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/api/commentAPI.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/components/header.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/pages/postDetail.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/components/imageModal.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/app.js?v=<%= System.currentTimeMillis() %>"></script>
    <script src="/static/js/utils/fallback.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>