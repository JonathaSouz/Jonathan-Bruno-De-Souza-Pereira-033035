package br.com.seplag.musicapi.api.dto;

import java.util.List;

public record AlbumResponse(Long id, String titulo, List<ArtistResponse> artistas) {}
