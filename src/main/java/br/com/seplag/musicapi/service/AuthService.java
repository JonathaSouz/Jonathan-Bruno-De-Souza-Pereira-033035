package br.com.seplag.musicapi.service;

import br.com.seplag.musicapi.domain.RefreshToken;
import br.com.seplag.musicapi.infra.security.JwtService;
import br.com.seplag.musicapi.infra.security.SecurityProperties;
import br.com.seplag.musicapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshRepo;
  private final SecurityProperties props;

  @Transactional
  public Tokens login(String username, String password) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

    var access = jwtService.generateAccessToken(username);
    var refresh = jwtService.generateRefreshToken(username);

    // rotate existing refresh tokens for simplicity
    refreshRepo.deleteByUsername(username);

    var rt = new RefreshToken();
    rt.setUsername(username);
    rt.setToken(refresh);
    rt.setExpiresAt(OffsetDateTime.now().plusMinutes(props.jwt().refreshTtlMinutes()));
    refreshRepo.save(rt);

    var expiresIn = props.jwt().accessTtlMinutes() * 60L;
    return new Tokens(access, refresh, expiresIn);
  }

  @Transactional
  public Tokens refresh(String refreshToken) {
    if (!jwtService.isValid(refreshToken)) {
      throw new IllegalArgumentException("Refresh token inválido.");
    }
    var username = jwtService.extractUsername(refreshToken);

    var stored = refreshRepo.findByToken(refreshToken)
        .orElseThrow(() -> new IllegalArgumentException("Refresh token não encontrado (revogado/rotacionado)."));

    if (stored.getExpiresAt().isBefore(OffsetDateTime.now())) {
      throw new IllegalArgumentException("Refresh token expirado.");
    }

    var newAccess = jwtService.generateAccessToken(username);
    var expiresIn = props.jwt().accessTtlMinutes() * 60L;
    return new Tokens(newAccess, refreshToken, expiresIn);
  }

  public record Tokens(String accessToken, String refreshToken, long expiresInSeconds) {}
}
