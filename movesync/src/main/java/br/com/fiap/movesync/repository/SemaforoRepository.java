package br.com.fiap.movesync.repository;

import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemaforoRepository extends JpaRepository<Semaforo, Long> {


    Optional<Semaforo> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    Page<Semaforo> findByStatus(StatusSemaforo status, Pageable pageable);
    List<Semaforo> findByCruzamentoId(Long idCruzamento); //Listar todos os semáforos do cruzamento
    long countByStatus(StatusSemaforo status);

}
