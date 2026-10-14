package com.election.repository;

import com.election.entity.VoteRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    boolean existsByElectionIdAndUserIdAndPositionId(Long electionId, Long userId, Long positionId);
    boolean existsByElectionIdAndUserId(Long electionId, Long userId);
}
