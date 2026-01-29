package br.com.seplag.musicapi.service;

import br.com.seplag.musicapi.domain.Artist;
import br.com.seplag.musicapi.domain.ArtistType;
import br.com.seplag.musicapi.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArtistService {

  private final ArtistRepository repo;

  @Transactional
  public Artist create(String nome, ArtistType tipo) {
    var a = new Artist();
    a.setNome(nome);
    a.setTipo(tipo);
    return repo.save(a);
  }

  @Transactional
  public Artist update(Long id, String nome, ArtistType tipo) {
    var a = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Artista não encontrado."));
    a.setNome(nome);
    a.setTipo(tipo);
    return repo.save(a);
  }

  @Transactional(readOnly = true)
  public Page<Artist> searchByName(String nome, Pageable pageable) {
    return repo.findByNomeContainingIgnoreCase(nome == null ? "" : nome, pageable);
  }
}
