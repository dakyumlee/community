package com.community.controller;

import com.community.dto.request.MessageRequest;
import com.community.dto.response.MessageResponse;
import com.community.mapper.CommunityMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private CommunityMapper messageMapper;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(
            @Valid @RequestBody MessageRequest request,
            @RequestParam(defaultValue = "13") Long senderId) {

        System.out.println("쪽지 전송 요청 받음:");
        System.out.println("receiverId: " + request.getReceiverId());
        System.out.println("senderId: " + senderId);
        
        try {
            List<Long> existingUserIds = messageMapper.findAllUsers().stream()
                .map(user -> user.getId()).collect(java.util.stream.Collectors.toList());
            
            System.out.println("존재하는 사용자 IDs: " + existingUserIds);
            
            if (!existingUserIds.contains(senderId)) {
                senderId = existingUserIds.get(0);
                System.out.println("senderId를 " + senderId + "로 변경");
            }
            
            if (!existingUserIds.contains(request.getReceiverId())) {
                System.out.println("receiverId " + request.getReceiverId() + "가 존재하지 않음");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "받는 사람이 존재하지 않습니다");
                return ResponseEntity.status(400).body(errorResponse);
            }
            
            System.out.println("DB에 쪽지 저장 시도...");
            messageMapper.createMessage(request, senderId);
            System.out.println("DB에 쪽지 저장 완료");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "쪽지가 성공적으로 전송되었습니다");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("쪽지 전송 중 오류 발생:");
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "쪽지 전송에 실패했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/received")
    public ResponseEntity<Map<String, Object>> getReceivedMessages(
            @RequestParam(defaultValue = "13") Long userId) {
        
        try {
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
    public ResponseEntity<Map<String, Object>> getSentMessages(
            @RequestParam(defaultValue = "13") Long userId) {
        
        try {
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
    public ResponseEntity<MessageResponse> getMessageDetail(@PathVariable Long id) {
        try {
            MessageResponse message = messageMapper.getMessageById(id);
            if (message == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            System.err.println("쪽지 상세 조회 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable Long id) {
        try {
            messageMapper.markAsRead(id);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "쪽지를 읽음으로 표시했습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("쪽지 읽음 처리 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/received/{id}")
    public ResponseEntity<Map<String, String>> deleteReceivedMessage(@PathVariable Long id) {
        try {
            messageMapper.deleteByReceiver(id);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "받은 쪽지가 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("받은 쪽지 삭제 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/sent/{id}")
    public ResponseEntity<Map<String, String>> deleteSentMessage(@PathVariable Long id) {
        try {
            messageMapper.deleteBySender(id);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "보낸 쪽지가 삭제되었습니다");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("보낸 쪽지 삭제 중 오류:");
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}