package br.com.seplag.musicapi.api.dto;

public record TokenResponse(String accessToken, String refreshToken, long expiresInSeconds) {}
