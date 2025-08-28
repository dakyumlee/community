let currentPage = 0;
let totalPages = 1;

// 페이지 로드 시 실행됨
document.addEventListener('DOMContentLoaded', function() {
    // 로그인 체크 - 북마크는 로그인한 사용자만 볼 수 있음ㅇㅇ
    if (!Auth.requireAuth()) {
        return;
    }
    
    // 헤더 초기화 (로그인 상태 표시)
    initHeader();
    
    // 북마크 목록 로드
    loadBookmarks(currentPage);
    
    // 이벤트 리스너 설정
    setupEventListeners();
});

// 헤더 초기화 함수
function initHeader() {
    const user = Auth.getUser();
    const userNickname = document.getElementById('user-nickname');
    const adminLink = document.getElementById('admin-link');
    
    if (user && userNickname) {
        userNickname.textContent = user.nickname;
        
        // 관리자인 경우 관리자 링크 표시됨
        if (Auth.isAdmin() && adminLink) {
            showElement(adminLink);
        }
    }
}

// 이벤트 리스너 설정
function setupEventListeners() {
    // 로그아웃 버튼
    const logoutBtn = document.getElementById('logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            if (confirm('로그아웃 하시겠습니까?')) {
                Auth.logout();
            }
        });
    }
    
    // 재시도 버튼...
    const retryBtn = document.getElementById('retry-btn');
    if (retryBtn) {
        retryBtn.addEventListener('click', () => loadBookmarks(currentPage));
    }
}

// 북마크 목록 로드
async function loadBookmarks(page = 0) {
    const loading = document.getElementById('loading');
    const errorBanner = document.getElementById('error-banner');
    const emptyState = document.getElementById('empty-state');
    const bookmarkList = document.getElementById('bookmark-list');
    const pagination = document.getElementById('pagination');
    
    try {
        // 로딩 상태 표시
        showElement(loading);
        hideElement(errorBanner);
        hideElement(emptyState);
        hideElement(pagination);
        
        // API 호출
        const response = await BookmarkAPI.getMyBookmarks(page, PAGINATION.DEFAULT_SIZE);
        
        // 응답 처리
        if (response.posts && response.posts.length > 0) {
            renderBookmarkList(response.posts, bookmarkList);
            
            // 페이지네이션 정보 업데이트
            currentPage = response.currentPage || page;
            totalPages = response.totalPages || 1;
            
            // 페이지네이션 표시 (2페이지 이상인 경우만)
            if (totalPages > 1) {
                createPagination();
                showElement(pagination);
            }
        } else {
            // 북마크가 없는 경우
            showElement(emptyState);
        }
        
    } catch (error) {
        console.error('북마크 로드 실패:', error);
        
        // 에러 메시지 표시(이건 안나는 게 제일 좋지만 비상시의 경우)
        const errorMessage = document.getElementById('error-message');
        if (errorMessage) {
            errorMessage.textContent = error.message || '북마크 목록을 불러오는데 실패했습니다.';
        }
        showElement(errorBanner);
        
    } finally {
        hideElement(loading);
    }
}

// 북마크 목록 렌더링
function renderBookmarkList(posts, container) {
    if (!container) return;
    
    let html = '';
    
    posts.forEach(post => {
        html += createBookmarkCard(post);
    });
    
    container.innerHTML = html;
}

// 북마크 카드 생성 (게시글 카드와 비슷하지만 북마크 제거 버튼 포함)
function createBookmarkCard(post) {
    const date = formatDate(post.createdAt);
    const content = post.content ? truncateText(post.content, 100) : '';
    
    return `
        <div class="post-card" onclick="goToPost(${post.id})">
            <div class="post-header">
                <div>
                    <h3 class="post-title">${sanitizeHTML(post.title)}</h3>
                    <div class="post-meta">
                        <span>${sanitizeHTML(post.authorNickname)}</span>
                        <span>${date}</span>
                    </div>
                </div>
                <button class="bookmark-btn bookmarked" onclick="removeBookmark(${post.id}, event)" title="북마크 제거">
                    🔖
                </button>
            </div>
            
            ${content ? `<div class="post-content">
                <p>${sanitizeHTML(content)}</p>
            </div>` : ''}
            
            <div class="post-stats">
                <div class="stat-item">
                    <span>❤️</span>
                    <span>${post.likeCount || 0}</span>
                </div>
                <div class="stat-item">
                    <span>💬</span>
                    <span>${post.commentCount || 0}</span>
                </div>
            </div>
        </div>
    `;
}

// 게시글로 이동시
function goToPost(postId) {
    window.location.href = `post-detail.html?id=${postId}`;
}

// 북마크 제거
async function removeBookmark(postId, event) {
    // 이벤트 전파 중지 (카드 클릭 이벤트 방지)
    event.stopPropagation();
    
    try {
        // 확인 메시지
        if (!confirm('북마크를 제거하시겠습니까?')) {
            return;
        }
        
        // API 호출시...
        await BookmarkAPI.toggleBookmark(postId);
        
        // 목록 새로고침
        loadBookmarks(currentPage);
        
    } catch (error) {
        console.error('북마크 제거 실패:', error);
        // 에러 메시지는 BookmarkAPI에서 이미 표시됨
    }
}

// 페이지네이션 생성
function createPagination() {
    const pagination = document.getElementById('pagination');
    const prevBtn = document.getElementById('prev-btn');
    const nextBtn = document.getElementById('next-btn');
    const pageNumbers = document.getElementById('page-numbers');
    
    if (!pagination) return;
    
    // 이전/다음 버튼 상태 설정
    prevBtn.disabled = currentPage <= 0;
    nextBtn.disabled = currentPage >= totalPages - 1;
    
    // 페이지 번호 버튼 생성
    let pageNumbersHtml = '';
    const maxVisiblePages = 5; // 최대 5개 페이지 번호 표시
    
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1);
    
    // startPage 재조정
    if (endPage - startPage < maxVisiblePages - 1) {
        startPage = Math.max(0, endPage - maxVisiblePages + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
        const isActive = i === currentPage ? 'active' : '';
        pageNumbersHtml += `
            <button class="btn btn-sm btn-outline ${isActive}" onclick="handlePageChange(${i})">
                ${i + 1}
            </button>
        `;
    }
    
    pageNumbers.innerHTML = pageNumbersHtml;
    
    // 이전/다음 버튼 이벤트
    prevBtn.onclick = () => handlePageChange(currentPage - 1);
    nextBtn.onclick = () => handlePageChange(currentPage + 1);
}

// 페이지 변경 처리
function handlePageChange(page) {
    if (page < 0 || page >= totalPages) return;
    
    currentPage = page;
    loadBookmarks(currentPage);
    
    // 페이지 상단으로 스크롤
    window.scrollTo({ top: 0, behavior: 'smooth' });
}