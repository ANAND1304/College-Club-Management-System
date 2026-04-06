package com.clubmanagement.repository;

import com.clubmanagement.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByClubId(Long clubId);
    List<Membership> findByUserId(Long userId);
    List<Membership> findByClubIdAndStatus(Long clubId, Membership.Status status);
    List<Membership> findByStatus(Membership.Status status);
    Optional<Membership> findByUserIdAndClubId(Long userId, Long clubId);
    boolean existsByUserIdAndClubId(Long userId, Long clubId);
    long countByStatus(Membership.Status status);
    long countByClubIdAndStatus(Long clubId, Membership.Status status);
}
