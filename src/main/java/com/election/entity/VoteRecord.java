package com.election.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vote_records", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"electionId", "userId", "positionId"})
})
public class VoteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long electionId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long positionId;

    public VoteRecord() {
    }

    public VoteRecord(Long id, Long electionId, Long userId, Long positionId) {
        this.id = id;
        this.electionId = electionId;
        this.userId = userId;
        this.positionId = positionId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
}
