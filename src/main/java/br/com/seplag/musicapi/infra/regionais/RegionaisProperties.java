package br.com.seplag.musicapi.infra.regionais;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.regionais")
public record RegionaisProperties(String url) {}
