class MessageAPI {

    static async sendMessage(recipientId, content) {
        console.log('MessageAPI.sendMessage 호출됨');
        console.log('recipientId:', recipientId);
        console.log('content:', content);

        try {
            const messageData = {
                receiverId: recipientId,
                title: "쪽지",
                content: content
            };

            console.log('전송할 데이터:', messageData);

            const token = Auth.getToken();
            if (!token) {
                throw new Error('로그인이 필요합니다.');
            }

            const url = `${API_BASE_URL}/api/messages`;
            console.log('요청 URL:', url);

            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify(messageData)
            });

            console.log('응답 상태:', response.status);

            if (!response.ok) {
                const errorText = await response.text();
                console.error('서버 에러 응답:', errorText);
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('성공 응답:', result);

            if (result.status === 'success') {
                alert('쪽지가 전송되었습니다.');
                
                if (typeof loadSentMessages === 'function') {
                    console.log('보낸쪽지함 새로고침 중...');
                    setTimeout(() => {
                        loadSentMessages();
                    }, 500);
                }
                
                if (typeof currentMessageTab !== 'undefined' && currentMessageTab === 'sent') {
                    console.log('마이페이지 보낸쪽지 탭 새로고침 중...');
                    setTimeout(() => {
                        if (typeof loadSentMessages === 'function') {
                            loadSentMessages();
                        }
                    }, 500);
                }
                
            } else {
                alert('쪽지 전송에 실패했습니다: ' + (result.message || '알 수 없는 오류'));
            }

            return result;

        } catch (error) {
            console.error('쪽지 전송 실패:', error);
            alert('쪽지 전송에 실패했습니다: ' + error.message);
            throw error;
        }
    }

    static async getReceivedMessages(page = 0, size = 10) {
        try {
            const token = Auth.getToken();
            if (!token) {
                throw new Error('로그인이 필요합니다.');
            }

            const url = `${API_BASE_URL}/api/messages/received`;
            const response = await fetch(url, {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('받은 쪽지 조회 실패:', error);
            throw error;
        }
    }

    static async getSentMessages(page = 0, size = 10) {
        try {
            const token = Auth.getToken();
            if (!token) {
                throw new Error('로그인이 필요합니다.');
            }

            const url = `${API_BASE_URL}/api/messages/sent`;
            const response = await fetch(url, {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();
        } catch (error) {
            console.error('보낸 쪽지 조회 실패:', error);
            throw error;
        }
    }
}

console.log('MessageAPI loaded - debug versionㅋㅋ');