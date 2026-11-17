package br.com.fiap.movesync.repository;

import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.enums.TipoDispositivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {


    Optional<Dispositivo> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    Page<Dispositivo> findByTipo(TipoDispositivo tipo, Pageable pageable);
    List<Dispositivo> findBySemaforosId(Long idSemaforo); //Listar os dispositivos do semáforo

}
