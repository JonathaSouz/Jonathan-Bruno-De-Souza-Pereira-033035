package br.com.seplag.musicapi.api;

import br.com.seplag.musicapi.api.dto.*;
import br.com.seplag.musicapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody LoginRequest req) {
    var tokens = authService.login(req.username(), req.password());
    return new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.expiresInSeconds());
  }

  @PostMapping("/refresh")
  public TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
    var tokens = authService.refresh(req.refreshToken());
    return new TokenResponse(tokens.accessToken(), tokens.refreshToken(), tokens.expiresInSeconds());
  }
}
