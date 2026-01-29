package br.com.seplag.musicapi.infra.ratelimit;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit")
public record RateLimitProperties(int requestsPerMinute) {}
