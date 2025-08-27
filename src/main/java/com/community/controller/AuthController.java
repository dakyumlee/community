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

            session.setAttribute("userId", user.getId());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("nickname", user.getNickname());
            session.setAttribute("isAdmin", isAdmin);

            System.out.println("세션에 저장된 userId: " + user.getId());
            System.out.println("세션 ID: " + session.getId());

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
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        System.out.println("세션 ID: " + session.getId());
        
        Long userId = (Long) session.getAttribute("userId");
        String email = (String) session.getAttribute("email");
        String nickname = (String) session.getAttribute("nickname");
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        
        System.out.println("세션의 userId: " + userId);
        System.out.println("세션의 email: " + email);
        System.out.println("세션의 nickname: " + nickname);
        System.out.println("세션의 isAdmin: " + isAdmin);
        
        if (userId == null) {
            System.out.println("아 너무짜증나 계속에러나ㅋㅋㅠ이거보면디버깅ㄱㄱ");
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다"));
        }

        Map<String, Object> userInfo = Map.of(
            "id", userId,
            "email", email != null ? email : "",
            "nickname", nickname != null ? nickname : "",
            "isAdmin", isAdmin != null ? isAdmin : false
        );

        System.out.println("반환할 사용자 정보: " + userInfo);
        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of(
            "message", "Auth controller is working!",
            "timestamp", String.valueOf(System.currentTimeMillis())
        ));
    }
}