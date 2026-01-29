package br.com.seplag.musicapi.api.dto;

import br.com.seplag.musicapi.domain.ArtistType;

public record ArtistResponse(Long id, String nome, ArtistType tipo) {}
