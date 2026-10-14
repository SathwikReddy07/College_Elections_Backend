package com.election.dto;

import java.time.LocalDateTime;

public record ElectionRequest(String title, String description, LocalDateTime startTime, LocalDateTime endTime) {}
