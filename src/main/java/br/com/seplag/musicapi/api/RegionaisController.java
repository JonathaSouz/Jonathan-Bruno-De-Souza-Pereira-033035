package br.com.seplag.musicapi.api;

import br.com.seplag.musicapi.api.dto.RegionalResponse;
import br.com.seplag.musicapi.repository.RegionalRepository;
import br.com.seplag.musicapi.service.RegionalSyncService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
public class RegionaisController {

  private final RegionalSyncService syncService;
  private final RegionalRepository repo;

  @PostMapping("/sync")
  public Map<String, Object> sync() {
    var r = syncService.sync();
    return Map.of(
        "inserted", r.inserted(),
        "inactivated", r.inactivated(),
        "changed", r.changed(),
        "externalCount", r.externalCount()
    );
  }

  @GetMapping
  public List<RegionalResponse> list(@RequestParam(defaultValue = "true") boolean ativo) {
    var list = ativo ? repo.findByAtivoTrue() : repo.findAll();
    return list.stream().map(r -> new RegionalResponse(r.getRegionalRefId(), r.getNome(), r.isAtivo())).toList();
  }
}
