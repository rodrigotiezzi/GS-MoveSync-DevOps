package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SimuladorIotServiceTest {

    @Mock
    private SemaforoRepository semaforoRepository;

    @Mock
    private LeituraTrafegoRepository leituraRepository;

    @InjectMocks
    private SimuladorIotService simulador;

    @BeforeEach
    void fixarSemente() {
        simulador.setRandom(new Random(42));
    }

    @Test
    @DisplayName("Gera leituras fictícias só para semáforos com dispositivo online")
    void simulaApenasComDispositivoOnline() {
        Semaforo comSensor = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        comSensor.getDispositivos().add(Fixtures.dispositivo(5L, true));
        Semaforo semSensorOnline = Fixtures.semaforo(2L, StatusSemaforo.ATIVO);
        semSensorOnline.getDispositivos().add(Fixtures.dispositivo(6L, false));

        when(semaforoRepository.findByStatus(eq(StatusSemaforo.ATIVO), any()))
                .thenReturn(new PageImpl<>(List.of(comSensor, semSensorOnline)));
        when(leituraRepository.save(any(LeituraTrafego.class))).thenAnswer(inv -> inv.getArgument(0));

        List<LeituraTrafegoResponseDTO> geradas = simulador.simular(3);

        assertEquals(3, geradas.size());
        assertTrue(geradas.stream().allMatch(l -> l.getIdSemaforo().equals(1L)));
        assertTrue(geradas.stream().allMatch(l -> l.getQtdVeiculos() >= 0 && l.getQtdPedestres() >= 0));
        verify(leituraRepository, times(3)).save(any(LeituraTrafego.class));
    }

    @Test
    @DisplayName("Quantidade fora do intervalo permitido é rejeitada")
    void quantidadeInvalida() {
        assertThrows(IllegalStateException.class, () -> simulador.simular(0));
        assertThrows(IllegalStateException.class, () -> simulador.simular(SimuladorIotService.MAX_LEITURAS_POR_SEMAFORO + 1));
    }

    @Test
    @DisplayName("Sem semáforo apto a simulação, avisa o usuário")
    void semSemaforosAptos() {
        when(semaforoRepository.findByStatus(eq(StatusSemaforo.ATIVO), any())).thenReturn(new PageImpl<>(List.of()));

        assertThrows(IllegalStateException.class, () -> simulador.simular(5));
    }
}
