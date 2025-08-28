package com.community.controller;

import com.community.dto.request.MessageRequest;
import com.community.dto.response.MessageResponse;
import com.community.mapper.CommunityMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private CommunityMapper messageMapper;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(
            @Valid @RequestBody MessageRequest request,
            HttpSession session) {

        try {
            Long senderId = (Long) session.getAttribute("userId");
            if (senderId == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다"));
            }

            System.out.println("쪽지 전송 요청 받음:");
            System.out.println("receiverId: " + request.getReceiverId());
            System.out.println("senderId: " + senderId);
            
            List<Long> existingUserIds = messageMapper.findAllUsers().stream()
                .map(user -> user.getId()).collect(java.util.stream.Collectors.toList());
            
            System.out.println("존재하는 사용자 IDs: " + existingUserIds);
            
            if (!existingUserIds.contains(request.getReceiverId())) {
                System.out.println("receiverId " + request.getReceiverId() + "가 존재하지 않음");
                return ResponseEntity.status(400).body(Map.of("status", "error", "message", "받는 사람이 존재하지 않습니다"));
            }
            
            System.out.println("DB에 쪽지 저장 시도...");
            messageMapper.createMessage(request, senderId);
            System.out.println("DB에 쪽지 저장 완료");

            return ResponseEntity.ok(Map.of("status", "success", "message", "쪽지가 성공적으로 전송되었습니다"));

        } catch (Exception e) {
            System.err.println("쪽지 전송 중 오류 발생:");
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", "쪽지 전송에 실패했습니다: " + e.getMessage()));
        }
    }

    @GetMapping("/received")
    public ResponseEntity<Map<String, Object>> getReceivedMessages(HttpSession session) {
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다"));
            }

            System.out.println("받은쪽지 조회 userId: " + userId);
            List<MessageResponse> messages = messageMapper.getReceivedMessages(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("messages", messages);
            response.put("count", messages.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("받은 쪽지 조회 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/sent")
    public ResponseEntity<Map<String, Object>> getSentMessages(HttpSession session) {
        
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다"));
            }

            System.out.println("보낸쪽지 조회 userId: " + userId);
            
            List<MessageResponse> messages = messageMapper.getSentMessages(userId);
            System.out.println("조회된 보낸쪽지 개수: " + messages.size());

            Map<String, Object> response = new HashMap<>();
            response.put("messages", messages);
            response.put("count", messages.size());
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("보낸 쪽지 조회 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessageDetail(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }

            MessageResponse message = messageMapper.getMessageById(id);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }

            if (!userId.equals(message.getSenderId()) && !userId.equals(message.getReceiverId())) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.err.println("쪽지 상세 조회 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다"));
            }

            messageMapper.markAsRead(id);

            return ResponseEntity.ok(Map.of("status", "success", "message", "쪽지를 읽음으로 표시했습니다"));
        } catch (Exception e) {
            System.err.println("쪽지 읽음 처리 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/received/{id}")
    public ResponseEntity<Map<String, String>> deleteReceivedMessage(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다"));
            }

            messageMapper.deleteByReceiver(id);

            return ResponseEntity.ok(Map.of("status", "success", "message", "받은 쪽지가 삭제되었습니다"));
        } catch (Exception e) {
            System.err.println("받은 쪽지 삭제 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/sent/{id}")
    public ResponseEntity<Map<String, String>> deleteSentMessage(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "로그인이 필요합니다"));
            }

            messageMapper.deleteBySender(id);

            return ResponseEntity.ok(Map.of("status", "success", "message", "보낸 쪽지가 삭제되었습니다"));
        } catch (Exception e) {
            System.err.println("보낸 쪽지 삭제 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}