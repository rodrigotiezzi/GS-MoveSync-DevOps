package br.com.fiap.movesync.repository;

import br.com.fiap.movesync.entity.Cruzamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CruzamentoRepository extends JpaRepository<Cruzamento, Long> {


    Page<Cruzamento> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    Page<Cruzamento> findByBairroIgnoreCase(String bairro, Pageable pageable);

}
