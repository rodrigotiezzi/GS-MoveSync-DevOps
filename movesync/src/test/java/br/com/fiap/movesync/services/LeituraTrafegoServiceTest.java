package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.LeituraTrafegoRequestDTO;
import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.repository.DispositivoRepository;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeituraTrafegoServiceTest {

    @Mock
    private LeituraTrafegoRepository leituraRepository;

    @Mock
    private SemaforoRepository semaforoRepository;

    @Mock
    private DispositivoRepository dispositivoRepository;

    @InjectMocks
    private LeituraTrafegoService leituraService;

    private LeituraTrafegoRequestDTO requisicao() {
        LeituraTrafegoRequestDTO dto = new LeituraTrafegoRequestDTO();
        dto.setIdSemaforo(1L);
        dto.setIdDispositivo(5L);
        dto.setQtdVeiculos(25);
        dto.setQtdPedestres(8);
        dto.setVelocidadeMedia(32.5);
        return dto;
    }

    @Test
    @DisplayName("Registrar leitura para semáforo inexistente lança EntityNotFoundException")
    void registrarSemaforoInexistente() {
        when(semaforoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> leituraService.registrar(requisicao()));
        verify(leituraRepository, never()).save(any());
    }

    @Test
    @DisplayName("Rejeita leitura de dispositivo que não está vinculado ao semáforo")
    void registrarDispositivoNaoVinculado() {
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.semaforo(1L, StatusSemaforo.ATIVO)));
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(Fixtures.dispositivo(5L, true)));

        assertThrows(IllegalStateException.class, () -> leituraService.registrar(requisicao()));
    }

    @Test
    @DisplayName("Rejeita leitura de dispositivo offline")
    void registrarDispositivoOffline() {
        Dispositivo offline = Fixtures.dispositivo(5L, false);
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        semaforo.getDispositivos().add(offline);
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(offline));

        assertThrows(IllegalStateException.class, () -> leituraService.registrar(requisicao()));
    }

    @Test
    @DisplayName("Registra a leitura com a data atual quando ela não é informada")
    void registrarLeituraValida() {
        Dispositivo camera = Fixtures.dispositivo(5L, true);
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        semaforo.getDispositivos().add(camera);
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(camera));
        when(leituraRepository.save(any(LeituraTrafego.class))).thenAnswer(inv -> {
            LeituraTrafego l = inv.getArgument(0);
            l.setId(100L);
            return l;
        });

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        LeituraTrafegoResponseDTO salva = leituraService.registrar(requisicao());

        assertEquals(100L, salva.getId());
        assertEquals("SEM-001", salva.getCodigoSemaforo());
        assertEquals("CAM-005", salva.getCodigoDispositivo());
        assertEquals(25, salva.getQtdVeiculos());
        assertTrue(salva.getDataHora().isAfter(antes));
    }

    @Test
    @DisplayName("Período com início depois do fim é inválido")
    void periodoInvalido() {
        LocalDateTime agora = LocalDateTime.now();
        assertThrows(IllegalStateException.class, () -> leituraService.buscarPorPeriodo(agora, agora.minusHours(1)));
    }
}
