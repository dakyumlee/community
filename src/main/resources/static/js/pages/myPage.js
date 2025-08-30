let currentUser = null;

document.addEventListener('DOMContentLoaded', function() {
    loadUserProfile();
    initTabNavigation();
    initProfileForm();
    initPasswordForm();
    initMessageTabNavigation();
});

async function loadUserProfile() {
    try {
        currentUser = await AuthAPI.getCurrentUser();
        if (!currentUser) {
            window.location.href = '/login.html';
            return;
        }
        
        displayUserProfile(currentUser);
        showProfileContainer();
        loadMyPosts();
        loadUserStats();
        loadMessages();
    } catch (error) {
        console.error('사용자 정보 로드 실패:', error);
        showError('사용자 정보를 불러오는 중 오류가 발생했습니다.');
        window.location.href = '/login.html';
    }
}

function showProfileContainer() {
    const loading = document.getElementById('loading');
    const profileContainer = document.getElementById('profile-container');
    
    if (loading) loading.classList.add('hidden');
    if (profileContainer) profileContainer.classList.remove('hidden');
}

function displayUserProfile(user) {
    const profileEmail = document.getElementById('profile-email');
    const profileNickname = document.getElementById('profile-nickname');
    const emailInput = document.getElementById('email');
    const departmentInput = document.getElementById('department');
    const jobPositionInput = document.getElementById('job-position');
    const nicknameInput = document.getElementById('nickname');
    const companyInput = document.getElementById('company');

    if (profileEmail) profileEmail.textContent = user.email;
    if (profileNickname) profileNickname.textContent = user.nickname;
    if (emailInput) emailInput.value = user.email;
    if (departmentInput) departmentInput.value = user.department || '';
    if (jobPositionInput) jobPositionInput.value = user.jobPosition || '';
    if (nicknameInput) nicknameInput.value = user.nickname || '';
    if (companyInput) companyInput.value = user.company || '';
}

function initTabNavigation() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabName = this.dataset.tab;
            
            tabButtons.forEach(btn => btn.classList.remove('active'));
            tabPanes.forEach(pane => pane.classList.remove('active'));
            
            this.classList.add('active');
            const targetPane = document.getElementById(tabName + '-tab');
            if (targetPane) {
                targetPane.classList.add('active');
            }

            if (tabName === 'posts') {
                loadMyPosts();
            } else if (tabName === 'messages') {
                loadMessages();
            } else if (tabName === 'stats') {
                loadUserStats();
            }
        });
    });
}

function initMessageTabNavigation() {
    document.querySelectorAll('.message-tab-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            document.querySelectorAll('.message-tab-btn').forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const tabType = this.dataset.messageTab;
            
            const receivedContainer = document.getElementById('received-messages');
            const sentContainer = document.getElementById('sent-messages');
            
            if (receivedContainer && sentContainer) {
                if (tabType === 'received') {
                    receivedContainer.classList.remove('hidden');
                    sentContainer.classList.add('hidden');
                    loadMessages();
                } else if (tabType === 'sent') {
                    receivedContainer.classList.add('hidden');
                    sentContainer.classList.remove('hidden');
                    loadSentMessages();
                }
            }
        });
    });
}

function initProfileForm() {
    const profileForm = document.getElementById('profile-form');
    if (profileForm) {
        profileForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const profileData = {
                department: formData.get('department'),
                jobPosition: formData.get('jobPosition'),
                nickname: formData.get('nickname'),
                company: formData.get('company'),
                userId: currentUser.id
            };

            try {
                const response = await APIClient.put(`/users/${currentUser.id}`, null, profileData);
                
                showSuccess('프로필이 업데이트되었습니다.');
                await loadUserProfile();
            } catch (error) {
                console.error('프로필 업데이트 실패:', error);
                showError('프로필 업데이트에 실패했습니다.');
            }
        });
    }
}

function initPasswordForm() {
    const passwordForm = document.getElementById('password-form');
    if (passwordForm) {
        passwordForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const formData = new FormData(this);
            const newPassword = formData.get('newPassword');
            const confirmPassword = formData.get('confirmPassword');

            if (newPassword !== confirmPassword) {
                showError('새 비밀번호가 일치하지 않습니다.');
                return;
            }

            if (newPassword.length < 8) {
                showError('비밀번호는 8자 이상이어야 합니다.');
                return;
            }

            try {
                await APIClient.put(`/users/${currentUser.id}/password`, null, {
                    newPassword: newPassword,
                    userId: currentUser.id
                });
                
                showSuccess('비밀번호가 변경되었습니다.');
                this.reset();
            } catch (error) {
                console.error('비밀번호 변경 실패:', error);
                showError('비밀번호 변경에 실패했습니다.');
            }
        });
    }
}

async function loadMyPosts() {
    try {
        if (!currentUser) return;
        
        const response = await APIClient.get(`/user/posts?userId=${currentUser.id}&page=0&size=20`);
        displayMyPosts(response.posts);
    } catch (error) {
        console.error('내 게시글 로드 실패:', error);
        const postsContainer = document.getElementById('my-posts-list');
        if (postsContainer) {
            postsContainer.innerHTML = '<div class="error-message">게시글을 불러오는 중 오류가 발생했습니다.</div>';
        }
    }
}

function displayMyPosts(posts) {
    const container = document.getElementById('my-posts-list');
    if (!container) return;

    if (!posts || posts.length === 0) {
        container.innerHTML = '<div class="no-data">작성한 게시글이 없습니다.</div>';
        return;
    }

    container.innerHTML = posts.map(post => `
        <div class="my-post-card" onclick="location.href='/post-detail.html?id=${post.id}'">
            <div class="post-title">${post.title}</div>
            <div class="post-meta">
                <span class="post-date">${formatDate(post.createdAt)}</span>
                <div class="post-stats">
                    <span>좋아요: ${post.likeCount}</span>
                    <span>댓글: ${post.commentCount}</span>
                </div>
            </div>
            <div class="post-content">${truncateText(post.content, 100)}</div>
        </div>
    `).join('');
}

async function loadUserStats() {
    try {
        if (!currentUser) return;
        
        const response = await APIClient.get(`/user/stats?userId=${currentUser.id}`);
        displayUserStats(response);
    } catch (error) {
        console.error('사용자 통계 로드 실패:', error);
        displayUserStats({
            postCount: 0,
            commentCount: 0,
            likeCount: 0
        });
    }
}

function displayUserStats(stats) {
    const totalPostsElement = document.getElementById('total-posts');
    const totalCommentsElement = document.getElementById('total-comments');
    const totalLikesElement = document.getElementById('total-likes');
    
    if (totalPostsElement) totalPostsElement.textContent = stats.postCount || 0;
    if (totalCommentsElement) totalCommentsElement.textContent = stats.commentCount || 0;
    if (totalLikesElement) totalLikesElement.textContent = stats.likeCount || 0;
}

async function loadMessages() {
    try {
        const user = await AuthAPI.getCurrentUser();
        if (!user) {
            showError('사용자 정보를 찾을 수 없습니다.');
            return;
        }

        const receivedMessages = await APIClient.get('/posts/messages/received');
        displayMessages(receivedMessages, 'received');
        updateUnreadCount(receivedMessages);

    } catch (error) {
        console.error('쪽지 로드 실패:', error);
        showError('쪽지를 불러오는 중 오류가 발생했습니다.');
    }
}

async function loadSentMessages() {
    try {
        const sentMessages = await APIClient.get('/posts/messages/sent');
        displayMessages(sentMessages, 'sent');
    } catch (error) {
        console.error('보낸 쪽지 로드 실패:', error);
        showError('보낸 쪽지를 불러오는 중 오류가 발생했습니다.');
    }
}

async function deleteMessage(messageId, deleteType) {
    if (!confirm('정말 삭제하시겠습니까?')) {
        return;
    }

    const messageCard = document.querySelector(`[data-id="${messageId}"]`);
    
    try {
        const token = Auth.getToken();
        if (!token) {
            showError('로그인이 필요합니다.');
            return;
        }

        const response = await fetch(`/api/posts/messages/${messageId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ deleteType: deleteType })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            if (messageCard) {
                messageCard.style.opacity = '0.5';
                messageCard.style.pointerEvents = 'none';
                setTimeout(() => {
                    messageCard.remove();
                }, 200);
            }
            
            showSuccess('쪽지가 삭제되었습니다.');
            
            const currentTab = document.querySelector('.message-tab-btn.active');
            if (currentTab) {
                const tabType = currentTab.dataset.messageTab;
                setTimeout(() => {
                    if (tabType === 'received') {
                        loadMessages();
                    } else {
                        loadSentMessages();
                    }
                }, 500);
            }
        } else {
            showError(result.message || '쪽지 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('쪽지 삭제 실패:', error);
        showError('쪽지 삭제 중 오류가 발생했습니다.');
        
        if (messageCard) {
            messageCard.style.opacity = '1';
            messageCard.style.pointerEvents = 'auto';
        }
    }
}

function displayMessages(messages, type) {
    const container = document.getElementById(type === 'received' ? 'received-messages' : 'sent-messages');
    if (!container) return;
    
    if (!messages || messages.length === 0) {
        container.innerHTML = '<div class="no-messages">쪽지가 없습니다.</div>';
        return;
    }

    container.innerHTML = messages.map(message => `
        <div class="message-card ${message.isRead === 0 ? 'unread' : ''}" data-id="${message.id}">
            <div class="message-meta">
                <strong>${type === 'received' ? message.senderName : message.receiverName}</strong>
                <span class="message-date">${formatDate(message.createdAt)}</span>
                ${message.isRead === 0 && type === 'received' ? '<span class="unread-badge">NEW</span>' : ''}
            </div>
            <div class="message-content">${message.title}</div>
            <div class="message-preview">${truncateText(message.content, 50)}</div>
            <div class="message-actions">
                <button class="btn btn-sm btn-outline-primary view-message-btn" data-id="${message.id}">읽기</button>
                <button class="btn btn-sm btn-outline-danger delete-message-btn" 
                        data-id="${message.id}" 
                        data-type="${type === 'received' ? 'receiver' : 'sender'}">삭제</button>
            </div>
        </div>
    `).join('');

    container.querySelectorAll('.view-message-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const messageId = e.target.dataset.id;
            viewMessageDetail(messageId);
        });
    });

    container.querySelectorAll('.delete-message-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const messageId = e.target.dataset.id;
            const deleteType = e.target.dataset.type;
            deleteMessage(messageId, deleteType);
        });
    });
}

async function viewMessageDetail(messageId) {
    try {
        await APIClient.put(`/posts/messages/${messageId}/read`);
        window.location.href = `/Message/View?id=${messageId}`;
    } catch (error) {
        console.error('쪽지 읽기 실패:', error);
        showError('쪽지를 읽는 중 오류가 발생했습니다.');
    }
}

async function deleteMessage(messageId, deleteType) {
    if (!confirm('정말 삭제하시겠습니까?')) {
        return;
    }

    try {
        const token = Auth.getToken();
        if (!token) {
            showError('로그인이 필요합니다.');
            return;
        }

        const response = await fetch(`/api/posts/messages/${messageId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify({ deleteType: deleteType })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const result = await response.json();
        
        if (result.success) {
            const messageCard = document.querySelector(`[data-id="${messageId}"]`);
            if (messageCard) {
                messageCard.remove();
            }
            
            showSuccess('쪽지가 삭제되었습니다.');
            
            setTimeout(() => {
                if (deleteType === 'received') {
                    loadMessages();
                } else {
                    loadSentMessages();
                }
            }, 300);
        } else {
            showError(result.message || '쪽지 삭제에 실패했습니다.');
        }
    } catch (error) {
        console.error('쪽지 삭제 실패:', error);
        showError('쪽지 삭제 중 오류가 발생했습니다.');
    }
}

function updateUnreadCount(messages) {
    const badge = document.getElementById('message-tab-badge');
    if (!badge) return;
    
    const unreadCount = messages.filter(msg => msg.isRead === 0).length;
    
    if (unreadCount > 0) {
        badge.textContent = unreadCount;
        badge.classList.remove('hidden');
    } else {
        badge.classList.add('hidden');
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const oneDay = 24 * 60 * 60 * 1000;

    if (diff < oneDay) {
        return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' });
    } else {
        return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
    }
}

function truncateText(text, maxLength) {
    return text && text.length > maxLength ? text.substring(0, maxLength) + '...' : text || '';
}

function showSuccess(message) {
    alert(message);
}

function showError(message) {
    alert(message);
}