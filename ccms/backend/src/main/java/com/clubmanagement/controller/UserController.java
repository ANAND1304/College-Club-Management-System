package com.clubmanagement.controller;

import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.Membership;
import com.clubmanagement.model.User;
import com.clubmanagement.repository.MembershipRepository;
import com.clubmanagement.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository       userRepository;
    private final MembershipRepository membershipRepository;

    /** GET /api/users/me — current user profile */
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toProfileDTO(user));
    }

    /** GET /api/users/me/clubs — clubs the current user belongs to */
    @GetMapping("/me/clubs")
    public ResponseEntity<List<MembershipSummary>> getMyClubs(@AuthenticationPrincipal User user) {
        List<Membership> memberships = membershipRepository.findByUserId(user.getId());
        List<MembershipSummary> result = memberships.stream().map(m -> {
            MembershipSummary s = new MembershipSummary();
            s.setMembershipId(m.getId());
            s.setClubId(m.getClub().getId());
            s.setClubName(m.getClub().getName());
            s.setClubCategory(m.getClub().getCategory());
            s.setClubRole(m.getClubRole());
            s.setStatus(m.getStatus().name());
            s.setJoinedAt(m.getJoinedAt());
            return s;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /** PUT /api/users/me — update own profile */
    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        User managed = userRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (body.containsKey("name")       && !body.get("name").isBlank())       managed.setName(body.get("name"));
        if (body.containsKey("department"))  managed.setDepartment(body.get("department"));
        if (body.containsKey("phone"))       managed.setPhone(body.get("phone"));
        return ResponseEntity.ok(toProfileDTO(userRepository.save(managed)));
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    private UserProfileDTO toProfileDTO(User u) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole().name());
        dto.setDepartment(u.getDepartment());
        dto.setPhone(u.getPhone());
        dto.setActive(u.isActive());
        dto.setCreatedAt(u.getCreatedAt());
        return dto;
    }

    @Data public static class UserProfileDTO {
        private Long          id;
        private String        name;
        private String        email;
        private String        role;
        private String        department;
        private String        phone;
        private boolean       active;
        private LocalDateTime createdAt;
    }

    @Data public static class MembershipSummary {
        private Long          membershipId;
        private Long          clubId;
        private String        clubName;
        private String        clubCategory;
        private String        clubRole;
        private String        status;
        private LocalDateTime joinedAt;
    }
}
