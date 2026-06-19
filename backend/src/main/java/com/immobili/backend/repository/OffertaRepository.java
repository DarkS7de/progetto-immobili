package com.immobili.backend.repository;

import com.immobili.backend.entity.Offerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OffertaRepository extends JpaRepository<Offerta, Long> {

    List<Offerta> findByAstaIdOrderByImportoDesc(Long astaId);

    Optional<Offerta> findFirstByAstaIdOrderByImportoDesc(Long astaId);
}