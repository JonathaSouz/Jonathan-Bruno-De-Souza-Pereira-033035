package br.com.seplag.musicapi.api;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.seplag.musicapi.service.RegionalSyncService;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, Object> badRequest(IllegalArgumentException ex) {
    return Map.of(
        "error", "BAD_REQUEST",
        "message", ex.getMessage()
    );
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, Object> generic(Exception ex) {
    return Map.of(
        "error", "INTERNAL_ERROR",
        "message", "Erro inesperado."
    );
  }
  
  @ExceptionHandler(RegionalSyncService.ExternalIntegrationException.class)
  public ResponseEntity<Map<String, Object>> handleExternal(RegionalSyncService.ExternalIntegrationException e) {
    return ResponseEntity.status(502).body(Map.of(
        "error", "BAD_GATEWAY",
        "message", e.getMessage()
    ));
  }
}
