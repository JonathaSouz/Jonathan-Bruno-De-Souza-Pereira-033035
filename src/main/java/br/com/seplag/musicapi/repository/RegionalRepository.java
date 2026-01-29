package br.com.seplag.musicapi.repository;

import br.com.seplag.musicapi.domain.Regional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionalRepository extends JpaRepository<Regional, Long> {
  List<Regional> findByAtivoTrue();
  List<Regional> findByRegionalRefIdAndAtivoTrue(Integer regionalRefId);
  Optional<Regional> findFirstByRegionalRefIdAndAtivoTrueOrderByIdDesc(Integer regionalRefId);
}
