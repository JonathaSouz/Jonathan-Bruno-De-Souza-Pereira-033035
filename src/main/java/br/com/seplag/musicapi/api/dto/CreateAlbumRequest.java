package br.com.seplag.musicapi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateAlbumRequest(
    @NotBlank @Size(max = 200) String titulo,
    @NotEmpty Set<Long> artistIds
) {}
