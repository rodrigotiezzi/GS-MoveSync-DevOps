package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import br.com.fiap.movesync.entity.AjusteTempo;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.mapper.AjusteTempoMapper;
import br.com.fiap.movesync.repository.AjusteTempoRepository;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// Coração do MoveSync: ajusta o tempo de verde conforme o fluxo real lido pelos sensores
@Service
public class AjusteTempoService {

    public static final int TEMPO_VERDE_MINIMO = 15;   // segundos
    public static final int TEMPO_VERDE_MAXIMO = 90;   // segundos
    public static final int TEMPO_VERDE_BASE = 15;     // segundos com a via vazia
    public static final double SEGUNDOS_POR_VEICULO = 1.5;

    @Autowired
    private AjusteTempoRepository ajusteRepository;

    @Autowired
    private SemaforoRepository semaforoRepository;

    @Autowired
    private LeituraTrafegoRepository leituraRepository;

    @Transactional
    public AjusteTempoResponseDTO ajustar(Long idSemaforo) {
        Semaforo semaforo = semaforoRepository.findById(idSemaforo)
                .orElseThrow(() -> new EntityNotFoundException("Semáforo não encontrado."));

        if (semaforo.getStatus() != StatusSemaforo.ATIVO) {
            throw new IllegalStateException("Só é possível ajustar semáforos com status ATIVO.");
        }

        List<LeituraTrafego> ultimas = leituraRepository.findTop5BySemaforoIdOrderByDataHoraDesc(idSemaforo);
        if (ultimas.isEmpty()) {
            throw new IllegalStateException("O semáforo ainda não tem leituras de tráfego para basear o ajuste.");
        }

        double mediaVeiculos = ultimas.stream()
                .mapToInt(LeituraTrafego::getQtdVeiculos)
                .average()
                .orElse(0);

        int tempoAnterior = semaforo.getTempoVerde();
        int tempoNovo = calcularTempoVerde(mediaVeiculos);

        semaforo.setTempoVerde(tempoNovo);
        semaforoRepository.save(semaforo);

        AjusteTempo ajuste = new AjusteTempo();
        ajuste.setSemaforo(semaforo);
        ajuste.setDataHora(LocalDateTime.now());
        ajuste.setTempoVerdeAnterior(tempoAnterior);
        ajuste.setTempoVerdeNovo(tempoNovo);
        ajuste.setMediaVeiculos(Math.round(mediaVeiculos * 10) / 10.0);
        ajuste.setMotivo(definirMotivo(tempoAnterior, tempoNovo));

        return AjusteTempoMapper.toDTO(ajusteRepository.save(ajuste));
    }

    // Regra do ajuste: base + 1,5 s por veículo na média, sempre entre o mínimo e o máximo
    public static int calcularTempoVerde(double mediaVeiculos) {
        if (mediaVeiculos < 0) {
            throw new IllegalArgumentException("A média de veículos não pode ser negativa.");
        }
        long calculado = Math.round(TEMPO_VERDE_BASE + mediaVeiculos * SEGUNDOS_POR_VEICULO);
        return (int) Math.max(TEMPO_VERDE_MINIMO, Math.min(TEMPO_VERDE_MAXIMO, calculado));
    }

    static String definirMotivo(int tempoAnterior, int tempoNovo) {
        if (tempoNovo > tempoAnterior) {
            return "Fluxo alto: verde aumentado";
        }
        if (tempoNovo < tempoAnterior) {
            return "Fluxo baixo: verde reduzido";
        }
        return "Fluxo estável: sem alteração";
    }

    public Page<AjusteTempoResponseDTO> buscarTodos(Pageable pageable) {
        return ajusteRepository.findAll(pageable).map(AjusteTempoMapper::toDTO);
    }

    public AjusteTempoResponseDTO buscarPorId(Long id) {
        return ajusteRepository.findById(id)
                .map(AjusteTempoMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("Ajuste não encontrado."));
    }

    public Page<AjusteTempoResponseDTO> buscarPorSemaforo(Long idSemaforo, Pageable pageable) {
        return ajusteRepository.findBySemaforoId(idSemaforo, pageable).map(AjusteTempoMapper::toDTO);
    }
}
