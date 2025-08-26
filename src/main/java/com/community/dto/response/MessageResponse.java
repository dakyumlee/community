package com.community.dto.response;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private String title;
    private String content;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private Integer isRead;
    private Integer deletedBySender;
    private Integer deletedByReceiver;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public MessageResponse() {}

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getContent() { 
        return content; 
    }
    
    public void setContent(String content) { 
        this.content = content; 
    }

    public Long getSenderId() { 
        return senderId; 
    }
    
    public void setSenderId(Long senderId) { 
        this.senderId = senderId; 
    }

    public String getSenderName() { 
        return senderName; 
    }
    
    public void setSenderName(String senderName) { 
        this.senderName = senderName; 
    }

    public Long getReceiverId() { 
        return receiverId; 
    }
    
    public void setReceiverId(Long receiverId) { 
        this.receiverId = receiverId; 
    }

    public String getReceiverName() { 
        return receiverName; 
    }
    
    public void setReceiverName(String receiverName) { 
        this.receiverName = receiverName; 
    }

    public Integer getIsRead() { 
        return isRead; 
    }
    
    public void setIsRead(Integer isRead) { 
        this.isRead = isRead; 
    }

    public Integer getDeletedBySender() { 
        return deletedBySender; 
    }
    
    public void setDeletedBySender(Integer deletedBySender) { 
        this.deletedBySender = deletedBySender; 
    }

    public Integer getDeletedByReceiver() { 
        return deletedByReceiver; 
    }
    
    public void setDeletedByReceiver(Integer deletedByReceiver) { 
        this.deletedByReceiver = deletedByReceiver; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getReadAt() { 
        return readAt; 
    }
    
    public void setReadAt(LocalDateTime readAt) { 
        this.readAt = readAt; 
    }
}