package com.community.suin;

import com.community.suin.CommentRequest;
import com.community.suin.CommentResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    List<CommentResponse> findCommentsByPostId(@Param("postId") Long postId);
    
    Long insertComment(@Param("content") String content, 
                      @Param("postId") Long postId, 
                      @Param("userId") Long userId, 
                      @Param("parentId") Long parentId);
    
    CommentResponse findCommentById(@Param("id") Long id);
    
    boolean isCommentAuthor(@Param("id") Long id, @Param("userId") Long userId);
    
    boolean updateComment(@Param("id") Long id, @Param("content") String content);
    
    boolean deleteComment(@Param("id") Long id);
    
    boolean isAdmin(@Param("userId") Long userId);
}