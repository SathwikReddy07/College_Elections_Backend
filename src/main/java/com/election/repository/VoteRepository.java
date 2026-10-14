package com.election.repository;

import com.election.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    @Query("SELECT v.candidateId, COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND v.positionId = :positionId GROUP BY v.candidateId")
    List<Object[]> countVotesByPosition(@Param("electionId") Long electionId, @Param("positionId") Long positionId);

    @Query("SELECT v.candidateId, COUNT(v) FROM Vote v WHERE v.electionId = :electionId AND v.positionId = :positionId AND (:branch IS NULL OR v.branch = :branch) AND (:batch IS NULL OR v.batch = :batch) AND (:studentType IS NULL OR v.studentType = :studentType) GROUP BY v.candidateId")
    List<Object[]> countVotesFiltered(@Param("electionId") Long electionId, @Param("positionId") Long positionId, @Param("branch") String branch, @Param("batch") String batch, @Param("studentType") String studentType);

    long countByElectionIdAndPositionId(Long electionId, Long positionId);
}
