package com.clubmanagement.controller;

import com.clubmanagement.dto.EventDTO;
import com.clubmanagement.model.User;
import com.clubmanagement.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventDTO.Response>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO.Response> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<EventDTO.Response>> getEventsByClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(eventService.getEventsByClub(clubId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLUB_HEAD')")
    public ResponseEntity<EventDTO.Response> createEvent(
            @Valid @RequestBody EventDTO.Request request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(request, user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLUB_HEAD')")
    public ResponseEntity<EventDTO.Response> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventDTO.Request request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(eventService.updateEvent(id, request, user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
