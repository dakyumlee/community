class BookmarkAPI {
    static async toggleBookmark(postId) {
        try {
            const user = Auth.getUser();
            if (!user || !user.id) {
                throw new Error('로그인이 필요합니다.');
            }
            
            const response = await APIClient.post(`/bookmarks/toggle?userId=${user.id}&postId=${postId}`);
            
            if (response) {
                showNotification(response ? '북마크에 추가되었습니다.' : '북마크가 해제되었습니다.', 'success');
            }
            
            return response;
        } catch (error) {
            showNotification(error.message || '북마크 처리 중 오류가 발생했습니다.', 'error');
            throw error;
        }
    }

    static async getMyBookmarks(page = 0, size = 10) {
        try {
            const user = Auth.getUser();
            if (!user || !user.id) {
                throw new Error('로그인이 필요합니다.');
            }
            return await APIClient.get(`/bookmarks?userId=${user.id}&page=${page}&size=${size}`);
        } catch (error) {
            console.error('북마크 목록 조회 실패:', error);
            throw error;
        }
    }

    static async checkBookmarkStatus(postId) {
        try {
            const user = Auth.getUser();
            if (!user || !user.id) {
                return false;
            }
            return await APIClient.get(`/bookmarks/check?userId=${user.id}&postId=${postId}`);
        } catch (error) {
            console.error('북마크 상태 확인 실패:', error);
            return false;
        }
    }

    static async addBookmark(postId) {
        try {
            const user = Auth.getUser();
            if (!user || !user.id) {
                throw new Error('로그인이 필요합니다.');
            }
            const result = await APIClient.post(`/bookmarks?userId=${user.id}&postId=${postId}`);
            showNotification('북마크에 추가되었습니다.', 'success');
            return result;
        } catch (error) {
            showNotification(error.message || '북마크 추가 중 오류가 발생했습니다.', 'error');
            throw error;
        }
    }

    static async removeBookmark(postId) {
        try {
            const user = Auth.getUser();
            if (!user || !user.id) {
                throw new Error('로그인이 필요합니다.');
            }
            const result = await APIClient.delete(`/bookmarks?userId=${user.id}&postId=${postId}`);
            showNotification('북마크가 해제되었습니다.', 'success');
            return result;
        } catch (error) {
            showNotification(error.message || '북마크 해제 중 오류가 발생했습니다.', 'error');
            throw error;
        }
    }
}

console.log('BookmarkAPI loaded');