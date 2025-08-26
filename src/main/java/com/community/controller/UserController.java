package com.community.controller;

import com.community.dto.response.UserResponse;
import com.community.mapper.CommunityMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CommunityMapper userMapper;

    public UserController(CommunityMapper userMapper) {
        this.userMapper = userMapper;
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
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                  @RequestParam String department,
                                                  @RequestParam String jobPosition,
                                                  @RequestParam String nickname,
                                                  @RequestParam String company,
                                                  @RequestParam Long userId) {
        if (!id.equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        if (userMapper.existsByNickname(nickname)) {
            UserResponse currentUser = userMapper.findUserById(id);
            if (currentUser != null && !nickname.equals(currentUser.getNickname())) {
                return ResponseEntity.badRequest().build();
            }
        }

        boolean updated = userMapper.updateUser(id, department, jobPosition, nickname, company);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }

        UserResponse updatedUser = userMapper.findUserById(id);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id,
                                              @RequestParam String newPassword,
                                              @RequestParam Long userId) {
        if (!id.equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        if (newPassword == null || newPassword.length() < 8) {
            return ResponseEntity.badRequest().build();
        }

        boolean updated = userMapper.updatePassword(id, newPassword);
        if (!updated) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
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