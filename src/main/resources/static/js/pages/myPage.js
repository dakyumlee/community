let currentUser = null;
let currentTab = 'profile';
let currentMessageTab = 'received';
let currentMessagePage = 0;
let selectedMessageId = null;

function showElement(element) {
    if (element) {
        element.classList.remove('hidden');
        element.style.display = '';
    }
}

function hideElement(element) {
    if (element) {
        element.classList.add('hidden');
        element.style.display = 'none';
    }
}

function setLoading(button, isLoading) {
    if (!button) return;
    
    if (isLoading) {
        button.disabled = true;
        button.textContent = '처리 중...';
    } else {
        button.disabled = false;
        button.textContent = button.dataset.originalText || '저장';
    }
}

function validateFormField(field) {
    if (field.classList.contains('error')) {
        field.classList.remove('error');
    }
    return true;
}

function hasValidationErrors(errors) {
    return Object.keys(errors).length > 0;
}

function showValidationErrors(errors, form) {
    Object.keys(errors).forEach(fieldName => {
        const field = form.querySelector(`[name="${fieldName}"]`);
        if (field) {
            field.classList.add('error');
        }
    });
}

function sanitizeHTML(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR');
}

function truncateText(text, maxLength) {
    if (!text || text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

function showNotification(message, type) {
    console.log(`[${type.toUpperCase()}] ${message}`);
    if (type === 'success') {
        alert(message);
    } else if (type === 'error') {
        alert(message);
    }
}

function initMyPagePage() {
    if (!Auth.isAuthenticated()) {
        window.location.href = '/login';
        return;
    }
    
    setupTabs();
    loadUserProfile();
    setupProfileForm();
    setupPasswordForm();
    updateUnreadMessageCount();
}

function setupTabs() {
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');
    
    console.log('탭 버튼 개수:', tabButtons.length);
    console.log('탭 패널 개수:', tabPanes.length);
    
    tabButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const tabName = this.dataset.tab;
            console.log('탭 클릭됨:', tabName);
            
            tabButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            tabPanes.forEach(pane => {
                pane.classList.remove('active');
            });
            
            const targetContent = document.getElementById(`${tabName}-tab`);
            if (targetContent) {
                targetContent.classList.add('active');
                console.log('탭 표시됨:', tabName);
            } else {
                console.error('탭을 찾을 수 없음:', `${tabName}-tab`);
            }
            
            currentTab = tabName;
            
            if (tabName === 'messages') {
                initMessageTab();
            } else if (tabName === 'posts') {
                loadMyPosts();
            } else if (tabName === 'stats') {
                loadMyStats();
            }
        });
    });
    
    console.log('탭 이벤트 리스너 등록 완료');
}

function setupProfileForm() {
    const profileForm = document.getElementById('profile-form');
    if (!profileForm) return;
    
    profileForm.addEventListener('submit', handleUpdateProfile);
    
    const inputs = profileForm.querySelectorAll('input, select, textarea');
    inputs.forEach(input => {
        input.addEventListener('blur', () => validateFormField(input));
        input.addEventListener('input', () => {
            if (input.classList.contains('error')) {
                validateFormField(input);
            }
        });
    });
}

function setupPasswordForm() {
    const passwordForm = document.getElementById('password-form');
    if (!passwordForm) return;
    
    passwordForm.addEventListener('submit', handleUpdatePassword);
    
    const inputs = passwordForm.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('blur', () => validateFormField(input));
        input.addEventListener('input', () => {
            if (input.classList.contains('error')) {
                validateFormField(input);
            }
        });
    });
}

async function loadUserProfile() {
    const loading = document.getElementById('loading');
    const errorBanner = document.getElementById('error-banner');
    const profileContainer = document.getElementById('profile-container');
    
    try {
        if (loading) showElement(loading);
        if (errorBanner) hideElement(errorBanner);
        
        console.log('=== 사용자 프로필 로딩 시작 ===');
        
        const userInfo = await APIClient.get('/api/auth/me');
        
        if (!userInfo || !userInfo.id) {
            throw new Error('로그인 세션이 만료되었습니다.');
        }
        
        console.log('현재 로그인 사용자:', userInfo);
        
        const userProfile = await APIClient.get(`/api/users/profile?userId=${userInfo.id}`);
        currentUser = userProfile;
        
        displayUserProfile(userProfile);
        if (profileContainer) showElement(profileContainer);
        
        console.log('사용자 프로필 로딩 완료');
        
    } catch (error) {
        console.error('사용자 프로필 로딩 실패:', error);
        
        if (error.status === 401) {
            alert('로그인 세션이 만료되었습니다. 다시 로그인해주세요.');
            window.location.href = '/login';
            return;
        }
        
        const errorMessage = document.getElementById('error-message');
        if (errorMessage) {
            errorMessage.textContent = error.message || '사용자 정보를 불러오는데 실패했습니다.';
        }
        if (errorBanner) showElement(errorBanner);
        
    } finally {
        if (loading) hideElement(loading);
    }
}

function displayUserProfile(user) {
    console.log('사용자 프로필 표시:', user);
    
    const emailInput = document.getElementById('email');
    const departmentInput = document.getElementById('department');
    const jobPositionInput = document.getElementById('job-position');
    const nicknameInput = document.getElementById('nickname');
    const companyInput = document.getElementById('company');
    
    if (emailInput) emailInput.value = user.email || '';
    if (departmentInput) departmentInput.value = user.department || '';
    if (jobPositionInput) jobPositionInput.value = user.jobPosition || '';
    if (nicknameInput) nicknameInput.value = user.nickname || '';
    if (companyInput) companyInput.value = user.company || '';
    
    const joinDate = document.getElementById('join-date');
    if (joinDate && user.createdAt) {
        const date = new Date(user.createdAt);
        joinDate.textContent = date.toLocaleDateString('ko-KR');
    }
    
    const profileEmail = document.getElementById('profile-email');
    if (profileEmail) {
        profileEmail.textContent = user.email;
    }
    
    const profileNickname = document.getElementById('profile-nickname');
    if (profileNickname) {
        profileNickname.textContent = user.nickname;
    }
    
    console.log('프로필 표시 완료');
}

async function handleUpdateProfile(e) {
    e.preventDefault();
    
    const form = e.target;
    const submitBtn = document.getElementById('profile-submit-btn');
    const errorBanner = document.getElementById('profile-error-banner');
    const errorMessage = document.getElementById('profile-error-message');
    
    const formData = {
        department: form.department.value.trim(),
        jobPosition: form.jobPosition.value.trim(),
        nickname: form.nickname.value.trim(),
        company: form.company.value.trim()
    };
    
    const errors = validateProfileForm(formData);
    
    if (hasValidationErrors(errors)) {
        showValidationErrors(errors, form);
        return;
    }
    
    try {
        setLoading(submitBtn, true);
        hideElement(errorBanner);
        
        await UserAPI.updateProfile(currentUser.id, formData);
        
        const updatedUser = { ...currentUser, ...formData };
        currentUser = updatedUser;
        Auth.setUser(updatedUser);
        
        showNotification('프로필이 성공적으로 업데이트되었습니다.', 'success');
        
    } catch (error) {
        console.error('Profile update error:', error);
        
        if (errorMessage) {
            errorMessage.textContent = error.message || 'SERVER_ERROR';
        }
        showElement(errorBanner);
        
    } finally {
        setLoading(submitBtn, false);
    }
}

async function handleUpdatePassword(e) {
    e.preventDefault();
    
    const form = e.target;
    const submitBtn = document.getElementById('password-submit-btn');
    const errorBanner = document.getElementById('password-error-banner');
    const errorMessage = document.getElementById('password-error-message');
    
    const formData = {
        newPassword: form.newPassword.value,
        confirmPassword: form.confirmPassword.value
    };
    
    const errors = validatePasswordForm(formData);
    
    if (hasValidationErrors(errors)) {
        showValidationErrors(errors, form);
        return;
    }
    
    try {
        setLoading(submitBtn, true);
        hideElement(errorBanner);
        
        await UserAPI.updatePassword(currentUser.id, formData.newPassword);
        
        showNotification('비밀번호가 성공적으로 변경되었습니다.', 'success');
        
        form.reset();
        
    } catch (error) {
        console.error('Password update error:', error);
        
        if (errorMessage) {
            errorMessage.textContent = error.message || 'SERVER_ERROR';
        }
        showElement(errorBanner);
        
    } finally {
        setLoading(submitBtn, false);
    }
}

function validateProfileForm(data) {
    const errors = {};
    
    if (!data.department || data.department.length < 2) {
        errors.department = '부서는 2글자 이상 입력해주세요.';
    }
    
    if (!data.jobPosition || data.jobPosition.length < 2) {
        errors.jobPosition = '직책은 2글자 이상 입력해주세요.';
    }
    
    if (!data.nickname || data.nickname.length < 2) {
        errors.nickname = '닉네임은 2글자 이상 입력해주세요.';
    }
    
    if (!data.company || data.company.length < 2) {
        errors.company = '회사명은 2글자 이상 입력해주세요.';
    }
    
    return errors;
}

function validatePasswordForm(data) {
    const errors = {};
    
    if (!data.newPassword || data.newPassword.length < 8) {
        errors.newPassword = '새 비밀번호는 8글자 이상 입력해주세요.';
    }
    
    if (data.newPassword !== data.confirmPassword) {
        errors.confirmPassword = '비밀번호가 일치하지 않습니다.';
    }
    
    return errors;
}

function initMessageTab() {
    setupMessageTabs();
    loadReceivedMessages();
}

function setupMessageTabs() {
    const messageTabBtns = document.querySelectorAll('.message-tab-btn');
    
    messageTabBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tabType = this.dataset.messageTab;
            
            messageTabBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            
            document.getElementById('received-messages').classList.add('hidden');
            document.getElementById('sent-messages').classList.add('hidden');
            
            currentMessageTab = tabType;
            currentMessagePage = 0;
            
            if (tabType === 'received') {
                document.getElementById('received-messages').classList.remove('hidden');
                loadReceivedMessages();
            } else if (tabType === 'sent') {
                document.getElementById('sent-messages').classList.remove('hidden');
                loadSentMessages();
            }
        });
    });
}

async function loadReceivedMessages() {
    const container = document.getElementById('received-messages');
    
    try {
        if (!currentUser || !currentUser.id) {
            container.innerHTML = '<div class="error-banner"><p>사용자 정보를 먼저 로드해주세요.</p></div>';
            return;
        }
        
        container.innerHTML = '<div class="loading"><div class="spinner"></div><p>받은 쪽지를 불러오는 중...</p></div>';
        
        const response = await APIClient.get(`/api/messages/received?userId=${currentUser.id}`);
        
        if (response.messages && response.messages.length > 0) {
            renderMessageList(response.messages, container, 'received');
        } else {
            container.innerHTML = '<div class="empty-state"><h3>받은 쪽지가 없습니다</h3><p>새로운 쪽지를 기다려보세요!</p></div>';
        }
        
    } catch (error) {
        console.error('받은 쪽지 로드 실패:', error);
        
        if (error.status === 401) {
            container.innerHTML = '<div class="error-banner"><p>로그인이 필요합니다.</p></div>';
        } else {
            container.innerHTML = '<div class="error-banner"><p>받은 쪽지를 불러오는데 실패했습니다.</p></div>';
        }
    }
}

async function loadSentMessages() {
    const container = document.getElementById('sent-messages');
    
    try {
        if (!currentUser || !currentUser.id) {
            container.innerHTML = '<div class="error-banner"><p>사용자 정보를 먼저 로드해주세요.</p></div>';
            return;
        }
        
        container.innerHTML = '<div class="loading"><div class="spinner"></div><p>보낸 쪽지를 불러오는 중...</p></div>';
        
        console.log('보낸쪽지 요청 userId:', currentUser.id);
        
        const response = await APIClient.get(`/api/messages/sent?userId=${currentUser.id}`);
        
        console.log('보낸 쪽지 응답:', response);
        
        if (response && response.messages && response.messages.length > 0) {
            renderMessageList(response.messages, container, 'sent');
        } else {
            container.innerHTML = '<div class="empty-state"><h3>보낸 쪽지가 없습니다</h3><p>새로운 쪽지를 보내보세요!</p></div>';
        }
        
    } catch (error) {
        console.error('보낸 쪽지 로드 실패:', error);
        
        if (error.status === 401) {
            container.innerHTML = '<div class="error-banner"><p>로그인이 필요합니다.</p></div>';
        } else if (error.status === 404) {
            container.innerHTML = '<div class="error-banner"><p>보낸쪽지 API를 찾을 수 없습니다.</p></div>';
        } else {
            container.innerHTML = '<div class="error-banner"><p>보낸 쪽지를 불러오는데 실패했습니다.</p></div>';
        }
    }
}

function renderMessageList(messages, container, type) {
    let html = '<div class="message-list">';
    
    messages.forEach(message => {
        html += createMessageCard(message, type);
    });
    
    html += '</div>';
    container.innerHTML = html;
}

function createMessageCard(message, type) {
    console.log('메시지 카드 생성:', message, 'type:', type);
    
    const isUnread = !message.isRead && type === 'received';
    const unreadClass = isUnread ? 'unread' : '';
    
    let contactInfo = '';
    if (type === 'received') {
        contactInfo = `<strong>보낸이:</strong> ${sanitizeHTML(message.senderName || '알 수 없음')}`;
    } else {
        contactInfo = `<strong>받는이:</strong> ${sanitizeHTML(message.receiverName || '알 수 없음')}`;
    }
    
    return `
        <div class="message-card ${unreadClass}" onclick="openMessageDetail(${message.id})">
            <div class="message-meta">
                <span>${contactInfo}</span>
                <div>
                    <span class="message-date">${formatDate(message.createdAt)}</span>
                </div>
            </div>
            <div class="message-content">
                ${sanitizeHTML(truncateText(message.content, 100))}
            </div>
            <div class="message-actions">
                <button onclick="deleteMessage(${message.id}, event)" class="btn btn-sm btn-danger">삭제</button>
            </div>
        </div>
    `;
}

async function openMessageDetail(messageId) {
    try {
        if (!currentUser || !currentUser.id) {
            alert('사용자 정보가 없습니다.');
            return;
        }
        
        if (currentMessageTab === 'received') {
            await APIClient.put(`/api/messages/${messageId}/read`);
        }
        
        const endpoint = currentMessageTab === 'received' ? 
            `/api/messages/received` : 
            `/api/messages/sent`;
        
        const response = await APIClient.get(endpoint);
        const message = response.messages.find(m => m.id === messageId);
        
        if (message) {
            alert(`쪽지 내용:\n\n${message.content}`);
            
            if (currentMessageTab === 'received') {
                loadReceivedMessages();
                updateUnreadMessageCount();
            }
        }
        
    } catch (error) {
        console.error('쪽지 상세 보기 실패:', error);
        showNotification('쪽지를 불러오는데 실패했습니다.', 'error');
    }
}

async function deleteMessage(messageId, event) {
    event.stopPropagation();
    
    if (!confirm('이 쪽지를 삭제하시겠습니까?')) {
        return;
    }
    
    try {
        if (!currentUser || !currentUser.id) {
            throw new Error('사용자 정보가 없습니다.');
        }
        
        const endpoint = currentMessageTab === 'received' ? 
            `/api/messages/received/${messageId}` :
            `/api/messages/sent/${messageId}`;
        
        await APIClient.delete(endpoint);
        
        if (currentMessageTab === 'received') {
            loadReceivedMessages();
            updateUnreadMessageCount();
        } else {
            loadSentMessages();
        }
        
        showNotification('쪽지가 삭제되었습니다.', 'success');
        
    } catch (error) {
        console.error('쪽지 삭제 실패:', error);
        showNotification('쪽지 삭제에 실패했습니다.', 'error');
    }
}

async function sendNewMessage(recipientId, content) {
    try {
        if (!currentUser || !currentUser.id) {
            return false;
        }
        
        await APIClient.post('/api/messages', {
            receiverId: recipientId,
            content: content,
            senderId: currentUser.id
        });
        
        setTimeout(() => {
            if (currentMessageTab === 'sent') {
                loadSentMessages();
            }
        }, 500);
        
        return true;
    } catch (error) {
        console.error('쪽지 전송 실패:', error);
        return false;
    }
}

async function updateUnreadMessageCount() {
    try {
        if (!currentUser || !currentUser.id) {
            return 0;
        }
        
        const response = await APIClient.get(`/api/messages/received`);
        const unreadCount = response.messages ? response.messages.filter(m => !m.isRead).length : 0;
        
        const notificationBadge = document.getElementById('message-notification-badge');
        if (notificationBadge) {
            if (unreadCount > 0) {
                notificationBadge.textContent = unreadCount;
                notificationBadge.classList.remove('hidden');
            } else {
                notificationBadge.classList.add('hidden');
            }
        }
        
        const messageTabBadge = document.getElementById('message-tab-badge');
        if (messageTabBadge) {
            if (unreadCount > 0) {
                messageTabBadge.textContent = unreadCount;
                messageTabBadge.classList.remove('hidden');
            } else {
                messageTabBadge.classList.add('hidden');
            }
        }
        
        return unreadCount;
    } catch (error) {
        console.error('읽지 않은 쪽지 개수 업데이트 실패:', error);
        return 0;
    }
}

const UserAPI = {
    async getProfile(userId) {
        try {
            return await APIClient.get(`/api/users/profile?userId=${userId}`);
        } catch (error) {
            throw error;
        }
    },
    
    async updateProfile(userId, data) {
        try {
            const params = new URLSearchParams({
                department: data.department,
                jobPosition: data.jobPosition,
                nickname: data.nickname,
                company: data.company,
                userId: userId
            });
            
            return await APIClient.put(`/api/users/${userId}?${params}`);
        } catch (error) {
            throw error;
        }
    },
    
    async updatePassword(userId, newPassword) {
        try {
            const params = new URLSearchParams({
                newPassword: newPassword,
                userId: userId
            });
            
            return await APIClient.put(`/api/users/${userId}/password?${params}`);
        } catch (error) {
            throw error;
        }
    }
};

async function loadMyPosts() {
    const container = document.getElementById('my-posts-list');
    
    try {
        if (!currentUser || !currentUser.id) {
            container.innerHTML = '<div class="error-banner"><p>사용자 정보를 먼저 로드해주세요.</p></div>';
            return;
        }
        
        container.innerHTML = '<div class="loading"><div class="spinner"></div><p>내 게시글을 불러오는 중...</p></div>';
        
        const response = await APIClient.get(`/api/posts?page=0&size=50`);
        
        if (response && response.posts) {
            const myPosts = response.posts.filter(post => post.authorId === currentUser.id);
            
            if (myPosts.length > 0) {
                let html = '<div class="my-posts-list">';
                myPosts.forEach(post => {
                    html += createMyPostCard(post);
                });
                html += '</div>';
                container.innerHTML = html;
            } else {
                container.innerHTML = '<div class="empty-state"><h3>작성한 게시글이 없습니다</h3><p>첫 번째 게시글을 작성해보세요!</p></div>';
            }
        } else {
            container.innerHTML = '<div class="empty-state"><h3>작성한 게시글이 없습니다</h3><p>첫 번째 게시글을 작성해보세요!</p></div>';
        }
        
    } catch (error) {
        console.error('내 게시글 로드 실패:', error);
        container.innerHTML = '<div class="error-banner"><p>게시글을 불러오는데 실패했습니다.</p></div>';
    }
}

function createMyPostCard(post) {
    const createdAt = formatDate(post.createdAt);
    
    return `
        <div class="my-post-card" onclick="window.location.href='/post-detail?id=${post.id}'">
            <div class="post-header">
                <h4 class="post-title">${sanitizeHTML(post.title)}</h4>
                <div class="post-meta">
                    <span class="post-date">${createdAt}</span>
                    <div class="post-stats">
                        <span>좋아요 ${post.likeCount || 0}</span>
                        <span>댓글 ${post.commentCount || 0}</span>
                    </div>
                </div>
            </div>
            <div class="post-content">
                ${sanitizeHTML(truncateText(post.content, 100))}
            </div>
        </div>
    `;
}

async function loadMyStats() {
    const totalPostsEl = document.getElementById('total-posts');
    const totalCommentsEl = document.getElementById('total-comments');
    const totalLikesEl = document.getElementById('total-likes');
    
    try {
        if (!currentUser || !currentUser.id) {
            return;
        }
        
        if (totalPostsEl) totalPostsEl.textContent = '로딩...';
        if (totalCommentsEl) totalCommentsEl.textContent = '로딩...';
        if (totalLikesEl) totalLikesEl.textContent = '로딩...';
        
        const response = await APIClient.get(`/api/posts?page=0&size=50`);
        
        if (response && response.posts) {
            const myPosts = response.posts.filter(post => post.authorId === currentUser.id);
            const totalLikes = myPosts.reduce((sum, post) => sum + (post.likeCount || 0), 0);
            
            if (totalPostsEl) totalPostsEl.textContent = myPosts.length;
            if (totalLikesEl) totalLikesEl.textContent = totalLikes;
        } else {
            if (totalPostsEl) totalPostsEl.textContent = '0';
            if (totalLikesEl) totalLikesEl.textContent = '0';
        }
        
        if (totalCommentsEl) totalCommentsEl.textContent = '-';
        
    } catch (error) {
        console.error('통계 로드 실패:', error);
        if (totalPostsEl) totalPostsEl.textContent = '-';
        if (totalCommentsEl) totalCommentsEl.textContent = '-';
        if (totalLikesEl) totalLikesEl.textContent = '-';
    }
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initMyPagePage);
} else {
    initMyPagePage();
}