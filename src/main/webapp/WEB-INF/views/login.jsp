<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>로그인 - 커뮤니티</title>
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
			<nav class="nav">
				<a href="/register" class="btn btn-primary btn-sm">회원가입</a>
			</nav>
		</div>
	</header>

	<main class="main-content">
		<div class="container" style="max-width: 400px;">
			<div class="page-header">
				<h1>로그인</h1>
				<p>커뮤니티에 로그인하여 다양한 사람들과 소통하세요</p>
			</div>

			<div class="card">
				<form id="login-form">
					<div class="error-banner hidden" id="error-banner">
						<p id="error-message"></p>
					</div>

					<div class="form-group">
						<label for="email" class="form-label">이메일</label>
						<input type="email" id="email" name="email" class="form-control" placeholder="이메일을 입력하세요" required>
						<div class="error-message hidden" id="email-error"></div>
					</div>

					<div class="form-group">
						<label for="password" class="form-label">비밀번호</label>
						<input type="password" id="password" name="password" class="form-control" placeholder="비밀번호를 입력하세요" required>
						<div class="error-message hidden" id="password-error"></div>
					</div>

					<button type="submit" class="btn btn-primary w-full" id="submit-btn">로그인</button>
				</form>

				<div class="text-center mt-3">
					<p>아직 계정이 없으신가요? <a href="/register">회원가입</a></p>
				</div>
			</div>
		</div>
	</main>
	<script src="/static/js/utils/constants.js"></script>
	<script src="/static/js/utils/helpers.js"></script>
	<script src="/static/js/utils/validation.js"></script>
	<script src="/static/js/utils/auth.js"></script>
	<script src="/static/js/api/apiClient.js"></script>
	<script src="/static/js/api/authAPI.js"></script>
	<script src="/static/js/components/header.js"></script>
	<script src="/static/js/pages/login.js"></script>
	<script src="/static/js/app.js"></script>
</body>
</html>