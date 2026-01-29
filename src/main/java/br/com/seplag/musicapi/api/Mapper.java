package br.com.seplag.musicapi.api;

import br.com.seplag.musicapi.api.dto.AlbumResponse;
import br.com.seplag.musicapi.api.dto.ArtistResponse;
import br.com.seplag.musicapi.domain.Album;
import br.com.seplag.musicapi.domain.Artist;

public final class Mapper {
  private Mapper() {}

  public static ArtistResponse toArtist(Artist a) {
    return new ArtistResponse(a.getId(), a.getNome(), a.getTipo());
  }

  public static AlbumResponse toAlbum(Album a) {
    var artistas = a.getArtistas().stream().map(Mapper::toArtist).toList();
    return new AlbumResponse(a.getId(), a.getTitulo(), artistas);
  }
}
