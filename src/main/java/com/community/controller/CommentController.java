package com.community.controller;

import com.community.dto.request.CommentRequest;
import com.community.dto.response.CommentResponse;
import com.community.mapper.CommunityMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommunityMapper commentMapper;
    private final CommunityMapper userMapper;

    public CommentController(CommunityMapper commentMapper, CommunityMapper userMapper) {
        this.commentMapper = commentMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getCommentsByPost(@PathVariable Long postId,
            Authentication authentication) {
        
        List<CommentResponse> comments = commentMapper.findCommentsByPostId(postId);
        
        if (authentication != null) {
            String email = authentication.getName();
            Long userId = userMapper.findIdByEmail(email);
            
            if (userId != null) {
                for (CommentResponse comment : comments) {
                    comment.setAuthor(comment.getAuthorId().equals(userId));
                }
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("comments", comments);
        response.put("totalElements", comments.size());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        
        String email = authentication.getName();
        Long userId = userMapper.findIdByEmail(email);
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Long commentId = commentMapper.insertComment(
            request.getContent().trim(),
            postId,
            userId,
            request.getParentId()
        );

        if (commentId == null) {
            return ResponseEntity.internalServerError().build();
        }

        CommentResponse createdComment = commentMapper.findCommentById(commentId);
        if (createdComment != null) {
            createdComment.setAuthor(true);
        }
        
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        
        String email = authentication.getName();
        Long userId = userMapper.findIdByEmail(email);
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        if (!commentMapper.isCommentAuthor(id, userId) && !userMapper.isAdmin(userId)) {
            return ResponseEntity.status(403).build();
        }

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        boolean updated = commentMapper.updateComment(id, request.getContent().trim());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }

        CommentResponse updatedComment = commentMapper.findCommentById(id);
        if (updatedComment != null) {
            updatedComment.setAuthor(updatedComment.getAuthorId().equals(userId));
        }
        
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(@PathVariable Long id, 
            Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        
        String email = authentication.getName();
        Long userId = userMapper.findIdByEmail(email);
        
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        if (!commentMapper.isCommentAuthor(id, userId) && !userMapper.isAdmin(userId)) {
            return ResponseEntity.status(403).build();
        }

        boolean deleted = commentMapper.deleteComment(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "댓글이 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}