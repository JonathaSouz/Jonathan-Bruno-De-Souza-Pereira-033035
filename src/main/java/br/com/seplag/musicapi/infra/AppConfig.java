package br.com.seplag.musicapi.infra;

import br.com.seplag.musicapi.infra.minio.MinioProperties;
import br.com.seplag.musicapi.infra.regionais.RegionaisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MinioProperties.class, RegionaisProperties.class})
public class AppConfig {}
