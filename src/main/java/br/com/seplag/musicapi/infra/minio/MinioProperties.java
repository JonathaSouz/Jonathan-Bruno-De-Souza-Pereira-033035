package br.com.seplag.musicapi.infra.minio;

import io.minio.MinioClient;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.minio")
public record MinioProperties(
    String endpoint,
    String accessKey,
    String secretKey,
    String bucket,
    int presignExpirationMinutes
) {
  public MinioClient toClient() {
    return MinioClient.builder()
        .endpoint(endpoint)
        .credentials(accessKey, secretKey)
        .build();
  }
}
