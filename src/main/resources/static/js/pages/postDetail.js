let postId = null;
let currentCommentsPage = 1;
let currentPostAuthor = null;
let selectedMessageRecipient = null;

function initPostDetailPage() {
    const urlParams = getURLParams();
    postId = urlParams.id;
    if (!postId) {
        const pathParts = window.location.pathname.split('/');
        const lastPart = pathParts[pathParts.length - 1];
        if (lastPart && !isNaN(lastPart)) {
            postId = lastPart;
        }
    }

    if (!postId) {
        showError('게시글 ID를 찾을 수 없습니다.');
        return;
    }

    loadPost();
    setupEventListeners();
}

function setupEventListeners() {
    const retryBtn = document.getElementById('retry-btn');
    if (retryBtn) {
        retryBtn.addEventListener('click', () => loadPost());
    }
    
    const deleteBtn = document.getElementById('delete-btn');
    if (deleteBtn) {
        deleteBtn.addEventListener('click', handleDeletePost);
    }
    
    const likeBtn = document.getElementById('like-btn');
    if (likeBtn) {
        likeBtn.addEventListener('click', handleToggleLike);
    }
    
    const commentForm = document.getElementById('comment-create-form');
    if (commentForm) {
        commentForm.addEventListener('submit', handleCreateComment);
    }

    setupMessageModal();
}

function setupMessageModal() {
    const messageBtn = document.getElementById('message-btn');
    if (messageBtn) {
        messageBtn.addEventListener('click', (e) => {
            e.preventDefault();
            handleOpenMessageModal(currentPostAuthor);
        });
    }

    const messageModal = document.getElementById('message-modal');
    const messageCancelBtn = document.getElementById('message-cancel-btn');
    const messageSendBtn = document.getElementById('message-send-btn');

    if (messageCancelBtn) {
        messageCancelBtn.addEventListener('click', (e) => {
            e.preventDefault();
            handleCloseMessageModal();
        });
    }

    if (messageSendBtn) {
        messageSendBtn.addEventListener('click', (e) => {
            e.preventDefault();
            handleSendMessage();
        });
    }

    if (messageModal) {
        messageModal.addEventListener('click', (e) => {
            if (e.target === messageModal) {
                handleCloseMessageModal();
            }
        });
    }
}

function handleOpenMessageModal(recipient) {
    if (!recipient || !recipient.nickname) {
        alert('사용자 정보를 찾을 수 없습니다.');
        return;
    }

    selectedMessageRecipient = recipient;

    const modal = document.getElementById('message-modal');
    const recipientInput = document.getElementById('message-recipient');
    const contentTextarea = document.getElementById('message-content');

    if (recipientInput) {
        recipientInput.value = recipient.nickname;
    }

    if (contentTextarea) {
        contentTextarea.value = '';
    }

    if (modal) {
        modal.classList.remove('hidden');
        modal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
        
        if (contentTextarea) {
            setTimeout(() => contentTextarea.focus(), 100);
        }
    }
}

function handleCloseMessageModal() {
    const modal = document.getElementById('message-modal');
    const contentTextarea = document.getElementById('message-content');

    selectedMessageRecipient = null;

    if (contentTextarea) {
        contentTextarea.value = '';
    }

    if (modal) {
        modal.classList.add('hidden');
        modal.style.display = 'none';
        document.body.style.overflow = '';
    }
}

async function handleSendMessage() {
    const contentTextarea = document.getElementById('message-content');
    const sendBtn = document.getElementById('message-send-btn');

    const content = contentTextarea.value.trim();

    if (!content) {
        alert('쪽지 내용을 입력해주세요.');
        contentTextarea.focus();
        return;
    }

    if (!selectedMessageRecipient || !selectedMessageRecipient.id) {
        alert('받는 사람 정보를 찾을 수 없습니다.');
        return;
    }

    try {
        sendBtn.disabled = true;
        sendBtn.textContent = '전송중...';

        if (typeof MessageAPI !== 'undefined') {
            await MessageAPI.sendMessage(selectedMessageRecipient.id, content);
            alert('쪽지가 전송되었습니다.');
            handleCloseMessageModal();
        } else {
            alert('MessageAPI가 로드되지 않았습니다.');
        }

    } catch (error) {
        console.error('쪽지 전송 실패:', error);
        alert('쪽지 전송에 실패했습니다.');

    } finally {
        sendBtn.disabled = false;
        sendBtn.textContent = '전송';
    }
}

function showError(message) {
    const errorBanner = document.getElementById('error-banner');
    const errorMessage = document.getElementById('error-message');
    const loading = document.getElementById('loading');

    if (loading) loading.classList.add('hidden');
    if (errorMessage) errorMessage.textContent = message;
    if (errorBanner) errorBanner.classList.remove('hidden');
}

async function loadPost() {
    const loading = document.getElementById('loading');
    const errorBanner = document.getElementById('error-banner');
    const postDetail = document.getElementById('post-detail');
    const commentsSection = document.getElementById('comments-section');

    try {
        if (loading) loading.classList.remove('hidden');
        if (errorBanner) errorBanner.classList.add('hidden');

        const response = await PostAPI.getPost(postId);
        
        renderPostDetail(response);

        if (loading) loading.classList.add('hidden');
        if (postDetail) postDetail.classList.remove('hidden');
        if (commentsSection) commentsSection.classList.remove('hidden');

        if (Auth.isAuthenticated()) {
            const commentForm = document.getElementById('comment-form');
            if (commentForm) commentForm.classList.remove('hidden');
        }

        loadComments();

    } catch (error) {
        console.error('게시글 로드 실패:', error);
        if (loading) loading.classList.add('hidden');
        showError('게시글을 불러오는 중 오류가 발생했습니다.');
    }
}

function renderPostDetail(post) {
    const titleElement = document.getElementById('post-title');
    const contentElement = document.getElementById('post-content');
    const authorElement = document.getElementById('post-author');
    const dateElement = document.getElementById('post-date');

    if (titleElement) titleElement.textContent = post.title || '';
    if (contentElement) {
        contentElement.innerHTML = (post.content || '').replace(/\n/g, '<br>');
    }
    if (authorElement) authorElement.textContent = post.authorNickname || '';
    if (dateElement) dateElement.textContent = formatDateTime(post.createdAt) || '';

    currentPostAuthor = {
        id: post.authorId,
        nickname: post.authorNickname
    };

    console.log('게시글 데이터:', post);
    console.log('isAuthor:', post.isAuthor);

    const messageBtn = document.getElementById('message-btn');
    const ownerActions = document.getElementById('post-owner-actions');
    const editBtn = document.getElementById('edit-btn');

    if (post.isAuthor) {
        console.log('작성자임 - 수정/삭제 버튼 표시');
        if (messageBtn) messageBtn.style.display = 'none';
        if (ownerActions) ownerActions.classList.remove('hidden');
        if (editBtn) editBtn.href = `/edit-post?id=${post.id}`;
    } else {
        console.log('작성자 아님 - 쪽지 버튼 표시');
        if (ownerActions) ownerActions.classList.add('hidden');
        if (messageBtn && Auth.isAuthenticated()) {
            messageBtn.style.display = 'inline-block';
        }
    }

    if (post.imageUrls && post.imageUrls.length > 0) {
        renderPostImages(post.imageUrls);
    }

    const likeBtn = document.getElementById('like-btn');
    const likeIcon = document.getElementById('like-icon');
    const likeCount = document.getElementById('like-count');
    
    if (likeBtn && likeIcon && likeCount) {
        likeBtn.className = post.isLiked ? 'like-btn liked' : 'like-btn';
        likeIcon.textContent = post.isLiked ? '❤️' : '🤍';
        likeCount.textContent = post.likeCount;
    }
}

function renderPostImages(imageUrls) {
    const imagesContainer = document.getElementById('post-images');
    const imagesGrid = document.getElementById('images-grid');

    if (!imagesContainer || !imagesGrid || !imageUrls || imageUrls.length === 0) {
        return;
    }

    imagesGrid.className = 'images-grid';
    if (imageUrls.length === 1) {
        imagesGrid.classList.add('single-image');
    } else if (imageUrls.length === 2) {
        imagesGrid.classList.add('two-images');
    } else if (imageUrls.length === 3) {
        imagesGrid.classList.add('three-images');
    } else {
        imagesGrid.classList.add('many-images');
    }

    let html = '';
    imageUrls.forEach((url, index) => {
        const cleanUrl = url.trim();
        html += `
            <div class="image-item ${imageUrls.length === 1 ? 'single' : ''}" onclick="showImageModal(${JSON.stringify(imageUrls)}, ${index})">
                <img src="${cleanUrl}" alt="게시글 이미지 ${index + 1}" loading="lazy" 
                     onerror="this.parentElement.style.display='none'" 
                     onload="this.style.opacity='1'">
                <div class="image-overlay">
                    <span>🔍</span>
                </div>
            </div>
        `;
    });

    imagesGrid.innerHTML = html;
    imagesContainer.classList.remove('hidden');
}

async function loadComments() {
    try {
        const response = await CommentAPI.getComments(postId, currentCommentsPage);
        
        const commentCount = document.getElementById('comment-count');
        if (commentCount) {
            commentCount.textContent = response.totalElements || 0;
        }
        
        renderComments(response.comments || []);
        
    } catch (error) {
        console.error('댓글 로드 실패:', error);
    }
}

function renderComments(comments) {
    const commentsList = document.getElementById('comments-list');
    if (!commentsList) return;
    
    commentsList.innerHTML = '';
    
    if (!comments || comments.length === 0) {
        commentsList.innerHTML = '<p class="no-comments">아직 댓글이 없습니다.</p>';
        return;
    }
    
    comments.forEach(comment => {
        const commentItem = createCommentItem(comment);
        commentsList.appendChild(commentItem);
    });
}

function createCommentItem(comment) {
    const div = document.createElement('div');
    div.className = 'comment-item';
    
    const currentUser = Auth.getUser();
    const isAuthor = currentUser && currentUser.id === comment.authorId;
    
    div.innerHTML = `
        <div class="comment-meta">
            <span class="comment-author">${sanitizeHTML(comment.authorNickname)}</span>
            <span class="comment-date">${formatDateTime(comment.createdAt)}</span>
            ${Auth.isAuthenticated() && currentUser && currentUser.id !== comment.authorId ? 
                `<button class="btn btn-sm btn-outline-primary" onclick="openCommentMessageModal(${comment.authorId}, '${sanitizeHTML(comment.authorNickname)}')">쪽지</button>` 
                : ''
            }
        </div>
        <div class="comment-content">${sanitizeHTML(comment.content)}</div>
        ${isAuthor ? `
            <div class="comment-actions">
                <button class="btn btn-sm btn-danger" onclick="deleteComment(${comment.id})">삭제</button>
            </div>
        ` : ''}
    `;
    
    return div;
}

function openCommentMessageModal(authorId, authorNickname) {
    const recipient = {
        id: authorId,
        nickname: authorNickname
    };
    
    handleOpenMessageModal(recipient);
}

async function handleToggleLike() {
    if (!Auth.requireAuth()) return;
    
    try {
        const response = await PostAPI.toggleLike(postId);
        
        const likeBtn = document.getElementById('like-btn');
        const likeIcon = document.getElementById('like-icon');
        const likeCount = document.getElementById('like-count');
        
        if (likeBtn && likeIcon && likeCount) {
            likeBtn.className = response.isLiked ? 'like-btn liked' : 'like-btn';
            likeIcon.textContent = response.isLiked ? '❤️' : '🤍';
            likeCount.textContent = response.likeCount;
        }
        
    } catch (error) {
        console.error('좋아요 토글 실패:', error);
        Auth.handleAuthError(error);
    }
}

async function handleCreateComment(e) {
    e.preventDefault();
    
    if (!Auth.requireAuth()) return;
    
    const form = e.target;
    const submitBtn = document.getElementById('comment-submit-btn');
    const contentTextarea = document.getElementById('comment-content');
    
    const content = contentTextarea.value.trim();
    
    if (!content) {
        addInputError(contentTextarea, '댓글 내용을 입력해주세요');
        return;
    }
    
    try {
        setLoading(submitBtn, true);
        
        await CommentAPI.createComment(postId, { content });
        
        showNotification('댓글이 작성되었습니다.', 'success');
        
        form.reset();
        removeInputError(contentTextarea);
        
        loadComments();
        
    } catch (error) {
        console.error('댓글 작성 실패:', error);
        Auth.handleAuthError(error);
        
    } finally {
        setLoading(submitBtn, false);
    }
}

async function handleDeletePost() {
    if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
        return;
    }
    
    try {
        await PostAPI.deletePost(postId);
        
        showNotification('게시글이 삭제되었습니다.', 'success');
        
        setTimeout(() => {
            window.location.href = '/';
        }, 1000);
        
    } catch (error) {
        console.error('게시글 삭제 실패:', error);
        Auth.handleAuthError(error);
    }
}

async function deleteComment(commentId) {
    if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
        return;
    }
    
    try {
        await CommentAPI.deleteComment(commentId);
        
        showNotification('댓글이 삭제되었습니다.', 'success');
        
        loadComments();
        
    } catch (error) {
        console.error('댓글 삭제 실패:', error);
        Auth.handleAuthError(error);
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPostDetailPage);
} else {
    initPostDetailPage();
}