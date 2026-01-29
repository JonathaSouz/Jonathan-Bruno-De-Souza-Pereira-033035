package br.com.seplag.musicapi.repository;

import br.com.seplag.musicapi.domain.Artist;
import br.com.seplag.musicapi.domain.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
  Page<Artist> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
  Page<Artist> findByTipo(ArtistType tipo, Pageable pageable);
}
