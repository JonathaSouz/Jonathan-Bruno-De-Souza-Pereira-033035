package br.com.seplag.musicapi.api.dto;

public record CoverLinkResponse(Long id, String fileName, String contentType, long sizeBytes, String url) {}
