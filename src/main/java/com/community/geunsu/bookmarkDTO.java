package com.community.geunsu;

import java.time.LocalDateTime;

// field
public class bookmarkDTO {
        private long id;
		private long user_id;
        private LocalDateTime created_at;
        
        // getter/setter
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
		public long getUser_id() {
			return user_id;
		}
		public void setUser_id(long user_id) {
			this.user_id = user_id;
		}
		public LocalDateTime getCreated_at() {
			return created_at;
		}
		public void setCreated_at(LocalDateTime created_at) {
			this.created_at = created_at;
		} 
		
		// constructor
		public bookmarkDTO() {
		
		}
		public bookmarkDTO(long id, long user_id, LocalDateTime created_at) {
			super();
			this.id = id;
			this.user_id = user_id;
			this.created_at = created_at;
		}
		
		// tostring
		@Override
		public String toString() {
			return "bookmarkDTO [id=" + id + ", user_id=" + user_id + ", created_at=" + created_at + "]";
		}
		
		
		
		
}
