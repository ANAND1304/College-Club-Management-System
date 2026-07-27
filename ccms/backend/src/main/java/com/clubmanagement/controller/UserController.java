package com.clubmanagement.controller;

import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.Membership;
import com.clubmanagement.model.User;
import com.clubmanagement.repository.MembershipRepository;
import com.clubmanagement.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository       userRepository;
    private final MembershipRepository membershipRepository;

    public UserController(UserRepository userRepository,
                          MembershipRepository membershipRepository) {
        this.userRepository       = userRepository;
        this.membershipRepository = membershipRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(toProfileDTO(user));
    }

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

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> body) {
        User managed = userRepository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (body.containsKey("name") && !body.get("name").isBlank())
            managed.setName(body.get("name"));
        if (body.containsKey("department"))
            managed.setDepartment(body.get("department"));
        if (body.containsKey("phone"))
            managed.setPhone(body.get("phone"));
        return ResponseEntity.ok(toProfileDTO(userRepository.save(managed)));
    }

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

    public static class UserProfileDTO {
        private Long id;
        private String name;
        private String email;
        private String role;
        private String department;
        private String phone;
        private boolean active;
        private LocalDateTime createdAt;

        public Long getId()                 { return id; }
        public String getName()             { return name; }
        public String getEmail()            { return email; }
        public String getRole()             { return role; }
        public String getDepartment()       { return department; }
        public String getPhone()            { return phone; }
        public boolean isActive()           { return active; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public void setId(Long v)                 { this.id = v; }
        public void setName(String v)             { this.name = v; }
        public void setEmail(String v)            { this.email = v; }
        public void setRole(String v)             { this.role = v; }
        public void setDepartment(String v)       { this.department = v; }
        public void setPhone(String v)            { this.phone = v; }
        public void setActive(boolean v)          { this.active = v; }
        public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    }

    public static class MembershipSummary {
        private Long membershipId;
        private Long clubId;
        private String clubName;
        private String clubCategory;
        private String clubRole;
        private String status;
        private LocalDateTime joinedAt;

        public Long getMembershipId()           { return membershipId; }
        public Long getClubId()                 { return clubId; }
        public String getClubName()             { return clubName; }
        public String getClubCategory()         { return clubCategory; }
        public String getClubRole()             { return clubRole; }
        public String getStatus()               { return status; }
        public LocalDateTime getJoinedAt()      { return joinedAt; }

        public void setMembershipId(Long v)         { this.membershipId = v; }
        public void setClubId(Long v)               { this.clubId = v; }
        public void setClubName(String v)           { this.clubName = v; }
        public void setClubCategory(String v)       { this.clubCategory = v; }
        public void setClubRole(String v)           { this.clubRole = v; }
        public void setStatus(String v)             { this.status = v; }
        public void setJoinedAt(LocalDateTime v)    { this.joinedAt = v; }
    }
}
