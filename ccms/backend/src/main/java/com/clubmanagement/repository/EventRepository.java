package com.clubmanagement.repository;

import com.clubmanagement.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByActiveTrueOrderByEventDateAsc();
    List<Event> findByClubIdAndActiveTrueOrderByEventDateAsc(Long clubId);
    List<Event> findByEventDateAfterAndActiveTrue(LocalDate date);
    long countByActiveTrue();
}
