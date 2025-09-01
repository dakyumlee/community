package com.community.hojun;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.community.dto.request.MessageRequest;
import com.community.dto.response.MessageResponse;

@Mapper
public interface MessagesMapper {

	List<MessageResponse> getReceiveMessageList(Long userId);

	MessageDTO getMessage(Long Id);

	void DeleteMessage(Long id);

	void sendMessage(MessageDTO messageDTO);

	void updateIsRead(Long id);

	List<MessageResponse> getSentMessageList(Long userId);


}





