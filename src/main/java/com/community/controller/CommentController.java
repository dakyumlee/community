package com.community.controller;

import com.community.dto.request.CommentRequest;
import com.community.dto.response.CommentResponse;
import com.community.mapper.CommunityMapper;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/Comment")
public class CommentController{
	private final CommunityMapper commentMapper;
	
	public CommentController(CommunityMapper commentMapper) {
		this.commentMapper = commentMapper;
	}

//댓글목록
@RequestMapping("/List")
public ModelAndView list(@RequestParam("postId")Long postId, Authentication authentication) {
	List<CommentResponse> commentList = commentMapper.findCommentsByPostId(postId);
	
	if(authentication !=null) {
		String email  = authentication.getName();
		Long userId   = commentMapper.findIdByEmail(email);
		
		for(CommentResponse comment : commentList) {
			comment.setAuthor(comment.getAuthorId().equals(userId));
		}
	}
	
	ModelAndView mv = new ModelAndView();
	mv.addObject("commentList", commentList);
	mv.addObject("postId", postId);
	mv.setViewName("comment/list");
	return mv;
  }

//댓글작성폼
@RequestMapping("/WriteForm")
public ModelAndView writeFrom(@RequestParam("postId")Long postId,
		@RequestParam(value="parentId", required = false)Long parentId) {
	ModelAndView mv = new ModelAndView();
	mv.addObject("postId", postId);
	mv.addObject("parentId",parentId);
	mv.setViewName("comment/write");
	return mv;
  }

//댓글작성처리
@RequestMapping("/Write")
public ModelAndView write(CommentRequest request, Authentication authentication) {
	if (authentication == null) {
		return new ModelAndView("redirect;/login"); //로그인이 안 됐을 경우 로그인 페이지로 이동(삭제해도 될 듯)
	}
	
	String email = authentication.getName();
	Long userId  = commentMapper.findIdByEmail(email);
	
	if(request.getContent() != null && !request.getContent().trim().isEmpty()) {
		commentMapper.insertComment(
				request.getContent().trim(),
				request.getPostId(),
				userId,
				request.getParentId()
				);
	}
	return new ModelAndView("redirect:/Comment/List?postId=" + request.getPostId());
}

//댓글수정
@RequestMapping("/EditForm")
public ModelAndView editForm(@RequestParam("id")Long id,
		@RequestParam("postId")Long postId,
		Authentication authentication) {
	
	CommentResponse comment = commentMapper.findCommentById(id);
	
	String email = authentication.getName();
	Long userId  = commentMapper.findIdByEmail(email);
	
	if (!commentMapper.isCommentAuthor(id, userId) && !commentMapper.isAdmin(userId)) {
        return new ModelAndView("redirect:/Comment/List?postId=" + postId); //권한이 없으면 목록으로(삭제해도 될 듯)
    }
	
	ModelAndView mv = new ModelAndView();
	mv.addObject("comment", comment);
	mv.addObject("postId", postId);
	mv.setViewName("comment/edit");
	return mv;
  }

//댓글수정처리
@RequestMapping("/Edit")
public ModelAndView edit(@RequestParam("id")Long id,
		@RequestParam("content")String content,
		@RequestParam("postId")Long postId,
		Authentication authentication) {
	
	String email = authentication.getName();
	Long userId = commentMapper.findIdByEmail(email);
	
	if(!commentMapper.isCommentAuthor(id, userId) && !commentMapper.isAdmin(userId)) {
       return new ModelAndView("redirect:/Comment/List?postId=" + postId);
     }
	 if(content != null && !content.trim().isEmpty()) {
        commentMapper.updateComment(id, content.trim());
	 }
	 return new ModelAndView("redirect:/Comment/List?postId="+postId);
  }

//댓글 삭제
@RequestMapping("/Delete")
public ModelAndView delete(@RequestParam("id") Long id,
		@RequestParam("postId") Long postId,
        Authentication authentication) {
	
	String email = authentication.getName();
	Long userId = commentMapper.findIdByEmail(email);
	

    if(!commentMapper.isCommentAuthor(id, userId) && !commentMapper.isAdmin(userId)) {
       return new ModelAndView("redirect:/Comment/List?postId=" + postId);
    }
    
    commentMapper.deleteComment(id);
    
    return new ModelAndView("redirect:/Comment/List?postId=" + postId);
  }
}



