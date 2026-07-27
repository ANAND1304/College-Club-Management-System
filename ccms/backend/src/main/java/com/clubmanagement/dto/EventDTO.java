package com.clubmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EventDTO {

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

        public String getTitle()           { return title; }
        public String getDescription()     { return description; }
        public LocalDate getEventDate()    { return eventDate; }
        public String getLocation()        { return location; }
        public String getImageUrl()        { return imageUrl; }
        public Integer getMaxParticipants(){ return maxParticipants; }
        public Long getClubId()            { return clubId; }
        public void setTitle(String v)           { this.title = v; }
        public void setDescription(String v)     { this.description = v; }
        public void setEventDate(LocalDate v)    { this.eventDate = v; }
        public void setLocation(String v)        { this.location = v; }
        public void setImageUrl(String v)        { this.imageUrl = v; }
        public void setMaxParticipants(Integer v){ this.maxParticipants = v; }
        public void setClubId(Long v)            { this.clubId = v; }
    }

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

        public Long getId()                  { return id; }
        public String getTitle()             { return title; }
        public String getDescription()       { return description; }
        public LocalDate getEventDate()      { return eventDate; }
        public String getLocation()          { return location; }
        public String getImageUrl()          { return imageUrl; }
        public Integer getMaxParticipants()  { return maxParticipants; }
        public boolean isActive()            { return active; }
        public LocalDateTime getCreatedAt()  { return createdAt; }
        public Long getClubId()              { return clubId; }
        public String getClubName()          { return clubName; }
        public String getCreatedByName()     { return createdByName; }

        public void setId(Long v)                    { this.id = v; }
        public void setTitle(String v)               { this.title = v; }
        public void setDescription(String v)         { this.description = v; }
        public void setEventDate(LocalDate v)        { this.eventDate = v; }
        public void setLocation(String v)            { this.location = v; }
        public void setImageUrl(String v)            { this.imageUrl = v; }
        public void setMaxParticipants(Integer v)    { this.maxParticipants = v; }
        public void setActive(boolean v)             { this.active = v; }
        public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }
        public void setClubId(Long v)                { this.clubId = v; }
        public void setClubName(String v)            { this.clubName = v; }
        public void setCreatedByName(String v)       { this.createdByName = v; }
    }
}
