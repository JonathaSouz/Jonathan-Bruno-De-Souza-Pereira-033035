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
@Table(name = "refresh_token")
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  @ToString.Include
  private Long id;

  @Column(name = "username", nullable = false, length = 100)
  @ToString.Include
  private String username;

  @Column(name = "token", nullable = false, length = 500)
  private String token;

  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;
}
