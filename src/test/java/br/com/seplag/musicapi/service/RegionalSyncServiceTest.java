package br.com.seplag.musicapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import br.com.seplag.musicapi.domain.Regional;
import br.com.seplag.musicapi.infra.regionais.RegionaisProperties;
import br.com.seplag.musicapi.repository.RegionalRepository;

class RegionalSyncServiceTest {

  @SuppressWarnings("unchecked")
  @Test
  void shouldInactivateMissingAndInsertNew() {
    var repo = mock(RegionalRepository.class);

    var active = new Regional();
    active.setRegionalRefId(1);
    active.setNome("A");
    active.setAtivo(true);

    when(repo.findByAtivoTrue()).thenReturn(List.of(active));

    // mock rest client chain
    var rc = mock(RestClient.class);

    var getSpec = mock(RestClient.RequestHeadersUriSpec.class);
    var headersSpec = mock(RestClient.RequestHeadersSpec.class);
    var responseSpec = mock(RestClient.ResponseSpec.class);

    when(rc.get()).thenReturn(getSpec);
    when(getSpec.uri(anyString())).thenReturn(headersSpec);
    when(headersSpec.accept(any())).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);

    // external has only id=2
    var external =
        new br.com.seplag.musicapi.infra.regionais.RegionalExternalDto[] {
            new br.com.seplag.musicapi.infra.regionais.RegionalExternalDto(2, "B")
        };
    when(responseSpec.body(br.com.seplag.musicapi.infra.regionais.RegionalExternalDto[].class)).thenReturn(external);

    var props = new RegionaisProperties("http://example/regionais");
    var svc = new RegionalSyncService(repo, rc, props);

    var result = svc.sync();
    assertEquals(1, result.inserted());
    assertEquals(1, result.inactivated());
  }
}
