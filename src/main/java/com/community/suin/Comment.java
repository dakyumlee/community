package com.community.suin;
//request/response 안 나누고 comment.java에 통합함

import java.time.LocalDateTime;
//댓글 작성,수정한 날짜,시간

public class Comment {
	
	private String ID;
	private String AUTHOR_ID;
	private String CONTENT;
	private String CREATED_AT;
	private String POST_ID;
	private String UPDATE_AT;
	private String PARENT_ID;
	//1.클라이언트가 서버로 댓글을 생성하거나 수정할 때 전달하는 데이터
	//private String content;
	//본문내용
	//private Long postId;
	//댓글의 ID
	//private Long parentId;
	//부모댓글 ID(답글일 경우)
	
	// toString
	@Override
	public String toString() {
		return "Comment [ID=" + ID + ", AUTHOR_ID=" + AUTHOR_ID + ", CONTENT=" + CONTENT + ", CREATED_AT=" + CREATED_AT
				+ ", POST_ID=" + POST_ID + ", UPDATE_AT=" + UPDATE_AT + ", PARENT_ID=" + PARENT_ID + "]";
	}
	
	// 생성자
	public Comment() {}	
	public Comment(String ID, String AUTHOR_ID, String CONTENT, String CREATED_AT, String POST_ID, String UPDATE_AT,
			String PARENT_ID) {
		super();
		ID = ID;
		AUTHOR_ID = AUTHOR_ID;
		CONTENT = CONTENT;
		CREATED_AT = CREATED_AT;
		POST_ID = POST_ID;
		UPDATE_AT = UPDATE_AT;
		PARENT_ID = PARENT_ID;
	}
	
	// 게터 세터
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	public String getAUTHOR_ID() {
		return AUTHOR_ID;
	}
	public void setAUTHOR_ID(String aUTHOR_ID) {
		AUTHOR_ID = aUTHOR_ID;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	public String getCREATED_AT() {
		return CREATED_AT;
	}
	public void setCREATED_AT(String cREATED_AT) {
		CREATED_AT = cREATED_AT;
	}
	public String getPOST_ID() {
		return POST_ID;
	}
	public void setPOST_ID(String pOST_ID) {
		POST_ID = pOST_ID;
	}
	public String getUPDATE_AT() {
		return UPDATE_AT;
	}
	public void setUPDATE_AT(String uPDATE_AT) {
		UPDATE_AT = uPDATE_AT;
	}
	public String getPARENT_ID() {
		return PARENT_ID;
	}
	public void setPARENT_ID(String pARENT_ID) {
		PARENT_ID = pARENT_ID;
	}
	
	//2.서버가 클라이언트에게 댓글 정보를 응답할 때 사용하는 데이터
	//private String content; <1.에서 써서 주석처리
	//private String content; <1.에서 써서 주석처리
	//private Long postId; <1.에서 써서 주석처리
	//private Long Id;
	//댓글 ID
	//private Long authorId;
	//작성자 ID
	//private String authorNickname;
	//작성자 닉네임
	//private LocalDateTime createdAt;
	//댓글 처음 작성 시간
	//private LocalDateTime updatedAt;
	//댓글이 수정된 시간
	//private boolean isAuthor;
	//사용자가 댓글의 작성자인지 여부
	
	
	
}