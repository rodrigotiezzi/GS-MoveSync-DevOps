package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.SemaforoRequestDTO;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.entity.Cruzamento;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.mapper.SemaforoMapper;
import br.com.fiap.movesync.repository.CruzamentoRepository;
import br.com.fiap.movesync.repository.DispositivoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SemaforoService {

    public static final int LIMITE_DISPOSITIVOS = 4;

    @Autowired
    private SemaforoRepository semaforoRepository;

    @Autowired
    private CruzamentoRepository cruzamentoRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    public SemaforoResponseDTO salvar(SemaforoRequestDTO dto) {
        Cruzamento cruzamento = buscarCruzamento(dto.getIdCruzamento());

        String codigo = normalizarCodigo(dto.getCodigo());
        if (semaforoRepository.existsByCodigo(codigo)) {
            throw new IllegalStateException("Já existe um semáforo com o código " + codigo + ".");
        }
        dto.setCodigo(codigo);

        Semaforo semaforo = SemaforoMapper.toEntity(dto, cruzamento);
        return SemaforoMapper.toDTO(semaforoRepository.save(semaforo));
    }

    public Page<SemaforoResponseDTO> buscarTodos(Pageable pageable) {
        return semaforoRepository.findAll(pageable).map(SemaforoMapper::toDTO);
    }

    public SemaforoResponseDTO buscarPorId(Long id) {
        return SemaforoMapper.toDTO(buscarEntidadePorId(id));
    }

    public SemaforoResponseDTO buscarPorCodigo(String codigo) {
        return semaforoRepository.findByCodigo(normalizarCodigo(codigo))
                .map(SemaforoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Semáforo não encontrado."));
    }

    public Page<SemaforoResponseDTO> buscarPorStatus(StatusSemaforo status, Pageable pageable) {
        return semaforoRepository.findByStatus(status, pageable).map(SemaforoMapper::toDTO);
    }

    public List<SemaforoResponseDTO> buscarPorCruzamento(Long idCruzamento) {
        return semaforoRepository.findByCruzamentoId(idCruzamento).stream().map(SemaforoMapper::toDTO).toList();
    }

    public long contarPorStatus(StatusSemaforo status) {
        return semaforoRepository.countByStatus(status);
    }

    public SemaforoResponseDTO atualizar(Long id, SemaforoRequestDTO dto) {
        Semaforo semaforo = buscarEntidadePorId(id);
        Cruzamento cruzamento = buscarCruzamento(dto.getIdCruzamento());

        String codigo = normalizarCodigo(dto.getCodigo());
        semaforoRepository.findByCodigo(codigo)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalStateException("Já existe um semáforo com o código " + codigo + ".");
                });

        semaforo.setCodigo(codigo);
        semaforo.setTipo(dto.getTipo());
        if (dto.getStatus() != null) {
            semaforo.setStatus(dto.getStatus());
        }
        semaforo.setTempoVerde(dto.getTempoVerde());
        semaforo.setTempoAmarelo(dto.getTempoAmarelo());
        semaforo.setTempoVermelho(dto.getTempoVermelho());
        semaforo.setCruzamento(cruzamento);
        return SemaforoMapper.toDTO(semaforoRepository.save(semaforo));
    }

    public SemaforoResponseDTO alterarStatus(Long id, StatusSemaforo status) {
        Semaforo semaforo = buscarEntidadePorId(id);
        semaforo.setStatus(status);
        return SemaforoMapper.toDTO(semaforoRepository.save(semaforo));
    }

    @Transactional
    public SemaforoResponseDTO vincularDispositivo(Long idSemaforo, Long idDispositivo) {
        Semaforo semaforo = buscarEntidadePorId(idSemaforo);
        Dispositivo dispositivo = dispositivoRepository.findById(idDispositivo)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado."));

        boolean jaVinculado = semaforo.getDispositivos().stream()
                .anyMatch(d -> d.getId().equals(idDispositivo));
        if (jaVinculado) {
            throw new IllegalStateException("Esse dispositivo já está vinculado a esse semáforo.");
        }

        if (semaforo.getDispositivos().size() >= LIMITE_DISPOSITIVOS) {
            throw new IllegalStateException(
                    "Um semáforo pode ter no máximo " + LIMITE_DISPOSITIVOS + " dispositivos."
            );
        }

        semaforo.getDispositivos().add(dispositivo);
        return SemaforoMapper.toDTO(semaforoRepository.save(semaforo));
    }

    @Transactional
    public SemaforoResponseDTO desvincularDispositivo(Long idSemaforo, Long idDispositivo) {
        Semaforo semaforo = buscarEntidadePorId(idSemaforo);

        boolean removido = semaforo.getDispositivos().removeIf(d -> d.getId().equals(idDispositivo));
        if (!removido) {
            throw new IllegalStateException("Esse dispositivo não está vinculado a esse semáforo.");
        }

        return SemaforoMapper.toDTO(semaforoRepository.save(semaforo));
    }

    public void deletar(Long id) {
        buscarEntidadePorId(id);
        semaforoRepository.deleteById(id);
    }

    // usado pelos services de leitura e ajuste, que precisam da entidade completa
    Semaforo buscarEntidadePorId(Long id) {
        return semaforoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Semáforo não encontrado."));
    }

    private Cruzamento buscarCruzamento(Long idCruzamento) {
        return cruzamentoRepository.findById(idCruzamento)
                .orElseThrow(() -> new EntityNotFoundException("Cruzamento não encontrado."));
    }

    // Código sempre em maiúsculas e sem espaços nas pontas (ex.: " sem-001 " -> "SEM-001")
    private String normalizarCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase();
    }
}
