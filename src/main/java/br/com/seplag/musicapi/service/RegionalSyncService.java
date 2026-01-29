package br.com.seplag.musicapi.service;

import br.com.seplag.musicapi.domain.Regional;
import br.com.seplag.musicapi.infra.regionais.RegionaisProperties;
import br.com.seplag.musicapi.infra.regionais.RegionalExternalDto;
import br.com.seplag.musicapi.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionalSyncService {

  private final RegionalRepository repo;
  private final RestClient restClient;
  private final RegionaisProperties props;

  @Transactional
  public SyncResult sync() {
    var external = fetchExternal();

    var externalMap = new LinkedHashMap<Integer, String>();
    var invalid = 0;
    var duplicated = 0;

    for (RegionalExternalDto dto : external) {
      if (dto == null || dto.id() == null || dto.nome() == null || dto.nome().isBlank()) {
        invalid++;
        continue;
      }
      if (externalMap.putIfAbsent(dto.id(), dto.nome()) != null) {
        duplicated++;
      }
    }

    var active = repo.findByAtivoTrue();
    var activeByRef = active.stream()
        .filter(r -> r.getRegionalRefId() != null)
        .collect(Collectors.toMap(
            Regional::getRegionalRefId,
            Function.identity(),
            (a, b) -> a
        ));

    var toSave = new ArrayList<Regional>();

    var inserted = 0;
    var inactivated = 0;
    var changed = 0;

    for (var entry : externalMap.entrySet()) {
      var refId = entry.getKey();
      var name = entry.getValue();

      var current = activeByRef.get(refId);

      if (current == null) {
        var r = new Regional();
        r.setRegionalRefId(refId);
        r.setNome(name);
        r.setAtivo(true);
        toSave.add(r);
        inserted++;
        continue;
      }

      if (!Objects.equals(current.getNome(), name)) {
        current.setAtivo(false);
        toSave.add(current);

        var r = new Regional();
        r.setRegionalRefId(refId);
        r.setNome(name);
        r.setAtivo(true);
        toSave.add(r);

        changed++;
      }
    }

    var externalIds = externalMap.keySet();
    for (Regional r : active) {
      var refId = r.getRegionalRefId();
      if (refId != null && !externalIds.contains(refId)) {
        r.setAtivo(false);
        toSave.add(r);
        inactivated++;
      }
    }

    if (!toSave.isEmpty()) {
      repo.saveAll(toSave);
    }

    return new SyncResult(inserted, inactivated, changed, external.size(), invalid, duplicated);
  }

  private List<RegionalExternalDto> fetchExternal() {
    try {
      var arr = restClient.get()
          .uri(props.url()) 
          .accept(MediaType.APPLICATION_JSON)
          .retrieve()
          .body(RegionalExternalDto[].class);

      return arr == null ? List.of() : Arrays.asList(arr);
    } catch (RestClientException e) {
      throw new ExternalIntegrationException("Falha ao consultar serviço de regionais: " + props.url(), e);
    }
  }

  public record SyncResult(
      int inserted,
      int inactivated,
      int changed,
      int externalCount,
      int invalidCount,
      int duplicatedCount
  ) {}

  public static class ExternalIntegrationException extends RuntimeException {
    public ExternalIntegrationException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
