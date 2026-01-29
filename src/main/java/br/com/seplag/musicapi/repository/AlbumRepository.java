package br.com.seplag.musicapi.repository;

import br.com.seplag.musicapi.domain.Album;
import br.com.seplag.musicapi.domain.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlbumRepository extends JpaRepository<Album, Long> {

  @Query("""
      select distinct a from Album a
      join a.artistas ar
      where (:artistName is null or lower(ar.nome) like lower(concat('%', :artistName, '%')))
        and (:artistType is null or ar.tipo = :artistType)
      """)
  Page<Album> search(@Param("artistName") String artistName,
                    @Param("artistType") ArtistType artistType,
                    Pageable pageable);
}
