package br.com.seplag.musicapi.infra.minio;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

@Service
public class MinioStorageService {

  private final MinioClient client;
  private final MinioProperties props;

  public MinioStorageService(MinioProperties props) {
    this.props = props;
    this.client = props.toClient();
  }

  public void ensureBucketExists() {
    try {
      var exists = client.bucketExists(BucketExistsArgs.builder().bucket(props.bucket()).build());
      if (!exists) {
        client.makeBucket(MakeBucketArgs.builder().bucket(props.bucket()).build());
      }
    } catch (Exception e) {
      throw new IllegalStateException("Falha ao validar/criar bucket no MinIO.", e);
    }
  }

  public String upload(String albumId, String originalFilename, String contentType, long size, InputStream inputStream) {
    ensureBucketExists();
    var safeName = (originalFilename == null || originalFilename.isBlank()) ? "cover" : originalFilename;
    var objectKey = "albums/" + albumId + "/" + UUID.randomUUID() + "-" + safeName;

    try {
      client.putObject(
          PutObjectArgs.builder()
              .bucket(props.bucket())
              .object(objectKey)
              .contentType(contentType)
              .stream(inputStream, size, -1)
              .build()
      );
      return objectKey;
    } catch (Exception e) {
      throw new IllegalStateException("Falha ao enviar arquivo para o MinIO.", e);
    }
  }

  public String presignedGetUrl(String objectKey) {
    try {
      return client.getPresignedObjectUrl(
          GetPresignedObjectUrlArgs.builder()
              .method(Method.GET)
              .bucket(props.bucket())
              .object(objectKey)
              .expiry(props.presignExpirationMinutes() * 60)
              .extraQueryParams(Map.of())
              .build()
      );
    } catch (Exception e) {
      throw new IllegalStateException("Falha ao gerar URL pré-assinada.", e);
    }
  }
}
