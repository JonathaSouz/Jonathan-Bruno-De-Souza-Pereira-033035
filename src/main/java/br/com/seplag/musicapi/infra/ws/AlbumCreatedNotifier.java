package br.com.seplag.musicapi.infra.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import br.com.seplag.musicapi.service.AlbumCreatedEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AlbumCreatedNotifier {

  private final SimpMessagingTemplate template;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onAlbumCreated(AlbumCreatedEvent event) {
    template.convertAndSend("/topic/albums", event);
  }
}
