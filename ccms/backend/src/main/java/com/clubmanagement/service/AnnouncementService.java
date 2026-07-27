package com.clubmanagement.service;

import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ClubRepository         clubRepository;

    public AnnouncementService(AnnouncementRepository announcementRepository,
                               ClubRepository clubRepository) {
        this.announcementRepository = announcementRepository;
        this.clubRepository         = clubRepository;
    }

    public List<AnnouncementDTO> getAllAnnouncements() {
        return announcementRepository.findAllByOrderByCreatedAtDesc()
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AnnouncementDTO> getAnnouncementsByClub(Long clubId) {
        return announcementRepository.findByClubIdOrderByCreatedAtDesc(clubId)
            .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public AnnouncementDTO create(Long clubId, String title, String content, User createdBy) {
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + clubId));
        Announcement a = Announcement.builder()
            .title(title).content(content).club(club).createdBy(createdBy).build();
        return toDTO(announcementRepository.save(a));
    }

    @Transactional
    public void delete(Long id) {
        announcementRepository.deleteById(id);
    }

    private AnnouncementDTO toDTO(Announcement a) {
        AnnouncementDTO dto = new AnnouncementDTO();
        dto.setId(a.getId());
        dto.setTitle(a.getTitle());
        dto.setContent(a.getContent());
        dto.setClubId(a.getClub().getId());
        dto.setClubName(a.getClub().getName());
        dto.setCreatedByName(a.getCreatedBy() != null ? a.getCreatedBy().getName() : null);
        dto.setCreatedAt(a.getCreatedAt());
        return dto;
    }

    public static class AnnouncementDTO {
        private Long id;
        private String title;
        private String content;
        private Long clubId;
        private String clubName;
        private String createdByName;
        private LocalDateTime createdAt;

        public Long getId()                 { return id; }
        public String getTitle()            { return title; }
        public String getContent()          { return content; }
        public Long getClubId()             { return clubId; }
        public String getClubName()         { return clubName; }
        public String getCreatedByName()    { return createdByName; }
        public LocalDateTime getCreatedAt() { return createdAt; }

        public void setId(Long v)                 { this.id = v; }
        public void setTitle(String v)            { this.title = v; }
        public void setContent(String v)          { this.content = v; }
        public void setClubId(Long v)             { this.clubId = v; }
        public void setClubName(String v)         { this.clubName = v; }
        public void setCreatedByName(String v)    { this.createdByName = v; }
        public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    }
}
