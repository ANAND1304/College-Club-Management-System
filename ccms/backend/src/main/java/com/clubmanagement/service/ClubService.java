package com.clubmanagement.service;

import com.clubmanagement.dto.ClubDTO;
import com.clubmanagement.exception.ResourceNotFoundException;
import com.clubmanagement.model.*;
import com.clubmanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private final ClubRepository       clubRepository;
    private final MembershipRepository membershipRepository;

    public ClubService(ClubRepository clubRepository,
                       MembershipRepository membershipRepository) {
        this.clubRepository       = clubRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional(readOnly = true)
    public List<ClubDTO.Response> getAllClubs() {
        return clubRepository.findByActiveTrue().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClubDTO.Response getClubById(Long id) {
        return toResponse(clubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + id)));
    }

    @Transactional
    public ClubDTO.Response createClub(ClubDTO.Request request, User createdBy) {
        Club club = Club.builder()
            .name(request.getName())
            .description(request.getDescription())
            .category(request.getCategory())
            .imageUrl(request.getImageUrl())
            .createdBy(createdBy)
            .active(true)
            .build();
        Club saved = clubRepository.save(club);
        // Build response manually to avoid lazy loading issues
        ClubDTO.Response r = new ClubDTO.Response();
        r.setId(saved.getId());
        r.setName(saved.getName());
        r.setDescription(saved.getDescription());
        r.setCategory(saved.getCategory());
        r.setImageUrl(saved.getImageUrl());
        r.setActive(saved.isActive());
        r.setCreatedAt(saved.getCreatedAt());
        r.setCreatedByName(createdBy.getName());
        r.setMemberCount(0);
        return r;
    }

    @Transactional
    public ClubDTO.Response updateClub(Long id, ClubDTO.Request request, User updatedBy) {
        Club club = clubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + id));
        club.setName(request.getName());
        club.setDescription(request.getDescription());
        club.setCategory(request.getCategory());
        club.setImageUrl(request.getImageUrl());
        return toResponse(clubRepository.save(club));
    }

    @Transactional
    public void deleteClub(Long id) {
        Club club = clubRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + id));
        club.setActive(false);
        clubRepository.save(club);
    }

    @Transactional
    public void joinClub(Long clubId, User user) {
        if (membershipRepository.existsByUserIdAndClubId(user.getId(), clubId)) {
            throw new RuntimeException("Already a member or request pending");
        }
        Club club = clubRepository.findById(clubId)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + clubId));
        Membership membership = Membership.builder()
            .user(user).club(club)
            .status(Membership.Status.PENDING)
            .clubRole("MEMBER")
            .build();
        membershipRepository.save(membership);
    }

    @Transactional
    public void leaveClub(Long clubId, User user) {
        Membership membership = membershipRepository
            .findByUserIdAndClubId(user.getId(), clubId)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));
        membershipRepository.delete(membership);
    }

    @Transactional(readOnly = true)
    public List<ClubDTO.MemberResponse> getClubMembers(Long clubId) {
        return membershipRepository
            .findByClubIdAndStatus(clubId, Membership.Status.APPROVED)
            .stream().map(m -> {
                ClubDTO.MemberResponse r = new ClubDTO.MemberResponse();
                r.setMembershipId(m.getId());
                r.setUserId(m.getUser().getId());
                r.setUserName(m.getUser().getName());
                r.setUserEmail(m.getUser().getEmail());
                r.setClubRole(m.getClubRole());
                r.setJoinedAt(m.getJoinedAt());
                return r;
            }).collect(Collectors.toList());
    }

    private ClubDTO.Response toResponse(Club club) {
        ClubDTO.Response r = new ClubDTO.Response();
        r.setId(club.getId());
        r.setName(club.getName());
        r.setDescription(club.getDescription());
        r.setCategory(club.getCategory());
        r.setImageUrl(club.getImageUrl());
        r.setActive(club.isActive());
        r.setCreatedAt(club.getCreatedAt());
        try {
            if (club.getCreatedBy() != null) {
                r.setCreatedByName(club.getCreatedBy().getName());
            }
        } catch (Exception e) {
            r.setCreatedByName(null);
        }
        r.setMemberCount(membershipRepository.countByClubIdAndStatus(
            club.getId(), Membership.Status.APPROVED));
        return r;
    }
}
