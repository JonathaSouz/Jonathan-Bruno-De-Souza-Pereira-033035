-- Artists
INSERT INTO artist (nome, tipo, created_at, updated_at) VALUES
  ('Serj Tankian', 'CANTOR', now(), now()),
  ('Mike Shinoda', 'CANTOR', now(), now()),
  ('Michel Teló', 'CANTOR', now(), now()),
  ('Guns N'' Roses', 'BANDA', now(), now());

-- Albums
INSERT INTO album (titulo, created_at, updated_at) VALUES
  ('Harakiri', now(), now()),
  ('Black Blooms', now(), now()),
  ('The Rough Dog', now(), now()),
  ('The Rising Tied', now(), now()),
  ('Post Traumatic', now(), now()),
  ('Post Traumatic EP', now(), now()),
  ('Where’d You Go', now(), now()),
  ('Bem Sertanejo', now(), now()),
  ('Bem Sertanejo - O Show (Ao Vivo)', now(), now()),
  ('Bem Sertanejo - (1ª Temporada) - EP', now(), now()),
  ('Use Your Illusion I', now(), now()),
  ('Use Your Illusion II', now(), now()),
  ('Greatest Hits', now(), now());

-- Relations (artist_album)
-- Serj
INSERT INTO artist_album (album_id, artist_id)
SELECT al.id, ar.id FROM album al, artist ar
WHERE ar.nome = 'Serj Tankian' AND al.titulo IN ('Harakiri','Black Blooms','The Rough Dog');

-- Mike
INSERT INTO artist_album (album_id, artist_id)
SELECT al.id, ar.id FROM album al, artist ar
WHERE ar.nome = 'Mike Shinoda' AND al.titulo IN ('The Rising Tied','Post Traumatic','Post Traumatic EP','Where’d You Go');

-- Michel
INSERT INTO artist_album (album_id, artist_id)
SELECT al.id, ar.id FROM album al, artist ar
WHERE ar.nome = 'Michel Teló' AND al.titulo IN ('Bem Sertanejo','Bem Sertanejo - O Show (Ao Vivo)','Bem Sertanejo - (1ª Temporada) - EP');

-- Guns
INSERT INTO artist_album (album_id, artist_id)
SELECT al.id, ar.id FROM album al, artist ar
WHERE ar.nome = 'Guns N'' Roses' AND al.titulo IN ('Use Your Illusion I','Use Your Illusion II','Greatest Hits');
