package com.election.repository;

import com.election.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByElectionId(Long electionId);
    void deleteByElectionId(Long electionId);
}
