package br.com.seplag.musicapi.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.seplag.musicapi.domain.Album;
import br.com.seplag.musicapi.domain.ArtistType;
import br.com.seplag.musicapi.repository.AlbumRepository;
import br.com.seplag.musicapi.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

  private final AlbumRepository albumRepository;
  private final ArtistRepository artistRepository;
  private final ApplicationEventPublisher publisher;

  @Transactional
  public Album create(String titulo, Set<Long> artistIds) {
    var artists = new HashSet<>(artistRepository.findAllById(artistIds));
    if (artists.size() != artistIds.size()) {
      throw new IllegalArgumentException("Um ou mais artistas não foram encontrados.");
    }
    var album = new Album();
    album.setTitulo(titulo);
    album.getArtistas().addAll(artists);

    var saved = albumRepository.save(album);
    publisher.publishEvent(new AlbumCreatedEvent(saved.getId(), saved.getTitulo()));
    return saved;
  }

  @Transactional
  public Album update(Long id, String titulo, Set<Long> artistIds) {
    var album = albumRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Álbum não encontrado."));
    var artists = new HashSet<>(artistRepository.findAllById(artistIds));
    if (artists.size() != artistIds.size()) {
      throw new IllegalArgumentException("Um ou mais artistas não foram encontrados.");
    }
    album.setTitulo(titulo);
    album.getArtistas().clear();
    album.getArtistas().addAll(artists);
    return albumRepository.save(album);
  }

  @Transactional(readOnly = true)
  public Page<Album> search(String artistName, ArtistType artistType, Pageable pageable) {
    return albumRepository.search(artistName, artistType, pageable);
  }
}
