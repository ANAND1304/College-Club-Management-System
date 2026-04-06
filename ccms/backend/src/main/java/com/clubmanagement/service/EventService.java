package com.clubmanagement.service;

import com.clubmanagement.dto.EventDTO;
import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ClubRepository clubRepository;

    public List<EventDTO.Response> getAllEvents() {
        return eventRepository.findByActiveTrueOrderByEventDateAsc()
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<EventDTO.Response> getEventsByClub(Long clubId) {
        return eventRepository.findByClubIdAndActiveTrueOrderByEventDateAsc(clubId)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public EventDTO.Response getEventById(Long id) {
        return toResponse(eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id)));
    }

    @Transactional
    public EventDTO.Response createEvent(EventDTO.Request request, User createdBy) {
        Club club = clubRepository.findById(request.getClubId())
            .orElseThrow(() -> new ResourceNotFoundException("Club not found with id: " + request.getClubId()));
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
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public EventDTO.Response updateEvent(Long id, EventDTO.Request request, User updatedBy) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
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
            .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
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
        r.setClubId(event.getClub().getId());
        r.setClubName(event.getClub().getName());
        if (event.getCreatedBy() != null) r.setCreatedByName(event.getCreatedBy().getName());
        return r;
    }
}
