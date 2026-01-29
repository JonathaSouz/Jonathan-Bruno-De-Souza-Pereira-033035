package br.com.seplag.musicapi.api.dto;

import br.com.seplag.musicapi.domain.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateArtistRequest(
    @NotBlank @Size(max = 200) String nome,
    @NotNull ArtistType tipo
) {}
