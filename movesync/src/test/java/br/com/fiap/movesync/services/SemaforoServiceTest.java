package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.SemaforoRequestDTO;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoSemaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.repository.CruzamentoRepository;
import br.com.fiap.movesync.repository.DispositivoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemaforoServiceTest {

    @Mock
    private SemaforoRepository semaforoRepository;

    @Mock
    private CruzamentoRepository cruzamentoRepository;

    @Mock
    private DispositivoRepository dispositivoRepository;

    @InjectMocks
    private SemaforoService semaforoService;

    private SemaforoRequestDTO requisicao() {
        SemaforoRequestDTO dto = new SemaforoRequestDTO();
        dto.setCodigo(" sem-010 ");
        dto.setTipo(TipoSemaforo.VEICULAR);
        dto.setTempoVerde(30);
        dto.setTempoAmarelo(4);
        dto.setTempoVermelho(30);
        dto.setIdCruzamento(1L);
        return dto;
    }

    @Test
    @DisplayName("Salvar semáforo sem cruzamento existente lança EntityNotFoundException")
    void salvarSemCruzamentoLancaExcecao() {
        when(cruzamentoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> semaforoService.salvar(requisicao()));
        verify(semaforoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não permite dois semáforos com o mesmo código")
    void salvarCodigoDuplicadoLancaExcecao() {
        when(cruzamentoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.cruzamento(1L)));
        when(semaforoRepository.existsByCodigo("SEM-010")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> semaforoService.salvar(requisicao()));
    }

    @Test
    @DisplayName("Salvar normaliza o código e cria o semáforo como ATIVO")
    void salvarNormalizaCodigoEStatusPadrao() {
        when(cruzamentoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.cruzamento(1L)));
        when(semaforoRepository.existsByCodigo("SEM-010")).thenReturn(false);
        when(semaforoRepository.save(any(Semaforo.class))).thenAnswer(inv -> {
            Semaforo s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        SemaforoResponseDTO salvo = semaforoService.salvar(requisicao());

        assertEquals("SEM-010", salvo.getCodigo());
        assertEquals(StatusSemaforo.ATIVO, salvo.getStatus());
        assertEquals(64, salvo.getCicloTotal());
        assertEquals(1L, salvo.getIdCruzamento());
    }

    @Test
    @DisplayName("Vincular dispositivo adiciona o sensor ao semáforo")
    void vincularDispositivo() {
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(Fixtures.dispositivo(5L, true)));
        when(semaforoRepository.save(semaforo)).thenReturn(semaforo);

        SemaforoResponseDTO resposta = semaforoService.vincularDispositivo(1L, 5L);

        assertTrue(resposta.getIdsDispositivos().contains(5L));
    }

    @Test
    @DisplayName("Não vincula o mesmo dispositivo duas vezes")
    void vincularDispositivoDuplicadoLancaExcecao() {
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        semaforo.getDispositivos().add(Fixtures.dispositivo(5L, true));
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(Fixtures.dispositivo(5L, true)));

        assertThrows(IllegalStateException.class, () -> semaforoService.vincularDispositivo(1L, 5L));
    }

    @Test
    @DisplayName("Respeita o limite de dispositivos por semáforo")
    void vincularAlemDoLimiteLancaExcecao() {
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        for (long i = 1; i <= SemaforoService.LIMITE_DISPOSITIVOS; i++) {
            semaforo.getDispositivos().add(Fixtures.dispositivo(i, true));
        }
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(dispositivoRepository.findById(99L)).thenReturn(Optional.of(Fixtures.dispositivo(99L, true)));

        assertThrows(IllegalStateException.class, () -> semaforoService.vincularDispositivo(1L, 99L));
    }

    @Test
    @DisplayName("Alterar status coloca o semáforo em manutenção")
    void alterarStatus() {
        Semaforo semaforo = Fixtures.semaforo(1L, StatusSemaforo.ATIVO);
        when(semaforoRepository.findById(1L)).thenReturn(Optional.of(semaforo));
        when(semaforoRepository.save(semaforo)).thenReturn(semaforo);

        SemaforoResponseDTO resposta = semaforoService.alterarStatus(1L, StatusSemaforo.MANUTENCAO);

        assertEquals(StatusSemaforo.MANUTENCAO, resposta.getStatus());
    }
}
