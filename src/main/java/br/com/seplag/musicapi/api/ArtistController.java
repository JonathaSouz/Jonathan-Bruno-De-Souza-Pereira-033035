package br.com.seplag.musicapi.api;

import br.com.seplag.musicapi.api.dto.*;
import br.com.seplag.musicapi.service.ArtistService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

  private final ArtistService service;

  @PostMapping
  public ArtistResponse create(@Valid @RequestBody CreateArtistRequest req) {
    return Mapper.toArtist(service.create(req.nome(), req.tipo()));
  }

  @PutMapping("/{id}")
  public ArtistResponse update(@PathVariable Long id, @Valid @RequestBody CreateArtistRequest req) {
    return Mapper.toArtist(service.update(id, req.nome(), req.tipo()));
  }

  @GetMapping
  public Page<ArtistResponse> list(@RequestParam(required = false) String nome,
                                  @Parameter(
                                      name = "sort",
                                      description = "Critério de ordenação no formato: campo,(asc|desc)",
                                      example = "nome,asc"
                                  )
                                  @ParameterObject
                                  @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
    return service.searchByName(nome, pageable).map(Mapper::toArtist);
  }
}
