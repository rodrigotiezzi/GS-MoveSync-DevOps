package br.com.fiap.movesync.repository;

import br.com.fiap.movesync.entity.AjusteTempo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AjusteTempoRepository extends JpaRepository<AjusteTempo, Long> {


    Page<AjusteTempo> findBySemaforoId(Long idSemaforo, Pageable pageable);

}
