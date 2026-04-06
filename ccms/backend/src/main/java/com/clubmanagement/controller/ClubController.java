package com.clubmanagement.controller;

import com.clubmanagement.dto.ClubDTO;
import com.clubmanagement.model.User;
import com.clubmanagement.service.ClubService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @GetMapping
    public ResponseEntity<List<ClubDTO.Response>> getAllClubs() {
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubDTO.Response> getClub(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubById(id));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<ClubDTO.MemberResponse>> getClubMembers(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubMembers(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClubDTO.Response> createClub(
            @Valid @RequestBody ClubDTO.Request request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.createClub(request, user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLUB_HEAD')")
    public ResponseEntity<ClubDTO.Response> updateClub(
            @PathVariable Long id,
            @Valid @RequestBody ClubDTO.Request request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(clubService.updateClub(id, request, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<String> joinClub(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        clubService.joinClub(id, user);
        return ResponseEntity.ok("Join request submitted successfully");
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<String> leaveClub(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        clubService.leaveClub(id, user);
        return ResponseEntity.ok("Left club successfully");
    }
}
