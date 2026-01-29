package br.com.seplag.musicapi.infra.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(
    String allowedOrigins,
    Jwt jwt
) {
  public record Jwt(String secret, int accessTtlMinutes, int refreshTtlMinutes) {}
}
