package com.community.hojun;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.community.dto.request.MessageRequest;
import com.community.dto.response.MessageResponse;
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
        
        System.out.println("여긴가 받은쪽지리스트 " + messageList);
      
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
        
        System.out.println("여긴가 받은쪽지리스트 " + messageList);
      
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
		System.out.println("여기는 상세보기" + messageDTO);
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
	public ModelAndView writeForm( Long sender_id, Long receiver_id ) {
		
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
	public ModelAndView sendMessage( MessageDTO messageDTO ) {
		
		ModelAndView mv = new ModelAndView(); 
		
		System.out.println("send메시지에에에에에에 : " + messageDTO);
		messagesMapper.sendMessage(messageDTO);
		
		mv.addObject("userId", messageDTO.getSender_id());
		mv.setViewName("redirect:/Message/List/Received");
		
		return mv;
		
	}
	
	@GetMapping("/Message/ReceiverDelete")
	public ModelAndView receiverDeleteMessage(Long id, HttpSession session) {
		
		Long userId = (Long) session.getAttribute("userId");
		
		ModelAndView mv = new ModelAndView();
		
		messagesMapper.receiverDeleteMessage(id);
		mv.addObject("id", id);
		mv.addObject("userId", userId);
		mv.addObject(mv);
		mv.setViewName("redirect:/Message/List/Received");
		
		return mv;
	}
	

	
	
}
