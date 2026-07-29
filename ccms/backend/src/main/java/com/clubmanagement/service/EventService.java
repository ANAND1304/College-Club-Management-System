package com.clubmanagement.service;

import com.clubmanagement.dto.EventDTO;
import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ClubRepository  clubRepository;

    public EventService(EventRepository eventRepository, ClubRepository clubRepository) {
        this.eventRepository = eventRepository;
        this.clubRepository  = clubRepository;
    }

    @Transactional(readOnly = true)
    public List<EventDTO.Response> getAllEvents() {
        return eventRepository.findByActiveTrueOrderByEventDateAsc()
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EventDTO.Response> getEventsByClub(Long clubId) {
        return eventRepository.findByClubIdAndActiveTrueOrderByEventDateAsc(clubId)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventDTO.Response getEventById(Long id) {
        return toResponse(eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id)));
    }

    @Transactional
    public EventDTO.Response createEvent(EventDTO.Request request, User createdBy) {
        Club club = clubRepository.findById(request.getClubId())
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + request.getClubId()));
        Event event = Event.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .eventDate(request.getEventDate())
            .location(request.getLocation())
            .imageUrl(request.getImageUrl())
            .maxParticipants(request.getMaxParticipants())
            .club(club)
            .createdBy(createdBy)
            .active(true)
            .build();
        Event saved = eventRepository.save(event);
        // Build response manually to avoid lazy loading
        EventDTO.Response r = new EventDTO.Response();
        r.setId(saved.getId());
        r.setTitle(saved.getTitle());
        r.setDescription(saved.getDescription());
        r.setEventDate(saved.getEventDate());
        r.setLocation(saved.getLocation());
        r.setImageUrl(saved.getImageUrl());
        r.setMaxParticipants(saved.getMaxParticipants());
        r.setActive(saved.isActive());
        r.setCreatedAt(saved.getCreatedAt());
        r.setClubId(club.getId());
        r.setClubName(club.getName());
        r.setCreatedByName(createdBy.getName());
        return r;
    }

    @Transactional
    public EventDTO.Response updateEvent(Long id, EventDTO.Request request, User updatedBy) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setImageUrl(request.getImageUrl());
        event.setMaxParticipants(request.getMaxParticipants());
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        event.setActive(false);
        eventRepository.save(event);
    }

    private EventDTO.Response toResponse(Event event) {
        EventDTO.Response r = new EventDTO.Response();
        r.setId(event.getId());
        r.setTitle(event.getTitle());
        r.setDescription(event.getDescription());
        r.setEventDate(event.getEventDate());
        r.setLocation(event.getLocation());
        r.setImageUrl(event.getImageUrl());
        r.setMaxParticipants(event.getMaxParticipants());
        r.setActive(event.isActive());
        r.setCreatedAt(event.getCreatedAt());
        try {
            r.setClubId(event.getClub().getId());
            r.setClubName(event.getClub().getName());
        } catch (Exception e) { /* lazy load */ }
        try {
            if (event.getCreatedBy() != null)
                r.setCreatedByName(event.getCreatedBy().getName());
        } catch (Exception e) { /* lazy load */ }
        return r;
    }
}
