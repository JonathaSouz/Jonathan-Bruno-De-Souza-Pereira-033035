package br.com.seplag.musicapi.infra.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

  private final SecurityProperties props;
  private final Key key;

  public JwtService(SecurityProperties props) {
    this.props = props;
    this.key = Keys.hmacShaKeyFor(props.jwt().secret().getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(String username) {
    var now = Instant.now();
    var exp = now.plus(Duration.ofMinutes(props.jwt().accessTtlMinutes()));

    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of("typ", "access"))
        .signWith(key)
        .compact();
  }

  public String generateRefreshToken(String username) {
    var now = Instant.now();
    var exp = now.plus(Duration.ofMinutes(props.jwt().refreshTtlMinutes()));

    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .claims(Map.of("typ", "refresh"))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser()
        .verifyWith((javax.crypto.SecretKey) key)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  public boolean isValid(String token) {
    try {
      Jwts.parser().verifyWith((javax.crypto.SecretKey) key).build().parseSignedClaims(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
