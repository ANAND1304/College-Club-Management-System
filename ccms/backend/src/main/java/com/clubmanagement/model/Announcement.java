package com.clubmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

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

    public Announcement() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Announcement a = new Announcement();
        public Builder title(String v)     { a.title = v; return this; }
        public Builder content(String v)   { a.content = v; return this; }
        public Builder club(Club v)        { a.club = v; return this; }
        public Builder createdBy(User v)   { a.createdBy = v; return this; }
        public Announcement build()        { return a; }
    }

    public Long getId()                 { return id; }
    public String getTitle()            { return title; }
    public String getContent()          { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Club getClub()               { return club; }
    public User getCreatedBy()          { return createdBy; }

    public void setId(Long id)                { this.id = id; }
    public void setTitle(String title)        { this.title = title; }
    public void setContent(String content)    { this.content = content; }
    public void setCreatedAt(LocalDateTime t) { this.createdAt = t; }
    public void setClub(Club club)            { this.club = club; }
    public void setCreatedBy(User u)          { this.createdBy = u; }
}
