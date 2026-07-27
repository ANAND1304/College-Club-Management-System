package com.clubmanagement.service;

import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository       userRepository;
    private final ClubRepository       clubRepository;
    private final EventRepository      eventRepository;
    private final MembershipRepository membershipRepository;

    public AdminService(UserRepository userRepository,
                        ClubRepository clubRepository,
                        EventRepository eventRepository,
                        MembershipRepository membershipRepository) {
        this.userRepository       = userRepository;
        this.clubRepository       = clubRepository;
        this.eventRepository      = eventRepository;
        this.membershipRepository = membershipRepository;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",      userRepository.count());
        stats.put("totalClubs",      clubRepository.countByActiveTrue());
        stats.put("totalEvents",     eventRepository.countByActiveTrue());
        stats.put("pendingRequests", membershipRepository.countByStatus(Membership.Status.PENDING));
        stats.put("approvedMembers", membershipRepository.countByStatus(Membership.Status.APPROVED));
        stats.put("adminCount",      userRepository.countByRole(User.Role.ADMIN));
        return stats;
    }

    public List<UserSummary> getAllUsers() {
        return userRepository.findAll().stream().map(u -> {
            UserSummary s = new UserSummary();
            s.setId(u.getId());
            s.setName(u.getName());
            s.setEmail(u.getEmail());
            s.setRole(u.getRole().name());
            s.setDepartment(u.getDepartment());
            s.setActive(u.isActive());
            s.setCreatedAt(u.getCreatedAt());
            return s;
        }).collect(Collectors.toList());
    }

    public List<MembershipSummary> getPendingMemberships() {
        return membershipRepository.findByStatus(Membership.Status.PENDING)
            .stream().map(m -> {
                MembershipSummary s = new MembershipSummary();
                s.setMembershipId(m.getId());
                s.setUserId(m.getUser().getId());
                s.setUserName(m.getUser().getName());
                s.setUserEmail(m.getUser().getEmail());
                s.setClubId(m.getClub().getId());
                s.setClubName(m.getClub().getName());
                s.setStatus(m.getStatus().name());
                s.setRequestedAt(m.getRequestedAt());
                return s;
            }).collect(Collectors.toList());
    }

    @Transactional
    public void approveMembership(Long id) {
        Membership m = membershipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        m.setStatus(Membership.Status.APPROVED);
        m.setJoinedAt(LocalDateTime.now());
        membershipRepository.save(m);
    }

    @Transactional
    public void rejectMembership(Long id) {
        Membership m = membershipRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        m.setStatus(Membership.Status.REJECTED);
        membershipRepository.save(m);
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Transactional
    public void promoteUser(Long userId, String role) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(User.Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
    }

    // ── Inner DTOs ────────────────────────────────────────────────────────────

    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String department;
        private boolean active;
        private LocalDateTime createdAt;

        public Long getId()                 { return id; }
        public String getName()             { return name; }
        public String getEmail()            { return email; }
        public String getRole()             { return role; }
        public String getDepartment()       { return department; }
        public boolean isActive()           { return active; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public void setId(Long v)                 { this.id = v; }
        public void setName(String v)             { this.name = v; }
        public void setEmail(String v)            { this.email = v; }
        public void setRole(String v)             { this.role = v; }
        public void setDepartment(String v)       { this.department = v; }
        public void setActive(boolean v)          { this.active = v; }
        public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    }

    public static class MembershipSummary {
        private Long membershipId;
        private Long userId;
        private String userName;
        private String userEmail;
        private Long clubId;
        private String clubName;
        private String status;
        private LocalDateTime requestedAt;

        public Long getMembershipId()           { return membershipId; }
        public Long getUserId()                 { return userId; }
        public String getUserName()             { return userName; }
        public String getUserEmail()            { return userEmail; }
        public Long getClubId()                 { return clubId; }
        public String getClubName()             { return clubName; }
        public String getStatus()               { return status; }
        public LocalDateTime getRequestedAt()   { return requestedAt; }

        public void setMembershipId(Long v)         { this.membershipId = v; }
        public void setUserId(Long v)               { this.userId = v; }
        public void setUserName(String v)           { this.userName = v; }
        public void setUserEmail(String v)          { this.userEmail = v; }
        public void setClubId(Long v)               { this.clubId = v; }
        public void setClubName(String v)           { this.clubName = v; }
        public void setStatus(String v)             { this.status = v; }
        public void setRequestedAt(LocalDateTime v) { this.requestedAt = v; }
    }
}
