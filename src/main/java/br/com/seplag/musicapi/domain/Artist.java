package br.com.seplag.musicapi.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "artist")
public class Artist {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long id;

  @Column(nullable = false, length = 200)
  @ToString.Include
  private String nome;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ArtistType tipo;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt = OffsetDateTime.now();

  @ManyToMany(mappedBy = "artistas")
  private Set<Album> albuns = new HashSet<>();

  @PreUpdate
  void onUpdate() {
    this.updatedAt = OffsetDateTime.now();
  }
}
