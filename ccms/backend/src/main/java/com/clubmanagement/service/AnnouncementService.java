package com.clubmanagement.service;

import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final ClubRepository         clubRepository;

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

    @Data
    public static class AnnouncementDTO {
        private Long          id;
        private String        title;
        private String        content;
        private Long          clubId;
        private String        clubName;
        private String        createdByName;
        private LocalDateTime createdAt;
    }
}
