package br.com.seplag.musicapi.domain;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "regional")
public class Regional {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long id;

  @Column(name = "regional_ref_id", nullable = false)
  @ToString.Include
  private Integer regionalRefId;

  @Column(nullable = false, length = 200)
  @ToString.Include
  private String nome;

  @Column(nullable = false)
  private boolean ativo;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt = OffsetDateTime.now();
}
