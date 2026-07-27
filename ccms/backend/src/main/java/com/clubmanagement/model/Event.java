package com.clubmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    private String location;
    private String imageUrl;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Event() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Event e = new Event();
        public Builder title(String v)           { e.title = v; return this; }
        public Builder description(String v)     { e.description = v; return this; }
        public Builder eventDate(LocalDate v)    { e.eventDate = v; return this; }
        public Builder location(String v)        { e.location = v; return this; }
        public Builder imageUrl(String v)        { e.imageUrl = v; return this; }
        public Builder maxParticipants(Integer v){ e.maxParticipants = v; return this; }
        public Builder club(Club v)              { e.club = v; return this; }
        public Builder createdBy(User v)         { e.createdBy = v; return this; }
        public Builder active(boolean v)         { e.active = v; return this; }
        public Event build()                     { return e; }
    }

    public Long getId()                  { return id; }
    public String getTitle()             { return title; }
    public String getDescription()       { return description; }
    public LocalDate getEventDate()      { return eventDate; }
    public String getLocation()          { return location; }
    public String getImageUrl()          { return imageUrl; }
    public Integer getMaxParticipants()  { return maxParticipants; }
    public boolean isActive()            { return active; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public Club getClub()                { return club; }
    public User getCreatedBy()           { return createdBy; }

    public void setId(Long id)                      { this.id = id; }
    public void setTitle(String title)              { this.title = title; }
    public void setDescription(String d)            { this.description = d; }
    public void setEventDate(LocalDate eventDate)   { this.eventDate = eventDate; }
    public void setLocation(String location)        { this.location = location; }
    public void setImageUrl(String imageUrl)        { this.imageUrl = imageUrl; }
    public void setMaxParticipants(Integer m)       { this.maxParticipants = m; }
    public void setActive(boolean active)           { this.active = active; }
    public void setCreatedAt(LocalDateTime t)       { this.createdAt = t; }
    public void setClub(Club club)                  { this.club = club; }
    public void setCreatedBy(User createdBy)        { this.createdBy = createdBy; }
}
