package br.com.seplag.musicapi.api;

import br.com.seplag.musicapi.api.dto.*;
import br.com.seplag.musicapi.domain.ArtistType;
import br.com.seplag.musicapi.service.AlbumCoverService;
import br.com.seplag.musicapi.service.AlbumService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/albums")
public class AlbumController {

  private final AlbumService service;
  private final AlbumCoverService coverService;

  public AlbumController(AlbumService service, AlbumCoverService coverService) {
    this.service = service;
    this.coverService = coverService;
  }

  @PostMapping
  public AlbumResponse create(@Valid @RequestBody CreateAlbumRequest req) {
    return Mapper.toAlbum(service.create(req.titulo(), req.artistIds()));
  }

  @PutMapping("/{id}")
  public AlbumResponse update(@PathVariable Long id, @Valid @RequestBody CreateAlbumRequest req) {
    return Mapper.toAlbum(service.update(id, req.titulo(), req.artistIds()));
  }

  @GetMapping
  public Page<AlbumResponse> list(@RequestParam(required = false) String artistName,
                                 @RequestParam(required = false) ArtistType artistType,
                                 @Parameter(
                                     name = "sort",
                                     description = "Critério de ordenação no formato: campo,(asc|desc)",
                                     example = "titulo,asc"
                                 )
                                 @ParameterObject
                                 @PageableDefault(size = 10, sort = "titulo") Pageable pageable) {
    return service.search(artistName, artistType, pageable).map(Mapper::toAlbum);
  }

  @PostMapping(path = "/{id}/covers", consumes = "multipart/form-data")
  public List<Long> upload(@PathVariable Long id, @RequestPart("files") List<MultipartFile> files) {
    return coverService.upload(id, files).stream().map(c -> c.getId()).toList();
  }

  @GetMapping("/{id}/covers")
  public List<CoverLinkResponse> listCovers(@PathVariable Long id) {
    return coverService.list(id).stream().map(c ->
        new CoverLinkResponse(
            c.getId(),
            c.getFileName(),
            c.getContentType(),
            c.getSizeBytes(),
            coverService.presignedUrl(c)
        )
    ).toList();
  }
}
