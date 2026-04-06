package com.clubmanagement.controller;

import com.clubmanagement.model.User;
import com.clubmanagement.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<List<AnnouncementService.AnnouncementDTO>> getAll() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<AnnouncementService.AnnouncementDTO>> getByClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByClub(clubId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLUB_HEAD')")
    public ResponseEntity<AnnouncementService.AnnouncementDTO> create(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User user) {
        Long   clubId  = Long.valueOf(body.get("clubId").toString());
        String title   = body.get("title").toString();
        String content = body.get("content").toString();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(announcementService.create(clubId, title, content, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
