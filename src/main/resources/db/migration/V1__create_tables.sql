CREATE TABLE artist (
  id BIGSERIAL PRIMARY KEY,
  nome VARCHAR(200) NOT NULL,
  tipo VARCHAR(20) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE album (
  id BIGSERIAL PRIMARY KEY,
  titulo VARCHAR(200) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL,
  updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE artist_album (
  album_id BIGINT NOT NULL REFERENCES album(id),
  artist_id BIGINT NOT NULL REFERENCES artist(id),
  PRIMARY KEY (album_id, artist_id)
);

CREATE TABLE album_cover (
  id BIGSERIAL PRIMARY KEY,
  album_id BIGINT NOT NULL REFERENCES album(id),
  object_key VARCHAR(500) NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  content_type VARCHAR(100) NOT NULL,
  size_bytes BIGINT NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE regional (
  id BIGSERIAL PRIMARY KEY,
  regional_ref_id INTEGER NOT NULL,
  nome VARCHAR(200) NOT NULL,
  ativo BOOLEAN NOT NULL,
  created_at TIMESTAMPTZ NOT NULL
);

-- Garante que só exista UMA regional ATIVA por regional_ref_id
CREATE UNIQUE INDEX IF NOT EXISTS ux_regional_ref_id_active
ON regional (regional_ref_id)
WHERE ativo = true;


CREATE TABLE refresh_token (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  token VARCHAR(500) NOT NULL,
  expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_refresh_token_token ON refresh_token (token);
