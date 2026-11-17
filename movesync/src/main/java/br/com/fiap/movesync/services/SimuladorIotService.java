package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoSemaforo;
import br.com.fiap.movesync.mapper.LeituraTrafegoMapper;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Simula os sensores e câmeras: os dados de IoT do MoveSync são FICTÍCIOS (não há hardware real)
@Service
public class SimuladorIotService {

    public static final int MAX_LEITURAS_POR_SEMAFORO = 10;

    @Autowired
    private SemaforoRepository semaforoRepository;

    @Autowired
    private LeituraTrafegoRepository leituraRepository;

    private Random random = new Random();

    // Gera leituras fictícias para cada semáforo ATIVO que tenha ao menos um dispositivo online vinculado
    @Transactional
    public List<LeituraTrafegoResponseDTO> simular(int leiturasPorSemaforo) {
        if (leiturasPorSemaforo < 1 || leiturasPorSemaforo > MAX_LEITURAS_POR_SEMAFORO) {
            throw new IllegalStateException("Informe de 1 a " + MAX_LEITURAS_POR_SEMAFORO + " leituras por semáforo.");
        }

        List<Semaforo> ativos = semaforoRepository.findByStatus(StatusSemaforo.ATIVO, Pageable.unpaged()).getContent();
        List<LeituraTrafegoResponseDTO> geradas = new ArrayList<>();
        LocalDateTime agora = LocalDateTime.now();

        for (Semaforo semaforo : ativos) {
            List<Dispositivo> online = semaforo.getDispositivos().stream()
                    .filter(d -> Boolean.TRUE.equals(d.getOnline()))
                    .toList();
            if (online.isEmpty()) {
                continue; // sem sensor online não há leitura
            }

            for (int i = 0; i < leiturasPorSemaforo; i++) {
                LeituraTrafego leitura = new LeituraTrafego();
                leitura.setSemaforo(semaforo);
                leitura.setDispositivo(online.get(random.nextInt(online.size())));
                leitura.setDataHora(agora.minusMinutes((long) (leiturasPorSemaforo - i) * 5));
                preencherFluxoFicticio(leitura, semaforo.getTipo());
                geradas.add(LeituraTrafegoMapper.toDTO(leituraRepository.save(leitura)));
            }
        }

        if (geradas.isEmpty()) {
            throw new IllegalStateException("Nenhum semáforo ATIVO com dispositivo online para simular leituras.");
        }
        return geradas;
    }

    // Semáforo veicular tem mais carros; o de pedestre, mais pessoas
    private void preencherFluxoFicticio(LeituraTrafego leitura, TipoSemaforo tipo) {
        if (tipo == TipoSemaforo.PEDESTRE) {
            leitura.setQtdVeiculos(random.nextInt(15));
            leitura.setQtdPedestres(5 + random.nextInt(40));
        } else {
            leitura.setQtdVeiculos(5 + random.nextInt(50));
            leitura.setQtdPedestres(random.nextInt(15));
        }
        double velocidade = 10 + random.nextDouble() * 50;
        leitura.setVelocidadeMedia(Math.round(velocidade * 10) / 10.0);
    }

    // Permite fixar a semente nos testes
    void setRandom(Random random) {
        this.random = random;
    }
}
