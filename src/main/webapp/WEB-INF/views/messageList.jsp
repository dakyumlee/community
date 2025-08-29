<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>쪽지함</title>
    <style>
        body { font-family: Arial, sans-serif; }
        .container { width: 80%; margin: 20px auto; }
        h1 { text-align: center; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
        th { background-color: #f2f2f2; }
        .read { color: gray; }
        .unread { font-weight: bold; }
        .actions { text-align: right; margin-bottom: 10px; }
    </style>
</head>
<body>

<div class="tabs">
    <button class="tab-btn active" data-tab="received">받은 쪽지함</button>

    <button class="tab-btn" data-tab="sent">보낸 쪽지함</button>
</div>

<div class="container">
    <h1 id="h1">${pageTitle }</h1>
    


    <table>
        <thead>
            <tr>
                <th>보낸 사람</th>
                <th>제목</th>
                <th>날짜</th>
                <th>삭제</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="message" items="${messageList}">
                <tr>
                    <td>${message.senderName}</td>
                    <td class="${message.isRead eq 1 ? 'read' : 'unread'}">
                        <a href="/Message/View?id=${message.id}">
                            ${message.title}
                        </a>
                    </td>
                    <td>${message.createdAt}</td>
                    <td>
                        <a href="/Message/DeleteMessage?id=${message.id}&pageType=${pageType}">삭제</a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty messageList}">
                <tr>
                    <td colspan="4" style="text-align: center;">받은 쪽지가 없습니다.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const tabs = document.querySelectorAll('.tab-btn');

        tabs.forEach(tab => {
            tab.addEventListener('click', function() {
                // 클릭된 버튼의 data-tab 값을 가져옵니다. ('received' 또는 'sent')
                const tabType = this.getAttribute('data-tab');
				const h1 = document.getElementById('h1');
                // 탭 종류에 따라 URL을 변경하여 페이지를 이동합니다.
                if (tabType === 'received') {
                    location.href = `/Message/List/Received?userId=${userId}`;
                    
                } else if (tabType === 'sent') {
                    location.href = `/Message/List/Sent?userId=${userId}`;
                    
                }
            });
        });
    });
</script>

</body>
</html>