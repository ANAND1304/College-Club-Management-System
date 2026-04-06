package com.clubmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventDTO {

    @Data
    public static class Request {
        @NotBlank(message = "Title is required")
        private String title;
        private String description;

        @NotNull(message = "Event date is required")
        private LocalDate eventDate;

        private String location;
        private String imageUrl;
        private Integer maxParticipants;

        @NotNull(message = "Club ID is required")
        private Long clubId;
    }

    @Data
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private LocalDate eventDate;
        private String location;
        private String imageUrl;
        private Integer maxParticipants;
        private boolean active;
        private LocalDateTime createdAt;
        private Long clubId;
        private String clubName;
        private String createdByName;
    }
}
