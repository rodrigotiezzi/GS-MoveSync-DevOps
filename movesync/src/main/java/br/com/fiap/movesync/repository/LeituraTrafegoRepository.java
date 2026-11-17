package br.com.fiap.movesync.repository;

import br.com.fiap.movesync.entity.LeituraTrafego;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LeituraTrafegoRepository extends JpaRepository<LeituraTrafego, Long> {


    Page<LeituraTrafego> findBySemaforoId(Long idSemaforo, Pageable pageable);
    Page<LeituraTrafego> findByDispositivoId(Long idDispositivo, Pageable pageable);
    boolean existsByDispositivoId(Long idDispositivo);
    boolean existsBySemaforoId(Long idSemaforo);
    List<LeituraTrafego> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Últimas leituras do semáforo - base do cálculo do ajuste de tempo
    List<LeituraTrafego> findTop5BySemaforoIdOrderByDataHoraDesc(Long idSemaforo);

}
