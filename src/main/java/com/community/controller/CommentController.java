package com.community.controller;

import com.community.dto.request.CommentRequest;
import com.community.dto.response.CommentResponse;
import com.community.mapper.CommunityMapper;
//1차 푸쉬 파일 댓글 작성이 안 되는이유
//기존 코드는 서버사이드 렌더링용이었는데, 우리는 REST API가 필요함
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.ResponseEntity; //JSON 응답을 위해 추가
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession; //세션 기반 인증으로 변경

import java.util.HashMap; //JSON 응답 구성용임 
import java.util.List;
import java.util.Map; //JSON 응답 구성용임 마찬가지로 

// @Controller -> @RestController로 변경 (JSON 응답용)
@RestController
// @RequestMapping("/Comment") -> "/api"로 변경 (REST API 표준)
@RequestMapping("/api")
@CrossOrigin(origins = "*") // CORS 문제 해결용 추가
public class CommentController{
    private final CommunityMapper commentMapper;
    
    public CommentController(CommunityMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

// 댓글목록 - 기존 방식 주석처리하고 REST API로 재작성
// @RequestMapping("/List") -> @GetMapping("/posts/{postId}/comments")로 변경
@GetMapping("/posts/{postId}/comments")
// ModelAndView -> ResponseEntity<Map<String, Object>>로 변경 (JSON 응답)
public ResponseEntity<Map<String, Object>> getCommentsByPost(
        @PathVariable Long postId, // URL에서 postId 받아오기
        @RequestParam(defaultValue = "1") int page, // 페이징 파라미터 추가
        @RequestParam(defaultValue = "10") int size, // 페이징 파라미터 추가
        HttpSession session) { // Authentication -> HttpSession으로 변경
    
    try { //예외처리 추가
        System.out.println("댓글 목록 조회"); // 디버깅용 로그
        System.out.println("게시글 ID: " + postId);
        
        //댓글 목록 조회
        List<CommentResponse> commentList = commentMapper.findCommentsByPostId(postId);
        System.out.println("조회된 댓글 수: " + commentList.size());
        
        //Authentication 대신 HttpSession 사용
        Long userId = (Long) session.getAttribute("userId");
        if (userId != null) {
            System.out.println("로그인된 사용자 ID: " + userId);
            //본인 댓글 표시
            for (CommentResponse comment : commentList) {
                comment.setAuthor(comment.getAuthorId().equals(userId));
            }
        } else {
            System.out.println("비로그인 상태로 댓글 조회");
        }
        
        //ModelAndView 대신 Map으로 JSON 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("comments", commentList);
        response.put("totalElements", commentList.size());
        response.put("currentPage", page); //페이지 정보 추가
        response.put("totalPages", 1); //추후 실제 페이징 구현 필요
        
        return ResponseEntity.ok(response); //JSON 응답 반환
        
    } catch (Exception e) { //예외처리 추가
        System.out.println("댓글 조회 에러: " + e.getMessage());
        e.printStackTrace();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "댓글을 불러올 수 없습니다.");
        return ResponseEntity.status(500).body(errorResponse);
    }
}

//댓글작성 - 폼 방식에서 JSON API로 변경
//@RequestMapping("/WriteForm") 제거 - REST API에서는 폼이 필요없음
//@RequestMapping("/Write") -> @PostMapping("/posts/{postId}/comments")로 변경
@PostMapping("/posts/{postId}/comments")
//ModelAndView -> ResponseEntity<Map<String, Object>>로 변경
public ResponseEntity<Map<String, Object>> createComment(
        @PathVariable Long postId, //URL에서 postId 받아오기
        @RequestBody CommentRequest request, //JSON 요청 본문 받기
        HttpSession session) { //Authentication -> HttpSession으로 변경
    
    Map<String, Object> response = new HashMap<>(); //응답용 Map 생성
    
    try { //예외처리 추가
        System.out.println("=== 댓글 작성 시도 ==="); //디버깅용 로그
        System.out.println("게시글 ID: " + postId);
        System.out.println("댓글 내용: " + request.getContent());
        
        //로그인 확인 - HttpSession 방식으로 변경
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("로그인되지 않은 사용자");
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response); // 401 Unauthorized 반환
        }
        
        System.out.println("로그인된 사용자 ID: " + userId);
        
        //기존 로직 재사용 - 댓글 내용 검증
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            System.out.println("댓글 내용이 비어있음");
            response.put("error", "댓글 내용을 입력해주세요.");
            return ResponseEntity.badRequest().body(response); //400 Bad Request 반환
        }

        //기존 로직 재사용 - 댓글 저장
        Long commentId = commentMapper.insertComment(
            request.getContent().trim(),
            postId, //PathVariable에서 받은 postId 사용
            userId,
            request.getParentId()
        );

        if (commentId == null) { //저장 실패 체크 추가
            System.out.println("댓글 저장 실패");
            response.put("error", "댓글 작성에 실패했습니다.");
            return ResponseEntity.status(500).body(response);
        }

        System.out.println("댓글 작성 성공, ID: " + commentId);
        
        //작성된 댓글 정보 반환 (기존에는 리다이렉트만 했음)
        CommentResponse createdComment = commentMapper.findCommentById(commentId);
        if (createdComment != null) {
            createdComment.setAuthor(true); //방금 작성한 댓글이므로 true
        }
        
        response.put("comment", createdComment); //작성된 댓글 정보 포함
        response.put("message", "댓글이 작성되었습니다.");
        return ResponseEntity.ok(response); //200 OK와 함께 JSON 반환
        
    } catch (Exception e) { //예외처리 추가
        System.out.println("댓글 작성 에러: " + e.getMessage());
        e.printStackTrace();
        response.put("error", "댓글 작성 중 오류가 발생했습니다.");
        return ResponseEntity.status(500).body(response);
    }
}

//댓글수정 - 폼 방식에서 직접 수정 API로 변경
//@RequestMapping("/EditForm") 제거 - REST API에서는 수정 폼이 필요없어용!!
//@RequestMapping("/Edit") -> @PutMapping("/comments/{id}")로 변경
@PutMapping("/comments/{id}")
//ModelAndView -> ResponseEntity<Map<String, Object>>로 변경
public ResponseEntity<Map<String, Object>> updateComment(
        @PathVariable Long id, //URL에서 댓글 ID 받기
        @RequestBody CommentRequest request, // JSON 요청 본문 받기
        HttpSession session) { //Authentication -> HttpSession으로 변경
    
    Map<String, Object> response = new HashMap<>(); //응답용 Map 생성
    
    try { //예외처리 추가
        System.out.println("=== 댓글 수정 시도 ==="); //디버깅용 로그
        System.out.println("댓글 ID: " + id);
        
        //로그인 확인 - HttpSession 방식으로 변경
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        //기존 로직 재사용 - 수정 권한 확인
        if (!commentMapper.isCommentAuthor(id, userId) && !commentMapper.isAdmin(userId)) {
            response.put("error", "댓글 수정 권한이 없습니다.");
            return ResponseEntity.status(403).body(response); //403 Forbidden 반환되는 코드
        }

        //댓글 내용 검증 추가
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            response.put("error", "댓글 내용을 입력해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        //기존 로직 재사용 - 댓글 수정
        boolean updated = commentMapper.updateComment(id, request.getContent().trim());
        if (!updated) { // 수정 실패 체크 추가
            response.put("error", "댓글을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(response); // 404 Not Found 반환
        }

        //수정된 댓글 정보 반환 (기존에는 리다이렉트만 했음)
        CommentResponse updatedComment = commentMapper.findCommentById(id);
        if (updatedComment != null) {
            updatedComment.setAuthor(updatedComment.getAuthorId().equals(userId));
        }
        
        response.put("comment", updatedComment); //수정된 댓글 정보 포함
        response.put("message", "댓글이 수정되었습니다.");
        return ResponseEntity.ok(response);
        
    } catch (Exception e) { //예외처리 추가
        System.out.println("댓글 수정 에러: " + e.getMessage());
        response.put("error", "댓글 수정 중 오류가 발생했습니다.");
        return ResponseEntity.status(500).body(response);
    }
}

//댓글 삭제 - URL과 응답 방식 변경
//@RequestMapping("/Delete") -> @DeleteMapping("/comments/{id}")로 변경
@DeleteMapping("/comments/{id}")
//ModelAndView -> ResponseEntity<Map<String, Object>>로 변경
public ResponseEntity<Map<String, Object>> deleteComment(
        @PathVariable Long id, //URL에서 댓글 ID 받기
        HttpSession session) { //Authentication -> HttpSession으로 변경
    
    Map<String, Object> response = new HashMap<>(); //응답용 Map 생성
    
    try { //예외처리 추가
        System.out.println("댓글 삭제 시도"); //디버깅용 로그
        System.out.println("댓글 ID: " + id);
        
        //로그인 확인 - HttpSession 방식으로 변경
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole"); //관리자 권한 확인용 추가
        
        if (userId == null) {
            response.put("error", "로그인이 필요합니다.");
            return ResponseEntity.status(401).body(response);
        }

        //기존 로직 재사용 +더 명확한 권한 확인
        boolean isAuthor = commentMapper.isCommentAuthor(id, userId);
        boolean isAdmin = "ADMIN".equals(userRole); //세션의 userRole 사용
        
        System.out.println("작성자 여부: " + isAuthor + ", 관리자 여부: " + isAdmin);
        
        if (!isAuthor && !isAdmin) {
            response.put("error", "댓글 삭제 권한이 없습니다.");
            return ResponseEntity.status(403).body(response); //403 Forbidden 반환
        }

        //댓글 삭제
        boolean deleted = commentMapper.deleteComment(id);
        if (!deleted) { //삭제 실패 체크 추가
            response.put("error", "댓글을 찾을 수 없습니다.");
            return ResponseEntity.status(404).body(response); //404 Not Found 반환
        }

        response.put("message", "댓글이 삭제되었습니다."); //성공 메시지
        return ResponseEntity.ok(response); //JSON 응답 반환 이건 자바스크립트
        
    } catch (Exception e) { //예외처리 추가
        System.out.println("댓글 삭제 에러: " + e.getMessage());
        response.put("error", "댓글 삭제 중 오류가 발생했습니다.");
        return ResponseEntity.status(500).body(response);
    }
}
}