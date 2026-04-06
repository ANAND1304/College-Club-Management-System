package com.clubmanagement.repository;

import com.clubmanagement.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {
    List<Club> findByActiveTrue();
    List<Club> findByCategoryAndActiveTrue(String category);

    @Query("SELECT DISTINCT c.category FROM Club c WHERE c.category IS NOT NULL")
    List<String> findAllCategories();

    long countByActiveTrue();
}
