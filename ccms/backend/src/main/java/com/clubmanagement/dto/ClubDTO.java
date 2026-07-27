package com.clubmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ClubDTO {

    public static class Request {
        @NotBlank(message = "Club name is required")
        private String name;
        private String description;
        private String category;
        private String imageUrl;

        public String getName()        { return name; }
        public String getDescription() { return description; }
        public String getCategory()    { return category; }
        public String getImageUrl()    { return imageUrl; }
        public void setName(String v)        { this.name = v; }
        public void setDescription(String v) { this.description = v; }
        public void setCategory(String v)    { this.category = v; }
        public void setImageUrl(String v)    { this.imageUrl = v; }
    }

    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String category;
        private String imageUrl;
        private boolean active;
        private LocalDateTime createdAt;
        private String createdByName;
        private long memberCount;

        public Long getId()                  { return id; }
        public String getName()              { return name; }
        public String getDescription()       { return description; }
        public String getCategory()          { return category; }
        public String getImageUrl()          { return imageUrl; }
        public boolean isActive()            { return active; }
        public LocalDateTime getCreatedAt()  { return createdAt; }
        public String getCreatedByName()     { return createdByName; }
        public long getMemberCount()         { return memberCount; }

        public void setId(Long v)                  { this.id = v; }
        public void setName(String v)              { this.name = v; }
        public void setDescription(String v)       { this.description = v; }
        public void setCategory(String v)          { this.category = v; }
        public void setImageUrl(String v)          { this.imageUrl = v; }
        public void setActive(boolean v)           { this.active = v; }
        public void setCreatedAt(LocalDateTime v)  { this.createdAt = v; }
        public void setCreatedByName(String v)     { this.createdByName = v; }
        public void setMemberCount(long v)         { this.memberCount = v; }
    }

    public static class MemberResponse {
        private Long membershipId;
        private Long userId;
        private String userName;
        private String userEmail;
        private String clubRole;
        private LocalDateTime joinedAt;

        public Long getMembershipId()        { return membershipId; }
        public Long getUserId()              { return userId; }
        public String getUserName()          { return userName; }
        public String getUserEmail()         { return userEmail; }
        public String getClubRole()          { return clubRole; }
        public LocalDateTime getJoinedAt()   { return joinedAt; }

        public void setMembershipId(Long v)      { this.membershipId = v; }
        public void setUserId(Long v)            { this.userId = v; }
        public void setUserName(String v)        { this.userName = v; }
        public void setUserEmail(String v)       { this.userEmail = v; }
        public void setClubRole(String v)        { this.clubRole = v; }
        public void setJoinedAt(LocalDateTime v) { this.joinedAt = v; }
    }
}
