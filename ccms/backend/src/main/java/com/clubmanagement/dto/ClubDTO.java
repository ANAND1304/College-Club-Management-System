package com.clubmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

public class ClubDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Club name is required")
        private String name;
        private String description;
        private String category;
        private String imageUrl;
    }

    @Data
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
    }

    @Data
    public static class MemberResponse {
        private Long membershipId;
        private Long userId;
        private String userName;
        private String userEmail;
        private String clubRole;
        private LocalDateTime joinedAt;
    }
}
