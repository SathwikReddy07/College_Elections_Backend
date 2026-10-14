package com.election.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes")
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long electionId;

    @Column(nullable = false)
    private Long positionId;

    @Column(nullable = false)
    private Long candidateId;

    private String branch;
    private String batch;
    private String studentType;
    private LocalDateTime votedAt;

    public Vote() {
    }

    public Vote(Long id, Long electionId, Long positionId, Long candidateId,
                String branch, String batch, String studentType, LocalDateTime votedAt) {
        this.id = id;
        this.electionId = electionId;
        this.positionId = positionId;
        this.candidateId = candidateId;
        this.branch = branch;
        this.batch = batch;
        this.studentType = studentType;
        this.votedAt = votedAt;
    }

    @PrePersist
    public void prePersist() {
        this.votedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getElectionId() {
        return electionId;
    }

    public void setElectionId(Long electionId) {
        this.electionId = electionId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
}
