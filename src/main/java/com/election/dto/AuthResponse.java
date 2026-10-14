package com.election.dto;

public record AuthResponse(String token, String email, String name, String picture, String role) {}
