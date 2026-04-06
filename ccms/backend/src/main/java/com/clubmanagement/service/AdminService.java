package com.clubmanagement.service;

import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final EventRepository eventRepository;
    private final MembershipRepository membershipRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalClubs", clubRepository.countByActiveTrue());
        stats.put("totalEvents", eventRepository.countByActiveTrue());
        stats.put("pendingRequests", membershipRepository.countByStatus(Membership.Status.PENDING));
        stats.put("approvedMembers", membershipRepository.countByStatus(Membership.Status.APPROVED));
        stats.put("adminCount", userRepository.countByRole(User.Role.ADMIN));
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

    @Data
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String department;
        private boolean active;
        private LocalDateTime createdAt;
    }

    @Data
    public static class MembershipSummary {
        private Long membershipId;
        private Long userId;
        private String userName;
        private String userEmail;
        private Long clubId;
        private String clubName;
        private String status;
        private LocalDateTime requestedAt;
    }
}
