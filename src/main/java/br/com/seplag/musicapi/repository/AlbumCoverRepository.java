package br.com.seplag.musicapi.repository;

import br.com.seplag.musicapi.domain.AlbumCover;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumCoverRepository extends JpaRepository<AlbumCover, Long> {
  List<AlbumCover> findByAlbumId(Long albumId);
}
