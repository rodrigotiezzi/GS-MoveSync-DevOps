package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.DispositivoRequestDTO;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.enums.TipoDispositivo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.mapper.DispositivoMapper;
import br.com.fiap.movesync.repository.DispositivoRepository;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DispositivoService {

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private LeituraTrafegoRepository leituraRepository;

    public DispositivoResponseDTO salvar(DispositivoRequestDTO dto) {
        String codigo = normalizarCodigo(dto.getCodigo());
        if (dispositivoRepository.existsByCodigo(codigo)) {
            throw new IllegalStateException("Já existe um dispositivo com o código " + codigo + ".");
        }
        dto.setCodigo(codigo);
        return DispositivoMapper.toDTO(dispositivoRepository.save(DispositivoMapper.toEntity(dto)));
    }

    public Page<DispositivoResponseDTO> buscarTodos(Pageable pageable) {
        return dispositivoRepository.findAll(pageable).map(DispositivoMapper::toDTO);
    }

    public DispositivoResponseDTO buscarPorId(Long id) {
        return DispositivoMapper.toDTO(buscarEntidadePorId(id));
    }

    public Page<DispositivoResponseDTO> buscarPorTipo(TipoDispositivo tipo, Pageable pageable) {
        return dispositivoRepository.findByTipo(tipo, pageable).map(DispositivoMapper::toDTO);
    }

    // Dispositivos que monitoram o semáforo (usado na tela de semáforos)
    @Transactional(readOnly = true)
    public List<DispositivoResponseDTO> buscarPorSemaforo(Long idSemaforo) {
        return dispositivoRepository.findBySemaforosId(idSemaforo).stream().map(DispositivoMapper::toDTO).toList();
    }

    public DispositivoResponseDTO atualizar(Long id, DispositivoRequestDTO dto) {
        Dispositivo dispositivo = buscarEntidadePorId(id);

        String codigo = normalizarCodigo(dto.getCodigo());
        dispositivoRepository.findByCodigo(codigo)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalStateException("Já existe um dispositivo com o código " + codigo + ".");
                });

        dispositivo.setCodigo(codigo);
        dispositivo.setTipo(dto.getTipo());
        dispositivo.setModelo(dto.getModelo());
        if (dto.getOnline() != null) {
            dispositivo.setOnline(dto.getOnline());
        }
        return DispositivoMapper.toDTO(dispositivoRepository.save(dispositivo));
    }

    @Transactional
    public void deletar(Long id) {
        Dispositivo dispositivo = buscarEntidadePorId(id);

        if (!dispositivo.getSemaforos().isEmpty()) {
            throw new IllegalStateException("Desvincule o dispositivo dos semáforos antes de excluir.");
        }
        if (leituraRepository.existsByDispositivoId(id)) {
            throw new IllegalStateException("Não é possível excluir: o dispositivo já enviou leituras de tráfego.");
        }

        dispositivoRepository.deleteById(id);
    }

    private Dispositivo buscarEntidadePorId(Long id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado."));
    }

    private String normalizarCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase();
    }
}
