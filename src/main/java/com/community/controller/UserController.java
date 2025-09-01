package com.community.controller;

import com.community.dto.response.UserResponse;
import com.community.mapper.CommunityMapper;
import com.community.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private CommunityMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                return jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userMapper.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse user = userMapper.findUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@RequestParam Long userId) {
        UserResponse user = userMapper.findUserById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                         @RequestBody Map<String, Object> request,
                                                         HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다"));
            }
            
            if (!id.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "권한이 없습니다"));
            }

            String department = (String) request.get("department");
            String jobPosition = (String) request.get("jobPosition");
            String nickname = (String) request.get("nickname");
            String company = (String) request.get("company");

            if (userMapper.existsByNickname(nickname)) {
                UserResponse currentUser = userMapper.findUserById(id);
                if (currentUser != null && !nickname.equals(currentUser.getNickname())) {
                    return ResponseEntity.status(400).body(Map.of("error", "이미 사용중인 닉네임입니다"));
                }
            }

            boolean updated = userMapper.updateUser(id, department, jobPosition, nickname, company);
            if (!updated) {
                return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            UserResponse updatedUser = userMapper.findUserById(id);
            return ResponseEntity.ok(Map.of("success", true, "user", updatedUser));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable Long id,
                                                             @RequestBody Map<String, Object> request,
                                                             HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다"));
            }
            
            if (!id.equals(userId)) {
                return ResponseEntity.status(403).body(Map.of("error", "권한이 없습니다"));
            }

            String newPassword = (String) request.get("newPassword");
            if (newPassword == null || newPassword.length() < 8) {
                return ResponseEntity.status(400).body(Map.of("error", "비밀번호는 8자 이상이어야 합니다"));
            }

            boolean updated = userMapper.updatePassword(id, newPassword);
            if (!updated) {
                return ResponseEntity.status(404).body(Map.of("error", "사용자를 찾을 수 없습니다"));
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 변경되었습니다"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long id) {
        try {
            UserResponse user = userMapper.findUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            int postCount = userMapper.getUserPostCount(id);
            int commentCount = userMapper.getUserCommentCount(id);
            int likeCount = userMapper.getUserLikeCount(id);
            int bookmarkCount = userMapper.getUserBookmarkCount(id);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("postCount", postCount);
            stats.put("commentCount", commentCount);
            stats.put("likeCount", likeCount);
            stats.put("bookmarkCount", bookmarkCount);
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = userMapper.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNicknameExists(@RequestParam String nickname) {
        boolean exists = userMapper.existsByNickname(nickname);
        return ResponseEntity.ok(exists);
    }
}