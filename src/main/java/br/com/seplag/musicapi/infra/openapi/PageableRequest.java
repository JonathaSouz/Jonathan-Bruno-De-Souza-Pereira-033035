package br.com.seplag.musicapi.infra.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Pageable")
public record PageableRequest(
    @Parameter(description = "Índice da página (0-based)", example = "0")
    Integer page,
    @Parameter(description = "Tamanho da página", example = "10")
    Integer size,
    @ArraySchema(
        arraySchema = @Schema(
            description = "Critério de ordenação no formato: campo,(asc|desc)",
            example = "[\"nome,asc\"]"
        ),
        schema = @Schema(
            description = "Critério de ordenação no formato: campo,(asc|desc)",
            example = "nome,asc"
        )
    )
    List<String> sort
) {}
