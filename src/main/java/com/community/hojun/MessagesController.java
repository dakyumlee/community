package com.community.hojun;

import java.util.List;

// import org.apache.ibatis.annotations.Param; // 사용안해서 제거함
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

// import com.community.dto.request.MessageRequest; // dto 패키지에서 가져오던거 삭제
// import com.community.dto.response.MessageResponse; // 이것도 삭제
import com.community.hojun.MessageRequest;
import com.community.hojun.MessageResponse;
import com.community.dto.response.UserResponse;
import com.community.hojun.MessagesMapper;
import com.community.mapper.CommunityMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class MessagesController {

    @Autowired
    MessagesMapper messagesMapper;
    
    @Autowired
    CommunityMapper mapper;
    
    // 받은쪽지함 리스트
    @GetMapping("/Message/List/Received")
    public ModelAndView getReceiveMessagesList(@RequestParam("userId") Long userId) {
        
        System.out.println("userId : " + userId);
        ModelAndView mv = new ModelAndView();        
    
        List<MessageResponse> messageList = messagesMapper.getReceiveMessageList(userId);
        
        String pageTitle = "받은 쪽지함";
        
        System.out.println("받은쪽지리스트 " + messageList);
      
        mv.addObject("pageTitle", pageTitle);
        mv.addObject("messageList", messageList); 
        mv.addObject("userId", userId);
        mv.setViewName("messageList");
        return mv;
    }
    
    // 보낸쪽지함 리스트
    @GetMapping("/Message/List/Sent")
    public ModelAndView getSentMessagesList(@RequestParam("userId") Long userId) {
        
        System.out.println("userId : " + userId);
        ModelAndView mv = new ModelAndView();        
        
        List<MessageResponse> messageList = messagesMapper.getSentMessageList(userId);
        
        String pageTitle = "보낸 쪽지함";
        
        System.out.println("보낸쪽지리스트 " + messageList);
      
        mv.addObject("pageTitle", pageTitle);
        mv.addObject("messageList", messageList); 
        mv.addObject("userId", userId);
        mv.setViewName("messageList");
        return mv;
    }
    
    
    @GetMapping("/Message/View")
    public ModelAndView getMessage(@RequestParam("id") Long id) {
        
        ModelAndView mv = new ModelAndView();
        
        messagesMapper.updateIsRead(id);
        
        MessageDTO messageDTO = messagesMapper.getMessage(id);
        System.out.println("쪽지 상세보기: " + messageDTO);
        UserResponse senderDTO = mapper.findUserById(messageDTO.getSender_id());
        UserResponse receiverDTO = mapper.findUserById(messageDTO.getReceiver_id());
        
        mv.addObject("messageDTO", messageDTO);
        mv.addObject("senderDTO", senderDTO);
        mv.addObject("receiverDTO", receiverDTO);
        mv.addObject("id", id);
        mv.setViewName("messageDetail");
        
        return mv;
        
    }
    
    @GetMapping("/Message/WriteForm")
    // public ModelAndView writeForm( Long sender_id, Long receiver_id ) { // 원래 코드인데 파라미터 바인딩이 애매해서 수정
    public ModelAndView writeForm(@RequestParam("sender_id") Long sender_id, 
                                  @RequestParam(value = "receiver_id", required = false) Long receiver_id) {
        
        UserResponse senderDTO = mapper.findUserById(sender_id); 
        
        UserResponse receiverDTO = null;
        if(receiver_id != null) {
            receiverDTO = mapper.findUserById(receiver_id);
        }
        
        ModelAndView mv = new ModelAndView();
        mv.addObject("senderDTO", senderDTO);
        mv.addObject("receiverDTO", receiverDTO);
        mv.addObject("sender_id", sender_id);
        mv.addObject("receiver_id", receiver_id);
        mv.setViewName("messageWrite");
        
        return mv;
        
    }

    
    @PostMapping("/Message/Send")
    // public ModelAndView sendMessage( MessageDTO messageDTO ) { // 원본 코드는 이거였는데 그냥 놔뒀음
    public ModelAndView sendMessage(MessageDTO messageDTO) {
        
        ModelAndView mv = new ModelAndView(); 
        
        System.out.println("쪽지 전송 데이터: " + messageDTO);
        messagesMapper.sendMessage(messageDTO);
        
        mv.addObject("userId", messageDTO.getSender_id());
        mv.setViewName("redirect:/Message/List/Received");
        
        return mv;
        
    }
    
    @GetMapping("/Message/ReceiverDelete")
    public ModelAndView receiverDeleteMessage(@RequestParam("id") Long id, HttpSession session) {
        
        Long userId = (Long) session.getAttribute("userId");
        
        ModelAndView mv = new ModelAndView();
        
        messagesMapper.receiverDeleteMessage(id);
        mv.addObject("id", id);
        mv.addObject("userId", userId);
        // mv.addObject(mv); // 이상한 코드라 삭제함
        mv.setViewName("redirect:/Message/List/Received");
        
        return mv;
    }
    
}