package com.clubmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active")
    private boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Membership> memberships = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Announcement> announcements = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Club() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Club club = new Club();
        public Builder name(String v)        { club.name = v; return this; }
        public Builder description(String v) { club.description = v; return this; }
        public Builder category(String v)    { club.category = v; return this; }
        public Builder imageUrl(String v)    { club.imageUrl = v; return this; }
        public Builder createdBy(User v)     { club.createdBy = v; return this; }
        public Builder active(boolean v)     { club.active = v; return this; }
        public Club build()                  { return club; }
    }

    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public String getDescription()       { return description; }
    public String getCategory()          { return category; }
    public String getImageUrl()          { return imageUrl; }
    public boolean isActive()            { return active; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public User getCreatedBy()           { return createdBy; }
    public List<Membership> getMemberships() { return memberships; }
    public List<Event> getEvents()       { return events; }

    public void setId(Long id)                  { this.id = id; }
    public void setName(String name)            { this.name = name; }
    public void setDescription(String d)        { this.description = d; }
    public void setCategory(String c)           { this.category = c; }
    public void setImageUrl(String u)           { this.imageUrl = u; }
    public void setActive(boolean active)       { this.active = active; }
    public void setCreatedAt(LocalDateTime t)   { this.createdAt = t; }
    public void setCreatedBy(User u)            { this.createdBy = u; }
}
