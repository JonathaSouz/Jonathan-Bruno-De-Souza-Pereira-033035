package br.com.seplag.musicapi.service;

import br.com.seplag.musicapi.domain.Artist;
import br.com.seplag.musicapi.domain.ArtistType;
import br.com.seplag.musicapi.repository.AlbumRepository;
import br.com.seplag.musicapi.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlbumServiceTest {

  @Test
  void shouldPublishEventOnCreate() {
    var albumRepo = mock(AlbumRepository.class);
    var artistRepo = mock(ArtistRepository.class);
    var publisher = mock(ApplicationEventPublisher.class);

    var a = new Artist();
    a.setNome("Serj");
    a.setTipo(ArtistType.CANTOR);

    when(artistRepo.findAllById(Set.of(1L))).thenReturn(List.of(a));
    when(albumRepo.save(any())).thenAnswer(inv -> {
      var album = (br.com.seplag.musicapi.domain.Album) inv.getArgument(0);
      // fake id
      var f = br.com.seplag.musicapi.domain.Album.class.getDeclaredField("id");
      f.setAccessible(true);
      f.set(album, 10L);
      return album;
    });

    var svc = new AlbumService(albumRepo, artistRepo, publisher);
    var created = svc.create("Harakiri", Set.of(1L));

    assertEquals("Harakiri", created.getTitulo());

    var captor = ArgumentCaptor.forClass(AlbumCreatedEvent.class);
    verify(publisher).publishEvent(captor.capture());
    assertEquals(10L, captor.getValue().albumId());
  }
}
