package com.clubmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memberships",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "club_id"}))
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    @Column(name = "club_role")
    private String clubRole = "MEMBER";

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @PrePersist
    protected void onCreate() {
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        if (status == null) status = Status.PENDING;
        if (clubRole == null) clubRole = "MEMBER";
    }

    public Membership() {}

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Membership m = new Membership();
        public Builder user(User v)      { m.user = v; return this; }
        public Builder club(Club v)      { m.club = v; return this; }
        public Builder status(Status v)  { m.status = v; return this; }
        public Builder clubRole(String v){ m.clubRole = v; return this; }
        public Membership build()        { return m; }
    }

    public Long getId()                  { return id; }
    public User getUser()                { return user; }
    public Club getClub()                { return club; }
    public Status getStatus()            { return status; }
    public String getClubRole()          { return clubRole; }
    public LocalDateTime getJoinedAt()   { return joinedAt; }
    public LocalDateTime getRequestedAt(){ return requestedAt; }

    public void setId(Long id)                 { this.id = id; }
    public void setUser(User user)             { this.user = user; }
    public void setClub(Club club)             { this.club = club; }
    public void setStatus(Status status)       { this.status = status; }
    public void setClubRole(String clubRole)   { this.clubRole = clubRole; }
    public void setJoinedAt(LocalDateTime t)   { this.joinedAt = t; }
    public void setRequestedAt(LocalDateTime t){ this.requestedAt = t; }

    public enum Status { PENDING, APPROVED, REJECTED }
}
