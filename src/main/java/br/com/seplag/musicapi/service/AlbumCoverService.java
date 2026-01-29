package br.com.seplag.musicapi.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import br.com.seplag.musicapi.domain.AlbumCover;
import br.com.seplag.musicapi.infra.minio.MinioStorageService;
import br.com.seplag.musicapi.repository.AlbumCoverRepository;
import br.com.seplag.musicapi.repository.AlbumRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumCoverService {

  private final AlbumRepository albumRepository;
  private final AlbumCoverRepository coverRepository;
  private final MinioStorageService storage;

  @Transactional
  public List<AlbumCover> upload(Long albumId, List<MultipartFile> files) {
    var album = albumRepository.findById(albumId).orElseThrow(() -> new IllegalArgumentException("Álbum não encontrado."));
    if (files == null || files.isEmpty()) {
      throw new IllegalArgumentException("Envie pelo menos 1 arquivo.");
    }

    return files.stream().map(file -> {
      try {
        var objectKey = storage.upload(albumId.toString(), file.getOriginalFilename(), file.getContentType(), file.getSize(), file.getInputStream());
        var cover = new AlbumCover();
        cover.setAlbum(album);
        cover.setObjectKey(objectKey);
        cover.setFileName(file.getOriginalFilename() == null ? "cover" : file.getOriginalFilename());
        cover.setContentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType());
        cover.setSizeBytes(file.getSize());
        return coverRepository.save(cover);
      } catch (Exception e) {
        throw new IllegalStateException("Falha ao processar upload.", e);
      }
    }).toList();
  }

  @Transactional(readOnly = true)
  public List<AlbumCover> list(Long albumId) {
    return coverRepository.findByAlbumId(albumId);
  }

  public String presignedUrl(AlbumCover cover) {
    return storage.presignedGetUrl(cover.getObjectKey());
  }
}
