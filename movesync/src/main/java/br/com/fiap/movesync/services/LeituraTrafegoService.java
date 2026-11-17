package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.LeituraTrafegoRequestDTO;
import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.mapper.LeituraTrafegoMapper;
import br.com.fiap.movesync.repository.DispositivoRepository;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeituraTrafegoService {

    @Autowired
    private LeituraTrafegoRepository leituraRepository;

    @Autowired
    private SemaforoRepository semaforoRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Transactional
    public LeituraTrafegoResponseDTO registrar(LeituraTrafegoRequestDTO dto) {
        Semaforo semaforo = buscarSemaforo(dto.getIdSemaforo());
        Dispositivo dispositivo = buscarDispositivoValido(semaforo, dto.getIdDispositivo());

        LeituraTrafego leitura = LeituraTrafegoMapper.toEntity(dto, semaforo, dispositivo);
        return LeituraTrafegoMapper.toDTO(leituraRepository.save(leitura));
    }

    public Page<LeituraTrafegoResponseDTO> buscarTodas(Pageable pageable) {
        return leituraRepository.findAll(pageable).map(LeituraTrafegoMapper::toDTO);
    }

    public LeituraTrafegoResponseDTO buscarPorId(Long id) {
        return LeituraTrafegoMapper.toDTO(buscarEntidadePorId(id));
    }

    public Page<LeituraTrafegoResponseDTO> buscarPorSemaforo(Long idSemaforo, Pageable pageable) {
        return leituraRepository.findBySemaforoId(idSemaforo, pageable).map(LeituraTrafegoMapper::toDTO);
    }

    public Page<LeituraTrafegoResponseDTO> buscarPorDispositivo(Long idDispositivo, Pageable pageable) {
        return leituraRepository.findByDispositivoId(idDispositivo, pageable).map(LeituraTrafegoMapper::toDTO);
    }

    public List<LeituraTrafegoResponseDTO> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalStateException("A data inicial deve ser anterior à data final.");
        }
        return leituraRepository.findByDataHoraBetween(inicio, fim)
                .stream().map(LeituraTrafegoMapper::toDTO).toList();
    }

    @Transactional
    public LeituraTrafegoResponseDTO atualizar(Long id, LeituraTrafegoRequestDTO dto) {
        LeituraTrafego leitura = buscarEntidadePorId(id);
        Semaforo semaforo = buscarSemaforo(dto.getIdSemaforo());

        leitura.setSemaforo(semaforo);
        leitura.setDispositivo(buscarDispositivoValido(semaforo, dto.getIdDispositivo()));
        if (dto.getDataHora() != null) {
            leitura.setDataHora(dto.getDataHora());
        }
        leitura.setQtdVeiculos(dto.getQtdVeiculos());
        leitura.setQtdPedestres(dto.getQtdPedestres());
        leitura.setVelocidadeMedia(dto.getVelocidadeMedia());
        return LeituraTrafegoMapper.toDTO(leituraRepository.save(leitura));
    }

    public void deletar(Long id) {
        buscarEntidadePorId(id);
        leituraRepository.deleteById(id);
    }

    private LeituraTrafego buscarEntidadePorId(Long id) {
        return leituraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Leitura de tráfego não encontrada."));
    }

    private Semaforo buscarSemaforo(Long idSemaforo) {
        return semaforoRepository.findById(idSemaforo)
                .orElseThrow(() -> new EntityNotFoundException("Semáforo não encontrado."));
    }

    // A leitura só vale se veio de um dispositivo online e vinculado ao semáforo
    private Dispositivo buscarDispositivoValido(Semaforo semaforo, Long idDispositivo) {
        Dispositivo dispositivo = dispositivoRepository.findById(idDispositivo)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo não encontrado."));

        boolean vinculado = semaforo.getDispositivos().stream()
                .anyMatch(d -> d.getId().equals(dispositivo.getId()));
        if (!vinculado) {
            throw new IllegalStateException("O dispositivo " + dispositivo.getCodigo()
                    + " não está vinculado ao semáforo " + semaforo.getCodigo() + ".");
        }
        if (!Boolean.TRUE.equals(dispositivo.getOnline())) {
            throw new IllegalStateException("O dispositivo " + dispositivo.getCodigo() + " está offline.");
        }
        return dispositivo;
    }
}
