package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import br.com.fiap.movesync.entity.AjusteTempo;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.repository.AjusteTempoRepository;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AjusteTempoServiceTest {

    @Mock
    private AjusteTempoRepository ajusteRepository;

    @Mock
    private SemaforoRepository semaforoRepository;

    @Mock
    private LeituraTrafegoRepository leituraRepository;

    @InjectMocks
    private AjusteTempoService ajusteService;

    private LeituraTrafego leitura(Semaforo semaforo, int veiculos) {
        return new LeituraTrafego(null, semaforo, Fixtures.dispositivo(5L, true), LocalDateTime.now(), veiculos, 3, 30.0);
    }

    @ParameterizedTest(name = "média de {0} veículos => verde de {1}s")
    @CsvSource({
            "0, 15",     // via vazia: mínimo
            "10, 30",    // 15 + 10 * 1,5
            "20, 45",    // 15 + 20 * 1,5
            "50, 90",    // 15 + 75 = 90 (exatamente o máximo)
            "200, 90"    // fluxo muito alto: travado no máximo
    })
    @DisplayName("Cálculo do tempo de verde respeita base, fator e limites")
    void calculaTempoVerde(double media, int esperado) {
        assertEquals(esperado, AjusteTempoService.calcularTempoVerde(media));
    }

    @Test
    @DisplayName("Média negativa é inválida")
    void mediaNegativaLancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> AjusteTempoService.calcularTempoVerde(-1));
    }

    @Test
    @DisplayName("Ajustar semáforo inexistente lança EntityNotFoundException")
    void ajustarInexistente() {
        when(semaforoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> ajusteService.ajustar(1L));
    }

    @Test
    @DisplayName("Só ajusta semáforo ATIVO")
    void ajustarSemaforoEmManutencao() {
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.semaforo(1L, StatusSemaforo.MANUTENCAO)));

        assertThrows(IllegalStateException.class, () -> ajusteService.ajustar(1L));
        verify(ajusteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Sem leituras não há base para o ajuste")
    void ajustarSemLeituras() {
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.semaforo(1L, StatusSemaforo.ATIVO)));
        when(leituraRepository.findTop5BySemaforoIdOrderByDataHoraDesc(1L)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> ajusteService.ajustar(1L));
    }

    @Test
    @DisplayName("Fluxo alto aumenta o verde e grava o histórico do ajuste")
    void ajustarComFluxoAlto() {
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO); // verde inicial: 30s
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(leituraRepository.findTop5BySemaforoIdOrderByDataHoraDesc(1L))
                .thenReturn(List.of(leitura(semaforo, 30), leitura(semaforo, 40), leitura(semaforo, 20)));
        when(ajusteRepository.save(any(AjusteTempo.class))).thenAnswer(inv -> inv.getArgument(0));

        AjusteTempoResponseDTO ajuste = ajusteService.ajustar(1L);

        // média 30 veículos => 15 + 30 * 1,5 = 60s
        assertEquals(30, ajuste.getTempoVerdeAnterior());
        assertEquals(60, ajuste.getTempoVerdeNovo());
        assertEquals(30.0, ajuste.getMediaVeiculos());
        assertEquals(30, ajuste.getVariacao());
        assertTrue(ajuste.getMotivo().startsWith("Fluxo alto"));
        assertEquals(60, semaforo.getTempoVerde());
        verify(semaforoRepository).save(semaforo);
    }

    @Test
    @DisplayName("Fluxo baixo reduz o verde")
    void ajustarComFluxoBaixo() {
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(leituraRepository.findTop5BySemaforoIdOrderByDataHoraDesc(1L)).thenReturn(List.of(leitura(semaforo, 2)));
        when(ajusteRepository.save(any(AjusteTempo.class))).thenAnswer(inv -> inv.getArgument(0));

        AjusteTempoResponseDTO ajuste = ajusteService.ajustar(1L);

        assertEquals(18, ajuste.getTempoVerdeNovo());
        assertTrue(ajuste.getMotivo().startsWith("Fluxo baixo"));
    }

    @Test
    @DisplayName("Motivo indica quando não houve alteração")
    void motivoSemAlteracao() {
        assertTrue(AjusteTempoService.definirMotivo(30, 30).startsWith("Fluxo estável"));
    }
}
