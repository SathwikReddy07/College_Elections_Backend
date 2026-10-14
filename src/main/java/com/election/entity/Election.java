package com.election.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    private List<Position> positions = new ArrayList<>();

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    private List<EligibilityRule> eligibilityRules = new ArrayList<>();

    public Election() {
    }

    public Election(Long id, String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                    LocalDateTime createdAt, List<Position> positions, List<EligibilityRule> eligibilityRules) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.positions = positions != null ? positions : new ArrayList<>();
        this.eligibilityRules = eligibilityRules != null ? eligibilityRules : new ArrayList<>();
    }

    public String getStatus() {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime)) return "UPCOMING";
        if (now.isAfter(endTime)) return "COMPLETED";
        return "ACTIVE";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public List<EligibilityRule> getEligibilityRules() {
        return eligibilityRules;
    }

    public void setEligibilityRules(List<EligibilityRule> eligibilityRules) {
        this.eligibilityRules = eligibilityRules;
    }
}
