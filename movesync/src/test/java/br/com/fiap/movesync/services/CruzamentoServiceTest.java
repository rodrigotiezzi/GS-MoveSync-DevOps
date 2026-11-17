package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.CruzamentoRequestDTO;
import br.com.fiap.movesync.dto.response.CruzamentoResponseDTO;
import br.com.fiap.movesync.entity.Cruzamento;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.repository.CruzamentoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CruzamentoServiceTest {

    @Mock
    private CruzamentoRepository cruzamentoRepository;

    @Mock
    private SemaforoRepository semaforoRepository;

    @InjectMocks
    private CruzamentoService cruzamentoService;

    private CruzamentoRequestDTO requisicao() {
        CruzamentoRequestDTO dto = new CruzamentoRequestDTO();
        dto.setNome("Av. Paulista x R. Augusta");
        dto.setEndereco("Av. Paulista, 1000");
        dto.setBairro("Bela Vista");
        dto.setLatitude(-23.5614);
        dto.setLongitude(-46.6559);
        return dto;
    }

    @Test
    @DisplayName("Salvar cruzamento retorna os dados gravados com ID")
    void salvarRetornaCruzamentoComId() {
        when(cruzamentoRepository.save(any(Cruzamento.class))).thenAnswer(inv -> {
            Cruzamento c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        CruzamentoResponseDTO salvo = cruzamentoService.salvar(requisicao());

        assertEquals(10L, salvo.getId());
        assertEquals("Av. Paulista x R. Augusta", salvo.getNome());
        assertEquals("Bela Vista", salvo.getBairro());
    }

    @Test
    @DisplayName("Buscar cruzamento inexistente lança EntityNotFoundException")
    void buscarInexistenteLancaExcecao() {
        when(cruzamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cruzamentoService.buscarPorId(99L));
    }

    @Test
    @DisplayName("Atualizar cruzamento troca nome e endereço")
    void atualizarAlteraCampos() {
        Cruzamento existente = Fixtures.cruzamento(1L);
        when(cruzamentoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(cruzamentoRepository.save(existente)).thenReturn(existente);

        CruzamentoRequestDTO dto = requisicao();
        dto.setNome("Av. Rebouças x R. Henrique Schaumann");
        dto.setEndereco("Av. Rebouças, 2000");

        CruzamentoResponseDTO atualizado = cruzamentoService.atualizar(1L, dto);

        assertEquals("Av. Rebouças x R. Henrique Schaumann", atualizado.getNome());
        assertEquals("Av. Rebouças, 2000", atualizado.getEndereco());
    }

    @Test
    @DisplayName("Não exclui cruzamento que ainda tem semáforos")
    void deletarComSemaforosLancaExcecao() {
        when(cruzamentoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.cruzamento(1L)));
        when(semaforoRepository.findByCruzamentoId(1L)).thenReturn(List.of(Fixtures.semaforo(1L, StatusSemaforo.ATIVO)));

        assertThrows(IllegalStateException.class, () -> cruzamentoService.deletar(1L));
        verify(cruzamentoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Exclui cruzamento sem semáforos")
    void deletarSemSemaforos() {
        when(cruzamentoRepository.findById(1L)).thenReturn(Optional.of(Fixtures.cruzamento(1L)));
        when(semaforoRepository.findByCruzamentoId(1L)).thenReturn(List.of());

        cruzamentoService.deletar(1L);

        verify(cruzamentoRepository).deleteById(1L);
    }
}
