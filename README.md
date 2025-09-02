# 🌐 익명 커뮤니티 게시판 (Team 꽉Java)

## 📖 프로젝트 소개
**익명 기반 사내 커뮤니티 플랫폼**
사용자는 자유롭게 글과 댓글을 작성하고, 익명 닉네임으로 소통할 수 있습니다.
관리자는 게시글과 댓글을 모니터링하고, 사용자 관리와 통계를 확인할 수 있습니다.

- 📅 프로젝트 기간: 2025.08.20 ~ 2025.09.03
- 👥 개발 인원: 이다겸, 윤호준, 최수인, 김근수, 권영승

---

## 🚀 개발 목적
- 직장 내외 자유로운 소통 공간 부재 문제 해결  
- 익명성 보장을 통해 위계와 부담을 줄이고 진솔한 의견 교환 가능  
- 관리 기능을 제공해 **건전한 커뮤니티 운영 지원**  

---

## ⚙️ 기술 스택
| 영역 | 기술 |
|------|------|
| Frontend | HTML, CSS, JavaScript (Vanilla) |
| Backend | Spring Boot (Java 17) |
| Database | Oracle |
| ORM/Mapper | MyBatis |
| Build & Deploy | Gradle |
| 협업 | GitHub, Notion, KakaoTalk |

---

## 🗂️ 주요 기능
### 👤 사용자
- 회원가입: 회사명 + 랜덤 숫자 3자리로 닉네임 자동 생성  
- 로그인/로그아웃: 권한(일반/관리자) 구분  
- 마이페이지: 내가 쓴 글, 댓글, 쪽지함, 북마크 관리  
- 게시판: 글 작성(이미지 포함), 수정/삭제, 페이징 목록 조회, 상세보기  
- 댓글/대댓글: 작성, 수정, 삭제 (실시간 반영)  
- 좋아요: 게시글/댓글 1인 1회 토글  
- 북마크: 게시글 저장 후 북마크 페이지에서 열람  
- 쪽지: 1:1 메시지 송수신, 읽음/삭제 처리  

### 🛠️ 관리자
- 게시글/댓글 전체 조회 및 삭제  
- 사용자 권한 관리 (권한 변경, 강제 조치)  
- 통계 대시보드 (게시글·댓글·좋아요 집계)  

---

## 🗄️ DB 설계 (ERD)
![ERD](./docs/erd.png)

핵심 테이블  
- USERS: 회원 정보 (email, password, role, nickname)  
- POSTS: 게시글 (author_id, content, like_count, comment_count)  
- COMMENTS: 댓글/대댓글 (post_id, parent_id 구조)  
- LIKES: 좋아요 (user_id, post_id UNIQUE)  
- BOOKMARKS: 북마크 (user_id, post_id UNIQUE)  
- MESSAGES: 쪽지 (sender_id, receiver_id, soft delete 지원)  
- POST_FILES: 첨부파일 (post_id FK, 경로 및 파일 정보)  

---

## 💻 화면 설계 (UI)
![UI](./docs/ui.png)

- 메인 게시판: 글 목록, 페이지네이션, 좋아요/댓글 수 표시  
- 상세 페이지: 글 본문, 댓글/대댓글, 좋아요 토글, 쪽지 전송  
- 마이페이지: 내가 쓴 글·댓글, 북마크, 쪽지함  
- 관리자 페이지: 사용자/게시글/댓글 관리, 통계 대시보드  

---

## ⚡ 실행 방법
### 1. 프로젝트 클론
```bash
git clone https://github.com/dakyumlee/community.git
cd community
```

### 2. DB 설정 (Oracle)
- schema.sql 실행 후 테이블 생성  
- application.yml에 DB 접속 정보 입력  

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521/XE
    username: community
    password: password
```

### 3. 빌드 및 실행
```bash
./gradlew build
java -jar build/libs/community-0.0.1-SNAPSHOT.jar
```

### 4. 접속
- 메인: http://localhost:8080  
- 관리자 페이지: role=ADMIN 계정으로 로그인 시 접근  

---

## 👥 팀원 역할 분담
| 이름 | 담당 기능 |
|------|-----------|
| **이다겸 (팀장)** | 회원가입/로그인, 게시판 CRUD, 마이페이지, 관리자 기능, DB 설계 |
| 윤호준 | 쪽지 기능, 마이페이지 쪽지함 |
| 최수인 | 댓글/대댓글 CRUD |
| 김근수 | 좋아요 기능 |
| 권영승 | 북마크 기능 |

---

## 📝 향후 개선점
- 비밀번호 평문 저장 → 해시 암호화 적용 예정  
- 검색 성능 향상 위해 ElasticSearch 도입 고려  
- 실시간 알림(WebSocket) 기능 추가  
- 모바일 반응형 UI 적용  
