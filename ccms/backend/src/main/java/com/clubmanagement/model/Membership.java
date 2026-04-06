package com.clubmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "memberships",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "club_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private LocalDateTime requestedAt = LocalDateTime.now();

    public enum Status {
        PENDING, APPROVED, REJECTED
    }
}
