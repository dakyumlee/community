package com.community.controller;

import com.community.dto.request.LoginRequest;
import com.community.dto.request.RegisterRequest;
import com.community.dto.response.LoginResponse;
import com.community.dto.response.UserResponse;
import com.community.mapper.CommunityMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final CommunityMapper userMapper;

    public AuthController(CommunityMapper userMapper) {
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("이메일: " + request.getEmail());

            if (userMapper.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("message", "이미 존재하는 이메일입니다"));
            }

            String nickname = generateNickname(request.getCompany());
            
            boolean success = userMapper.insertUser(
                request.getEmail(),
                request.getPassword(),
                request.getDepartment(),
                request.getJobPosition(),
                nickname,
                request.getCompany()
            );

            if (success) {
                System.out.println("회원가입 성공!");
                return ResponseEntity.ok(Map.of(
                    "message", "회원가입이 완료되었습니다",
                    "nickname", nickname
                ));
            } else {
                return ResponseEntity.status(500)
                    .body(Map.of("message", "회원가입에 실패했습니다"));
            }

        } catch (Exception e) {
            System.out.println("회원가입 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }

    private String generateNickname(String company) {
        String baseNickname = (company != null && !company.trim().isEmpty()) 
            ? company.trim() : "사용자";
        
        String nickname;
        int attempt = 0;
        
        do {
            int randomNum = (int) (Math.random() * 999) + 1;
            nickname = baseNickname + "-" + String.format("%03d", randomNum);
            attempt++;
        } while (userMapper.existsByNickname(nickname) && attempt < 10);
        
        if (attempt >= 10) {
            long timestamp = System.currentTimeMillis() % 10000;
            nickname = baseNickname + "-" + timestamp;
        }
        
        return nickname;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpSession session) {
        try {
            System.out.println("=== 로그인 시도 ===");
            System.out.println("이메일: " + request.getEmail());
            System.out.println("기존 세션 ID: " + session.getId());

            UserResponse user = userMapper.findUserByEmail(request.getEmail());
            if (user == null) {
                System.out.println("사용자를 찾을 수 없음");
                return ResponseEntity.status(401)
                    .body(Map.of("message", "이메일 또는 비밀번호가 올바르지 않습니다"));
            }

            String storedPassword = userMapper.findPasswordByEmail(request.getEmail());
            System.out.println("DB 저장된 비밀번호: " + storedPassword);
            System.out.println("입력된 비밀번호: " + request.getPassword());

            if (storedPassword == null || !request.getPassword().equals(storedPassword)) {
                System.out.println("비밀번호 불일치");
                return ResponseEntity.status(401)
                    .body(Map.of("message", "이메일 또는 비밀번호가 올바르지 않습니다"));
            }

            System.out.println("로그인 성공!");

            boolean isAdmin = "ADMIN".equals(user.getRole());

            // 세션에 모든 필요한 정보 저장
            session.setAttribute("userId", user.getId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("nickname", user.getNickname());
            session.setAttribute("userRole", user.getRole()); // 누락되었던 부분 추가
            session.setAttribute("isAdmin", isAdmin);
            
            // 세션 타임아웃 설정 (30분)
            session.setMaxInactiveInterval(30 * 60);

            System.out.println("=== 세션 정보 저장 완료 ===");
            System.out.println("세션 ID: " + session.getId());
            System.out.println("저장된 userId: " + user.getId());
            System.out.println("저장된 email: " + user.getEmail());
            System.out.println("저장된 nickname: " + user.getNickname());
            System.out.println("저장된 userRole: " + user.getRole());
            System.out.println("저장된 isAdmin: " + isAdmin);

            LoginResponse response = new LoginResponse();
            response.setToken("session-" + session.getId());
            response.setId(user.getId());
            response.setEmail(user.getEmail());
            response.setNickname(user.getNickname());
            response.setIsAdmin(isAdmin);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("로그인 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("message", "서버 오류가 발생했습니다"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        try {
            System.out.println("=== 로그아웃 처리 ===");
            System.out.println("로그아웃할 세션 ID: " + session.getId());
            session.invalidate();
            System.out.println("세션 무효화 완료");
            return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다"));
        } catch (Exception e) {
            System.out.println("로그아웃 에러: " + e.getMessage());
            return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        try {
            System.out.println("=== 현재 사용자 정보 조회 ===");
            System.out.println("세션 ID: " + session.getId());
            System.out.println("세션 생성 시간: " + new java.util.Date(session.getCreationTime()));
            System.out.println("세션 마지막 접근 시간: " + new java.util.Date(session.getLastAccessedTime()));
            
            Long userId = (Long) session.getAttribute("userId");
            String email = (String) session.getAttribute("email");
            String nickname = (String) session.getAttribute("nickname");
            String userRole = (String) session.getAttribute("userRole");
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            
            System.out.println("세션의 userId: " + userId);
            System.out.println("세션의 email: " + email);
            System.out.println("세션의 nickname: " + nickname);
            System.out.println("세션의 userRole: " + userRole);
            System.out.println("세션의 isAdmin: " + isAdmin);
            
            if (userId == null) {
                System.out.println("세션에 userId가 없음 - 로그인 필요");
                return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다"));
            }

            Map<String, Object> userInfo = Map.of(
                "id", userId,
                "email", email != null ? email : "",
                "nickname", nickname != null ? nickname : "",
                "role", userRole != null ? userRole : "USER",
                "isAdmin", isAdmin != null ? isAdmin : false,
                "sessionId", session.getId()
            );

            System.out.println("반환할 사용자 정보: " + userInfo);
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            System.out.println("사용자 정보 조회 에러: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(401).body(Map.of("error", "세션 오류가 발생했습니다"));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test(HttpSession session) {
        return ResponseEntity.ok(Map.of(
            "message", "Auth controller is working!",
            "timestamp", String.valueOf(System.currentTimeMillis()),
            "sessionId", session.getId()
        ));
    }
}